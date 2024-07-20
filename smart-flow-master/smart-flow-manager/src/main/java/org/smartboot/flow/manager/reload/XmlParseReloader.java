package org.smartboot.flow.manager.reload;

import org.smartboot.flow.core.FlowEngine;
import org.smartboot.flow.core.exception.FlowException;
import org.smartboot.flow.core.parser.DefaultObjectCreator;
import org.smartboot.flow.core.parser.DefaultParser;
import org.smartboot.flow.core.parser.DefinitionVisitor;
import org.smartboot.flow.core.parser.ObjectCreator;
import org.smartboot.flow.core.parser.ParserContext;
import org.smartboot.flow.core.util.AssertUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author qinluo
 * @date 2022-12-21 14:11:11
 * @since 1.0.0
 */
public class XmlParseReloader extends AbstractReloader {

    protected ObjectCreator objectCreator = DefaultObjectCreator.getInstance();
    protected boolean assemble;
    protected DefinitionVisitor visitor;
    private XmlSelector xmlSelector;
    private final XmlSelector memoryXmlSelector = MemoryXmlSelector.INSTANCE;

    public void setXmlSelector(XmlSelector xmlSelector) {
        this.xmlSelector = xmlSelector;
    }

    public void setObjectCreator(ObjectCreator objectCreator) {
        this.objectCreator = objectCreator;
    }

    public void setAssemble(boolean assemble) {
        this.assemble = assemble;
    }

    public void setVisitor(DefinitionVisitor visitor) {
        this.visitor = visitor;
    }

    @Override
    public void doReload(String engineName) {
        XmlSelector selector = xmlSelector;
        if (selector == null) {
            selector = memoryXmlSelector;
        }

        AssertUtil.notNull(selector, "selector must not be null!");

        // select config.
        String xml = selector.select(engineName);
        if (xml == null || xml.trim().length() == 0) {
            xml = memoryXmlSelector.select(engineName);
        }

        if (xml == null || xml.trim().length() == 0) {
            throw new FlowException("load config " + engineName + " is empty");
        }

        InputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));

        // init parser.
        DefaultParser parser = new DefaultParser();
        // object creator include independent and spring ref independent.
        parser.setObjectCreator(objectCreator);
        parser.setAssemble(assemble);

        // parse
        parser.parse(stream);

        // if assemble
        if (assemble) {
            List<String> engineNames = parser.getEngineNames();
            engineNames.forEach(p -> {
                FlowEngine<?, ?> engine = parser.getEngine(p);
                if (engine != null) {
                    engine.validate();
                }
            });
        } else if (visitor != null){
            // not assemble, register to spring.
            ParserContext ctx = parser.getCtx();
            visitor.init(ctx);
            ctx.getRegistered().forEach(p -> visitor.visit(p));
        }
    }
}
