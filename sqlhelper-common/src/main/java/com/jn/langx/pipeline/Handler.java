package com.jn.langx.pipeline;

import com.jn.langx.annotation.Singleton;

@Singleton
public interface Handler {
    void inbound(HandlerContext ctx);

    void outbound(HandlerContext ctx);
}
