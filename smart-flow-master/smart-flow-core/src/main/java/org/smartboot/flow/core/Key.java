package org.smartboot.flow.core;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author qinluo
 * @date 2022-11-11 21:40:24
 * @since 1.0.0
 */
public class Key<T> implements Serializable {

    private static final long serialVersionUID = -8986329829049453156L;

    private transient Object value;
    private String name;

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Key<?> key = (Key<?>) o;
        // Use value's equals
        return Objects.equals(value, key.value) && Objects.equals(name, key.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    public static <T> Key<T> of(String name) {
        Key<T> key = new Key<>();
        key.name = name;
        return key;
    }

    public static <T> Key<T> of(Object value) {
        Key<T> key = new Key<>();
        key.value = value;
        return key;
    }

    public static <T> Key<T> of(String name, Object value) {
        Key<T> key = new Key<>();
        key.value = value;
        key.name = name;
        return key;
    }
}


