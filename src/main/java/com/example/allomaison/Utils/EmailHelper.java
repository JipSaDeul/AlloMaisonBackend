package com.example.allomaison.Utils;

import java.util.regex.Pattern;

public class EmailHelper {

    // Email must be ASCII only, with at least one char before @, one after, and a dot+letter domain
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[\\p{ASCII}&&[^@\\s]]+@[\\p{ASCII}&&[^@\\s]]+\\.[A-Za-z]+$"
    );

    public static boolean isPossiblyValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
}
