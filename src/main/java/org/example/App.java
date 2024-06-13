package org.example;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.functions.Column;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {

        try {
            // Чтение файла
            FileInputStream file = new FileInputStream(new File("C:\\Users\\etezova_fm\\IdeaProjects\\EBUCHI\\src\\main\\resources\\аудиторный фонд обход_ЕВ.xlsx"));
            Workbook workbook = new XSSFWorkbook(file);

            // Создаем новую книгу для записи результата
            Workbook transposedWorkbook = new XSSFWorkbook();
            Sheet transposedSheet = transposedWorkbook.createSheet("Transposed Data");

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                var sheet = workbook.getSheetAt(i);

                var name = workbook.getSheetName(i);
                if ("Кафедральный аудит".equals(name) || "Перечень характеристик".equals(name)) continue;
                transposedSheet.createRow(i).createCell(0).setCellValue(name);

                for (Row row: sheet) {
                    if (row.getRowNum() < 6) continue;
                    for (Cell cell : row) {
                        if (cell == null) continue;
                        if (cell.getColumnIndex() < 3 || cell.getColumnIndex() > 8) continue;


                        int columnNum = cell.getColumnIndex();

                        var type = cell.getCellType();

                        if (type == 0) {
                            if (transposedSheet.getRow(i) == null) {
                                transposedSheet.createRow(i).createCell(row.getRowNum()).setCellValue(cell.getNumericCellValue());
                            } else {
                                transposedSheet.getRow(i).createCell(row.getRowNum()).setCellValue(cell.getNumericCellValue());
                            }
                        } else if (type == 1) {
                            if (transposedSheet.getRow(i) == null) {
                                var val = cell.getStringCellValue();
                                if (val != null)
                                    transposedSheet.createRow(i).createCell(row.getRowNum()).setCellValue(cell.getStringCellValue());
                            } else {
                                var val = cell.getStringCellValue();
                                if (val != null)
                                    transposedSheet.getRow(i).createCell(row.getRowNum()).setCellValue(cell.getStringCellValue());
                            }
                        }

                        transposedSheet.getRow(i).createCell(5).setCellValue(sheet.getRow(5).getCell(columnNum).getStringCellValue());
                    }
                }
            }



            // Транспонируем данные
//            for (int r = 0; r < sheet.getLastRowNum() + 1; r++) {
//                Row row = sheet.getRow(r);
//                for (int c = 0; c < row.getLastCellNum(); c++) {
//                    Cell cell = row.getCell(c);
//                    if (cell == null) continue;
//
//                    var type = cell.getCellType();
//                    if (type == 0) {
//                        if (transposedSheet.getRow(c) == null) {
//                            transposedSheet.createRow(c).createCell(r).setCellValue(cell.getNumericCellValue());
//                        } else {
//                            transposedSheet.getRow(c).createCell(r).setCellValue(cell.getNumericCellValue());
//                        }
//                    } else if (type ==1) {
//                        if (transposedSheet.getRow(c) == null) {
//                            transposedSheet.createRow(c).createCell(r).setCellValue(cell.getStringCellValue());
//                        } else {
//                            transposedSheet.getRow(c).createCell(r).setCellValue(cell.getStringCellValue());
//                        }
//                    }
//                }
//            }

            // Сохраняем результат в новый файл
            String desktopPath = "C:\\Users\\etezova_fm\\IdeaProjects\\EBUCHI\\src\\main\\resources\\result\\govno.xlsx";
            try (FileOutputStream fileOut = new FileOutputStream(desktopPath)) {
                transposedWorkbook.write(fileOut);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
