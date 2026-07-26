package com.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Whitelist validation for dynamic SQL identifiers (table/column names).
 * Prevents SQL injection when MyBatis uses ${} substitution.
 */
public final class SqlSafe {

    private static final Pattern IDENT = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*$");

    private static final Set<String> ALLOWED_TABLES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
            "users", "yonghu", "gongyingshang", "shangpinfenlei", "shangpinxinxi",
            "rukuxinxi", "chukuxinxi", "dinghuoxinxi", "buhuotixing", "config",
            "token", "storeup", "news", "discuss", "chat", "messages",
            "ai_chat_session", "ai_chat_message", "ai_report"
    )));

    private SqlSafe() {
    }

    public static boolean isSafeIdentifier(String name) {
        return name != null && IDENT.matcher(name).matches();
    }

    public static boolean isAllowedTable(String tableName) {
        return isSafeIdentifier(tableName) && ALLOWED_TABLES.contains(tableName.toLowerCase(Locale.ROOT));
    }

    public static void requireSafeIdentifier(String name, String label) {
        if (!isSafeIdentifier(name)) {
            throw new IllegalArgumentException("Invalid " + label + ": " + name);
        }
    }

    public static void requireAllowedTable(String tableName) {
        if (!isAllowedTable(tableName)) {
            throw new IllegalArgumentException("Table not allowed: " + tableName);
        }
    }
}
