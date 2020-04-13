package com.nickolasfisher.downstream_hystrix;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.Random;

@RestController
public class MyController {

    private static final Random random = new Random(30);

    @GetMapping("/downstream")
    public ResponseEntity<String> getDownstream() throws Exception {
        int i = random.nextInt(1000);
        System.out.println("int: " + i);
        Thread.sleep(500 + i);
        return ResponseEntity.of(Optional.of("hello"));
    }
}
