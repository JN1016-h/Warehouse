package com.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dao.DinghuoxinxiDao;
import com.entity.DinghuoxinxiEntity;
import com.entity.view.DinghuoxinxiView;
import com.entity.vo.DinghuoxinxiVO;
import com.service.impl.DinghuoxinxiServiceImpl;
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

public class DinghuoxinxiServiceImplTest {

    @Mock
    private DinghuoxinxiDao dinghuoxinxiDao;

    @Spy
    @InjectMocks
    private DinghuoxinxiServiceImpl service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void queryPageDefault() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("page", "1");
        params.put("limit", "10");
        Page<DinghuoxinxiEntity> page = new Page<DinghuoxinxiEntity>(1, 10);
        doReturn(page).when(service).selectPage(any(Page.class), any(QueryWrapper.class));
        assertNotNull(service.queryPage(params));
    }

    @Test
    public void queryPageWithWrapper() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("page", "1");
        params.put("limit", "10");
        when(dinghuoxinxiDao.selectListView(any(Page.class), any(QueryWrapper.class)))
                .thenReturn(Collections.<DinghuoxinxiView>emptyList());
        assertNotNull(service.queryPage(params, new QueryWrapper<DinghuoxinxiEntity>()));
    }

    @Test
    public void selectListVODelegates() {
        when(dinghuoxinxiDao.selectListVO(any(QueryWrapper.class)))
                .thenReturn(Collections.<DinghuoxinxiVO>emptyList());
        assertNotNull(service.selectListVO(new QueryWrapper<DinghuoxinxiEntity>()));
    }

    @Test
    public void selectVODelegates() {
        when(dinghuoxinxiDao.selectVO(any(QueryWrapper.class))).thenReturn(new DinghuoxinxiVO());
        assertNotNull(service.selectVO(new QueryWrapper<DinghuoxinxiEntity>()));
    }

    @Test
    public void selectListViewDelegates() {
        when(dinghuoxinxiDao.selectListView(any(QueryWrapper.class)))
                .thenReturn(Collections.<DinghuoxinxiView>emptyList());
        assertNotNull(service.selectListView(new QueryWrapper<DinghuoxinxiEntity>()));
    }

    @Test
    public void selectViewDelegates() {
        when(dinghuoxinxiDao.selectView(any(QueryWrapper.class))).thenReturn(new DinghuoxinxiView());
        assertNotNull(service.selectView(new QueryWrapper<DinghuoxinxiEntity>()));
    }

    @Test
    public void selectValueDelegates() {
        when(dinghuoxinxiDao.selectValue(any(), any(QueryWrapper.class)))
                .thenReturn(Collections.<Map<String, Object>>emptyList());
        assertNotNull(service.selectValue(new HashMap<String, Object>(), new QueryWrapper<DinghuoxinxiEntity>()));
    }

    @Test
    public void selectTimeStatValueDelegates() {
        when(dinghuoxinxiDao.selectTimeStatValue(any(), any(QueryWrapper.class)))
                .thenReturn(Collections.<Map<String, Object>>emptyList());
        assertNotNull(service.selectTimeStatValue(new HashMap<String, Object>(), new QueryWrapper<DinghuoxinxiEntity>()));
    }

    @Test
    public void selectGroupDelegates() {
        when(dinghuoxinxiDao.selectGroup(any(), any(QueryWrapper.class)))
                .thenReturn(Collections.<Map<String, Object>>emptyList());
        assertNotNull(service.selectGroup(new HashMap<String, Object>(), new QueryWrapper<DinghuoxinxiEntity>()));
    }
}
