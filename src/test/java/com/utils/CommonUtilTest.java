package com.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CommonUtil unit tests.
 */
public class CommonUtilTest {

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetRandomString() {
        String value = CommonUtil.getRandomString(12);
        assertNotNull(value);
        assertEquals(12, value.length());
        assertTrue(value.matches("[a-z0-9]+"));
    }

    @Test
    public void testGetRandomNumber() {
        String value = CommonUtil.getRandomNumber(6);
        assertNotNull(value);
        assertEquals(6, value.length());
        assertTrue(value.matches("[0-9]+"));
    }

    @Test
    public void testGetCellValue_nullCell() {
        assertEquals("", CommonUtil.getCellValue(null));
    }

    @Test
    public void testGetCellValue_stringBooleanNumericAndFormula() throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("data");
        Row row = sheet.createRow(0);

        Cell stringCell = row.createCell(0);
        stringCell.setCellValue("  hello  ");
        assertEquals("hello", CommonUtil.getCellValue(stringCell));

        Cell boolCell = row.createCell(1);
        boolCell.setCellValue(true);
        assertEquals("true", CommonUtil.getCellValue(boolCell));

        Cell numericCell = row.createCell(2);
        numericCell.setCellValue(123.456789);
        assertEquals("123.456789", CommonUtil.getCellValue(numericCell));

        Cell formulaCell = row.createCell(3);
        formulaCell.setCellFormula("1+2");
        assertNotNull(CommonUtil.getCellValue(formulaCell));

        workbook.close();
    }

    @Test
    public void testGetCellValue_dateFormats() throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("dates");
        Row row = sheet.createRow(0);

        int[] formats = {14, 20, 21, 31, 32, 33, 57, 58, 176};
        for (int i = 0; i < formats.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(45292.5);
            cell.getCellStyle().setDataFormat((short) formats[i]);
            assertNotNull(CommonUtil.getCellValue(cell));
        }

        workbook.close();
    }
}
