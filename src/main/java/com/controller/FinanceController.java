package com.controller;

import com.dto.*;
import com.enums.PaymentStatus;
import com.service.FinanceService;
import com.service.ExportService;
import com.utils.PageUtils;
import com.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 财务管理控制器
 * 提供应收应付款项查询、统计和导出功能
 * 
 * @author 
 * @email 
 * @date 2025-01-02
 */
@RestController
@RequestMapping("/finance")
public class FinanceController {
    
    @Autowired
    private FinanceService financeService;
    
    @Autowired
    private ExportService exportService;
    
    /**
     * 查询应收款项列表
     * 
     * @param params 查询参数（startDate, endDate, paymentStatus, customerName, page, limit）
     * @return 分页结果
     */
    @GetMapping("/receivables")
    public R queryReceivables(@RequestParam Map<String, Object> params) {
        try {
            ReceivableQuery query = buildReceivableQuery(params);
            PageUtils page = financeService.queryReceivables(query);
            return R.ok().put("data", page);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("查询应收款项失败：" + e.getMessage());
        }
    }
    
    /**
     * 查询应付款项列表
     * 
     * @param params 查询参数（startDate, endDate, paymentStatus, supplierName, page, limit）
     * @return 分页结果
     */
    @GetMapping("/payables")
    public R queryPayables(@RequestParam Map<String, Object> params) {
        try {
            PayableQuery query = buildPayableQuery(params);
            PageUtils page = financeService.queryPayables(query);
            return R.ok().put("data", page);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("查询应付款项失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新付款状态
     * 
     * @param dto 付款状态更新DTO
     * @return 更新结果
     */
    @PostMapping("/updatePaymentStatus")
    public R updatePaymentStatus(@RequestBody PaymentUpdateDTO dto) {
        if (dto.getOrderId() == null) {
            return R.error("订单ID不能为空");
        }
        if (dto.getOrderType() == null || dto.getOrderType().isEmpty()) {
            return R.error("订单类型不能为空");
        }
        if (dto.getPaymentStatus() == null) {
            return R.error("付款状态不能为空");
        }
        
        try {
            boolean success = financeService.updatePaymentStatus(
                dto.getOrderId(), 
                dto.getOrderType(), 
                dto.getPaymentStatus()
            );
            
            if (success) {
                return R.ok("付款状态更新成功");
            } else {
                return R.error("付款状态更新失败，订单不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("付款状态更新失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取应收款项统计汇总
     * 
     * @param params 查询参数
     * @return 统计汇总信息
     */
    @GetMapping("/receivables/summary")
    public R getReceivableSummary(@RequestParam Map<String, Object> params) {
        try {
            ReceivableQuery query = buildReceivableQuery(params);
            FinanceSummary summary = financeService.calculateReceivableSummary(query);
            return R.ok().put("data", summary);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("统计应收款项失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取应付款项统计汇总
     * 
     * @param params 查询参数
     * @return 统计汇总信息
     */
    @GetMapping("/payables/summary")
    public R getPayableSummary(@RequestParam Map<String, Object> params) {
        try {
            PayableQuery query = buildPayableQuery(params);
            FinanceSummary summary = financeService.calculatePayableSummary(query);
            return R.ok().put("data", summary);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("统计应付款项失败：" + e.getMessage());
        }
    }
    
    /**
     * 导出应收款项到Excel
     * 
     * @param params 查询参数
     * @param response HTTP响应
     */
    @GetMapping("/receivables/export/excel")
    public void exportReceivablesToExcel(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        try {
            // 查询数据（不分页，获取所有符合条件的数据）
            ReceivableQuery query = buildReceivableQuery(params);
            query.setPage(1);
            query.setLimit(10000); // 最大导出10000条
            
            PageUtils page = financeService.queryReceivables(query);
            List<ReceivableDTO> dataList = (List<ReceivableDTO>) page.getList();
            
            // 验证导出数据量
            if (!exportService.validateExportSize(dataList.size(), "EXCEL")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("导出数据量过大，请缩小查询范围");
                return;
            }
            
            // 定义Excel列标题和字段
            String[] headers = {"订单号", "客户名称", "应收金额", "付款状态", "订单日期", "付款时间"};
            String[] fields = {"orderNo", "customerName", "amount", "paymentStatusDisplayName", "orderDate", "paymentTime"};
            
            // 生成Excel文件
            byte[] excelBytes = exportService.exportToExcel(dataList, headers, fields);
            
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            String fileName = "应收款项_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xlsx";
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
            
            // 写入响应流
            OutputStream out = response.getOutputStream();
            out.write(excelBytes);
            out.flush();
            out.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Excel生成失败：" + e.getMessage());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
    
    /**
     * 导出应收款项到PDF
     * 
     * @param params 查询参数
     * @param response HTTP响应
     */
    @GetMapping("/receivables/export/pdf")
    public void exportReceivablesToPDF(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        try {
            // 查询数据（不分页，获取所有符合条件的数据）
            ReceivableQuery query = buildReceivableQuery(params);
            query.setPage(1);
            query.setLimit(1000); // 最大导出1000条
            
            PageUtils page = financeService.queryReceivables(query);
            List<ReceivableDTO> dataList = (List<ReceivableDTO>) page.getList();
            
            // 验证导出数据量
            if (!exportService.validateExportSize(dataList.size(), "PDF")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("导出数据量过大，请缩小查询范围");
                return;
            }
            
            // 定义PDF列标题和字段
            String title = "应收款项报表";
            String[] headers = {"订单号", "客户名称", "应收金额", "付款状态", "订单日期", "付款时间"};
            String[] fields = {"orderNo", "customerName", "amount", "paymentStatusDisplayName", "orderDate", "paymentTime"};
            
            // 生成PDF文件
            byte[] pdfBytes = exportService.exportToPDF(dataList, title, headers, fields);
            
            // 设置响应头
            response.setContentType("application/pdf");
            response.setCharacterEncoding("UTF-8");
            String fileName = "应收款项_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".pdf";
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
            
            // 写入响应流
            OutputStream out = response.getOutputStream();
            out.write(pdfBytes);
            out.flush();
            out.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("PDF生成失败：" + e.getMessage());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
    
    /**
     * 导出应付款项到Excel
     * 
     * @param params 查询参数
     * @param response HTTP响应
     */
    @GetMapping("/payables/export/excel")
    public void exportPayablesToExcel(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        try {
            // 查询数据（不分页，获取所有符合条件的数据）
            PayableQuery query = buildPayableQuery(params);
            query.setPage(1);
            query.setLimit(10000); // 最大导出10000条
            
            PageUtils page = financeService.queryPayables(query);
            List<PayableDTO> dataList = (List<PayableDTO>) page.getList();
            
            // 验证导出数据量
            if (!exportService.validateExportSize(dataList.size(), "EXCEL")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("导出数据量过大，请缩小查询范围");
                return;
            }
            
            // 定义Excel列标题和字段
            String[] headers = {"订单号", "供应商名称", "应付金额", "付款状态", "订单日期", "付款时间"};
            String[] fields = {"orderNo", "supplierName", "amount", "paymentStatusDisplayName", "orderDate", "paymentTime"};
            
            // 生成Excel文件
            byte[] excelBytes = exportService.exportToExcel(dataList, headers, fields);
            
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            String fileName = "应付款项_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xlsx";
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
            
            // 写入响应流
            OutputStream out = response.getOutputStream();
            out.write(excelBytes);
            out.flush();
            out.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Excel生成失败：" + e.getMessage());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
    
    /**
     * 导出应付款项到PDF
     * 
     * @param params 查询参数
     * @param response HTTP响应
     */
    @GetMapping("/payables/export/pdf")
    public void exportPayablesToPDF(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        try {
            // 查询数据（不分页，获取所有符合条件的数据）
            PayableQuery query = buildPayableQuery(params);
            query.setPage(1);
            query.setLimit(1000); // 最大导出1000条
            
            PageUtils page = financeService.queryPayables(query);
            List<PayableDTO> dataList = (List<PayableDTO>) page.getList();
            
            // 验证导出数据量
            if (!exportService.validateExportSize(dataList.size(), "PDF")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("导出数据量过大，请缩小查询范围");
                return;
            }
            
            // 定义PDF列标题和字段
            String title = "应付款项报表";
            String[] headers = {"订单号", "供应商名称", "应付金额", "付款状态", "订单日期", "付款时间"};
            String[] fields = {"orderNo", "supplierName", "amount", "paymentStatusDisplayName", "orderDate", "paymentTime"};
            
            // 生成PDF文件
            byte[] pdfBytes = exportService.exportToPDF(dataList, title, headers, fields);
            
            // 设置响应头
            response.setContentType("application/pdf");
            response.setCharacterEncoding("UTF-8");
            String fileName = "应付款项_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".pdf";
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
            
            // 写入响应流
            OutputStream out = response.getOutputStream();
            out.write(pdfBytes);
            out.flush();
            out.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("PDF生成失败：" + e.getMessage());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
    
    /**
     * 构建应收款项查询对象
     * 
     * @param params 请求参数
     * @return 应收款项查询对象
     */
    private ReceivableQuery buildReceivableQuery(Map<String, Object> params) {
        ReceivableQuery query = new ReceivableQuery();
        
        // 设置日期范围
        if (params.containsKey("startDate") && params.get("startDate") != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                query.setStartDate(sdf.parse(params.get("startDate").toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        if (params.containsKey("endDate") && params.get("endDate") != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                query.setEndDate(sdf.parse(params.get("endDate").toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // 设置付款状态
        if (params.containsKey("paymentStatus") && params.get("paymentStatus") != null 
            && !params.get("paymentStatus").toString().isEmpty()) {
            try {
                query.setPaymentStatus(PaymentStatus.valueOf(params.get("paymentStatus").toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // 设置客户名称
        if (params.containsKey("customerName") && params.get("customerName") != null) {
            query.setCustomerName(params.get("customerName").toString());
        }
        
        // 设置分页参数
        if (params.containsKey("page") && params.get("page") != null) {
            query.setPage(Integer.parseInt(params.get("page").toString()));
        } else {
            query.setPage(1);
        }
        
        if (params.containsKey("limit") && params.get("limit") != null) {
            query.setLimit(Integer.parseInt(params.get("limit").toString()));
        } else {
            query.setLimit(10);
        }
        
        return query;
    }
    
    /**
     * 构建应付款项查询对象
     * 
     * @param params 请求参数
     * @return 应付款项查询对象
     */
    private PayableQuery buildPayableQuery(Map<String, Object> params) {
        PayableQuery query = new PayableQuery();
        
        // 设置日期范围
        if (params.containsKey("startDate") && params.get("startDate") != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                query.setStartDate(sdf.parse(params.get("startDate").toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        if (params.containsKey("endDate") && params.get("endDate") != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                query.setEndDate(sdf.parse(params.get("endDate").toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // 设置付款状态
        if (params.containsKey("paymentStatus") && params.get("paymentStatus") != null 
            && !params.get("paymentStatus").toString().isEmpty()) {
            try {
                query.setPaymentStatus(PaymentStatus.valueOf(params.get("paymentStatus").toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // 设置供应商名称
        if (params.containsKey("supplierName") && params.get("supplierName") != null) {
            query.setSupplierName(params.get("supplierName").toString());
        }
        
        // 设置分页参数
        if (params.containsKey("page") && params.get("page") != null) {
            query.setPage(Integer.parseInt(params.get("page").toString()));
        } else {
            query.setPage(1);
        }
        
        if (params.containsKey("limit") && params.get("limit") != null) {
            query.setLimit(Integer.parseInt(params.get("limit").toString()));
        } else {
            query.setLimit(10);
        }
        
        return query;
    }
}
