package io.github.grano22.carfleetapp.kit;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InMemoryCrudRepositoryTest {
    @Entity
    @Table(
        name = "complex_entities",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {"name_unique_via_column"}),
            @UniqueConstraint(columnNames = {"year", "status"})
        }
    )
    @Getter
    @Builder(toBuilder = true)
    @AllArgsConstructor
    private static final class ComplexEntity {
        @Id
        private Long id;

        @Column(name = "name", nullable = false, unique = true)
        private String name;

        @Column(name = "name_unique_via_column", nullable = false)
        private String nameUniqueViaColumn;

        @Column(name = "year", nullable = false)
        private int year;

        @Enumerated(EnumType.STRING)
        @Column(name = "status", nullable = false)
        private Status status;

        @ElementCollection(targetClass = Feature.class)
        @Enumerated(EnumType.STRING)
        @CollectionTable(name = "entity_features", joinColumns = @JoinColumn(name = "feature_id"))
        @Column(name = "features", nullable = false)
        private List<Feature> features;

        @Version
        private Long version;

        public ComplexEntity() {}

        enum Status {
            ACTIVE,
            INACTIVE,
            DELETED;
        }

        enum Feature {
            FREEDOM,
            SECURITY,
            ECOLOGY;
        }
    }

    private static final class ComplexEntityRepository extends InMemoryCrudRepository<ComplexEntity, Long> {
        ComplexEntityRepository(Class<ComplexEntity> entityType, Class<Long> idType) {
            super(entityType, idType);
        }
    }

    @Test
    public void complexEntityCanBeSavedAndRetrivedCorrectly() {
        ComplexEntityRepository repo = new ComplexEntityRepository(ComplexEntity.class, Long.class);
        ComplexEntity entity = ComplexEntity.builder()
            .id(1L)
            .name("test")
            .nameUniqueViaColumn("test")
            .year(2022)
            .status(ComplexEntity.Status.ACTIVE)
            .build()
        ;

        repo.save(entity);
        ComplexEntity retrieved = repo.findById(1L).orElse(null);

        assert retrieved != null;
        assert retrieved == entity;
    }

    @Test
    public void newEntityCannotBeSavedWhenHasDuplicatedFieldAndItWasAnnotatedViaUnique() {
        ComplexEntityRepository repo = new ComplexEntityRepository(ComplexEntity.class, Long.class);
        ComplexEntity entity = ComplexEntity.builder()
            .id(1L)
            .name("test")
            .nameUniqueViaColumn("test")
            .year(2024)
            .status(ComplexEntity.Status.ACTIVE)
            .build()
        ;

        repo.save(entity);

        ComplexEntity secondEntity = entity.toBuilder()
            .id(2L)
            .name("test")
            .year(2025)
            .status(ComplexEntity.Status.ACTIVE)
            .nameUniqueViaColumn("test2")
            .build()
         ;

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            repo.save(secondEntity);
        });

        assertEquals("Unique constraint violation on 'name' for value 'test'", exception.getMessage());
    }

    @Test
    public void newEntityCannotBeSavedWhenHasDuplicatedFieldAndItWasAnnotatedViaUniqueTableConstraint() {
        ComplexEntityRepository repo = new ComplexEntityRepository(ComplexEntity.class, Long.class);
        ComplexEntity entity = ComplexEntity.builder()
            .id(1L)
            .name("test")
            .nameUniqueViaColumn("test")
            .year(2024)
            .status(ComplexEntity.Status.ACTIVE)
            .build()
        ;

        repo.save(entity);

        ComplexEntity secondEntity = entity.toBuilder()
            .id(2L)
            .name("test2")
            .nameUniqueViaColumn("test")
            .year(2025)
            .status(ComplexEntity.Status.ACTIVE)
            .build()
         ;

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            repo.save(secondEntity);
        });

        assertEquals("Unique constraint violation on 'name_unique_via_column' for value 'test'", exception.getMessage());
    }

    @Test
    public void newEntityCannotBeSavedWhenHasDuplicatedCompoundFiledThatWasAnnotatedViaUniqueTableConstraint() {
        ComplexEntityRepository repo = new ComplexEntityRepository(ComplexEntity.class, Long.class);
        ComplexEntity entity = ComplexEntity.builder()
            .id(1L)
            .name("test")
            .nameUniqueViaColumn("test")
            .year(2025)
            .status(ComplexEntity.Status.ACTIVE)
            .build()
        ;

        repo.save(entity);

        ComplexEntity secondEntity = entity.toBuilder()
            .id(2L)
            .name("test2")
            .nameUniqueViaColumn("test2")
            .year(2025)
            .status(ComplexEntity.Status.ACTIVE)
            .build()
        ;

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            repo.save(secondEntity);
        });

        assertEquals("Composite unique constraint violation on (year, status) for key [2025, ACTIVE]", exception.getMessage());
    }


    @Test
    public void entityCannotBeSavedBecauseIdIsNull() {
        ComplexEntityRepository repo = new ComplexEntityRepository(ComplexEntity.class, Long.class);
        ComplexEntity entity = new ComplexEntity();

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            repo.save(entity);
        });

        assertEquals("ID field is null", exception.getMessage());
    }
}
