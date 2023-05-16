package org.azdaks.test.e2e.endpoint;

import org.azdaks.test.e2e.contract.request.CreateAccountRequest;
import org.azdaks.test.e2e.contract.response.ApiResponse;
import org.azdaks.test.e2e.contract.response.CreateAccountResponse;
import org.azdaks.test.e2e.util.Printer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CreateAccountEndpoint implements Endpoint<CreateAccountResponse> {
    @Override
    public ApiResponse<CreateAccountResponse> execute(Executor executor) throws URISyntaxException, IOException, InterruptedException {
        var createAccountRequest = CreateAccountRequest.builder()
                .owner(executor.getSettings().getOwner())
                .amount(executor.getSettings().getAmount())
                .build();

        var payload = executor.getObjectMapper().writeValueAsString(createAccountRequest);
        Printer.request(payload);

        var request = HttpRequest.newBuilder()
                .uri(URI.create(executor.getSettings().getApiUrl() + "/accounts"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(executor.getObjectMapper().writeValueAsString(createAccountRequest)))
                .build();

        var response = executor.getHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        Printer.response(response.body());

        var createAccountResponse = executor.getObjectMapper().readValue(response.body(), CreateAccountResponse.class);

        return ApiResponse.<CreateAccountResponse>builder()
                .response(response)
                .body(createAccountResponse)
                .build();
    }
}
