package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dao.ChukuxinxiDao;
import com.dao.DinghuoxinxiDao;
import com.dao.RukuxinxiDao;
import com.dto.FinanceSummary;
import com.dto.PayableDTO;
import com.dto.PayableQuery;
import com.dto.ReceivableDTO;
import com.dto.ReceivableQuery;
import com.entity.ChukuxinxiEntity;
import com.entity.DinghuoxinxiEntity;
import com.entity.RukuxinxiEntity;
import com.enums.PaymentStatus;
import com.service.FinanceService;
import com.utils.PageUtils;
import com.utils.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 财务服务实现类
 * 
 * @author 
 * @email 
 * @date 2025-01-02
 */
@Service("financeService")
public class FinanceServiceImpl implements FinanceService {
    
    @Autowired
    private ChukuxinxiDao chukuxinxiDao;
    
    @Autowired
    private RukuxinxiDao rukuxinxiDao;
    
    @Autowired
    private DinghuoxinxiDao dinghuoxinxiDao;
    
    @Override
    public PageUtils queryReceivables(ReceivableQuery query) {
        Map<String, Object> params = new HashMap<>();
        params.put("page", query.getPage() != null ? query.getPage().toString() : "1");
        params.put("limit", query.getLimit() != null ? query.getLimit().toString() : "10");
        
        Page<ChukuxinxiEntity> page = new Query<ChukuxinxiEntity>(params).getPage();
        QueryWrapper<ChukuxinxiEntity> wrapper = new QueryWrapper<>();
        
        // 添加日期范围筛选
        if (query.getStartDate() != null) {
            wrapper.ge("jiaohuoshijian", query.getStartDate());
        }
        if (query.getEndDate() != null) {
            wrapper.le("jiaohuoshijian", query.getEndDate());
        }
        
        // 添加付款状态筛选
        if (query.getPaymentStatus() != null) {
            wrapper.eq("payment_status", query.getPaymentStatus().name());
        }
        
        // 添加客户名称筛选（模糊查询）
        if (query.getCustomerName() != null && !query.getCustomerName().trim().isEmpty()) {
            wrapper.like("kehumingcheng", query.getCustomerName());
        }
        
        // 按交货时间降序排列
        wrapper.orderBy(true, false, "jiaohuoshijian");
        
        // 查询出库信息
        IPage<ChukuxinxiEntity> resultPage = chukuxinxiDao.selectPage(page, wrapper);
        List<ChukuxinxiEntity> chukuxinxiList = resultPage.getRecords();
        
        // 转换为ReceivableDTO
        List<ReceivableDTO> receivableDTOList = new ArrayList<>();
        for (ChukuxinxiEntity chukuxinxi : chukuxinxiList) {
            ReceivableDTO dto = convertToReceivableDTO(chukuxinxi);
            receivableDTOList.add(dto);
        }
        
        resultPage.setRecords((List) receivableDTOList);
        return new PageUtils(resultPage);
    }
    
    @Override
    public PageUtils queryPayables(PayableQuery query) {
        Map<String, Object> params = new HashMap<>();
        params.put("page", query.getPage() != null ? query.getPage().toString() : "1");
        params.put("limit", query.getLimit() != null ? query.getLimit().toString() : "10");
        
        Page<RukuxinxiEntity> page = new Query<RukuxinxiEntity>(params).getPage();
        QueryWrapper<RukuxinxiEntity> wrapper = new QueryWrapper<>();
        
        // 添加日期范围筛选
        if (query.getStartDate() != null) {
            wrapper.ge("rukushijian", query.getStartDate());
        }
        if (query.getEndDate() != null) {
            wrapper.le("rukushijian", query.getEndDate());
        }
        
        // 添加付款状态筛选
        if (query.getPaymentStatus() != null) {
            wrapper.eq("payment_status", query.getPaymentStatus().name());
        }
        
        // 添加供应商名称筛选（模糊查询）
        if (query.getSupplierName() != null && !query.getSupplierName().trim().isEmpty()) {
            wrapper.like("gongyingshangmingcheng", query.getSupplierName());
        }
        
        // 按入库时间降序排列
        wrapper.orderBy(true, false, "rukushijian");
        
        // 查询入库信息
        IPage<RukuxinxiEntity> resultPage = rukuxinxiDao.selectPage(page, wrapper);
        List<RukuxinxiEntity> rukuxinxiList = resultPage.getRecords();
        
        // 转换为PayableDTO
        List<PayableDTO> payableDTOList = new ArrayList<>();
        for (RukuxinxiEntity rukuxinxi : rukuxinxiList) {
            PayableDTO dto = convertToPayableDTO(rukuxinxi);
            payableDTOList.add(dto);
        }
        
        resultPage.setRecords((List) payableDTOList);
        return new PageUtils(resultPage);
    }

    
    @Override
    @Transactional
    public boolean updatePaymentStatus(Long orderId, String orderType, PaymentStatus status) {
        if (orderId == null || orderType == null || status == null) {
            return false;
        }
        
        try {
            if ("OUTBOUND".equalsIgnoreCase(orderType)) {
                // 更新出库订单的付款状态
                ChukuxinxiEntity chukuxinxi = chukuxinxiDao.selectById(orderId);
                if (chukuxinxi == null) {
                    return false;
                }
                
                chukuxinxi.setPaymentStatus(status.name());
                
                // 如果状态为已付款，记录付款时间；如果为未付款，清除付款时间
                if (status == PaymentStatus.PAID) {
                    chukuxinxi.setPaymentTime(new Date());
                } else {
                    chukuxinxi.setPaymentTime(null);
                }
                
                return chukuxinxiDao.updateById(chukuxinxi) > 0;
                
            } else if ("INBOUND".equalsIgnoreCase(orderType)) {
                // 更新入库订单的付款状态
                RukuxinxiEntity rukuxinxi = rukuxinxiDao.selectById(orderId);
                if (rukuxinxi == null) {
                    return false;
                }
                
                rukuxinxi.setPaymentStatus(status.name());
                
                // 如果状态为已付款，记录付款时间；如果为未付款，清除付款时间
                if (status == PaymentStatus.PAID) {
                    rukuxinxi.setPaymentTime(new Date());
                } else {
                    rukuxinxi.setPaymentTime(null);
                }
                
                return rukuxinxiDao.updateById(rukuxinxi) > 0;
            }
            
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public FinanceSummary calculateReceivableSummary(ReceivableQuery query) {
        QueryWrapper<ChukuxinxiEntity> wrapper = new QueryWrapper<>();
        
        // 添加日期范围筛选
        if (query.getStartDate() != null) {
            wrapper.ge("jiaohuoshijian", query.getStartDate());
        }
        if (query.getEndDate() != null) {
            wrapper.le("jiaohuoshijian", query.getEndDate());
        }
        
        // 添加付款状态筛选
        if (query.getPaymentStatus() != null) {
            wrapper.eq("payment_status", query.getPaymentStatus().name());
        }
        
        // 添加客户名称筛选（模糊查询）
        if (query.getCustomerName() != null && !query.getCustomerName().trim().isEmpty()) {
            wrapper.like("kehumingcheng", query.getCustomerName());
        }
        
        // 查询所有符合条件的出库记录
        List<ChukuxinxiEntity> chukuxinxiList = chukuxinxiDao.selectList(wrapper);
        
        FinanceSummary summary = new FinanceSummary();
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal paidAmount = BigDecimal.ZERO;
        BigDecimal unpaidAmount = BigDecimal.ZERO;
        int totalCount = 0;
        int paidCount = 0;
        int unpaidCount = 0;
        
        for (ChukuxinxiEntity chukuxinxi : chukuxinxiList) {
            BigDecimal amount = calculateReceivableAmount(chukuxinxi);
            totalAmount = totalAmount.add(amount);
            totalCount++;
            
            if (PaymentStatus.PAID.name().equals(chukuxinxi.getPaymentStatus())) {
                paidAmount = paidAmount.add(amount);
                paidCount++;
            } else {
                unpaidAmount = unpaidAmount.add(amount);
                unpaidCount++;
            }
        }
        
        summary.setTotalAmount(totalAmount);
        summary.setPaidAmount(paidAmount);
        summary.setUnpaidAmount(unpaidAmount);
        summary.setTotalCount(totalCount);
        summary.setPaidCount(paidCount);
        summary.setUnpaidCount(unpaidCount);
        
        return summary;
    }
    
    @Override
    public FinanceSummary calculatePayableSummary(PayableQuery query) {
        QueryWrapper<RukuxinxiEntity> wrapper = new QueryWrapper<>();
        
        // 添加日期范围筛选
        if (query.getStartDate() != null) {
            wrapper.ge("rukushijian", query.getStartDate());
        }
        if (query.getEndDate() != null) {
            wrapper.le("rukushijian", query.getEndDate());
        }
        
        // 添加付款状态筛选
        if (query.getPaymentStatus() != null) {
            wrapper.eq("payment_status", query.getPaymentStatus().name());
        }
        
        // 添加供应商名称筛选（模糊查询）
        if (query.getSupplierName() != null && !query.getSupplierName().trim().isEmpty()) {
            wrapper.like("gongyingshangmingcheng", query.getSupplierName());
        }
        
        // 查询所有符合条件的入库记录
        List<RukuxinxiEntity> rukuxinxiList = rukuxinxiDao.selectList(wrapper);
        
        FinanceSummary summary = new FinanceSummary();
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal paidAmount = BigDecimal.ZERO;
        BigDecimal unpaidAmount = BigDecimal.ZERO;
        int totalCount = 0;
        int paidCount = 0;
        int unpaidCount = 0;
        
        for (RukuxinxiEntity rukuxinxi : rukuxinxiList) {
            BigDecimal amount = calculatePayableAmount(rukuxinxi);
            totalAmount = totalAmount.add(amount);
            totalCount++;
            
            if (PaymentStatus.PAID.name().equals(rukuxinxi.getPaymentStatus())) {
                paidAmount = paidAmount.add(amount);
                paidCount++;
            } else {
                unpaidAmount = unpaidAmount.add(amount);
                unpaidCount++;
            }
        }
        
        summary.setTotalAmount(totalAmount);
        summary.setPaidAmount(paidAmount);
        summary.setUnpaidAmount(unpaidAmount);
        summary.setTotalCount(totalCount);
        summary.setPaidCount(paidCount);
        summary.setUnpaidCount(unpaidCount);
        
        return summary;
    }
    
    /**
     * 转换出库信息为应收款项DTO
     */
    private ReceivableDTO convertToReceivableDTO(ChukuxinxiEntity chukuxinxi) {
        ReceivableDTO dto = new ReceivableDTO();
        dto.setId(chukuxinxi.getId());
        dto.setOrderNo(chukuxinxi.getFuzhuangbianhao());
        dto.setCustomerName(chukuxinxi.getKehumingcheng());
        dto.setAmount(calculateReceivableAmount(chukuxinxi));
        dto.setOrderDate(chukuxinxi.getJiaohuoshijian());
        dto.setPaymentTime(chukuxinxi.getPaymentTime());
        
        // 设置付款状态
        if (chukuxinxi.getPaymentStatus() != null) {
            try {
                dto.setPaymentStatus(PaymentStatus.valueOf(chukuxinxi.getPaymentStatus()));
            } catch (IllegalArgumentException e) {
                dto.setPaymentStatus(PaymentStatus.UNPAID);
            }
        } else {
            dto.setPaymentStatus(PaymentStatus.UNPAID);
        }
        
        return dto;
    }
    
    /**
     * 转换入库信息为应付款项DTO
     */
    private PayableDTO convertToPayableDTO(RukuxinxiEntity rukuxinxi) {
        PayableDTO dto = new PayableDTO();
        dto.setId(rukuxinxi.getId());
        dto.setOrderNo(rukuxinxi.getFuzhuangbianhao());
        dto.setSupplierName(rukuxinxi.getGongyingshangmingcheng());
        dto.setAmount(calculatePayableAmount(rukuxinxi));
        dto.setOrderDate(rukuxinxi.getRukushijian());
        dto.setPaymentTime(rukuxinxi.getPaymentTime());
        
        // 设置付款状态
        if (rukuxinxi.getPaymentStatus() != null) {
            try {
                dto.setPaymentStatus(PaymentStatus.valueOf(rukuxinxi.getPaymentStatus()));
            } catch (IllegalArgumentException e) {
                dto.setPaymentStatus(PaymentStatus.UNPAID);
            }
        } else {
            dto.setPaymentStatus(PaymentStatus.UNPAID);
        }
        
        return dto;
    }
    
    /**
     * 计算应收金额
     * 通过查询关联的订货信息获取总金额
     */
    private BigDecimal calculateReceivableAmount(ChukuxinxiEntity chukuxinxi) {
        // 尝试通过服装编号查找对应的订货信息
        QueryWrapper<DinghuoxinxiEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("fuzhuangbianhao", chukuxinxi.getFuzhuangbianhao());
        wrapper.eq("kehumingcheng", chukuxinxi.getKehumingcheng());
        wrapper.orderBy(true, false, "addtime");
        
        List<DinghuoxinxiEntity> dinghuoxinxiList = dinghuoxinxiDao.selectList(wrapper);
        
        if (dinghuoxinxiList != null && !dinghuoxinxiList.isEmpty()) {
            DinghuoxinxiEntity dinghuoxinxi = dinghuoxinxiList.get(0);
            if (dinghuoxinxi.getZongjine() != null) {
                return BigDecimal.valueOf(dinghuoxinxi.getZongjine());
            }
        }
        
        // 如果找不到订货信息，返回0
        return BigDecimal.ZERO;
    }
    
    /**
     * 计算应付金额
     * 通过查询关联的订货信息获取总金额
     */
    private BigDecimal calculatePayableAmount(RukuxinxiEntity rukuxinxi) {
        // 如果有订货ID，直接查询
        if (rukuxinxi.getDinghuoid() != null && !rukuxinxi.getDinghuoid().trim().isEmpty()) {
            try {
                Long dinghuoId = Long.parseLong(rukuxinxi.getDinghuoid());
                DinghuoxinxiEntity dinghuoxinxi = dinghuoxinxiDao.selectById(dinghuoId);
                if (dinghuoxinxi != null && dinghuoxinxi.getZongjine() != null) {
                    return BigDecimal.valueOf(dinghuoxinxi.getZongjine());
                }
            } catch (NumberFormatException e) {
                // 如果订货ID不是数字，尝试其他方式
            }
        }
        
        // 尝试通过服装编号和供应商名称查找对应的订货信息
        QueryWrapper<DinghuoxinxiEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("fuzhuangbianhao", rukuxinxi.getFuzhuangbianhao());
        wrapper.eq("gongyingshangmingcheng", rukuxinxi.getGongyingshangmingcheng());
        wrapper.orderBy(true, false, "addtime");
        
        List<DinghuoxinxiEntity> dinghuoxinxiList = dinghuoxinxiDao.selectList(wrapper);
        
        if (dinghuoxinxiList != null && !dinghuoxinxiList.isEmpty()) {
            DinghuoxinxiEntity dinghuoxinxi = dinghuoxinxiList.get(0);
            if (dinghuoxinxi.getZongjine() != null) {
                return BigDecimal.valueOf(dinghuoxinxi.getZongjine());
            }
        }
        
        // 如果找不到订货信息，返回0
        return BigDecimal.ZERO;
    }
}
