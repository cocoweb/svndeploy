package com.foresee.xdeploy.file;

import static com.foresee.xdeploy.file.XdeployBase.ListCols.ColList_Path;
import static com.foresee.xdeploy.file.XdeployBase.ListCols.ColList_ProjPackage;
import static com.foresee.xdeploy.file.XdeployBase.ListCols.ColList_Ver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.foresee.xdeploy.utils.excel.ExcelMoreUtil;
import com.foresee.xdeploy.utils.excel.ExcelMoreUtil.IHandleCopyRow;
import com.foresee.xdeploy.utils.excel.ExcelMoreUtil.IHandleScanRow;

public class ExcelListHelper {

    public static final String SheetName = "功能清单";
    

    public ExcelListHelper() {
        // TODO Auto-generated constructor stub
    }

    public FilesList loadFilesList(ExcelFiles excelfiles) {
        final FilesList svnfiles = new FilesList(excelfiles);

        for (String filepath : excelfiles.getExcelList()) {
            if (excelfiles.mergeToFileName.isEmpty()) 
                svnfiles.addAll( loadFilesList(new File(filepath)).SvnFileList);
            else  //同时合并excel文件
                svnfiles.addAll(loadFilesList(filepath, excelfiles.mergeToFileName).SvnFileList);
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

    /**
     * 获取一个excel文件的内容
     * 
     * @param xfile
     * @return
     */
    public FilesList loadFilesList(File xfile) {
        final FilesList svnfiles = new FilesList();
        final String filename = xfile.getName();

        try {
            ExcelMoreUtil.scanExcelData(xfile.getPath(), SheetName, new IHandleScanRow() {
                @Override
                public void handleRow(HSSFRow row, HSSFWorkbook fromWB) {
                     ExcelFiles.addRowToList(svnfiles,row, filename);

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

    public int iExcelRowCount = 1;

    public FilesList loadFilesList(final String sfile, String tofilename) {
        final FilesList svnfiles = new FilesList();

        try {
            ExcelMoreUtil.copyExcelDataToFile(sfile, tofilename, SheetName, new IHandleCopyRow() {
                // copy row 本地代码实现回调

                @Override
                public void handleRow(HSSFRow targetRow, HSSFRow sourceRow, HSSFWorkbook targetWork, HSSFWorkbook sourceWork) {
                    ExcelFiles.addRowToList(svnfiles,sourceRow, sfile);
                    ExcelFiles.copyRow(targetRow, sourceRow, targetWork, sourceWork, iExcelRowCount);

                    iExcelRowCount++; // 行计数

                }

            });
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return svnfiles;

    }

    public List<ArrayList<String>> loadSvnFilesList(File xfile) {

        return loadFilesList(xfile).SvnFileList;

    }


 
}