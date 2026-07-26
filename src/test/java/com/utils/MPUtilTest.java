package com.utils;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MPUtil unit tests.
 */
public class MPUtilTest {

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCamelToUnderline() {
        assertEquals("", MPUtil.camelToUnderline(null));
        assertEquals("", MPUtil.camelToUnderline("   "));
        assertEquals("user_name", MPUtil.camelToUnderline("userName"));
        assertEquals("_a_b_cddf_a_n_m", MPUtil.camelToUnderline("ABCddfANM"));
    }

    @Test
    public void testCamelToUnderlineMap() {
        Map<String, Object> source = new HashMap<>();
        source.put("userName", "alice");
        source.put("orderId", 1L);

        Map result = MPUtil.camelToUnderlineMap(source, "");
        assertEquals("alice", result.get("user_name"));
        assertEquals(1L, result.get("order_id"));

        Map prefixed = MPUtil.camelToUnderlineMap(source, "t");
        assertEquals("alice", prefixed.get("t.user_name"));

        Map dotPrefixed = MPUtil.camelToUnderlineMap(source, "t.");
        assertEquals("alice", dotPrefixed.get("t.user_name"));
    }

    @Test
    public void testAllEQMap() {
        SampleEntity entity = new SampleEntity();
        entity.setUserName("bob");
        entity.setStatus("active");

        Map map = MPUtil.allEQMap(entity);
        assertEquals("bob", map.get("user_name"));
        assertEquals("active", map.get("status"));

        Map preMap = MPUtil.allEQMapPre(entity, "u");
        assertEquals("bob", preMap.get("u.user_name"));
    }

    @Test
    public void testGenLikeAndGenEq() {
        SampleEntity entity = new SampleEntity();
        entity.setUserName("alice");
        entity.setStatus("open");

        EntityWrapper<SampleEntity> wrapper = new EntityWrapper<>();
        MPUtil.allLike(wrapper, entity);
        assertNotNull(wrapper.getSqlSegment());

        EntityWrapper<SampleEntity> eqWrapper = new EntityWrapper<>();
        MPUtil.allEq(eqWrapper, entity);
        assertNotNull(eqWrapper.getSqlSegment());
    }

    @Test
    public void testGenLikeOrEq_percentUsesLike() {
        Map<String, Object> param = new HashMap<>();
        param.put("name", "%ali%");
        param.put("status", "open");

        EntityWrapper<SampleEntity> wrapper = new EntityWrapper<>();
        MPUtil.genLikeOrEq(wrapper, param);
        assertNotNull(wrapper.getSqlSegment());
    }

    @Test
    public void testBetweenAndSort() {
        EntityWrapper<SampleEntity> wrapper = new EntityWrapper<>();

        Map<String, Object> betweenParams = new HashMap<>();
        betweenParams.put("create_time_start", "2024-01-01");
        betweenParams.put("create_time_end", "2024-12-31");
        betweenParams.put("empty_start", "");
        MPUtil.between(wrapper, betweenParams);

        Map<String, Object> sortParams = new HashMap<>();
        sortParams.put("sort", "id");
        sortParams.put("order", "desc");
        MPUtil.sort2(new EntityWrapper<>(), sortParams);

        Map<String, Object> multiSort = new HashMap<>();
        multiSort.put("sort", "id,name");
        multiSort.put("order", "asc,desc");
        MPUtil.sort(new EntityWrapper<>(), multiSort);

        assertNotNull(wrapper.getSqlSegment());
    }

    @Test
    public void testLikeOrEqAndMain() {
        SampleEntity entity = new SampleEntity();
        entity.setUserName("%ali%");
        entity.setStatus("open");

        EntityWrapper<SampleEntity> wrapper = new EntityWrapper<>();
        MPUtil.likeOrEq(wrapper, entity);
        assertNotNull(wrapper.getSqlSegment());

        MPUtil.main(new String[] {});
    }

    @Test
    public void testSortWithMismatchedOrderSortSize() {
        Map<String, Object> params = new HashMap<>();
        params.put("sort", "id,name");
        params.put("order", "asc");
        MPUtil.sort(new EntityWrapper<>(), params);
    }

    @Test
    public void testBetweenOnlyEndKey() {
        EntityWrapper<SampleEntity> wrapper = new EntityWrapper<>();
        Map<String, Object> params = new HashMap<>();
        params.put("price_end", "100");
        MPUtil.between(wrapper, params);
        assertNotNull(wrapper.getSqlSegment());
    }

    @Test
    public void testGenLikeEmptyMap() {
        EntityWrapper<SampleEntity> wrapper = new EntityWrapper<>();
        MPUtil.genLike(wrapper, new HashMap<String, Object>());
        assertNotNull(wrapper);
    }

    @Test
    public void testAllLikePre() {
        SampleEntity entity = new SampleEntity();
        entity.setUserName("test");
        Wrapper<SampleEntity> wrapper = MPUtil.allLikePre(new EntityWrapper<>(), entity, "t");
        assertNotNull(wrapper);
    }

    @Test
    public void testSort2Desc() {
        Map<String, Object> params = new HashMap<>();
        params.put("sort", "id");
        params.put("order", "desc");
        MPUtil.sort2(new EntityWrapper<>(), params);
    }

    @Test
    public void testSort2Asc() {
        Map<String, Object> params = new HashMap<>();
        params.put("sort", "name");
        params.put("order", "asc");
        MPUtil.sort2(new EntityWrapper<>(), params);
    }

    @Test
    public void testSortMultiColumnDesc() {
        Map<String, Object> params = new HashMap<>();
        params.put("sort", "id,name");
        params.put("order", "desc,asc");
        MPUtil.sort(new EntityWrapper<>(), params);
    }

    @Test
    public void testGenLikeOrEqExactMatch() {
        Map<String, Object> param = new HashMap<>();
        param.put("status", "open");
        EntityWrapper<SampleEntity> wrapper = new EntityWrapper<>();
        MPUtil.genLikeOrEq(wrapper, param);
        assertNotNull(wrapper.getSqlSegment());
    }

    @Test
    public void testAllEq() {
        SampleEntity entity = new SampleEntity();
        entity.setUserName("alice");
        entity.setStatus("open");
        EntityWrapper<SampleEntity> wrapper = new EntityWrapper<>();
        MPUtil.allEq(wrapper, entity);
        assertNotNull(wrapper.getSqlSegment());
    }

    @Test
    public void testBetweenBlankStartValue() {
        EntityWrapper<SampleEntity> wrapper = new EntityWrapper<>();
        Map<String, Object> params = new HashMap<>();
        params.put("price_start", "  ");
        params.put("price_end", "100");
        MPUtil.between(wrapper, params);
        assertNotNull(wrapper.getSqlSegment());
    }

    @Test
    public void testCamelToUnderlineMapEmptyPre() {
        Map<String, Object> source = new HashMap<>();
        source.put("userName", "bob");
        Map result = MPUtil.camelToUnderlineMap(source, "");
        assertEquals("bob", result.get("user_name"));
    }

    @Test
    public void testSort2WithoutOrder() {
        Map<String, Object> params = new HashMap<>();
        params.put("sort", "id");
        MPUtil.sort2(new EntityWrapper<>(), params);
    }

    @Test
    public void testSortWithoutSortKey() {
        Map<String, Object> params = new HashMap<>();
        params.put("order", "asc");
        MPUtil.sort(new EntityWrapper<>(), params);
    }

    @Test
    public void testGenLikeMultipleEntries() {
        Map<String, Object> param = new HashMap<>();
        param.put("name", "ali");
        param.put("status", "open");
        EntityWrapper<SampleEntity> wrapper = new EntityWrapper<>();
        MPUtil.genLike(wrapper, param);
        assertNotNull(wrapper.getSqlSegment());
    }

    @Test
    public void testSort2WithoutSortKey() {
        Map<String, Object> params = new HashMap<>();
        params.put("order", "desc");
        MPUtil.sort2(new EntityWrapper<>(), params);
    }

    @Test
    public void testSortWithNullOrderAndSort() {
        MPUtil.sort(new EntityWrapper<>(), new HashMap<String, Object>());
    }

    @Test
    public void testSortAscBranch() {
        Map<String, Object> params = new HashMap<>();
        params.put("sort", "name");
        params.put("order", "ASC");
        MPUtil.sort(new EntityWrapper<>(), params);
    }

    @Test
    public void testBetweenOnlyStartKeyBlank() {
        EntityWrapper<SampleEntity> wrapper = new EntityWrapper<>();
        Map<String, Object> params = new HashMap<>();
        params.put("price_start", "");
        MPUtil.between(wrapper, params);
        assertNotNull(wrapper);
    }

    @Test
    public void testGenEqMultipleEntries() {
        Map<String, Object> param = new HashMap<>();
        param.put("status", "open");
        param.put("name", "bob");
        EntityWrapper<SampleEntity> wrapper = new EntityWrapper<>();
        MPUtil.genEq(wrapper, param);
        assertNotNull(wrapper.getSqlSegment());
    }

    public static class SampleEntity {
        private String userName;
        private String status;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
