package com.azdaks.publicapiservice.controller;

import com.azdaks.publicapiservice.model.TransferRequest;
import com.azdaks.publicapiservice.model.TransferResponse;
import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import io.dapr.client.domain.Metadata;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import static java.util.Collections.singletonMap;

import java.util.logging.Logger;

@RestController
public class TransfersController {

    private static final String PUBSUB_NAME = "pubsub";
    private static final String TOPIC_NAME = "transfer";
    private String MESSAGE_TTL_IN_SECONDS = "1000";

    private static Logger logger = Logger.getLogger(TransfersController.class.getName());

    public TransfersController() {
        logger.info("TransfersController created");
    }

    // Create Transfer Request Endpoint
    @PostMapping(path = "/transfers", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransferResponse> createTransferRequest(@RequestBody TransferRequest transferRequest) {

        logger.info("Transfer Request Received");

        var message = "Transfer Request Received: " + transferRequest;
        var status = HttpStatus.ACCEPTED;
        var transactionId = "123456789";

        try (DaprClient client = (new DaprClientBuilder()).build()) {
            client.publishEvent(PUBSUB_NAME, TOPIC_NAME, transferRequest, singletonMap(Metadata.TTL_IN_SECONDS, MESSAGE_TTL_IN_SECONDS)).block();
        } catch (Exception e) {
            logger.severe("Error publishing message: " + e.getMessage());

            status = HttpStatus.BAD_REQUEST;
            message = e.getMessage();
        }

        var response = TransferResponse
                .builder()
                .message(message)
                .status(status.toString())
                .transactionId(transactionId)
                .build();

        return status == HttpStatus.ACCEPTED ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }
}
