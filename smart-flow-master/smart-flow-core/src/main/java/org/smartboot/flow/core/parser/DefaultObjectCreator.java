package org.smartboot.flow.core.parser;

import org.smartboot.flow.core.exception.FlowException;
import org.smartboot.flow.core.util.AssertUtil;
import org.smartboot.flow.core.util.AuxiliaryUtils;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qinluo
 * @date 2022/11/19 14:29
 * @since 1.0.0
 */
@SuppressWarnings("unchecked")
public class DefaultObjectCreator implements ObjectCreator {

    private static final ObjectCreator INSTANCE = new DefaultObjectCreator();

    private final Map<Class<?>, Object> cachedObjects = new ConcurrentHashMap<>();
    private final Map<Class<?>, Constructor<?>> cachedConstructors = new ConcurrentHashMap<>();

    public static ObjectCreator getInstance() {
        return INSTANCE;
    }

    @Override
    public <T> T create(String typename, Class<T> expectType, boolean useCache) {
        AssertUtil.notBlank(typename, "type must not be blank!");
        Class<T> type = (Class<T>) AuxiliaryUtils.asClass(typename);
        AssertUtil.notNull(type, "typename " + typename + " is not class");

        T created = null;
        if (useCache) {
            created = (T) cachedObjects.get(type);
        }

        if (created != null) {
            return created;
        }

        try {

            Constructor<T> noargConstructor = compute(type);
            AssertUtil.notNull(noargConstructor, "cannot find no-arg constructor for type " + typename);

            created = noargConstructor.newInstance();
            cachedObjects.put(type, created);
            return created;
        } catch (Exception e) {
            throw new FlowException(type.getName(), e);
        }
    }

    private <T> Constructor<T> compute(Class<T> type) {
        Constructor<T> candidate = (Constructor<T>)cachedConstructors.get(type);
        if (candidate != null) {
            return candidate;
        }

        Constructor<?>[] constructors = type.getDeclaredConstructors();
        for (Constructor<?> con : constructors) {
            if (con.getParameterCount() == 0) {
                candidate = (Constructor<T>) con;
                break;
            }
        }

        if (candidate != null) {
            cachedConstructors.put(type, candidate);
            candidate.setAccessible(true);
        }

        return candidate;
    }
}
