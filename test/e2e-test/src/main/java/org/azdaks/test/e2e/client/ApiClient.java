package org.azdaks.test.e2e.client;

import org.azdaks.test.e2e.util.Printer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ApiClient {

    private final ApiClientSettings _settings;
    private final HttpClient _httpClient;

    public ApiClient(ApiClientSettings settings) {
        _settings = settings;

        _httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(_settings.timeoutSeconds()))
                .build();
    }

    public void CheckApplicationIsRunning() throws Exception {
        Printer.writeSection("0. Application Running");

        Printer.writeMessage("👀 Test Application is Running");
        var request = HttpRequest.newBuilder()
                .uri(new URI(_settings.apiUrl()))
                .GET()
                .build();


        var response = _httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        Printer.writeMessage("\t🔬 Response\n\n\t" + response.body());

        if (response.statusCode() == 200) {
            Printer.writeMessage("✅ Application is Running");
        } else {
            Printer.writeMessage("🛑 Application is Down");
            throw new Exception("Application is Down");
        }
    }

    public void createAccount() {
        Printer.writeSection("1. Test Create Account");
    }

    public void createMoneyTransfer() {
        Printer.writeSection("2. Test Create Money Transfer Completed");
    }

    public void checkMoneyTransfer() {
        Printer.writeSection("3. Test Money Transfer");
    }
}
