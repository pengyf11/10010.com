package com.ailk.mall.common.export;
import java.io.BufferedWriter;  
import java.io.File;  
import java.io.FileInputStream;  
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.io.OutputStream;
import java.io.OutputStreamWriter;  
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;  
import java.util.LinkedHashMap;  

import javax.servlet.http.HttpServletResponse;  

import org.apache.commons.beanutils.BeanUtils;  
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;

import com.ailk.mall.sell.manage.common.CsvExport;
  
/** 
 * @作者: cc 
 * @日期: 2016-10-12 
 * @描述: CSV导出工具 
 */  
public class ExportCsv<T> {  
  
    private LinkedHashMap headMap; 
    
    public void exportCvsToBrowser(String filename, Collection<T> exportData) throws IOException {
        HttpServletResponse response = ServletActionContext.getResponse();
        // response.setContentType("application/vnd.ms-excel;charset=gb2312");
        response.setContentType("application/msexcel");
        response.setHeader("Content-disposition", "inline;filename=" + filename
                + ".csv");
       //创建输出流
        OutputStream out = response.getOutputStream();
        FileInputStream in = null;
        File csvFile = null;
        try {
            csvFile = new ExportCsv().createCSVFile(filename, exportData);
          //读取要下载的文件，保存到文件输入流
            in = new FileInputStream(csvFile);
            
            //创建缓冲区
            byte buffer[] = new byte[1024];
            int len = 0;
            //循环将输入流中的内容读取到缓冲区当中
            while((len=in.read(buffer))>0){
            //输出缓冲区的内容到浏览器，实现文件下载
            out.write(buffer, 0, len);
            }
        } finally {
            if(out != null){
                out.flush();
                out.close();
            }
            if(in != null){
                in.close();
            }
            if(csvFile.exists()){
                csvFile.delete();  
            }
        }
    }
    
    /** 
     * 导出为CVS文件 
     *  
     * @param exportData 
     */  
    public File createCSVFile(String fileName, Collection<T> exportData) {  

        File csvFile = null;  
        BufferedWriter csvFileOutputStream = null;  
        try {  
            csvFile = new File(fileName + ".csv");   
            // GB2312使正确读取分隔符","  
            csvFileOutputStream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), "GB2312"),  
                    1024);  
            // 写入文件头部  
            Iterator<T> itc = exportData.iterator();
            Class tCls = itc.next().getClass();
            parseExportBean(tCls);
            for (Iterator propertyIterator = headMap.entrySet().iterator(); propertyIterator.hasNext();) {  
                java.util.Map.Entry propertyEntry = (java.util.Map.Entry) propertyIterator.next();  
                csvFileOutputStream.write("\"" + propertyEntry.getValue().toString() + "\"");  
                if (propertyIterator.hasNext()) {  
                    csvFileOutputStream.write(",");  
                }  
            }  
            csvFileOutputStream.newLine();  
            // 写入文件内容  
            for (Iterator iterator = exportData.iterator(); iterator.hasNext();) {  
                Object row = (Object) iterator.next();  
                for (Iterator propertyIterator = headMap.entrySet().iterator(); propertyIterator.hasNext();) {  
                    java.util.Map.Entry propertyEntry = (java.util.Map.Entry) propertyIterator.next();  
                    String key = propertyEntry.getKey().toString();
                    String value = BeanUtils.getProperty(row, key);
                    if(StringUtils.isEmpty(value)){
                        value = "";
                    }
                    csvFileOutputStream.write("\""  
                            + value.toString() + "\t\"");  
                    if (propertyIterator.hasNext()) {  
                        csvFileOutputStream.write(",");  
                    }  
                }  
                if (iterator.hasNext()) {  
                    csvFileOutputStream.newLine();  
                }  
            }  
            csvFileOutputStream.flush();  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                csvFileOutputStream.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return csvFile;  
    }  
  
    /**
     * 解析ExportBean
     * 
     * @param tCls
     */
    private void parseExportBean(Class tCls) {
        headMap = new LinkedHashMap();
        Field fields[] = tCls.getDeclaredFields();
        
        // 遍历fields
        for (Field f : fields) {
            CsvExport exp = f.getAnnotation(CsvExport.class);

            // 设置导出的表头
            if (exp != null) {
                String code = exp.exportCode();
                String head = exp.exportHead();
                headMap.put(code, head);
            }
        }
    }
}  