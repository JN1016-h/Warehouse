package com.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.dao.TokenDao;
import com.entity.TokenEntity;
import com.service.impl.TokenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class TokenServiceImplTest {

    @Mock
    private TokenDao tokenDao;

    @Spy
    @InjectMocks
    private TokenServiceImpl tokenService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void generateTokenUpdatesExisting() {
        TokenEntity existing = new TokenEntity();
        existing.setId(1L);
        existing.setUserid(10L);
        existing.setRole("管理员");
        doReturn(existing).when(tokenService).selectOne(any(EntityWrapper.class));
        doReturn(true).when(tokenService).updateById(any(TokenEntity.class));

        String token = tokenService.generateToken(10L, "user1", "yonghu", "管理员");

        assertNotNull(token);
        assertEquals(32, token.length());
        assertEquals(token, existing.getToken());
        assertNotNull(existing.getExpiratedtime());
        verify(tokenService).updateById(existing);
        verify(tokenService, never()).insert(any(TokenEntity.class));
    }

    @Test
    public void generateTokenInsertsNew() {
        doReturn(null).when(tokenService).selectOne(any(EntityWrapper.class));
        doReturn(true).when(tokenService).insert(any(TokenEntity.class));

        String token = tokenService.generateToken(20L, "newuser", "yonghu", "用户");

        assertNotNull(token);
        ArgumentCaptor<TokenEntity> captor = ArgumentCaptor.forClass(TokenEntity.class);
        verify(tokenService).insert(captor.capture());
        TokenEntity inserted = captor.getValue();
        assertEquals(20L, inserted.getUserid().longValue());
        assertEquals("newuser", inserted.getUsername());
        assertEquals("yonghu", inserted.getTablename());
        assertEquals("用户", inserted.getRole());
    }

    @Test
    public void getTokenEntityValid() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 1);
        TokenEntity entity = new TokenEntity(1L, "u", "yonghu", "管理员", "abc", cal.getTime());
        doReturn(entity).when(tokenService).selectOne(any(EntityWrapper.class));

        TokenEntity result = tokenService.getTokenEntity("abc");

        assertSame(entity, result);
    }

    @Test
    public void getTokenEntityExpired() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, -1);
        TokenEntity entity = new TokenEntity(1L, "u", "yonghu", "管理员", "expired", cal.getTime());
        doReturn(entity).when(tokenService).selectOne(any(EntityWrapper.class));

        assertNull(tokenService.getTokenEntity("expired"));
    }

    @Test
    public void getTokenEntityNotFound() {
        doReturn(null).when(tokenService).selectOne(any(EntityWrapper.class));
        assertNull(tokenService.getTokenEntity("missing"));
    }

    @Test
    public void queryPageWithWrapper() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("page", "1");
        params.put("limit", "10");
        when(tokenDao.selectListView(any(), any())).thenReturn(Collections.<TokenEntity>emptyList());

        assertNotNull(tokenService.queryPage(params, new EntityWrapper<TokenEntity>()));
    }

    @Test
    public void selectListViewDelegates() {
        when(tokenDao.selectListView(any(EntityWrapper.class))).thenReturn(Collections.<TokenEntity>emptyList());

        assertNotNull(tokenService.selectListView(new EntityWrapper<TokenEntity>()));
    }
}
