package org.smartboot.flow.manager;

import org.smartboot.flow.core.FlowEngine;
import org.smartboot.flow.helper.view.XmlEngineVisitor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Flat engine manager.
 *
 * @author qinluo
 * @date 2023/1/30 22:43
 * @since 1.0.7
 */
public class FlatManager {

    private static final FlatManager INSTANCE = new FlatManager();
    private final Map<String, FlatEngine> flatEngineMap = new ConcurrentHashMap<>(8);

    public static FlatManager getInstance() {
        return INSTANCE;
    }

    public <T, S> FlatEngine getFlatEngine(FlowEngine<T, S> source) {
        FlatEngine flatEngine = flatEngineMap.get(source.getName());
        if (flatEngine == null || flatEngine.getTimestamp() != source.getStartedAt()) {
            flatEngine = createFlatEngine(source);
            flatEngineMap.put(source.getName(), flatEngine);
        }

        return flatEngine;
    }

    private <T, S> FlatEngine createFlatEngine(FlowEngine<T, S> source) {
        FlatEngine engine = new FlatEngine();
        engine.setTimestamp(source.getStartedAt());
        engine.setName(source.getName());
        engine.setReportContent(true);

        XmlEngineVisitor visitor = new XmlEngineVisitor();
        visitor.compressContent();
        visitor.visit(source);
        String content = visitor.getContent();
        engine.setContent(content);
        engine.setMd5(encryptMd5(content));
        return engine;
    }

    private static String encryptMd5(String content) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md5.digest(content.getBytes(StandardCharsets.UTF_8));
            return new String(Base64.getEncoder().encode(md5Bytes), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }
}
