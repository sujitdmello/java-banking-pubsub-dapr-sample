package org.azdaks.test.e2e.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.azdaks.test.e2e.client.ApiClientSettings;
import org.azdaks.test.e2e.contract.response.ApiResponse;
import org.azdaks.test.e2e.contract.response.HomeResponse;
import org.azdaks.test.e2e.util.Printer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class HomeEndpoint implements Endpoint<HomeResponse> {

    private final ApiClientSettings _settings;
    private final HttpClient _httpClient;
    private final ObjectMapper _objectMapper;

    public HomeEndpoint(ApiClientSettings settings, ObjectMapper objectMapper) {
        _settings = settings;
        _objectMapper = objectMapper;

        _httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(_settings.getTimeoutSeconds()))
                .build();
    }

    @Override
    public ApiResponse<HomeResponse> execute() throws URISyntaxException, IOException, InterruptedException {
        var request = HttpRequest.newBuilder()
                .uri(new URI(_settings.getApiUrl()))
                .GET()
                .build();


        var response = _httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Printer.response(response.body());

        var body = _objectMapper.readValue(response.body(), HomeResponse.class);

        return ApiResponse.<HomeResponse>builder()
                .response(response)
                .body(body)
                .build();
    }
}
