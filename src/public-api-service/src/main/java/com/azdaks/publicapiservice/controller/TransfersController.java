package com.azdaks.publicapiservice.controller;

import com.azdaks.publicapiservice.model.TransferRequest;
import com.azdaks.publicapiservice.model.TransferResponse;
import io.dapr.client.domain.CloudEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class TransfersController {

    private static final String PUBSUB_NAME = "money-transfer-pubsub";
    private static final String TOPIC_NAME = "transfer";

    private static final Logger logger = Logger.getLogger(TransfersController.class.getName());

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static final String DAPR_HOST = System.getenv().getOrDefault("DAPR_HOST", "http://localhost");
    private static final String DAPR_HTTP_PORT = System.getenv().getOrDefault("DAPR_HTTP_PORT", "3500");

    public TransfersController() {
        logger.info("TransfersController created");
    }

    // Create Transfer Request Endpoint
    @PostMapping(path = "/transfers", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransferResponse> createTransferRequest(@RequestBody TransferRequest transferRequest) {

        var pubsubUri = DAPR_HOST + ":" + DAPR_HTTP_PORT + "/v1.0/publish/" + PUBSUB_NAME + "/" + TOPIC_NAME;

        logger.info("Transfer Request Received");
        logger.info(String.format("Start publishing message to Dapr Pub/Sub: %s", pubsubUri));

        var message = "Transfer Request Received: " + transferRequest;
        var status = HttpStatus.ACCEPTED;
        var transactionId = "123456789";

        try {
            /**
             * The official Dapr Java SDK does not support latest reactor version.
             * Instead, we go with HTTP client.
             * https://github.com/dapr/java-sdk/issues/815
             */
            ///client.publishEvent(PUBSUB_NAME, TOPIC_NAME, transferRequest, singletonMap(Metadata.TTL_IN_SECONDS, MESSAGE_TTL_IN_SECONDS)).block();

            CloudEvent cloudEvent = new CloudEvent();
            cloudEvent.setData(transferRequest);
            cloudEvent.setType("com.dapr.cloudevent.sent");
            cloudEvent.setId(transactionId);
            cloudEvent.setSource("money-transfer-app");
            cloudEvent.setSpecversion("1.0");
            cloudEvent.setDatacontenttype("application/cloudevents+json");


                    // Publish an event/message using Dapr PubSub via HTTP Post
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(new ObjectMapper().writeValueAsString(cloudEvent)))
                    .uri(URI.create(pubsubUri))
                    .header("Content-Type", "application/cloudevents+json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            logger.info(String.valueOf(response.statusCode()));
            logger.info(response.body());


        } catch (Exception e) {
            logger.severe("Error publishing message: ");

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
