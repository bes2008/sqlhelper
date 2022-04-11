package com.jn.sqlhelper.common.symbolmapper;

import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Function;

public class UnderlineToCamelSymbolMapper implements SqlSymbolMapper {
    @Override
    public String apply(String sqlSymbol) {
        final StringBuilder builder = new StringBuilder();
        Pipeline.<String>of(Strings.split(sqlSymbol, "_")).map(new Function<String, String>() {
            @Override
            public String apply(String input) {
                return input.toLowerCase();
            }
        }).forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                builder.append(Strings.upperCase(s, 0, 1));
            }
        });
        return Strings.lowerCase(builder.toString(), 0, 1);
    }
}
