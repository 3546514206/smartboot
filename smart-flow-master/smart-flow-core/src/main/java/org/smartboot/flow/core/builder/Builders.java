package org.smartboot.flow.core.builder;

/**
 * Builder Auxiliary Util.
 *
 * @author qinluo
 * @date 2022-11-11 14:54:34
 * @since 1.0.9
 */
public final class Builders {

    public static <T, S> ChooseBuilder<T, S> newChoose() {
        return new ChooseBuilder<>();
    }

    public static <T, S, P, Q> AdapterBuilder<T, S, P, Q> newAdapter() {
        return new AdapterBuilder<>();
    }

    public static <T, S> IfComponentBuilder<T, S> newIf() {
        return new IfComponentBuilder<>();
    }

    public static <T, S> EngineBuilder<T, S> engine() {
        return new EngineBuilder<>();
    }

    public static <T, S> PipelineBuilder<T, S> pipeline() {
        return new PipelineBuilder<>();
    }

    public static <T, S> ScriptBuilder<T, S> script() {
        return new ScriptBuilder<>();
    }

    public static <T, S> ScriptExecutableBuilder<T, S> scriptExecutable() {
        return new ScriptExecutableBuilder<>();
    }

    public static <T, S> ExecutableBuilder<T, S> executable() {
        return new ExecutableBuilder<>();
    }

    public static <T, S> PipelineComponentBuilder<T, S> pipelineRef() {
        return new PipelineComponentBuilder<>();
    }
}
