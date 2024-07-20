package org.smartboot.flow.core.script;

import org.smartboot.flow.core.exception.FlowException;
import org.smartboot.flow.core.util.AssertUtil;
import org.smartboot.flow.core.util.AuxiliaryUtils;

import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qinluo
 * @date 2022/11/28 19:41
 * @since 1.0.0
 */
public class ScriptDetector {

    /**
     * The phrase for {@link org.smartboot.flow.core.script.ScriptExecutor} location.
     */
    private static final String LOCATION = "META-INF/smart-flow-script";

    /**
     * The stored phrase for {@link org.smartboot.flow.core.script.ScriptExecutor}
     */
    private final Map<String, Class<?>> javaTypes = new ConcurrentHashMap<>();

    /**
     * The singleton instance for detector.
     */
    private static final ScriptDetector DETECTOR = new ScriptDetector();

    public static ScriptDetector get() {
        return DETECTOR;
    }

    private ScriptDetector() {
        try {
            Enumeration<URL> resources = this.getClass().getClassLoader().getResources(LOCATION);
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();

                // phrase files must be properties.
                Properties properties = new Properties();
                properties.load(url.openStream());

                // key : phrase, value: full java classname.
                Set<String> keys = properties.stringPropertyNames();
                for (String key : keys) {
                    AssertUtil.notBlank(key, "script key must not be blank");
                    String type = properties.getProperty(key);
                    AssertUtil.notBlank(type, "script " + key + " type must not be blank");
                    Class<?> javaType = AuxiliaryUtils.asClass(type);
                    AssertUtil.notNull(javaType, "script " + key + " type must be a javaType");
                    AssertUtil.isTrue(ScriptExecutor.class.isAssignableFrom(javaType), "script " + key + " type must be a subclass of ScriptExecutor");
                    AssertUtil.isTrue(ScriptExecutor.class != (javaType), "script " + key + " type must be a subclass of ScriptExecutor");
                    javaTypes.put(key.toLowerCase(), javaType);
                }
            }
        } catch (Exception e) {
            throw new FlowException(e);
        }

    }

    public Class<?> getJavaType(String type) {
        AssertUtil.notBlank(type, "type must not blank");
        return javaTypes.get(type.trim().toLowerCase());
    }

    public void register(String phrase, Class<?> javaType) {
        AssertUtil.notBlank(phrase, "phrase must not blank");
        AssertUtil.notNull(javaType, "type must be null");
        AssertUtil.isTrue(ScriptExecutor.class.isAssignableFrom(javaType), "type must be a subclass of ScriptExecutor");
        AssertUtil.isTrue(ScriptExecutor.class != (javaType), "type must be a subclass of ScriptExecutor");
        javaTypes.put(phrase, javaType);
    }

    public String getPhrase(Class<?> type) {
        AssertUtil.notNull(type, "type must not null");
        for (Map.Entry<String, Class<?>> entry : javaTypes.entrySet()) {
            if (entry.getValue() == type) {
                return entry.getKey();
            }
        }
        return null;
    }
}
