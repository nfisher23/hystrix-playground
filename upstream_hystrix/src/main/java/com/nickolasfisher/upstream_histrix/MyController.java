package com.nickolasfisher.upstream_histrix;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    private final MyService myService;

    public MyController(MyService myService) {
        this.myService = myService;
    }

    @GetMapping("/home")
    public String getSomething() {
        return this.myService.getDownstream();
    }
}
