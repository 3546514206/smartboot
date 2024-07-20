package org.smartboot.flow.helper.useful;

import org.smartboot.flow.core.Key;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 中介者,建立领域模型的主从关系。提供面向业务可扩展、可维护的关系模型。
 *
 * @param <T> 领域模型主体对象
 * @author huqiang
 * @since  1.5.1
 */
@SuppressWarnings("unchecked")
public final class Mediator<T> {

    /**
     * 领域模型主体对象
     */
    private final T value;

    /**
     * 扩展
     */
    private final Map<Key<?>, Object> extensions = new ConcurrentHashMap<>();


    /**
     * 构建中介者模型
     *
     * @param value 领域模型主对象
     */
    public Mediator(T value) {
        this.value = value;
    }

    /**
     * 获取中介者内的主体模型
     *
     * @return value
     */
    public T value() {
        return value;
    }

    /**
     * 类Map类操作
     */

    public <S> S getExt(Key<S> key) {
        return getExt(key, null);
    }

    public <S> S getExt(Key<S> key, S defaultValue) {
        return (S)extensions.getOrDefault(key, defaultValue);
    }

    public <S> void put(Key<S> key, S value) {
        if (value == null) {
            return;
        }

        extensions.put(key, value);
    }

    public <S> void putIfAbsent(Key<S> key, S value) {
        if (value == null) {
            return;
        }

        extensions.putIfAbsent(key, value);
    }

    public <S> S remove(Key<S> key) {
        return (S)extensions.remove(key);
    }

}

