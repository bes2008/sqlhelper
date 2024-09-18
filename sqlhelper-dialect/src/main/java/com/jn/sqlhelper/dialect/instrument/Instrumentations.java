package com.jn.sqlhelper.dialect.instrument;

import com.jn.langx.annotation.Name;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.reflect.Reflects;

public class Instrumentations {
    public static String getAliasName(Instrumentation instrumentation) {
        Preconditions.checkNotNull(instrumentation);
        Name name = Reflects.getAnnotation(instrumentation.getClass(), Name.class);
        if (name != null) {
            return name.value();
        }
        return null;
    }


}
