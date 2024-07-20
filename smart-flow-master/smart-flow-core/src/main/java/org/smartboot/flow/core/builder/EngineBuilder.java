package org.smartboot.flow.core.builder;


import org.smartboot.flow.core.FlowEngine;
import org.smartboot.flow.core.Pipeline;
import org.smartboot.flow.core.exception.ExceptionHandler;
import org.smartboot.flow.core.util.AssertUtil;

import java.util.concurrent.ExecutorService;

/**
 * @author qinluo
 * @date 2022-11-11 11:23:42
 * @since 1.0.0
 */
public class EngineBuilder<T, S> {

    private Pipeline<T, S> pipeline;

    /**
     * 引擎名称
     */
    private String name;

    /**
     * 异步步骤执行线程池，含有异步组件时必须设置
     */
    private ExecutorService executor;

    /**
     * 异常处理器
     */
    private ExceptionHandler handler;

    public EngineBuilder<T, S> pipeline(Pipeline<T, S> pipeline) {
        AssertUtil.notNull(pipeline, "must not be null");
        this.pipeline = pipeline;
        return this;
    }

    public EngineBuilder<T, S> name(String name) {
        AssertUtil.notBlank(name, "name is required");
        this.name = name;
        return this;
    }

    public EngineBuilder<T, S> executor(ExecutorService executor) {
        AssertUtil.notNull(executor, "executor must not be null");
        this.executor = executor;
        return this;
    }

    public EngineBuilder<T, S> handler(ExceptionHandler handler) {
        AssertUtil.notNull(handler, "handler must not be null");
        this.handler = handler;
        return this;
    }

    public FlowEngine<T, S> build() {
        AssertUtil.notBlank(name, "engine's name is required");

        FlowEngine<T, S> engine = new FlowEngine<>();
        engine.setName(name);
        engine.setPipeline(pipeline);
        engine.setExecutor(executor);

        if (handler != null) {
            engine.setExceptionHandler(handler);
        }

        return engine;
    }
}
