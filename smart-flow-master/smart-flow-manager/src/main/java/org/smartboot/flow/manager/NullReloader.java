package org.smartboot.flow.manager;

import org.smartboot.flow.core.exception.FlowException;
import org.smartboot.flow.manager.reload.Reloader;

/**
 * @author qinluo
 * @date 2023-01-05 19:47:49
 * @since 1.0.0
 */
public class NullReloader implements Reloader {

    public static final Reloader NULL = new NullReloader();

    @Override
    public void reload(String engineName) {
        throw new FlowException("Unsupported reload");
    }
}
