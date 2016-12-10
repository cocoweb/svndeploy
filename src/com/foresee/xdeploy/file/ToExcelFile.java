package com.foresee.xdeploy.file;

import static com.foresee.xdeploy.file.base.XdeployBase.ExcelCols.ColExcel_Path;
import static com.foresee.xdeploy.file.base.XdeployBase.ExcelCols.ColExcel_ROWNo;
import static com.foresee.xdeploy.file.base.XdeployBase.ExcelCols.ColExcel_Ver;
import static com.foresee.xdeploy.file.base.XdeployBase.ListCols.ColList_Path;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.foresee.test.util.io.FileUtil;
import com.foresee.test.util.lang.DateUtil;
import com.foresee.xdeploy.file.base.XdeployBase;
import com.foresee.xdeploy.utils.PathUtils;
import com.foresee.xdeploy.utils.excel.ExcelMoreUtil;
import com.foresee.xdeploy.utils.excel.POIExcelMakerUtil;

/**
 * 封装输出Excel相关逻辑
 * @author allan
 *
 */
public class ToExcelFile extends XdeployBase{
	/**
	 * 行号
	 */
	int iRowNum=0; 
	
	String excelFileName ="";
	String sheetName =SheetName;
	POIExcelMakerUtil poiExcelMaker =null;
	private static String outexcelfilename = "";

	
	private ToExcelFile(String filename) {
		super();
		excelFileName = filename;
		
	}
	private ToExcelFile(String filename,boolean withOpen) {
		this(filename);
		if(withOpen){
			try {
				poiExcelMaker = new POIExcelMakerUtil(new File(filename));
			} catch (Exception e) {
				
				e.printStackTrace();
			}

		}
	}
	
	
	
	public static ToExcelFile createToExcelFile(){
    	
    	return createToExcelFile(true);
		
	}
	public static ToExcelFile createToExcelFile(boolean withOpen){
    	String tofilename = getOutExcelFileName();
    	
    	FileUtil.Copy( PropValue.getInstance().excelfiletemplate, tofilename);
    	
    	return new ToExcelFile(tofilename ,withOpen);
		
	}
	
	

	public String getSheetName() {
		return sheetName;
	}



	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
	
	public int getCurrentRowNum(){
		return iRowNum+2;
	}
	
	public void addRow(int rowNum,long version,String sPath){
		try {
			poiExcelMaker.insertRow(sheetName, getCurrentRowNum());
			
			poiExcelMaker.writeToExcel(sheetName, getCurrentRowNum(), ColExcel_ROWNo, rowNum);
			poiExcelMaker.writeToExcel(sheetName, getCurrentRowNum(), ColExcel_Ver, version);
			poiExcelMaker.writeToExcel(sheetName, getCurrentRowNum(), ColExcel_Path, sPath);
			
			iRowNum++;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	Workbook toworkbook=null;
	Sheet toworksheet=null;
	/**
	 * 当前行索引
	 */
	int rowsIndex = 0;
	
	public void openExcel(){
		try {
			toworkbook = ExcelMoreUtil.loadWorkbook(excelFileName);
	        // 添加保存到file2中
	         toworksheet= toworkbook.getSheet(sheetName);
	         
	         rowsIndex = toworksheet.getPhysicalNumberOfRows();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
	}
	
	public void copyRow(HSSFRow sourceRow, HSSFWorkbook sourceWork,int iExcelRowCount){
		Row torow = toworksheet.createRow(rowsIndex +iRowNum ); // 创建一行to
		
		copyRow((HSSFRow)torow, sourceRow,  (HSSFWorkbook) toworkbook,  sourceWork,iExcelRowCount);
		
	}


	public  void copyRow(HSSFRow targetRow, HSSFRow sourceRow, HSSFWorkbook targetWork, HSSFWorkbook sourceWork,int iExcelRowCount) {
		iRowNum ++;
		
		
	    for (int i = 0; i <= sourceRow.getLastCellNum(); i++) {
		//for (int i = sourceRow.getFirstCellNum(); i <= sourceRow.getLastCellNum(); i++) {
	        HSSFCell sourceCell = sourceRow.getCell(i);
	        HSSFCell targetCell = targetRow.getCell(i);
	
	        if (sourceCell != null ||i==ColExcel_ROWNo) {
	            if (targetCell == null) {
	                targetCell = targetRow.createCell(i);
	            }
	
	            switch (i) { // 根据列号进行处理
	            case ColExcel_ROWNo:
	                targetCell.setCellValue(iRowNum);  //iExcelRowCount);
	                break;
	            case ColExcel_Path:
	            	
	                targetCell.setCellValue(handlePath(sourceCell.getStringCellValue()));
	                        //PathUtils.autoPathRoot(handlePath(sourceCell.getStringCellValue()), PropValue.getInstance().filekeyroot));
	                		
	                break;
	            default:
	                // 拷贝单元格，包括内容和样式
	                ExcelMoreUtil.copyCell(targetCell, sourceCell, targetWork, sourceWork, null);
	
	            }
	
	        }
	    }
	
	}
	
	public void writeAndClose(){
		ExcelMoreUtil.writeWorkbookToExcel(toworkbook, excelFileName);
		close();
	}
	
	public void close(){
		try {
			if(poiExcelMaker!=null)
				poiExcelMaker.writeAndClose();
			if (toworkbook != null)
				toworkbook.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static String getOutExcelFileName() {
	    if (outexcelfilename == "") {
	          
	        outexcelfilename = PropValue.getInstance().excelfiletemplate.substring(0, PropValue.getInstance().excelfiletemplate.indexOf(".")) + "-"
	                + DateUtil.format(new Date(), "yyyyMMdd-HHmm") + "-产品线-合并.xls";
	    }
	    return outexcelfilename;
	}
	



}
