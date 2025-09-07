package io.github.grano22.carfleetapp.shared;

import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.function.Consumer;

public class BuilderPatcher {
    public static <D extends Record, B> B patch(D dto, B builder, Class<B> builderClass, Consumer<String> onUpdate) {
        for (RecordComponent component : dto.getClass().getRecordComponents()) {
            try {
                Method accessor = dto.getClass().getMethod(component.getName());
                Object value = accessor.invoke(dto);

                if (value != null) {
                    Method setter = builder.getClass().getMethod(component.getName(), component.getType());
                    builder = builderClass.cast(setter.invoke(builder, value));
                    onUpdate.accept(component.getName());
                }
            } catch (Exception e) {
                throw new RuntimeException("Error updating builder field: " + component.getName(), e);
            }
        }

        return builder;
    }
}
