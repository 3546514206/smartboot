package org.smartboot.smart.flow.admin.g6;

import com.alibaba.fastjson.JSONObject;
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
public class TraceEngineAnalyzer extends EngineAnalyzer {

    private final Map<String, JSONObject> traceDataMap;

    public TraceEngineAnalyzer(String content) {
        this.traceDataMap = new HashMap<>(32);
        List<JSONObject> jsonArray = JSONObject.parseArray(content, JSONObject.class);
        for (JSONObject item : jsonArray) {
            traceDataMap.put(item.getString("name"), item);
        }
    }


    private List<JSONObject> initDefaultPanels() {
        List<JSONObject> panels = new ArrayList<>();

        JSONObject escape = new JSONObject();
        escape.put("title", "执行耗时");
        escape.put("value", '-');
        panels.add(escape);

        JSONObject rollback = new JSONObject();
        rollback.put("title", "回滚耗时");
        rollback.put("value", '-');
        panels.add(rollback);

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

        JSONObject jsonObject = traceDataMap.get(node.getId());

        if (jsonObject != null) {
            jsonObject.remove("name");
            jsonObject.remove("type");
            node.setCustomData(jsonObject);

            long escape = jsonObject.getLongValue("escape");
            boolean failed = jsonObject.containsKey("ex");
            if (failed) {
                node.getCustomStyle().put(G6Constants.COLOR, G6Constants.ERROR);
            } else if (escape > 500) {
                node.getCustomStyle().put(G6Constants.COLOR, G6Constants.WARN);
            }

            JSONObject panel = new JSONObject();
            panel.put("title", "执行耗时");
            panel.put("value", escape + "ms");
            node.getPanels().add(panel);


            JSONObject rollback = new JSONObject();
            rollback.put("title", "回滚耗时");
            rollback.put("value", jsonObject.getLongValue("rollbackStart") == 0 ? "-" :
                    jsonObject.getLongValue("rollbackEnd") - jsonObject.getLongValue("rollbackStart") + "ms");
            node.getPanels().add(rollback);

            // After that,
            processKeys(jsonObject);
        } else {
            // 未执行
            node.getPanels().addAll(initDefaultPanels());
            node.getCustomStyle().put(G6Constants.COLOR, G6Constants.GERY);
            if (node.getType() != null) {
                node.addStyle("fill", "#CCCCCC");
            }
        }
    }

    @Override
    public void analyze(Combo combo) {
        JSONObject jsonObject = traceDataMap.get(combo.getId());

        if (jsonObject != null) {
            jsonObject.remove("name");
            jsonObject.remove("type");
            processKeys(jsonObject);
            combo.setCustomData(jsonObject);
        }
    }

    private void processKeys(JSONObject jsonObject) {
        JSONObject newJson = new JSONObject();

        jsonObject.forEach((k,v) -> {
            if (Objects.equals("escape", k)) {
                newJson.put("执行耗时", v + "ms");
            } else if (Objects.equals("rollbackStart", k)) {
                long l1 = ((Number)v).longValue();
                if (l1 != 0) {
                    newJson.put("回滚开始时间", DateUtils.format(new Date(l1)));
                }
            } else if (Objects.equals("rollbackEnd", k)) {
                long l1 = ((Number)v).longValue();
                if (l1 != 0) {
                    newJson.put("回滚结束时间", DateUtils.format(new Date(l1)));
                }
            } else if (Objects.equals("start", k)) {
                newJson.put("开始执行时间", DateUtils.format(new Date((Long)v)));
            } else {
                newJson.put(k, v);
            }

        });

        if (newJson.size() > 0) {
            newJson.put("flag", 1);
        }

        jsonObject.clear();
        jsonObject.putAll(newJson);

    }
}
