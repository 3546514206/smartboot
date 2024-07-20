package org.smartboot.flow.manager.reload;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qinluo
 * @date 2022-12-21 16:36:25
 * @since 1.1.3
 */
public class MemoryXmlSelector implements XmlSelector {

    private static final Map<String, String> contentMap = new ConcurrentHashMap<>();
    static XmlSelector INSTANCE = new MemoryXmlSelector();

    private MemoryXmlSelector() {

    }

    @Override
    public String select(String engineName) {
        return contentMap.get(engineName);
    }

    /*
      Static methods.
     */
    public static void updateContent(String engine, String content) {
        contentMap.put(engine, content);
    }

    public static String remove(String engine) {
        return contentMap.remove(engine);
    }
}
