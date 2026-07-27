package com.service;

import com.dao.ChukuxinxiDao;
import com.dao.DinghuoxinxiDao;
import com.dao.RukuxinxiDao;
import com.dto.FinanceSummary;
import com.dto.PayableQuery;
import com.dto.ReceivableQuery;
import com.entity.ChukuxinxiEntity;
import com.entity.DinghuoxinxiEntity;
import com.entity.RukuxinxiEntity;
import com.enums.PaymentStatus;
import com.service.impl.FinanceServiceImpl;
import com.utils.PageUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * FinanceService单元测试
 * 测试财务服务的核心功能
 */
public class FinanceServiceTest {
    
    @Mock
    private ChukuxinxiDao chukuxinxiDao;
    
    @Mock
    private RukuxinxiDao rukuxinxiDao;
    
    @Mock
    private DinghuoxinxiDao dinghuoxinxiDao;
    
    @InjectMocks
    private FinanceServiceImpl financeService;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    /**
     * 测试queryReceivables - 基本查询
     * 验证能够正确查询应收款项列表
     */
    @Test
    public void testQueryReceivables_BasicQuery() {
        // 准备测试数据
        ReceivableQuery query = new ReceivableQuery();
        query.setPage(1);
        query.setLimit(10);
        
        List<ChukuxinxiEntity> chukuxinxiList = new ArrayList<>();
        ChukuxinxiEntity entity = new ChukuxinxiEntity();
        entity.setId(1L);
        entity.setFuzhuangbianhao("CK001");
        entity.setKehumingcheng("客户A");
        entity.setPaymentStatus("UNPAID");
        entity.setJiaohuoshijian(new Date());
        chukuxinxiList.add(entity);
        
        // 模拟DAO行为（selectPage 返回 IPage，不是 List）
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ChukuxinxiEntity> pageResult =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(query.getPage(), query.getLimit());
        pageResult.setRecords(chukuxinxiList);
        when(chukuxinxiDao.selectPage(any(), any())).thenReturn(pageResult);
        
        // 执行测试
        PageUtils result = financeService.queryReceivables(query);
        
        // 验证结果
        assertNotNull(result);
        assertNotNull(result.getList());
        
        // 验证方法调用
        verify(chukuxinxiDao, times(1)).selectPage(any(), any());
    }
    
    /**
     * 测试queryPayables - 基本查询
     * 验证能够正确查询应付款项列表
     */
    @Test
    public void testQueryPayables_BasicQuery() {
        // 准备测试数据
        PayableQuery query = new PayableQuery();
        query.setPage(1);
        query.setLimit(10);
        
        List<RukuxinxiEntity> rukuxinxiList = new ArrayList<>();
        RukuxinxiEntity entity = new RukuxinxiEntity();
        entity.setId(1L);
        entity.setFuzhuangbianhao("RK001");
        entity.setGongyingshangmingcheng("供应商A");
        entity.setPaymentStatus("UNPAID");
        entity.setRukushijian(new Date());
        rukuxinxiList.add(entity);
        
        // 模拟DAO行为（selectPage 返回 IPage，不是 List）
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<RukuxinxiEntity> pageResult =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(query.getPage(), query.getLimit());
        pageResult.setRecords(rukuxinxiList);
        when(rukuxinxiDao.selectPage(any(), any())).thenReturn(pageResult);
        
        // 执行测试
        PageUtils result = financeService.queryPayables(query);
        
        // 验证结果
        assertNotNull(result);
        assertNotNull(result.getList());
        
        // 验证方法调用
        verify(rukuxinxiDao, times(1)).selectPage(any(), any());
    }
    
    /**
     * 测试updatePaymentStatus - 更新出库订单为已付款
     * 验证能够成功更新出库订单的付款状态并记录付款时间
     */
    @Test
    public void testUpdatePaymentStatus_OutboundToPaid() {
        // 准备测试数据
        Long orderId = 1L;
        ChukuxinxiEntity entity = new ChukuxinxiEntity();
        entity.setId(orderId);
        entity.setPaymentStatus("UNPAID");
        entity.setPaymentTime(null);
        
        // 模拟DAO行为
        when(chukuxinxiDao.selectById(orderId)).thenReturn(entity);
        when(chukuxinxiDao.updateById(any(ChukuxinxiEntity.class))).thenReturn(1);
        
        // 执行测试
        boolean result = financeService.updatePaymentStatus(orderId, "OUTBOUND", PaymentStatus.PAID);
        
        // 验证结果
        assertTrue(result);
        assertEquals("PAID", entity.getPaymentStatus());
        assertNotNull(entity.getPaymentTime());
        
        // 验证方法调用
        verify(chukuxinxiDao, times(1)).selectById(orderId);
        verify(chukuxinxiDao, times(1)).updateById(entity);
    }
    
    /**
     * 测试updatePaymentStatus - 更新入库订单为已付款
     * 验证能够成功更新入库订单的付款状态并记录付款时间
     */
    @Test
    public void testUpdatePaymentStatus_InboundToPaid() {
        // 准备测试数据
        Long orderId = 1L;
        RukuxinxiEntity entity = new RukuxinxiEntity();
        entity.setId(orderId);
        entity.setPaymentStatus("UNPAID");
        entity.setPaymentTime(null);
        
        // 模拟DAO行为
        when(rukuxinxiDao.selectById(orderId)).thenReturn(entity);
        when(rukuxinxiDao.updateById(any(RukuxinxiEntity.class))).thenReturn(1);
        
        // 执行测试
        boolean result = financeService.updatePaymentStatus(orderId, "INBOUND", PaymentStatus.PAID);
        
        // 验证结果
        assertTrue(result);
        assertEquals("PAID", entity.getPaymentStatus());
        assertNotNull(entity.getPaymentTime());
        
        // 验证方法调用
        verify(rukuxinxiDao, times(1)).selectById(orderId);
        verify(rukuxinxiDao, times(1)).updateById(entity);
    }
    
    /**
     * 测试updatePaymentStatus - 更新为未付款清除付款时间
     * 验证当状态改为未付款时，付款时间被清除
     */
    @Test
    public void testUpdatePaymentStatus_ToUnpaidClearsTime() {
        // 准备测试数据
        Long orderId = 1L;
        ChukuxinxiEntity entity = new ChukuxinxiEntity();
        entity.setId(orderId);
        entity.setPaymentStatus("PAID");
        entity.setPaymentTime(new Date());
        
        // 模拟DAO行为
        when(chukuxinxiDao.selectById(orderId)).thenReturn(entity);
        when(chukuxinxiDao.updateById(any(ChukuxinxiEntity.class))).thenReturn(1);
        
        // 执行测试
        boolean result = financeService.updatePaymentStatus(orderId, "OUTBOUND", PaymentStatus.UNPAID);
        
        // 验证结果
        assertTrue(result);
        assertEquals("UNPAID", entity.getPaymentStatus());
        assertNull(entity.getPaymentTime());
        
        // 验证方法调用
        verify(chukuxinxiDao, times(1)).selectById(orderId);
        verify(chukuxinxiDao, times(1)).updateById(entity);
    }
    
    /**
     * 测试updatePaymentStatus - 订单不存在
     * 验证当订单不存在时返回false
     */
    @Test
    public void testUpdatePaymentStatus_OrderNotFound() {
        // 准备测试数据
        Long orderId = 999L;
        
        // 模拟DAO行为
        when(chukuxinxiDao.selectById(orderId)).thenReturn(null);
        
        // 执行测试
        boolean result = financeService.updatePaymentStatus(orderId, "OUTBOUND", PaymentStatus.PAID);
        
        // 验证结果
        assertFalse(result);
        
        // 验证方法调用
        verify(chukuxinxiDao, times(1)).selectById(orderId);
        verify(chukuxinxiDao, never()).updateById(any(ChukuxinxiEntity.class));
    }
    
    /**
     * 测试updatePaymentStatus - 参数为null
     * 验证当参数为null时返回false
     */
    @Test
    public void testUpdatePaymentStatus_NullParameters() {
        assertFalse(financeService.updatePaymentStatus(null, "OUTBOUND", PaymentStatus.PAID));
        assertFalse(financeService.updatePaymentStatus(1L, null, PaymentStatus.PAID));
        assertFalse(financeService.updatePaymentStatus(1L, "OUTBOUND", null));
        
        // 验证方法未被调用
        verify(chukuxinxiDao, never()).selectById(any());
        verify(rukuxinxiDao, never()).selectById(any());
    }
    
    /**
     * 测试calculateReceivableSummary - 基本统计
     * 验证能够正确统计应收款项金额
     */
    @Test
    public void testCalculateReceivableSummary_BasicCalculation() {
        // 准备测试数据
        ReceivableQuery query = new ReceivableQuery();
        
        List<ChukuxinxiEntity> chukuxinxiList = new ArrayList<>();
        
        // 已付款订单
        ChukuxinxiEntity paidEntity = new ChukuxinxiEntity();
        paidEntity.setId(1L);
        paidEntity.setFuzhuangbianhao("CK001");
        paidEntity.setKehumingcheng("客户A");
        paidEntity.setPaymentStatus("PAID");
        chukuxinxiList.add(paidEntity);
        
        // 未付款订单
        ChukuxinxiEntity unpaidEntity = new ChukuxinxiEntity();
        unpaidEntity.setId(2L);
        unpaidEntity.setFuzhuangbianhao("CK002");
        unpaidEntity.setKehumingcheng("客户B");
        unpaidEntity.setPaymentStatus("UNPAID");
        chukuxinxiList.add(unpaidEntity);
        
        // 模拟DAO行为
        when(chukuxinxiDao.selectList(any())).thenReturn(chukuxinxiList);
        
        // 执行测试
        FinanceSummary result = financeService.calculateReceivableSummary(query);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.getTotalCount());
        assertEquals(1, result.getPaidCount());
        assertEquals(1, result.getUnpaidCount());
        assertNotNull(result.getTotalAmount());
        assertNotNull(result.getPaidAmount());
        assertNotNull(result.getUnpaidAmount());
        
        // 验证方法调用
        verify(chukuxinxiDao, times(1)).selectList(any());
    }
    
    /**
     * 测试calculatePayableSummary - 基本统计
     * 验证能够正确统计应付款项金额
     */
    @Test
    public void testCalculatePayableSummary_BasicCalculation() {
        // 准备测试数据
        PayableQuery query = new PayableQuery();
        
        List<RukuxinxiEntity> rukuxinxiList = new ArrayList<>();
        
        // 已付款订单
        RukuxinxiEntity paidEntity = new RukuxinxiEntity();
        paidEntity.setId(1L);
        paidEntity.setFuzhuangbianhao("RK001");
        paidEntity.setGongyingshangmingcheng("供应商A");
        paidEntity.setPaymentStatus("PAID");
        rukuxinxiList.add(paidEntity);
        
        // 未付款订单
        RukuxinxiEntity unpaidEntity = new RukuxinxiEntity();
        unpaidEntity.setId(2L);
        unpaidEntity.setFuzhuangbianhao("RK002");
        unpaidEntity.setGongyingshangmingcheng("供应商B");
        unpaidEntity.setPaymentStatus("UNPAID");
        rukuxinxiList.add(unpaidEntity);
        
        // 模拟DAO行为
        when(rukuxinxiDao.selectList(any())).thenReturn(rukuxinxiList);
        
        // 执行测试
        FinanceSummary result = financeService.calculatePayableSummary(query);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.getTotalCount());
        assertEquals(1, result.getPaidCount());
        assertEquals(1, result.getUnpaidCount());
        assertNotNull(result.getTotalAmount());
        assertNotNull(result.getPaidAmount());
        assertNotNull(result.getUnpaidAmount());
        
        // 验证方法调用
        verify(rukuxinxiDao, times(1)).selectList(any());
    }
    
    /**
     * 测试calculateReceivableSummary - 空列表
     * 验证当没有记录时返回零值汇总
     */
    @Test
    public void testCalculateReceivableSummary_EmptyList() {
        // 准备测试数据
        ReceivableQuery query = new ReceivableQuery();
        List<ChukuxinxiEntity> emptyList = new ArrayList<>();
        
        // 模拟DAO行为
        when(chukuxinxiDao.selectList(any())).thenReturn(emptyList);
        
        // 执行测试
        FinanceSummary result = financeService.calculateReceivableSummary(query);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(0, result.getTotalCount());
        assertEquals(0, result.getPaidCount());
        assertEquals(0, result.getUnpaidCount());
        assertEquals(BigDecimal.ZERO, result.getTotalAmount());
        assertEquals(BigDecimal.ZERO, result.getPaidAmount());
        assertEquals(BigDecimal.ZERO, result.getUnpaidAmount());
    }

    @Test
    public void testQueryReceivables_WithFilters() {
        ReceivableQuery query = new ReceivableQuery();
        query.setPage(1);
        query.setLimit(10);
        query.setStartDate(new Date());
        query.setEndDate(new Date());
        query.setPaymentStatus(PaymentStatus.PAID);
        query.setCustomerName("客户A");

        ChukuxinxiEntity entity = new ChukuxinxiEntity();
        entity.setId(1L);
        entity.setFuzhuangbianhao("CK001");
        entity.setKehumingcheng("客户A");
        entity.setPaymentStatus("PAID");
        entity.setJiaohuoshijian(new Date());

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ChukuxinxiEntity> pageResult =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(query.getPage(), query.getLimit());
        pageResult.setRecords(Collections.singletonList(entity));
        when(chukuxinxiDao.selectPage(any(), any())).thenReturn(pageResult);
        when(dinghuoxinxiDao.selectList(any())).thenReturn(Collections.emptyList());

        PageUtils result = financeService.queryReceivables(query);
        assertNotNull(result);
        verify(chukuxinxiDao).selectPage(any(), any());
    }

    @Test
    public void testQueryPayables_WithFilters() {
        PayableQuery query = new PayableQuery();
        query.setPage(1);
        query.setLimit(10);
        query.setStartDate(new Date());
        query.setEndDate(new Date());
        query.setPaymentStatus(PaymentStatus.UNPAID);
        query.setSupplierName("供应商B");

        RukuxinxiEntity entity = new RukuxinxiEntity();
        entity.setId(2L);
        entity.setFuzhuangbianhao("RK002");
        entity.setGongyingshangmingcheng("供应商B");
        entity.setPaymentStatus("UNPAID");
        entity.setRukushijian(new Date());

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<RukuxinxiEntity> pageResult =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(query.getPage(), query.getLimit());
        pageResult.setRecords(Collections.singletonList(entity));
        when(rukuxinxiDao.selectPage(any(), any())).thenReturn(pageResult);
        when(dinghuoxinxiDao.selectById(any())).thenReturn(null);
        when(dinghuoxinxiDao.selectList(any())).thenReturn(Collections.emptyList());

        PageUtils result = financeService.queryPayables(query);
        assertNotNull(result);
    }

    @Test
    public void testUpdatePaymentStatus_InvalidOrderType() {
        assertFalse(financeService.updatePaymentStatus(1L, "UNKNOWN", PaymentStatus.PAID));
        verify(chukuxinxiDao, never()).selectById(any());
        verify(rukuxinxiDao, never()).selectById(any());
    }

    @Test
    public void testUpdatePaymentStatus_InboundNotFound() {
        when(rukuxinxiDao.selectById(99L)).thenReturn(null);
        assertFalse(financeService.updatePaymentStatus(99L, "INBOUND", PaymentStatus.PAID));
    }

    @Test
    public void testUpdatePaymentStatus_UpdateReturnsZero() {
        ChukuxinxiEntity entity = new ChukuxinxiEntity();
        entity.setId(1L);
        when(chukuxinxiDao.selectById(1L)).thenReturn(entity);
        when(chukuxinxiDao.updateById(any(ChukuxinxiEntity.class))).thenReturn(0);
        assertFalse(financeService.updatePaymentStatus(1L, "OUTBOUND", PaymentStatus.PAID));
    }

    @Test
    public void testUpdatePaymentStatus_InboundToUnpaid() {
        RukuxinxiEntity entity = new RukuxinxiEntity();
        entity.setId(1L);
        entity.setPaymentStatus("PAID");
        entity.setPaymentTime(new Date());
        when(rukuxinxiDao.selectById(1L)).thenReturn(entity);
        when(rukuxinxiDao.updateById(any(RukuxinxiEntity.class))).thenReturn(1);

        assertTrue(financeService.updatePaymentStatus(1L, "INBOUND", PaymentStatus.UNPAID));
        assertNull(entity.getPaymentTime());
    }

    @Test
    public void testCalculatePayableSummary_EmptyList() {
        PayableQuery query = new PayableQuery();
        when(rukuxinxiDao.selectList(any())).thenReturn(new ArrayList<RukuxinxiEntity>());

        FinanceSummary result = financeService.calculatePayableSummary(query);
        assertEquals(0, result.getTotalCount());
        assertEquals(BigDecimal.ZERO, result.getTotalAmount());
    }

    @Test
    public void testCalculateReceivableSummary_WithDinghuoAmount() {
        ReceivableQuery query = new ReceivableQuery();
        ChukuxinxiEntity entity = new ChukuxinxiEntity();
        entity.setFuzhuangbianhao("CK001");
        entity.setKehumingcheng("客户A");
        entity.setPaymentStatus("PAID");

        DinghuoxinxiEntity order = new DinghuoxinxiEntity();
        order.setZongjine(500.0);

        when(chukuxinxiDao.selectList(any())).thenReturn(Collections.singletonList(entity));
        when(dinghuoxinxiDao.selectList(any())).thenReturn(Collections.singletonList(order));

        FinanceSummary result = financeService.calculateReceivableSummary(query);
        assertEquals(BigDecimal.valueOf(500.0), result.getTotalAmount());
        assertEquals(1, result.getPaidCount());
    }

    @Test
    public void testCalculatePayableSummary_WithDinghuoId() {
        PayableQuery query = new PayableQuery();
        RukuxinxiEntity entity = new RukuxinxiEntity();
        entity.setDinghuoid("10");
        entity.setPaymentStatus("UNPAID");

        DinghuoxinxiEntity order = new DinghuoxinxiEntity();
        order.setZongjine(300.0);

        when(rukuxinxiDao.selectList(any())).thenReturn(Collections.singletonList(entity));
        when(dinghuoxinxiDao.selectById(10L)).thenReturn(order);

        FinanceSummary result = financeService.calculatePayableSummary(query);
        assertEquals(BigDecimal.valueOf(300.0), result.getUnpaidAmount());
    }

    @Test
    public void testCalculatePayableSummary_InvalidDinghuoIdFallback() {
        PayableQuery query = new PayableQuery();
        RukuxinxiEntity entity = new RukuxinxiEntity();
        entity.setDinghuoid("not-a-number");
        entity.setFuzhuangbianhao("RK001");
        entity.setGongyingshangmingcheng("供应商A");
        entity.setPaymentStatus("UNPAID");

        DinghuoxinxiEntity order = new DinghuoxinxiEntity();
        order.setZongjine(200.0);

        when(rukuxinxiDao.selectList(any())).thenReturn(Collections.singletonList(entity));
        when(dinghuoxinxiDao.selectList(any())).thenReturn(Collections.singletonList(order));

        FinanceSummary result = financeService.calculatePayableSummary(query);
        assertEquals(BigDecimal.valueOf(200.0), result.getTotalAmount());
    }

    @Test
    public void testQueryReceivables_InvalidPaymentStatusDefaultsUnpaid() {
        ReceivableQuery query = new ReceivableQuery();
        query.setPage(1);
        query.setLimit(10);

        ChukuxinxiEntity entity = new ChukuxinxiEntity();
        entity.setId(1L);
        entity.setFuzhuangbianhao("CK001");
        entity.setPaymentStatus("INVALID_STATUS");

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ChukuxinxiEntity> pageResult =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(query.getPage(), query.getLimit());
        pageResult.setRecords(Collections.singletonList(entity));
        when(chukuxinxiDao.selectPage(any(), any())).thenReturn(pageResult);
        when(dinghuoxinxiDao.selectList(any())).thenReturn(Collections.emptyList());

        PageUtils result = financeService.queryReceivables(query);
        assertNotNull(result.getList());
    }

    @Test
    public void testQueryReceivables_NullPaymentStatusDefaultsUnpaid() {
        ReceivableQuery query = new ReceivableQuery();
        query.setPage(1);
        query.setLimit(10);

        ChukuxinxiEntity entity = new ChukuxinxiEntity();
        entity.setId(1L);
        entity.setPaymentStatus(null);

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ChukuxinxiEntity> pageResult =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(query.getPage(), query.getLimit());
        pageResult.setRecords(Collections.singletonList(entity));
        when(chukuxinxiDao.selectPage(any(), any())).thenReturn(pageResult);
        when(dinghuoxinxiDao.selectList(any())).thenReturn(Collections.emptyList());

        PageUtils result = financeService.queryReceivables(query);
        assertNotNull(result);
    }

    @Test
    public void testQueryReceivables_NullPageLimit() {
        ReceivableQuery query = new ReceivableQuery();
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ChukuxinxiEntity> pageResult =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 10);
        pageResult.setRecords(Collections.emptyList());
        when(chukuxinxiDao.selectPage(any(), any())).thenReturn(pageResult);
        PageUtils result = financeService.queryReceivables(query);
        assertNotNull(result);
    }

    @Test
    public void testQueryPayables_NullPageLimit() {
        PayableQuery query = new PayableQuery();
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<RukuxinxiEntity> pageResult =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 10);
        pageResult.setRecords(Collections.emptyList());
        when(rukuxinxiDao.selectPage(any(), any())).thenReturn(pageResult);
        PageUtils result = financeService.queryPayables(query);
        assertNotNull(result);
    }

    @Test
    public void testCalculateReceivableSummary_WithFilters() {
        ReceivableQuery query = new ReceivableQuery();
        query.setStartDate(new Date());
        query.setEndDate(new Date());
        query.setPaymentStatus(PaymentStatus.PAID);
        query.setCustomerName("客户A");

        when(chukuxinxiDao.selectList(any())).thenReturn(Collections.emptyList());
        FinanceSummary result = financeService.calculateReceivableSummary(query);
        assertEquals(0, result.getTotalCount());
    }

    @Test
    public void testCalculatePayableSummary_WithFilters() {
        PayableQuery query = new PayableQuery();
        query.setStartDate(new Date());
        query.setPaymentStatus(PaymentStatus.UNPAID);
        query.setSupplierName("供应商");

        when(rukuxinxiDao.selectList(any())).thenReturn(Collections.emptyList());
        FinanceSummary result = financeService.calculatePayableSummary(query);
        assertEquals(0, result.getTotalCount());
    }

    @Test
    public void testCalculateReceivableSummary_NullZongjine() {
        ReceivableQuery query = new ReceivableQuery();
        ChukuxinxiEntity entity = new ChukuxinxiEntity();
        entity.setFuzhuangbianhao("CK001");
        entity.setKehumingcheng("客户A");
        entity.setPaymentStatus("UNPAID");

        DinghuoxinxiEntity order = new DinghuoxinxiEntity();
        order.setZongjine(null);

        when(chukuxinxiDao.selectList(any())).thenReturn(Collections.singletonList(entity));
        when(dinghuoxinxiDao.selectList(any())).thenReturn(Collections.singletonList(order));

        FinanceSummary result = financeService.calculateReceivableSummary(query);
        assertEquals(BigDecimal.ZERO, result.getTotalAmount());
    }

    @Test
    public void testCalculatePayableSummary_ByDinghuoIdNullAmount() {
        PayableQuery query = new PayableQuery();
        RukuxinxiEntity entity = new RukuxinxiEntity();
        entity.setDinghuoid("5");
        entity.setPaymentStatus("PAID");

        DinghuoxinxiEntity order = new DinghuoxinxiEntity();
        order.setZongjine(null);

        when(rukuxinxiDao.selectList(any())).thenReturn(Collections.singletonList(entity));
        when(dinghuoxinxiDao.selectById(5L)).thenReturn(order);

        FinanceSummary result = financeService.calculatePayableSummary(query);
        assertEquals(BigDecimal.ZERO, result.getTotalAmount());
    }

    @Test
    public void testCalculatePayableSummary_EmptyDinghuoId() {
        PayableQuery query = new PayableQuery();
        RukuxinxiEntity entity = new RukuxinxiEntity();
        entity.setDinghuoid("  ");
        entity.setFuzhuangbianhao("RK001");
        entity.setGongyingshangmingcheng("供应商A");
        entity.setPaymentStatus("UNPAID");

        when(rukuxinxiDao.selectList(any())).thenReturn(Collections.singletonList(entity));
        when(dinghuoxinxiDao.selectList(any())).thenReturn(Collections.emptyList());

        FinanceSummary result = financeService.calculatePayableSummary(query);
        assertEquals(BigDecimal.ZERO, result.getTotalAmount());
    }

    @Test
    public void testQueryPayables_InvalidPaymentStatusDefaultsUnpaid() {
        PayableQuery query = new PayableQuery();
        query.setPage(1);
        query.setLimit(10);

        RukuxinxiEntity entity = new RukuxinxiEntity();
        entity.setId(1L);
        entity.setPaymentStatus("BAD");

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<RukuxinxiEntity> pageResult =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(query.getPage(), query.getLimit());
        pageResult.setRecords(Collections.singletonList(entity));
        when(rukuxinxiDao.selectPage(any(), any())).thenReturn(pageResult);
        when(dinghuoxinxiDao.selectById(any())).thenReturn(null);
        when(dinghuoxinxiDao.selectList(any())).thenReturn(Collections.emptyList());

        PageUtils result = financeService.queryPayables(query);
        assertNotNull(result.getList());
    }

    @Test
    public void testQueryPayables_NullPaymentStatus() {
        PayableQuery query = new PayableQuery();
        RukuxinxiEntity entity = new RukuxinxiEntity();
        entity.setPaymentStatus(null);

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<RukuxinxiEntity> pageResult =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 10);
        pageResult.setRecords(Collections.singletonList(entity));
        when(rukuxinxiDao.selectPage(any(), any())).thenReturn(pageResult);
        when(dinghuoxinxiDao.selectList(any())).thenReturn(Collections.emptyList());

        PageUtils result = financeService.queryPayables(query);
        assertNotNull(result);
    }

    @Test
    public void testCalculatePayableSummary_NullZongjineFromList() {
        PayableQuery query = new PayableQuery();
        RukuxinxiEntity entity = new RukuxinxiEntity();
        entity.setFuzhuangbianhao("RK001");
        entity.setGongyingshangmingcheng("供应商A");
        entity.setPaymentStatus("PAID");

        DinghuoxinxiEntity order = new DinghuoxinxiEntity();
        order.setZongjine(null);

        when(rukuxinxiDao.selectList(any())).thenReturn(Collections.singletonList(entity));
        when(dinghuoxinxiDao.selectList(any())).thenReturn(Collections.singletonList(order));

        FinanceSummary result = financeService.calculatePayableSummary(query);
        assertEquals(BigDecimal.ZERO, result.getTotalAmount());
    }

    @Test
    public void testUpdatePaymentStatus_ExceptionReturnsFalse() {
        when(chukuxinxiDao.selectById(1L)).thenThrow(new RuntimeException("db"));
        assertFalse(financeService.updatePaymentStatus(1L, "OUTBOUND", PaymentStatus.PAID));
    }

    @Test
    public void testQueryReceivables_EmptyCustomerNameSkipped() {
        ReceivableQuery query = new ReceivableQuery();
        query.setCustomerName("   ");
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ChukuxinxiEntity> pageResult =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 10);
        pageResult.setRecords(Collections.emptyList());
        when(chukuxinxiDao.selectPage(any(), any())).thenReturn(pageResult);
        PageUtils result = financeService.queryReceivables(query);
        assertNotNull(result);
    }

    @Test
    public void testQueryPayables_EmptySupplierNameSkipped() {
        PayableQuery query = new PayableQuery();
        query.setSupplierName("  ");
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<RukuxinxiEntity> pageResult =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 10);
        pageResult.setRecords(Collections.emptyList());
        when(rukuxinxiDao.selectPage(any(), any())).thenReturn(pageResult);
        PageUtils result = financeService.queryPayables(query);
        assertNotNull(result);
    }

    @Test
    public void testCalculateReceivableSummary_EmptyCustomerName() {
        ReceivableQuery query = new ReceivableQuery();
        query.setCustomerName("  ");
        when(chukuxinxiDao.selectList(any())).thenReturn(Collections.emptyList());
        FinanceSummary result = financeService.calculateReceivableSummary(query);
        assertEquals(0, result.getTotalCount());
    }

    @Test
    public void testCalculatePayableSummary_ByDinghuoIdNotFound() {
        PayableQuery query = new PayableQuery();
        RukuxinxiEntity entity = new RukuxinxiEntity();
        entity.setDinghuoid("99");
        entity.setPaymentStatus("UNPAID");

        when(rukuxinxiDao.selectList(any())).thenReturn(Collections.singletonList(entity));
        when(dinghuoxinxiDao.selectById(99L)).thenReturn(null);
        when(dinghuoxinxiDao.selectList(any())).thenReturn(Collections.emptyList());

        FinanceSummary result = financeService.calculatePayableSummary(query);
        assertEquals(BigDecimal.ZERO, result.getTotalAmount());
    }

    @Test
    public void testUpdatePaymentStatus_InboundUnpaidClearsPaymentTime() {
        Long orderId = 2L;
        RukuxinxiEntity entity = new RukuxinxiEntity();
        entity.setId(orderId);
        entity.setPaymentStatus("PAID");
        entity.setPaymentTime(new Date());

        when(rukuxinxiDao.selectById(orderId)).thenReturn(entity);
        when(rukuxinxiDao.updateById(any(RukuxinxiEntity.class))).thenReturn(1);

        boolean result = financeService.updatePaymentStatus(orderId, "INBOUND", PaymentStatus.UNPAID);
        assertTrue(result);
        assertEquals("UNPAID", entity.getPaymentStatus());
        assertNull(entity.getPaymentTime());
    }

    @Test
    public void testCalculateReceivableSummary_WithPaidAmount() {
        ReceivableQuery query = new ReceivableQuery();
        ChukuxinxiEntity entity = new ChukuxinxiEntity();
        entity.setFuzhuangbianhao("CK002");
        entity.setKehumingcheng("客户B");
        entity.setPaymentStatus("PAID");

        DinghuoxinxiEntity order = new DinghuoxinxiEntity();
        order.setZongjine(200.0);

        when(chukuxinxiDao.selectList(any())).thenReturn(Collections.singletonList(entity));
        when(dinghuoxinxiDao.selectList(any())).thenReturn(Collections.singletonList(order));

        FinanceSummary result = financeService.calculateReceivableSummary(query);
        assertEquals(BigDecimal.valueOf(200.0), result.getTotalAmount());
        assertEquals(1, result.getPaidCount());
    }

    @Test
    public void testCalculatePayableSummary_WithValidDinghuoId() {
        PayableQuery query = new PayableQuery();
        RukuxinxiEntity entity = new RukuxinxiEntity();
        entity.setDinghuoid("7");
        entity.setPaymentStatus("UNPAID");

        DinghuoxinxiEntity order = new DinghuoxinxiEntity();
        order.setZongjine(300.0);

        when(rukuxinxiDao.selectList(any())).thenReturn(Collections.singletonList(entity));
        when(dinghuoxinxiDao.selectById(7L)).thenReturn(order);

        FinanceSummary result = financeService.calculatePayableSummary(query);
        assertEquals(BigDecimal.valueOf(300.0), result.getTotalAmount());
    }
}
