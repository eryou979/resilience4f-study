<h1>resilience4j-study</h1>

[官方地址](https://resilience4j.readme.io/)

# 说明

Resilience4j 是一个轻量级的容错库，专为函数式编程而设计。Resilience4j 提供高阶函数（装饰器），以使用断路器、速率限制器、重试或隔板来增强任何功能接口、lambda 表达式或方法引用。可以在任何函数接口、lambda 表达式或方法引用上堆叠多个装饰器。

# 依赖

resilience4j 基于 aop 实现；actuator 可以监控 CircuitBreaker 、RateLimiter 的状态；

```xml
<!-- resilience4j -->
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot2</artifactId>
</dependency>
<!-- aop -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
<!-- actuator -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

# yml

```yaml
server:
  port: 8001
  servlet:
    context-path: /

spring:
  application:
    name: resilience4j-study
  profiles:
    active: '@profileActive@'

  jackson:
    default-property-inclusion: NON_NULL
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: UTC
    serialization:
      fail-on-empty-beans: false
      order-map-entries-by-keys: true
    deserialization:
      fail-on-unknown-properties: false

management:
  health:
    circuitbreakers:
      enabled: true
    ratelimiters:
      enabled: true
  endpoints:
    web:
      base-path: /actuator
      exposure:
        include: "*"
  endpoint:
    health:
      show-details: always
```

# CircuitBreaker 

CircuitBreaker 通过有限状态机实现，该状态机具有三种正常状态：CLOSED、OPEN 和 HALF_OPEN 以及两种特殊状态 DISABLED 和 FORCED_OPEN。

![426](1-picture/39cdd54-state_machine.jpg)

CircuitBreaker 使用滑动窗口来存储和聚合调用结果。有两种计算模式：基于计数的滑动窗口、基于时间的滑动窗口。基于计数的滑动窗口聚合了最后 N 次调用的结果。基于时间的滑动窗口聚合了最后 N 秒的调用结果。

## 基于计数

基于计数的滑动窗口是通过 N 个测量值的循环数组实现的。
如果计数窗口大小为 10，则圆形数组始终具有 10 个测量值。
滑动窗口以增量方式更新总聚合。记录新的请求结果时，将更新总聚合。当最早的度量被逐出时，将从总聚合中减去该度量值，并重置存储桶。

## 基于时间

基于时间的滑动窗口是通过 N 个部分聚合（存储桶）的循环数组实现的。
如果时间窗口大小为 10 秒，则循环数组始终具有 10 个部分聚合（存储桶）。每个存储桶都聚合了在单位秒内发生的所有请求的结果。循环数组的头桶存储当前秒的请求结果，其他部分聚合存储前几秒的请求结果。
滑动窗口不会单独存储请求结果，而是以增量方式更新部分聚合和总聚合。
记录新的请求结果时，总聚合会以增量方式更新。逐出最早的存储桶时，将从总聚合中减去该存储桶的部分总聚合，并重置存储桶。

```yaml
resilience4j:
  circuitbreaker: # 断路器配置
    configs:
      default:
        register-health-indicator: true # 注册健康状态到 actuator
        sliding-window-size: 10 # 滑动窗口大小, 用于记录请求结果. 默认 100
        sliding-window-type: count_based # 滑动窗口类型 count_based(基于计数)/time_based(基于时间, 单位 s). 默认 count_based
        failure-rate-threshold: 50 # 故障率阈值, 单位 %. 默认 50
        permitted-number-of-calls-in-half-open-state: 5 # 允许 half_open 状态请求次数. 默认 10
        automatic-transition-from-open-to-half-open-enabled: true # 自动从 open 状态转到 half_open 状态. 默认 false
        wait-duration-in-open-state: 1s # open 状态持续时间, 时间到了会转到 half_open 状态. 默认单位 ms,  60000
        max-wait-duration-in-half-open-state: 6s # half_open 状态持续时间, 时间到了还没转为 closed 状态, 就会转到 open 状态. 默认单位 ms, 0, 表示无限等待
        record-exceptions: # 记录失败的异常集合
          - java.lang.RuntimeException
        ignore-exceptions: # 忽略失败的异常集合
    instances:
      service1:
        base-config: default
        minimum-number-of-calls: 5 # 每个滑动窗口周期至少记录数. 默认 100
```

```java
@GetMapping("circuit-breaker")
@CircuitBreaker(name = "service1", fallbackMethod = "circuitBreakerFallBack")
public CommonResult<?> circuitBreaker() {
    throw new BaseException(ResultCode.ERROR);
}

private CommonResult<?> circuitBreakerFallBack(CallNotPermittedException e) {
    log.error("circuitBreakerFallBack", e);
    throw new BaseException(ResultCode.SYSTEM_BUSY);
}
```

初始调用

```json
{
	"traceId": "1d6344eadd87ced1",
	"code": "error",
	"message": "请求失败"
}
```

多次调用，触发断路，调用 circuitBreakerFallBack 方法

```json
{
	"traceId": "b935dc51a7deb672",
	"code": "system_busy",
	"message": "服务器繁忙, 请稍后再试",
	"error": "服务器繁忙, 请稍后再试"
}
```

# Bulkhead

```yaml
resilience4j:
  bulkhead: # 信号量舱壁
    configs:
      default:
        max-concurrent-calls: 1 # 最大并发量. 默认 25
        max-wait-duration: 1s # 并发饱和时, 线程等待时间. 默认单位 ms, 0, 表示不等待
    instances:
      service1:
        base-config: default
```

```java
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
```

# RateLimiter

```yaml
resilience4j:
  ratelimiter: # 限流器
    configs:
      default:
        register-health-indicator: true # 注册健康状态到 actuator
        timeout-duration: 1s # 当速率满时, 线程等待时间
        limit-refresh-period: 10s # 刷新周期
        limit-for-period: 1 # 周期可用权限数
    instances:
      service1:
        base-config: default
```

```java
@GetMapping("rateLimiter")
@RateLimiter(name = "service1",fallbackMethod = "rateLimiterFallBack")
public CommonResult<?> rateLimiter()  {
    return CommonResult.success();
}

private CommonResult<?> rateLimiterFallBack(RequestNotPermitted e) {
    log.error("fallback", e);
    throw new BaseException(ResultCode.SYSTEM_BUSY);
}
```

# Retry

```yaml
resilience4j:
  retry: # 重试
    configs:
      default:
        max-attempts: 3 # 最大尝试次数(包括初次), 重试完后还是错误, 就会调用 fallbackMethod
        wait-duration: 1s # 重试之间的等待时间
        retry-exceptions:
```

```java
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
```
