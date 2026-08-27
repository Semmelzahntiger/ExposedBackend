package com.semmelzahntiger.brainrotbackend.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
@Slf4j
public class ThreadUtil {
    private static final Map<String, AtomicInteger> THREAD_COUNTER = new ConcurrentHashMap<>();
    public static final ScheduledExecutorService SCHEDULER =
            Executors.newSingleThreadScheduledExecutor();
    private static final ThreadFactory VIRTUAL_THREAD_FACTORY =
            Thread.ofVirtual()
                    .name("worker-", 0)
                    .uncaughtExceptionHandler(
                            (thread, throwable) ->
                                    log.error("Uncaught Exception occurred in Virtual Thread '{}'",
                                            thread.getName(), throwable))
                    .factory();
    public static final ExecutorService THREAD_PER_TASK_EXECUTOR =
            Executors.newThreadPerTaskExecutor(VIRTUAL_THREAD_FACTORY);

    public static ExecutorService getAsyncTaskExecutor() {
        return THREAD_PER_TASK_EXECUTOR;
    }

    public static Future<?> runTaskAsync(Runnable runnable) {
        return getAsyncTaskExecutor().submit(
                        () -> {
                            try {
                                runnable.run();
                            } catch (Throwable e) {
                                log.error("Uncaught Exception occurred in Virtual Thread '{}'",
                                        Thread.currentThread().getName(), e);
                            }
                        });
    }

    public static Future<?> runTaskAsync(Runnable runnable, String threadName) {
        return getAsyncTaskExecutor()
                .submit(
                        () -> {
                            Thread.currentThread().setName(getCountedThreadName(threadName));
                            try {
                                runnable.run();
                            } catch (Throwable e) {
                                log.error("Uncaught Exception occurred in Virtual Thread '{}'",
                                        Thread.currentThread().getName(), e);
                            }
                        });
    }

    private static String getCountedThreadName(String name) {
        int counter = THREAD_COUNTER.computeIfAbsent(name, k -> new AtomicInteger()).getAndIncrement();
        return name + "-" + counter;
    }

    public static Future<?> scheduleTask(Runnable runnable, long delay, TimeUnit unit) {
        return SCHEDULER.schedule(
                () -> {
                    try {
                        runnable.run();
                    } catch (Throwable e) {
                       log.error("Uncaught Exception triggered in scheduled Task");
                    }
                },
                delay,
                unit);
    }

    public static Future<?> scheduleTask(
            Runnable runnable, long delay, TimeUnit unit, String threadName) {
        return SCHEDULER.schedule(
                () -> {
                    try {
                        Thread.currentThread().setName(getCountedThreadName(threadName));
                        runnable.run();
                    } catch (Throwable e) {
                        log.error("Uncaught Exception triggered in scheduled Task");
                    }
                },
                delay, unit);
    }
    public static ScheduledFuture<?> scheduleTaskWithRate(Runnable runnable, long initial, long schedule, TimeUnit unit) {
        return SCHEDULER.scheduleAtFixedRate(
                () -> {
                    try {
                        runnable.run();
                    } catch(Throwable e) {
                        log.error("Uncaught Exception triggered in scheduled Task");
                    }
                },
                initial,
                schedule,
                unit
        );
    }
}

