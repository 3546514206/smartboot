package org.smartboot.flow.core.parser;

import org.smartboot.flow.core.FlowEngine;
import org.smartboot.flow.core.attribute.AttributeValueResolver;
import org.smartboot.flow.core.util.AssertUtil;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 更便捷的解析入口
 *
 * <pre>
 * {@code
 *
 * try {
 *     DefaultParser parser = new DefaultParser();
 *     parser.parse(this.getClass().getResourceAsStream("/engine.xml");
 *     FlowEngine<Request, Response> engine = parser.get("testEngine");
 * } catch (Exception e) {
 *
 * }
 * }
 * </pre>
 *
 * 可以简化为如下代码
 * <pre>
 * {@code
 * FlowEngine<Request, Response> engine
 *      = ParseHelper.classpath("engine.xml").get("testEngine");
 *
 * }
 * </pre>
 *
 * 或者(仅有一个引擎的情况下)
 * <pre>
 * {@code
 * FlowEngine<Request, Response> engine
 *      = ParseHelper.classpath("engine.xml").unique();
 *
 * }
 * </pre>
 *
 * 当然也提供方法支持多个配置文件以及自定义解析器
 *
 * @author qinluo
 * @date 2023-10-26 15:00:41
 * @since 1.1.4
 */
public final class ParseHelper {

    private static final int classpath = 1;
    private static final int absolute = 2;
    private static final int relative = 3;

    public static ParseHelperBuilder classpath(String config) {
        return new ParseHelperBuilder(config, classpath);
    }

    public static ParseHelperBuilder absolute(String config) {
        return new ParseHelperBuilder(config, absolute);
    }

    public static ParseHelperBuilder relative(String config) {
        return new ParseHelperBuilder(config, relative);
    }

    static InputStream getResource(String config, int type) {
        try {
            if (type == classpath) {
                return ParseHelper.class.getResourceAsStream(config);
            } else if (type == absolute || type == relative) {
                return new FileInputStream(config);
            }
        } catch (Exception ignored) {

        }

        return null;
    }

    public static class ParseHelperBuilder {
        private final List<InputStream> streams = new ArrayList<>(0);
        private final int type;
        private boolean completed;
        private final DefaultParser parser = new DefaultParser();
        private final InputStream mainStream;

        ParseHelperBuilder(String config, int type) {
            this.type = type;
            AssertUtil.notBlank(config, "config must not be blank");

            InputStream stream = getResource(config, type);
            AssertUtil.notNull(stream, "config " + config + " cannot found.");
            this.mainStream = stream;
        }

        private void addStream(String config, int type) {
            InputStream stream = getResource(config, type);
            AssertUtil.notNull(stream, "config " + config + " cannot found.");
            this.streams.add(stream);
        }

        private void processParse() {
            if (!completed) {
                parser.parse(mainStream, streams.toArray(new InputStream[0]));
            }
            completed = true;
        }

        /* Engine scene. */

        public <T, S> FlowEngine<T, S> unique() {
            processParse();
            AssertUtil.isTrue(parser.getEngines().size() == 1, "Multiple engines.");
            List<String> engineNames = parser.getEngineNames();
            return parser.getEngine(engineNames.get(0));
        }

        public <T, S> FlowEngine<T, S> first() {
            processParse();
            List<String> engineNames = parser.getEngineNames();
            if (engineNames.size() > 0) {
                return parser.getEngine(engineNames.get(0));
            }
            return null;
        }

        public <T, S> FlowEngine<T, S> get(String name) {
            processParse();
            return parser.getEngine(name);
        }


        /* Customized Parser scene methods */
        public ParseHelperBuilder withObjectCreator(ObjectCreator creator) {
            this.parser.setObjectCreator(creator);
            return this;
        }

        public ParseHelperBuilder withResolver(AttributeValueResolver resolver) {
            this.parser.setAttributeValueResolver(resolver);
            return this;
        }

        public ParseHelperBuilder withScriptLocations(String ...locations) {
            this.parser.scriptLoader().locations(locations);
            return this;
        }

        /* Special multiple config locations scene methods. */
        public ParseHelperBuilder addConfig(String config) {
            this.addStream(config, type);
            return this;
        }

        public ParseHelperBuilder addClasspath(String config) {
            this.addStream(config, classpath);
            return this;
        }

        public ParseHelperBuilder addAbsolute(String config) {
            this.addStream(config, absolute);
            return this;
        }

        public ParseHelperBuilder addRelative(String config) {
            this.addStream(config, relative);
            return this;
        }
    }
}
