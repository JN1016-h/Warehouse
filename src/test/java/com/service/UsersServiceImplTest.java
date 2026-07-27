package com.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dao.UsersDao;
import com.entity.UsersEntity;
import com.service.impl.UsersServiceImpl;
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

public class UsersServiceImplTest {

    @Mock
    private UsersDao usersDao;

    @Spy
    @InjectMocks
    private UsersServiceImpl service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void queryPageDefault() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("page", "1");
        params.put("limit", "10");
        Page<UsersEntity> page = new Page<UsersEntity>(1, 10);
        doReturn(page).when(service).selectPage(any(Page.class), any(QueryWrapper.class));
        assertNotNull(service.queryPage(params));
    }

    @Test
    public void queryPageWithWrapper() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("page", "1");
        params.put("limit", "10");
        when(usersDao.selectListView(any(Page.class), any(QueryWrapper.class)))
                .thenReturn(Collections.<UsersEntity>emptyList());
        assertNotNull(service.queryPage(params, new QueryWrapper<UsersEntity>()));
    }

    @Test
    public void selectListViewDelegates() {
        when(usersDao.selectListView(any(QueryWrapper.class)))
                .thenReturn(Collections.<UsersEntity>emptyList());
        assertNotNull(service.selectListView(new QueryWrapper<UsersEntity>()));
    }
}
