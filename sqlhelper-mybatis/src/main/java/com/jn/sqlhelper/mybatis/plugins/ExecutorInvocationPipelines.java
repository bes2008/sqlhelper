package com.jn.sqlhelper.mybatis.plugins;

import com.jn.langx.pipeline.HandlerContext;
import com.jn.langx.pipeline.Pipeline;
import org.apache.ibatis.plugin.Invocation;

public class ExecutorInvocationPipelines {
    public static void skipPipeline(HandlerContext ctx) throws Throwable {
        ExecutorInvocation executorInvocation = (ExecutorInvocation) ctx.getPipeline().getTarget();
        Invocation invocation = executorInvocation.getInvocation();
        executorInvocation.setResult(invocation.proceed());
        ctx.getPipeline().outbound();
    }
}
