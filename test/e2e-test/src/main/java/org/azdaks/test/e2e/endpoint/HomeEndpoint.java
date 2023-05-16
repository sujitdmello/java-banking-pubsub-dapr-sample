package org.azdaks.test.e2e.endpoint;

import org.azdaks.test.e2e.contract.response.ApiResponse;
import org.azdaks.test.e2e.contract.response.HomeResponse;
import org.azdaks.test.e2e.util.ApiRequest;
import org.azdaks.test.e2e.util.Print;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;

public class HomeEndpoint implements Endpoint<HomeResponse> {

    @Override
    public ApiResponse<HomeResponse> execute(Executor executor) throws URISyntaxException, IOException, InterruptedException {
        var request = ApiRequest.buildGetRequest(executor.getSettings().getApiUrl());

        var response = executor.getHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        Print.response(response.body());

        var body = executor.getObjectMapper().readValue(response.body(), HomeResponse.class);

        return ApiResponse.<HomeResponse>builder()
                .response(response)
                .body(body)
                .build();
    }
}
