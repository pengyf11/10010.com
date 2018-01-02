package com.ailk.mall.common.export;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.struts2.ServletActionContext;
import org.phw.core.exception.MessageException;
import org.phw.eop.api.internal.util.Strings;

import com.ailk.mall.sell.manage.common.ExcelExport;

public class ExportExcel<T> {

    private HSSFCellStyle headStyle;// 头样式

    private HSSFCellStyle bodyStyle;// 主体样式

    private List<String> expHeaders;// 表格头

    private List<Integer> expColWidth;// 列宽

    private List<Method> expMethod;// Bean中方法列表

    public void exportExcelToBrowser(String title, Collection<T> data,
            String filename) throws IOException {
        HttpServletResponse response = ServletActionContext.getResponse();
        // response.setContentType("application/vnd.ms-excel;charset=gb2312");
        response.setContentType("application/msexcel");
        response.setHeader("Content-disposition", "inline;filename=" + filename
                + ".xls");
        OutputStream out = response.getOutputStream();
        try {
            exportExcel(title, data, out);
        } finally {
            out.flush();
            out.close();
        }
    }

    // 导出并压缩
    public void exportExcelZip(String title, Collection<T> data, String filename)
            throws IOException {
        OutputStream out = new FileOutputStream(filename);
        exportExcel(title, data, out);
        out.close();
    }

    /**
     * Excel导出方法
     * 
     * @param title
     * @param data
     * @param out
     * @param isZip
     */
    public void exportExcel(String title, Collection<T> data, OutputStream out) {

        // 校验数据是否为空
        if (data == null || title == null || out == null) {
            throw new MessageException("列表为空，无法导出");
        }

        Iterator<T> itc = data.iterator();
        Iterator<T> it = data.iterator();// 获得T的class

        Class tCls = itc.next().getClass();

        // 解析Bean(获得表头，以及需要导出的字段)
        parseExportBean(tCls);

        HSSFWorkbook workbook = new HSSFWorkbook();

        int a = 1;

        HSSFSheet sheet = workbook.createSheet(title);

        // 如果未设置样式设置默认样式
        if (headStyle == null) {
            genDefaultHeadStyle(workbook);
        }
        if (bodyStyle == null) {
            genDefaultBodyStyle(workbook);
        }

        // 产生表格标题行
        HSSFRow row = sheet.createRow(0);
        for (int i = 0; i < expHeaders.size(); i++) {
            sheet.setColumnWidth(i, expColWidth.get(i));
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(headStyle);
            HSSFRichTextString text = new HSSFRichTextString(expHeaders.get(i));
            cell.setCellValue(text);
        }

        // 遍历集合数据，产生数据行
        int index = 0;
        while (it.hasNext()) {
            index++;

            if (index == 50000) {
                sheet = workbook.createSheet(title + a);
                if (headStyle == null) {
                    genDefaultHeadStyle(workbook);
                }
                if (bodyStyle == null) {
                    genDefaultBodyStyle(workbook);
                }

                // 产生表格标题行
                row = sheet.createRow(0);
                for (int i = 0; i < expHeaders.size(); i++) {
                    sheet.setColumnWidth(i, expColWidth.get(i));
                    HSSFCell cell = row.createCell(i);
                    cell.setCellStyle(headStyle);
                    HSSFRichTextString text = new HSSFRichTextString(expHeaders.get(i));
                    cell.setCellValue(text);
                }
                
                a++;
                index = 1;
            }

            row = sheet.createRow(index);
            T t = it.next();
            for (int i = 0; i < expMethod.size(); i++) {
                HSSFCell cell = row.createCell(i);
                cell.setCellStyle(bodyStyle);
                cell.setCellType(Cell.CELL_TYPE_STRING);
                try {
                    Method getMethod = expMethod.get(i);
                    Object value = getMethod.invoke(t, new Object[] {});
                    String textValue = null;
                    textValue = dealValue(value);
                    cell.setCellValue(textValue);
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            workbook.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置头默认样式
     * 
     * @param workbook
     */
    private void genDefaultHeadStyle(HSSFWorkbook workbook) {

        headStyle = workbook.createCellStyle();

        headStyle.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
        headStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        headStyle.setBorderBottom(CellStyle.BORDER_THIN);
        headStyle.setBorderLeft(CellStyle.BORDER_THIN);
        headStyle.setBorderRight(CellStyle.BORDER_THIN);
        headStyle.setBorderTop(CellStyle.BORDER_THIN);
        headStyle.setAlignment(CellStyle.ALIGN_CENTER);

        HSSFFont font = workbook.createFont();
        font.setColor(HSSFColor.VIOLET.index);
        font.setFontHeightInPoints((short) 11);
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);

        headStyle.setFont(font);
    }

    /**
     * 设置主体默认样式
     * 
     * @param workbook
     */
    private void genDefaultBodyStyle(HSSFWorkbook workbook) {

        bodyStyle = workbook.createCellStyle();

        bodyStyle.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
        bodyStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        bodyStyle.setBorderBottom(CellStyle.BORDER_THIN);
        bodyStyle.setBorderLeft(CellStyle.BORDER_THIN);
        bodyStyle.setBorderRight(CellStyle.BORDER_THIN);
        bodyStyle.setBorderTop(CellStyle.BORDER_THIN);
        bodyStyle.setAlignment(CellStyle.ALIGN_CENTER);
        bodyStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

        HSSFFont font = workbook.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_NORMAL);
        bodyStyle.setFont(font);
    }

    /**
     * 解析ExportBean
     * 
     * @param tCls
     */
    private void parseExportBean(Class tCls) {
        Field fields[] = tCls.getDeclaredFields();
        expHeaders = new ArrayList<String>();
        expColWidth = new ArrayList<Integer>();
        expMethod = new ArrayList<Method>();

        // 遍历fields
        for (Field f : fields) {
            ExcelExport exp = f.getAnnotation(ExcelExport.class);

            // 设置导出的表头
            if (exp != null) {
                String head = exp.exportHead();
                expHeaders.add(head);
                int colWidth = exp.exportColWidth();
                expColWidth.add(colWidth);

                // 取得所有需要导出的字段的get方法
                String getMethodName = "get" + Strings.capitalize(f.getName());
                Method getMethod;
                try {
                    getMethod = tCls.getMethod(getMethodName);
                    expMethod.add(getMethod);
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String dealValue(Object value) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String textValue = "";
        if (value != null) {
            if (value instanceof Date) {
                textValue = sdf.format(value);
            } else if (value instanceof Boolean) {
                textValue = "是";
                if (!(Boolean) value) {
                    textValue = "否";
                }
            } else {
                textValue = value.toString();
            }
        }
        return textValue;
    }

    public HSSFCellStyle getHeadStyle() {
        return headStyle;
    }

    public void setHeadStyle(HSSFCellStyle headStyle) {
        this.headStyle = headStyle;
    }

    public HSSFCellStyle getBodyStyle() {
        return bodyStyle;
    }

    public void setBodyStyle(HSSFCellStyle bodyStyle) {
        this.bodyStyle = bodyStyle;
    }
}

