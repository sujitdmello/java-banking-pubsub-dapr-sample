package com.azdaks.publicapiservice.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransferRequest {
    private String sender;
    private String receiver;
    private double amount;

    public String toString() {
        return "Sender: " + sender + ", Receiver: " + receiver + ", Amount: " + amount;
    }
}
