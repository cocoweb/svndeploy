package com.foresee.xdeploy.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import com.foresee.test.util.io.File2Util;

public class ExcelFiles {
    //excle Files 包含多个or一个Excel文件
	//
	public interface ListCols {
        // List列字段序号
        public static final int ColList_Ver = 0;
        public static final int ColList_Path = 1;
        public static final int ColList_ProjPackage = 2;
        public static final int ColList_Man = 3;
        public static final int ColList_FileName = 4;

    }

    public interface ExcelCols {

        // Excel列字段序号
        public static final int ColExcel_ROWNo = 0;
        public static final int ColExcel_Ver = 3;
        public static final int ColExcel_Path = 6;
        public static final int ColExcel_ProjPackage = 7;
        public static final int ColExcel_Man = 13;

    }
    public static final String BATCH = "BATCH";
    public static final String FILE = "FILE";

    public String sfilePath;
    public String sFolderPath;
    public String sFilter;

    public String scanOption = BATCH; // 默认批量扫描
    
    public ArrayList<String> fileList = new ArrayList<String>();

	public ExcelFiles(String sfilePath, String sFolderPath, String scanOption) {
		super();
		this.sfilePath = sfilePath;
		this.sFolderPath = sFolderPath;
		this.scanOption = scanOption;
	}
    public ExcelFiles(String sfilePath) {
        super();
        this.sfilePath = sfilePath;
        this.scanOption = FILE;
    }
    public ExcelFiles(String sFolderPath, String sFilter) {
        super();
        this.sFolderPath = sFolderPath;
        this.sFilter = sFilter;
        this.scanOption = BATCH;
    }
	
	public ArrayList<String>  getExcelList(){
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

}
