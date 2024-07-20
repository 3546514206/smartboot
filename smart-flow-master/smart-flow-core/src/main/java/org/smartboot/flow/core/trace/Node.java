package org.smartboot.flow.core.trace;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author qinluo
 * @date 2022-11-11 21:53:16
 * @since 1.0.0
 */
public class Node {

    private static final int TAB_SIZE = 4;

    /**
     * Node message info.
     */
    private final String message;

    /**
     * Node escaped time in mills.
     */
    private long escaped;
    private final long timestamp;

    /**
     * Parent Node.
     */
    private Node parent;
    private boolean async;

    /**
     * Children nodes.
     */
    private final List<Node> children = new ArrayList<>(16);

    /**
     * Create this node object's thread.
     */
    private final long threadId = Thread.currentThread().getId();

    public String getMessage() {
        return message;
    }

    public long getEscaped() {
        return escaped;
    }

    public void setEscaped(long escaped) {
        this.escaped = escaped;
    }

    public List<Node> getChildren() {
        return children;
    }

    public long getThreadId() {
        return threadId;
    }

    private String wrap(String message) {
        if (parent == null) {
            return message;
        }
        return " " + message + " ";
    }

    public Node(String message) {
        this.message = message;
        this.escaped = System.currentTimeMillis();
        this.timestamp = escaped;
    }

    public void addChild(Node node) {
        if (node.threadId != threadId) {
            node.async = true;
        }
        children.add(node);
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public void exit() {
        this.escaped = System.currentTimeMillis() - escaped;
    }

    public void dump(String prefix, StringBuilder sb) {
        sb.append(prefix).append(wrap(message)).append(" escaped ").append(escaped).append("ms");
        sb.append("\n");
        if (children.isEmpty()) {
            return;
        }

        children.sort((Comparator.comparingLong(o -> o.timestamp)));
        boolean hasNextBrother = hasNextBrother();

        for (Node node : children) {
            String nextPrefix = generatePrefix(prefix, node.async, hasNextBrother);
            node.dump(nextPrefix, sb);
        }
    }

    private boolean hasNextBrother() {
        if (parent == null || parent.children.isEmpty()) {
            return false;
        }

        return parent.children.indexOf(this) < parent.children.size() - 1;
    }

    private String generatePrefix(String prefix, boolean async, boolean hasNext) {
        if (prefix == null || prefix.length() == 0) {
            return "|---";
        }

        if (prefix.length() == TAB_SIZE) {
            return (hasNext ? "|" : " ") + "   |" + (async ? "~~~" : "---");
        }

        int length = prefix.length() - 5;

        return prefix.substring(0, length) + (hasNext ? " |" : "  ") + "   |" + (async ? "~~~" : "---");
    }
}
