package org.smartboot.flow.helper.view;

/**
 * @author qinluo
 * @date 2022-11-14 22:05:14
 * @since 1.0.0
 */
public enum Color {

    /**
     * 同步组件，淡蓝
     */
    ASYNC("异步组件", "66FFFF"),
    /**
     * 可降级组件，淡绿
     */
    DEGRADABLE("可降级组件", "99FF99"),

    /**
     * 可回滚组件
     */
    ROLLBACKABLE("可回滚组件","FF9933"),

    DISABLED("已禁用组件",  "grey"),
    ;

    private final String rgb;
    private final String desc;

    Color(String desc, String rgb) {
        this.rgb = rgb;
        this.desc = desc;
    }

    public String getColor() {
        return "#" + rgb;
    }

    public String mix(String color) {
        return color + "\\" + rgb;
    }

    public String getDesc() {
        return desc;
    }
}
