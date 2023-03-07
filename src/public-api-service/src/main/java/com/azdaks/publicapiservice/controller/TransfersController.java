package com.azdaks.publicapiservice.controller;

import com.azdaks.publicapiservice.model.TransferRequest;
import com.azdaks.publicapiservice.model.TransferResponse;
import io.dapr.Topic;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@RestController
public class TransfersController {

    private static Logger logger = Logger.getLogger(TransfersController.class.getName());

    public TransfersController() {
        logger.info("TransfersController created");
    }

    // Create Transfer Request Endpoint
    @PostMapping(path = "/transfers", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public TransferResponse createTransferRequest(@RequestBody TransferRequest transferRequest) {
        var transferRequestMessage = "Transfer Request Received: " + transferRequest;

        var response = TransferResponse
                .builder()
                .message(transferRequestMessage)
                .status("ACCEPTED")
                .transactionId("123456789")
                .build();

        logger.info(transferRequestMessage);
        return response;
    }
}
