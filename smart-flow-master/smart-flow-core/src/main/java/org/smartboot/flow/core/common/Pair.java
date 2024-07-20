package org.smartboot.flow.core.common;

import java.io.Serializable;

/**
 * @author qinluo
 * @date 2022/12/7 21:14
 * @since 1.0.0
 */
public class Pair<T, S> implements Serializable {

    private static final long serialVersionUID = -6226657512890578902L;

    private T left;
    private S right;

    public static <T, S> Pair<T, S> of(T left, S right) {
        Pair<T, S> pair = new Pair<>();
        pair.left = left;
        pair.right = right;
        return pair;
    }

    public T getLeft() {
        return left;
    }

    public S getRight() {
        return right;
    }
}
