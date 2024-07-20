package org.smartboot.plugin.executable;

/**
 * @author qinluo
 * @date 2023-06-19 10:16:06
 * @since 1.1.0
 */
public enum ShellType {

    BASH("/bin/bash"),
    SH("/bin/sh"),
    ZSH("/bin/zsh"),
    ;

    private final String path;

    ShellType(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
