package com.lovbe.icharge.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.lang.reflect.Method;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: lovbe0210
 * @Date: 2025/2/11 21:17
 * @Description: 定时任务调度线程池
 */
@Slf4j
@Configuration
public class ScheduleConfig implements SchedulingConfigurer, AsyncConfigurer{

        @Override
        public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
            scheduledTaskRegistrar.setScheduler(setTaskExecutors());
        }

        @Bean("threadPoolTaskScheduler")
        public ThreadPoolTaskScheduler setTaskExecutors() {
            ThreadPoolTaskScheduler executor = new ThreadPoolTaskScheduler();
            executor.setPoolSize(64);
            executor.setAwaitTerminationSeconds(120);
            executor.setWaitForTasksToCompleteOnShutdown(true);
            //配置线程池中的线程的名称前缀
            executor.setThreadNamePrefix("stock-select-");
            // rejection-policy：当pool已经达到max size的时候，如何处理新任务
            // CALLER_RUNS：不在新线程中执行任务，而是由调用者所在的线程来执行
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
            //执行初始化
            executor.initialize();
            return executor;
        }

        @Bean("threadPoolExecutor")
        public ThreadPoolExecutor createThreadPool(){
            ThreadPoolExecutor executor = new ThreadPoolExecutor(2,
                    4,
                    1,
                    TimeUnit.MINUTES,
                    new LinkedBlockingQueue<>(5000),
                    new ThreadPoolExecutor.CallerRunsPolicy());
            return executor;
        }


        /**
         * @description: 异步任务中异常处理
         * @return: org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
         * @author: lovbe0210
         * @date: 2025/2/11 21:19
         */
        @Override
        public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
            return new AsyncUncaughtExceptionHandler() {
                @Override
                public void handleUncaughtException(Throwable ex, Method method, Object... params) {
                    log.error("=========================={}=======================", ex.getMessage(), ex);
                    log.error("exception method:{}", method.getName());
                }
            };
        }
}
