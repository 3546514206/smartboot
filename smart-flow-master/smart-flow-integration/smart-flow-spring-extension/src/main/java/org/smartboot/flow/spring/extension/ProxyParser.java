package org.smartboot.flow.spring.extension;

import org.smartboot.flow.core.parser.ElementParser;
import org.smartboot.flow.core.parser.ElementUtils;
import org.smartboot.flow.core.util.AssertUtil;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;


/**
 * @author qinluo
 * @date 2022/11/16 22:13
 * @since 1.0.0
 */
public class ProxyParser implements BeanDefinitionParser {

    private static final ProxyParser INSTANCE = new ProxyParser();

    private org.smartboot.flow.core.parser.ParserContext context;

    public static ProxyParser getInstance() {
        return INSTANCE;
    }

    public org.smartboot.flow.core.parser.ParserContext getContext() {
        return context;
    }

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        if (context == null) {
            context = new org.smartboot.flow.core.parser.ParserContext();
        }

        SmartFlowRegistrar.registerAll(parserContext.getRegistry());
        // Ensure identifier in spring scope is unique.
        context.setIdentifierManager(new SpringIdentifierManager(parserContext.getRegistry()));

        ElementParser parser = context.getParser(ElementUtils.getName(element));
        AssertUtil.notNull(parser, "Could not find parse for element " + ElementUtils.getName(element));

        parser.parseElement(element, context);
        return null;
    }

    public void reset() {
        this.context = null;
    }
}
