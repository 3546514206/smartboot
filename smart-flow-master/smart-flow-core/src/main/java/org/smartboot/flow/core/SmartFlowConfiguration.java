package org.smartboot.flow.core;

/**
 * @author qinluo
 * @date 2023-04-12 20:54:04
 * @since 1.0.9
 */
public class SmartFlowConfiguration {

    private static final SmartFlowConfiguration GLOBAL = new SmartFlowConfiguration();

    /**
     * 配置mask
     */
    private volatile int _mask;

    public boolean isConfigured(int mask) {
        return (_mask & mask) == mask;
    }

    public boolean isConfigured(Feature feature, Feature ...next) {
        int mask = feature.mask;
        if (next != null) {
            for (Feature n : next) {
                mask |= n.mask;
            }
        }

        return (_mask & mask) == mask;
    }

    synchronized void setMask(int mask) {
        _mask |= mask;
    }

    synchronized void setMask(Feature feature, Feature ...next) {
        int mask = feature.mask;
        if (next != null) {
            for (Feature n : next) {
                mask |= n.mask;
            }
        }

        _mask |= mask;
    }

    public static void config(int mask) {
        GLOBAL.setMask(mask);
    }

    public static void config(Feature feature, Feature ...next) {
        GLOBAL.setMask(feature, next);
    }

    public static SmartFlowConfiguration newCfg() {
        SmartFlowConfiguration lastest = new SmartFlowConfiguration();
        lastest._mask = GLOBAL._mask;
        return lastest;
    }
}
