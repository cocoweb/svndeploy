package com.foresee.xdeploy.file;

import static com.foresee.xdeploy.file.base.XdeployBase.ExcelCols.ColExcel_Man;
import static com.foresee.xdeploy.file.base.XdeployBase.ExcelCols.ColExcel_Path;
import static com.foresee.xdeploy.file.base.XdeployBase.ExcelCols.ColExcel_ProjPackage;
import static com.foresee.xdeploy.file.base.XdeployBase.ExcelCols.ColExcel_ROWNo;
import static com.foresee.xdeploy.file.base.XdeployBase.ExcelCols.ColExcel_Ver;
import static com.foresee.xdeploy.file.base.XdeployBase.ListCols.ColList_Path;
import static com.foresee.xdeploy.file.base.XdeployBase.ListCols.ColList_ProjPackage;
import static com.foresee.xdeploy.file.base.XdeployBase.ListCols.ColList_Ver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.foresee.xdeploy.file.base.XdeployBase;
import com.foresee.xdeploy.utils.excel.ExcelMoreUtil;
import com.foresee.xdeploy.utils.excel.POIExcelMakerUtil;
import com.foresee.xdeploy.utils.excel.ExcelMoreUtil.IHandleCopyRow;
import com.foresee.xdeploy.utils.excel.ExcelMoreUtil.IHandleScanRow;

/**
 * Excel 文件清单的处理
 * @author allan
 *
 */
public class ExcelListHelper  extends XdeployBase {

    public static final String SheetName = "功能清单";
    

    public ExcelListHelper() {
    }
    
    /**
     * 创建文件清单对象
     * 
     * @return
     */
    public FilesList createFilesList(){
        return loadFilesList(new ExcelFiles());
    }

		    
	static int iRowNum=0;

    /**
	     * 获取一个excel文件的内容
	     * 
	     * @param xfile
	     * @return
	     */

	public FilesList loadFilesList(ExcelFiles excelfiles) {
		iRowNum = 0;
        final FilesList svnfiles = new FilesList(excelfiles);

        for (String filepath : excelfiles.getExcelList()) {
        	svnfiles.addAll( loadFilesList(new File(filepath),excelfiles.mergeToFileName).SvnFileList);
//            if (excelfiles.mergeToFileName.isEmpty()) 
//                svnfiles.addAll( loadFilesList(new File(filepath),).SvnFileList);
//            else  //同时合并excel文件
//                svnfiles.addAll(loadFilesList(filepath, excelfiles.mergeToFileName).SvnFileList);
        }

        // 排序返回的清单
        Collections.sort(svnfiles.SvnFileList, new Comparator<ArrayList<String>>() {
            @Override
            public int compare(ArrayList<String> o1, ArrayList<String> o2) {
                return (o1.get(ColList_Path) +o1.get(ColList_Ver) +o1.get(ColList_ProjPackage)  ).compareTo(
                        o2.get(ColList_Path) +o2.get(ColList_Ver)+ o2.get(ColList_ProjPackage) );
            }

        });
        
        return svnfiles;
    }
    

    public List<ArrayList<String>> loadSvnFilesList(ExcelFiles excelfiles) {
        return loadFilesList(excelfiles).SvnFileList;
    }


	
	    public List<ArrayList<String>> loadSvnFilesList(File xfile) {
	
	        return loadFilesList(xfile,"").SvnFileList;
	
	    }

	public FilesList loadFilesList(File xfile,String tofilename) {
        final FilesList svnfiles = new FilesList();
        final String filename = xfile.getPath();

        try {
        	if (tofilename.isEmpty()){
        	
	            ExcelMoreUtil.scanExcelData(filename, SheetName, new IHandleScanRow() {
	                @Override
	                public void handleRow(HSSFRow row, HSSFWorkbook fromWB, int iCount) {
	                     addRowToList(svnfiles,row, filename);
	                }
	
	                @Override
	                public int skipRow() {
	                    return 2;
	                }
	
	            });
        	}else{
                ExcelMoreUtil.copyExcelDataToFile(filename, tofilename, SheetName, new IHandleCopyRow() {
                    // copy row 本地代码实现回调

                    @Override
                    public void handleRow(HSSFRow targetRow, HSSFRow sourceRow, HSSFWorkbook targetWork, HSSFWorkbook sourceWork, int iCount) {
                    	iRowNum ++;
                        addRowToList(svnfiles,sourceRow, filename);
                        
                        //合并到新的Excel文件
                        copyRow(targetRow, sourceRow, targetWork, sourceWork, iCount);
                        
                    }

					
					protected  void copyRow(HSSFRow targetRow, HSSFRow sourceRow, HSSFWorkbook targetWork, HSSFWorkbook sourceWork,int iExcelRowCount) {
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
					                break;
					            default:
					                // 拷贝单元格，包括内容和样式
					                ExcelMoreUtil.copyCell(targetCell, sourceCell, targetWork, sourceWork, null);
					
					            }
					
					        }
					    }
					
					}

                });

        	}
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return svnfiles;

    }

    
    protected  HSSFRow localrow;
    protected  String getValue(int col) {
        return getValue(localrow,col).toString();
    }
    
    protected  String getValue(HSSFRow xrow,int col) {
        return POIExcelMakerUtil.getCellValue(xrow.getCell(col)).toString();
    }


    protected  void addRowToList(FilesList xsvnfiles, HSSFRow xlocalrow, String filename) {
        localrow = xlocalrow;
    
        for (String xfield : handlePathList(getValue(ColExcel_Path))) {
            xsvnfiles.addItem(getValue(ColExcel_Ver), xfield, getValue(ColExcel_ProjPackage), getValue(ColExcel_Man), filename);
        }
    
    }


 
}