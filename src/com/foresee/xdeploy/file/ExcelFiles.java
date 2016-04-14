package com.foresee.xdeploy.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.foresee.test.util.io.File2Util;
import com.foresee.test.util.io.FileUtil;
import com.foresee.test.util.lang.DateUtil;

public class ExcelFiles extends XdeployBase{
    public static final String BATCH = "BATCH";
    public static final String FILE = "FILE";

    public String sfilePath;
    public String sFolderPath;
    public String sFilter;
    public static String excelfiletemplate;

    public String scanOption = BATCH; // 默认批量扫描

    public ArrayList<String> fileList = new ArrayList<String>();

    public ExcelFiles(String sfilePath, String sFolderPath, String scanOption) {
        super();
        this.sfilePath = sfilePath;
        this.sFolderPath = sFolderPath;
        this.scanOption = scanOption;
    }

    public ExcelFiles(String sfilePath) {
        this(sfilePath,"",FILE);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ExcelFiles [" + getExcelList() + "]";
    }

    public ExcelFiles(String sFolderPath, String sFilter) {
        this("",sFolderPath,BATCH);
    }
    public ExcelFiles(PropValue pv) {
        this(pv.excelfile,pv.excelFolder,pv.scanOption);
        excelfiletemplate = pv.excelfiletemplate;
        sFilter = pv.excelFolderFilter;
        if (pv.getProperty("file.excel.merge").equals("true")) { // 判断是否需要合并excel
            // 生成excel输出文件
            mergeToFile();
        }
        
    }
    
    public ExcelFiles(){
        this(PropValue.getInstance());
    }

    public String mergeToFileName = ""; // 默认为""，不用合并excel
    public String mergeToFile() {

        // 生成excel输出文件名
        mergeToFileName = getOutExcelFileName();
        // 生成合并的excel文件
        FileUtil.Copy(excelfiletemplate, mergeToFileName);

        return mergeToFileName;
    }

    public ArrayList<String> getExcelList() {
        if (scanOption.equals(FILE)) {
            System.out.println("Loading List >>> " + sfilePath);
            fileList.add(sfilePath);

        } else {

            // 遍历文件夹，并过滤
            Collection<File> clFiles = File2Util.getAllFiles(sFolderPath, sFilter);
            for (File xfile : clFiles) {
                System.out.println("Loading List >>> " + xfile.getPath());
                fileList.add(xfile.getPath());

            }

        }
        return fileList;

    }

    private static String outexcelfilename = "";

    public static String getOutExcelFileName() {
        if (outexcelfilename == "") {
              
            outexcelfilename = PropValue.getInstance().excelfiletemplate.substring(0, PropValue.getInstance().excelfiletemplate.indexOf(".")) + "-"
                    + DateUtil.format(new Date(), "yyyyMMdd-HHmm") + "-产品线-合并.xls";
        }
        return outexcelfilename;
    }

}
