package io.github.grano22.carfleetapp.kit;

import jakarta.persistence.*;
import lombok.NonNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.repository.CrudRepository;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.beans.BeanUtils.getPropertyDescriptors;

public abstract class InMemoryCrudRepository<T, ID> implements CrudRepository<T, ID> {
    private final Map<ID, T> store = new ConcurrentHashMap<>();
    private final Class<T> entityType;
    private final Class<ID> idType;

    private final Map<String, PropertyAccessor> accessorsByColumnName;

    private final Map<String, Map<Object, T>> indexes = new ConcurrentHashMap<>();
    private final Map<List<String>, Map<CompositeKey, T>> compositeIndexes = new ConcurrentHashMap<>();

    private final Set<String> uniqueColumnNames;
    private final List<List<String>> compositeUniqueColumnSets;

    protected InMemoryCrudRepository(Class<T> entityType, Class<ID> idType) {
        this.entityType = entityType;
        this.idType = idType;

        this.accessorsByColumnName = buildFieldsByColumnName(entityType);

        UniqueConstraintsScanResult uniques = scanUniqueConstraints(entityType);
        this.uniqueColumnNames = uniques.singleColumns();
        this.compositeUniqueColumnSets = uniques.compositeColumns();

        initializeIndexes();
    }

    @Override
    public <S extends T> @NonNull S save(@NonNull S entity) {
        enforceUniqueConstraints(entity);
        final ID id = this.getId(entity);
        this.store.put(id, entity);
        updateIndexesFor(entity);
        updateCompositeIndexesFor(entity);

        return entity;

    }

    @Override
    public <S extends T> @NonNull Iterable<S> saveAll(@NonNull Iterable<S> entities) {
        return StreamSupport.stream(entities.spliterator(), false)
                .peek(this::save)
                .toList();
    }

    @Override
    public @NonNull Optional<T> findById(@NonNull ID i) {
        return Optional.ofNullable(this.store.get(i));
    }

    public @NonNull Optional<T> findBy(Map<String, Object> criteria) {
        return store.values()
            .stream()
            .filter(entity -> {
                for (Map.Entry<String, Object> entry : criteria.entrySet()) {
                    String fieldName = entry.getKey();
                    Object expectedValue = entry.getValue();

                    PropertyAccessor accessor = accessorsByColumnName.get(fieldName);

                    if (accessor == null) return false;
                    Object actualValue = accessor.get(entity);

                    if (!Objects.equals(expectedValue, actualValue)) {
                        return false;
                    }
                }

                return true;
            })
            .findFirst()
        ;
    }

    @Override
    public boolean existsById(@NonNull ID i) {
        return this.store.containsKey(i);
    }

    @Override
    public @NonNull Iterable<T> findAll() {
        return List.copyOf(this.store.values());
    }

    @Override
    public @NonNull Iterable<T> findAllById(@NonNull Iterable<ID> is) {
        return StreamSupport.stream(is.spliterator(), false)
                .map(this.store::get)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public long count() {
        return this.store.size();
    }

    @Override
    public void deleteById(@NonNull ID i) {
        T removed = this.store.remove(i);

        if (removed != null) {
            removeFromIndexes(removed);
            removeFromCompositeIndexes(removed);
        }
    }

    @Override
    public void delete(@NonNull T entity) {
        ID id = this.getId(entity);
        T removed = this.store.remove(id);

        if (removed != null) {
            removeFromIndexes(removed);
            removeFromCompositeIndexes(removed);
        }

    }

    @Override
    public void deleteAllById(@NonNull Iterable<? extends ID> is) {
        StreamSupport.stream(is.spliterator(), false)
                .forEach(id -> {
                    T removed = this.store.remove(id);
                    if (removed != null) {
                        removeFromIndexes(removed);
                        removeFromCompositeIndexes(removed);
                    }
                });
    }

    @Override
    public void deleteAll(@NonNull Iterable<? extends T> entities) {
        StreamSupport.stream(entities.spliterator(), false)
                .forEach(this::delete);
    }

    @Override
    public void deleteAll() {
        this.indexes.values().forEach(Map::clear);
        this.compositeIndexes.values().forEach(Map::clear);
        this.store.clear();
    }

    // --- Unique constraints scanning and enforcement ---

    private record UniqueConstraintsScanResult(Set<String> singleColumns, List<List<String>> compositeColumns) {
    }

    private UniqueConstraintsScanResult scanUniqueConstraints(Class<T> type) {
        Set<String> single = new HashSet<>();

        // @Column(unique = true)
        Arrays.stream(type.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Column.class) && f.getAnnotation(Column.class).unique())
                .map(this::getColumnName)
                .forEach(single::add);

        // @Table(uniqueConstraints = ...)
        Table table = type.getAnnotation(Table.class);
        List<List<String>> composite = new ArrayList<>();
        if (table != null) {
            for (UniqueConstraint uc : table.uniqueConstraints()) {
                String[] cols = uc.columnNames();
                if (cols == null || cols.length == 0) continue;

                List<String> orderedColumns = Arrays.stream(cols).map(String::trim).filter(s -> !s.isEmpty()).toList();
                if (orderedColumns.isEmpty()) continue;

                if (orderedColumns.size() == 1) {
                    single.add(orderedColumns.getFirst());

                    continue;
                }

                boolean resolvable = orderedColumns.stream().allMatch(accessorsByColumnName::containsKey);

                if (resolvable) {
                    composite.add(List.copyOf(orderedColumns));
                }
            }
        }

        return new UniqueConstraintsScanResult(Collections.unmodifiableSet(single), List.copyOf(composite));
    }

    private Map<String, PropertyAccessor> buildFieldsByColumnName(Class<T> type) {
        Map<String, PropertyAccessor> map = new HashMap<>();

        Map<String, PropertyDescriptor> pds = Arrays.stream(getPropertyDescriptors(type))
                .filter(pd -> pd.getReadMethod() != null)
                .collect(Collectors.toMap(PropertyDescriptor::getName, pd -> pd));

        for (Field field : type.getDeclaredFields()) {
            String propName = field.getName();
            PropertyDescriptor pd = pds.get(propName);
            if (pd == null || pd.getReadMethod() == null) {
                continue;
            }

            String columnName = getColumnName(field);
            PropertyAccessor accessor = new PropertyAccessor(columnName, propName, pd.getReadMethod());

            map.put(columnName, accessor);

            if (!columnName.equals(propName)) {
                map.putIfAbsent(propName, accessor);
            }
        }

        return Collections.unmodifiableMap(map);
    }

    private String getColumnName(Field f) {
        Column c = f.getAnnotation(Column.class);
        if (c != null && c.name() != null && !c.name().isBlank()) {
            return c.name();
        }
        return f.getName();
    }

    private void enforceUniqueConstraints(T entity) {
        ID currentId = getId(entity);

        // Single-column uniques
        for (String column : uniqueColumnNames) {
            PropertyAccessor accessor = accessorsByColumnName.get(column);
            if (accessor == null) continue;
            Object value = accessor.get(entity);
            if (value == null) continue;

            Map<Object, T> index = indexes.get(column);
            if (index == null) continue;
            T existing = index.get(value);
            if (existing != null) {
                ID existingId = getId(existing);
                if (!existingId.equals(currentId)) {
                    throw new DataIntegrityViolationException(
                         "Unique constraint violation on '" + column + "' for value '" + value + "'"
                    );
                }
            }
        }

        // Composite uniques
        for (List<String> columns : compositeUniqueColumnSets) {
            CompositeKey key = buildCompositeKey(entity, columns);

            if (key == null) continue;

            Map<CompositeKey, T> index = compositeIndexes.get(columns);
            if (index == null) continue;
            T existing = index.get(key);
            if (existing != null) {
                ID existingId = getId(existing);
                if (!existingId.equals(currentId)) {
                    String cols = String.join(", ", columns);
                    throw new DataIntegrityViolationException(
                         "Composite unique constraint violation on (" + cols + ") for key " + key
                    );
                }
            }
        }
    }


    // --- Indexing (single-field) ---
    private void initializeIndexes() {
        accessorsByColumnName.keySet().forEach(col -> indexes.put(col, new ConcurrentHashMap<>()));
        uniqueColumnNames.stream()
                .filter(col -> !indexes.containsKey(col))
                .forEach(col -> indexes.put(col, new ConcurrentHashMap<>()));

        compositeUniqueColumnSets.forEach(colsList ->
             this.compositeIndexes.put(colsList, new ConcurrentHashMap<>())
        );
    }

    private void updateIndexesFor(T entity) {
        for (Map.Entry<String, Map<Object, T>> e : indexes.entrySet()) {
            String columnName = e.getKey();
            PropertyAccessor accessor = accessorsByColumnName.get(columnName);
            if (accessor == null) continue;

            Object value = accessor.get(entity);
            Map<Object, T> index = e.getValue();

            if (value != null) {
                index.put(value, entity);
            }
        }
    }

    private void removeFromIndexes(@NonNull T entity) {
        for (Map.Entry<String, Map<Object, T>> e : indexes.entrySet()) {
            String columnName = e.getKey();
            PropertyAccessor accessor = accessorsByColumnName.get(columnName);
            if (accessor == null) continue;

            Object value = accessor.get(entity);
            Map<Object, T> index = e.getValue();
            if (value == null) continue;

            index.computeIfPresent(value, (k, v) -> v == entity ? null : v);
        }

    }

    // --- Indexing (composite) ---

    private void updateCompositeIndexesFor(T entity) {
        for (List<String> columns : compositeUniqueColumnSets) {
            CompositeKey key = buildCompositeKey(entity, columns);
            if (key == null) continue;

            Map<CompositeKey, T> index = compositeIndexes.get(columns);
            if (index != null) {
                index.put(key, entity);
            }
        }
    }

    private void removeFromCompositeIndexes(T entity) {
        for (List<String> columns : compositeUniqueColumnSets) {
            CompositeKey key = buildCompositeKey(entity, columns);
            if (key == null) continue;
            Map<CompositeKey, T> index = compositeIndexes.get(columns);
            if (index != null) {
                index.computeIfPresent(key, (k, v) -> v == entity ? null : v);
            }
        }
    }

    private CompositeKey buildCompositeKey(T entity, List<String> columns) {
        List<Object> parts = new ArrayList<>(columns.size());
        for (String col : columns) {
            PropertyAccessor accessor = accessorsByColumnName.get(col);
            if (accessor == null) return null;
            Object value = accessor.get(entity);
            if (value == null) return null;
            parts.add(value);
        }

        return CompositeKey.of(parts);
    }

    private record CompositeKey(List<Object> parts) {
        private CompositeKey(List<Object> parts) {
            this.parts = List.copyOf(parts);
        }

        static CompositeKey of(List<Object> parts) {
            return new CompositeKey(parts);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CompositeKey(List<Object> parts1))) return false;
            return Objects.equals(parts, parts1);
        }

        @Override
            public @NonNull String toString() {
                return parts.toString();
            }
    }


    private record PropertyAccessor(
        String columnName,
        String propertyName,
        Method getter
    ) {
        Object get(Object bean) {
            try {
                return getter.invoke(bean);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Failed to read property '" + propertyName + "' via getter", e);
            }
        }
    }

    // --- ID utilities ---

    private ID getId(@NonNull T entity) {
        for (Field field : this.entityType.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                PropertyAccessor accessor = accessorsByColumnName.get(field.getName());
                Object rawId = accessor.get(entity);

                if (rawId == null) {
                    if (field.isAnnotationPresent(GeneratedValue.class) && field.getAnnotation(GeneratedValue.class).strategy() == GenerationType.AUTO) {
                        return generateDefaultValue();
                    }

                    throw new IllegalStateException("ID field is null");
                }

                if (idType.isInstance(rawId)) {
                    return idType.cast(rawId);
                }

                throw new IllegalStateException("Id has unexpected type: " + rawId.getClass().getName());
            }
        }

        throw new IllegalStateException("No field annotated with @Id found in " + this.entityType.getName());
    }

    private ID generateDefaultValue() {
        if (idType.isPrimitive()) {
            return idType.cast(0);
        }

        if (idType.equals(UUID.class)) {
            return idType.cast(UUID.randomUUID());
        }

        throw new IllegalStateException("Unsupported ID type: " + idType.getName());
    }
}
