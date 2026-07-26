package com.controller;

import com.entity.DinghuoxinxiEntity;
import com.entity.view.DinghuoxinxiView;
import com.service.DinghuoxinxiService;
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

public class DinghuoxinxiControllerTest {

    @Mock
    private DinghuoxinxiService dinghuoxinxiService;

    @InjectMocks
    private DinghuoxinxiController controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPageAsYonghu() {
        when(dinghuoxinxiService.queryPage(any(), any())).thenReturn(ControllerTestSupport.emptyPage());
        R result = controller.page(ControllerTestSupport.pageParams(), new DinghuoxinxiEntity(),
                ControllerTestSupport.mockRequestWithSession("yonghu", "user1", 1L, null));
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testList() {
        when(dinghuoxinxiService.queryPage(any(), any())).thenReturn(ControllerTestSupport.emptyPage());
        R result = controller.list(ControllerTestSupport.pageParams(), new DinghuoxinxiEntity(),
                ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testLists() {
        when(dinghuoxinxiService.selectListView(any())).thenReturn(Collections.emptyList());
        R result = controller.list(new DinghuoxinxiEntity());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testQuery() {
        when(dinghuoxinxiService.selectView(any())).thenReturn(new DinghuoxinxiView());
        R result = controller.query(new DinghuoxinxiEntity());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testInfo() {
        when(dinghuoxinxiService.selectById(1L)).thenReturn(new DinghuoxinxiEntity());
        R result = controller.info(1L);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testDetail() {
        when(dinghuoxinxiService.selectById(1L)).thenReturn(new DinghuoxinxiEntity());
        R result = controller.detail(1L);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testSave() {
        R result = controller.save(new DinghuoxinxiEntity(), ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testAdd() {
        DinghuoxinxiEntity entity = new DinghuoxinxiEntity();
        entity.setId(1L);
        R result = controller.add(entity, ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
        assertNotNull(result.get("data"));
    }

    @Test
    public void testUpdate() {
        R result = controller.update(new DinghuoxinxiEntity(), ControllerTestSupport.mockAdminRequest());
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
        row.put("total", 5);
        row.put("addtime", new Date());
        rows.add(row);
        when(dinghuoxinxiService.selectValue(any(), any())).thenReturn(rows);
        R result = controller.value("colY", "colX", ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testValueMul() throws Exception {
        when(dinghuoxinxiService.selectValue(any(), any())).thenReturn(Collections.emptyList());
        R result = controller.valueMul("colX", "colY1,colY2", ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testValueDay() throws Exception {
        when(dinghuoxinxiService.selectTimeStatValue(any(), any())).thenReturn(Collections.emptyList());
        R result = controller.valueDay("colY", "colX", "day", ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testValueMulDay() throws Exception {
        when(dinghuoxinxiService.selectTimeStatValue(any(), any())).thenReturn(Collections.emptyList());
        R result = controller.valueMulDay("colX", "day", "colY1,colY2", ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testGroup() throws Exception {
        when(dinghuoxinxiService.selectGroup(any(), any())).thenReturn(Collections.emptyList());
        R result = controller.group("status", ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testCount() {
        when(dinghuoxinxiService.selectCount(any())).thenReturn(3);
        R result = controller.count(ControllerTestSupport.pageParams(), new DinghuoxinxiEntity(),
                ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testPageWithNullUserId() {
        when(dinghuoxinxiService.queryPage(any(), any())).thenReturn(ControllerTestSupport.emptyPage());
        R result = controller.page(ControllerTestSupport.pageParams(), new DinghuoxinxiEntity(),
                ControllerTestSupport.mockRequestWithSession("yonghu", "user1", null, null));
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testSaveAsYonghu() {
        DinghuoxinxiEntity entity = new DinghuoxinxiEntity();
        entity.setKehumingcheng("客户A");
        R result = controller.save(entity,
                ControllerTestSupport.mockRequestWithSession("yonghu", "user1", 1L, "张三"));
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testValueAsYonghuWithDates() throws Exception {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Map<String, Object> row = new HashMap<String, Object>();
        row.put("total", 15);
        row.put("addtime", new Date());
        rows.add(row);
        when(dinghuoxinxiService.selectValue(any(), any())).thenReturn(rows);

        R result = controller.value("colY", "colX",
                ControllerTestSupport.mockRequestWithSession("yonghu", "user1", 1L, null));
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testValueMulAsYonghu() throws Exception {
        when(dinghuoxinxiService.selectValue(any(), any())).thenReturn(Collections.emptyList());
        R result = controller.valueMul("colX", "colY1,colY2",
                ControllerTestSupport.mockRequestWithSession("yonghu", "user1", 1L, null));
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testValueDayAsYonghu() throws Exception {
        when(dinghuoxinxiService.selectTimeStatValue(any(), any())).thenReturn(Collections.emptyList());
        R result = controller.valueDay("colY", "colX", "day",
                ControllerTestSupport.mockRequestWithSession("yonghu", "user1", 1L, null));
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testValueMulDayAsYonghu() throws Exception {
        when(dinghuoxinxiService.selectTimeStatValue(any(), any())).thenReturn(Collections.emptyList());
        R result = controller.valueMulDay("colX", "day", "colY1,colY2",
                ControllerTestSupport.mockRequestWithSession("yonghu", "user1", 1L, null));
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testGroupAsYonghu() throws Exception {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Map<String, Object> row = new HashMap<String, Object>();
        row.put("status", "active");
        row.put("addtime", new Date());
        rows.add(row);
        when(dinghuoxinxiService.selectGroup(any(), any())).thenReturn(rows);

        R result = controller.group("status",
                ControllerTestSupport.mockRequestWithSession("yonghu", "user1", 1L, null));
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testCountAsYonghu() {
        when(dinghuoxinxiService.selectCount(any())).thenReturn(5);
        R result = controller.count(ControllerTestSupport.pageParams(), new DinghuoxinxiEntity(),
                ControllerTestSupport.mockRequestWithSession("yonghu", "user1", 1L, null));
        assertEquals(0, result.get("code"));
    }
}
