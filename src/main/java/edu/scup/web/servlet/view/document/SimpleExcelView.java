package edu.scup.web.servlet.view.document;

import edu.scup.data.excel.ExcelHeader;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.document.AbstractExcelView;
import org.springside.modules.utils.Encodes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.*;

public class SimpleExcelView extends AbstractExcelView {
    private static final Logger logger = LoggerFactory.getLogger(SimpleExcelView.class);
    private String fileName;

    public SimpleExcelView() {
    }

    public SimpleExcelView(String fileName) {
        this.fileName = fileName;
    }

    @Override
    protected void buildExcelDocument(Map<String, Object> model, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (fileName != null) {
            response.setHeader("Content-Disposition", "attachment; filename=" + Encodes.urlEncode(fileName) + ".xls");
        }
        for (Map.Entry<String, Object> entry : model.entrySet()) {
            HSSFSheet sheet = workbook.createSheet(entry.getKey());
            if (!(entry.getValue() instanceof List)) {
                logger.error("model value must be List");
                continue;
            }
            List list = (List) entry.getValue();
            if (CollectionUtils.isEmpty(list)) {
                continue;
            }

            Class<?> cla = list.get(0).getClass();
            Field[] fields = cla.getDeclaredFields();
            Map<ExcelHeader, Field> headerMap = new TreeMap<ExcelHeader, Field>(new Comparator<ExcelHeader>() {
                @Override
                public int compare(ExcelHeader o1, ExcelHeader o2) {
                    return (o1.headerOrder() < o2.headerOrder()) ? -1 : ((o1.headerOrder() == o2.headerOrder()) ? 0 : 1);
                }
            });

            for (Field field : fields) {
                if (field.isAnnotationPresent(ExcelHeader.class)) {
                    ExcelHeader fieldAnnotation = field.getAnnotation(ExcelHeader.class);
                    headerMap.put(fieldAnnotation, field);
                }
            }

            //write header
            int col = 0;
            for (Map.Entry<ExcelHeader, Field> header : headerMap.entrySet()) {
                String title = StringUtils.isEmpty(header.getKey().title()) ? header.getValue().getName() : header.getKey().title();
                HSSFCell cell = getCell(sheet, 0, col);
                setText(cell, title);
                String[] validData = header.getKey().validData();
                if (validData.length > 0) {
                    CellRangeAddressList regions = new CellRangeAddressList(0, Integer.MAX_VALUE, col, col);
                    DVConstraint constraint = DVConstraint.createExplicitListConstraint(validData);
                    HSSFDataValidation data_validation = new HSSFDataValidation(regions, constraint);
                    sheet.addValidationData(data_validation);
                }
                col++;
            }

            //write content
            int row = 1;
            for (Object data : list) {
                col = 0;
                HSSFRow sheetRow = sheet.createRow(row++);
                for (Map.Entry<ExcelHeader, Field> header : headerMap.entrySet()) {
                    Field field = header.getValue();
                    field.setAccessible(true);
                    Object value = field.get(data);
                    sheetRow.createCell(col++).setCellValue(value == null ? "" : value.toString());
                }
            }
        }
    }
}
