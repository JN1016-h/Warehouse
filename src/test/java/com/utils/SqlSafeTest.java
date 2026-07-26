package com.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SqlSafeTest {

    @Test
    public void allowsKnownTablesAndSafeColumns() {
        assertTrue(SqlSafe.isAllowedTable("shangpinxinxi"));
        assertTrue(SqlSafe.isSafeIdentifier("yonghuzhanghao"));
        assertDoesNotThrow(() -> SqlSafe.requireAllowedTable("users"));
        assertDoesNotThrow(() -> SqlSafe.requireSafeIdentifier("id", "column"));
    }

    @Test
    public void rejectsInjectionPayloads() {
        assertFalse(SqlSafe.isAllowedTable("users; drop table users"));
        assertFalse(SqlSafe.isSafeIdentifier("id) OR 1=1--"));
        assertThrows(IllegalArgumentException.class, () -> SqlSafe.requireAllowedTable("evil"));
        assertThrows(IllegalArgumentException.class, () -> SqlSafe.requireSafeIdentifier("a b", "column"));
    }
}
