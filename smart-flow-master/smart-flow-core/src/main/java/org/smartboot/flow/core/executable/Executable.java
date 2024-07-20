package org.smartboot.flow.core.executable;


import org.smartboot.flow.core.Describable;
import org.smartboot.flow.core.EngineContext;
import org.smartboot.flow.core.EngineContextHelper;
import org.smartboot.flow.core.Rollback;
import org.smartboot.flow.core.visitor.ExecutableVisitor;

/**
 * 调用层，与业务相关
 *
 * @author qinluo
 * @date 2022-11-12 21:29:01
 * @since 1.0.0
 */
public interface Executable<T, S> extends Rollback<T, S>, Describable {

    /**
     * 业务逻辑执行
     *
     * @param context 执行上下文
     */
    void execute(EngineContext<T, S> context);

    /**
     * Visit executable source.
     *
     * @param visitor visitor.
     */
    default void visit(ExecutableVisitor visitor) {
        visitor.visitSource(this);
    }


    /**
     * Broken current subprocess.
     *
     * @since 1.1.3
     */
    default void broken() {
        EngineContextHelper.broken();
    }

    /**
     * Broken whole process.
     *
     * @since 1.1.3
     */
    default void brokenAll() {
        EngineContextHelper.brokenAll();
    }
}
