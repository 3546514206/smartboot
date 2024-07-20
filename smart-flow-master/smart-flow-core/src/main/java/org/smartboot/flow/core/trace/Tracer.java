package org.smartboot.flow.core.trace;

/**
 * 以类似栈的方式记录调用路径（支持追踪异步调用路径）
 *
 * @author qinluo
 * @date 2022-11-11 21:53:04
 * @since 1.0.0
 */
public class Tracer {

    private static final ThreadLocal<Node> TOP = new ThreadLocal<>();

    /**
     * Record invoking tree path string.
     */
    private String trace;

    /**
     * The root node.
     */
    private Node root;

    public String getTrace() {
        if (trace != null) {
            return trace;
        }

        if (root == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder("\n");
        root.dump("", sb);
        this.trace = sb.toString();
        return trace;
    }

    public void enter(String message) {
        Node node = TOP.get();
        if (node == null) {
            node = new Node(message);
            TOP.set(node);
            return;
        }

        Node child = new Node(message);
        node.addChild(child);
        child.setParent(node);
        TOP.set(child);
    }

    public void exit() {
        Node node = TOP.get();
        if (node == null) {
            return;
        }

        node.exit();
        Node parent = node.getParent();
        if (parent == null) {
            this.root = node;
            // Current invoking is finished.
            TOP.remove();
            return;
        }

        TOP.set(parent);
    }

    public void reset() {
        this.root = null;
        this.trace = null;
        TOP.remove();
    }

    public static void remove() {
        TOP.remove();
    }

    public static Node get() {
        return TOP.get();
    }

    public static void setNode(Node n) {
        TOP.set(n);
    }
}
