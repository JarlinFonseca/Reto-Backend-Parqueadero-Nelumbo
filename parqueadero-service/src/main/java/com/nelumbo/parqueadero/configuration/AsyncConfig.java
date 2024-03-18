package com.nelumbo.parqueadero.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

import java.util.concurrent.Executor;


@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2); // Número de hilos en el pool
        executor.setMaxPoolSize(2); // Máximo número de hilos en el pool
        executor.setQueueCapacity(100); // Capacidad de la cola de tareas
       // executor.setTaskDecorator(new SecurityContextHolderAwareCallableProcessingInterceptor());
        executor.initialize();
        return executor;
    }

    @Bean
    public DelegatingSecurityContextAsyncTaskExecutor taskExecutor(ThreadPoolTaskExecutor delegate) {
        return new DelegatingSecurityContextAsyncTaskExecutor(delegate);
    }
}
