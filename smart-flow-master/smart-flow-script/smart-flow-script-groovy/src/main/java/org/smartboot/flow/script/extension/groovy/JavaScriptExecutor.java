package org.smartboot.flow.script.extension.groovy;

/**
 * @author qinluo
 * @date 2022/11/29 21:01
 * @since 1.0.0
 */
public class JavaScriptExecutor<T, S> extends GroovyScriptExecutor<T, S> {

    @Override
    protected String getScriptLang() {
        return "javascript";
    }
}
