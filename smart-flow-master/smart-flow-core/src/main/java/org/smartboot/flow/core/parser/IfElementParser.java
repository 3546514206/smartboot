package org.smartboot.flow.core.parser;

import org.smartboot.flow.core.parser.definition.ComponentDefinition;
import org.smartboot.flow.core.parser.definition.ElementDefinition;
import org.smartboot.flow.core.parser.definition.IfElementDefinition;
import org.smartboot.flow.core.util.AssertUtil;
import org.smartboot.flow.core.util.AuxiliaryUtils;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * parse <pre> if </pre> element
 *
 * @author huqiang
 * @since 2022/11/15 16:30
 */
public class IfElementParser extends AbstractElementParser {

    private static final List<String> ALLOWED = new ArrayList<>();

    public IfElementParser() {
        ALLOWED.add(ParseConstants.ELSE);
        ALLOWED.add(ParseConstants.THEN);
    }

    private void allowed(String elementName) {
        AssertUtil.isTrue(ALLOWED.contains(elementName), "unsupported tag " + elementName + " in if");
    }

    @Override
    public void doParse(Element element, ParserContext context) {
        String test = element.getAttribute(ParseConstants.TEST);
        AssertUtil.notBlank(test, "attribute [test] cannot be null");

        IfElementDefinition ifDef = new IfElementDefinition();
        ifDef.setIdentifier(super.getIdentifier(element, context));
        ifDef.setTest(test);
        ElementUtils.build(ifDef, element);

        List<Element> elementList = ElementUtils.subElements(element);
        AssertUtil.isTrue(elementList.size() != 0, "[if] childNodes can't be null");
        AssertUtil.isTrue(elementList.size() <= 2, "[if] childNodes only then and else");

        ComponentDefinition elseDef = null;
        ComponentDefinition thenDef = null;

        for (Element subElement : elementList) {
            // 标签名
            String elementName = ElementUtils.getName(subElement);
            this.allowed(elementName);

            ElementDefinition basicDef = new ElementDefinition();
            ElementUtils.build(basicDef, subElement);
            basicDef.setIdentifier(super.getIdentifier(subElement, context));
            basicDef.setContext(context);
            basicDef.setExecute(subElement.getAttribute(ParseConstants.EXECUTE));

            if (Objects.equals(elementName, ParseConstants.ELSE)) {
                elseDef = basicDef;
            } else {
                thenDef = basicDef;
            }

            String type = subElement.getAttribute(ParseConstants.EXECUTE);
            if (AuxiliaryUtils.isNotBlank(type)) {
                super.parseExtensionAttributes(subElement, basicDef);
                super.extraBindingAttrs(subElement, basicDef);
                continue;
            }

            // Wrap sub elements as pipeline.
            ComponentDefinition def = parseSubElements(subElement, context);
            if (Objects.equals(elementName, ParseConstants.ELSE)) {
                elseDef = def;
            } else if (Objects.equals(elementName, ParseConstants.THEN)) {
                thenDef = def;
            }
        }

        ifDef.setIfElseRef(elseDef);
        ifDef.setIfThenRef(thenDef);
        super.parseExtensionAttributes(element, ifDef);
        context.register(ifDef);
    }


    @Override
    public String getElementName() {
        return ParseConstants.IF;
    }
}
