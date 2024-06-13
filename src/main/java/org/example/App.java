package org.example;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class App {
    public static void main(String[] args) {

        try {
            // путь к исходному файла
            var sourceFilePath = "/Users/elizavetacaplina/IdeaProjects/mirea/govno/src/main/resources/аудиторный фонд обход_ЕВ.xlsx";
            // путь к файлу с результатом
            var resultFilePath = "/Users/elizavetacaplina/IdeaProjects/mirea/govno/src/main/resources/result/govno.xlsx";

            // Чтение файла
            FileInputStream file = new FileInputStream(sourceFilePath);
            Workbook workbook = new XSSFWorkbook(file);

            // Создаем новую книгу для записи результата
            Workbook transposedWorkbook = new XSSFWorkbook();
            Sheet transposedSheet = transposedWorkbook.createSheet("Transposed Data");

            // цикл по листам
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                // получили лист
                var sheet = workbook.getSheetAt(i);

                // получили имя листа
                var name = sheet.getSheetName();

                // пропускаем ненужные листы
                if ("Кафедральный аудит".equals(name) || "Перечень характеристик".equals(name)) continue;

                // пишем имя листа
                transposedSheet.createRow(i).createCell(0).setCellValue(name);

                // создаем переменную для поиска заполненной колонки
                int columnNum = -1;

                // для каждой строки
                for (Row row : sheet) {
                    // пропускаем строки до 6-ой (с 0)
                    if (row.getRowNum() < 6) continue;

                    // для каждой ячейке в строке
                    for (Cell cell : row) {
                        if (cell == null) continue;

                        // пропускаем столбцы
                        if (cell.getColumnIndex() < 3 || cell.getColumnIndex() > 8) continue;

                        // получаем значение
                        var cellValue = getCellValue(cell);

                        // если значение есть
                        if (cellValue != null && !cellValue.isBlank()) {
                            // сохраняем в нашу переменную номер колонки
                            columnNum = cell.getColumnIndex();

                            // пишем в результат файл
                            if (transposedSheet.getRow(i) == null)
                                transposedSheet.createRow(i).createCell(row.getRowNum()).setCellValue(cellValue);
                            else
                                transposedSheet.getRow(i).createCell(row.getRowNum()).setCellValue(cellValue);
                        }
                    }
                }

                // если была найдена заполненная колонка - пишем ее заголовок в строку
                if (columnNum != -1)
                    transposedSheet.getRow(i).createCell(5).setCellValue(sheet.getRow(5).getCell(columnNum).getStringCellValue());
            }

            // Сохраняем результат в новый файл
            try (FileOutputStream fileOut = new FileOutputStream(resultFilePath)) {
                transposedWorkbook.write(fileOut);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Функция для получения значения ячейки
     */
    public static String getCellValue(Cell cell) {
        if (cell != null) {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getRichStringCellValue().getString().trim();
                case NUMERIC:
                    double cellDouble = Math.abs(cell.getNumericCellValue());
                    double floorDouble = Math.floor(cellDouble);
                    if (cellDouble == floorDouble) {
                        return Long.toString(Math.round(cell.getNumericCellValue()));
                    } else {
                        return Double.toString(cell.getNumericCellValue());
                    }
                case FORMULA:
                    try {
                        return cell.getRichStringCellValue().toString().trim();
                    } catch (Exception e) {
                        try {
                            return Double.toString(cell.getNumericCellValue());
                        } catch (Exception e1) {
                            return null;
                        }
                    }
                default:
                    return null;
            }
        }
        return null;
    }
}
