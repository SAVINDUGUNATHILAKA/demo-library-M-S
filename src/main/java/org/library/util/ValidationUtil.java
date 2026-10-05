package org.library.util;

import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * Utility for validating form inputs across the application.
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9+\\-\\s()]{7,15}$");

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        if (!isNotEmpty(email)) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (!isNotEmpty(phone)) return false;
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isPositiveInteger(String value) {
        if (!isNotEmpty(value)) return false;
        try {
            int val = Integer.parseInt(value.trim());
            return val > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidYear(String value) {
        if (!isNotEmpty(value)) return false;
        try {
            int year = Integer.parseInt(value.trim());
            int currentYear = LocalDate.now().getYear();
            return year >= 1000 && year <= currentYear + 1;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
