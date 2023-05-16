package org.azdaks.test.e2e.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.azdaks.test.e2e.endpoint.CreateAccountEndpoint;
import org.azdaks.test.e2e.endpoint.CreateMoneyTransferEndpoint;
import org.azdaks.test.e2e.endpoint.Executor;
import org.azdaks.test.e2e.endpoint.HomeEndpoint;
import org.azdaks.test.e2e.util.Assert;
import org.azdaks.test.e2e.util.Print;

import java.net.http.HttpClient;
import java.time.Duration;

public class ApiClient {

    private final Executor _executor;

    public ApiClient(ApiClientSettings settings) {

        var _httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(settings.getTimeoutSeconds()))
                .build();

        var _objectMapper = new ObjectMapper();

        _executor = Executor.builder()
                .settings(settings)
                .httpClient(_httpClient)
                .objectMapper(_objectMapper)
                .build();
    }

    public void checkApplicationIsRunning() throws Exception {
        Print.section("0. Application Running");
        Print.message("👀 Test Application is Running");

        var result = new HomeEndpoint().execute(_executor);

        Assert.matchesStatusCode(200, result.getResponse().statusCode(), "✅ Application is Running", "🛑 Application is Not Running");
        Assert.contentContains("Public API Service Started", result.getBody().getMessage(), "✅ Application is Running Correctly", "🛑 Application is Not Running Correctly");
    }

    public void createAccount() throws Exception {
        Print.section("1. Test Create Account");
        Print.message("👀 Test Account Creation");

        var result = new CreateAccountEndpoint().execute(_executor);

        Assert.matchesStatusCode(200, result.getResponse().statusCode(), "✅ Account Created", "🛑 Account Creation Failed");
        Assert.contentMatches(_executor.getSettings().getOwner(), result.getBody().getAccount().getOwner(), "✅ Account Owner is Correct", "🛑 Account Owner is Not Correct");
        Assert.contentMatches(_executor.getSettings().getAmount(), result.getBody().getAccount().getAmount(), "✅ Account Amount is Correct", "🛑 Account Amount is Not Correct");
    }

    public void createMoneyTransfer() throws Exception {
        Print.section("2. Test Create Money Transfer");
        Print.message("👀 Test Money Transfer Creation");

        var result = new CreateMoneyTransferEndpoint().execute(_executor);

        Assert.matchesStatusCode(202, result.getResponse().statusCode(), "✅ Money Transfer Created", "🛑 Money Transfer Creation Failed");
        Assert.contentMatches("ACCEPTED", result.getBody().getStatus(), "✅ Money Transfer Status is Correct", "🛑 Money Transfer Status is Not Correct");
    }

    public void checkMoneyTransfer() throws Exception {
        Print.section("3. Test Money Transfer Completed");
    }
}
