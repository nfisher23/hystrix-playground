package com.nickolasfisher.upstream_histrix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class MyController {

    private final MyService myService;

    private static final Logger LOG = LoggerFactory.getLogger(MyController.class);

    public MyController(MyService myService) {
        this.myService = myService;
    }

    @GetMapping("/upstream")
    public String getSomething() {
        UUID uuid = UUID.randomUUID();
        LOG.info("request: {}", uuid);
        String downstream = this.myService.getDownstream(uuid.toString());
        LOG.info("response: {}", uuid);
        return downstream;
    }

    @GetMapping("/other/upstream")
    public String getOther() {
        UUID uuid = UUID.randomUUID();
        LOG.info("request getOther: {}", uuid);
        String downstream = this.myService.getOtherDownstream(uuid.toString());
        LOG.info("response getOther: {}", uuid);
        return downstream;
    }
}
