package org.smartboot.flow.script.extension.ognl;

import ognl.Ognl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.flow.core.EngineContext;
import org.smartboot.flow.core.exception.FlowException;
import org.smartboot.flow.core.script.ScriptConstants;
import org.smartboot.flow.core.script.ScriptExecutor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qinluo
 * @date 2022/11/29 21:01
 * @since 1.0.0
 */
public class OgnlScriptExecutor<T, S> extends ScriptExecutor<T, S> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OgnlScriptExecutor.class);

    @Override
    public Object execute(EngineContext<T, S> engineContext) {
        try {
            Map<String, Object> context = new HashMap<>(8);
            context.put(ScriptConstants.REQ, engineContext.getReq());
            context.put(ScriptConstants.RESULT, engineContext.getResult());
            context.put(ScriptConstants.CONTEXT, engineContext);
            context.put(ScriptConstants.CTX, engineContext);

            Map<String, Object> variables = this.bindCustomized(engineContext);
            if (variables != null) {
                context.putAll(variables);
            }

            Object value = Ognl.getValue(script, context);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("evaluate script [{}] finished", script);
                LOGGER.debug("evaluate value {}", value);
            }

            return value;
        } catch (Exception e) {
            throw new FlowException("Ognl evaluate failed, script : " + script, e);
        }
    }

    @Override
    public String getType() {
        return "OGNL";
    }
}
