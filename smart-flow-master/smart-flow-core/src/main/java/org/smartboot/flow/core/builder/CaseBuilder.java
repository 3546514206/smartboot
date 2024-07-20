package org.smartboot.flow.core.builder;

import org.smartboot.flow.core.component.Component;
import org.smartboot.flow.core.executable.Executable;
import org.smartboot.flow.core.executable.ExecutableAdapter;
import org.smartboot.flow.core.util.AssertUtil;

/**
 * @author qinluo
 * @date 2022-11-11 14:54:34
 * @since 1.0.9
 */
public class CaseBuilder<T, S> {

    /**
     * Case condition.
     */
    private final Object when;

    private final ChooseBuilder<T, S> chooseBuilder;

    CaseBuilder(ChooseBuilder<T, S> chooseBuilder, Object when) {
        this.when = when;
        this.chooseBuilder = chooseBuilder;
    }

    public ChooseBuilder<T, S> then(Component<T, S> branch) {
        AssertUtil.notNull(branch, "branch must not be null");
        chooseBuilder.addBranch(when, branch);
        return chooseBuilder;
    }

    public ChooseBuilder<T, S> then(Executable<T, S> branch) {
        AssertUtil.notNull(branch, "branch must not be null");
        ExecutableAdapter<T, S> adapter = new ExecutableAdapter<>();
        adapter.setExecutable(branch);
        return then(adapter);
    }
}
