package org.smartboot.flow.core.parser;

import org.smartboot.flow.core.parser.definition.ComponentDefinition;
import org.smartboot.flow.core.parser.definition.PipelineDefinition;
import org.smartboot.flow.core.util.AssertUtil;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Parse pipeline tag.
 * <code>
 *     <pipeline name="">
 *          <if/>
 *          <choose/>
 *          <component/>
 *          <!-- nested pipeline -->
 *          <pipeline/>
 *     </pipeline>
 * </code>
 *
 * @author qinluo
 * @date 2022/11/15 20:51
 * @since 1.0.0
 */
public class PipelineElementParser extends AbstractElementParser {

    @Override
    public void doParse(Element element, ParserContext context) {
        PipelineDefinition definition = new PipelineDefinition();
        // pipeline name as identifier
        String identifier = element.getAttribute(ParseConstants.NAME);
        AssertUtil.notBlank(identifier, "pipeline name must not be null");

        // Check has any elements.
        List<Element> elements = ElementUtils.subElements(element);
        AssertUtil.isTrue(elements.size() != 0, "[pipeline] element's sub elements must not be empty");

        definition.setName(identifier);
        definition.setIdentifier(identifier);

        List<ComponentDefinition> subDefinitions = new ArrayList<>();
        for (Element sub : elements) {
            subDefinitions.add(parseSubElement(sub, context));
        }

        definition.setChildren(subDefinitions);
        context.register(definition);

    }

    @Override
    public String getElementName() {
        return ParseConstants.PIPELINE;
    }
}
