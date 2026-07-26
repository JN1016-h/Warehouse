package com.utils;

import com.entity.EIException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SQLFilter unit tests.
 */
public class SQLFilterTest {

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSqlInject_blankReturnsNull() {
        assertNull(SQLFilter.sqlInject(null));
        assertNull(SQLFilter.sqlInject(""));
        assertNull(SQLFilter.sqlInject("   "));
    }

    @Test
    public void testSqlInject_stripsDangerousChars() {
        assertEquals("abc", SQLFilter.sqlInject("a'b\"c;\\"));
    }

    @Test
    public void testSqlInject_validColumnName() {
        assertEquals("create_time", SQLFilter.sqlInject("create_time"));
        assertEquals("name", SQLFilter.sqlInject("name"));
    }

    @Test
    public void testSqlInject_throwsOnKeyword() {
        EIException ex = assertThrows(EIException.class, () -> SQLFilter.sqlInject("select * from users"));
        assertEquals("包含非法字符", ex.getMsg());
    }

    @Test
    public void testSqlInject_keywordCaseInsensitive() {
        assertThrows(EIException.class, () -> SQLFilter.sqlInject("DELETE FROM t"));
        assertThrows(EIException.class, () -> SQLFilter.sqlInject("drop table x"));
    }
}
