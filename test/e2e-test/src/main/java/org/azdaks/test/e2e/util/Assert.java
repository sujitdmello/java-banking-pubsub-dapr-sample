package org.azdaks.test.e2e.util;

public class Assert {
    public static void statusCodeOk(int statusCode, String successMessage, String errorMessage) throws Exception {
        var expectedStatusCode = statusCode == 200;

        if (!expectedStatusCode) {
            Printer.message(errorMessage + ", Expected: 200, Actual: " + statusCode);
            throw new Exception(errorMessage);
        }

        Printer.message(successMessage);
    }

    public static void contentContains(String expected, String actual, String successMessage, String errorMessage) throws Exception {
        if (!actual.contains(expected)) {
            Printer.message(errorMessage + ", Expected: " + expected + ", Actual: " + actual);
            throw new Exception(errorMessage);
        }

        Printer.message(successMessage);
    }

    public static void contentMatches(String expected, String actual, String successMessage, String errorMessage) throws Exception {
        if (!actual.matches(expected)) {
            Printer.message(errorMessage + ", Expected: " + expected + ", Actual: " + actual);
            throw new Exception(errorMessage);
        }

        Printer.message(successMessage);
    }

    public static void contentMatches(double expected, double actual, String successMessage, String errorMessage) throws Exception {
        if (actual != expected) {
            Printer.message(errorMessage + ", Expected: " + expected + ", Actual: " + actual);
            throw new Exception(errorMessage);
        }

        Printer.message(successMessage);
    }
}
