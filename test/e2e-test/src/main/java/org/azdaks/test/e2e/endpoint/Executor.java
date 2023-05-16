package org.azdaks.test.e2e.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;
import org.azdaks.test.e2e.client.ApiClientSettings;

import java.net.http.HttpClient;

@Builder
@Getter
public class Executor {
    private ApiClientSettings settings;
    private HttpClient httpClient;
    private ObjectMapper objectMapper;
}
