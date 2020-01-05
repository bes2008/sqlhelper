package com.jn.sqlhelper.mybatis.plugins;

import com.jn.langx.pipeline.AbstractHandler;
import com.jn.langx.pipeline.HandlerContext;
import com.jn.langx.util.Objects;
import org.apache.ibatis.plugin.Invocation;

public class ExecutorInvocationSinkHandler extends AbstractHandler {
    @Override
    public void inbound(HandlerContext ctx) throws Throwable {
        ExecutorInvocation executorInvocation = (ExecutorInvocation) ctx.getPipeline().getTarget();
        Invocation invocation = executorInvocation.getInvocation();
        String method = executorInvocation.getMethodName();
        if (!method.equals("query")) {
            executorInvocation.setResult(invocation.proceed());
        } else {
            if (executorInvocation.isBoundSqlChanged() && Objects.isNotNull(executorInvocation.getBoundSql())) {
                executorInvocation.setResult(executorInvocation.getExecutor().query(executorInvocation.getMappedStatement(), executorInvocation.getParameter(), executorInvocation.getRowBounds(), executorInvocation.getResultHandler(), executorInvocation.getCacheKey(), executorInvocation.getBoundSql()));
            } else {
                executorInvocation.setResult(invocation.proceed());
            }
        }
    }
}
