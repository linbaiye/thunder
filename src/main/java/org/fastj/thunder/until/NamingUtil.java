package org.fastj.thunder.until;

public final class NamingUtil {
    private NamingUtil() {}

    public static String nameProperty(String propertyName) {
        return nameClass(propertyName);
    }

    public static String capitalFirstChar(String str) {
        return str.length() > 1 ?
            str.substring(0, 1).toLowerCase() + str.substring(1) : str;
    }

    public static String nameClass(String className) {
        if (className.length() > 1) {
            return className.substring(0, 1).toLowerCase() + className.substring(1);
        } else {
            return className.substring(0, 1);
        }
    }
}
