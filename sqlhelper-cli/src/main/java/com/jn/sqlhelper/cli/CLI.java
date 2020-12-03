package com.jn.sqlhelper.cli;

import ch.qos.logback.classic.util.ContextInitializer;
import com.jn.langx.io.resource.ClassPathResource;
import com.jn.langx.io.resource.FileResource;
import com.jn.langx.io.resource.Resources;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.io.file.Files;
import com.jn.langx.util.os.Platform;
import com.jn.langx.util.reflect.Reflects;
import com.jn.sqlhelper.cli.utils.ProjectBanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@SpringBootApplication
public class CLI {

    private static final String HOME_DIR_KEY = "SQLHelper-CLI.location";
    private static final String RUN_MODE_KEY = "runMode";
    private static Logger logger;

    private static final List<String> configLocations = Collects.emptyArrayList();

    public static void main(String[] args) {
        // run mode
        boolean devMode = Boolean.parseBoolean(System.getProperty("dev", "false"));
        System.clearProperty("dev");
        String runMode = devMode ? "dev" : "production";
        System.setProperty(RUN_MODE_KEY, runMode);

        // homeDir
        String applicationDefaultConfigPath = Reflects.getCodeLocation(CLI.class).getPath();
        String homeDirDefault = "./..";
        if (devMode) {
            homeDirDefault = new File(applicationDefaultConfigPath).getPath();
        }
        String homeDir = System.getProperty(HOME_DIR_KEY, homeDirDefault);
        homeDir = new File(homeDir).getAbsolutePath();
        System.setProperty(HOME_DIR_KEY, homeDir);

        // pid
        recordPid(homeDir);

        Files.makeDirs(homeDir + File.separator + "logs");

        // custom logback.xml
        if (hasCustomLogbackXml(homeDir + File.separator + "config")) {
            System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, new File(homeDir + File.separator + "config/logback.xml").getAbsolutePath());
        } else if (hasCustomLogbackXml(homeDir)) {
            System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, new File(homeDir + File.separator + "logback.xml").getAbsolutePath());
        }
        logger = LoggerFactory.getLogger(CLI.class);

        // spring.profiles.active, spring.config.location
        String customConfigDir = FileResource.PREFIX + new File(homeDir).getPath() + "/config/";
        String configDirInJar = ClassPathResource.PREFIX + "./";
        configLocations.add(customConfigDir);
        configLocations.add(configDirInJar);

        final List<String> activeProfiles = Collects.emptyArrayList();
        activeProfiles.add("sqlhelper-cli");
        if (devMode) {
            activeProfiles.add("dev");
        }
        String specifiedProfiles = System.getProperty("spring.profiles.active");
        if (specifiedProfiles != null) {
            Pipeline.of(Strings.split(specifiedProfiles, ",")).forEach(new Consumer<String>() {
                @Override
                public void accept(String s) {
                    if (Strings.isNotBlank(s)) {
                        activeProfiles.add(s.trim());
                    }
                }
            });
        }
        System.setProperty("spring.profiles.active", Strings.join(",", activeProfiles));
        System.setProperty("spring.config.location", Strings.join(",", configLocations));

        // startup ...
        final SpringApplication app = new SpringApplication(CLI.class);
        app.setBanner(new ProjectBanner());
        app.setBannerMode(Banner.Mode.LOG);
        final ApplicationContext context = app.run(args);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                SpringApplication.exit(context);
            }
        });
    }

    private static void recordPid(String homeDir) {
        String pid = Platform.processId;
        File pidFile = new File(homeDir + File.separator + "bin/pid");
        FileWriter fw = null;
        try {
            if (!pidFile.getParentFile().exists()) {
                pidFile.getParentFile().mkdirs();
            }
            if (!pidFile.exists()) {
                pidFile.createNewFile();
            }
            fw = new FileWriter(pidFile, false);
            IOs.write(pid, fw);
        } catch (IOException e) {
            logger.error("failed to write pid: {}", pid);
        } finally {
            IOs.close(fw);
        }
    }

    private static boolean hasCustomLogbackXml(String dir) {
        FileResource fileResource = Resources.loadFileResource(dir + File.separator + "logback.xml");
        return fileResource.exists();
    }


}
