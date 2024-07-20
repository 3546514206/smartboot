package org.smartboot.flow.helper.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author qinluo
 * @date 2023-04-06 17:27:57
 * @since 1.0.0
 */
public class PipelineCollector {

    private static final ThreadLocal<PipelineCollector> HOLDER = new ThreadLocal<>();

    /**
     * script-name : script-content : script-type
     */
    private final Map<String, XmlPipelineVisitor> refPipelines = new HashMap<>();
    private final Set<String> processed = new HashSet<>(8);

    public static void start() {
        HOLDER.set(new PipelineCollector());
    }

    public static void processed(String pipeline) {
        PipelineCollector collector = HOLDER.get();
        if (collector != null) {
            collector.processed.add(pipeline);
        }
    }

    public static void collect(String pipeline, XmlPipelineVisitor visitor) {
        PipelineCollector collector = HOLDER.get();
        if (collector != null) {
            collector.refPipelines.putIfAbsent(pipeline, visitor);
        }
    }

    public static List<XmlPipelineVisitor> getUnprocessed() {
        PipelineCollector collector = HOLDER.get();
        if (collector == null) {
            return Collections.emptyList();
        }

        List<XmlPipelineVisitor> visitors = new ArrayList<>();
        collector.refPipelines.forEach((k, v) -> {
            if (!collector.processed.contains(k)) {
                visitors.add(v);
            }
        });

        return visitors;
    }

}
