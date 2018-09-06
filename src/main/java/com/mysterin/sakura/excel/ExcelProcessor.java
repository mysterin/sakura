package com.mysterin.sakura.excel;

import org.apache.poi.UnsupportedFileFormatException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.List;
import java.util.Map;

public class ExcelProcessor {

    private String path;
    private Workbook wb;
    private int sheetIndex;

    public ExcelProcessor(String path) throws IOException {
        this.path = path;
        if (path.endsWith(".xls")) {
            wb = new HSSFWorkbook();
        } else if (path.endsWith(".xlsx")) {
            wb = new XSSFWorkbook();
        }
        throw new RuntimeException("不支持文件类型");
    }

    /**
     * 生成 excel
     * @param titles
     * @param data
     */
    public void createExcel(List<String> titles, List<Map<String, String>> data) throws FileNotFoundException {
        Sheet sheet = createSheet();
        setTitle(titles, sheet);
        setData(data, sheet, 1);
        FileOutputStream out = new FileOutputStream(new File(path));

    }

    /**
     * 生成 sheet
     * @return
     */
    public Sheet createSheet() {
        Sheet sheet = createSheet("sheet" + sheetIndex);
        sheetIndex++;
        return sheet;
    }

    public Sheet createSheet(String sheetName) {
        return wb.createSheet(sheetName);
    }

    /**
     * 设置标题
     * @param titles
     * @param sheet
     */
    public void setTitle(List<String> titles, Sheet sheet) {
        Row row = sheet.createRow(0);
        for (int i=0; i<titles.size(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(titles.get(i));
        }
    }

    /**
     * 设置数据
     * @param data
     * @param sheet
     * @param rowIndex
     */
    public void setData(List<Map<String, String>> data, Sheet sheet, int rowIndex) {
        for (Map<String, String> rowData : data) {
            Row row = sheet.createRow(rowIndex);
            rowIndex++;
            int j = 0;
            for (Map.Entry<String, String> entry : rowData.entrySet()) {
                Cell cell = row.createCell(j);
                cell.setCellValue(entry.getValue());
                j++;
            }
        }
    }

}
