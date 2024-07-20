package org.smartboot.flow.core.parser;

import org.smartboot.flow.core.parser.definition.EngineDefinition;
import org.smartboot.flow.core.util.AssertUtil;
import org.w3c.dom.Element;


/**
 * Parse engine tag.
 * <code>
 *     <engine name="engine's name" pipeline="engine's pipeline"/>
 * </code>
 *
 * @author qinluo
 * @date 2022/11/15 20:51
 * @since 1.0.0
 */
public class EngineElementParser extends AbstractElementParser {

    @Override
    public void doParse(Element element, ParserContext context) {
        EngineDefinition definition = new EngineDefinition();
        // name
        String name = element.getAttribute(ParseConstants.NAME);
        AssertUtil.notBlank(name, "[engine] element's name must not be blank");

        // pipeline
        String pipeline = element.getAttribute(ParseConstants.PIPELINE);
        AssertUtil.notBlank(pipeline, "[engine] element's pipeline must not be blank");

        definition.setName(name);
        definition.setPipeline(pipeline);
        // name as identifier.
        definition.setIdentifier(name);

        // 提取threadpool开头的属性
        definition.getThreadpools().addAll(ElementUtils.extraAttributes(element, new PrefixAttributeAcceptor(ParseConstants.THREAD)));
        definition.setExceptionHandler(element.getAttribute(ParseConstants.EXCEPTION_HANDLER));

        context.register(definition);
    }

    @Override
    public String getElementName() {
        return ParseConstants.ENGINE;
    }
}
