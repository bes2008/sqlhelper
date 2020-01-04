package com.jn.langx.pipeline;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.annotation.Prototype;
import com.jn.langx.util.Preconditions;

@Prototype
public class HandlerContext {
    @Nullable
    private HandlerContext prev;
    @Nullable
    private HandlerContext next;
    @NonNull
    private Handler handler;

    @NonNull
    private Pipeline pipeline;

    public HandlerContext(Handler handler) {
        Preconditions.checkNotNull(handler);
        this.handler = handler;
    }

    public void setNext(HandlerContext next) {
        this.next = next;
    }

    public void setPrev(HandlerContext prev) {
        this.prev = prev;
    }

    public void inbound() {
        handler.inbound(this);
    }

    public void outbound() {
        handler.outbound(this);
    }

    public boolean hasNext() {
        return next != null;
    }

    public HandlerContext getNext() {
        return next;
    }

    public boolean hasPrev() {
        return prev != null;
    }

    public HandlerContext getPrev() {
        return prev;
    }

    public void clear() {
        this.next = null;
        this.prev = null;
        this.handler = null;
    }

    public Pipeline getPipeline() {
        return this.pipeline;
    }

    public void setPipeline(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    @Override
    public String toString() {
        return "HandlerContext{" +
                "pipeline=" + pipeline +
                ", handler=" + handler +
                '}';
    }
}
