package com.controller;

import com.entity.BuhuotixingEntity;
import com.entity.view.BuhuotixingView;
import com.service.BuhuotixingService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class BuhuotixingControllerTest {

    @Mock
    private BuhuotixingService buhuotixingService;

    @InjectMocks
    private BuhuotixingController controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPageAsYonghu() {
        when(buhuotixingService.queryPage(any(), any())).thenReturn(ControllerTestSupport.emptyPage());
        R result = controller.page(ControllerTestSupport.pageParams(), new BuhuotixingEntity(),
                ControllerTestSupport.mockRequestWithSession("yonghu", "user1", 1L, null));
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testList() {
        when(buhuotixingService.queryPage(any(), any())).thenReturn(ControllerTestSupport.emptyPage());
        R result = controller.list(ControllerTestSupport.pageParams(), new BuhuotixingEntity(),
                ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testLists() {
        when(buhuotixingService.selectListView(any())).thenReturn(Collections.emptyList());
        R result = controller.list(new BuhuotixingEntity());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testQuery() {
        when(buhuotixingService.selectView(any())).thenReturn(new BuhuotixingView());
        R result = controller.query(new BuhuotixingEntity());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testInfo() {
        when(buhuotixingService.selectById(1L)).thenReturn(new BuhuotixingEntity());
        R result = controller.info(1L);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testDetail() {
        when(buhuotixingService.selectById(1L)).thenReturn(new BuhuotixingEntity());
        R result = controller.detail(1L);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testSaveSetsDefaultStatus() {
        BuhuotixingEntity entity = new BuhuotixingEntity();
        R result = controller.save(entity, ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
        assertEquals("待处理", entity.getTixingzhuangtai());
    }

    @Test
    public void testAdd() {
        R result = controller.add(new BuhuotixingEntity(), ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testUpdate() {
        R result = controller.update(new BuhuotixingEntity(), ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testDelete() {
        R result = controller.delete(new Long[]{1L});
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testRemindCountType1() {
        when(buhuotixingService.selectCount(any())).thenReturn(3);
        Map<String, Object> map = new HashMap<String, Object>();
        R result = controller.remindCount("addtime", ControllerTestSupport.mockAdminRequest(), "1", map);
        assertEquals(0, result.get("code"));
        assertEquals(3, result.get("count"));
    }

    @Test
    public void testRemindCountType2() {
        when(buhuotixingService.selectCount(any())).thenReturn(2);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("remindstart", "1");
        map.put("remindend", "7");
        R result = controller.remindCount("addtime", ControllerTestSupport.mockAdminRequest(), "2", map);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testComplete() {
        R result = controller.complete(1L);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testCancel() {
        R result = controller.cancel(1L);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testCount() {
        when(buhuotixingService.selectCount(any())).thenReturn(5);
        R result = controller.count(ControllerTestSupport.pageParams(), new BuhuotixingEntity());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testValue() {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Map<String, Object> row = new HashMap<String, Object>();
        row.put("addtime", new Date());
        rows.add(row);
        when(buhuotixingService.selectValue(any(), any())).thenReturn(rows);
        R result = controller.value("colY", "colX", ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testValueDay() {
        when(buhuotixingService.selectTimeStatValue(any(), any())).thenReturn(Collections.emptyList());
        R result = controller.valueDay("colY", "colX", "day", ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testGroup() {
        when(buhuotixingService.selectGroup(any(), any())).thenReturn(Collections.emptyList());
        R result = controller.group("status", ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testPageWithNullSession() {
        when(buhuotixingService.queryPage(any(), any())).thenReturn(ControllerTestSupport.emptyPage());
        R result = controller.page(ControllerTestSupport.pageParams(), new BuhuotixingEntity(),
                ControllerTestSupport.mockRequestWithSession("yonghu", "user1", null, null));
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testSaveWithExistingStatus() {
        BuhuotixingEntity entity = new BuhuotixingEntity();
        entity.setTixingzhuangtai("已完成");
        R result = controller.save(entity, ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
        assertEquals("已完成", entity.getTixingzhuangtai());
    }

    @Test
    public void testRemindCountType2EmptyMap() {
        when(buhuotixingService.selectCount(any())).thenReturn(1);
        R result = controller.remindCount("addtime", ControllerTestSupport.mockAdminRequest(), "2",
                new HashMap<String, Object>());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testPageAsAdmin() {
        when(buhuotixingService.queryPage(any(), any())).thenReturn(ControllerTestSupport.emptyPage());
        R result = controller.page(ControllerTestSupport.pageParams(), new BuhuotixingEntity(),
                ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testAddSetsDefaultStatus() {
        BuhuotixingEntity entity = new BuhuotixingEntity();
        R result = controller.add(entity, ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
        assertEquals("待处理", entity.getTixingzhuangtai());
    }

    @Test
    public void testRemindCountType2OnlyStart() {
        when(buhuotixingService.selectCount(any())).thenReturn(2);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("remindstart", "3");
        R result = controller.remindCount("addtime", ControllerTestSupport.mockAdminRequest(), "2", map);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testRemindCountType2OnlyEnd() {
        when(buhuotixingService.selectCount(any())).thenReturn(1);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("remindend", "7");
        R result = controller.remindCount("addtime", ControllerTestSupport.mockAdminRequest(), "2", map);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testValueNonDateFields() {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Map<String, Object> row = new HashMap<String, Object>();
        row.put("total", 9);
        row.put("name", "label");
        rows.add(row);
        when(buhuotixingService.selectValue(any(), any())).thenReturn(rows);
        R result = controller.value("colY", "colX", ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testRemindCountType1WithRange() {
        when(buhuotixingService.selectCount(any())).thenReturn(4);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("remindstart", "2025-01-01");
        map.put("remindend", "2025-12-31");
        R result = controller.remindCount("addtime", ControllerTestSupport.mockAdminRequest(), "1", map);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testValueDayWithDates() {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Map<String, Object> row = new HashMap<String, Object>();
        row.put("addtime", new Date());
        rows.add(row);
        when(buhuotixingService.selectTimeStatValue(any(), any())).thenReturn(rows);
        R result = controller.valueDay("colY", "colX", "day", ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testGroupWithDates() {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Map<String, Object> row = new HashMap<String, Object>();
        row.put("addtime", new Date());
        rows.add(row);
        when(buhuotixingService.selectGroup(any(), any())).thenReturn(rows);
        R result = controller.group("status", ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }
}
