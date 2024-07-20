package org.smartboot.flow.core.parser;

import org.smartboot.flow.core.ExtensionAttribute;
import org.smartboot.flow.core.parser.definition.ComponentDefinition;
import org.smartboot.flow.core.parser.definition.ElementDefinition;
import org.smartboot.flow.core.parser.definition.FlowDefinition;
import org.smartboot.flow.core.parser.definition.PipelineComponentDefinition;
import org.smartboot.flow.core.parser.definition.PipelineDefinition;
import org.smartboot.flow.core.util.AssertUtil;
import org.smartboot.flow.core.util.AuxiliaryUtils;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author qinluo
 * @date 2022/11/16 20:23
 * @since 1.0.0
 */
public abstract class AbstractElementParser implements ElementParser {

    @Override
    public void parseElement(Element element, ParserContext context) {
        AssertUtil.notNull(element, "[" + getElementName() + "] element must not be null!");
        String localName = ElementUtils.getName(element);
        AssertUtil.assertEquals(localName, getElementName(), "element must be [" + getElementName() + "] tag");
        this.doParse(element, context);
    }

    protected void parseExtensionAttributes(Element element, ComponentDefinition edf) {
        List<ExtensionAttrParser> registered = ExtensionAttrParserRegistry.getRegistered();
        for (ExtensionAttrParser parser : registered) {

            List<ElementAttr> elementAttrs = ElementUtils.extraAttributes(element, parser.acceptor());
            if (elementAttrs.size() == 0) {
                continue;
            }

            ExtensionAttribute ea = new ExtensionAttribute();
            ea.setPrefix(parser.prefix());

            elementAttrs.forEach(p -> ea.getValues().put(p.getName(), p.getValue()));
            edf.getAttributeMap().put(parser.prefix(), ea);
        }
    }

    protected String getIdentifier(Element element, ParserContext context) {
        // name
        String name = element.getAttribute(ParseConstants.NAME);
        // nested pipeline maybe no name.
        if (name.trim().length() == 0) {
            name = context.getIdentifier(element);
        }

        if (name == null) {
            name = context.allocateIdentifier(element);
        }

        return name;
    }

    /**
     * Returns supported name.
     */
    public abstract String getElementName();

    /**
     * Parse internal.
     */
    public abstract void doParse(Element element, ParserContext context);

    protected ComponentDefinition parseSubElements(Element element, ParserContext context) {
        List<Element> subs = ElementUtils.subElements(element);
        AssertUtil.isTrue(subs.size() != 0, "[" + ElementUtils.getName(element) + "] childNodes can't be null");

        // 只有一个标签时 优化解析，不使用pipeline进行包装
        if (subs.size() == 1) {
            Element subElement = subs.get(0);
            return parseSubElement(subElement, context);
        }

        // pipeline identifier.
        String pipelineIdentifier = context.allocateIdentifier("anonymous-pipeline");

        // Wrap sub elements as pipeline.
        String identifier = context.allocateIdentifier("anonymous-pipeline-wrapper");
        PipelineComponentDefinition def = new PipelineComponentDefinition();
        def.setIdentifier(identifier);
        def.setPipeline(pipelineIdentifier);
        context.register(def);

        // Wrap as pipeline.
        PipelineDefinition pipelineDef = new PipelineDefinition();
        pipelineDef.setName(pipelineIdentifier);
        pipelineDef.setIdentifier(pipelineIdentifier);

        List<ComponentDefinition> subDefinitions = new ArrayList<>();
        for (Element sub : subs) {
            ComponentDefinition registered = parseSubElement(sub, context);
            this.parseExtensionAttributes(sub, registered);
            subDefinitions.add(registered);
        }

        pipelineDef.setChildren(subDefinitions);
        context.register(pipelineDef);

        return def;
    }

    protected ComponentDefinition parseSubElement(Element sub, ParserContext context) {
        String subName = ElementUtils.getName(sub);
        AssertUtil.isTrue(context.isAllowed(subName), "element " + subName + " not allowed in pipeline");
        ElementParser parser = context.getParser(subName);
        AssertUtil.notNull(parser, "Could not find parser for element " + subName);

        String elementIdentifier = getIdentifier(sub, context);

        // nested subprocess
        if (Objects.equals(subName, ParseConstants.PIPELINE)) {
            PipelineComponentDefinition nestedWrap = new PipelineComponentDefinition();
            String nestedIdentifier = context.allocateIdentifier("anonymous-pipeline-wrapper");
            nestedWrap.setIdentifier(nestedIdentifier);
            nestedWrap.setPipeline(elementIdentifier);
            context.register(nestedWrap);

            // Use nested wrap identifier.
            elementIdentifier = nestedIdentifier;
        }

        parser.parseElement(sub, context);

        // Append sub elements.
        FlowDefinition registered = context.getRegistered(elementIdentifier);
        AssertUtil.notNull(registered, elementIdentifier + " sub elements parse failed.");
        AssertUtil.isTrue(registered instanceof ComponentDefinition, "unaccepted type");
        return (ComponentDefinition) registered;
    }

    protected void extraBindingAttrs(Element element, ElementDefinition definition) {
        Class<?> javaType = AuxiliaryUtils.asClass(definition.getExecute());
        if (javaType == null) {
            javaType = ExecutableTypeDetector.get().getJavaType(definition.getExecute());
        }

        String prefix = ParseConstants.EXECUTE_BINDING;

        List<ElementAttr> elementAttrs = null;
        if (javaType != null) {
            String phrase = ExecutableTypeDetector.get().getPhrase(javaType);
            if (AuxiliaryUtils.isNotBlank(phrase)) {
                prefix = phrase + ".";
                elementAttrs = ElementUtils.extraAttributes(element, prefix);
            }
        }

        if (elementAttrs == null || elementAttrs.size() == 0) {
            prefix = ParseConstants.EXECUTE_BINDING;
            elementAttrs = ElementUtils.extraAttributes(element, prefix);
        }

        definition.setBindingAttrPrefix(prefix);
        definition.getBindingAttrs().addAll(elementAttrs);
    }
}
