package org.smartboot.flow.helper.useful;

import java.io.Serializable;

/**
 * @author qinluo
 * @version 1.0.0
 * @since 2019-05-20 14:23
 */
public abstract class AbstractEngineQuery implements Serializable {

    private static final long serialVersionUID = -4493559976724466867L;

    /**
     * 得到组装字符串
     *
     * @return 组装的字符串，由各个实现自由决定
     */
    public abstract String getKey();

}
