package com.jn.sqlhelper.cli.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class DDLDump {

    @ShellMethod("Add two integers together.")
    public int ddldump(String url, String driver, String username, String password, int a, int b) {
        return a + b;
    }
}