package com.jn.langx.pipeline;

import com.jn.langx.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultPipeline<T> implements Pipeline<T> {
    private static final Logger logger = LoggerFactory.getLogger(DefaultPipeline.class);
    private HeadHandlerContext head;
    private TailHandlerContext tail;
    private T target;
    private HandlerContext current = null;

    public DefaultPipeline() {
        this(new HeadHandlerContext(), new TailHandlerContext());
    }

    public DefaultPipeline(Handler handler) {
        this(handler, handler);
    }

    public DefaultPipeline(Handler headHandler, Handler tailHandler) {
        this(new HeadHandlerContext(headHandler), new TailHandlerContext(tailHandler));
    }

    private DefaultPipeline(HeadHandlerContext head, TailHandlerContext tail) {
        this.head = head;
        this.tail = tail;
        this.head.setNext(tail);
        this.tail.setPrev(head);
        head.setPipeline(this);
        tail.setPipeline(this);
    }

    @Override
    public void addFirst(Handler handler) {
        HandlerContext ctx = new HandlerContext(handler);
        ctx.setPipeline(this);
        HandlerContext first = head.getNext();

        first.setPrev(ctx);
        ctx.setNext(first);

        head.setNext(ctx);
        ctx.setPrev(head);
    }

    @Override
    public void addLast(Handler handler) {
        HandlerContext ctx = new HandlerContext(handler);
        ctx.setPipeline(this);
        HandlerContext last = tail.getPrev();

        last.setNext(ctx);
        ctx.setPrev(last);

        ctx.setNext(tail);
        tail.setPrev(ctx);
    }

    @Override
    public HeadHandlerContext getHead() {
        return head;
    }

    @Override
    public void clear() {
        setCurrentHandlerContext(null);
        HandlerContext ctx = getHead();
        HandlerContext next = null;
        while (ctx.hasNext()) {
            next = ctx.getNext();
            ctx.clear();
            ctx = next;
        }
        if (next != null) {
            next.clear();
        }
        unbindTarget();
    }

    @Override
    public void inbound() throws Throwable {
        Preconditions.checkNotNull(target, "target is null");
        getHead().inbound();
    }

    @Override
    public void outbound() throws Throwable {
        Preconditions.checkNotNull(target, "target is null");
        Preconditions.checkNotNull(current, "current handler context is null");
        current.outbound();
    }

    @Override
    public void bindTarget(T target) {
        this.target = target;
    }

    @Override
    public void unbindTarget() {
        this.target = null;
    }

    @Override
    public T getTarget() {
        return target;
    }

    public void setHeadHandler(Handler headHandler) {
        Preconditions.checkTrue(head != null && !head.isSkiped() && !head.isInbounded() && !head.isOutbounded());
        HeadHandlerContext ctx = new HeadHandlerContext(headHandler);
        ctx.setPipeline(this);

        if (this.head.hasNext()) {
            ctx.setNext(this.head.getNext());
            this.head.getNext().setPrev(ctx);
        }

        this.head = ctx;
    }

    public void setTailHandler(Handler tailHandler) {
        Preconditions.checkNotNull(tail != null && !tail.isSkiped() && !tail.isInbounded() && tail.isOutbounded());
        TailHandlerContext ctx = new TailHandlerContext(tailHandler);
        ctx.setPipeline(this);
        if (this.tail.hasPrev()) {
            this.tail.getPrev().setNext(ctx);
            ctx.setPrev(this.tail);
        }
    }

    @Override
    public boolean hadOutbound() {
        return false;
    }

    @Override
    public HandlerContext getCurrentHandlerContext() {
        return current;
    }

    @Override
    public void setCurrentHandlerContext(HandlerContext handlerContext) {
        this.current = handlerContext;
    }
}
