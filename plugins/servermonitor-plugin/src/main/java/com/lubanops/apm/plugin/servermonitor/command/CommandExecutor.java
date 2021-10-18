/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.lubanops.apm.plugin.servermonitor.command;

import com.lubanops.apm.plugin.servermonitor.common.Consumer;
import com.lubanops.apm.plugin.servermonitor.common.Function;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Linux指令执行器
 *
 * <p>通过调用{@link #execute(MonitorCommand)}方法来执行Linux指令，该方法将
 * 调用{@link Runtime#exec(String)}方法调用外部进程来执行{@link MonitorCommand#getCommand()}
 * 方法提供的Linux指令，并通过{@link MonitorCommand#parseResult(InputStream)}和
 * {@link MonitorCommand#handleError(InputStream)}来处理外部进程的输出流和错误流，
 * 外部进程的输出流的处理结果作为方法的结果。</p>
 *
 * <p>重构泛PaaS：com.huawei.apm.plugin.collection.util.RunLinuxCommand</p>
 */
public class CommandExecutor {

    private static final Runtime RUNTIME = Runtime.getRuntime();

    private static final ExecutorService POOL = new ThreadPoolExecutor(2, 2,
        0, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());

    //private static final int EXEC_TIMEOUT_SECONDS = 60;

    public static <T> T execute(final MonitorCommand<T> command) {
        final Process process;
        try {
            process = RUNTIME.exec(command.getCommand());
        } catch (IOException e) {
            // TODO LOGGER.error("command exec failed.")
            // JDK6 用不了Optional
            return null;
        }
        handleErrorStream(command, process.getErrorStream());
        Future<T> parseFuture = parseResult(command, process.getInputStream());

        try {
            // JDK6 无法超时等待
            // process.waitFor(EXEC_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            process.waitFor();
            return parseFuture.get();
            // LOGGER.warn("timeout.")
            // Should destroy the subprocess when timout? JDK6 也用不了
            // process.destroyForcibly();
        } catch (InterruptedException e) {
            // Ignored.
        } catch (ExecutionException e) {
            // TODO LOG
        }
        return null;
    }

    private static <T> Future<T> parseResult(final MonitorCommand<T> command, final InputStream inputStream) {
        return POOL.submit(new InputHandleTask<T>(new Function<InputStream, T>() {
            @Override
            public T apply(InputStream inputStream) {
                return command.parseResult(inputStream);
            }
        }, inputStream));
    }

    private static <T> void handleErrorStream(final MonitorCommand<T> command, final InputStream errorStream) {
        POOL.execute(new ErrorHandleTask(new Consumer<InputStream>() {
            @Override
            public void accept(InputStream inputStream) {
                command.handleError(inputStream);
            }
        }, errorStream));
    }

    private static class InputHandleTask<T> implements Callable<T> {

        private final Function<InputStream, T> function;

        private final InputStream inputStream;

        public InputHandleTask(Function<InputStream, T> function, InputStream inputStream) {
            this.function = function;
            this.inputStream = inputStream;
        }

        @Override
        public T call() {
            try {
                return function.apply(inputStream);
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    //ignored
                }
            }
        }
    }

    private static class ErrorHandleTask implements Runnable {

        private final Consumer<InputStream> consumer;

        private final InputStream inputStream;

        public ErrorHandleTask(Consumer<InputStream> consumer, InputStream inputStream) {
            this.consumer = consumer;
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            try {
                consumer.accept(inputStream);
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    //ignored
                }
            }
        }
    }

}
