package org.smartboot.flow.core.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.flow.core.parser.definition.ScriptDefinition;
import org.smartboot.flow.core.util.AuxiliaryUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

/**
 * @author qinluo
 * @date 2023/3/11 21:53
 * @since 1.0.8
 */
public class ScriptLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptLoader.class);

    /**
     * Script locations
     */
    private final Set<File> locations = new HashSet<>();

    /**
     * Accept file extensions.
     */
    private final Set<String> acceptExtensions = new HashSet<>();

    /**
     * Exclude file extensions.
     */
    private final Set<String> excludeExtensions = new HashSet<>();

    public ScriptLoader() {
        this.exclude("xml", "properties");
    }

    public void locations(String ...fileLocations) {
        if (fileLocations == null) {
            return;
        }

        for (String fileLocation : fileLocations) {
            if (fileLocation == null) {
                continue;
            }

            File file = new File(fileLocation);
            if (!file.exists()) {
                file = null;
                URL resource = this.getClass().getResource(fileLocation);

                if (resource == null && !fileLocation.startsWith("/")) {
                    resource = this.getClass().getResource("/" + fileLocation);
                }

                if (resource != null) {
                    file = new File(resource.getPath());
                }
            }

            if (file == null || !file.exists() || !file.canRead()) {
                continue;
            }

            locations.add(file);
        }
    }

    public void locations(File ...fileLocations) {
        if (fileLocations == null) {
            return;
        }

        for (File file : fileLocations) {
            if (file == null || !file.exists() || !file.canRead()) {
                continue;
            }

            locations.add(file);
        }
    }

    public void accept(String ...extensions) {
        this.acceptExtensions.addAll(AuxiliaryUtils.asList(extensions));
    }

    public void exclude(String ...extensions) {
        this.excludeExtensions.addAll(AuxiliaryUtils.asList(extensions));
    }

    /**
     * Load additional script from specified locations.
     *
     * @param ctx ctx.
     */
    public void load(ParserContext ctx) {
        // foreach locations
        Set<String> visitedLocations = new HashSet<>(locations.size());
        for (File file : locations) {
            if (file.isFile() && !accept(file)) {
                continue;
            }

            // Found duplicated file in locations.
            if (!visitedLocations.add(file.getAbsolutePath())) {
                LOGGER.info("duplicated location {}", file.getAbsolutePath());
                continue;
            }

            if (file.isFile()) {
                doReadAndRegister(file, ctx);
            } else if (file.isDirectory()){
                doReadDirectory(file, ctx);
            }
        }

        this.clear();
    }

    private void doReadDirectory(File file, ParserContext ctx) {
        File[] files = file.listFiles();
        if (files == null) {
            return;
        }

        for (File f : files) {
            if (f.isFile() && !accept(f)) {
                continue;
            }

            if (f.isFile()) {
                doReadAndRegister(f, ctx);
            } else if (f.isDirectory()){
                doReadDirectory(f, ctx);
            }
        }
    }

    private void doReadAndRegister(File file, ParserContext ctx) {
        String type = getExtension(file);
        String name = file.getName().substring(0, file.getName().lastIndexOf("."));

        // Additional script has lowest priority.
        if (ctx.getRegistered(name) != null) {
            return;
        }

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int n;
            BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(file.toPath()));
            while ((n = bis.read(buffer, 0, buffer.length)) != -1) {
                bos.write(buffer, 0, n);
            }

            String content = new String(bos.toByteArray(), StandardCharsets.UTF_8);
            ScriptDefinition def = new ScriptDefinition();
            def.setScript(content);
            def.setName(name);
            def.setType(type);
            def.setIdentifier(name);
            ctx.register(def);
        } catch (Exception e) {
            LOGGER.error("read file {} failed", file.getPath(), e);
        }

    }

    private void clear() {
        this.locations.clear();
    }

    private boolean accept(File file) {
        String extension = getExtension(file);
        if (AuxiliaryUtils.isBlank(extension)) {
            return false;
        }

        if (excludeExtensions.contains(extension)) {
            return false;
        }

        return acceptExtensions.isEmpty() || acceptExtensions.contains(extension);
    }

    private String getExtension(File file) {
        String name = file.getName();
        int index = name.lastIndexOf(".");
        if (index == -1) {
            return "";
        }

        return name.substring(index + 1);
    }

}
