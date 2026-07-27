package com.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dao.ShangpinxinxiDao;
import com.entity.ShangpinxinxiEntity;
import com.entity.view.ShangpinxinxiView;
import com.entity.vo.ShangpinxinxiVO;
import com.service.impl.ShangpinxinxiServiceImpl;
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

public class ShangpinxinxiServiceImplTest {

    @Mock
    private ShangpinxinxiDao shangpinxinxiDao;

    @Spy
    @InjectMocks
    private ShangpinxinxiServiceImpl service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void queryPageDefault() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("page", "1");
        params.put("limit", "10");
        Page<ShangpinxinxiEntity> page = new Page<ShangpinxinxiEntity>(1, 10);
        doReturn(page).when(service).selectPage(any(Page.class), any(QueryWrapper.class));

        assertNotNull(service.queryPage(params));
    }

    @Test
    public void queryPageWithWrapper() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("page", "1");
        params.put("limit", "10");
        when(shangpinxinxiDao.selectListView(any(Page.class), any(QueryWrapper.class)))
                .thenReturn(Collections.<ShangpinxinxiView>emptyList());

        assertNotNull(service.queryPage(params, new QueryWrapper<ShangpinxinxiEntity>()));
    }

    @Test
    public void selectListVODelegates() {
        when(shangpinxinxiDao.selectListVO(any(QueryWrapper.class)))
                .thenReturn(Collections.<ShangpinxinxiVO>emptyList());
        assertNotNull(service.selectListVO(new QueryWrapper<ShangpinxinxiEntity>()));
    }

    @Test
    public void selectVODelegates() {
        when(shangpinxinxiDao.selectVO(any(QueryWrapper.class))).thenReturn(new ShangpinxinxiVO());
        assertNotNull(service.selectVO(new QueryWrapper<ShangpinxinxiEntity>()));
    }

    @Test
    public void selectListViewDelegates() {
        when(shangpinxinxiDao.selectListView(any(QueryWrapper.class)))
                .thenReturn(Collections.<ShangpinxinxiView>emptyList());
        assertNotNull(service.selectListView(new QueryWrapper<ShangpinxinxiEntity>()));
    }

    @Test
    public void selectViewDelegates() {
        when(shangpinxinxiDao.selectView(any(QueryWrapper.class))).thenReturn(new ShangpinxinxiView());
        assertNotNull(service.selectView(new QueryWrapper<ShangpinxinxiEntity>()));
    }

    @Test
    public void selectValueDelegates() {
        when(shangpinxinxiDao.selectValue(any(), any(QueryWrapper.class)))
                .thenReturn(Collections.<Map<String, Object>>emptyList());
        assertNotNull(service.selectValue(new HashMap<String, Object>(), new QueryWrapper<ShangpinxinxiEntity>()));
    }

    @Test
    public void selectTimeStatValueDelegates() {
        when(shangpinxinxiDao.selectTimeStatValue(any(), any(QueryWrapper.class)))
                .thenReturn(Collections.<Map<String, Object>>emptyList());
        assertNotNull(service.selectTimeStatValue(new HashMap<String, Object>(), new QueryWrapper<ShangpinxinxiEntity>()));
    }

    @Test
    public void selectGroupDelegates() {
        when(shangpinxinxiDao.selectGroup(any(), any(QueryWrapper.class)))
                .thenReturn(Collections.<Map<String, Object>>emptyList());
        assertNotNull(service.selectGroup(new HashMap<String, Object>(), new QueryWrapper<ShangpinxinxiEntity>()));
    }
}
