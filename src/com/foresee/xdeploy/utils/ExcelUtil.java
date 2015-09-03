package com.foresee.xdeploy.utils;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil {
	/**
	 * Excel文件到List
	 * @param fileName
	 * @param sheetIndex	// 工作表索引
	 * @param skipRows	// 跳过的表头
	 * @return
	 * @throws Exception 
	 */
	public static List<Object> readToList(String fileName, int sheetIndex, int skipRows) throws Exception{
		List<Object> ls = new ArrayList<Object>();
		Workbook wb = loadWorkbook(fileName);
		if (null != wb) {
			Sheet sh = wb.getSheetAt(sheetIndex);
			int rows = sh.getPhysicalNumberOfRows();
			for (int i = skipRows; i < rows; i++) {
				Row row = sh.getRow(i);
				if(null==row){
					break;
				}
				int cells = row.getPhysicalNumberOfCells();
				if(cells==0){
					continue;
				}
				List<String> r = new ArrayList<String>(cells);
				for (int c = 0; c < cells; c++) {
					if(c == 0 || c== 4){
						try{
							r.add(String.format("%.0f", row.getCell(c).getNumericCellValue()));
						}catch(Exception e){
							throw new Exception("出现该错误请依次检查：<br>1、【序号】或【端口号】请使用数字<br>2、检查《Webservice信息》是否是第二个sheet页");
						}
					}else{
						r.add(row.getCell(c).getStringCellValue());
					}
				}
				ls.add(r);
			}
		}
		
		return ls;
	}
	
	/**
	 * 读取Excel文件，支持2000与2007格式
	 * @param fileName
	 * @return
	 * @throws Exception 
	 */
	public static Workbook loadWorkbook(String fileName) throws Exception {
		if (null == fileName)
			return null;

		Workbook wb = null;
		if (fileName.toLowerCase().endsWith(".xls")) {
			try {
				InputStream in = new FileInputStream(fileName);
				POIFSFileSystem fs = new POIFSFileSystem(in);
				wb = new HSSFWorkbook(fs);
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (fileName.toLowerCase().endsWith(".xlsx")) {
			try {
				InputStream in = new FileInputStream(fileName);
				wb = new XSSFWorkbook(in);
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			throw new Exception("不是一个有效的Excel文件");
		}
		return wb;
	}
	
	public static void writeToExcel(Workbook wb, OutputStream out){
		try {
			wb.write(out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static enum ExcelType{
		xls, xlsx;
	}
	
	public static Workbook listToWorkbook(List<?> rows, ExcelType type){
		Workbook wb = null;
		if(ExcelType.xls.equals(type)){
			wb = new HSSFWorkbook();
		}else if(ExcelType.xlsx.equals(type)){
			wb = new XSSFWorkbook();
		}else{
			return null;
		}
		
		Sheet sh = wb.createSheet();
		if(null!=rows){
			for(int i=0; i<rows.size(); i++){
				Object obj = rows.get(i);
				Row row = sh.createRow(i);
				
				if (obj instanceof Collection) {
					Collection<?> r = (Collection<?>) obj;
					Iterator<?> it = r.iterator();
					int j = 0;
					while(it.hasNext()){
						Cell cell = row.createCell(j++);
						cell.setCellValue(String.valueOf(it.next()));
					}
				}else if(obj instanceof Object[]){
					Object[] r = (Object[]) obj;
					for(int j=0; j<r.length; j++){
						Cell cell = row.createCell(j);
						cell.setCellValue(String.valueOf(r[j]));
					}
				}else{
					Cell cell = row.createCell(0);
					cell.setCellValue(String.valueOf(obj));
				}
			}
		}
		
		return wb;
	}
	
	public static void main(String[] args) {
		List<Object> rows = new ArrayList<Object>();
		
		List<Object> row = new ArrayList<Object>();
		row.add("字符串");
		row.add(11);
		row.add(new Date());
		row.add(1.0);
		rows.add(((Object)row));
		
		rows.add("中文");
		rows.add(new Date());
		
		listToWorkbook(rows, ExcelType.xls);
		listToWorkbook(rows, ExcelType.xlsx);
	}
}
