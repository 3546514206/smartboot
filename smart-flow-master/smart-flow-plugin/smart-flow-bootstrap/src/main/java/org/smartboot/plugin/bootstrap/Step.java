package org.smartboot.plugin.bootstrap;

import org.smartboot.flow.core.attribute.AttributeValueResolver;
import org.smartboot.flow.core.attribute.Attributes;
import org.smartboot.flow.core.builder.Builders;
import org.smartboot.flow.core.component.Component;
import org.smartboot.flow.core.executable.Executable;
import org.smartboot.flow.core.util.AuxiliaryUtils;
import org.smartboot.plugin.executable.ShellExecutable;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yamikaze
 * @date 2023/6/18 19:23
 * @since 1.0.0
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class Step implements Serializable {

    private static final long serialVersionUID = -6820700691473794707L;

    private String name;
    private String type;
    private List<String> commands;
    private Boolean degradable;
    private Boolean async;
    private Long timeout;
    private String shellType;
    private String workdir;
    private String file;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    public Boolean getDegradable() {
        return degradable;
    }

    public void setDegradable(Boolean degradable) {
        this.degradable = degradable;
    }

    public Boolean getAsync() {
        return async;
    }

    public void setAsync(Boolean async) {
        this.async = async;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public String getShellType() {
        return shellType;
    }

    public void setShellType(String shellType) {
        this.shellType = shellType;
    }

    public String getWorkdir() {
        return workdir;
    }

    public void setWorkdir(String workdir) {
        this.workdir = workdir;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Component assemble(AttributeValueResolver resolver) {
        type = AuxiliaryUtils.isNotBlank(type) ? type : "shell";
        commands = commands.stream().map(p -> resolver.resolve(String.class, p)).collect(Collectors.toList());

        Executable executable = null;

        if (Objects.equals(type, "shell")) {
            ShellExecutable shellExecutable = new ShellExecutable();
            shellExecutable.setName(resolver.resolve(String.class, name));
            shellExecutable.setCommands(commands);
            shellExecutable.setType(resolver.resolve(String.class, shellType));
            shellExecutable.setWorkdir(resolver.resolve(String.class, workdir));
            shellExecutable.setFile(resolver.resolve(String.class, file));
            executable = shellExecutable;
        }

        return Builders.executable().executable(executable)
                .apply(Attributes.NAME, name)
                .apply(Attributes.ASYNC, async != null ? async : false)
                .apply(Attributes.DEGRADABLE, degradable != null ? degradable : false)
                .apply(Attributes.TIMEOUT, timeout != null ? timeout : 0L)
                .build();
    }


}
