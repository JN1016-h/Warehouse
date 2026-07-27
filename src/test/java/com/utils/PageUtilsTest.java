package com.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PageUtils unit tests.
 */
public class PageUtilsTest {

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testConstructorWithList() {
        List<String> data = Arrays.asList("a", "b");
        PageUtils page = new PageUtils(data, 25, 10, 2);

        assertEquals(data, page.getList());
        assertEquals(25, page.getTotal());
        assertEquals(10, page.getPageSize());
        assertEquals(2, page.getCurrPage());
        assertEquals(3, page.getTotalPage());
    }

    @Test
    public void testConstructorWithMybatisPage() {
        Page<String> mpPage = new Page<>(2, 5);
        mpPage.setRecords(Arrays.asList("x", "y"));
        mpPage.setTotal(12);

        PageUtils page = new PageUtils(mpPage);
        assertEquals(2, page.getList().size());
        assertEquals(12, page.getTotal());
        assertEquals(5, page.getPageSize());
        assertEquals(2, page.getCurrPage());
    }

    @Test
    public void testConstructorWithParamsMap() {
        Map<String, Object> params = new HashMap<>();
        params.put("page", "1");
        params.put("limit", "10");
        params.put("sidx", "id");
        params.put("order", "asc");

        PageUtils page = new PageUtils(params);
        assertNotNull(page);
    }

    @Test
    public void testSettersAndGetters() {
        PageUtils page = new PageUtils(Arrays.asList("z"), 1, 1, 1);
        page.setTotal(99);
        page.setPageSize(20);
        page.setCurrPage(3);
        page.setTotalPage(5);
        page.setList(Arrays.asList("new"));

        assertEquals(99, page.getTotal());
        assertEquals(20, page.getPageSize());
        assertEquals(3, page.getCurrPage());
        assertEquals(5, page.getTotalPage());
        assertEquals(1, page.getList().size());
    }
}
