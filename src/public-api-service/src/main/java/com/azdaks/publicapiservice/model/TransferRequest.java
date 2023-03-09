package com.azdaks.publicapiservice.model;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Setter
@Jacksonized
public class TransferRequest {
    private String sender;
    private String receiver;
    private double amount;
    private String id;

    public String toString() {
        return "ID: " + id + "Sender: " + sender + ", Receiver: " + receiver + ", Amount: " + amount;
    }

    public static String generateId() {
        return NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, "abcdef12345".toCharArray(), 5);
    }
}
