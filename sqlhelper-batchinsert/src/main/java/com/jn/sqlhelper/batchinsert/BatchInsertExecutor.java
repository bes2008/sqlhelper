/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the LGPL, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jn.sqlhelper.batchinsert;

import com.jn.langx.util.Dates;
import com.jn.sqlhelper.common.connection.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BatchInsertExecutor {
    private static final Logger logger = LoggerFactory.getLogger(BatchInsertExecutor.class);

    private ConnectionFactory connFactory = null;

    private final Random random = new Random(1000);
    protected Calendar start;
    private final long end;
    private final ExecutorService executor;

    private final List<Future<BatchInsertResult>> futures = new LinkedList<Future<BatchInsertResult>>();

    private BatchInsertTaskFactory taskFactory;

    public BatchInsertExecutor(long start, long end, int concurrency, ConnectionFactory connectionFactory) {
        this.start = Calendar.getInstance();
        this.start.setTimeInMillis(start);
        this.end = end;
        this.connFactory = connectionFactory;
        concurrency = concurrency > 0 ? concurrency : 1;
        executor = Executors.newFixedThreadPool(concurrency);
    }

    public void setConnectionFactory(ConnectionFactory connFactory) {
        this.connFactory = connFactory;
    }

    public void setTaskFactory(BatchInsertTaskFactory taskFactory) {
        this.taskFactory = taskFactory;
    }

    public void setStartTimeAsZero() {
        this.start.set(Calendar.HOUR, 0);
        this.start.set(Calendar.MINUTE, 0);
        this.start.set(Calendar.SECOND, 0);
    }

    protected long nextTime() {
        start.add(Calendar.HOUR, 1);
        return start.getTimeInMillis();
    }

    public void startup() {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        logger.info("startup() insert time: {}", df.format(new Date(System.currentTimeMillis())));
        long time = end;
        while ((time = nextTime()) <= end) {
            logger.info(Dates.format(new Date(time), Dates.yyyy_MM_dd_HH_mm_ss));
            BatchInsertTask task = taskFactory.createTask(df.format(new Date(time)), random.nextInt());
            submitTask(task);
        }
    }

    private void submitTask(BatchInsertTask task) {
        task.setConnFactory(connFactory);
        futures.add(executor.submit(task));
    }

    public void shutdown() throws InterruptedException, ExecutionException {
        try {
            for (int i = 0; i < futures.size(); i++) {
                BatchInsertResult result = futures.get(i).get();
                if (result.getExpectResult() == result.getExpectResult()) {
                    logger.info(result.getTime() + ": success");
                } else {
                    logger.warn(result.getTime() + ": fail");
                }
            }
        } finally {
            if (executor != null && !executor.isShutdown() && !executor.isTerminated()) {
                executor.shutdownNow();
            }
            logger.info("shutdown() insert time: {}", Dates.format(new Date(), Dates.yyyy_MM_dd_HH_mm_ss));
        }
    }

}
