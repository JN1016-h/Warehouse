package com.service;

import com.dao.CommonDao;
import com.service.impl.CommonServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CommonServiceImplTest {

    @Mock
    private CommonDao commonDao;

    @InjectMocks
    private CommonServiceImpl service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getOptionDelegates() {
        when(commonDao.getOption(any())).thenReturn(Arrays.asList("A", "B"));
        assertEquals(2, service.getOption(new HashMap<String, Object>()).size());
        verify(commonDao).getOption(any());
    }

    @Test
    public void getFollowByOptionDelegates() {
        Map<String, Object> row = new HashMap<String, Object>();
        row.put("id", 1L);
        when(commonDao.getFollowByOption(any())).thenReturn(row);
        assertNotNull(service.getFollowByOption(new HashMap<String, Object>()));
    }

    @Test
    public void shDelegates() {
        Map<String, Object> params = new HashMap<String, Object>();
        service.sh(params);
        verify(commonDao).sh(params);
    }

    @Test
    public void remindCountDelegates() {
        when(commonDao.remindCount(any())).thenReturn(5);
        assertEquals(5, service.remindCount(new HashMap<String, Object>()));
    }

    @Test
    public void selectCalDelegates() {
        Map<String, Object> cal = new HashMap<String, Object>();
        cal.put("sum", 100);
        when(commonDao.selectCal(any())).thenReturn(cal);
        assertEquals(100, service.selectCal(new HashMap<String, Object>()).get("sum"));
    }

    @Test
    public void selectGroupDelegates() {
        when(commonDao.selectGroup(any())).thenReturn(Collections.<Map<String, Object>>emptyList());
        assertNotNull(service.selectGroup(new HashMap<String, Object>()));
    }

    @Test
    public void selectValueDelegates() {
        when(commonDao.selectValue(any())).thenReturn(Collections.<Map<String, Object>>emptyList());
        assertNotNull(service.selectValue(new HashMap<String, Object>()));
    }

    @Test
    public void selectTimeStatValueDelegates() {
        when(commonDao.selectTimeStatValue(any())).thenReturn(Collections.<Map<String, Object>>emptyList());
        assertNotNull(service.selectTimeStatValue(new HashMap<String, Object>()));
    }
}
