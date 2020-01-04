package com.jn.langx.pipeline;

import com.jn.langx.annotation.Prototype;
import com.jn.langx.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Prototype
public class DefaultPipeline<T> implements Pipeline<T> {
    private static final Logger logger = LoggerFactory.getLogger(DefaultPipeline.class);
    private HeadHandlerContext head;
    private TailHandlerContext tail;
    private T target;

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
    public void inbound() {
        Preconditions.checkNotNull(target, "target is null");
        getHead().inbound();
    }

    @Override
    public void outbound() {
        Preconditions.checkNotNull(target, "target is null");
        tail.outbound();
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


}
