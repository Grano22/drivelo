package io.github.grano22.carfleetapp.shared;

import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("UnnecessaryUnboxing")
public class BuilderPatcher {
    // TODO: Use Guava BiMap or DualHashBidiMap
    private static final Map<Class<?>, Class<?>> SUPPORTED_TYPES_MAP_TO_BOXED = Map.of(
        int.class, Integer.class,
        long.class, Long.class,
        double.class, Double.class,
        float.class, Float.class,
        char.class, Character.class,
        boolean.class, Boolean.class,
        byte.class, Byte.class,
        short.class, Short.class
    );
    private static final Map<Class<?>, Class<?>> SUPPORTED_TYPES_MAP_TO_PRIMITIVE = Map.of(
        Integer.class, int.class,
        Long.class, long.class,
        Double.class, double.class,
        Float.class, float.class,
        Character.class, char.class,
        Boolean.class, boolean.class,
        Byte.class, byte.class,
        Short.class, short.class
    );

    private static final Map<Class<?>, Function<Object, Object>> UNBOXERS = Map.of(
        Boolean.class, v -> v == null ? false : ((Boolean) v).booleanValue(),
        Byte.class, v -> v == null ? (byte)0 : ((Byte) v).byteValue(),
        Short.class, v -> v == null ? (short)0 : ((Short) v).shortValue(),
        Character.class, v -> v == null ? (char)0 : ((Character) v).charValue(),
        Integer.class, v -> v == null ? 0 : ((Integer) v).intValue(),
        Long.class, v -> v == null ? 0L : ((Long) v).longValue(),
        Float.class, v -> v == null ? 0f : ((Float) v).floatValue(),
        Double.class, v -> v == null ? 0d : ((Double) v).doubleValue()
    );
    private static final Map<Class<?>, Function<Object, Object>> BOXERS = Map.of(
        boolean.class, v -> v == null ? Boolean.FALSE : Boolean.valueOf((boolean) v),
        byte.class, v -> v == null ? Byte.valueOf((byte) 0) : Byte.valueOf((byte) v),
        short.class, v -> v == null ? Short.valueOf((short) 0) : Short.valueOf((short) v),
        char.class, v -> v == null ? Character.valueOf((char) 0) : Character.valueOf((char) v),
        int.class, v -> v == null ? Integer.valueOf(0) : Integer.valueOf((int) v),
        long.class, v -> v == null ? Long.valueOf(0) : Long.valueOf((long) v),
        float.class, v -> v == null ? Float.valueOf(0f) : Float.valueOf((float) v),
        double.class, v -> v == null ? Double.valueOf(0d) : Double.valueOf((double) v)
    );

    // FIXME: Note, reflection is slow in the production environment. On the fully prod env use MapStruct
    public static <D extends Record, B> B patch(D dto, B builder, Class<B> builderClass, Consumer<String> onUpdate) {
        for (RecordComponent component : dto.getClass().getRecordComponents()) {
            try {
                Method accessor = dto.getClass().getMethod(component.getName());
                Object value = accessor.invoke(dto);

                if (value == null) {
                    continue;
                }

                Optional<Method> setter = Arrays.stream(builder.getClass().getDeclaredMethods())
                    .filter(m ->
                        m.getName().equals(component.getName()) &&
                        m.getParameterCount() == 1 &&
                        Arrays.equals(m.getParameterTypes(), new Class<?>[]{component.getType()})
                    )
                    .findFirst()
                ;

                if (setter.isEmpty()) {
                    var reverseType = component.getType().isPrimitive() ?
                        SUPPORTED_TYPES_MAP_TO_BOXED.get(component.getType()) :
                        SUPPORTED_TYPES_MAP_TO_PRIMITIVE.get(component.getType())
                    ;

                    setter = Arrays.stream(builder.getClass().getDeclaredMethods())
                        .filter(m ->
                            m.getName().equals(component.getName()) &&
                            m.getParameterCount() == 1 &&
                            Arrays.equals(m.getParameterTypes(), new Class<?>[]{reverseType})
                        )
                        .findFirst()
                    ;

                    value = component.getType().isPrimitive() ?
                        toBoxed(value, component.getType()) :
                        toPrimitive(value, component.getType())
                    ;
                }

                String builderName = builder.getClass().getSimpleName();
                builder = builderClass.cast(
                     setter.orElseThrow(() ->
                          new NoSuchMethodException(MessageFormat.format(
                              "Cannot patch {0} on {1} due to lack of compatible setter",
                              component.getName(),
                              builderName
                          ))
                     )
                     .invoke(builder, value));
                onUpdate.accept(component.getName());
            } catch (Exception e) {
                throw new RuntimeException("Error updating builder field: " + component.getName(), e);
            }
        }

        return builder;
    }

    private static Object toBoxed(Object value, Class<?> type) {
        return BOXERS.get(type).apply(value);
    }

    private static Object toPrimitive(Object value, Class<?> type) {
        return UNBOXERS.get(type).apply(value);
    }
}
