package org.smartboot.smart.flow.admin.g6;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.smartboot.flow.manager.metric.NamedMetrics;
import org.smartboot.smart.flow.admin.util.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author qinluo
 * @date 2023/2/18 0:49
 * @since 1.0.0
 */
public class MetricsEngineAnalyzer extends EngineAnalyzer {

    protected final Map<String, JSONObject> metricsMap;

    public MetricsEngineAnalyzer(String content) {
        this.metricsMap = new HashMap<>(32);
        JSONArray jsonArray = JSONObject.parseArray(content);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            metricsMap.put(item.getString("name"), item);
        }
    }

    private List<JSONObject> initDefaultPanels() {
        List<JSONObject> panels = new ArrayList<>();

        JSONObject avgRt = new JSONObject();
        avgRt.put("title", "平均耗时");
        avgRt.put("value", '-');
        panels.add(avgRt);

        JSONObject exeCnt = new JSONObject();
        exeCnt.put("title", "调用次数");
        exeCnt.put("value", '-');
        panels.add(exeCnt);

        JSONObject maxRt = new JSONObject();
        maxRt.put("title", "最大耗时");
        maxRt.put("value", '-');
        panels.add(maxRt);

        return panels;
    }

    @Override
    public void analyze(Node node) {
        if ("#start".equals(node.getId()) || "#end".equals(node.getId())) {
            node.addStyle("fill", "#66FFB3");
            node.setType("rect");
            return;
        }

        node.setTitle(node.getLabel());
        node.setLabel(null);
        node.getCustomStyle().put(G6Constants.COLOR, G6Constants.NORMAL);

        JSONObject jsonObject = metricsMap.get(node.getId());
        jsonObject = jsonObject != null ? jsonObject.getJSONObject("metrics") : null;

        if (jsonObject != null) {
            node.setCustomData(jsonObject);

            long execute = jsonObject.getLongValue("execute");
            execute = execute == 0 ? 1 : execute;

            double avgRt = calculate(jsonObject.getDoubleValue("totalEscape"), execute);
            long failed = jsonObject.getLongValue(NamedMetrics.FAIL);

            if (avgRt > 500 || failed * 1.0 / execute > 0.3) {
                node.getCustomStyle().put(G6Constants.COLOR, G6Constants.WARN);
            }

            JSONObject panel = new JSONObject();
            panel.put("title", "平均耗时");
            panel.put("value", avgRt + "ms");
            node.getPanels().add(panel);

            JSONObject exeCnt = new JSONObject();
            exeCnt.put("title", "调用次数");
            exeCnt.put("value", jsonObject.getLongValue("execute"));
            node.getPanels().add(exeCnt);

            JSONObject maxRt = new JSONObject();
            maxRt.put("title", "最大耗时");
            maxRt.put("value", jsonObject.getLongValue("maxEscape") + "ms");
            node.getPanels().add(maxRt);

            processKeys(jsonObject);
        } else {
            node.getPanels().addAll(initDefaultPanels());
        }
    }

    @Override
    public void analyze(Combo combo) {
        JSONObject jsonObject = metricsMap.get(combo.getId());
        JSONObject metrics = jsonObject != null ? jsonObject.getJSONObject("metrics") : null;
        if (metrics != null) {
            processKeys(metrics);
            combo.setCustomData(metrics);
        }
    }

    private void processKeys(JSONObject jsonObject) {
        JSONObject newJson = new JSONObject();
        jsonObject.remove(NamedMetrics.TOTAL_ESCAPE);
        jsonObject.remove(NamedMetrics.ROLLBACK_TOTAL_ESCAPE);

        jsonObject.forEach((k,v) -> {
            if (Objects.equals(NamedMetrics.MAX_ESCAPE, k)) {
                newJson.put("最大执行耗时", v + "ms");
            } else if (Objects.equals(NamedMetrics.EXECUTE, k)) {
                newJson.put("执行次数", v);
            } else if (Objects.equals(NamedMetrics.ROLLBACK, k)) {
                newJson.put("回滚次数", v);
            } else if (Objects.equals(NamedMetrics.FAIL, k)) {
                newJson.put("执行失败次数", v);
            } else if (Objects.equals(NamedMetrics.TOTAL_ESCAPE, k)) {
                newJson.put("总耗时", v + "ms");
            } else if (Objects.equals(NamedMetrics.ROLLBACK_MAX_ESCAPE, k)) {
                newJson.put("回滚最大耗时", v + "ms");
            } else if (Objects.equals(NamedMetrics.ROLLBACK_TOTAL_ESCAPE, k)) {
                newJson.put("回滚总耗时", v + "ms");
            } else if (Objects.equals("started", k)) {
                newJson.put("started", DateUtils.format(new Date((Long)v)));
            } else {
                newJson.put(k, v);
            }

        });

        if (newJson.size() > 1) {
            newJson.put("flag", 1);
        }
        jsonObject.clear();
        jsonObject.putAll(newJson);

    }
}
