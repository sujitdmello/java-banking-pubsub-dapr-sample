package org.azdaks.test.e2e.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.azdaks.test.e2e.api.ApiClientSettings;
import org.azdaks.test.e2e.api.ApiRequest;
import org.azdaks.test.e2e.contract.request.CreateTransferRequest;
import org.azdaks.test.e2e.util.Print;

import java.io.IOException;
import java.net.http.HttpRequest;

public class CreateMoneyTransferEndpoint implements Endpoint {

    @Override
    public HttpRequest createRequest(ApiClientSettings settings, ObjectMapper objectMapper) throws IOException {
        var createTransferRequest = CreateTransferRequest.builder()
                .sender(settings.getOwner())
                .receiver("Receiver")
                .amount(10.0d)
                .build();

        var payload = objectMapper.writeValueAsString(createTransferRequest);
        Print.request(payload);

        return ApiRequest.buildPostRequest(settings.getApiUrl() + "/transfers", payload);
    }
}
