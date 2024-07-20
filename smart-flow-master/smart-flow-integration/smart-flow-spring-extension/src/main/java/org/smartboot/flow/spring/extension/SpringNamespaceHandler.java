package org.smartboot.flow.spring.extension;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author qinluo
 * @date 2022/11/16 22:12
 * @since 1.0.0
 */
public class SpringNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        ProxyParser proxyParser = ProxyParser.getInstance();
        this.registerBeanDefinitionParser("pipeline", proxyParser);
        this.registerBeanDefinitionParser("engine", proxyParser);
        this.registerBeanDefinitionParser("script", proxyParser);
        this.registerBeanDefinitionParser("script-loader", proxyParser);
    }
}
