package org.smartboot.smart.flow.admin.controller;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.flow.core.FlowEngine;
import org.smartboot.flow.core.util.AssertUtil;
import org.smartboot.flow.core.util.AuxiliaryUtils;
import org.smartboot.flow.core.visitor.EngineCyclicVisitor;
import org.smartboot.flow.manager.report.HttpReportModel;
import org.smartboot.flow.manager.trace.TraceReportRequest;
import org.smartboot.smart.flow.admin.Result;
import org.smartboot.smart.flow.admin.g6.G6EngineVisitor;
import org.smartboot.smart.flow.admin.g6.G6Result;
import org.smartboot.smart.flow.admin.g6.MetricsEngineAnalyzer;
import org.smartboot.smart.flow.admin.g6.TraceEngineAnalyzer;
import org.smartboot.smart.flow.admin.mapper.EngineMetricMapper;
import org.smartboot.smart.flow.admin.mapper.EngineSnapshotMapper;
import org.smartboot.smart.flow.admin.mapper.EngineTraceMapper;
import org.smartboot.smart.flow.admin.model.EngineMetrics;
import org.smartboot.smart.flow.admin.model.EngineSnapshot;
import org.smartboot.smart.flow.admin.model.EngineTrace;
import org.smartboot.smart.flow.admin.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qinluo
 * @date 2023-01-30 12:01:42
 * @since 1.0.7
 */
@RestController
@RequestMapping(value = "/api/report", produces = MediaType.APPLICATION_JSON_VALUE)
@ResponseBody
public class ReportController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    private EngineSnapshotMapper snapshotMapper;

    @Autowired
    private EngineMetricMapper metricMapper;

    @Autowired
    private EngineTraceMapper traceMapper;

    @RequestMapping("/query")
    public String query(@RequestParam("engineMd5") String engineMd5) {
        if (AuxiliaryUtils.isBlank(engineMd5)) {
            return Result.fail(500, "not blank");
        }

        List<String> existEngines = new ArrayList<>();

        String[] split = engineMd5.split(",");
        for (String s : split) {
            String[] engineWithMd5 = s.split("-");
            if (engineWithMd5.length != 2) {
                continue;
            }

            // 引擎对应的内容md5已经存在，则不需要进行上报内容，仅上报md5即可。
            if (snapshotMapper.detail(engineWithMd5[0], engineWithMd5[1]) != null) {
                existEngines.add(engineWithMd5[0]);
            }
        }

        return Result.ok(existEngines);
    }

    private void writeSnapshot(String engineName, String md5, String content) {
        try {
            WebUtils.parseValidate(content, false);
        } catch (Exception e) {
            return;
        }

        try {
            EngineSnapshot snapshot = new EngineSnapshot();
            snapshot.setEngineName(engineName);
            snapshot.setMd5(md5);
            snapshot.setContent(content);
            snapshotMapper.insert(snapshot);
        } catch (DuplicateKeyException ignored) {

        }
    }

    @PostMapping("/metrics")
    public String receiveMetrics(@RequestBody HttpReportModel model) {
        LOGGER.info("receive \n {}", JSON.toJSONString(model));
        if (AuxiliaryUtils.isBlank(model.getEngineName()) || AuxiliaryUtils.isBlank(model.getMd5())) {
            return Result.fail(500, "engineName and md5 must not be blank.");
        }

        EngineSnapshot detail = snapshotMapper.detail(model.getEngineName(), model.getMd5());
        if (detail == null) {
            writeSnapshot(model.getEngineName(), model.getMd5(), model.getContent());
            detail = snapshotMapper.detail(model.getEngineName(), model.getMd5());
        }

        if (detail == null) {
            return Result.ok("failed due to invalid structure");
        }

        EngineMetrics metrics = new EngineMetrics();
        metrics.setAddress(model.getAddress());
        metrics.setHost(model.getHost());
        metrics.setReportTime(new Date(model.getTimestamp()));
        metrics.setContent(JSON.toJSONString(model.getJson()));
        metrics.setEngineName(model.getEngineName());
        metrics.setMd5(model.getMd5());
        metricMapper.insert(metrics);

        return Result.ok(metrics.getId());
    }

    @PostMapping("/metrics_list")
    public String metricsList(@RequestBody ReportQuery query) {
        LOGGER.info("query \n {}", JSON.toJSONString(query));
        query.setStart((query.getPageNo()- 1) * query.getPageSize());
        long cnt = metricMapper.count(query);
        List<EngineMetrics> metrics = metricMapper.list(query);
        Map<String, Object> result = new HashMap<>();
        result.put("list", metrics);
        result.put("total", cnt);
        result.put("pageNo", query.getPageNo());
        result.put("pageSize", query.getPageSize());
        return Result.ok(result);
    }

    @GetMapping("/metrics_detail")
    public String metricsDetail(@RequestParam("id") Long id) {
        LOGGER.info("metrics id = {}", id);

        EngineMetrics detail = metricMapper.detail(id);
        if (detail == null) {
            return Result.fail(500, "cannot find metrics");
        }

        EngineSnapshot snapshot = snapshotMapper.detail(detail.getEngineName(), detail.getMd5());

        // Validate and ensure name.
        FlowEngine<?, ?> engine = WebUtils.parseValidate(snapshot.getContent(), false);
        AssertUtil.notNull(engine, "Sanity Check");

        G6EngineVisitor g6Visitor = new G6EngineVisitor();
        engine.accept(new EngineCyclicVisitor(g6Visitor));

        MetricsEngineAnalyzer analyzer = new MetricsEngineAnalyzer(detail.getContent());
        g6Visitor.getResult().getNodes().forEach(analyzer::analyze);
        g6Visitor.getResult().getCombos().forEach(analyzer::analyze);

        return Result.ok(g6Visitor.getResult());
    }


    @PostMapping("/trace_list")
    public String traceList(@RequestBody ReportQuery query) {
        LOGGER.info("query \n {}", JSON.toJSONString(query));
        query.setStart((query.getPageNo()- 1) * query.getPageSize());
        long cnt = traceMapper.count(query);
        List<EngineTrace> traces = traceMapper.list(query);
        Map<String, Object> result = new HashMap<>();
        result.put("list", traces);
        result.put("total", cnt);
        result.put("pageNo", query.getPageNo());
        result.put("pageSize", query.getPageSize());
        traces.forEach(p -> {
            if (p.getMessage() != null && p.getMessage().length() > 512) {
                p.setMessage(p.getMessage().substring(0, 512));
            }
        });
        return Result.ok(result);
    }

    @GetMapping("/trace_detail")
    public String traceDetail(@RequestParam("id") Long id) {
        LOGGER.info("trace id = {}", id);

        EngineTrace trace = traceMapper.detail(id);
        if (trace == null) {
            return Result.fail(500, "cannot find trace");
        }

        EngineSnapshot snapshot = snapshotMapper.detail(trace.getEngineName(), trace.getMd5());

        // Validate and ensure name.
        FlowEngine<?, ?> engine = WebUtils.parseValidate(snapshot.getContent(), false);
        AssertUtil.notNull(engine, "Sanity Check");
        engine.validate();

        G6EngineVisitor g6Visitor = new G6EngineVisitor();
        engine.accept(new EngineCyclicVisitor(g6Visitor));

        G6Result result = g6Visitor.getResult();

        TraceEngineAnalyzer analyzer = new TraceEngineAnalyzer(trace.getContent());
        result.getNodes().forEach(analyzer::analyze);
        result.getCombos().forEach(analyzer::analyze);

        // trace info
        result.setResult(trace.getResult());
        result.setRequest(trace.getRequest());
        result.setTraceTime(DateUtils.format(trace.getTraceTime()));
        result.setEndTime(DateUtils.format(trace.getEndTime()));
        result.setMessage(trace.getMessage());
        result.setHost(trace.getHost());
        result.setAddress(trace.getAddress());
        result.setTraceId(trace.getTraceId());
        result.setMd5(trace.getMd5());

        return Result.ok(result);
    }

    @PostMapping("/trace")
    public String receiveTrace(@RequestBody TraceReportRequest model) {
        LOGGER.info("receive \n {}", JSON.toJSONString(model));
        if (AuxiliaryUtils.isBlank(model.getEngineName()) || AuxiliaryUtils.isBlank(model.getMd5())) {
            return Result.fail(500, "engineName and md5 must not be blank.");
        }

        EngineSnapshot detail = snapshotMapper.detail(model.getEngineName(), model.getMd5());
        if (detail == null) {
            writeSnapshot(model.getEngineName(), model.getMd5(), model.getContent());
            detail = snapshotMapper.detail(model.getEngineName(), model.getMd5());
        }

        if (detail == null) {
            return Result.ok("failed due to invalid structure");
        }

        EngineTrace trace = new EngineTrace();
        trace.setTraceId(model.getTraceId());
        trace.setAddress(model.getAddress());
        trace.setHost(model.getHost());
        trace.setReportTime(new Date(model.getTimestamp()));
        trace.setContent(JSON.toJSONString(model.getJson()));
        trace.setEngineName(model.getEngineName());
        trace.setMd5(model.getMd5());
        trace.setStatus(model.getSuccess() ? 1 : 0);
        trace.setMessage(model.getEx());
        trace.setRequest(model.getRequest());
        trace.setResult(model.getResult());
        trace.setTraceTime(new Date(model.getTraceTime()));
        trace.setEndTime(new Date(model.getEndTime()));
        trace.setEscaped(model.getEndTime() - model.getTraceTime());
        traceMapper.insert(trace);

        return Result.ok(trace.getId());
    }
}
