package org.example.service1;

import cn.hutool.core.util.RandomUtil;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.example.service1.exception.BaseException;
import org.example.service1.result.CommonResult;
import org.example.service1.result.ResultCode;
import org.example.service1.util.ApplicationContextUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@RestController
@RequestMapping("service1")
@Slf4j
public class Service1Application {

    public static void main(String[] args) {
        ResultCode.verifyDuplicate();
        System.out.println("线程数量:" + Runtime.getRuntime().availableProcessors());
        SpringApplication.run(Service1Application.class, args);
    }

    @GetMapping("circuit-breaker")
    @CircuitBreaker(name = "service1", fallbackMethod = "circuitBreakerFallBack")
    public CommonResult<?> circuitBreaker() {
        System.out.println(1 / 0);
        return CommonResult.success();
    }

    private CommonResult<?> circuitBreakerFallBack(CallNotPermittedException e) {
        log.error("circuitBreakerFallBack", e);
        throw new BaseException(ResultCode.SYSTEM_BUSY);
    }

    @GetMapping("bulkhead")
    @Bulkhead(name = "service1", fallbackMethod = "bulkheadFallBack", type = Bulkhead.Type.SEMAPHORE)
    public CommonResult<?> bulkhead1() throws InterruptedException {
        TimeUnit.DAYS.sleep(1);
        return CommonResult.success();
    }

    private CommonResult<?> bulkheadFallBack(BulkheadFullException e) {
        log.error("fallback", e);
        throw new BaseException(ResultCode.SYSTEM_BUSY);
    }

    @GetMapping("rateLimiter")
    @RateLimiter(name = "service1", fallbackMethod = "rateLimiterFallBack")
    public CommonResult<?> rateLimiter() {
        return CommonResult.success();
    }

    private CommonResult<?> rateLimiterFallBack(RequestNotPermitted e) {
        log.error("fallback", e);
        throw new BaseException(ResultCode.SYSTEM_BUSY);
    }

    @GetMapping("retry")
    @Retry(name = "service1", fallbackMethod = "retryFallBack")
    public CommonResult<?> retry() {
        System.out.println(1 / 0);
        return CommonResult.success();
    }

    private CommonResult<?> retryFallBack(Exception e) {
        log.error("fallback", e);
        throw new BaseException(ResultCode.SYSTEM_BUSY);
    }

}
