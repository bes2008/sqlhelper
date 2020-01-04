package com.jn.langx.pipeline;

public class AbstractHandler implements Handler {
    @Override
    public void inbound(HandlerContext ctx) {
        if (ctx.hasNext()) {
            ctx.getNext().inbound();
        }
    }

    @Override
    public void outbound(HandlerContext ctx) {
        if (ctx.hasPrev()) {
            ctx.getPrev().inbound();
        }
    }
}
