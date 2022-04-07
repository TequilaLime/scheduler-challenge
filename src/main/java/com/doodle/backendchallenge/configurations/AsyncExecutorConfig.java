package com.doodle.backendchallenge.configurations;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

@Slf4j
@EnableAsync
@Configuration
public class AsyncExecutorConfig implements AsyncConfigurer {

  @Value("${com.doogle.backendchallenge.async.threads:25}")
  private Integer nThreads;

  @Override
  public Executor getAsyncExecutor() {
    return Executors.newFixedThreadPool(nThreads);
  }
}
