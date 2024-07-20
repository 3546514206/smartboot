package org.smartboot.smart.flow.admin.g6;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author qinluo
 * @date 2023/2/10 20:24
 * @since 1.0.0
 */
public class Node {

    private String id;
    private String type;
    private String label;
    private String comboId;
    private double x;
    private double y;
    private List<List<Double>> anchorPoints = new ArrayList<>();
    private final JSONObject style = new JSONObject();
    private final JSONObject customStyle = new JSONObject();
    private String title;

    /**
     * 组件信息
     */
    private ComponentInfo componentInfo;
    private Object customData = new JSONObject();
    private List<Object> panels = new ArrayList<>();

    public Node() {
        anchorPoints.add(Arrays.asList(0.5, 0d));
        anchorPoints.add(Arrays.asList(0.5, 1d));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Object> getPanels() {
        return panels;
    }

    public void setPanels(List<Object> panels) {
        this.panels = panels;
    }

    public Object getCustomData() {
        return customData;
    }

    public void setCustomData(Object customData) {
        this.customData = customData;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String getComboId() {
        return comboId;
    }

    public void setComboId(String comboId) {
        this.comboId = comboId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<List<Double>> getAnchorPoints() {
        return anchorPoints;
    }

    public void setAnchorPoints(List<List<Double>> anchorPoints) {
        this.anchorPoints = anchorPoints;
    }

    public ComponentInfo getComponentInfo() {
        return componentInfo;
    }

    public void setComponentInfo(ComponentInfo componentInfo) {
        this.componentInfo = componentInfo;
    }

    public void addStyle(String key, String value) {
        style.put(key, value);
    }

    public JSONObject getStyle() {
        return style;
    }

    public JSONObject getCustomStyle() {
        return customStyle;
    }
}
