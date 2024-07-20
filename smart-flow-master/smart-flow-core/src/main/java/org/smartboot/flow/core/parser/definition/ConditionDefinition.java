package org.smartboot.flow.core.parser.definition;

/**
 * @author yamikaze
 * @date 2023/6/17 10:27
 * @since 1.1.0
 */
public abstract class ConditionDefinition extends ComponentDefinition {

    private String test;

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }
}
