package org.smartboot.flow.manager.reload;

/**
 * @author qinluo
 * @date 2022-12-21 16:36:25
 * @since 1.0.0
 */
public interface XmlSelector {

    /**
     * Select engine xml content.
     *
     * @param engineName engineName.
     * @return           xml content.
     */
    String select(String engineName);
}
