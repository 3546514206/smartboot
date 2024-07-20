package org.smartboot.flow.core.parser.definition;

import org.smartboot.flow.core.executable.ExecutableAdapter;
import org.smartboot.flow.core.parser.DefinitionVisitor;
import org.smartboot.flow.core.parser.ElementAttr;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qinluo
 * @date 2022-11-15 13:00:20
 * @since 1.0.0
 */
public class ElementDefinition extends ComponentDefinition {

    private String execute;

    /**
     * Attributes that starts with {@link org.smartboot.flow.core.parser.ParseConstants#EXECUTE_BINDING}
     *
     * @since 1.1.0
     */
    private final List<ElementAttr> bindingAttrs = new ArrayList<>(0);
    private String bindingAttrPrefix;

    @Override
    public void doVisit(DefinitionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Class<?> resolveType() {
        return ExecutableAdapter.class;
    }

    public String getExecute() {
        return execute;
    }

    public void setExecute(String execute) {
        this.execute = execute;
    }

    public List<ElementAttr> getBindingAttrs() {
        return bindingAttrs;
    }

    public String getBindingAttrPrefix() {
        return bindingAttrPrefix;
    }

    public void setBindingAttrPrefix(String bindingAttrPrefix) {
        this.bindingAttrPrefix = bindingAttrPrefix;
    }
}
