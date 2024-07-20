package org.smartboot.flow.core.parser;

import java.util.Objects;

/**
 * @author yamikaze
 * @date 2022/11/13
 */
public interface ParseConstants {

    /**
     * tags
     */
    String ENGINES = "engines";
    String ENGINE = "engine";
    String PIPELINE = "pipeline";
    String IF = "if";
    String CHOOSE = "choose";
    String COMPONENT = "component";
    String ADAPTER = "adapter";
    String SCRIPT = "script";
    String SCRIPT_LOADER = "script-loader";

    /**
     * Attributes;
     */
    String NAME = "name";
    String EXECUTE = "execute";
    String TEST = "test";
    String THEN = "then";
    String WHEN = "when";
    String ELSE = "else";
    String DEFAULT = "default";
    String CASE = "case";
    String SUBPROCESS = "subprocess";

    String EXCEPTION_HANDLER = "exceptionHandler";

    String THREAD = "threadpool.";

    /**
     * Execute binding attrs.
     */
    String EXECUTE_BINDING = "execute.";

    static boolean isBuiltin(String attribute) {
        return Objects.equals(EXECUTE, attribute)
                || Objects.equals(WHEN, attribute)
                || Objects.equals(SUBPROCESS, attribute)
                || Objects.equals(TEST, attribute);
    }

}
