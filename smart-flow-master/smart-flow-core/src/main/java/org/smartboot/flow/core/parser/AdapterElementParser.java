package org.smartboot.flow.core.parser;

import org.smartboot.flow.core.parser.definition.AdapterDefinition;
import org.smartboot.flow.core.parser.definition.ComponentDefinition;
import org.smartboot.flow.core.util.AssertUtil;
import org.w3c.dom.Element;

import java.util.List;

/**
 * @author huqiang
 * @since 2022/12/8 11:08
 */
public class AdapterElementParser extends AbstractElementParser{
    @Override
    public String getElementName() {
        return ParseConstants.ADAPTER;
    }

    @Override
    public void doParse(Element element, ParserContext context) {
        AdapterDefinition definition = new AdapterDefinition();
        String identifier = getIdentifier(element, context);
        String execute = element.getAttribute(ParseConstants.EXECUTE);
        AssertUtil.notBlank(execute, "attribute [execute] cannot be null");

        // Check has any elements.
        List<Element> elements = ElementUtils.subElements(element);
        AssertUtil.isTrue(elements.size() != 0, "[adapter] element's sub elements must not be empty");

        definition.setExecute(execute);
        definition.setIdentifier(identifier);
        ElementUtils.build(definition, element);

        ComponentDefinition def = parseSubElements(element, context);
        definition.setPipelineElement(def);

        super.parseExtensionAttributes(element, definition);

        context.register(definition);

    }
}
