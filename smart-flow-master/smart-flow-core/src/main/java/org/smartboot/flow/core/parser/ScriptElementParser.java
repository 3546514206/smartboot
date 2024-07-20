package org.smartboot.flow.core.parser;

import org.smartboot.flow.core.parser.definition.ScriptDefinition;
import org.smartboot.flow.core.util.AssertUtil;
import org.w3c.dom.Element;

/**
 * Parse <code>script</code> element.
 *
 * @author qinluo
 * @date 2022-11-15 13:00:59
 * @since 1.0.3
 */
public class ScriptElementParser extends AbstractElementParser {

    @Override
    public void doParse(Element element, ParserContext context) {
        ScriptDefinition elementDefinition = new ScriptDefinition();
        String name = element.getAttribute(ParseConstants.NAME);
        AssertUtil.notBlank(name, "script name must not be blank");
        String type = element.getAttribute("type");

        elementDefinition.setName(name);
        elementDefinition.setIdentifier(name);
        elementDefinition.setType(type);
        elementDefinition.setScript(element.getTextContent());
        context.register(elementDefinition);
    }

    @Override
    public String getElementName() {
        return ParseConstants.SCRIPT;
    }
}
