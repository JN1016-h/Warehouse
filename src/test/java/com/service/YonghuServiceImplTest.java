package com.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.dao.YonghuDao;
import com.entity.YonghuEntity;
import com.entity.view.YonghuView;
import com.entity.vo.YonghuVO;
import com.service.impl.YonghuServiceImpl;
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

public class YonghuServiceImplTest {

    @Mock
    private YonghuDao yonghuDao;

    @Spy
    @InjectMocks
    private YonghuServiceImpl service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void queryPageDefault() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("page", "1");
        params.put("limit", "10");
        Page<YonghuEntity> page = new Page<YonghuEntity>(1, 10);
        doReturn(page).when(service).selectPage(any(Page.class), any(EntityWrapper.class));
        assertNotNull(service.queryPage(params));
    }

    @Test
    public void queryPageWithWrapper() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("page", "1");
        params.put("limit", "10");
        when(yonghuDao.selectListView(any(Page.class), any(EntityWrapper.class)))
                .thenReturn(Collections.<YonghuView>emptyList());
        assertNotNull(service.queryPage(params, new EntityWrapper<YonghuEntity>()));
    }

    @Test
    public void selectListVODelegates() {
        when(yonghuDao.selectListVO(any(EntityWrapper.class)))
                .thenReturn(Collections.<YonghuVO>emptyList());
        assertNotNull(service.selectListVO(new EntityWrapper<YonghuEntity>()));
    }

    @Test
    public void selectVODelegates() {
        when(yonghuDao.selectVO(any(EntityWrapper.class))).thenReturn(new YonghuVO());
        assertNotNull(service.selectVO(new EntityWrapper<YonghuEntity>()));
    }

    @Test
    public void selectListViewDelegates() {
        when(yonghuDao.selectListView(any(EntityWrapper.class)))
                .thenReturn(Collections.<YonghuView>emptyList());
        assertNotNull(service.selectListView(new EntityWrapper<YonghuEntity>()));
    }

    @Test
    public void selectViewDelegates() {
        when(yonghuDao.selectView(any(EntityWrapper.class))).thenReturn(new YonghuView());
        assertNotNull(service.selectView(new EntityWrapper<YonghuEntity>()));
    }
}
