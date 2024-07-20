package org.smartboot.smart.flow.admin.g6;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author qinluo
 * @date 2023-02-16
 * @since 1.0.7
 */
public class G6Assembler {

    /**
     * Store any nodes.
     */
    private final Map<String, Node> nodeMap = new HashMap<>(32);
    private final Map<Double, List<Node>> horizonMap = new HashMap<>();
    private final SafeStack<EdgeBranch> stack = new SafeStack<>();
    private final List<Edge> edges = new ArrayList<>();
    private final List<Combo> combos = new ArrayList<>();
    private final SafeStack<String> comboStack = new SafeStack<>();
    private final Map<String, G6PipelineVisitor> namedPipelineMap = new HashMap<>();
    
    private double cx;
    private double cy;

    public double getCx() {
        return cx;
    }

    public void setCx(double cx) {
        this.cx = cx;
    }

    public double getCy() {
        return cy;
    }

    public void setCy(double cy) {
        this.cy = cy;
    }

    public Node getNode(String id) {
        return nodeMap.get(id);
    }

    public Collection<Node> getNodes() {
        return nodeMap.values();
    }

    public void set(Node n) {
        nodeMap.put(n.getId(), n);
        List<Node> nodes = horizonMap.computeIfAbsent(n.getY(), k -> new ArrayList<>());
        nodes.add(n);
    }

    public double getVertical(double y) {
        double x = 0;
        List<Node> nodes = horizonMap.get(y);
        if (nodes == null) {
            return x;
        }
        
        for (Node n : nodes) {
            x = Math.max(n.getX(), x);
        }
        
        return x + 1;
    }

    public void push(String name) {
        EdgeBranch peek = this.stack.peek();
        buildEdges(name, peek);

        stack.push(new EdgeBranch(name));
    }

    public void push(String target, String label) {
        EdgeBranch peek = stack.peek();
        buildEdges(target, peek);
        stack.push(new EdgeBranch(target, label));
    }

    private void buildEdges(String target, EdgeBranch peek) {
        if (peek == null) {
            return;
        }

        peek.getBranches().forEach(p -> {
            Edge edge = new Edge();
            edge.setSource(p);
            edge.setTarget(target);
            edge.setLabel(peek.getLabel());
            edges.add(edge);
        });
        this.stack.pop();
    }

    public void push(List<String> branches) {
        stack.push(new EdgeBranch(branches));
    }

    public EdgeBranch pop() {
        return stack.pop();
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public List<Combo> getCombos() {
        return combos;
    }

    public SafeStack<String> comboStack() {
        return comboStack;
    }

    public double incr() {
        double t = this.cy;
        this.cy += 1.2;
        return t;
    }

    public double incrVertical(int v) {
        this.cx += v;
        return this.cx;
    }

    public Edge findEdge(String source, String label) {
        return edges.stream().filter(p -> Objects.equals(source, p.getSource()) && (label == null || Objects.equals(label, p.getLabel()))).findFirst().orElse(null);
    }

    public Map<String, G6PipelineVisitor> getNamedPipelineMap() {
        return namedPipelineMap;
    }
}
