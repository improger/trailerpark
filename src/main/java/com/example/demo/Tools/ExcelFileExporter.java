package com.example.demo.Tools;

import com.example.demo.models.Trailer;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelFileExporter {

    public static ByteArrayInputStream contactListToExcelFile(List<Trailer> trailers) {
        try(Workbook workbook = new XSSFWorkbook()){
            Sheet sheet = workbook.createSheet("Trailers");

            Row row = sheet.createRow(0);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            // Creating header
            Cell cell = row.createCell(0);
            cell.setCellValue("Trailer");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(1);
            cell.setCellValue("In_Date");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(2);
            cell.setCellValue("In_Truck");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(3);
            cell.setCellValue("In_Name");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(4);
            cell.setCellValue("In_Lastname");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(5);
            cell.setCellValue("Out_Date");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(6);
            cell.setCellValue("Out_Truck");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(7);
            cell.setCellValue("Out_Name");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(8);
            cell.setCellValue("Out_Lastname");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(9);
            cell.setCellValue("Department");
            cell.setCellStyle(headerCellStyle);

            // Creating data rows for each customer
            for(int i = 0; i < trailers.size(); i++) {
                Row dataRow = sheet.createRow(i + 1);
                dataRow.createCell(0).setCellValue(trailers.get(i).getTrl());
                //dataRow.createCell(1).setCellValue(trailers.get(i).getInDate());

                CellStyle dateStyle = workbook.createCellStyle();
                CreationHelper createHelper = workbook.getCreationHelper();
                dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));

                dataRow.createCell(1).setCellStyle(dateStyle);
                dataRow.getCell(1).setCellValue(trailers.get(i).getInDate());


                dataRow.createCell(2).setCellValue(trailers.get(i).getInTrc());
                dataRow.createCell(3).setCellValue(trailers.get(i).getInName());
                dataRow.createCell(4).setCellValue(trailers.get(i).getInLname());

                dataRow.createCell(5).setCellStyle(dateStyle);
                dataRow.getCell(5).setCellValue(trailers.get(i).getOutDate());


                dataRow.createCell(6).setCellValue(trailers.get(i).getOutTrc());
                dataRow.createCell(7).setCellValue(trailers.get(i).getOutName());
                dataRow.createCell(8).setCellValue(trailers.get(i).getOutLname());
                dataRow.createCell(9).setCellValue(trailers.get(i).getDepartment());

            }

            // Making size of column auto resize to fit with data
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
