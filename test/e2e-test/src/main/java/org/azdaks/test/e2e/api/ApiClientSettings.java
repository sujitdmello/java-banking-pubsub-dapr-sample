package org.azdaks.test.e2e.client;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ApiClientSettings {
    private String apiUrl;
    private int timeoutSeconds;
    private String owner;
    private double amount;
}
