package com.controller;

import com.service.CommonService;
import com.service.ConfigService;
import com.utils.R;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CommonControllerTest {

    @Mock
    private CommonService commonService;

    @Mock
    private ConfigService configService;

    @InjectMocks
    private CommonController controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetOptionBasic() {
        when(commonService.getOption(any())).thenReturn(Arrays.asList("A", "B"));
        R result = controller.getOption("yonghu", "yonghuxingming", null, null, null, null);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testGetOptionWithFilters() {
        when(commonService.getOption(any())).thenReturn(Collections.singletonList("X"));
        R result = controller.getOption("yonghu", "yonghuxingming", "id", "1", "1", "0");
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testGetFollowByOption() {
        Map<String, Object> row = new HashMap<String, Object>();
        row.put("id", 1L);
        row.put("yonghuzhanghao", "user1");
        when(commonService.getFollowByOption(any())).thenReturn(row);
        R result = controller.getFollowByOption("yonghu", "yonghuzhanghao", "user1");
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testSh() {
        R result = controller.sh("yonghu", new HashMap<String, Object>());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testRemindCountType1() {
        when(commonService.remindCount(any())).thenReturn(4);
        Map<String, Object> map = new HashMap<String, Object>();
        R result = controller.remindCount("yonghu", "addtime", "1", map);
        assertEquals(0, result.get("code"));
        assertEquals(4, result.get("count"));
    }

    @Test
    public void testRemindCountType2() {
        when(commonService.remindCount(any())).thenReturn(2);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("remindstart", "1");
        map.put("remindend", "5");
        R result = controller.remindCount("yonghu", "addtime", "2", map);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testCal() {
        Map<String, Object> cal = new HashMap<String, Object>();
        cal.put("sum", 100);
        when(commonService.selectCal(any())).thenReturn(cal);
        R result = controller.cal("yonghu", "money");
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testGroup() {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Map<String, Object> row = new HashMap<String, Object>();
        row.put("addtime", new Date());
        rows.add(row);
        when(commonService.selectGroup(any())).thenReturn(rows);
        R result = controller.group("yonghu", "status");
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testValue() {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Map<String, Object> row = new HashMap<String, Object>();
        row.put("addtime", new Date());
        rows.add(row);
        when(commonService.selectValue(any())).thenReturn(rows);
        R result = controller.value("yonghu", "colY", "colX");
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testValueDay() {
        when(commonService.selectTimeStatValue(any())).thenReturn(Collections.emptyList());
        R result = controller.valueDay("yonghu", "colY", "colX", "day");
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testGetOptionEmptyFilters() {
        when(commonService.getOption(any())).thenReturn(Collections.emptyList());
        R result = controller.getOption("yonghu", "col", "", "", "", "");
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testRemindCountEmptyMap() {
        when(commonService.remindCount(any())).thenReturn(0);
        R result = controller.remindCount("yonghu", "addtime", "1", new HashMap<String, Object>());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testRemindCountType2OnlyEnd() {
        when(commonService.remindCount(any())).thenReturn(1);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("remindend", "3");
        R result = controller.remindCount("yonghu", "addtime", "2", map);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testGroupNonDateField() {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Map<String, Object> row = new HashMap<String, Object>();
        row.put("status", "ok");
        rows.add(row);
        when(commonService.selectGroup(any())).thenReturn(rows);
        R result = controller.group("yonghu", "status");
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testRemindCountType2OnlyStart() {
        when(commonService.remindCount(any())).thenReturn(2);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("remindstart", "2");
        R result = controller.remindCount("yonghu", "addtime", "2", map);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testCalEmptyResult() {
        when(commonService.selectCal(any())).thenReturn(new HashMap<String, Object>());
        R result = controller.cal("yonghu", "money");
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testValueDayWithDateFields() {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Map<String, Object> row = new HashMap<String, Object>();
        row.put("addtime", new Date());
        row.put("total", 5);
        rows.add(row);
        when(commonService.selectTimeStatValue(any())).thenReturn(rows);

        R result = controller.valueDay("yonghu", "colY", "colX", "day");
        assertEquals(0, result.get("code"));
        assertNotNull(result.get("data"));
    }

    @Test
    public void testValueDayMixedFieldTypes() {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        Map<String, Object> row = new HashMap<String, Object>();
        row.put("status", "ok");
        row.put("created", new Date());
        rows.add(row);
        when(commonService.selectTimeStatValue(any())).thenReturn(rows);

        R result = controller.valueDay("yonghu", "colY", "colX", "month");
        assertEquals(0, result.get("code"));
    }
}
