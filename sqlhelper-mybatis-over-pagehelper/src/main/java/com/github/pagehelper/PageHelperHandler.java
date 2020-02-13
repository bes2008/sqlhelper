package com.github.pagehelper;

import com.jn.langx.pipeline.AbstractHandler;
import com.jn.langx.pipeline.HandlerContext;
import com.jn.langx.pipeline.Pipelines;
import com.jn.sqlhelper.mybatis.MybatisUtils;
import com.jn.sqlhelper.mybatis.plugins.ExecutorInvocation;

/**
 * after PaginationHandler
 * before sink handler
 */
public class PageHelperHandler extends AbstractHandler {
    @Override
    public void inbound(HandlerContext ctx) throws Throwable {
        ExecutorInvocation executorInvocation = (ExecutorInvocation) ctx.getPipeline().getTarget();
        if (MybatisUtils.isQueryStatement(executorInvocation.getMappedStatement()) && executorInvocation.getMethodName().equals("query")) {
            Page page = PageHelper.getLocalPage();
            if (page != null) {
                executorInvocation.setResult(page);
            }
            PageHelper.clearPage();
        } else {
            Pipelines.skipHandler(ctx, true);
        }
    }
}
