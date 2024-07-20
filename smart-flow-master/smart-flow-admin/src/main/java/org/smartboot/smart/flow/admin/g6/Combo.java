package org.smartboot.smart.flow.admin.g6;

import com.alibaba.fastjson.JSONObject;

/**
 * @author qinluo
 * @date 2023/2/10 21:27
 * @since 1.0.0
 */
public class Combo {

    private String id;
    private String parentId;
    private String label;

    /**
     * 组件信息
     */
    private ComponentInfo componentInfo;

    private Object customData = new JSONObject();

    public Object getCustomData() {
        return customData;
    }

    public void setCustomData(Object customData) {
        this.customData = customData;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public ComponentInfo getComponentInfo() {
        return componentInfo;
    }

    public void setComponentInfo(ComponentInfo componentInfo) {
        this.componentInfo = componentInfo;
    }
}
