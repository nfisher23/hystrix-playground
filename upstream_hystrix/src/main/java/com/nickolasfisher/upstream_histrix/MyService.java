package com.nickolasfisher.upstream_histrix;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixException;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class MyService {

    private final RestTemplate restTemplate;

    public MyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /*
        Because the underlying rest template has a timeout of 1 second, the configuration of
        "execution.isolation.thread.timeoutInMilliseconds" of "500" appears to do nothing
        to actually stop the request from going through. They all seem to happen inside the same threadpool, which is
        even stranger at first glance.
     */
    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "500")
    },
            raiseHystrixExceptions = HystrixException.RUNTIME_EXCEPTION,
    fallbackMethod = "fallback")
    public String getDownstream() {
        return this.restTemplate.getForEntity("http://localhost:9100/downstream", String.class).getBody();
    }

    private String fallback() {
        return "fallback";
    }
}
