package com.example.integratebank.enumeration;

import java.util.Arrays;
import java.util.regex.Pattern;

public enum CardType {
    VISA,
    JCB,
    UPOP,
    Master;

    public static boolean isCardType(String str) {
        return Arrays.stream(values())
                     .anyMatch(v -> v.name().equals(str));
    }

    public static CardType fromText(String text) {
        return Arrays.stream(values())
                     .filter(status -> status.name().equalsIgnoreCase(text))
                     .findFirst().orElseThrow(() -> new IllegalArgumentException("No enum constant. " + text));
    }

    public static CardType fromCardNumber(String cardNumber) {
        try {
            if (cardNumber.startsWith("4")) return VISA;
            if (cardNumber.startsWith("8") || cardNumber.startsWith("62") || cardNumber.startsWith("5598"))
                return UPOP;
            long firstTwo = Long.parseLong(cardNumber.substring(0, 2));
            if (firstTwo >= 51 && firstTwo <= 55 || firstTwo >= 22 && firstTwo <= 27) return Master;
            long firstFour = Long.parseLong(cardNumber.substring(0, 4));
            if (firstFour >= 3528 && firstFour <= 3589) return JCB;
        } catch (Exception ignore) {
            return UPOP;
        }
        return UPOP;
    }
}
