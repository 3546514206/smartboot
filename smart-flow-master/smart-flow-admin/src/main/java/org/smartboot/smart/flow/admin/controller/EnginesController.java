package org.smartboot.smart.flow.admin.controller;

import net.sourceforge.plantuml.SourceFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.flow.core.FlowEngine;
import org.smartboot.flow.core.attribute.Attributes;
import org.smartboot.flow.core.exception.FlowException;
import org.smartboot.flow.core.util.AssertUtil;
import org.smartboot.flow.core.util.AuxiliaryUtils;
import org.smartboot.flow.core.visitor.EngineCyclicVisitor;
import org.smartboot.flow.helper.view.PlantumlEngineVisitor;
import org.smartboot.flow.helper.view.XmlEngineVisitor;
import org.smartboot.smart.flow.admin.Result;
import org.smartboot.smart.flow.admin.g6.G6EngineVisitor;
import org.smartboot.smart.flow.admin.g6.G6Result;
import org.smartboot.smart.flow.admin.g6.G6ResultSerializer;
import org.smartboot.smart.flow.admin.g6.MetricsEngineAnalyzer;
import org.smartboot.smart.flow.admin.mapper.EngineHistoryMapper;
import org.smartboot.smart.flow.admin.mapper.EngineMapper;
import org.smartboot.smart.flow.admin.model.EngineConfig;
import org.smartboot.smart.flow.admin.model.EngineHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author qinluo
 * @date 2023-01-30 12:01:42
 * @since 1.0.0
 */
@RestController
@RequestMapping(value = "/api/engines", produces = MediaType.APPLICATION_JSON_VALUE)
@ResponseBody
public class EnginesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnginesController.class);

    @Autowired
    private EngineMapper engineMapper;

    @Autowired
    private EngineHistoryMapper engineHistoryMapper;

    @PostMapping("/save")
    public String save(@RequestBody EngineConfig config) {
        if (AuxiliaryUtils.isBlank(config.getEngineName())) {
            return Result.fail(500, "引擎名称不能为空");
        }

        if (AuxiliaryUtils.isBlank(config.getContent())) {
            return Result.fail(500, "引擎文件内容不能为空");
        }

        try {
            // Does not check, because may use this function slightly content.
            saveInternal(config, true);
        } catch (Exception e) {
            return Result.fail(500, e.getMessage());
        }

        return Result.ok(true);
    }

    private void saveInternal(EngineConfig config, boolean check) {
        if (check) {
            FlowEngine<?, ?> flowEngine = WebUtils.parseValidate(config.getContent(), false);
            AssertUtil.notNull(flowEngine, "Sanity check.");

            XmlEngineVisitor visitor = new XmlEngineVisitor();
            visitor.visit(flowEngine);

            config.setContent(visitor.getContent());
        }

        EngineConfig exist = null;
        if (config.getId() != null && config.getId() > 0) {
            exist = engineMapper.detail(config.getId());
        }

        EngineConfig named = engineMapper.getByName(config.getEngineName());
        if (named != null && !Objects.equals(named.getId(), config.getId())) {
            throw new FlowException("引擎名称不能重复");
        }

        boolean update = exist != null;
        boolean insertHistory = true;
        if (update) {
            // When update, name and content has changed.
            insertHistory = !Objects.equals(exist.getEngineName(), config.getEngineName())
                    || !Objects.equals(exist.getContent(), config.getContent());
        }

        // Update not changed.
        if (!insertHistory) {
            return;
        }

        int affectRows;
        if (update) {
            affectRows = engineMapper.update(config);
        } else {
            affectRows = engineMapper.insert(config);
        }

        if (affectRows == 0) {
            throw new FlowException("保存失败");
        }

        EngineConfig now = engineMapper.detail(config.getId());
        EngineHistory history = new EngineHistory();
        history.setEngineName(config.getEngineName());
        history.setContent(config.getContent());
        history.setVersion(now.getVersion());
        history.setEngineId(now.getId());
        engineHistoryMapper.insert(history);
    }

    @RequestMapping("/list")
    public String list(@RequestParam(value = "engineName", required = false) String name,
                       @RequestParam(value = "pageNo", required = false, defaultValue = "1") Integer pageNo,
                       @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        long cnt = engineMapper.count(name);
        List<EngineConfig> list = engineMapper.queryConfig(name, (pageNo - 1) * pageSize, pageSize);
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", cnt);
        result.put("pageNo", pageNo);
        result.put("pageSize", pageSize);
        return Result.ok(result);
    }

    @RequestMapping("/history")
    public String history(@RequestParam(value = "id") Long id) {
        return Result.ok(engineHistoryMapper.queryHistory(id));
    }

    @RequestMapping("/detail")
    public String detail(@RequestParam(value = "id") Long id) {
        return Result.ok(engineMapper.detail(id));
    }

    @RequestMapping("/history_detail")
    public String historyDetail(@RequestParam(value = "historyId") Long id) {
        return Result.ok(engineHistoryMapper.detail(id));
    }

    @RequestMapping("/offline")
    public String offline(@RequestParam(value = "id") Long id) {
        return Result.ok(engineMapper.offline(id));
    }

    @RequestMapping("/online")
    public String online(@RequestParam(value = "id") Long id) {
        return Result.ok(engineMapper.online(id));
    }

    @RequestMapping("/validate")
    public String validate(@RequestBody EngineConfig config) {
        String content = config.getContent();
        if (content == null || content.trim().length() == 0) {
            return Result.fail(500, "内容为空");
        }

        try {
            FlowEngine<?, ?> flowEngine = WebUtils.parseValidate(content, false);
            AssertUtil.notNull(flowEngine, "Sanity check.");
            return Result.ok(flowEngine.getName());
        } catch (Exception e) {
            LOGGER.warn("invalid content, content = {}\n", content, e);
            return Result.fail(500, e.getMessage());
        }
    }

    @RequestMapping("/plant_uml_view")
    public String plantUmlView(@RequestBody EngineConfig config) {
        String content = config.getContent();
        if (content == null || content.trim().length() == 0) {
            return Result.fail(500, "内容为空");
        }

        File plantuml = null;
        try {
            FlowEngine<?, ?> engine = WebUtils.parseValidate(content, false);
            AssertUtil.notNull(engine, "Sanity check.");
            engine.validate();

            plantuml = File.createTempFile("plantuml", ".puml");
            PlantumlEngineVisitor visitor = new PlantumlEngineVisitor(plantuml.getAbsolutePath().substring(0, plantuml.getAbsolutePath().lastIndexOf(".")));
            visitor.visit(engine);

            SourceFileReader reader = new SourceFileReader(plantuml, plantuml.getAbsoluteFile().getParentFile(), "UTF-8");
            FileInputStream fis = new FileInputStream(reader.getGeneratedImages().get(0).getPngFile());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int len;

            while ((len = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }

            byte[] bytes = bos.toByteArray();
            List<Byte> values = new ArrayList<>(bytes.length);
            for (byte b : bytes) {
                values.add(b);
            }

            return Result.ok(values);

        } catch (Exception e) {
            LOGGER.warn("invalid content, content = \n{}\n", content, e);
            return Result.fail(500, e.getMessage());
        } finally {
            if (plantuml != null) {
                //noinspection ResultOfMethodCallIgnored
                plantuml.delete();
            }
        }

    }

    @GetMapping("/g6_detail")
    public String g6Detail(@RequestParam("id") Long id) {
        LOGGER.info("detail id = {}", id);

        EngineConfig detail = engineMapper.detail(id);
        if (detail == null) {
            return Result.fail(500, "cannot find detail");
        }

        // Validate and ensure name.
        FlowEngine<?, ?> engine = WebUtils.parseValidate(detail.getContent(), false);
        AssertUtil.notNull(engine, "Sanity check");

        G6EngineVisitor g6Visitor = new G6EngineVisitor();
        engine.accept(new EngineCyclicVisitor(g6Visitor));

        MetricsEngineAnalyzer analyzer = new MetricsEngineAnalyzer("[]");
        g6Visitor.getResult().getNodes().forEach(analyzer::analyze);
        g6Visitor.getResult().getCombos().forEach(analyzer::analyze);
        g6Visitor.getResult().setId(detail.getId());

        return Result.ok(g6Visitor.getResult());
    }

    @RequestMapping("/g6_save")
    public String g6Save(@RequestBody G6Result result) {
        if (AuxiliaryUtils.isBlank(result.getName())) {
            return Result.fail(500, "引擎名称不能为空");
        }

        if (AuxiliaryUtils.isBlank(result.getProcess())) {
            return Result.fail(500, "引擎流水线不能为空");
        }

        if (Objects.equals(result.getProcess(), result.getName())) {
            return Result.fail(500, "引擎名称与流水线名称不能相同");
        }

        if (result.getNodes() == null || result.getNodes().size() == 0
                || result.getEdges() == null || result.getEdges().size() == 0) {
            return Result.fail(500, "编排内容不能为空");
        }

        try {
            String content = new G6ResultSerializer().serialize(result);
            AssertUtil.notBlank(content, "编排内容不能为空");

            EngineConfig config = new EngineConfig();
            config.setId(result.getId());
            config.setEngineName(result.getName());
            config.setContent(content);
            saveInternal(config, false);
        } catch (Exception e) {
            return Result.fail(500, e.getMessage());
        }

        return Result.ok(true);
    }

    @RequestMapping("/standard_attributes")
    public String standardAttributes() {
        List<Object> results = new ArrayList<>();
        Stream.of(Attributes.values()).forEach(p -> {
            if (p.isVisible() && p != Attributes.NAME) {
                Map<String, Object> single = new HashMap<>();
                single.put("name", p.getName());
                single.put("remark", p.getDescription());
                results.add(single);
            }
        });

        return Result.ok(results);
    }
}
