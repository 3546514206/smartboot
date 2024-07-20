package org.smartboot.flow.helper.useful;

/**
 * 通过engineName查询engine
 *
 * @author qinluo
 * @date 2022-12-07 14:30:08
 * @since 1.0.0
 */
public class DefaultEngineQuery extends AbstractEngineQuery {

    private static final long serialVersionUID = -383072247314629042L;

    private final String engineName;

    public DefaultEngineQuery(String name) {
        this.engineName = name;
    }

    @Override
    public String getKey() {
        return engineName;
    }
}
