package com.bfhl.api.service;

import com.bfhl.api.dto.BfhlRequest;
import com.bfhl.api.dto.BfhlResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of BfhlService containing all business logic
 */
@Service
public class BfhlServiceImpl implements BfhlService {

    // ── CHANGE THESE TO YOUR DETAILS ──────────────────────────────────────────
    private static final String FULL_NAME   = "vansh_kapil";
    private static final String DOB         = "21072005";          // ddmmyyyy
    private static final String EMAIL       = "vanshkapil@gmail.com";
    private static final String ROLL_NUMBER = "2310991314";
    // ──────────────────────────────────────────────────────────────────────────

    private static final String USER_ID = FULL_NAME + "_" + DOB;

    @Override
    public BfhlResponse processData(BfhlRequest request) {
        List<String> data = request.getData();

        List<String> oddNumbers       = new ArrayList<>();
        List<String> evenNumbers      = new ArrayList<>();
        List<String> alphabets        = new ArrayList<>();
        List<String> specialChars     = new ArrayList<>();
        long         sumOfNumbers     = 0;
        List<String> allAlphaChars    = new ArrayList<>(); // individual chars for concat

        for (String token : data) {

            if (isNumber(token)) {
                long num = Long.parseLong(token);
                sumOfNumbers += num;
                if (num % 2 == 0) {
                    evenNumbers.add(token);
                } else {
                    oddNumbers.add(token);
                }

            } else if (isAlphabetic(token)) {
                // Uppercase the whole token (handles multi-char like "ABCD", "DOE")
                alphabets.add(token.toUpperCase());
                // Collect individual characters in original order for concat_string
                for (char c : token.toCharArray()) {
                    allAlphaChars.add(String.valueOf(c));
                }

            } else {
                // Anything that is neither purely numeric nor purely alphabetic
                specialChars.add(token);
            }
        }

        String concatString = buildConcatString(allAlphaChars);

        return BfhlResponse.builder()
                .isSuccess(true)
                .userId(USER_ID)
                .email(EMAIL)
                .rollNumber(ROLL_NUMBER)
                .oddNumbers(oddNumbers)
                .evenNumbers(evenNumbers)
                .alphabets(alphabets)
                .specialCharacters(specialChars)
                .sum(String.valueOf(sumOfNumbers))
                .concatString(concatString)
                .build();
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    /**
     * Returns true if the token represents an integer (positive or negative).
     */
    private boolean isNumber(String token) {
        if (token == null || token.isEmpty()) return false;
        try {
            Long.parseLong(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Returns true if every character in the token is an ASCII letter.
     * Handles multi-char tokens like "ABCD" or "DOE".
     */
    private boolean isAlphabetic(String token) {
        if (token == null || token.isEmpty()) return false;
        for (char c : token.toCharArray()) {
            if (!Character.isLetter(c)) return false;
        }
        return true;
    }

    /**
     * Builds the concat_string:
     * - Reverse the list of individual alphabetical characters collected from input.
     * - Apply alternating caps: index 0 → uppercase, index 1 → lowercase, …
     *
     * Example B: input chars [a, y, b] → reversed [b, y, a]
     *   → alternating: B (upper), y (lower), A (upper) → "ByA"
     *
     * Example C: input chars [A, B, C, D, D, O, E] → reversed [E, O, D, D, C, B, A]
     *   → E(U) o(l) D(U) d(l) C(U) b(l) A(U) → "EoDdCbAa"  ✓
     */
    private String buildConcatString(List<String> chars) {
        if (chars.isEmpty()) return "";

        List<String> reversed = new ArrayList<>(chars);
        java.util.Collections.reverse(reversed);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < reversed.size(); i++) {
            String c = reversed.get(i);
            sb.append(i % 2 == 0 ? c.toUpperCase() : c.toLowerCase());
        }
        return sb.toString();
    }
}
