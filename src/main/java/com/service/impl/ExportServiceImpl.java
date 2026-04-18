package com.service.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.service.ExportService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 报表导出服务实现类
 */
@Service("exportService")
public class ExportServiceImpl implements ExportService {
    
    private static final int EXCEL_MAX_SIZE = 10000;
    private static final int PDF_MAX_SIZE = 1000;
    
    @Override
    public byte[] exportToExcel(List<?> data, String[] headers, String[] fields) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("数据报表");
            
            // 创建标题行样式
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(CellStyle.BORDER_THIN);
            headerStyle.setBorderTop(CellStyle.BORDER_THIN);
            headerStyle.setBorderLeft(CellStyle.BORDER_THIN);
            headerStyle.setBorderRight(CellStyle.BORDER_THIN);
            headerStyle.setAlignment(CellStyle.ALIGN_CENTER);
            
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBoldweight(org.apache.poi.ss.usermodel.Font.BOLDWEIGHT_BOLD);
            headerStyle.setFont(headerFont);
            
            // 创建标题行
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // 创建数据行样式
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(CellStyle.BORDER_THIN);
            dataStyle.setBorderTop(CellStyle.BORDER_THIN);
            dataStyle.setBorderLeft(CellStyle.BORDER_THIN);
            dataStyle.setBorderRight(CellStyle.BORDER_THIN);
            
            // 填充数据
            for (int i = 0; i < data.size(); i++) {
                Row row = sheet.createRow(i + 1);
                Object obj = data.get(i);
                
                for (int j = 0; j < fields.length; j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellStyle(dataStyle);
                    
                    Object value = getFieldValue(obj, fields[j]);
                    setCellValue(cell, value);
                }
            }
            
            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                // 设置最小宽度
                if (sheet.getColumnWidth(i) < 3000) {
                    sheet.setColumnWidth(i, 3000);
                }
            }
            
            workbook.write(out);
            return out.toByteArray();
            
        } catch (Exception e) {
            throw new RuntimeException("Excel生成失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public byte[] exportToPDF(List<?> data, String title, String[] headers, String[] fields) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();
            
            // 设置中文字体
            BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(bfChinese, 18, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(bfChinese, 12, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font dataFont = new com.itextpdf.text.Font(bfChinese, 10, com.itextpdf.text.Font.NORMAL);
            
            // 添加标题
            Paragraph titleParagraph = new Paragraph(title, titleFont);
            titleParagraph.setAlignment(Element.ALIGN_CENTER);
            titleParagraph.setSpacingAfter(10);
            document.add(titleParagraph);
            
            // 添加生成时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Paragraph timeParagraph = new Paragraph("生成时间: " + sdf.format(new Date()), dataFont);
            timeParagraph.setAlignment(Element.ALIGN_RIGHT);
            timeParagraph.setSpacingAfter(20);
            document.add(timeParagraph);
            
            // 创建表格
            PdfPTable table = new PdfPTable(headers.length);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);
            
            // 添加表头
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setPadding(5);
                table.addCell(cell);
            }
            
            // 添加数据行
            for (Object obj : data) {
                for (String field : fields) {
                    Object value = getFieldValue(obj, field);
                    String cellValue = formatValue(value);
                    
                    PdfPCell cell = new PdfPCell(new Phrase(cellValue, dataFont));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setPadding(5);
                    table.addCell(cell);
                }
            }
            
            document.add(table);
            
            // 添加页码
            int pageNumber = writer.getPageNumber();
            Paragraph pageInfo = new Paragraph("第 " + pageNumber + " 页", dataFont);
            pageInfo.setAlignment(Element.ALIGN_CENTER);
            document.add(pageInfo);
            
            document.close();
            return out.toByteArray();
            
        } catch (Exception e) {
            throw new RuntimeException("PDF生成失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean validateExportSize(int dataSize, String exportType) {
        if ("EXCEL".equalsIgnoreCase(exportType)) {
            return dataSize <= EXCEL_MAX_SIZE;
        } else if ("PDF".equalsIgnoreCase(exportType)) {
            return dataSize <= PDF_MAX_SIZE;
        }
        return false;
    }
    
    /**
     * 通过反射获取对象字段值
     */
    private Object getFieldValue(Object obj, String fieldName) {
        try {
            // 尝试直接访问字段
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (NoSuchFieldException e) {
            // 如果字段不存在，尝试调用getter方法
            try {
                String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                return obj.getClass().getMethod(methodName).invoke(obj);
            } catch (Exception ex) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 设置Excel单元格值
     */
    private void setCellValue(Cell cell, Object value) {
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Date) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            cell.setCellValue(sdf.format((Date) value));
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue(value.toString());
        }
    }
    
    /**
     * 格式化值为字符串（用于PDF）
     */
    private String formatValue(Object value) {
        if (value == null) {
            return "";
        } else if (value instanceof Date) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.format((Date) value);
        } else {
            return value.toString();
        }
    }
}
