package org.smartboot.plugin.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.flow.core.EngineContext;
import org.smartboot.flow.core.Feature;
import org.smartboot.flow.core.FlowEngine;
import org.smartboot.flow.core.SmartFlowConfiguration;
import org.smartboot.flow.core.parser.DefaultParser;
import org.smartboot.flow.core.util.AuxiliaryUtils;
import org.smartboot.plugin.resovler.DefaultPlaceholderAttributeValueResolver;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author yamikaze
 * @date 2023/6/18 14:15
 * @since 1.0.0
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final String USAGE;

    static {
        String usageTxt = "";
        InputStream stream = Main.class.getResourceAsStream("/usage.txt");
        try {
            if (stream != null) {
                BufferedReader br = new BufferedReader(new InputStreamReader(stream));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                usageTxt = sb.toString();
            }
        } catch (Exception ignored) {

        } finally {
            close(stream);
        }

        USAGE = usageTxt;
    }

    /**
     * java org.smartboot.plugin.bootstrap.Main -f xxxx.yaml
     *
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void main(String[] args) {
        Arguments argument = parse(args);
        if (argument.hasOption(Option.HELP) || !argument.hasOption(Option.FILE)) {
            LOGGER.info("{}", USAGE);
            System.exit(1);
        }

        Argument fileArgument = argument.getArgument(Option.FILE);
        String fileLocation = fileArgument.args.get(0);
        File file = new File(fileLocation);

        SupportedFileType fileType = SupportedFileType.getFileType(fileLocation);
        if (fileType == null) {
            LOGGER.warn("unsupported file type {}", fileLocation);
            System.exit(1);
        }

        if (!file.exists()) {
            LOGGER.warn("could not find file {}", fileLocation);
            System.exit(1);
        }

        FlowEngine engine = null;
        DefaultPlaceholderAttributeValueResolver resolver = new DefaultPlaceholderAttributeValueResolver();

        try {
            InputStream fileStream = Files.newInputStream(file.toPath());
            if (fileType == SupportedFileType.XML) {
                DefaultParser parser = new DefaultParser();

                parser.setAttributeValueResolver(resolver);
                parser.parse(fileStream);

                List<String> engineNames = parser.getEngineNames();
                if (engineNames.size() != 1) {
                    LOGGER.error("find {} engine in file, only allow one", engineNames.size());
                    System.exit(1);
                }

                engine = parser.getEngine(engineNames.get(0));
            } else if (fileType == SupportedFileType.YAML || fileType == SupportedFileType.YAML2) {
                LoaderOptions options = new LoaderOptions();
                Constructor constructor = new Constructor(options);
                constructor.setPropertyUtils(new PropertyUtils() {
                    @Override
                    public Property getProperty(Class<?> type, String name) {
                        // allowed resultId with result-id
                        name = AuxiliaryUtils.transfer2CamelCase(name);
                        // allowed unrecognized properties in yml.
                        setSkipMissingProperties(true);
                        return super.getProperty(type, name);
                    }
                });

                Yaml yaml = new Yaml(constructor);
                YamlConfig cfg = yaml.loadAs(fileStream, YamlConfig.class);
                cfg.setResolver(resolver);
                cfg.validate();
                engine = cfg.assemble();
            }
        } catch (Exception e) {
            LOGGER.error("failed to parse file {}", fileLocation, e);
            System.exit(1);
        }

        if (engine == null) {
            LOGGER.error("failed to parse file {}", fileLocation);
            System.exit(1);
        }

        try {
            if (argument.hasOption(Option.INVOKE_TREE)) {
                SmartFlowConfiguration.config(Feature.RecordTrace, Feature.values());
            }

            EngineContext ctx = new EngineContext();
            engine.execute(ctx);

            System.exit(0);
        } catch (Exception e) {
            LOGGER.error("failed to execute engine {}", engine.getName(), e);
        }
    }

    public static void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Throwable ignored) {

        }
    }

    private static Arguments parse(String[] args) {
        Arguments argument = new Arguments();
        for (int i = 0; i < args.length; i++) {
            String value = args[i];
            Option option = Option.findOption(value);
            if (option == null) {
                LOGGER.error("unrecognized option {}", value);
                Argument parsed = new Argument();
                parsed.option = Option.HELP;
                argument.arguments.add(parsed);
                break;
            }

            Argument parsed = new Argument();
            parsed.option = option;
            argument.arguments.add(parsed);

            if (option.getArgCnt() > 0) {
                if (i + option.getArgCnt() >= args.length) {
                    LOGGER.error("option {} need {} arg, but found {}", value, option.getArgCnt(), args.length - i - 1);
                    parsed.option = Option.HELP;
                    break;
                }

                parsed.args.addAll(Arrays.asList(args).subList(1 + i, option.getArgCnt() + (i) + 1));

                i += option.getArgCnt();
            }
        }

        return argument;
    }

    static class Arguments {
        private final List<Argument> arguments = new ArrayList<>(4);

        public boolean hasOption(Option option) {
            return arguments.stream().anyMatch(p -> p.option == option);
        }

        public Argument getArgument(Option option) {
            return arguments.stream().filter(p -> p.option == option).findFirst().orElse(null);
        }
    }

    static class Argument {
        private Option option;
        private final List<String> args = new ArrayList<>(4);
    }
}
