package com.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MapUtils unit tests.
 */
public class MapUtilsTest {

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPutChaining() {
        MapUtils map = new MapUtils();
        MapUtils result = map.put("a", 1).put("b", "two");
        assertSame(map, result);
        assertEquals(1, map.get("a"));
        assertEquals("two", map.get("b"));
    }

    @Test
    public void testObjectToMap() throws IllegalAccessException {
        SampleBean bean = new SampleBean();
        bean.setName("item");
        bean.setCount(3);

        Map<String, Object> map = MapUtils.objectToMap(bean);
        assertEquals("item", map.get("name"));
        assertEquals(3, map.get("count"));
    }

    @Test
    public void testMapToObject() throws IllegalAccessException, InstantiationException {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "mapped");
        map.put("count", 7);
        map.put("unknownField", "ignored");

        SampleBean bean = MapUtils.mapToObject(map, SampleBean.class);
        assertEquals("mapped", bean.getName());
        assertEquals(7, bean.getCount());
    }

    public static class SampleBean {
        private String name;
        private int count;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }
}
