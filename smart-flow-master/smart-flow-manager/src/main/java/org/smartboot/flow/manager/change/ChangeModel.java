package org.smartboot.flow.manager.change;

import java.io.Serializable;

/**
 * @author qinluo
 * @date 2022/11/23 21:29
 * @since 1.0.0
 */
public class ChangeModel implements Serializable {
    private static final long serialVersionUID = -3610942087085947434L;

    /**
     * Change action
     *
     * @see ManagerAction
     */
    private String action;

    /**
     * Change value.
     */
    private String value;

    private String identifier;
    private String name;
    private long timestamp;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
