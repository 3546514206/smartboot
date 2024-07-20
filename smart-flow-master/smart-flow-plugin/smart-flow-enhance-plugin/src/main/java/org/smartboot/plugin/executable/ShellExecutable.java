package org.smartboot.plugin.executable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartboot.flow.core.EngineContext;
import org.smartboot.flow.core.executable.AbstractExecutable;
import org.smartboot.flow.core.util.AuxiliaryUtils;
import org.smartboot.flow.helper.annotated.Key;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * @author yamikaze
 * @date 2023/6/18 13:38
 * @since 1.1.0
 */
public class ShellExecutable <T, S> extends AbstractExecutable<T, S> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShellExecutable.class);

    private String[] commands;
    @Key
    private String name;
    private String file;
    private String workdir;
    private ShellType type;
    private String executeCmd;

    @Override
    public String describe() {
        return "shell@" + name;
    }

    private static String join(String[] args) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String arg : args) {
            if (first) {
                first = false;
            } else {
                sb.append(" && ");
            }

            sb.append(arg);
        }

        return sb.toString();
    }

    @Override
    public void execute(EngineContext<T, S> context) {
        Process exec = null;
        LOGGER.info("===================== start execute shell {} ============================================", name);
        try {
            File wr = null;
            File executeFile = null;
            if (AuxiliaryUtils.isNotBlank(workdir)) {
                wr = new File(workdir);
            }

            if (AuxiliaryUtils.isNotBlank(file)) {
                executeFile = new File(file);
                executeFile = executeFile.exists() ? executeFile : null;
            }

            this.type = this.type != null ? this.type : ShellType.BASH;


            String mode = executeFile != null ? "-f" : "-c";
            String cmd = executeFile != null ? executeFile.getAbsolutePath() : executeCmd;

            String[] cmdArray = new String[] {this.type.getPath(), mode, cmd};
            exec = Runtime.getRuntime().exec(cmdArray, null, wr);

            do {
                BufferedReader br = new BufferedReader(new InputStreamReader(exec.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    LOGGER.info("{}", line);
                }

                br = new BufferedReader(new InputStreamReader(exec.getErrorStream()));
                while ((line = br.readLine()) != null) {
                    LOGGER.info("{}", line);
                }

                OutputStream outstream = exec.getOutputStream();
                InputStream in = System.in;
                byte[] buffer = new byte[4096];
                int len;
                while (in.available() > 0 && (len = in.read(buffer, 0, buffer.length)) != -1) {
                    outstream.write(buffer, 0, len);
                    outstream.flush();
                }
            } while (exec.isAlive());
        } catch (Exception e) {
            LOGGER.error("failed to execute {}, commands {}", name, Arrays.toString(commands), e);
        } finally {
            if (exec != null) {
                exec.destroy();
            }

            LOGGER.info("=====================  end  execute shell {} ============================================", name);
        }

    }

    public String getCommand() {
        return commands[0];
    }

    public void setCommand(String command) {
        this.commands = new String[1];
        commands[0] = command;
        this.executeCmd = join(commands);
    }

    public void setCommands(List<String> commands) {
        commands = commands.stream().filter(AuxiliaryUtils::isNotBlank).collect(Collectors.toList());
        this.commands = new String[commands.size()];
        int index = 0;
        for (String command : commands) {
            this.commands[index++] = command;
        }
        this.executeCmd = join(this.commands);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getWorkdir() {
        return workdir;
    }

    public void setWorkdir(String workdir) {
        this.workdir = workdir;
    }

    public ShellType getType() {
        return type;
    }

    public void setType(String type) {
        if (AuxiliaryUtils.isBlank(type)) {
            return;
        }

        try {
            this.type = ShellType.valueOf(type.toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            this.type = ShellType.BASH;
        }
    }
}
