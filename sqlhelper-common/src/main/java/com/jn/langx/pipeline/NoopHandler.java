package com.jn.langx.pipeline;

import com.jn.langx.annotation.Singleton;

@Singleton
public class NoopHandler extends AbstractHandler {

    private static final NoopHandler instance = new NoopHandler();

    private NoopHandler() {
    }

    public static NoopHandler getInstance() {
        return instance;
    }

    @Override
    public void inbound(HandlerContext ctx) {
        // NOOP
        super.inbound(ctx);
    }

    @Override
    public void outbound(HandlerContext ctx) {
        // NOOP
        super.outbound(ctx);
    }
}
