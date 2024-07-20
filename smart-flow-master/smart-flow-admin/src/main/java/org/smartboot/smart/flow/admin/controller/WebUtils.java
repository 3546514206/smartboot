package org.smartboot.smart.flow.admin.controller;

import org.smartboot.flow.core.FlowEngine;
import org.smartboot.flow.core.exception.FlowException;
import org.smartboot.flow.core.parser.DefaultParser;
import org.smartboot.flow.core.util.AssertUtil;
import org.smartboot.flow.helper.mock.FakeObjectCreator;
import org.smartboot.flow.helper.mock.FakeValueResolver;

import java.io.ByteArrayInputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author qinluo
 * @date 2023-04-06 22:23:59
 * @since 1.0.9
 */
public final class WebUtils {

    public static FlowEngine<?, ?> parseValidate(String content, boolean decodeIfNecessary) {
        DefaultParser parser = new DefaultParser();
        parser.setObjectCreator(new FakeObjectCreator());
        parser.setAttributeValueResolver(new FakeValueResolver());

        try {
            if (decodeIfNecessary) {
                content = URLDecoder.decode(content, "UTF-8");
            }

            parser.parse(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
            List<String> engineNames = parser.getEngineNames();
            AssertUtil.isTrue(engineNames.size() == 1, "只允许存在一个engine");

            FlowEngine<?, ?> engine = parser.getEngine(engineNames.get(0));
            engine.validate();
            return engine;
        } catch (Exception e) {
            handleException(e);
        }

        AssertUtil.shouldNotReachHere();
        return null;
    }

    public static void handleException(Exception e) {
        if (e instanceof FlowException) {
            throw (FlowException)e;
        }

        throw new FlowException(e);
    }
}
