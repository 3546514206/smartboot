package org.smartboot.smart.flow.admin.g6;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.smartboot.flow.manager.metric.NamedMetrics;

import java.util.List;
import java.util.Objects;

/**
 * @author qinluo
 * @date 2023/2/18 0:49
 * @since 1.1.1
 */
public class ManagementViewEngineAnalyzer extends MetricsEngineAnalyzer {


    public ManagementViewEngineAnalyzer(List<String> contents) {
        super("[]");
        for (String content : contents) {
            JSONArray jsonArray = JSONObject.parseArray(content);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                String name = item.getString("name");
                JSONObject exist = metricsMap.get(name);
                metricsMap.put(name, merge(item, exist));
            }
        }

    }

    private JSONObject merge(JSONObject current, JSONObject exist) {
        if (exist == null) {
            return current;
        }

        JSONObject currentMetrics = current.getJSONObject("metrics");
        JSONObject existMetrics = exist.getJSONObject("metrics");

        if (currentMetrics == null) {
            return exist;
        }

        currentMetrics.forEach((k, v) -> {
            Object value = existMetrics.get(k);
            if (value == null) {
                existMetrics.put(k, v);
            } else if (Objects.equals(k, "started")){
                existMetrics.put(k, v);
            } else if (Objects.equals(k, NamedMetrics.MAX_ESCAPE) || Objects.equals(k, NamedMetrics.ROLLBACK_MAX_ESCAPE)){
                long v1 = currentMetrics.getLongValue(k);
                long v2 = existMetrics.getLongValue(k);
                existMetrics.put(k, Math.max(v1, v2));
            } else {
                long v1 = currentMetrics.getLongValue(k);
                long v2 = existMetrics.getLongValue(k);
                existMetrics.put(k, v1 + v2);
            }

        });
        exist.put("metrics", existMetrics);
        return exist;
    }
}
