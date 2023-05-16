package org.azdaks.test.e2e.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.azdaks.test.e2e.contract.response.CreateAccountResponse;
import org.azdaks.test.e2e.contract.response.HomeResponse;
import org.azdaks.test.e2e.contract.response.TransferResponse;
import org.azdaks.test.e2e.endpoint.CreateAccountEndpoint;
import org.azdaks.test.e2e.endpoint.CreateMoneyTransferEndpoint;
import org.azdaks.test.e2e.endpoint.HomeEndpoint;
import org.azdaks.test.e2e.util.Assert;
import org.azdaks.test.e2e.util.Print;

import java.net.http.HttpClient;
import java.time.Duration;

public class ApiTestRunner {

    private final HttpClient _httpClient;
    private final ObjectMapper _objectMapper;
    private final ApiClientSettings _settings;

    public ApiTestRunner(ApiClientSettings settings) {

        _settings = settings;

        _httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(settings.getTimeoutSeconds()))
                .build();

        _objectMapper = new ObjectMapper();
    }

    public void checkApplicationIsRunning() throws Exception {
        Print.section("0. Application Running");
        Print.message("👀 Test Application is Running");

        var result = ApiClient.<HomeResponse>builder()
                .settings(_settings)
                .httpClient(_httpClient)
                .objectMapper(_objectMapper)
                .endpoint(new HomeEndpoint())
                .build()
                .send(HomeResponse.class);

        Assert.matchesStatusCode(200, result.getResponse().statusCode(), "✅ Application is Running", "🛑 Application is Not Running");
        Assert.contentContains("Public API Service Started", result.getBody().getMessage(), "✅ Application is Running Correctly", "🛑 Application is Not Running Correctly");
    }

    public void createAccount() throws Exception {
        Print.section("1. Test Create Account");
        Print.message("👀 Test Account Creation");

        var result = ApiClient.<CreateAccountResponse>builder()
                .settings(_settings)
                .httpClient(_httpClient)
                .objectMapper(_objectMapper)
                .endpoint(new CreateAccountEndpoint())
                .build()
                .send(CreateAccountResponse.class);

        Assert.matchesStatusCode(200, result.getResponse().statusCode(), "✅ Account Created", "🛑 Account Creation Failed");
        Assert.contentMatches(_settings.getOwner(), result.getBody().getAccount().getOwner(), "✅ Account Owner is Correct", "🛑 Account Owner is Not Correct");
        Assert.contentMatches(_settings.getAmount(), result.getBody().getAccount().getAmount(), "✅ Account Amount is Correct", "🛑 Account Amount is Not Correct");
    }

    public void createMoneyTransfer() throws Exception {
        Print.section("2. Test Create Money Transfer");
        Print.message("👀 Test Money Transfer Creation");

        var result = ApiClient.<TransferResponse>builder()
                .settings(_settings)
                .httpClient(_httpClient)
                .objectMapper(_objectMapper)
                .endpoint(new CreateMoneyTransferEndpoint())
                .build()
                .send(TransferResponse.class);

        Assert.matchesStatusCode(202, result.getResponse().statusCode(), "✅ Money Transfer Created", "🛑 Money Transfer Creation Failed");
        Assert.contentMatches("ACCEPTED", result.getBody().getStatus(), "✅ Money Transfer Status is Correct", "🛑 Money Transfer Status is Not Correct");
    }

    public void checkMoneyTransfer() throws Exception {
        Print.section("3. Test Money Transfer Completed");
    }
}
