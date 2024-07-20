package org.smartboot.smart.flow.admin.g6;

import java.io.Serializable;

/**
 * @author qinluo
 * @date 2023-02-23
 * @since 1.0.0
 */
public class AttrInfo implements Serializable {

    private static final long serialVersionUID = -2013140191725322410L;

    private String name;
    private String value;
    private String remark;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
