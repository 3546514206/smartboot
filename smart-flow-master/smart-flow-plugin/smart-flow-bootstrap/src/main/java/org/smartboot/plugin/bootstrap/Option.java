package org.smartboot.plugin.bootstrap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author yamikaze
 * @date 2023/6/18 18:39
 * @since 1.0.0
 */
public enum Option {

    HELP("-h", 0, "-help"),

    INVOKE_TREE("-t", 0),

    FILE("-f", 1, "-file"),

    ;

    private final String option;
    private final int argCnt;
    private final List<String> alias;

    Option(String option, int argCnt, String ...args) {
        this.option = option;
        this.argCnt = argCnt;
        this.alias = new ArrayList<>();
        Collections.addAll(this.alias, args);
    }

    public String getOption() {
        return option;
    }

    public int getArgCnt() {
        return argCnt;
    }

    public List<String> getAlias() {
        return alias;
    }

    public static Option findOption(String value) {
        for (Option option : values()) {
            if (Objects.equals(option.option, value) || option.alias.contains(value)) {
                return option;
            }
        }

        return null;
    }
}
