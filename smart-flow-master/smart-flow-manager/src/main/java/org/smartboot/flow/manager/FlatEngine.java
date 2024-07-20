package org.smartboot.flow.manager;

/**
 * @author qinluo
 * @date 2023-02-07
 * @since 1.0.7
 */
public class FlatEngine {

    /**
     * Md5 string
     */
    private String md5;

    /**
     * xml content.
     */
    private String content;

    /**
     * engine created stamp.
     */
    private long timestamp;

    /**
     * engine name.
     */
    private String name;

    private volatile boolean reportContent;

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getReportContent() {
        return reportContent;
    }

    public void setReportContent(boolean reportContent) {
        this.reportContent = reportContent;
    }
}
