package org.azdaks.test.e2e;

import org.azdaks.test.e2e.client.ApiClient;
import org.azdaks.test.e2e.client.ApiClientSettings;

public class Main {

    private static final String API_URL = "http://localhost:8080";
    private static final int TIMEOUT_SECONDS = 10;

    public static void main(String[] args) throws Exception {

        var apiSettings = ApiClientSettings.builder()
                .apiUrl(API_URL)
                .timeoutSeconds(TIMEOUT_SECONDS)
                .build();

        var apiClient = new ApiClient(apiSettings);

        apiClient.checkApplicationIsRunning();
        apiClient.createAccount();
        apiClient.createMoneyTransfer();
        apiClient.checkMoneyTransfer();
    }


}