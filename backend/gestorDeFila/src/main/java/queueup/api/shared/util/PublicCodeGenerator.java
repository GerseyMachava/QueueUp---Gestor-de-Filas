package queueup.api.shared.util;

import java.security.SecureRandom;

public final class PublicCodeGenerator {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int LENGHT = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PublicCodeGenerator() {
    }

    public static String generate() {
        StringBuilder sb = new StringBuilder(LENGHT);
        for (int i = 0; i < LENGHT; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

}
