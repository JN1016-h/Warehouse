package com.utils;

import com.baomidou.mybatisplus.plugins.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Query unit tests.
 */
public class QueryTest {

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testQueryFromJQPageInfo() {
        JQPageInfo info = new JQPageInfo();
        info.setPage(2);
        info.setLimit(15);
        info.setSidx("id");
        info.setOrder("DESC");

        Query<Object> query = new Query<>(info);
        assertEquals(2, query.getCurrPage());
        assertEquals(15, query.getLimit());

        Page<Object> page = query.getPage();
        assertNotNull(page);
        assertEquals(2, page.getCurrent());
        assertEquals(15, page.getSize());
        assertEquals("id", page.getOrderByField());
        assertFalse(page.isAsc());
    }

    @Test
    public void testQueryFromJQPageInfoAscOrder() {
        JQPageInfo info = new JQPageInfo();
        info.setSidx("name");
        info.setOrder("asc");

        Query<Object> query = new Query<>(info);
        assertTrue(query.getPage().isAsc());
    }

    @Test
    public void testQueryFromMapParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("page", "3");
        params.put("limit", "20");
        params.put("sidx", "create_time");
        params.put("order", "asc");
        params.put("keyword", "box");

        Query<String> query = new Query<>(params);
        assertEquals(3, query.getCurrPage());
        assertEquals(20, query.getLimit());
        assertEquals(40, query.get("offset"));
        assertEquals(3, query.get("page"));
        assertEquals(20, query.get("limit"));
        assertEquals("create_time", query.get("sidx"));
        assertEquals("asc", query.get("order"));
        assertEquals("box", query.get("keyword"));
        assertTrue(query.getPage().isAsc());
    }

    @Test
    public void testQueryDefaultsWhenPageInfoNullFields() {
        JQPageInfo info = new JQPageInfo();
        Query<Object> query = new Query<>(info);
        assertEquals(1, query.getCurrPage());
        assertEquals(10, query.getLimit());
    }
}
