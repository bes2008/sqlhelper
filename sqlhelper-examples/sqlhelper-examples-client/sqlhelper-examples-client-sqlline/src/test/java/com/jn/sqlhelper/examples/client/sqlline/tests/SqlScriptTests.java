package com.jn.sqlhelper.examples.client.sqlline.tests;

import com.jn.langx.io.resource.Resources;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.text.properties.Props;
import org.junit.Test;
import sqlline.SqlLine;

import java.net.URL;

public class SqlScriptTests {
    @Test
    public void test() throws Throwable{
        String h2DatabaseUrlTemplate = "jdbc:h2:file:${user.dir}/../../sqlhelper-examples-db/src/main/resources/test";
        String url = StringTemplates.formatWithMap(h2DatabaseUrlTemplate, Props.toStringMap(System.getProperties()));


        URL scriptURL= Resources.loadClassPathResource("./sql_script.sql",SqlScriptTests.class).getUrl();
        String scriptPath= scriptURL.getFile();

        String[] args = new String[]{
                "-u", url,      // url
                "-n", "sa",     // username
                "-p", "123456", // password
                "-d", "org.h2.Driver",   // driver
                "-f",scriptPath,
                "--silent",
                "--maxHeight=80",
                "--maxWidth=80"
        };
        SqlLine.main(args);
    }
}
