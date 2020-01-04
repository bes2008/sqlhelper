package com.jn.langx.pipeline;

public class AbstractHandler implements Handler {
    @Override
    public void inbound(HandlerContext ctx) throws Throwable  {
        Pipelines.inbound(ctx);
    }

    @Override
    public void outbound(HandlerContext ctx) throws Throwable  {
        Pipelines.outbound(ctx);
    }
}
