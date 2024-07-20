package org.smartboot.flow.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author qinluo
 * @date 2022-11-20 11:32:23
 * @since 1.0.0
 */
public final class AuxiliaryUtils {

    public static <T> T or(T t, T defaultValue) {
        return t != null ? t : defaultValue;
    }

    public static String or(String t, String defaultValue) {
        return t != null && t.trim().length() > 0 ? t : defaultValue;
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().length() == 0;
    }

    public static boolean isNotBlank(String value) {
        return !isBlank(value);
    }

    public static boolean isType(String typename) {
        return asClass(typename) != null;
    }

    public static Class<?> asClass(String typename) {
        if (isBlank(typename)) {
            return null;
        }

        try {
            return Class.forName(typename, false, Thread.currentThread().getContextClassLoader());
        } catch (Exception e) {
            return null;
        }
    }

    public static void appendTab(StringBuilder sb, int numbersOfTab) {
        for (int i = 0; i < numbersOfTab; i++) {
            sb.append("\t");
        }
    }

    public static boolean isAnonymous(String name) {
        return name != null && name.contains("anonymous");
    }

    @SafeVarargs
    public static <T> List<T> asList(T ...args) {
        List<T> values = new ArrayList<>(0);
        if (args == null) {
            return values;
        }

        for (T arg : args) {
            if (arg != null) {
                values.add(arg);
            }
        }

        return values;
    }

    public static List<String> splitByComma(String value) {
        if (value == null || value.trim().length() == 0) {
            return Collections.emptyList();
        }

        List<String> values = new ArrayList<>();
        String[] split = value.split(",+");
        for (String val : split) {
            if (val.trim().length() != 0) {
                values.add(val.trim());
            }
        }

        return values;
    }

    public static String transfer2CamelCase(String value) {
        if (value == null || value.length() == 0) {
            return value;
        }

        boolean upper = false;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);

            if (c == '-') {
                upper = true;
            } else if (upper && c >= 'a' && c <= 'z') {
                sb.append((char)(c - 32));
                upper = false;
            } else {
                sb.append(c);
                upper = false;
            }
        }

        return sb.toString();
    }
}
