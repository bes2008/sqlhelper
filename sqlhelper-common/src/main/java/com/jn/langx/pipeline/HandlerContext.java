package com.jn.langx.pipeline;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Preconditions;

public class HandlerContext {
    @Nullable
    private HandlerContext prev;
    @Nullable
    private HandlerContext next;
    @NonNull
    private Handler handler;

    @NonNull
    private Pipeline pipeline;

    private boolean inbounded = false;
    private boolean outbounded = false;
    private boolean skiped = false;

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

    public void inbound() throws Throwable {
        if(isSkiped()){
            Pipelines.skipHandler(this, true);
        }
        getPipeline().setCurrentHandlerContext(this);
        this.inbounded = true;
        handler.inbound(this);
    }

    public void outbound() throws Throwable {
        if (isSkiped()) {
            Pipelines.skipHandler(this, false);
        }
        getPipeline().setCurrentHandlerContext(this);
        this.outbounded = true;
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

    public boolean isInbounded() {
        return inbounded;
    }

    public void setInbounded(boolean inbounded) {
        this.inbounded = inbounded;
    }

    public boolean isOutbounded() {
        return outbounded;
    }

    public void setOutbounded(boolean outbounded) {
        this.outbounded = outbounded;
    }

    public boolean isSkiped() {
        return skiped;
    }

    public void setSkiped(boolean skiped) {
        this.skiped = skiped;
    }

    @Override
    public String toString() {
        return "HandlerContext{" +
                "pipeline=" + pipeline +
                ", handler=" + handler +
                '}';
    }
}
