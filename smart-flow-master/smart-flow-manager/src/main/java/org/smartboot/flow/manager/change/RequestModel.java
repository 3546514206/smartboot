package org.smartboot.flow.manager.change;

import java.io.Serializable;
import java.util.List;

/**
 * @author qinluo
 * @date 2022-11-25 10:38:04
 * @since 1.0.0
 */
public class RequestModel implements Serializable {
    private static final long serialVersionUID = -457007982997285755L;

    private long timestamp;
    private String address;
    private String host;
    private List<String> engineNames;

    public List<String> getEngineNames() {
        return engineNames;
    }

    public void setEngineNames(List<String> engineNames) {
        this.engineNames = engineNames;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
