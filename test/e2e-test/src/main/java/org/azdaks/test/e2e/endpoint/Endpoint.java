package org.azdaks.test.e2e.endpoint;

import org.azdaks.test.e2e.contract.response.ApiResponse;

import java.io.IOException;
import java.net.URISyntaxException;

public interface Endpoint<T> {
    ApiResponse<T> execute(Executor executor) throws URISyntaxException, IOException, InterruptedException;
}
