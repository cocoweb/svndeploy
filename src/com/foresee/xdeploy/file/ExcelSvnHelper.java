package com.foresee.xdeploy.file;

import static com.foresee.xdeploy.file.ExcelFiles.ExcelCols.ColExcel_Man;
import static com.foresee.xdeploy.file.ExcelFiles.ExcelCols.ColExcel_Path;
import static com.foresee.xdeploy.file.ExcelFiles.ExcelCols.ColExcel_ProjPackage;
import static com.foresee.xdeploy.file.ExcelFiles.ExcelCols.ColExcel_Ver;
import static com.foresee.xdeploy.file.ScanIncrementFiles.ListCols.ColList_FileName;
import static com.foresee.xdeploy.file.ScanIncrementFiles.ListCols.ColList_Man;
import static com.foresee.xdeploy.file.ScanIncrementFiles.ListCols.ColList_Path;
import static com.foresee.xdeploy.file.ScanIncrementFiles.ListCols.ColList_ProjPackage;
import static com.foresee.xdeploy.file.ScanIncrementFiles.ListCols.ColList_Ver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.foresee.test.util.lang.StringUtil;
import com.foresee.xdeploy.utils.PathUtils;
import com.foresee.xdeploy.utils.excel.ExcelMoreUtil;
import com.foresee.xdeploy.utils.excel.POIExcelMakerUtil;
import com.foresee.xdeploy.utils.excel.ExcelMoreUtil.IHandleScanRow;

public class ExcelSvnHelper {

    public static final String SheetName = "功能清单";
    final SvnFiles svnfiles = new SvnFiles();

    public ExcelSvnHelper() {
        // TODO Auto-generated constructor stub
    }
    
    public SvnFiles loadSvnFiles(ExcelFiles excelfiles){
        
        for(String filepath:excelfiles.getExcelList()){
            loadSvnFiles(new File(filepath));
        }
        
        // 排序返回的清单
        Collections.sort(svnfiles.SvnFileList, new Comparator<ArrayList<String>>() {
            @Override
            public int compare(ArrayList<String> o1, ArrayList<String> o2) {
                return (o1.get(ColList_ProjPackage) + o1.get(ColList_Path) + o1.get(ColList_Ver)).compareTo(o2
                        .get(ColList_ProjPackage) + o2.get(ColList_Path) + o2.get(ColList_Ver));
            }

        });
        
        return svnfiles;
    }
    
    public  List<ArrayList<String>> loadSvnFilesList(ExcelFiles excelfiles) {
        return loadSvnFiles(excelfiles).SvnFileList;
    }

    
    /**
     * 获取一个excel文件的内容
     * @param xfile
     * @return
     */
    public SvnFiles loadSvnFiles(File xfile) {
        final String filename = xfile.getName();
        
        try {
            ExcelMoreUtil.scanExcelData(xfile.getPath(), SheetName, new IHandleScanRow(){
                HSSFRow localrow;

                private String getValue(int col){
                    return  POIExcelMakerUtil.getCellValue(localrow.getCell(col)).toString();
                }
                
                @Override
                public void handleRow(HSSFRow row, HSSFWorkbook fromWB) {
                    localrow =row;
                    
                    if ( !StringUtil.isEmpty(getValue(ColExcel_Path))) {
                        for (String xfield : handlePathList(getValue(ColExcel_Path))) {
                            svnfiles.addItem(getValue(ColExcel_Ver), xfield, getValue(ColExcel_ProjPackage),
                                    getValue(ColExcel_Man), filename);
                        }

//                        // 判断是否包含多个文件分隔
//                        if (getValue( ColExcel_Path).contains("\n")) {
//                            for (String xfield : handlePathList(getValue(ColExcel_Path))) {
//                                svnfiles.addItem(getValue(ColExcel_Ver), xfield, getValue(ColExcel_ProjPackage),
//                                        getValue(ColExcel_Man), filename);
//                            }
//
//                        } else {
//                            svnfiles.addItem(getValue(ColExcel_Ver)
//                                    , getValue(ColExcel_Path)
//                                    , getValue(ColExcel_ProjPackage)
//                                    , getValue(ColExcel_Man)
//                                    , filename);
//                        }

                    }
                   
                    
                }

                @Override
                public int skipRow() {
                    return 2;
                }
                
            });
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return svnfiles;
        
    }
    
    public  List<ArrayList<String>> loadSvnFilesList(File xfile) {
        
        return loadSvnFiles(xfile).SvnFileList;
        
    }
    private List<String> handlePathList(String sPath) {
        String[] xstr = StringUtil.split(sPath);
        return Arrays.asList(xstr);
    }
    
    


}
