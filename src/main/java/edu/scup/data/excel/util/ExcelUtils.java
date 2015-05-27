package edu.scup.data.excel.util;

import edu.scup.data.excel.ExcelHeader;
import edu.scup.data.util.ValidateUtil;
import edu.scup.web.sys.entity.SDictGroup;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.ss.usermodel.*;
import cn.wujc.util.StringUtils;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelUtils {

    public static <T> List<T> excelToBean(InputStream inputStream, Class<T> beanClz) throws Exception {
        List<T> beanList = new ArrayList<>();
        Field[] fields = beanClz.getDeclaredFields();
        Map<String, Field> headerMap = new HashMap<>();
        Map<Integer, ExcelHeader> headerIndexMap = new HashMap<>();
        Map<String, ExcelHeader> headerTitleMap = new HashMap<>();

        for (Field field : fields) {
            if (field.isAnnotationPresent(ExcelHeader.class)) {
                ExcelHeader fieldAnnotation = field.getAnnotation(ExcelHeader.class);
                String title = StringUtils.isEmpty(fieldAnnotation.title()) ? field.getName() : fieldAnnotation.title();
                headerMap.put(title, field);
                headerTitleMap.put(title, fieldAnnotation);
            }
        }
        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        Field[] excelFields = new Field[0];
        boolean isHeader = true;
        for (Row row : sheet) {
            if (isHeader) {
                isHeader = false;
                excelFields = new Field[row.getLastCellNum()];
                for (Cell cell : row) {
                    String rowValue = getCellValueAsString(cell) != null ? getCellValueAsString(cell) : "";
                    excelFields[cell.getColumnIndex()] = headerMap.get(rowValue);
                    headerIndexMap.put(cell.getColumnIndex(), headerTitleMap.get(rowValue));
                }
            } else {
                T t = beanClz.newInstance();
                boolean allBlank = true;
                for (Cell cell : row) {
                    int c = cell.getColumnIndex();
                    if (c >= excelFields.length || excelFields[c] == null) {
                        continue;
                    }

                    String rowStringValue = getCellValueAsString(cell) != null ? getCellValueAsString(cell) : "";
                    if (StringUtils.isNotBlank(rowStringValue)) {
                        String dict = headerIndexMap.get(c).dict();
                        if (StringUtils.isNotBlank(dict)) {
                            rowStringValue = SDictGroup.getDictCode(dict, rowStringValue);
                        }
                        allBlank = false;
                        Object rowValue = StringUtils.stringToObject(rowStringValue, excelFields[c].getType());
                        BeanUtils.setProperty(t, excelFields[c].getName(), rowValue);
                    }
                }
                if (!allBlank && ValidateUtil.validate(t)) {
                    beanList.add(t);
                }
            }
        }
        return beanList;
    }

    public static String getCellValueAsString(Cell cell) {
        cell.setCellType(Cell.CELL_TYPE_STRING);
        return cell.getStringCellValue();
    }
}