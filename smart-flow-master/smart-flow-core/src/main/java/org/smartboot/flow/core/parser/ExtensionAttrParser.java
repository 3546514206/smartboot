package org.smartboot.flow.core.parser;

import org.smartboot.flow.core.ExecutionListener;

/**
 * @author qinluo
 * @date 2023-03-30 19:44:24
 * @since 1.0.0
 */
public interface ExtensionAttrParser {

    /**
     * Returns extension attribute prefix.
     * for example: prefix = preexecute
     *
     * <pre>{@code
     *<component execute="initRef" preexecute.env="echo ${java_home}" preexecute.bash="export JAVA_HOME=/Users/jdk8" />
     *}
     * </pre>
     *
     * @return extension attribute group prefix.
     */
    String prefix();

    /**
     * Return attribute acceptor.
     *
     * @return default prefix acceptor.
     */
    default AttributeAcceptor acceptor() {
        return new PrefixAttributeAcceptor(prefix());
    }

    /**
     * Returns the <code>ExecutionListener</code> instance which use parsed
     * extension attributes that stored in component object.
     *
     * @return ExecutionListener
     */
    default ExecutionListener getListener() {
        return ExecutionListener.NOOP;
    }

    /**
     * Allow override when two <code>ExtensionAttrParser</code> has same prefixed-attribute.
     *
     * @return allowed
     */
    default boolean allowOverride() {
        return false;
    }

    /**
     * Returns <code>ExtensionAttrParser</code>'s identifier, default behavior is full-classname.
     *
     * @return identifier.
     */
    default String id() {
        return this.getClass().getName();
    }

    /**
     * Install parser.
     */
    default void install() {
        ExtensionAttrParserRegistry.register(this);
    }

    /**
     * Uninstall parser.
     */
    default void uninstall() {
        ExtensionAttrParserRegistry.unregister(this);
    }
}
