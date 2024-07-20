package org.smartboot.flow.core.parser;

import org.smartboot.flow.core.FlowEngine;
import org.smartboot.flow.core.attribute.AttributeValueResolver;
import org.smartboot.flow.core.parser.definition.FlowDefinition;
import org.smartboot.flow.core.util.AssertUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qinluo
 * @date 2022-11-15 23:20:15
 * @since 1.0.0
 */
public class DefaultParser implements Parser {

    private final static List<String> ALLOWED = new ArrayList<>(8);
    static {
        ALLOWED.add(ParseConstants.ENGINE);
        ALLOWED.add(ParseConstants.PIPELINE);
        ALLOWED.add(ParseConstants.SCRIPT);
        ALLOWED.add(ParseConstants.SCRIPT_LOADER);
    }

    private BuilderDefinitionVisitor visitor;
    private final ParserContext context = new ParserContext();
    private ObjectCreator objectCreator = DefaultObjectCreator.getInstance();
    private AttributeValueResolver resolver = null;
    private boolean assemble = true;
    private final ScriptLoader scriptLoader = new ScriptLoader();

    public ScriptLoader scriptLoader() {
        return this.scriptLoader;
    }

    @Override
    public void parse(InputStream is, InputStream... streams) {
        AssertUtil.notNull(is, "Stream must not be null");
        List<InputStream> willParsedStreams = new ArrayList<>();
        willParsedStreams.add(is);

        if (streams != null) {
            for (InputStream stream : streams) {
                AssertUtil.notNull(stream, "Stream must not be null");
                willParsedStreams.add(stream);
            }
        }

        boolean useCache = false;

        for (InputStream stream : willParsedStreams) {
            Element root = readRoot(stream);
            AssertUtil.notNull(root, "Read root element is null");
            AssertUtil.assertEquals(ElementUtils.getName(root), ParseConstants.ENGINES, "Root element must be engines");

            List<Element> elements = ElementUtils.subElements(root);
            AssertUtil.isTrue(elements.size() != 0, "[engines] element's sub elements must not be empty");

            for (Element sub : elements) {
                String subName = ElementUtils.getName(sub);
                AssertUtil.isTrue(isAllowed(subName), "element " + subName + " not allowed in engines");
                ElementParser parser = context.getParser(subName);
                AssertUtil.notNull(parser, "Could not find parser for element " + subName);
                parser.parseElement(sub, context);
            }

            useCache = useCache || Boolean.parseBoolean(root.getAttribute("useCache"));
        }

        // Load script inside xml.
        context.getScriptLoaders().forEach(p -> p.load(context));

        // Load script outside xml.
        scriptLoader.load(context);

        if (assemble) {
            this.visitor = new BuilderDefinitionVisitor(useCache, objectCreator, resolver != null ? resolver : new AttributeValueResolver(objectCreator));
            context.getRegistered().forEach(FlowDefinition::validate);
            context.getRegistered().forEach(p -> visitor.visit(p));
        }
    }

    @Override
    public void parse(String f, String... files) {
        AssertUtil.notBlank(f, "filename must not be null!");
        if (files != null) {
            for (String file : files) {
                AssertUtil.notBlank(file, "filename must not be null!");
            }
        }

        InputStream fstream = this.getClass().getResourceAsStream(f);
        AssertUtil.notNull(fstream, "filename " + f + " not exist");

        InputStream[] streams = new InputStream[0];
        int index = 0;
        if (files != null && files.length != 0) {
            streams = new InputStream[files.length];
            for (String file : files) {
                InputStream stream = this.getClass().getResourceAsStream(f);
                AssertUtil.notNull(stream, "filename " + file + " not exist");
                streams[index++] = stream;
            }
        }

        this.parse(fstream, streams);
    }

    protected boolean isAllowed(String name) {
        return ALLOWED.contains(name);
    }

    public void setObjectCreator(ObjectCreator objectCreator) {
        this.objectCreator = objectCreator;
    }

    public void setAttributeValueResolver(AttributeValueResolver resolver) {
        this.resolver = resolver;
    }

    public ParserContext getCtx() {
        return context;
    }

    public void setAssemble(boolean assemble) {
        this.assemble = assemble;
    }

    public <T, S>FlowEngine<T, S> getEngine(String name) {
        return visitor.getEngine(name);
    }

    public List<String> getEngineNames() {
        return visitor.getEngineNames();
    }

    public <T, S> Map<String, FlowEngine<T, S>> getEngines() {
        List<String> engineNames = visitor.getEngineNames();
        Map<String, FlowEngine<T, S>> engines = new HashMap<>(engineNames.size());
        for (String name : engineNames) {
            engines.put(name, getEngine(name));
        }

        return engines;
    }

    private Element readRoot(InputStream is) {
        try {
            // 1、创建DocumentFactory
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // 2、创建DocumentBuilder
            DocumentBuilder builder = factory.newDocumentBuilder();

            // 3、创建Document文档对象
            Document doc = builder.parse(is);

            // 4、得到文档的根元素
            return doc.getDocumentElement();
        } catch (Exception e) {
            throw new IllegalStateException("Read root element failed", e);
        }
    }


}
