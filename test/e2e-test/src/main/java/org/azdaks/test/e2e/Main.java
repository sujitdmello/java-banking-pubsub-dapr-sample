package org.azdaks.test.e2e;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import org.azdaks.test.e2e.client.ApiClient;
import org.azdaks.test.e2e.client.ApiClientSettings;

import java.util.Random;

public class Main {

    private static final String API_URL = "http://localhost:8080";
    private static final int TIMEOUT_SECONDS = 10;

    public static void main(String[] args) throws Exception {

        var owner = NanoIdUtils.randomNanoId(new Random(), NanoIdUtils.DEFAULT_ALPHABET, 10);
        var amount = 1000.0d;

        var apiSettings = ApiClientSettings.builder()
                .apiUrl(API_URL)
                .timeoutSeconds(TIMEOUT_SECONDS)
                .owner(owner)
                .amount(amount)
                .build();

        var apiClient = new ApiClient(apiSettings);

        apiClient.checkApplicationIsRunning();
        apiClient.createAccount();
        apiClient.createMoneyTransfer();
        apiClient.checkMoneyTransfer();
    }


}