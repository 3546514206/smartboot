package org.smartboot.flow.core.parser;

import org.smartboot.flow.core.attribute.AttributeHolder;
import org.smartboot.flow.core.attribute.Attributes;
import org.smartboot.flow.core.parser.definition.ElementDefinition;
import org.smartboot.flow.core.parser.definition.PipelineComponentDefinition;
import org.smartboot.flow.core.util.AssertUtil;
import org.smartboot.flow.core.util.AuxiliaryUtils;
import org.w3c.dom.Element;

/**
 * Parse <code>component</code> element.
 *
 * @author qinluo
 * @date 2022-11-15 13:00:59
 * @since 1.0.0
 */
public class ComponentElementParser extends AbstractElementParser {

    @Override
    public void doParse(Element element, ParserContext context) {
        ElementDefinition elementDefinition = new ElementDefinition();
        ElementUtils.build(elementDefinition, element);
        elementDefinition.setIdentifier(super.getIdentifier(element, context));
        elementDefinition.setExecute(element.getAttribute(ParseConstants.EXECUTE));

        String subprocess = element.getAttribute(ParseConstants.SUBPROCESS);
        if (AuxiliaryUtils.isNotBlank(subprocess)) {
            PipelineComponentDefinition def = new PipelineComponentDefinition();
            ElementUtils.build(def, element);
            def.setIdentifier(super.getIdentifier(element, context));
            def.setPipeline(subprocess);
            // @since 1.0.9, diff nested pipeline tag and ref.
            def.getAttributes().add(AttributeHolder.of(Attributes.REFERENCED_PIPELINE, true));
            super.parseExtensionAttributes(element, def);
            context.register(def);
            return;
        }

        AssertUtil.notBlank(elementDefinition.getExecute(), "attribute [execute] cannot be null");
        Class<?> javaType = ExecutableTypeDetector.get().getJavaType(elementDefinition.getExecute());
        if (javaType != null) {
            elementDefinition.setExecute(javaType.getName());
        }

        super.extraBindingAttrs(element, elementDefinition);
        super.parseExtensionAttributes(element, elementDefinition);
        context.register(elementDefinition);
    }

    @Override
    public String getElementName() {
        return ParseConstants.COMPONENT;
    }
}
