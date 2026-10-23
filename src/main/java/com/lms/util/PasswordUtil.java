package com.lms.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for secure password hashing using SHA-256 algorithm.
 * Complies with requirement: "Password must not be stored as plain text.
 * Use password hashing before storing it in the database."
 */
public class PasswordUtil {

    /**
     * Hashes a raw plain-text password using SHA-256 and returns a 64-character hexadecimal string.
     *
     * @param plainPassword Raw password provided by user
     * @return Hex-encoded SHA-256 hash
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 hashing algorithm not available", e);
        }
    }

    /**
     * Compares a plain password against an existing SHA-256 hash.
     *
     * @param plainPassword Raw password
     * @param hashedPassword Hex-encoded hash stored in database
     * @return true if matches, false otherwise
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        return hashPassword(plainPassword).equalsIgnoreCase(hashedPassword);
    }
}
