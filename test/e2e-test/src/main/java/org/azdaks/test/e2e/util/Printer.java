package org.azdaks.test.e2e.util;

public class Printer {

    public static void writeSection(String title) {
        System.out.println("-------------------------------------------------");
        System.out.println("🧪 " + title + " 🧪");
        System.out.println("-------------------------------------------------");
    }

    public static void writeMessage(String message) {
        System.out.println(message + "\n");
    }
}
