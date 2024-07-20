package org.smartboot.flow.core.parser;

import org.smartboot.flow.core.parser.definition.ChooseDefinition;
import org.smartboot.flow.core.parser.definition.ComponentDefinition;
import org.smartboot.flow.core.parser.definition.ElementDefinition;
import org.smartboot.flow.core.util.AssertUtil;
import org.smartboot.flow.core.util.AuxiliaryUtils;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author huqiang
 * @since 2022/11/15 17:39
 */
public class ChooseElementParser extends AbstractElementParser{

    private static final List<String> ALLOWED = new ArrayList<>();

    public ChooseElementParser() {
        ALLOWED.add(ParseConstants.CASE);
        ALLOWED.add(ParseConstants.DEFAULT);
    }

    private void allowed(String elementName) {
        AssertUtil.isTrue(ALLOWED.contains(elementName), "unsupported tag " + elementName + " in choose");
    }

    @Override
    public void doParse(Element element, ParserContext context) {
        String test = element.getAttribute(ParseConstants.TEST);
        AssertUtil.notBlank(test, "attribute [test] cannot be null");

        String identifier = super.getIdentifier(element, context);
        ChooseDefinition chooseDefinition = new ChooseDefinition();
        chooseDefinition.setIdentifier(identifier);
        chooseDefinition.setTest(test);
        ElementUtils.build(chooseDefinition, element);

        List<Element> subElements = ElementUtils.subElements(element);
        Map<String, ComponentDefinition> caseMap = new HashMap<>(8);

        for (Element subElement : subElements) {
            String elementName = ElementUtils.getName(subElement);
            this.allowed(elementName);

            ElementDefinition currentDef = new ElementDefinition();
            ElementUtils.build(currentDef, subElement);
            currentDef.setIdentifier(super.getIdentifier(subElement, context));
            currentDef.setExecute(subElement.getAttribute(ParseConstants.EXECUTE));
            currentDef.setContext(context);

            String when = subElement.getAttribute(ParseConstants.WHEN);
            if (Objects.equals(elementName, ParseConstants.CASE)) {
                AssertUtil.notBlank(when, "branch condition is blank");
                caseMap.put(when, currentDef);
            } else {
                chooseDefinition.setDefaultDef(currentDef);
            }

            String type = subElement.getAttribute(ParseConstants.EXECUTE);
            if (AuxiliaryUtils.isNotBlank(type)) {
                super.extraBindingAttrs(subElement, currentDef);
                super.parseExtensionAttributes(subElement, currentDef);
                continue;
            }

            // Wrap sub elements as pipeline.
            ComponentDefinition def = parseSubElements(subElement, context);
            if (elementName.equals(ParseConstants.CASE)) {
                caseMap.put(when, def);
            } else {
                chooseDefinition.setDefaultDef(def);
            }
        }

        chooseDefinition.setCaseMap(caseMap);
        super.parseExtensionAttributes(element, chooseDefinition);
        context.register(chooseDefinition);
    }

    @Override
    public String getElementName() {
        return ParseConstants.CHOOSE;
    }
}
