package com.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dao.GongyingshangDao;
import com.entity.GongyingshangEntity;
import com.entity.view.GongyingshangView;
import com.entity.vo.GongyingshangVO;
import com.service.impl.GongyingshangServiceImpl;
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

public class GongyingshangServiceImplTest {

    @Mock
    private GongyingshangDao gongyingshangDao;

    @Spy
    @InjectMocks
    private GongyingshangServiceImpl service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void queryPageDefault() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("page", "1");
        params.put("limit", "10");
        Page<GongyingshangEntity> page = new Page<GongyingshangEntity>(1, 10);
        doReturn(page).when(service).selectPage(any(Page.class), any(QueryWrapper.class));
        assertNotNull(service.queryPage(params));
    }

    @Test
    public void queryPageWithWrapper() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("page", "1");
        params.put("limit", "10");
        when(gongyingshangDao.selectListView(any(Page.class), any(QueryWrapper.class)))
                .thenReturn(Collections.<GongyingshangView>emptyList());
        assertNotNull(service.queryPage(params, new QueryWrapper<GongyingshangEntity>()));
    }

    @Test
    public void selectListVODelegates() {
        when(gongyingshangDao.selectListVO(any(QueryWrapper.class)))
                .thenReturn(Collections.<GongyingshangVO>emptyList());
        assertNotNull(service.selectListVO(new QueryWrapper<GongyingshangEntity>()));
    }

    @Test
    public void selectVODelegates() {
        when(gongyingshangDao.selectVO(any(QueryWrapper.class))).thenReturn(new GongyingshangVO());
        assertNotNull(service.selectVO(new QueryWrapper<GongyingshangEntity>()));
    }

    @Test
    public void selectListViewDelegates() {
        when(gongyingshangDao.selectListView(any(QueryWrapper.class)))
                .thenReturn(Collections.<GongyingshangView>emptyList());
        assertNotNull(service.selectListView(new QueryWrapper<GongyingshangEntity>()));
    }

    @Test
    public void selectViewDelegates() {
        when(gongyingshangDao.selectView(any(QueryWrapper.class))).thenReturn(new GongyingshangView());
        assertNotNull(service.selectView(new QueryWrapper<GongyingshangEntity>()));
    }
}
