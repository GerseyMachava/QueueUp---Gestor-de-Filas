package queueup.api.shared.util;

import org.springframework.http.HttpStatus;

import queueup.api.shared.exceptions.BusinessException;

public final class EnumUtils {

    private EnumUtils() {
    }

    public static <E extends Enum<E>> E parse(Class<E> enumClass, String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(fieldName + " can not be null", HttpStatus.BAD_REQUEST);
        }

        String normalizedValue = value.trim();
        try {
            return Enum.valueOf(enumClass, normalizedValue.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException(
                    "Invalid " + fieldName + ": '" + value + "'. Allowed values: " + allowedValues(enumClass),
                    HttpStatus.BAD_REQUEST);
        }
    }

    private static <E extends Enum<E>> String allowedValues(Class<E> enumClass) {
        StringBuilder sb = new StringBuilder();
        for (E e : enumClass.getEnumConstants()) {
            if (sb.length() > 0)
                sb.append(", ");
            sb.append(e.name());
        }
        return sb.toString();
    }
}