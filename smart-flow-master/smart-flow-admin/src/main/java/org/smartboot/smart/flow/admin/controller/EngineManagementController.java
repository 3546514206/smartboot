package org.smartboot.smart.flow.admin.controller;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.flow.core.FlowEngine;
import org.smartboot.flow.core.util.AssertUtil;
import org.smartboot.flow.core.util.AuxiliaryUtils;
import org.smartboot.flow.core.visitor.EngineCyclicVisitor;
import org.smartboot.smart.flow.admin.Result;
import org.smartboot.smart.flow.admin.g6.G6EngineVisitor;
import org.smartboot.smart.flow.admin.g6.G6Result;
import org.smartboot.smart.flow.admin.g6.ManagementViewEngineAnalyzer;
import org.smartboot.smart.flow.admin.mapper.EngineMetricMapper;
import org.smartboot.smart.flow.admin.mapper.EngineSnapshotMapper;
import org.smartboot.smart.flow.admin.model.EngineMetrics;
import org.smartboot.smart.flow.admin.model.EngineSnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yamikaze
 * @date 2023/6/26 22:12
 * @since 1.1.1
 */
@RestController
@RequestMapping(value = "/api/management/", produces = MediaType.APPLICATION_JSON_VALUE)
@ResponseBody
public class EngineManagementController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EngineManagementController.class);

    @Autowired
    private EngineMetricMapper engineMetricMapper;

    @Autowired
    private EngineSnapshotMapper snapshotMapper;

    @GetMapping("/engines")
    public String managementEngines() {
        ReportQuery query = new ReportQuery();
        query.setStartTime(new Date(System.currentTimeMillis() - 60*60*1000));
        List<EngineMetrics> flatEngines = engineMetricMapper.selectEngines(query);

        List<String> engines = new ArrayList<>(16);
        Map<String, List<String>> engineVersions = new HashMap<>(16);
        Map<String, List<String>> versionHosts = new HashMap<>(16);

        flatEngines.forEach(p -> {
            engines.add(p.getEngineName());
            List<String> versions = engineVersions.computeIfAbsent(p.getEngineName(), k -> new ArrayList<>());
            if (!versions.contains(p.getMd5())) {
                versions.add(p.getMd5());
            }

            List<String> hosts = versionHosts.computeIfAbsent(p.getMd5(), k -> new ArrayList<>());
            if (!hosts.contains(p.getHost())) {
                hosts.add(p.getHost());
            }
        });

        Map<String, Object> engineGroup = new HashMap<>(4);
        engineGroup.put("engines", engines.stream().distinct().collect(Collectors.toList()));
        engineGroup.put("versions", engineVersions);
        engineGroup.put("hosts", versionHosts);

        return Result.ok(engineGroup);
    }

    private <T> List<T> emptyIfNull(List<T> list) {
        return list != null ? list : new ArrayList<>(0);
    }

    @PostMapping("/metrics_view")
    public String metricsList(@RequestBody ReportQuery query) {
        LOGGER.info("query \n {}", JSON.toJSONString(query));
        if (AuxiliaryUtils.isBlank(query.getEngineName()) || AuxiliaryUtils.isBlank(query.getMd5())) {
            return Result.ok(new G6Result());
        }

        query.setStartTime(new Date(System.currentTimeMillis() - 60*60*1000));
        List<String> hosts = emptyIfNull(query.getHosts()).stream().filter(p -> !Objects.equals(p, "-1")).collect(Collectors.toList());
        if (AuxiliaryUtils.isNotBlank(query.getHost()) && !Objects.equals(query.getHost(), "-1")) {
            hosts.add(query.getHost());
        }

        query.setHosts(hosts.size() > 0 ? hosts : null);
        List<EngineMetrics> maxIds = engineMetricMapper.queryLastestRecordIds(query);
        if (maxIds.size() == 0) {
            return Result.ok(new G6Result());
        }

        List<EngineMetrics> engineMetrics = engineMetricMapper.listByIdList(maxIds.stream().map(EngineMetrics::getId).collect(Collectors.toList()));
        EngineSnapshot snapshot = snapshotMapper.detail(query.getEngineName(), query.getMd5());

        // Validate and ensure name.
        FlowEngine<?, ?> engine = WebUtils.parseValidate(snapshot.getContent(), false);
        AssertUtil.notNull(engine, "Sanity Check");

        G6EngineVisitor g6Visitor = new G6EngineVisitor();
        engine.accept(new EngineCyclicVisitor(g6Visitor));

        List<String> contents = engineMetrics.stream().map(EngineMetrics::getContent).collect(Collectors.toList());
        ManagementViewEngineAnalyzer analyzer = new ManagementViewEngineAnalyzer(contents);
        g6Visitor.getResult().getNodes().forEach(analyzer::analyze);
        g6Visitor.getResult().getCombos().forEach(analyzer::analyze);
        return Result.ok(g6Visitor.getResult());
    }
}
