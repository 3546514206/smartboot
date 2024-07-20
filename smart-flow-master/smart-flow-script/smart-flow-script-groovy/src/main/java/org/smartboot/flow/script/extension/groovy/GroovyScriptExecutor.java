package org.smartboot.flow.script.extension.groovy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.flow.core.EngineContext;
import org.smartboot.flow.core.exception.FlowException;
import org.smartboot.flow.core.script.ScriptConstants;
import org.smartboot.flow.core.script.ScriptExecutor;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Map;

/**
 * @author qinluo
 * @date 2022/11/29 21:01
 * @since 1.0.0
 */
public class GroovyScriptExecutor<T, S> extends ScriptExecutor<T, S> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroovyScriptExecutor.class);
    private static final ScriptEngineManager MANAGER = new ScriptEngineManager();

    protected String getScriptLang() {
        return "groovy";
    }

    @Override
    public Object execute(EngineContext<T, S> engineContext) {
        try {
            ScriptEngine engine = MANAGER.getEngineByName(getScriptLang());
            Bindings data = engine.createBindings();
            data.put(ScriptConstants.REQ, engineContext.getReq());
            data.put(ScriptConstants.RESULT, engineContext.getResult());
            data.put(ScriptConstants.CONTEXT, engineContext);
            data.put(ScriptConstants.CTX, engineContext);

            Map<String, Object> variables = this.bindCustomized(engineContext);
            if (variables != null) {
                data.putAll(variables);
            }

            Object value = engine.eval(script, data);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("evaluate script [{}] finished", script);
                LOGGER.debug("evaluate value {}", value);
            }

            return value;
        } catch (Exception e) {
            throw new FlowException(getScriptLang() + " evaluate failed, script : " + script, e);
        }
    }

    @Override
    public String getType() {
        return getScriptLang();
    }
}
