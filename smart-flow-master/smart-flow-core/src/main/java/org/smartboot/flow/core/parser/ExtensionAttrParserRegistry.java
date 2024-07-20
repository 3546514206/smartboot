package org.smartboot.flow.core.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.flow.core.ExecutionListenerRegistry;
import org.smartboot.flow.core.attribute.Attributes;
import org.smartboot.flow.core.util.AssertUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;

/**
 * @author qinluo
 * @date 2023-03-30 20:06:52
 * @since 1.0.0
 */
public class ExtensionAttrParserRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtensionAttrParserRegistry.class);

    /**
     * Registered all parsers.
     */
    private static final List<ExtensionAttrParser> REGISTERED = new ArrayList<>();

    /*
     * Load from SPI location and install.
     */
    static {
        try {
            ServiceLoader<ExtensionAttrParser> loader = ServiceLoader.load(ExtensionAttrParser.class);
            for (ExtensionAttrParser next : loader) {
                next.install();
            }
        } catch (Exception e) {
            LOGGER.error("load ExtensionAttrParser from META-INF failed", e);
        }
    }

    public static void register(ExtensionAttrParser parser) {
        AssertUtil.notNull(parser, "parser must not be null!");
        AssertUtil.assertNull(Attributes.byName(parser.prefix()), "prefix must not same as attribute " + parser.prefix());

        ExtensionAttrParser registeredOne = null;
        for (ExtensionAttrParser registered : REGISTERED) {
            if (Objects.equals(registered.prefix(), parser.prefix())) {
                registeredOne = registered;
                break;
            }
        }

        if (registeredOne == null) {
            REGISTERED.add(parser);
            ExecutionListenerRegistry.register(parser.getListener());
        } else if (registeredOne != parser && registeredOne.allowOverride()) {
            LOGGER.warn("ExtensionAttrParserRegistry.register override, old = {}, new = {}, prefix = {}",
                    registeredOne.id(), parser.id(), parser.prefix());
            REGISTERED.remove(registeredOne);
            REGISTERED.add(parser);
            ExecutionListenerRegistry.register(parser.getListener());
            ExecutionListenerRegistry.unregister(registeredOne.getListener());
        }
    }

    public static void unregister(ExtensionAttrParser parser) {
        AssertUtil.notNull(parser, "parser must not be null!");
        REGISTERED.remove(parser);
        ExecutionListenerRegistry.unregister(parser.getListener());
    }

    public static void unregister(String id) {
        AssertUtil.notNull(id, "parser's id must not be null!");
        ExtensionAttrParser registered = getRegistered(id);
        if (registered == null) {
            return;
        }

        unregister(registered);
    }

    public static ExtensionAttrParser getRegistered(String id) {
        for (ExtensionAttrParser parser : REGISTERED) {
            if (Objects.equals(parser.id(), id)) {
                return parser;
            }
        }

        return null;
    }

    public static List<ExtensionAttrParser> getRegistered() {
        return new ArrayList<>(REGISTERED);
    }
}
