package com.azdaks.publicapiservice.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.azdaks.publicapiservice.model.AccountResponse;
import com.azdaks.publicapiservice.model.CreateAccountRequest;
import com.azdaks.publicapiservice.model.TransferResponse;
import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
public class AccountsController {
    
    private static final String STATE_STORE = "money-transfer-state";
    private static final String PUBSUB_NAME = "money-transfer-pubsub";
    private static final String TOPIC_NAME = "deposit";

    private static final Logger logger = LoggerFactory.getLogger(TransfersController.class.getName());

    private final DaprClient client = new DaprClientBuilder().build();

    // Create Transfer Request Endpoint
    @PostMapping(path = "/accounts", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransferResponse> createAccount(@RequestBody CreateAccountRequest request) {

        logger.info("Create Account Request Received");

        var message = "Account created for: " + request;

        logger.info(String.format("Saving to State: Owner: %s, Amount: %f", request.getOwner(), request.getAmount()));
        client.saveState(STATE_STORE, request.getOwner(), request.getAmount()).block();

        logger.info("Publishing event to Dapr Pub/Sub Broker: %s, %s".formatted(PUBSUB_NAME, request.toString()));
        client.publishEvent(PUBSUB_NAME, TOPIC_NAME, request).block();

        return ResponseEntity.ok(TransferResponse.builder()
                .message(message)
                .build());
    }

    @GetMapping(path = "/accounts/{owner}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountResponse> getAccount(@PathVariable String owner) {

        logger.info("Get Account Request Received");

        var accountAmount = client.getState(STATE_STORE, owner, Double.class).block();

        return ResponseEntity.ok(AccountResponse.builder()
                .owner(owner)
                .amount(accountAmount.getValue())
                .build());
    }
}
