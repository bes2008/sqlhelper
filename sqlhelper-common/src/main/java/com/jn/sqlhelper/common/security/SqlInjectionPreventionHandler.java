package com.jn.sqlhelper.common.security;

import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.struct.Holder;

import java.util.List;

/**
 * 目前这个做法，太过暴力，不适合将其运用到所有的参数上。
 */
public class SqlInjectionPreventionHandler implements Function<String,String> {
    private final List<String> DEFAULT_REMOVED_SYMBOLS = Collects.asList(
            "--","/*","*/","waitfor delay",
            "#","|", "&", ";", "$", "%", "@", "'", "\"", "<", ">", "(", ")", "+", "\t", "\r", "\f", ",", "\\"

    );



    protected List<String> removedSymbols = null;

    public void setRemovedSymbols(List<String> removedSymbols) {
        this.removedSymbols = removedSymbols;
    }

    public List<String> getRemovedSymbols() {
        return Objs.useValueIfEmpty(removedSymbols, DEFAULT_REMOVED_SYMBOLS);
    }

    @Override
    public String apply(String value) {
        final Holder<String> stringHolder = new Holder<String>(value);
        Collects.forEach(getRemovedSymbols(), new Consumer<String>() {
            @Override
            public void accept(String str) {
                String v = stringHolder.get();
                v = Strings.remove(v, str);
                stringHolder.set(v);
            }
        }, new Predicate<String>() {
            @Override
            public boolean test(String str) {
                return stringHolder.isEmpty();
            }
        });
        return stringHolder.get();
    }

}
