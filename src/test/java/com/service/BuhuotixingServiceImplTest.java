package com.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dao.BuhuotixingDao;
import com.entity.BuhuotixingEntity;
import com.entity.ShangpinxinxiEntity;
import com.service.impl.BuhuotixingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class BuhuotixingServiceImplTest {

    @Mock
    private ShangpinxinxiService shangpinxinxiService;

    @Mock
    private BuhuotixingDao buhuotixingDao;

    @Spy
    @InjectMocks
    private BuhuotixingServiceImpl buhuotixingService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void checkAndCreateAlertProductNotFound() {
        when(shangpinxinxiService.getOne(any(QueryWrapper.class))).thenReturn(null);

        buhuotixingService.checkAndCreateAlert("SKU001", "user1");

        verify(buhuotixingService, never()).insert(any(BuhuotixingEntity.class));
    }

    @Test
    public void checkAndCreateAlertStockAboveThreshold() {
        ShangpinxinxiEntity product = product("SKU001", 50, 10);
        when(shangpinxinxiService.getOne(any(QueryWrapper.class))).thenReturn(product);

        buhuotixingService.checkAndCreateAlert("SKU001", "user1");

        verify(buhuotixingService, never()).insert(any(BuhuotixingEntity.class));
    }

    @Test
    public void checkAndCreateAlertExistingPending() {
        ShangpinxinxiEntity product = product("SKU001", 5, 10);
        when(shangpinxinxiService.getOne(any(QueryWrapper.class))).thenReturn(product);
        doReturn(1).when(buhuotixingService).selectCount(any(QueryWrapper.class));

        buhuotixingService.checkAndCreateAlert("SKU001", "user1");

        verify(buhuotixingService, never()).insert(any(BuhuotixingEntity.class));
    }

    @Test
    public void checkAndCreateAlertCreatesNew() {
        ShangpinxinxiEntity product = product("SKU001", 5, 10);
        product.setFuzhuangmingcheng("测试商品");
        product.setShangpinfenlei("上衣");
        product.setGongyingshangmingcheng("供应商A");
        when(shangpinxinxiService.getOne(any(QueryWrapper.class))).thenReturn(product);
        doReturn(0).when(buhuotixingService).selectCount(any(QueryWrapper.class));
        doReturn(true).when(buhuotixingService).insert(any(BuhuotixingEntity.class));

        buhuotixingService.checkAndCreateAlert("SKU001", "user1");

        verify(buhuotixingService).insert(any(BuhuotixingEntity.class));
    }

    @Test
    public void checkAndCreateAlertUsesDefaultThreshold() {
        ShangpinxinxiEntity product = product("SKU002", 5, null);
        when(shangpinxinxiService.getOne(any(QueryWrapper.class))).thenReturn(product);
        doReturn(0).when(buhuotixingService).selectCount(any(QueryWrapper.class));
        doReturn(true).when(buhuotixingService).insert(any(BuhuotixingEntity.class));

        buhuotixingService.checkAndCreateAlert("SKU002", "user1");

        verify(buhuotixingService).insert(argThat(alert ->
                alert.getKucunyuzhi() == 10 && alert.getBuhuoshuliang() == 25));
    }

    @Test
    public void completeAlertUpdatesStatus() {
        BuhuotixingEntity alert = new BuhuotixingEntity();
        alert.setId(1L);
        alert.setTixingzhuangtai("待处理");
        doReturn(alert).when(buhuotixingService).selectById(1L);
        doReturn(true).when(buhuotixingService).updateById(any(BuhuotixingEntity.class));

        buhuotixingService.completeAlert(1L);

        assertEquals("已完成", alert.getTixingzhuangtai());
        assertNotNull(alert.getWanchengshijian());
        verify(buhuotixingService).updateById(alert);
    }

    @Test
    public void completeAlertNotFound() {
        doReturn(null).when(buhuotixingService).selectById(99L);

        buhuotixingService.completeAlert(99L);

        verify(buhuotixingService, never()).updateById(any());
    }

    @Test
    public void cancelAlertUpdatesStatus() {
        BuhuotixingEntity alert = new BuhuotixingEntity();
        alert.setId(2L);
        doReturn(alert).when(buhuotixingService).selectById(2L);
        doReturn(true).when(buhuotixingService).updateById(any(BuhuotixingEntity.class));

        buhuotixingService.cancelAlert(2L);

        assertEquals("已取消", alert.getTixingzhuangtai());
    }

    @Test
    public void queryPageDelegatesToMapper() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("page", "1");
        params.put("limit", "10");
        doReturn(Collections.emptyList()).when(buhuotixingDao).selectListView(any(), any());

        assertNotNull(buhuotixingService.queryPage(params, new QueryWrapper<BuhuotixingEntity>()));
    }

    private ShangpinxinxiEntity product(String sku, int stock, Integer threshold) {
        ShangpinxinxiEntity p = new ShangpinxinxiEntity();
        p.setFuzhuangbianhao(sku);
        p.setFuzhuangkucun(stock);
        p.setKucunyuzhi(threshold);
        return p;
    }
}
