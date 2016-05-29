package com.foresee.xdeploy.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import com.foresee.test.util.io.File2Util;
import com.foresee.xdeploy.file.base.XdeployBase;

public class ExcelFiles extends XdeployBase{
    public static final String BATCH = "BATCH";
    public static final String FILE = "FILE";

    public String sfilePath;
    public String sFolderPath;
    public String sFilter;

    public String scanOption = BATCH; // 默认批量扫描

    private ArrayList<String> fileList = new ArrayList<String>();

    public ExcelFiles(String sfilePath, String sFolderPath, String scanOption) {
        super();
        this.sfilePath = sfilePath;
        this.sFolderPath = sFolderPath;
        this.scanOption = scanOption;
    }

    public ExcelFiles(String sfilePath) {
        this(sfilePath,"",FILE);
    }

    public ExcelFiles(String sFolderPath, String sFilter) {
        this("",sFolderPath,BATCH);
    }
    public ExcelFiles(PropValue pv) {
        this(pv.excelfile,pv.excelFolder,pv.scanOption);
        sFilter = pv.excelFolderFilter;
    }
    
    public ExcelFiles(){
        this(PropValue.getInstance());
    }

    /**
     * @return excel文件名的列表
     */
    public ArrayList<String> getExcelList() {
        if(fileList.isEmpty()){  
            //如果为空，就读取文件名； 只获取一次
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
        }
        return fileList;

    }

/* (non-Javadoc)
 * @see java.lang.Object#toString()
 */
@Override
public String toString() {
    return "ExcelFiles [" + getExcelList() + "]";
}

}
