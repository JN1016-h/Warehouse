package com.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dao.RukuxinxiDao;
import com.entity.RukuxinxiEntity;
import com.entity.view.RukuxinxiView;
import com.entity.vo.RukuxinxiVO;
import com.service.impl.RukuxinxiServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public class RukuxinxiServiceImplTest {

    @Mock
    private RukuxinxiDao rukuxinxiDao;

    @Spy
    @InjectMocks
    private RukuxinxiServiceImpl service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void queryPageDefault() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("page", "1");
        params.put("limit", "10");
        Page<RukuxinxiEntity> page = new Page<RukuxinxiEntity>(1, 10);
        doReturn(page).when(service).selectPage(any(Page.class), any(QueryWrapper.class));
        assertNotNull(service.queryPage(params));
    }

    @Test
    public void queryPageWithWrapper() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("page", "1");
        params.put("limit", "10");
        when(rukuxinxiDao.selectListView(any(Page.class), any(QueryWrapper.class)))
                .thenReturn(Collections.<RukuxinxiView>emptyList());
        assertNotNull(service.queryPage(params, new QueryWrapper<RukuxinxiEntity>()));
    }

    @Test
    public void selectListVODelegates() {
        when(rukuxinxiDao.selectListVO(any(QueryWrapper.class)))
                .thenReturn(Collections.<RukuxinxiVO>emptyList());
        assertNotNull(service.selectListVO(new QueryWrapper<RukuxinxiEntity>()));
    }

    @Test
    public void selectVODelegates() {
        when(rukuxinxiDao.selectVO(any(QueryWrapper.class))).thenReturn(new RukuxinxiVO());
        assertNotNull(service.selectVO(new QueryWrapper<RukuxinxiEntity>()));
    }

    @Test
    public void selectListViewDelegates() {
        when(rukuxinxiDao.selectListView(any(QueryWrapper.class)))
                .thenReturn(Collections.<RukuxinxiView>emptyList());
        assertNotNull(service.selectListView(new QueryWrapper<RukuxinxiEntity>()));
    }

    @Test
    public void selectViewDelegates() {
        when(rukuxinxiDao.selectView(any(QueryWrapper.class))).thenReturn(new RukuxinxiView());
        assertNotNull(service.selectView(new QueryWrapper<RukuxinxiEntity>()));
    }

    @Test
    public void selectValueDelegates() {
        when(rukuxinxiDao.selectValue(any(), any(QueryWrapper.class)))
                .thenReturn(Collections.<Map<String, Object>>emptyList());
        assertNotNull(service.selectValue(new HashMap<String, Object>(), new QueryWrapper<RukuxinxiEntity>()));
    }

    @Test
    public void selectTimeStatValueDelegates() {
        when(rukuxinxiDao.selectTimeStatValue(any(), any(QueryWrapper.class)))
                .thenReturn(Collections.<Map<String, Object>>emptyList());
        assertNotNull(service.selectTimeStatValue(new HashMap<String, Object>(), new QueryWrapper<RukuxinxiEntity>()));
    }

    @Test
    public void selectGroupDelegates() {
        when(rukuxinxiDao.selectGroup(any(), any(QueryWrapper.class)))
                .thenReturn(Collections.<Map<String, Object>>emptyList());
        assertNotNull(service.selectGroup(new HashMap<String, Object>(), new QueryWrapper<RukuxinxiEntity>()));
    }
}
