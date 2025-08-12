package mg.sprint.framework.utils;

public class ConvertUtil {

    public static Object convertValue(String value, Class<?> type) {
        if (value == null) return null;
        if (type == String.class) return value;
        else if (type == int.class || type == Integer.class) return Integer.parseInt(value);
        else if (type == double.class || type == Double.class) return Double.parseDouble(value);
        else if (type == boolean.class || type == Boolean.class) return Boolean.parseBoolean(value);
        else if (type == long.class || type == Long.class) return Long.parseLong(value);
        else if (type == float.class || type == Float.class) return Float.parseFloat(value);
        else if (type == short.class || type == Short.class) return Short.parseShort(value);
        else if (type == byte.class || type == Byte.class) return Byte.parseByte(value);
        else if (type == char.class || type == Character.class) return value.charAt(0);
        else throw new IllegalArgumentException("Type non supporté : " + type.getName());
    }
}