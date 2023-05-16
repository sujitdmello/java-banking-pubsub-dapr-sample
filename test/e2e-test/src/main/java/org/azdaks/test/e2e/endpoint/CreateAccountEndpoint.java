package org.azdaks.test.e2e.endpoint;

import org.azdaks.test.e2e.contract.request.CreateAccountRequest;
import org.azdaks.test.e2e.contract.response.ApiResponse;
import org.azdaks.test.e2e.contract.response.CreateAccountResponse;
import org.azdaks.test.e2e.util.ApiRequest;
import org.azdaks.test.e2e.util.Print;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;

public class CreateAccountEndpoint implements Endpoint<CreateAccountResponse> {
    @Override
    public ApiResponse<CreateAccountResponse> execute(Executor executor) throws URISyntaxException, IOException, InterruptedException {
        var createAccountRequest = CreateAccountRequest.builder()
                .owner(executor.getSettings().getOwner())
                .amount(executor.getSettings().getAmount())
                .build();

        var payload = executor.getObjectMapper().writeValueAsString(createAccountRequest);
        Print.request(payload);

        var request = ApiRequest.buildPostRequest(executor.getSettings().getApiUrl() + "/accounts", payload);

        var result = executor.getHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        Print.response(result.body());

        var response = executor.getObjectMapper().readValue(result.body(), CreateAccountResponse.class);

        return ApiResponse.<CreateAccountResponse>builder()
                .response(result)
                .body(response)
                .build();
    }
}
