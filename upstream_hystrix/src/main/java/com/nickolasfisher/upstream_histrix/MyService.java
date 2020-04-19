package com.nickolasfisher.upstream_histrix;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Component
public class MyService {

    private final RestTemplate shortRestTemplate;
    private final RestTemplate longRestTemplate;

    private static final Logger LOG = LoggerFactory.getLogger(MyService.class);

    public MyService(RestTemplateBuilder builder) {
        this.shortRestTemplate = builder.setReadTimeout(Duration.ofMillis(500))
                .setConnectTimeout(Duration.ofMillis(500))
                .build();

        this.longRestTemplate = builder.setReadTimeout(Duration.ofMillis(2000))
                .setConnectTimeout(Duration.ofMillis(2000))
                .build();
    }

    /*
        Because the underlying rest template has a timeout of 1 second, the configuration of
        "execution.isolation.thread.timeoutInMilliseconds" of "500" appears to do nothing
        to actually stop the request from going through. They all seem to happen inside the same threadpool, which is
        even stranger at first glance.
     */
    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"),
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "40")
//            @HystrixProperty(name = "fallback.isolation.semaphore.maxConcurrentRequests", value = "20")
    },
            threadPoolKey = "prim",
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = "10")
            },
            fallbackMethod = "fallback")
    public String getDownstream(String uuid) {
        LOG.info("inside getDownstream with: {}", uuid);
        return this.longRestTemplate.getForEntity("http://localhost:9100/downstream", String.class).getBody();
    }

    private String fallback(String uuid, Throwable t) {
        LOG.warn("ex: {}", t.getClass());
        LOG.warn("Thrown: {}", t.getMessage());
        return "fallback";
    }

    private String otherFallback(String uuid) {
        return "fallback-other";
    }

    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000")
    },
            threadPoolKey = "other",
    fallbackMethod = "otherFallback")
    public String getOtherDownstream(String uuid) {
        LOG.info("inside getOtherDownstream with: {}", uuid);
        return this.longRestTemplate.getForEntity("http://localhost:9100/downstream", String.class).getBody();
    }
}
