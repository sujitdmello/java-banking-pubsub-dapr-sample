package org.azdaks.test.e2e;

import org.azdaks.test.e2e.client.ApiClient;
import org.azdaks.test.e2e.client.ApiClientSettings;

public class Main {

    private static final String API_URL = "http://localhost:8080";
    private static final int TIMEOUT_SECONDS = 10;

    public static void main(String[] args) throws Exception {

        var apiClient = new ApiClient(new ApiClientSettings(API_URL, TIMEOUT_SECONDS));

        apiClient.CheckApplicationIsRunning();
        apiClient.createAccount();
        apiClient.createMoneyTransfer();
        apiClient.createMoneyTransfer();
    }


}