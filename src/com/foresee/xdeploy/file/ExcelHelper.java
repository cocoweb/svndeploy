package com.foresee.xdeploy.file;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.foresee.xdeploy.utils.ExcelMoreUtil;

public class ExcelHelper {

    public ExcelHelper() {
        // TODO Auto-generated constructor stub
    }

    public static ScanIncrementFiles scanListfile(String excelfile, String excelFolder, String scanOption,
            String xfilter,String tofilename) {
        ScanIncrementFiles xx = new ScanIncrementFiles(excelfile, excelFolder, scanOption);
        xx.retList = xx.loadExcelFiles(xfilter);
    
        try {
            // 生成excel
            for (String sfile : xx.fileList) {
                ExcelMoreUtil.copyExcelDataToFile(sfile, tofilename);
    
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    
        return xx;
    }

    public static List<ArrayList<String>> scanListfile(String excelfile, String excelFolder, String scanOption,
            String xfilter) {
        ScanIncrementFiles xx = new ScanIncrementFiles(excelfile, excelFolder, scanOption);
        List<ArrayList<String>> retList = xx.loadExcelFiles(xfilter);
    
        try {
            // 生成excel
            for (String sfile : xx.fileList) {
                ExcelMoreUtil.copyExcelDataToFile(sfile, "p:/因开发所致环境变更记录表模版 - 副本.xls");
    
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    
        return retList;
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
    
    }

}
