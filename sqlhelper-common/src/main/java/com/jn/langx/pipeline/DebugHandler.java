package com.jn.langx.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebugHandler extends AbstractHandler {
    private static final Logger logger = LoggerFactory.getLogger(DebugHandler.class);

    @Override
    public void inbound(HandlerContext ctx) {
        if (logger.isDebugEnabled()) {
            logger.debug("inbounding, context: {}", ctx.toString());
        }
        super.inbound(ctx);
    }

    @Override
    public void outbound(HandlerContext ctx) {
        if (logger.isDebugEnabled()) {
            logger.debug("outbounding, context: {}", ctx.toString());
        }
        super.outbound(ctx);
    }
}
