package com.foresee.xdeploy.file;

import java.util.ArrayList;

import static com.foresee.xdeploy.file.ScanIncrementFiles.ListCols.ColList_FileName;
import static com.foresee.xdeploy.file.ScanIncrementFiles.ListCols.ColList_Man;
import static com.foresee.xdeploy.file.ScanIncrementFiles.ListCols.ColList_Path;
import static com.foresee.xdeploy.file.ScanIncrementFiles.ListCols.ColList_ProjPackage;
import static com.foresee.xdeploy.file.ScanIncrementFiles.ListCols.ColList_Ver;

public class SvnFile {
    
    String FilePath="";
    ArrayList<String> svnfileDefList = null;

    public SvnFile() {
        // TODO Auto-generated constructor stub
    }
    
    public SvnFile(ArrayList<String> ll) {
        svnfileDefList =  ll;
    }

    public String getURL(){
        return "";
    }
    
    public String getVer(){
        return svnfileDefList.get(ColList_Ver);
    }
    
    public String getPath(){
        return svnfileDefList.get(ColList_Path);
       
    }

}
