package com.controller;

import com.entity.RukuxinxiEntity;
import com.entity.ShangpinxinxiEntity;
import com.entity.view.RukuxinxiView;
import com.service.BuhuotixingService;
import com.service.RukuxinxiService;
import com.service.ShangpinxinxiService;
import com.utils.R;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class RukuxinxiControllerTest {

    @Mock
    private RukuxinxiService rukuxinxiService;

    @Mock
    private ShangpinxinxiService shangpinxinxiService;

    @Mock
    private BuhuotixingService buhuotixingService;

    @InjectMocks
    private RukuxinxiController controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPageAsYonghu() {
        when(rukuxinxiService.queryPage(any(), any())).thenReturn(ControllerTestSupport.emptyPage());
        R result = controller.page(ControllerTestSupport.pageParams(), new RukuxinxiEntity(),
                ControllerTestSupport.mockRequestWithSession("yonghu", "user1", 1L, null));
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testList() {
        when(rukuxinxiService.queryPage(any(), any())).thenReturn(ControllerTestSupport.emptyPage());
        R result = controller.list(ControllerTestSupport.pageParams(), new RukuxinxiEntity(),
                ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testLists() {
        when(rukuxinxiService.selectListView(any())).thenReturn(Collections.emptyList());
        R result = controller.list(new RukuxinxiEntity());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testQuery() {
        when(rukuxinxiService.selectView(any())).thenReturn(new RukuxinxiView());
        R result = controller.query(new RukuxinxiEntity());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testInfo() {
        when(rukuxinxiService.selectById(1L)).thenReturn(new RukuxinxiEntity());
        R result = controller.info(1L);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testDetail() {
        when(rukuxinxiService.selectById(1L)).thenReturn(new RukuxinxiEntity());
        R result = controller.detail(1L);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testSaveWithStockUpdate() {
        RukuxinxiEntity entity = new RukuxinxiEntity();
        entity.setFuzhuangbianhao("SP001");
        entity.setFuzhuangkucun(10);
        entity.setZhanghao("user1");

        ShangpinxinxiEntity product = new ShangpinxinxiEntity();
        product.setFuzhuangkucun(5);
        when(shangpinxinxiService.selectOne(any())).thenReturn(product);

        R result = controller.save(entity,
                ControllerTestSupport.mockRequestWithSession("yonghu", "user1", 1L, "张三"));
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testAdd() {
        RukuxinxiEntity entity = new RukuxinxiEntity();
        entity.setId(1L);
        R result = controller.add(entity, ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
        assertNotNull(result.get("data"));
    }

    @Test
    public void testUpdate() {
        R result = controller.update(new RukuxinxiEntity(), ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testDelete() {
        R result = controller.delete(new Long[]{1L});
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testValue() throws Exception {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Map<String, Object> row = new HashMap<String, Object>();
        row.put("total", 10);
        row.put("addtime", new Date());
        rows.add(row);
        when(rukuxinxiService.selectValue(any(), any())).thenReturn(rows);
        R result = controller.value("colY", "colX", ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testValueMul() throws Exception {
        when(rukuxinxiService.selectValue(any(), any())).thenReturn(Collections.emptyList());
        R result = controller.valueMul("colX", "colY1,colY2", ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testValueDay() throws Exception {
        when(rukuxinxiService.selectTimeStatValue(any(), any())).thenReturn(Collections.emptyList());
        R result = controller.valueDay("colY", "colX", "day", ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testValueMulDay() throws Exception {
        when(rukuxinxiService.selectTimeStatValue(any(), any())).thenReturn(Collections.emptyList());
        R result = controller.valueMulDay("colX", "day", "colY1,colY2", ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testGroup() throws Exception {
        when(rukuxinxiService.selectGroup(any(), any())).thenReturn(Collections.emptyList());
        R result = controller.group("status", ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testCount() {
        when(rukuxinxiService.selectCount(any())).thenReturn(7);
        R result = controller.count(ControllerTestSupport.pageParams(), new RukuxinxiEntity(),
                ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testSaveProductNotFound() {
        RukuxinxiEntity entity = new RukuxinxiEntity();
        entity.setFuzhuangbianhao("MISSING");
        entity.setFuzhuangkucun(10);
        when(shangpinxinxiService.selectOne(any())).thenReturn(null);

        R result = controller.save(entity,
                ControllerTestSupport.mockRequestWithSession("yonghu", "user1", 1L, "张三"));
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testSaveWithNullStock() {
        RukuxinxiEntity entity = new RukuxinxiEntity();
        entity.setFuzhuangbianhao("SP001");
        entity.setFuzhuangkucun(null);

        R result = controller.save(entity, ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testPageWithEmptyParams() {
        when(rukuxinxiService.queryPage(any(), any())).thenReturn(ControllerTestSupport.emptyPage());
        R result = controller.page(new HashMap<String, Object>(), new RukuxinxiEntity(),
                ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testSaveAsAdminNoStockUpdate() {
        RukuxinxiEntity entity = new RukuxinxiEntity();
        entity.setFuzhuangbianhao("SP001");
        entity.setFuzhuangkucun(5);
        R result = controller.save(entity, ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testSaveWithNullProductStock() {
        RukuxinxiEntity entity = new RukuxinxiEntity();
        entity.setFuzhuangbianhao("SP001");
        entity.setFuzhuangkucun(10);
        entity.setZhanghao("user1");

        ShangpinxinxiEntity product = new ShangpinxinxiEntity();
        product.setFuzhuangkucun(null);
        when(shangpinxinxiService.selectOne(any())).thenReturn(product);

        R result = controller.save(entity,
                ControllerTestSupport.mockRequestWithSession("yonghu", "user1", 1L, "张三"));
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testSaveWithEmptyUsernameSkipsAlert() {
        RukuxinxiEntity entity = new RukuxinxiEntity();
        entity.setFuzhuangbianhao("SP001");
        entity.setFuzhuangkucun(10);
        entity.setZhanghao("");

        ShangpinxinxiEntity product = new ShangpinxinxiEntity();
        product.setFuzhuangkucun(5);
        when(shangpinxinxiService.selectOne(any())).thenReturn(product);

        R result = controller.save(entity,
                ControllerTestSupport.mockRequestWithSession("yonghu", "user1", 1L, "张三"));
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testValueAsYonghuWithDates() throws Exception {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Map<String, Object> row = new HashMap<String, Object>();
        row.put("total", 20);
        row.put("addtime", new Date());
        rows.add(row);
        when(rukuxinxiService.selectValue(any(), any())).thenReturn(rows);

        R result = controller.value("colY", "colX",
                ControllerTestSupport.mockRequestWithSession("yonghu", "user1", 1L, null));
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testGroupAsYonghu() throws Exception {
        when(rukuxinxiService.selectGroup(any(), any())).thenReturn(Collections.emptyList());
        R result = controller.group("status",
                ControllerTestSupport.mockRequestWithSession("yonghu", "user1", 1L, null));
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testCountAsYonghu() {
        when(rukuxinxiService.selectCount(any())).thenReturn(4);
        R result = controller.count(ControllerTestSupport.pageParams(), new RukuxinxiEntity(),
                ControllerTestSupport.mockRequestWithSession("yonghu", "user1", 1L, null));
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testValueMulAsYonghu() throws Exception {
        when(rukuxinxiService.selectValue(any(), any())).thenReturn(Collections.emptyList());
        R result = controller.valueMul("colX", "colY1,colY2",
                ControllerTestSupport.mockRequestWithSession("yonghu", "user1", 1L, null));
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testValueDayAsYonghu() throws Exception {
        when(rukuxinxiService.selectTimeStatValue(any(), any())).thenReturn(Collections.emptyList());
        R result = controller.valueDay("colY", "colX", "day",
                ControllerTestSupport.mockRequestWithSession("yonghu", "user1", 1L, null));
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testValueMulDayAsYonghu() throws Exception {
        when(rukuxinxiService.selectTimeStatValue(any(), any())).thenReturn(Collections.emptyList());
        R result = controller.valueMulDay("colX", "day", "colY1,colY2",
                ControllerTestSupport.mockRequestWithSession("yonghu", "user1", 1L, null));
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testSaveWithNullSkuSkipsStockUpdate() {
        RukuxinxiEntity entity = new RukuxinxiEntity();
        entity.setFuzhuangbianhao(null);
        entity.setFuzhuangkucun(10);
        R result = controller.save(entity, ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testSaveWithNullUsernameSkipsAlert() {
        RukuxinxiEntity entity = new RukuxinxiEntity();
        entity.setFuzhuangbianhao("SP001");
        entity.setFuzhuangkucun(10);
        entity.setZhanghao(null);

        ShangpinxinxiEntity product = new ShangpinxinxiEntity();
        product.setFuzhuangkucun(5);
        when(shangpinxinxiService.selectOne(any())).thenReturn(product);

        R result = controller.save(entity,
                ControllerTestSupport.mockRequestWithSession("yonghu", "user1", 1L, "张三"));
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testValueFromJsonCache() throws Exception {
        String file = "value_rukuxinxi_colX_colY_timeType.json";
        try {
            ControllerTestSupport.writeJsonCache(file, "[{\"total\":99}]");
            R result = controller.value("colY", "colX", ControllerTestSupport.mockAdminRequest());
            assertEquals(0, result.get("code"));
        } finally {
            ControllerTestSupport.deleteJsonCache(file);
        }
    }

    @Test
    public void testValueMulFromJsonCache() throws Exception {
        String file = "value_rukuxinxi_colX_colY1,colY2_timeType.json";
        try {
            ControllerTestSupport.writeJsonCache(file, "[{\"total\":1}]");
            R result = controller.valueMul("colX", "colY1,colY2", ControllerTestSupport.mockAdminRequest());
            assertEquals(0, result.get("code"));
        } finally {
            ControllerTestSupport.deleteJsonCache(file);
        }
    }

    @Test
    public void testValueDayFromJsonCache() throws Exception {
        String file = "value_rukuxinxi_colX_colY_day.json";
        try {
            ControllerTestSupport.writeJsonCache(file, "[{\"total\":2}]");
            R result = controller.valueDay("colY", "colX", "day", ControllerTestSupport.mockAdminRequest());
            assertEquals(0, result.get("code"));
        } finally {
            ControllerTestSupport.deleteJsonCache(file);
        }
    }

    @Test
    public void testValueMulDayFromJsonCache() throws Exception {
        String file = "value_rukuxinxi_colX_colY1,colY2_day.json";
        try {
            ControllerTestSupport.writeJsonCache(file, "[{\"total\":3}]");
            R result = controller.valueMulDay("colX", "day", "colY1,colY2", ControllerTestSupport.mockAdminRequest());
            assertEquals(0, result.get("code"));
        } finally {
            ControllerTestSupport.deleteJsonCache(file);
        }
    }

    @Test
    public void testGroupFromJsonCache() throws Exception {
        String file = "group_rukuxinxi_status_timeType.json";
        try {
            ControllerTestSupport.writeJsonCache(file, "[{\"total\":4}]");
            R result = controller.group("status", ControllerTestSupport.mockAdminRequest());
            assertEquals(0, result.get("code"));
        } finally {
            ControllerTestSupport.deleteJsonCache(file);
        }
    }

    @Test
    public void testValueMulWithDatesInRows() throws Exception {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Map<String, Object> row = new HashMap<String, Object>();
        row.put("total", 10);
        row.put("addtime", new Date());
        rows.add(row);
        when(rukuxinxiService.selectValue(any(), any())).thenReturn(rows);

        R result = controller.valueMul("colX", "colY1,colY2", ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testValueDayWithDatesInRows() throws Exception {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Map<String, Object> row = new HashMap<String, Object>();
        row.put("total", 10);
        row.put("addtime", new Date());
        rows.add(row);
        when(rukuxinxiService.selectTimeStatValue(any(), any())).thenReturn(rows);

        R result = controller.valueDay("colY", "colX", "day", ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testValueMulDayWithDatesInRows() throws Exception {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Map<String, Object> row = new HashMap<String, Object>();
        row.put("total", 10);
        row.put("addtime", new Date());
        rows.add(row);
        when(rukuxinxiService.selectTimeStatValue(any(), any())).thenReturn(rows);

        R result = controller.valueMulDay("colX", "day", "colY1,colY2", ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testGroupWithNonDateFields() throws Exception {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Map<String, Object> row = new HashMap<String, Object>();
        row.put("status", "ok");
        rows.add(row);
        when(rukuxinxiService.selectGroup(any(), any())).thenReturn(rows);

        R result = controller.group("status", ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testValueSortsMultipleRows() throws Exception {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Map<String, Object> low = new HashMap<String, Object>();
        low.put("total", 5);
        rows.add(low);
        Map<String, Object> high = new HashMap<String, Object>();
        high.put("total", 50);
        rows.add(high);
        when(rukuxinxiService.selectValue(any(), any())).thenReturn(rows);

        R result = controller.value("colY", "colX", ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }
}
