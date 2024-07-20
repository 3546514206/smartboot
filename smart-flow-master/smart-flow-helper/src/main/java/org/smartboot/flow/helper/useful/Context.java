package org.smartboot.flow.helper.useful;

import org.smartboot.flow.core.Key;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 业务流程编排上下文对象。
 * <p>
 * 此处设计为泛型是批量的中介者模型Mediator。
 * </p>
 *
 * @author huqiang
 * @since 1.5.1
 */
@SuppressWarnings("unchecked")
public class Context<T> {

    private List<Mediator<T>> mediators;
    private final Map<Key<?>, Object> extensions = new ConcurrentHashMap<>();

    public void setMediators(List<T> values) {
        List<Mediator<T>> domains = new ArrayList<>();
        values.forEach(p -> domains.add(new Mediator<>(p)));
        this.mediators = domains;
    }

    public Context(List<T> values) {
        this.setMediators(values);
    }

    public Context() {
    }

    /**
     * 获取存储于上下文中的中介者对象
     *
     * @return 中介者对象列表
     */
    public List<Mediator<T>> getMediators() {
        return mediators;
    }

    /**
     * 获取存储于上下文中的中介者主对象
     *
     * @return 中介者主对象列表
     */
    public List<T> values() {
        List<T> list = new ArrayList<>();
        if (mediators == null) {
            return list;
        }

        for (Mediator<T> mediator: mediators) {
            if (mediator.value() != null) {
                list.add(mediator.value());
            }
        }

        return list;
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