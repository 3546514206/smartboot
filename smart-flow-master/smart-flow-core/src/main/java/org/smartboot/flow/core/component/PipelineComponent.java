package org.smartboot.flow.core.component;


import org.smartboot.flow.core.EngineContext;
import org.smartboot.flow.core.Pipeline;
import org.smartboot.flow.core.common.ComponentType;
import org.smartboot.flow.core.util.AssertUtil;
import org.smartboot.flow.core.util.AuxiliaryUtils;
import org.smartboot.flow.core.visitor.ComponentVisitor;
import org.smartboot.flow.core.visitor.PipelineVisitor;

/**
 * @author qinluo
 * @date 2022-11-12 21:03:36
 * @since 1.0.0
 */
public class PipelineComponent<T, S> extends Component<T, S> {

    private Pipeline<T, S> pipeline;

    public void setPipeline(Pipeline<T, S> pipeline) {
        this.pipeline = pipeline;
    }

    @Override
    public int invoke(EngineContext<T, S> context) throws Throwable {

        // Create ctx for subprocess;
        EngineContext<T, S> subprocess = context.newContext();


        try {
            pipeline.execute(subprocess);
        } finally {
            subprocess.apply();
        }

        if (subprocess.getRollback() && !AuxiliaryUtils.isAnonymous(pipeline.describe())) {
            subprocess.setExecuting(EngineContext.ROLLBACK);
            this.rollback(subprocess);
        }

        if (subprocess.getFatal() != null) {
            throw subprocess.getFatal();
        }

        return 1;
    }

    @Override
    public boolean isRollbackable(EngineContext<T, S> context) {
        return pipeline.isRollbackable(context);
    }

    @Override
    public void rollback(EngineContext<T, S> context) {
        if (!isRollbackable(context)) {
            return;
        }
        pipeline.rollback(context);
    }

    @Override
    public String describe() {
        return "pipeline@" + pipeline.describe();
    }

    @Override
    public void accept(ComponentVisitor visitor) {
        visitor.visitAttributes(attributes);
        visitor.visitExtensionAttributes(this.get());
        visitor.visitSource(this);
        PipelineVisitor pipelineVisitor = visitor.visitPipeline(pipeline.describe());
        if (pipelineVisitor != null) {
            pipeline.accept(pipelineVisitor);
        }
        visitor.visitEnd();
    }

    @Override
    public void doValidate() {
        AssertUtil.notNull(pipeline, "subprocess[" + getName() + "]pipeline must not be null!");
        pipeline.validate();
    }

    @Override
    public ComponentType getType() {
        return ComponentType.SUBPROCESS;
    }

    @Override
    public void reset() {
        super.reset();
        this.pipeline.reset();
    }
}
