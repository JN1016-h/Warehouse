package com.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dao.ShangpinfenleiDao;
import com.entity.ShangpinfenleiEntity;
import com.entity.view.ShangpinfenleiView;
import com.entity.vo.ShangpinfenleiVO;
import com.service.impl.ShangpinfenleiServiceImpl;
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

public class ShangpinfenleiServiceImplTest {

    @Mock
    private ShangpinfenleiDao shangpinfenleiDao;

    @Spy
    @InjectMocks
    private ShangpinfenleiServiceImpl service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void queryPageDefault() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("page", "1");
        params.put("limit", "10");
        Page<ShangpinfenleiEntity> page = new Page<ShangpinfenleiEntity>(1, 10);
        doReturn(page).when(service).selectPage(any(Page.class), any(QueryWrapper.class));
        assertNotNull(service.queryPage(params));
    }

    @Test
    public void queryPageWithWrapper() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("page", "1");
        params.put("limit", "10");
        when(shangpinfenleiDao.selectListView(any(Page.class), any(QueryWrapper.class)))
                .thenReturn(Collections.<ShangpinfenleiView>emptyList());
        assertNotNull(service.queryPage(params, new QueryWrapper<ShangpinfenleiEntity>()));
    }

    @Test
    public void selectListVODelegates() {
        when(shangpinfenleiDao.selectListVO(any(QueryWrapper.class)))
                .thenReturn(Collections.<ShangpinfenleiVO>emptyList());
        assertNotNull(service.selectListVO(new QueryWrapper<ShangpinfenleiEntity>()));
    }

    @Test
    public void selectVODelegates() {
        when(shangpinfenleiDao.selectVO(any(QueryWrapper.class))).thenReturn(new ShangpinfenleiVO());
        assertNotNull(service.selectVO(new QueryWrapper<ShangpinfenleiEntity>()));
    }

    @Test
    public void selectListViewDelegates() {
        when(shangpinfenleiDao.selectListView(any(QueryWrapper.class)))
                .thenReturn(Collections.<ShangpinfenleiView>emptyList());
        assertNotNull(service.selectListView(new QueryWrapper<ShangpinfenleiEntity>()));
    }

    @Test
    public void selectViewDelegates() {
        when(shangpinfenleiDao.selectView(any(QueryWrapper.class))).thenReturn(new ShangpinfenleiView());
        assertNotNull(service.selectView(new QueryWrapper<ShangpinfenleiEntity>()));
    }
}
