package com.service;

import java.util.List;

/**
 * 报表导出服务接口
 */
public interface ExportService {
    
    /**
     * 导出Excel文件
     * 
     * @param data 数据列表
     * @param headers 列标题数组
     * @param fields 字段名数组（对应数据对象的属性名）
     * @return Excel文件的字节数组
     */
    byte[] exportToExcel(List<?> data, String[] headers, String[] fields);
    
    /**
     * 导出PDF文件
     * 
     * @param data 数据列表
     * @param title 报表标题
     * @param headers 列标题数组
     * @param fields 字段名数组（对应数据对象的属性名）
     * @return PDF文件的字节数组
     */
    byte[] exportToPDF(List<?> data, String title, String[] headers, String[] fields);
    
    /**
     * 验证导出数据量
     * 
     * @param dataSize 数据量
     * @param exportType 导出类型（EXCEL或PDF）
     * @return 是否通过验证
     */
    boolean validateExportSize(int dataSize, String exportType);
}
