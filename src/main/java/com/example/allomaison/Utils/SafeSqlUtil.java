package com.example.allomaison.Utils;

/**
 * Utility class for validating dynamic SQL fragments to prevent injection risks.
 */
public class SafeSqlUtil {

    /**
     * Validates a table name used in dynamic SQL queries.
     * Only allows names that start with "Chat" followed by uppercase hex digits.
     *
     * @param tableName the table name to validate
     * @return the same table name if valid
     * @throws IllegalArgumentException if the name is unsafe
     */
    public static String safeChatTableName(String tableName) {
        if (tableName == null || !tableName.matches("^Chat[A-Z0-9]+$")) {
            throw new IllegalArgumentException("Unsafe or malformed table name: " + tableName);
        }
        return tableName;
    }

}
