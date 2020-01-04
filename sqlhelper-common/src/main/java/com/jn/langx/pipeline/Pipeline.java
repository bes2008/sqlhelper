package com.jn.langx.pipeline;

public interface Pipeline<T> {
    void addFirst(Handler handler);

    void addLast(Handler handler);

    HeadHandlerContext getHead();

    void clear();

    void bindTarget(T target);

    void unbindTarget();

    T getTarget();

    void inbound() throws Throwable;

    void outbound() throws Throwable;

    boolean hadOutbound();

    HandlerContext getCurrentHandlerContext();
    void setCurrentHandlerContext(HandlerContext handlerContext);
}
