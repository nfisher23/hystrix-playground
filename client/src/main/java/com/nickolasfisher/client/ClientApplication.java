package com.nickolasfisher.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.Function;

@SpringBootApplication
public class ClientApplication implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(ClientApplication.class);


    public static void main(String[] args) throws Exception {
        SpringApplication.run(ClientApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        showThreadIsolation();

        tripTheCircuit();
    }

    private void tripTheCircuit() throws Exception {
        loop(() -> {
            try {
                showThreadIsolation();
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException("java is dumb sometimes");
            }
        }, 4);
    }

    private void showThreadIsolation() throws InterruptedException {
        WebClient webClient = WebClient.builder().baseUrl("http://localhost:8080").build();

        int COUNT = 20;
        CountDownLatch latch = new CountDownLatch(2 * COUNT);


        loop(() -> executeWC(webClient, latch, "/upstream", "prim: "), COUNT);
        Thread.sleep(1000);
        loop(() -> executeWC(webClient, latch, "/other/upstream", "other: "), COUNT);

        latch.await();
        LOG.info("done");
    }

    private static void loop(Runnable r, int numTimes) {
        for (int i = 0; i < numTimes; i++) {
            r.run();
        }
    }

    private void executeWC(WebClient webClient, CountDownLatch latch, String path, String logPrefix) {
        webClient.get()
                .uri((Function<UriBuilder, URI>) uriBuilder -> uriBuilder.path(path).build())
                .exchange()
                .subscribe(clientResponse -> {
                    clientResponse.bodyToFlux(String.class)
                            .subscribe(s -> {
                                LOG.info(logPrefix + s);
                                latch.countDown();
                            });
                }, throwable -> {
                    LOG.warn(throwable.getCause().getMessage());
                    latch.countDown();
                });
    }
}
