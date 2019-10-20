package com.jn.sqlhelper.common.symbolmapper;

import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Predicate;

import java.util.StringTokenizer;

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

    public static String[] split(@Nullable String string, @Nullable String separator) {
        if (Emptys.isEmpty(string)) {
            return new String[0];
        }

        if (Emptys.isEmpty(separator)) {
            return Pipeline.of(string.split("")).filter(new Predicate<String>() {
                @Override
                public boolean test(String value) {
                    return Strings.isNotBlank(value);
                }
            }).toArray(String[].class);
        }

        StringTokenizer tokenizer = new StringTokenizer(string, separator, false);
        return Pipeline.of(tokenizer).map(new Function<Object, String>() {
            @Override
            public String apply(Object input) {
                return input.toString().trim();
            }
        }).filter(new Predicate<String>() {
            @Override
            public boolean test(String value) {
                return Strings.isNotBlank(value);
            }
        }).toArray(String[].class);
    }
}
