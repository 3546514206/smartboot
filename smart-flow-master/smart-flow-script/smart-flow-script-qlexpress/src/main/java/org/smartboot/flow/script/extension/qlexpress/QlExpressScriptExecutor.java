package org.smartboot.flow.script.extension.qlexpress;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.flow.core.EngineContext;
import org.smartboot.flow.core.exception.FlowException;
import org.smartboot.flow.core.script.ScriptConstants;
import org.smartboot.flow.core.script.ScriptExecutor;

import java.util.Map;

/**
 * @author qinluo
 * @date 2022/11/29 21:01
 * @since 1.0.0
 */
public class QlExpressScriptExecutor<T, S> extends ScriptExecutor<T, S> {

    private static final Logger LOGGER = LoggerFactory.getLogger(QlExpressScriptExecutor.class);

    @Override
    public Object execute(EngineContext<T, S> engineContext) {
        try {

            DefaultContext<String, Object> qlContext = new DefaultContext<>();
            qlContext.put(ScriptConstants.REQ, engineContext.getReq());
            qlContext.put(ScriptConstants.RESULT, engineContext.getResult());
            qlContext.put(ScriptConstants.CONTEXT, engineContext);
            qlContext.put(ScriptConstants.CTX, engineContext);

            Map<String, Object> variables = this.bindCustomized(engineContext);
            if (variables != null) {
                qlContext.putAll(variables);
            }

            ExpressRunner runner = new ExpressRunner();
            Object value = runner.execute(script, qlContext, null, true, false);
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
        return "qlexpress";
    }
}
