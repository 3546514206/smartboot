package org.smartboot.smart.flow.admin.g6;

import org.smartboot.flow.core.attribute.Attributes;
import org.smartboot.flow.core.common.ComponentType;
import org.smartboot.flow.core.common.Pair;
import org.smartboot.flow.core.exception.FlowException;
import org.smartboot.flow.core.util.AssertUtil;
import org.smartboot.flow.core.util.AuxiliaryUtils;
import org.smartboot.flow.helper.view.ScriptCollector;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Stack;

/**
 * 序列化为g6结果为xml形式
 *
 * @author qinluo
 * @date 2023/2/18 0:49
 * @since 1.0.9
 */
public class G6ResultSerializer {

    private static final String START = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<engines xmlns=\"http://org.smartboot/smart-flow\"\n" +
            "       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "       xsi:schemaLocation=\"http://org.smartboot/smart-flow\n" +
            "                           http://org.smartboot/smart-flow-1.0.1.xsd\">";

    private static final String END = "</engines>";
    private static final String START_NODE = "#start";
    private static final String END_NODE = "#end";

    public String serialize(G6Result result) {
        removeFixedNodesAndEdges(result);

        AnalyzerContext ctx = new AnalyzerContext(result);

        // Check valid parameter
        checkArguments(ctx);

        Node start = findStartNode(result, ctx);
        Node end = findEndNode(result, ctx);

        AssertUtil.notNull(start, "start node must not be null!");
        AssertUtil.notNull(end, "end node must not be null!");
        AssertUtil.isFalse(start == end, "start == end");

        StringBuilder content = new StringBuilder();
        content.append(START).append("\n");
        // <engine name="" pipeline=""/>
        content.append("\t<engine name=\"").append(result.getName()).append("\" pipeline=\"").append(result.getProcess()).append("\"/>\n");
        content.append("\t<pipeline name=\"").append(result.getProcess()).append("\">\n");

        // start collect script. [BASIC, IF, CHOOSE]
        ScriptCollector.start();

        processNode(ctx, content, start, 2);

        content.append("\t</pipeline>\n");

        Map<String, Pair<String, String>> scripts = ScriptCollector.end();
        if (scripts != null && scripts.size() > 0) {
            scripts.forEach((k, v) -> content.append("\n\t<script name=\"").append(k)
                    .append("\" type=\"").append(v.getRight())
                    .append("\">").append("<![CDATA[").append(v.getLeft()).append("]]>").append("</script>\n"));
        }

        content.append("\n").append(END).append("\n");
        return content.toString();
    }

    private void processNode(AnalyzerContext ctx, StringBuilder content, Node current, int numOfTab) {
        if (current == null) {
            return;
        }

        Node peek = ctx.endedNodes.peek();
        if (current == peek) {
            return;
        }

        int nested = tryStartProcessCombo(ctx, content, current.getComboId(), numOfTab);
        if (nested > 0) {
            numOfTab += nested;
        }

        ComponentInfo componentInfo = current.getComponentInfo();
        ComponentType type = ComponentType.valueOf(componentInfo.getType());

        if (type == ComponentType.BASIC) {
            AuxiliaryUtils.appendTab(content, numOfTab);
            content.append("<component ");
            if (AuxiliaryUtils.isNotBlank(componentInfo.getName())) {
                content.append("name=\"").append(componentInfo.getName()).append("\" ");
            }

            AssertUtil.notBlank(componentInfo.getDescribe(), "基本组件执行器不能为空");

            // execute
            content.append("execute=\"").append(componentInfo.getDescribe()).append("\" ");

            appendStandardAttributes(content, current.getComponentInfo());
            appendExtensionAttributes(content, current.getComponentInfo());

            content.append("/>\n");

            boolean scripted = false;
            if (AuxiliaryUtils.isNotBlank(componentInfo.getType())
                    && AuxiliaryUtils.isNotBlank(componentInfo.getScriptName())
                    && AuxiliaryUtils.isNotBlank(componentInfo.getScript())) {
                scripted = true;
                ScriptCollector.collect(componentInfo.getScriptName(), componentInfo.getScript(), componentInfo.getScriptType());
            }

            if (scripted && AuxiliaryUtils.isNotBlank(componentInfo.getRollbackScriptType())
                    && AuxiliaryUtils.isNotBlank(componentInfo.getScriptName())
                    && AuxiliaryUtils.isNotBlank(componentInfo.getRollbackScript())) {
                ScriptCollector.collect(componentInfo.getScriptName() + "-rollback", componentInfo.getRollbackScript(), componentInfo.getRollbackScriptType());
            }

            // next node;
            Edge edge = first(ctx.sourceEdgeMap.get(current.getId()));
            if (edge != null && ctx.nodeMap.get(edge.getTarget()) != null) {
                tryEndProcessCombo(ctx, content, current, numOfTab);
                processNode(ctx, content, ctx.nodeMap.get(edge.getTarget()), numOfTab);
            }
        } else if (type == ComponentType.IF) {
            Node endedNode = findBlockEndNode(ctx, current, type);
            if (endedNode != null) {
                ctx.endedNodes.push(endedNode);
            }

            List<Edge> edges = ctx.sourceEdgeMap.get(current.getId());
            AssertUtil.notNull(edges, "edges must not be null");
            AuxiliaryUtils.appendTab(content, numOfTab);
            content.append("<if ");
            if (AuxiliaryUtils.isNotBlank(componentInfo.getName())) {
                content.append("name=\"").append(componentInfo.getName()).append("\" ");
            }

            AssertUtil.notBlank(AuxiliaryUtils.or(componentInfo.getDescribe(), componentInfo.getScriptName()), "IF组件执行类不能为空");

            // execute
            content.append("test=\"").append(AuxiliaryUtils.or(componentInfo.getDescribe(), componentInfo.getScriptName())).append("\" ");

            appendStandardAttributes(content, current.getComponentInfo());
            appendExtensionAttributes(content, current.getComponentInfo());

            content.append(">\n");

            // process edges

            // check edges label must be true, and another is false or null.
            for (Edge p : edges) {
                if (Objects.equals(p.getLabel(), "true")) {
                    AuxiliaryUtils.appendTab(content, numOfTab + 1);
                    content.append("<then>\n");
                    processNode(ctx, content, ctx.nodeMap.get(p.getTarget()), numOfTab + 2);
                    AuxiliaryUtils.appendTab(content, numOfTab + 1);
                    content.append("</then>\n");
                } else if (Objects.equals(p.getLabel(), "false")) {
                    AuxiliaryUtils.appendTab(content, numOfTab + 1);
                    content.append("<else>\n");
                    processNode(ctx, content, ctx.nodeMap.get(p.getTarget()), numOfTab + 2);
                    AuxiliaryUtils.appendTab(content, numOfTab + 1);
                    content.append("</else>\n");
                }
            }

            if (AuxiliaryUtils.isNotBlank(componentInfo.getType())
                    && AuxiliaryUtils.isNotBlank(componentInfo.getScriptName())
                    && AuxiliaryUtils.isNotBlank(componentInfo.getScript())) {
                ScriptCollector.collect(componentInfo.getScriptName(), componentInfo.getScript(), componentInfo.getScriptType());
            }
            content.append("\n");
            AuxiliaryUtils.appendTab(content, numOfTab);
            content.append("</if>\n");
            tryEndProcessCombo(ctx, content, current, numOfTab);
            if (endedNode != null) {
                ctx.endedNodes.pop();
                processNode(ctx, content, endedNode, numOfTab);
            }

        } else if (type == ComponentType.CHOOSE) {
            Node endedNode = findBlockEndNode(ctx, current, type);
            if (endedNode != null) {
                ctx.endedNodes.push(endedNode);
            }

            List<Edge> edges = ctx.sourceEdgeMap.get(current.getId());
            AssertUtil.notNull(edges, "edges must not be null");
            AssertUtil.notBlank(AuxiliaryUtils.or(componentInfo.getDescribe(), componentInfo.getScriptName()), "CHOOSE组件执行类不能为空");
            content.append("\n");
            AuxiliaryUtils.appendTab(content, numOfTab);
            content.append("<choose ");
            if (AuxiliaryUtils.isNotBlank(componentInfo.getName())) {
                content.append("name=\"").append(componentInfo.getName()).append("\" ");
            }

            // execute
            content.append("test=\"").append(AuxiliaryUtils.or(componentInfo.getDescribe(), componentInfo.getScriptName())).append("\" ");

            appendStandardAttributes(content, current.getComponentInfo());
            appendExtensionAttributes(content, current.getComponentInfo());

            content.append(">\n");

            // check edges label must have value, and one is default or null.
            for (Edge p : edges) {
                if (Objects.equals(p.getLabel(), "default") || AuxiliaryUtils.isBlank(p.getLabel())) {
                    AuxiliaryUtils.appendTab(content, numOfTab + 1);
                    content.append("<default>\n");
                    processNode(ctx, content, ctx.nodeMap.get(p.getTarget()), numOfTab + 2);
                    AuxiliaryUtils.appendTab(content, numOfTab + 1);
                    content.append("</default>\n");
                } else {
                    AuxiliaryUtils.appendTab(content, numOfTab + 1);
                    content.append("<case when=\"").append(p.getLabel()).append("\">\n");
                    processNode(ctx, content, ctx.nodeMap.get(p.getTarget()), numOfTab + 2);
                    AuxiliaryUtils.appendTab(content, numOfTab + 1);
                    content.append("</case>\n");
                }
            }

            if (AuxiliaryUtils.isNotBlank(componentInfo.getType())
                    && AuxiliaryUtils.isNotBlank(componentInfo.getScriptName())
                    && AuxiliaryUtils.isNotBlank(componentInfo.getScript())) {
                ScriptCollector.collect(componentInfo.getScriptName(), componentInfo.getScript(), componentInfo.getScriptType());
            }

            content.append("\n");
            AuxiliaryUtils.appendTab(content, numOfTab);
            content.append("</choose>\n");
            if (nested > 0) {
                numOfTab -= nested;
            }
            tryEndProcessCombo(ctx, content, current, numOfTab);
            if (endedNode != null) {
                ctx.endedNodes.pop();
                processNode(ctx, content, endedNode, numOfTab);
            }
        } else {
            throw new FlowException("uncorrected type in node.");
        }

    }

    private int tryStartProcessCombo(AnalyzerContext ctx, StringBuilder content, String comboId, int numsOfTab) {
        Combo combo = ctx.comboMap.get(comboId);
        if (combo == null) {
            return 0;
        }

        if (Objects.equals(ctx.enteredCombo.get(combo.getId()), true)) {
            return 0;
        }

        // 没有进入，处理combo
        int nested = 0;
        ctx.enteredCombo.put(combo.getId(), true);
        ComponentInfo componentInfo = combo.getComponentInfo();
        ComponentType ctype = ComponentType.valueOf(componentInfo.getType());

        if (AuxiliaryUtils.isNotBlank(combo.getParentId())) {
            nested = tryStartProcessCombo(ctx, content, combo.getParentId(), numsOfTab);
            nested++;
        }

        numsOfTab += nested;

        if (ctype == ComponentType.ADAPTER) {
            AuxiliaryUtils.appendTab(content, numsOfTab);
            content.append("<adapter ");
            if (AuxiliaryUtils.isNotBlank(componentInfo.getName())) {
                content.append("name=\"").append(componentInfo.getName()).append("\" ");
            }

            AssertUtil.notBlank(componentInfo.getDescribe(), "适配器组件不能为空");


            // execute
            content.append(" execute=\"").append(componentInfo.getDescribe()).append("\" ");

            appendStandardAttributes(content, combo.getComponentInfo());
            appendExtensionAttributes(content, combo.getComponentInfo());

            content.append(">\n");
        } else if (AuxiliaryUtils.isNotBlank(componentInfo.getName()) && !AuxiliaryUtils.isAnonymous(componentInfo.getName())){
            AuxiliaryUtils.appendTab(content, numsOfTab);
            content.append("<pipeline name=\"").append(componentInfo.getName()).append("\">");
        }

        return nested;
    }

    private void tryEndProcessCombo(AnalyzerContext ctx, StringBuilder content, Node current, int numsOfTab) {
        if (AuxiliaryUtils.isBlank(current.getComboId())) {
            return;
        }

        Combo combo = ctx.comboMap.get(current.getComboId());
        if (combo == null) {
            return;
        }

        String removeId = current.getId();

        Stack<String> processCombos = new SafeStack<>();
        processCombos.add(combo.getId());
        while (!processCombos.isEmpty()) {
            String comboId = processCombos.pop();
            Combo cur = ctx.comboMap.get(comboId);

            List<String> nodes = ctx.comboNodeMap.getOrDefault(comboId, new ArrayList<>(0));
            nodes.remove(removeId);
            // end combo
            if (nodes.isEmpty()) {
                content.append("\n");
                AuxiliaryUtils.appendTab(content, numsOfTab--);
                if (Objects.equals(cur.getComponentInfo().getType(), ComponentType.ADAPTER.name())) {
                    content.append("</adapter>\n");
                } else if (AuxiliaryUtils.isNotBlank(cur.getComponentInfo().getName()) && !AuxiliaryUtils.isAnonymous(cur.getComponentInfo().getName())) {
                    content.append("</pipeline>\n");
                }

                if (AuxiliaryUtils.isBlank(cur.getParentId())) {
                    continue;
                }

                Combo parent = ctx.comboMap.get(cur.getParentId());
                processCombos.add(parent.getId());
                removeId = cur.getId();
            }
        }
    }

    private Node findBlockEndNode(AnalyzerContext ctx, Node current, ComponentType type) {
        List<Edge> edges = ctx.sourceEdgeMap.get(current.getId());

        if (type == ComponentType.IF) {
            Edge endBranch = edges.stream().filter(p -> AuxiliaryUtils.isBlank(p.getLabel())).findFirst().orElse(null);
            if (endBranch != null) {
                return ctx.nodeMap.get(endBranch.getTarget());
            }

            if (edges.size() == 1) {
                return null;
            }
        }

        // 从edges出发，每条边都遍历到结束，然后获取所有边相同的第一个节点
        List<List<String>> edgeNodes = new ArrayList<>();

        edges.forEach(p -> {
            Stack<Edge> edgeStack = new SafeStack<>();
            edgeStack.add(p);

            // 不包含当前节点
            List<String> nodes = new ArrayList<>();

            while (!edgeStack.isEmpty()) {
                Edge edge = edgeStack.pop();
                List<Edge> nextEdges = ctx.sourceEdgeMap.get(edge.getTarget());
                // 多条边随机选择第一条即可
                Edge candidate = first(nextEdges);

                if (candidate != null) {
                    edgeStack.add(candidate);
                }

                nodes.add(edge.getTarget());
            }


            edgeNodes.add(nodes);
        });

        List<String> first = edgeNodes.get(0);
        String candidateNodeId = null;

        for (String node : first) {
            if (edgeNodes.stream().allMatch(p -> p.contains(node))) {
                candidateNodeId = node;
                break;
            }
        }

        if (candidateNodeId != null) {
            return ctx.nodeMap.get(candidateNodeId);
        }

        return null;
    }

    private void removeFixedNodesAndEdges(G6Result result) {
        result.getNodes().removeIf(p -> Objects.equals(p.getId(), START_NODE) || Objects.equals(p.getId(), END_NODE));
        result.getEdges().removeIf(p -> Objects.equals(p.getSource(), START_NODE) || Objects.equals(p.getTarget(), END_NODE));
    }

    private static void checkArguments(AnalyzerContext ctx) {
        AssertUtil.notBlank(ctx.engineName, "engineName must not be blank!");
        AssertUtil.notBlank(ctx.pipelineName, "process must not be blank!");
        AssertUtil.isFalse(ctx.nodeMap.isEmpty(), "Nodes size must greater than zero");
        AssertUtil.isFalse(ctx.sourceEdgeMap.isEmpty(), "Edges size must greater than zero");
    }

    private Node findStartNode(G6Result result, AnalyzerContext ctx) {
        // 没有边的target，只有边的source的node为开始节点
        return result.getNodes().stream().filter(p -> {
            List<Edge> edges = ctx.sourceEdgeMap.get(p.getId());
            List<Edge> targetEdges = ctx.targetEdgeMap.get(p.getId());
            return edges != null && edges.size() >= 1 && (targetEdges == null || targetEdges.isEmpty());
        }).findFirst().orElse(null);
    }

    private Node findEndNode(G6Result result, AnalyzerContext ctx) {
        // 没有边的source，只有边的target的node为结束节点
        return result.getNodes().stream().filter(p -> {
            List<Edge> edges = ctx.sourceEdgeMap.get(p.getId());
            List<Edge> targetEdges = ctx.targetEdgeMap.get(p.getId());
            return (edges == null || edges.size() == 0) && (targetEdges != null && targetEdges.size() >= 1);
        }).findFirst().orElse(null);
    }

    private void appendStandardAttributes(StringBuilder content, ComponentInfo componentInfo) {
        componentInfo.getAttributes().forEach(p -> {
            if (Attributes.byName(p.getName()) == null || AuxiliaryUtils.isBlank(p.getValue())) {
                return;
            }
            content.append(" ").append(p.getName()).append("=\"").append(p.getValue()).append("\" ");
        });
    }

    private void appendExtensionAttributes(StringBuilder content, ComponentInfo componentInfo) {
        if (AuxiliaryUtils.isBlank(componentInfo.getExtensionAttributes())) {
            return;
        }

        try {
            Properties properties = new Properties();
            properties.load(new ByteArrayInputStream(componentInfo.getExtensionAttributes().getBytes(StandardCharsets.UTF_8)));
            properties.forEach((k,v) -> {
                if (k == null || AuxiliaryUtils.isBlank(String.valueOf(k))
                        || Attributes.byName(String.valueOf(k)) != null) {
                    return;
                }

                content.append(" ").append(k).append("=\"").append(v).append("\"");
            });
        } catch (Exception e) {
            throw new FlowException("extension attribute error", e);
        }


    }

    private <T> T first(List<T> list) {
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    private static class AnalyzerContext {
        public final Map<String, List<Edge>> sourceEdgeMap = new HashMap<>();
        public final Map<String, List<Edge>> targetEdgeMap = new HashMap<>();
        public final Map<String, Node> nodeMap = new HashMap<>();
        public final Map<String, Combo> comboMap = new HashMap<>();
        public final Stack<Node> endedNodes = new SafeStack<>();
        public final Map<String, Boolean> enteredCombo = new HashMap<>();
        public final Map<String, List<String>> comboNodeMap = new HashMap<>();
        public String engineName;
        public String pipelineName;

        AnalyzerContext(G6Result result) {
            engineName = result.getName();
            pipelineName = result.getProcess();
            result.getNodes().forEach(p -> {
                nodeMap.put(p.getId(), p);
                if (AuxiliaryUtils.isNotBlank(p.getComboId())) {
                    List<String> nodes = comboNodeMap.getOrDefault(p.getComboId(), new ArrayList<>(8));
                    nodes.add(p.getId());
                    comboNodeMap.put(p.getComboId(), nodes);
                }

            });
            result.getCombos().forEach(p -> {
                comboMap.put(p.getId(), p);
                if (AuxiliaryUtils.isNotBlank(p.getParentId())) {
                    List<String> nodes = comboNodeMap.getOrDefault(p.getParentId(), new ArrayList<>(8));
                    nodes.add(p.getId());
                    comboNodeMap.put(p.getParentId(), nodes);
                }
            });
            result.getEdges().forEach(p -> {
                List<Edge> edges = sourceEdgeMap.getOrDefault(p.getSource(), new ArrayList<>(0));
                edges.add(p);
                sourceEdgeMap.put(p.getSource(), edges);

                List<Edge> targetEdges = targetEdgeMap.getOrDefault(p.getTarget(), new ArrayList<>(0));
                targetEdges.add(p);
                targetEdgeMap.put(p.getTarget(), targetEdges);
            });
        }
    }
}
