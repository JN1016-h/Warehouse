package com.controller;

import com.entity.ShangpinxinxiEntity;
import com.entity.view.ShangpinxinxiView;
import com.service.BuhuotixingService;
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

public class ShangpinxinxiControllerTest {

    @Mock
    private ShangpinxinxiService shangpinxinxiService;

    @Mock
    private BuhuotixingService buhuotixingService;

    @InjectMocks
    private ShangpinxinxiController controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPage() {
        when(shangpinxinxiService.queryPage(any(), any())).thenReturn(ControllerTestSupport.emptyPage());
        R result = controller.page(ControllerTestSupport.pageParams(), new ShangpinxinxiEntity(),
                ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testList() {
        when(shangpinxinxiService.queryPage(any(), any())).thenReturn(ControllerTestSupport.emptyPage());
        R result = controller.list(ControllerTestSupport.pageParams(), new ShangpinxinxiEntity(),
                ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testLists() {
        when(shangpinxinxiService.selectListView(any())).thenReturn(Collections.emptyList());
        R result = controller.list(new ShangpinxinxiEntity());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testQuery() {
        when(shangpinxinxiService.selectView(any())).thenReturn(new ShangpinxinxiView());
        R result = controller.query(new ShangpinxinxiEntity());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testInfo() {
        when(shangpinxinxiService.selectById(1L)).thenReturn(new ShangpinxinxiEntity());
        R result = controller.info(1L);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testDetail() {
        when(shangpinxinxiService.selectById(1L)).thenReturn(new ShangpinxinxiEntity());
        R result = controller.detail(1L);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testSave() {
        R result = controller.save(new ShangpinxinxiEntity(), ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testAdd() {
        ShangpinxinxiEntity entity = new ShangpinxinxiEntity();
        entity.setId(1L);
        R result = controller.add(entity, ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
        assertNotNull(result.get("data"));
    }

    @Test
    public void testUpdateWithAlertCheck() {
        ShangpinxinxiEntity entity = new ShangpinxinxiEntity();
        entity.setFuzhuangbianhao("SP001");
        R result = controller.update(entity,
                ControllerTestSupport.mockRequestWithSession("yonghu", "user1", 1L, null));
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
        row.put("total", 8);
        row.put("addtime", new Date());
        rows.add(row);
        when(shangpinxinxiService.selectValue(any(), any())).thenReturn(rows);
        R result = controller.value("colY", "colX", ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testValueMul() throws Exception {
        when(shangpinxinxiService.selectValue(any(), any())).thenReturn(Collections.emptyList());
        R result = controller.valueMul("colX", "colY1,colY2", ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testValueDay() throws Exception {
        when(shangpinxinxiService.selectTimeStatValue(any(), any())).thenReturn(Collections.emptyList());
        R result = controller.valueDay("colY", "colX", "day", ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testValueMulDay() throws Exception {
        when(shangpinxinxiService.selectTimeStatValue(any(), any())).thenReturn(Collections.emptyList());
        R result = controller.valueMulDay("colX", "day", "colY1,colY2", ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testGroup() throws Exception {
        when(shangpinxinxiService.selectGroup(any(), any())).thenReturn(Collections.emptyList());
        R result = controller.group("status", ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testCount() {
        when(shangpinxinxiService.selectCount(any())).thenReturn(12);
        R result = controller.count(ControllerTestSupport.pageParams(), new ShangpinxinxiEntity(),
                ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testPageAsYonghuWithNullSessionAttrs() {
        when(shangpinxinxiService.queryPage(any(), any())).thenReturn(ControllerTestSupport.emptyPage());
        R result = controller.page(ControllerTestSupport.pageParams(), new ShangpinxinxiEntity(),
                ControllerTestSupport.mockRequestWithSession("yonghu", "user1", null, null));
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testSaveAsYonghu() {
        ShangpinxinxiEntity entity = new ShangpinxinxiEntity();
        entity.setFuzhuangbianhao("SP002");
        R result = controller.save(entity,
                ControllerTestSupport.mockRequestWithSession("yonghu", "user1", 1L, "张三"));
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testUpdateWithoutSkuNoAlert() {
        ShangpinxinxiEntity entity = new ShangpinxinxiEntity();
        entity.setId(1L);
        R result = controller.update(entity, ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testUpdateAsYonghuTriggersAlert() {
        ShangpinxinxiEntity entity = new ShangpinxinxiEntity();
        entity.setId(1L);
        entity.setFuzhuangbianhao("SP001");
        R result = controller.update(entity,
                ControllerTestSupport.mockRequestWithSession("yonghu", "user1", 1L, null));
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testValueWithDateFormatting() throws Exception {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Map<String, Object> row = new HashMap<String, Object>();
        row.put("total", 100);
        row.put("addtime", new Date());
        rows.add(row);
        when(shangpinxinxiService.selectValue(any(), any())).thenReturn(rows);
        R result = controller.value("colY", "colX", ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testValueMulDayWithDates() throws Exception {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Map<String, Object> row = new HashMap<String, Object>();
        row.put("total", 50);
        row.put("addtime", new Date());
        rows.add(row);
        when(shangpinxinxiService.selectTimeStatValue(any(), any())).thenReturn(rows);
        R result = controller.valueMulDay("colX", "day", "colY1,colY2", ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testGroupWithDates() throws Exception {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Map<String, Object> row = new HashMap<String, Object>();
        row.put("status", "ok");
        row.put("addtime", new Date());
        rows.add(row);
        when(shangpinxinxiService.selectGroup(any(), any())).thenReturn(rows);
        R result = controller.group("status", ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }
}
