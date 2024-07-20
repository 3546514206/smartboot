package org.smartboot.smart.flow.admin.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Date;

/**
 * @author qinluo
 * @date 2023-01-30
 * @since 1.0.0
 */
public class EngineHistory implements Serializable {
    private static final long serialVersionUID = 5630916400064848439L;

    private Long id;
    private String engineName;
    private String content;
    private int version;
    private Long engineId;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date created;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updated;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEngineName() {
        return engineName;
    }

    public void setEngineName(String engineName) {
        this.engineName = engineName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Long getEngineId() {
        return engineId;
    }

    public void setEngineId(Long engineId) {
        this.engineId = engineId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}
