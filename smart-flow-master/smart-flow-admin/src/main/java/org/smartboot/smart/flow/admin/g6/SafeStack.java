package org.smartboot.smart.flow.admin.g6;

import java.util.Stack;

/**
 * @author qinluo
 * @date 2023/2/10 23:23
 * @since 1.0.0
 */
public class SafeStack<T> extends Stack<T> {

    private static final long serialVersionUID = -4107181661207212787L;

    @Override
    public synchronized T peek() {
        if (this.isEmpty()) {
            return null;
        }

        return super.peek();
    }

    @Override
    public synchronized T pop() {
        if (this.isEmpty()) {
            return null;
        }

        return super.pop();
    }
}
