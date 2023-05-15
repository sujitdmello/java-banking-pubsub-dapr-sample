package org.azdaks.test.e2e;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

public class Main {

    private static final String API_URL = "http://localhost:8080";
    private static final int TIMEOUT_SECONDS = 10;

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
       output("🤖 Testing Application");



        output("👀 Check Application is Running");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(API_URL))
                .timeout(Duration.of(TIMEOUT_SECONDS, SECONDS))
                .GET()
                .build();


        HttpResponse<String> response = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());

        output("Response: " + response.body());

        if (response.statusCode() == 200) {
            output("✅ Application is Running");
        } else {
            output("🛑 Application is Down");
        }

    }

    private static void output(String message) {
        System.out.println("\n" + message + "\n");
    }
}