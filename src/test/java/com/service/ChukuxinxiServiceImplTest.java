package com.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dao.ChukuxinxiDao;
import com.entity.ChukuxinxiEntity;
import com.entity.view.ChukuxinxiView;
import com.entity.vo.ChukuxinxiVO;
import com.service.impl.ChukuxinxiServiceImpl;
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

public class ChukuxinxiServiceImplTest {

    @Mock
    private ChukuxinxiDao chukuxinxiDao;

    @Spy
    @InjectMocks
    private ChukuxinxiServiceImpl service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void queryPageDefault() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("page", "1");
        params.put("limit", "10");
        Page<ChukuxinxiEntity> page = new Page<ChukuxinxiEntity>(1, 10);
        doReturn(page).when(service).selectPage(any(Page.class), any(QueryWrapper.class));
        assertNotNull(service.queryPage(params));
    }

    @Test
    public void queryPageWithWrapper() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("page", "1");
        params.put("limit", "10");
        when(chukuxinxiDao.selectListView(any(Page.class), any(QueryWrapper.class)))
                .thenReturn(Collections.<ChukuxinxiView>emptyList());
        assertNotNull(service.queryPage(params, new QueryWrapper<ChukuxinxiEntity>()));
    }

    @Test
    public void selectListVODelegates() {
        when(chukuxinxiDao.selectListVO(any(QueryWrapper.class)))
                .thenReturn(Collections.<ChukuxinxiVO>emptyList());
        assertNotNull(service.selectListVO(new QueryWrapper<ChukuxinxiEntity>()));
    }

    @Test
    public void selectVODelegates() {
        when(chukuxinxiDao.selectVO(any(QueryWrapper.class))).thenReturn(new ChukuxinxiVO());
        assertNotNull(service.selectVO(new QueryWrapper<ChukuxinxiEntity>()));
    }

    @Test
    public void selectListViewDelegates() {
        when(chukuxinxiDao.selectListView(any(QueryWrapper.class)))
                .thenReturn(Collections.<ChukuxinxiView>emptyList());
        assertNotNull(service.selectListView(new QueryWrapper<ChukuxinxiEntity>()));
    }

    @Test
    public void selectViewDelegates() {
        when(chukuxinxiDao.selectView(any(QueryWrapper.class))).thenReturn(new ChukuxinxiView());
        assertNotNull(service.selectView(new QueryWrapper<ChukuxinxiEntity>()));
    }
}
