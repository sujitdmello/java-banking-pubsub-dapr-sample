package org.azdaks.test.e2e.client;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.azdaks.test.e2e.contract.request.CreateAccountRequest;
import org.azdaks.test.e2e.contract.response.CreateAccountResponse;
import org.azdaks.test.e2e.contract.response.HomeResponse;
import org.azdaks.test.e2e.util.Printer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Random;

public class ApiClient {

    private final ApiClientSettings _settings;
    private final HttpClient _httpClient;
    private final ObjectMapper _objectMapper;
    private final String _owner;
    private final float _amount;

    public ApiClient(ApiClientSettings settings) {
        _settings = settings;

        _httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(_settings.getTimeoutSeconds()))
                .build();

        _objectMapper = new ObjectMapper();

        _owner = NanoIdUtils.randomNanoId(new Random(), NanoIdUtils.DEFAULT_ALPHABET, 10);
        _amount = 1000.0f;
    }

    public void checkApplicationIsRunning() throws Exception {
        Printer.section("0. Application Running");

        Printer.message("👀 Test Application is Running");
        var request = HttpRequest.newBuilder()
                .uri(new URI(_settings.getApiUrl()))
                .GET()
                .build();


        var response = _httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Printer.response(response.body());

        var homeResponse = _objectMapper.readValue(response.body(), HomeResponse.class);

        var expectedStatusCode = response.statusCode() == 200;
        var expectedMessageContent = "Public API Service Started";
        var expectedMessage = homeResponse.getMessage().contains(expectedMessageContent);

        if (!expectedStatusCode) {
            var errorMessage = "🛑 Application is Down";
            Printer.message(errorMessage);
            throw new Exception(errorMessage);
        }

        Printer.message("✅ Application is Running");

        if (!expectedMessage) {
            var errorMessage = "🛑 Application is Not Running Expected Message, Expected:" + expectedMessageContent;
            Printer.message(errorMessage);
            throw new Exception(errorMessage);
        }

        Printer.message("✅ Application is Running Correctly");
    }

    public void createAccount() throws Exception {
        Printer.section("1. Test Create Account");

        var createAccountRequest = CreateAccountRequest.builder()
                .owner(_owner)
                .amount(_amount)
                .build();

        var payload = _objectMapper.writeValueAsString(createAccountRequest);
        Printer.request(payload);

        var request = HttpRequest.newBuilder()
                .uri(URI.create(_settings.getApiUrl() + "/accounts"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(_objectMapper.writeValueAsString(createAccountRequest)))
                .build();

        var response = _httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Printer.response(response.body());

        var createAccountResponse = _objectMapper.readValue(response.body(), CreateAccountResponse.class);

        var expectedStatusCode = response.statusCode() == 200;
        if (!expectedStatusCode) {
            var errorMessage = "🛑 Account Creating Failed";
            Printer.message(errorMessage);
            throw new Exception(errorMessage);
        }

        Printer.message("✅ Account Created");

        var expectedOwner = createAccountResponse.getAccount().getOwner();
        if (!expectedOwner.equals(_owner)) {
            var errorMessage = "🛑 Account Owner is Not Correct, Expected:" + _owner;
            Printer.message(errorMessage);
            throw new Exception(errorMessage);
        }

        Printer.message("✅ Account Owner is Correct");

        var expectedAmount = createAccountResponse.getAccount().getAmount();
        if (expectedAmount != _amount) {
            var errorMessage = "🛑 Account Amount is Not Correct, Expected:" + _amount;
            Printer.message(errorMessage);
            throw new Exception(errorMessage);
        }

        Printer.message("✅ Account Amount is Correct");
    }

    public void createMoneyTransfer() {
        Printer.section("2. Test Create Money Transfer Completed");
    }

    public void checkMoneyTransfer() {
        Printer.section("3. Test Money Transfer");
    }
}
