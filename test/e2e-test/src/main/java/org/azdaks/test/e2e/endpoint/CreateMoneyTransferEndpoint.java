package org.azdaks.test.e2e.endpoint;

import org.azdaks.test.e2e.contract.request.CreateTransferRequest;
import org.azdaks.test.e2e.contract.response.ApiResponse;
import org.azdaks.test.e2e.contract.response.TransferResponse;
import org.azdaks.test.e2e.util.ApiRequest;
import org.azdaks.test.e2e.util.Print;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;

public class CreateMoneyTransferEndpoint implements Endpoint<TransferResponse> {
    @Override
    public ApiResponse<TransferResponse> execute(Executor executor) throws URISyntaxException, IOException, InterruptedException {
        var createTransferRequest = CreateTransferRequest.builder()
                .sender(executor.getSettings().getOwner())
                .receiver("Receiver")
                .amount(10.0d)
                .build();

        var payload = executor.getObjectMapper().writeValueAsString(createTransferRequest);
        Print.request(payload);

        var request = ApiRequest.buildPostRequest(executor.getSettings().getApiUrl() + "/transfers", payload);

        var result = executor.getHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        Print.response(result.body());

        var response = executor.getObjectMapper().readValue(result.body(), TransferResponse.class);

        return ApiResponse.<TransferResponse>builder()
                .response(result)
                .body(response)
                .build();
    }
}
