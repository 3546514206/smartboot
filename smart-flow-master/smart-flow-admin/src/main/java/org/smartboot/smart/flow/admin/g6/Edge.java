package org.smartboot.smart.flow.admin.g6;

/**
 * @author qinluo
 * @date 2023/2/10 20:24
 * @since 1.0.0
 */
public class Edge {

    private String source;
    private String target;
    private String label;
    private String type;
    private Integer sourceAnchor;
    private Integer targetAnchor;

    public Integer getTargetAnchor() {
        return targetAnchor;
    }

    public void setTargetAnchor(Integer targetAnchor) {
        this.targetAnchor = targetAnchor;
    }

    public Integer getSourceAnchor() {
        return sourceAnchor;
    }

    public void setSourceAnchor(Integer sourceAnchor) {
        this.sourceAnchor = sourceAnchor;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
