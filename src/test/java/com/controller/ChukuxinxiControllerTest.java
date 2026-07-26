package com.controller;

import com.entity.ChukuxinxiEntity;
import com.entity.ShangpinxinxiEntity;
import com.entity.view.ChukuxinxiView;
import com.service.BuhuotixingService;
import com.service.ChukuxinxiService;
import com.service.ShangpinxinxiService;
import com.utils.R;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ChukuxinxiControllerTest {

    @Mock
    private ChukuxinxiService chukuxinxiService;

    @Mock
    private ShangpinxinxiService shangpinxinxiService;

    @Mock
    private BuhuotixingService buhuotixingService;

    @InjectMocks
    private ChukuxinxiController controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPageAsYonghu() {
        when(chukuxinxiService.queryPage(any(), any())).thenReturn(ControllerTestSupport.emptyPage());
        R result = controller.page(ControllerTestSupport.pageParams(), new ChukuxinxiEntity(),
                ControllerTestSupport.mockRequestWithSession("yonghu", "user1", 1L, "张三"));
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testPageAsAdmin() {
        when(chukuxinxiService.queryPage(any(), any())).thenReturn(ControllerTestSupport.emptyPage());
        R result = controller.page(ControllerTestSupport.pageParams(), new ChukuxinxiEntity(),
                ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testList() {
        when(chukuxinxiService.queryPage(any(), any())).thenReturn(ControllerTestSupport.emptyPage());
        R result = controller.list(ControllerTestSupport.pageParams(), new ChukuxinxiEntity(),
                ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testLists() {
        when(chukuxinxiService.selectListView(any())).thenReturn(Collections.emptyList());
        R result = controller.list(new ChukuxinxiEntity());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testQuery() {
        when(chukuxinxiService.selectView(any())).thenReturn(new ChukuxinxiView());
        R result = controller.query(new ChukuxinxiEntity());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testInfo() {
        when(chukuxinxiService.selectById(1L)).thenReturn(new ChukuxinxiEntity());
        R result = controller.info(1L);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testDetail() {
        when(chukuxinxiService.selectById(1L)).thenReturn(new ChukuxinxiEntity());
        R result = controller.detail(1L);
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testSaveWithStockUpdate() {
        ChukuxinxiEntity entity = new ChukuxinxiEntity();
        entity.setFuzhuangbianhao("SP001");
        entity.setFuzhuangkucun(5);
        entity.setZhanghao("user1");

        ShangpinxinxiEntity product = new ShangpinxinxiEntity();
        product.setFuzhuangkucun(10);
        when(shangpinxinxiService.selectOne(any())).thenReturn(product);

        R result = controller.save(entity,
                ControllerTestSupport.mockRequestWithSession("yonghu", "user1", 1L, "张三"));
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testSaveWithoutProduct() {
        ChukuxinxiEntity entity = new ChukuxinxiEntity();
        entity.setFuzhuangbianhao("SP001");
        entity.setFuzhuangkucun(5);
        when(shangpinxinxiService.selectOne(any())).thenReturn(null);

        R result = controller.save(entity, ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testAdd() {
        ChukuxinxiEntity entity = new ChukuxinxiEntity();
        entity.setId(1L);
        R result = controller.add(entity, ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
        assertNotNull(result.get("data"));
    }

    @Test
    public void testUpdate() {
        R result = controller.update(new ChukuxinxiEntity(), ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testDelete() {
        R result = controller.delete(new Long[]{1L});
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testSaveAsAdminWithNullProductStock() {
        ChukuxinxiEntity entity = new ChukuxinxiEntity();
        entity.setFuzhuangbianhao("SP002");
        entity.setFuzhuangkucun(3);
        entity.setZhanghao("admin");

        ShangpinxinxiEntity product = new ShangpinxinxiEntity();
        product.setFuzhuangkucun(null);
        when(shangpinxinxiService.selectOne(any())).thenReturn(product);

        R result = controller.save(entity, ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testSaveSkipsStockUpdateWhenSkuMissing() {
        ChukuxinxiEntity entity = new ChukuxinxiEntity();
        entity.setFuzhuangkucun(5);
        entity.setZhanghao("user1");

        R result = controller.save(entity, ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testSaveSkipsStockUpdateWhenQtyMissing() {
        ChukuxinxiEntity entity = new ChukuxinxiEntity();
        entity.setFuzhuangbianhao("SP003");
        entity.setZhanghao("user1");

        R result = controller.save(entity, ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testSaveSkipsAlertWhenUsernameBlank() {
        ChukuxinxiEntity entity = new ChukuxinxiEntity();
        entity.setFuzhuangbianhao("SP004");
        entity.setFuzhuangkucun(2);
        entity.setZhanghao("  ");

        ShangpinxinxiEntity product = new ShangpinxinxiEntity();
        product.setFuzhuangkucun(10);
        when(shangpinxinxiService.selectOne(any())).thenReturn(product);

        R result = controller.save(entity, ControllerTestSupport.mockAdminRequest());
        assertEquals(0, result.get("code"));
    }

    @Test
    public void testSaveAsYonghuSetsSessionFields() {
        ChukuxinxiEntity entity = new ChukuxinxiEntity();
        entity.setFuzhuangbianhao("SP005");
        entity.setFuzhuangkucun(1);

        ShangpinxinxiEntity product = new ShangpinxinxiEntity();
        product.setFuzhuangkucun(5);
        when(shangpinxinxiService.selectOne(any())).thenReturn(product);

        R result = controller.save(entity,
                ControllerTestSupport.mockRequestWithSession("yonghu", "user1", 1L, "张三"));
        assertEquals(0, result.get("code"));
        assertEquals("user1", entity.getZhanghao());
        assertEquals("张三", entity.getXingming());
    }
}
