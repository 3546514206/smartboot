package org.smartboot.flow.core.parser;

import org.smartboot.flow.core.DefaultIdentifierManager;
import org.smartboot.flow.core.IdentifierManager;
import org.smartboot.flow.core.parser.definition.FlowDefinition;
import org.smartboot.flow.core.util.AssertUtil;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yamikaze
 * @date 2022/11/13
 */
public class ParserContext {

    /**
     * Allowed sub elements.
     */
    private static final List<String> ALLOWED = new ArrayList<>();
    static {
        ALLOWED.add(ParseConstants.PIPELINE);
        ALLOWED.add(ParseConstants.IF);
        ALLOWED.add(ParseConstants.CHOOSE);
        ALLOWED.add(ParseConstants.COMPONENT);
        ALLOWED.add(ParseConstants.ADAPTER);
    }

    /**
     * ElementParser registry
     */
    private final ElementParserRegistry parserRegistry = ElementParserRegistry.getInstance();

    /** Registered Element definitions */
    private final Map<String, FlowDefinition> registered = new ConcurrentHashMap<>();
    private final Map<Element, String> generatedIdentifiers = new ConcurrentHashMap<>();
    private IdentifierManager identifierManager = new DefaultIdentifierManager();

    /**
     * Script loaders in xml.
     *
     * @since 1.0.8
     */
    private final List<ScriptLoader> scriptLoaders = new ArrayList<>();

    public List<ScriptLoader> getScriptLoaders() {
        return scriptLoaders;
    }

    public void addScriptLoad(ScriptLoader loader) {
        this.scriptLoaders.add(loader);
    }

    public IdentifierManager getIdentifierManager() {
        return identifierManager;
    }

    public void setIdentifierManager(IdentifierManager identifierManager) {
        this.identifierManager = identifierManager;
    }

    public void register(FlowDefinition ed) {
        FlowDefinition elementDefinition = registered.get(ed.getIdentifier());
        AssertUtil.assertNull(elementDefinition, "definition " + ed.getIdentifier() + " already exist");
        ed.setContext(this);
        registered.put(ed.getIdentifier(), ed);
    }

    public FlowDefinition getRegistered(String name) {
        return registered.get(name);
    }

    public List<FlowDefinition> getRegistered() {
        return new ArrayList<>(registered.values());
    }

    /**
     * Delegate methods
     */
    public ElementParser getParser(String name) {
        return parserRegistry.getParser(name);
    }

    public String getIdentifier(Element element) {
        return generatedIdentifiers.get(element);
    }

    public String allocateIdentifier(Element element) {
        String identifier = generatedIdentifiers.get(element);
        String elementName = ElementUtils.getName(element);

        if (identifier == null) {
            identifier = identifierManager.allocate(elementName);
            this.generatedIdentifiers.put(element, identifier);
        }
        return identifier;
    }

    public String allocateIdentifier(String prefix) {
        return identifierManager.allocate(prefix);
    }

    /**
     * 检查子元素是否允许出现
     */
    public boolean isAllowed(String name) {
        return ALLOWED.contains(name);
    }
}
