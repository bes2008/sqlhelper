package com.jn.langx.pipeline;

import com.jn.langx.annotation.Singleton;

@Singleton
public interface Handler {
    void inbound(HandlerContext ctx) throws Throwable;

    void outbound(HandlerContext ctx) throws Throwable;
}
