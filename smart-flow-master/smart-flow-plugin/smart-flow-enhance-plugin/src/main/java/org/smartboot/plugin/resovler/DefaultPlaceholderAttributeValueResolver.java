package org.smartboot.plugin.resovler;

import org.smartboot.flow.core.attribute.AttributeValueResolver;

/**
 * @author yamikaze
 * @date 2023/6/17 22:20
 * @since 1.1.0
 */
public class DefaultPlaceholderAttributeValueResolver extends AttributeValueResolver {

    private final MultiplePropertyResolver propertyResolver = new MultiplePropertyResolver();
    private final PlaceHolderResolver resolver = new PlaceHolderResolver("${", "}", propertyResolver);

    public DefaultPlaceholderAttributeValueResolver() {
        this.propertyResolver.addResolver(new SystemPropertyResolver());
        this.propertyResolver.addResolver(new SystemEnvResolver());
    }

    public void addPropertyResolver(AbstractPropertyResolver resolver) {
        this.propertyResolver.addResolver(resolver);
    }

    @Override
    protected String earlyResolve(String value) {
        try {
            return resolver.resolve(value);
        } catch (Exception e) {
            return value;
        }
    }
}
