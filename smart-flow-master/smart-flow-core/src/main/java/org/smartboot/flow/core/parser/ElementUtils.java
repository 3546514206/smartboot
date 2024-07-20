package org.smartboot.flow.core.parser;

import org.smartboot.flow.core.attribute.AttributeHolder;
import org.smartboot.flow.core.attribute.Attributes;
import org.smartboot.flow.core.parser.definition.ComponentDefinition;
import org.smartboot.flow.core.util.AuxiliaryUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qinluo
 * @date 2022-11-15 20:00:48
 * @since 1.0.0
 */
public final class ElementUtils {

    /**
     * Extra all well-known attributes.
     */
    public static List<AttributeHolder> extraAttributes(Element element) {
        List<AttributeHolder> holders = new ArrayList<>();
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            Attributes attribute;
            if ((attribute = Attributes.byName(item.getNodeName())) == null) {
                continue;
            }

            // 跳过空字符串
            if (AuxiliaryUtils.isBlank(item.getNodeValue())) {
                continue;
            }

            holders.add(AttributeHolder.of(attribute, item.getNodeValue()));
        }

        return holders;
    }

    public static List<ElementAttr> extraAttributes(Element element, String prefix) {
        return extraAttributes(element, new PrefixAttributeAcceptor(prefix));
    }

    /**
     * Extra prefix attributes
     */
    public static List<ElementAttr> extraAttributes(Element element, AttributeAcceptor acceptor) {
        List<ElementAttr> holders = new ArrayList<>();
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            // Skip standard attributes.
            if (Attributes.byName(item.getNodeName()) != null
                    || ParseConstants.isBuiltin(item.getNodeName())) {
                continue;
            }

            if (!acceptor.accept(item.getNodeName())) {
                continue;
            }

            // 跳过空字符串
            if (AuxiliaryUtils.isBlank(item.getNodeValue())) {
                continue;
            }

            holders.add(ElementAttr.of(item.getNodeName(), item.getNodeValue()));
        }

        return holders;
    }



    /**
     * Extra all elements.
     */
    public static List<Element> subElements(Element element) {
        NodeList nodes = element.getChildNodes();
        List<Element> subElements = new ArrayList<>(nodes.getLength());

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node instanceof Element) {
                Element subElement = (Element) node;
                subElements.add(subElement);
            }
        }

        return subElements;
    }

    public static String getName(Element element) {
        String localName = element.getLocalName();
        if (localName == null || localName.trim().length() == 0) {
            return element.getNodeName();
        }
        return localName;
    }

    public static void build(ComponentDefinition definition, Element element) {
        definition.setName(AuxiliaryUtils.or(element.getAttribute(ParseConstants.NAME), null));
        // Extra well-known attributes.
        definition.getAttributes().addAll(ElementUtils.extraAttributes(element));
    }
}
