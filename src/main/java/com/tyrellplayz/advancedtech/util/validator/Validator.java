package com.tyrellplayz.advancedtech.util.validator;

import java.io.InvalidObjectException;
import java.lang.reflect.Field;

public class Validator {

    public static <T> boolean isValidObject(T t) throws IllegalAccessException, InvalidObjectException {
        if (t == null) {
            return false;
        } else {
            Field[] fields = t.getClass().getDeclaredFields();
            Field[] var2 = fields;
            int var3 = fields.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Field field = var2[var4];
                if (field.getDeclaredAnnotation(Optional.class) == null) {
                    field.setAccessible(true);
                    if (field.get(t) == null) {
                        throw new InvalidObjectException("Missing required property: " + field.getName());
                    }

                    if (!field.getType().isPrimitive() && field.getType() != String.class && !field.getType().isEnum() && field.getDeclaredAnnotation(Ignored.class) == null) {
                        return isValidObject(field.get(t));
                    }
                }
            }

            return true;
        }
    }

}
