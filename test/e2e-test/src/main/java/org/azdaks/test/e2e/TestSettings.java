package org.azdaks.test.e2e;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
public class TestSettings {
    private String apiUrl;
    private int timeoutSeconds;
    private String owner;
    private double amount;
    private double transferAmount;
    private double fraudAmount;


    @Setter
    private String transferId;
}
