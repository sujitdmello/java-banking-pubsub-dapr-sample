package org.azdaks.test.e2e.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;
import org.azdaks.test.e2e.contract.response.ApiResponse;
import org.azdaks.test.e2e.util.Print;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Builder
@Getter
public class ApiClient<T> {
    private ApiClientSettings settings;
    private HttpClient httpClient;
    private ObjectMapper objectMapper;

    public ApiResponse<T> send(HttpRequest request, Class<T> response) throws IOException, InterruptedException {
        var result = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Print.response(result.body());

        var body = objectMapper.readValue(result.body(), response);

        return ApiResponse.<T>builder()
                .response(result)
                .body(body)
                .build();
    }
}
