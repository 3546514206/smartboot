package org.smartboot.flow.core.parser;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qinluo
 * @date 2022-11-15 20:05:30
 * @since 1.0.0
 */
public class ElementParserRegistry {

    private static final ElementParserRegistry INSTANCE = new ElementParserRegistry();
    private final Map<String, ElementParser> MAPPINGS = new HashMap<>(32);

    private ElementParserRegistry() {
        this.register("component", new ComponentElementParser());
        this.register("if", new IfElementParser());
        this.register("pipeline", new PipelineElementParser());
        this.register("choose", new ChooseElementParser());
        this.register("engine", new EngineElementParser());
        this.register("script", new ScriptElementParser());
        this.register("adapter", new AdapterElementParser());
        this.register("script-loader", new ScriptLoaderParser());
    }

    public static ElementParserRegistry getInstance() {
        return INSTANCE;
    }

    public ElementParser getParser(String elementName) {
        return MAPPINGS.get(elementName);
    }

    public void register(String elementName, ElementParser parser) {
        this.MAPPINGS.put(elementName, parser);
    }
}
