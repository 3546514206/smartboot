package org.smartboot.plugin.bootstrap;

/**
 * @author yamikaze
 * @date 2023/6/18 14:19
 * @since 1.0.0
 */
public enum SupportedFileType {

    XML(".xml"),
    YAML(".yml"),
    YAML2(".yaml"),
    ;

    private final String suffix;

    SupportedFileType(String suffix) {
        this.suffix = suffix;
    }

    public String getSuffix() {
        return suffix;
    }

    public boolean accept(String name) {
        return name != null && name.endsWith(suffix);
    }

    public static SupportedFileType getFileType(String filename) {
        for (SupportedFileType fileType : values()) {
            if (fileType.accept(filename)) {
                return fileType;
            }
        }

        return null;
    }
}
