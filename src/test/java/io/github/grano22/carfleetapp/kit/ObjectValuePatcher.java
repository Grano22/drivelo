package io.github.grano22.carfleetapp.kit;

import lombok.NonNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiFunction;

public class ObjectValuePatcher {
    public interface Strategy<T> {
        T patch(@NonNull T object, @NonNull String fieldName, Object value);
    }

    public static class BuilderPatchStrategy<T> implements Strategy<T> {
        private Class<T> objClass;
        private Class<?> builderClazz;

        public BuilderPatchStrategy(Class<T> objClass, Class<?> builderClazz) {
            this.objClass = objClass;
            this.builderClazz = builderClazz;
        }

        @Override
        public @NonNull T patch(@NonNull T object, @NonNull String fieldName, Object value) {
            try {
                var toBuilder = object.getClass().getMethod("toBuilder");
                Object builder = toBuilder.invoke(object);

                Method setter = Arrays.stream(builder.getClass().getMethods())
                        .filter(m -> m.getName().equals(fieldName) && m.getParameterCount() == 1)
                        .findFirst()
                        .orElse(null);

                if (setter == null) {
                    throw new NoSuchMethodException(fieldName);
                }

                setter.invoke(builder, value);

                Method build = builder.getClass().getMethod("build");

                return objClass.cast(build.invoke(builder));
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {

            }

            throw new UnsupportedOperationException("Creating object from entire builder is not supported");
        }
    }

    public static class WitherPatchStrategy<T> implements Strategy<T> {
        private Class<T> objClass;

        public WitherPatchStrategy(Class<T> objClass) {
            this.objClass = objClass;
        }

        @Override
        public @NonNull T patch(@NonNull T object, @NonNull String fieldName, Object value) {
            throw new UnsupportedOperationException("Setter strategy is not supported");
        }
    }

    public static class SetterPatchStrategy<T> implements Strategy<T> {
        private Class<T> objClass;

        public SetterPatchStrategy(Class<T> objClass) {
            this.objClass = objClass;
        }

        @Override
        public @NonNull T patch(@NonNull T object, @NonNull String fieldName, Object value) {
            throw new UnsupportedOperationException("Setter strategy is not supported");
        }
    }

    public static <T> Set<Strategy<T>> findAvailableStrategiesForField(Class<T> clazz, String fieldName) {
        Set<Strategy<T>> strategies = new HashSet<>();
        Set<Class<? extends Strategy>> strategiesByPriority = Set.of(BuilderPatchStrategy.class);
        Map<Class<? extends Strategy>, BiFunction<Class<T>, String, Optional<? extends Strategy<T>>>> checkers = Map.ofEntries(
            Map.entry(BuilderPatchStrategy.class, ObjectValuePatcher::createBuilderPatchStrategyIfPossible)
        );

        for (var strategy : strategiesByPriority) {
            var patcher = checkers.get(strategy).apply(clazz, fieldName);

            patcher.ifPresent(strategies::add);
        }

        return strategies;
    }

    public static <T> Optional<BuilderPatchStrategy<T>> createBuilderPatchStrategyIfPossible(Class<T> clazz, String field) {
        Optional<Class<?>> builderClass = getClassBuilder(clazz);

        if (builderClass.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new BuilderPatchStrategy<>(clazz, builderClass.get()));
    }

    public static <T> Optional<Class<?>> getClassBuilder(Class<T> clazz) {
        Class<?>[] nested = clazz.getDeclaredClasses();
        for (Class<?> nestedClass : nested) {
            if (nestedClass.getSimpleName().toLowerCase().contains("builder")) {
                return Optional.of(nestedClass);
            }
        }
        try {
            Method builderMethod = clazz.getMethod("builder");
            if (builderMethod.getReturnType().getSimpleName().toLowerCase().contains("builder")) {
                return Optional.of(builderMethod.getReturnType());
            }
        } catch (NoSuchMethodException ignored) {}

        return Optional.empty();
    }
}
