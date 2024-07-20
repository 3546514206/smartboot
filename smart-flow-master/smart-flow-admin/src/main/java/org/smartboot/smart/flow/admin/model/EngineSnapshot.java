package org.smartboot.smart.flow.admin.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author qinluo
 * @date 2023-01-30
 * @since 1.0.7
 */
public class EngineSnapshot implements Serializable {
    private static final long serialVersionUID = 5630916400064848439L;

    private Long id;
    private String engineName;
    private String content;
    private String md5;

    private Date created;

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

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
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
