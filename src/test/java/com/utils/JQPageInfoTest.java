package com.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JQPageInfo unit tests.
 */
public class JQPageInfoTest {

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGettersAndSetters() {
        JQPageInfo info = new JQPageInfo();
        info.setPage(2);
        info.setLimit(20);
        info.setSidx("id");
        info.setOrder("desc");
        info.setOffset(20);

        assertEquals(2, info.getPage());
        assertEquals(20, info.getLimit());
        assertEquals("id", info.getSidx());
        assertEquals("desc", info.getOrder());
        assertEquals(20, info.getOffset());
    }
}
