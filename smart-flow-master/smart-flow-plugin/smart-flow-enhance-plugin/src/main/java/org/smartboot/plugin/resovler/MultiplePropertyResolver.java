package org.smartboot.plugin.resovler;

import org.smartboot.flow.core.exception.FlowException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yamikaze
 * @date 2023/6/17 22:13
 * @since 1.1.0
 */
public class MultiplePropertyResolver extends AbstractPropertyResolver {

    private final List<AbstractPropertyResolver> resolvers = new ArrayList<>(4);

    public MultiplePropertyResolver() {

    }

    public MultiplePropertyResolver(List<AbstractPropertyResolver> resolvers) {
        this.resolvers.addAll(resolvers);
    }

    public void addResolver(AbstractPropertyResolver resolver) {
        this.resolvers.add(resolver);
    }

    @Override
    public String getProperty(String name) {
        for (AbstractPropertyResolver resolver : resolvers) {
            try {
                String resolved = resolver.getProperty(name);
                if (resolved != null) {
                    return resolved;
                }
            } catch (Exception ignored) {

            }
        }

        throw new FlowException("getProperty " + name + " missed ");
    }
}
