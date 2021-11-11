package com.jn.sqlhelper.examples.client.sqlline.tests;

import com.jn.langx.io.resource.Resource;
import com.jn.langx.io.resource.Resources;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.text.properties.Props;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.io.Charsets;
import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlScript;
import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlScriptParser;
import com.jn.sqlhelper.common.sql.sqlscript.PlainSqlStatement;
import com.jn.sqlhelper.dialect.Dialect;
import com.jn.sqlhelper.dialect.DialectRegistry;
import org.junit.Test;
import sqlline.SqlLine;

import java.net.URL;
import java.util.List;

public class SqlScriptTests {
    @Test
    public void testWithSqlLine() throws Throwable{
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

    @Test
    public void sqlScriptParseTests(){
        sqlScriptParseTests("d:/tmp/bpm_smdb.sql");
    }
    public void sqlScriptParseTests(String location){
        Resource resource = Resources.loadFileResource(location);
        PlainSqlScript sqlScript = new PlainSqlScript( resource, Charsets.UTF_8.name());

        Dialect dialect = DialectRegistry.getInstance().getDialectByName("mysql");

        PlainSqlScriptParser parser = dialect.getPlainSqlScriptParser();
        List<PlainSqlStatement> sqls = parser.parse(sqlScript);
        Collects.forEach(sqls, new Consumer<PlainSqlStatement>() {
            @Override
            public void accept(PlainSqlStatement plainSqlStatement) {
                System.out.println(plainSqlStatement.getSql());
                System.out.println("============");
            }
        });

    }

    @Test
    public void dialectTests(){
        showDatabaseId("jdbc:h2://localhost:3306/mysql");
        showDatabaseId("H2Database");
        showDatabaseId("com.h2.Driver");

        showDatabaseId("jdbc:mysql://localhost:3306/mydb");

        showDatabaseId("jdbc:sybase:Tds:<host>:<port>?ServiceName=<database_name>");
        showDatabaseId("com.jn.sqlhelper.dialect.internal.SybaseDialect");

        showDatabaseId("jdbc:postgresql://<host>:<port>/<database_name>");
    }

    private void showDatabaseId(String str){
        System.out.println(DialectRegistry.guessDatabaseId(str));
    }



}
