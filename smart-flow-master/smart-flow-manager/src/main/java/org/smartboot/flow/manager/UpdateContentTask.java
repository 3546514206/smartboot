package org.smartboot.flow.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.smartboot.flow.core.manager.EngineManager;
import org.smartboot.flow.core.manager.EngineModel;
import org.smartboot.flow.core.util.AuxiliaryUtils;
import org.smartboot.http.client.HttpClient;
import org.smartboot.http.client.HttpGet;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author qinluo
 * @date 2023-02-07
 * @since 1.0.7
 */
public class UpdateContentTask extends Thread {

    private static volatile boolean started;
    private static final ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("query-thread"));

    private final String host;
    private final int port;

    public UpdateContentTask(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public synchronized static void startTask(String host, int port) {
        if (started) {
            return;
        }

        started = true;
        executorService.schedule(new UpdateContentTask(host, port), 1, TimeUnit.SECONDS);
    }

    @Override
    public void run() {

        try {
            executeQuery();
        } finally {
            executorService.schedule(new UpdateContentTask(host, port), 5, TimeUnit.SECONDS);
        }

    }

    private void executeQuery() {
        EngineManager defaultManager = EngineManager.defaultManager();
        List<String> allEngines = defaultManager.getRegisteredEngineNames();
        StringBuilder sb = new StringBuilder();
        for (String name : allEngines) {
            EngineModel model = defaultManager.getEngineModel(name);
            FlatEngine flatEngine = FlatManager.getInstance().getFlatEngine(model.getSource());
            sb.append(flatEngine.getName()).append("-").append(flatEngine.getMd5()).append(",");

        }

        String value = sb.toString();
        if (AuxiliaryUtils.isBlank(value)) {
            return;
        }

        HttpClient httpClient = new HttpClient(host, port);
        httpClient.configuration().connectTimeout(5000);
        HttpGet httpGet = httpClient.get(ManagerConstants.SNAPSHOT_QUERY);
        httpGet.addQueryParam(ManagerConstants.SNAPSHOT_QUERY_PARAM, value);
        httpGet.onSuccess(response -> {
            JSONObject data = JSONObject.parseObject(response.body());
            Boolean success = data.getBoolean("success");
            if (!success) {
                return;
            }

            List<String> engineNames = JSONArray.parseArray(data.getString("data"), String.class);

            List<String> registeredEngineNames = defaultManager.getRegisteredEngineNames();
            for (String name : registeredEngineNames) {
                EngineModel model = defaultManager.getEngineModel(name);
                FlatManager.getInstance().getFlatEngine(model.getSource()).setReportContent(!engineNames.contains(name));
            }
        }).onFailure(e -> {

        }).done();
    }
}
