package com.jn.langx.pipeline;

public class TailHandlerContext extends HandlerContext {
    public TailHandlerContext() {
        super(NoopHandler.getInstance());
    }

    public TailHandlerContext(Handler handler) {
        super(handler);
    }

    @Override
    public boolean hasNext() {
        return false;
    }
}
