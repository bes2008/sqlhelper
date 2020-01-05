package com.jn.langx.pipeline;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;

import java.util.List;

public class Pipelines {

    public static DefaultPipeline newPipeline(List<Handler> handlers) {
        DefaultPipeline pipeline = new DefaultPipeline();
        addHandlers(pipeline, handlers);
        return pipeline;
    }


    public static DefaultPipeline newPipeline(Handler headHandler, Handler tailHandler, List<Handler> handlers) {
        DefaultPipeline pipeline = new DefaultPipeline(headHandler, tailHandler);
        addHandlers(pipeline, handlers);
        return pipeline;
    }

    public static void addHandlers(final Pipeline pipeline, List<Handler> handlers) {
        Collects.forEach(handlers, new Consumer<Handler>() {
            @Override
            public void accept(Handler handler) {
                pipeline.addLast(handler);
            }
        });
    }

    public static void skipHandler(HandlerContext ctx, boolean inbound) throws Throwable {
        ctx.setSkiped(true);
        if (inbound) {
            if (ctx.hasNext()) {
                ctx.getNext().inbound();
            }
        } else {
            if (ctx.hasPrev()) {
                ctx.getPrev().outbound();
            }
        }
    }

    public static void inbound(HandlerContext ctx) throws Throwable {
        if (ctx.hasNext()) {
            ctx.getNext().inbound();
        }
    }

    public static void outbound(HandlerContext ctx) throws Throwable {
        if (ctx.hasPrev()) {
            ctx.getPrev().outbound();
        }
    }

    public static void outbound(Pipeline pipeline) throws Throwable {
        pipeline.outbound();
    }

    public static void interruptPipeline(Pipeline pipeline) throws Throwable {
        outbound(pipeline);
    }
}
