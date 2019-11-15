/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the LGPL, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jn.sqlhelper.cli.utils;

import com.jn.langx.io.resource.ClassPathResource;
import com.jn.langx.io.resource.Resources;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.jar.Manifests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.core.env.Environment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class ProjectBanner implements Banner {
    private static final Logger logger = LoggerFactory.getLogger(ProjectBanner.class);
    private static final List<String> BANNER = Collects.emptyArrayList();
    private String defaultValue = "Project";

    static {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(Resources.loadClassPathResource("banner").getInputStream()));
            reader.lines().forEach(BANNER::add);
        } catch (IOException ex) {
            logger.warn("Can't find the banner resource: {}", ClassPathResource.PREFIX + "banner");
        } finally {
            IOs.close(reader);
        }
    }

    public ProjectBanner() {

    }

    public ProjectBanner(String defaultValue) {
        this.defaultValue = defaultValue;
    }


    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }


    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
        // banner
        out.println();
        if (Emptys.isNotEmpty(BANNER)) {
            for (String line : BANNER) {
                out.println(AnsiOutput.toString(AnsiColor.YELLOW, line));
            }
        } else {
            out.println(AnsiOutput.toString(AnsiColor.YELLOW, defaultValue));
        }

        Manifest manifest = Manifests.loadManifest();
        if (manifest != null) {
            Attributes attributes = manifest.getMainAttributes();
            String majorVersion = attributes.getValue("Build-Major-Version");
            String scmVersion = attributes.getValue("Build-SCM-Version");
            String fixedScmVersion = attributes.getValue("Build-FIXED-SCM-Version");
            if (Strings.isNotBlank(fixedScmVersion) && !fixedScmVersion.contains("fixed_version")) {
                scmVersion = fixedScmVersion;
            }
            String buildTime = attributes.getValue("Build-Timestamp");
            out.println(AnsiOutput.toString(AnsiColor.RED, StringTemplates.formatWithPlaceholder("Version: {} ({})", majorVersion, scmVersion)));
            out.println(AnsiOutput.toString(AnsiColor.RED, StringTemplates.formatWithPlaceholder("Build Time: {}", buildTime)));
        }

    }
}
