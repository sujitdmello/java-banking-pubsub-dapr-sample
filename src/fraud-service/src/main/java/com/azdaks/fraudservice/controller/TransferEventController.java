package com.azdaks.fraudservice.controller;

import com.azdaks.fraudservice.model.TransferRequest;
import io.dapr.Topic;
import io.dapr.client.domain.CloudEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class TransferEventController {

    private static final Logger logger = LoggerFactory.getLogger(TransferEventController.class);

    private static final String PUBSUB_NAME = "money-transfer-pubsub";
    private static final String TOPIC_NAME = "transfer";

    @Topic(name = TOPIC_NAME, pubsubName = PUBSUB_NAME)
    @PostMapping(path = "/transfers", consumes = MediaType.ALL_VALUE)
    public Mono<ResponseEntity> getCheckout(@RequestBody(required = false) CloudEvent<TransferRequest> cloudEvent) {
        return Mono.fromSupplier(() -> {
            try {
                logger.info("Fraud service received: " + cloudEvent.getData().toString());

                if (cloudEvent.getData().getAmount() > 1000) {
                    logger.info("Fraud detected, amount has to be less than 1000");
                    return ResponseEntity.badRequest().body("FRAUD DETECTED");
                }

                return ResponseEntity.ok("SUCCESS");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
