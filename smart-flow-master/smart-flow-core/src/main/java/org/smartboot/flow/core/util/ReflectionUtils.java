package org.smartboot.flow.core.util;

import org.smartboot.flow.core.exception.FlowException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * @author yamikaze
 * @date 2023/6/17 12:20
 * @since 1.1.0
 */
public class ReflectionUtils {

    public static Method lookUpMethod(Class<?> type, String methodName, MethodMatcher matcher) {
        List<Class<?>> classes = collectAllSuperClassAndInterfaces(type);
        for (Class<?> clazz : classes) {
            Method[] declaredMethods = clazz.getDeclaredMethods();
            for (Method m : declaredMethods) {
                if (Objects.equals(methodName, m.getName()) && (matcher == null || matcher.match(m))) {
                    return m;
                }
            }
        }

        return null;
    }

    public static Field lookupField(Class<?> type, String fieldName) {
        List<Class<?>> classes = collectAllSuperClassAndInterfaces(type);

        for (Class<?> clazz : classes) {
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field f : declaredFields) {
                if (Objects.equals(f.getName(), fieldName)) {
                    return f;
                }
            }
        }

        return null;
    }

    public static List<Class<?>> collectAllSuperClassAndInterfaces(Class<?> type) {
        List<Class<?>> classes = new ArrayList<>(8);
        if (type == null) {
            return classes;
        }

        Class<?> st = type;

        // Collect super class.
        while (st != null && st != Object.class) {
            classes.add(st);
            st = st.getSuperclass();
        }

        // Collect interfaces.
        Stack<Class<?>> stack = new Stack<>();
        stack.add(type);

        while (!stack.isEmpty()) {
            st = stack.pop();
            Class<?>[] interfaces = st.getInterfaces();
            classes.addAll(Arrays.asList(interfaces));
            stack.addAll(Arrays.asList(interfaces));
        }

        return classes.stream().distinct().collect(Collectors.toList());
    }

    public static Object invokeMethod(Object target, Method m, Object[] args) {
        if (target == null || m == null || args == null) {
            return null;
        }

        try {
            m.setAccessible(true);
            return m.invoke(target, args);
        } catch (Exception e) {
            throw new FlowException("invoke method failed", e);
        }
    }

    public static void setField(Object target, Field f, Object value) {
        if (target == null || f == null || value == null) {
            return;
        }

        try {
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new FlowException("invoke field failed", e);
        }
    }

    public static Class<?> getWrappedType(Class<?> type) {
        if (!type.isPrimitive()) {
            return type;
        }

        if (type == byte.class) {
            return Byte.class;
        } else if (type == short.class) {
            return Short.class;
        } else if (type == int.class) {
            return Integer.class;
        } else if (type == long.class) {
            return Long.class;
        } else if (type == float.class) {
            return Float.class;
        } else if (type == double.class) {
            return Double.class;
        } else if (type == char.class) {
            return Character.class;
        } else if (type == boolean.class) {
            return Boolean.class;
        }

        AssertUtil.shouldNotReachHere();
        return null;
    }
}
