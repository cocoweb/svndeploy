package com.foresee.xdeploy.file;

import java.util.ArrayList;

import com.foresee.xdeploy.utils.PathUtils;

import static com.foresee.xdeploy.file.ScanIncrementFiles.ListCols.ColList_FileName;
import static com.foresee.xdeploy.file.ScanIncrementFiles.ListCols.ColList_Man;
import static com.foresee.xdeploy.file.ScanIncrementFiles.ListCols.ColList_Path;
import static com.foresee.xdeploy.file.ScanIncrementFiles.ListCols.ColList_ProjPackage;
import static com.foresee.xdeploy.file.ScanIncrementFiles.ListCols.ColList_Ver;

public class SvnFile {
    
    String FilePath="";
    ArrayList<String> svnfileDefList = null;
    SvnFiles parentSvnFiles =null;
    String keyRoot="";
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Ver:[" + getVer() + "] |"
                + getProj() + "| " 
                + getPath(keyRoot) + "  " 
                + getMan()
                + " << " + getExcelName() + "\n";

    }

    public SvnFile(ArrayList<String> listString, SvnFiles svnfiles) {
        svnfileDefList =  listString;
        parentSvnFiles = svnfiles;
    }

    public String getURL(){
        return "";
    }
    
    public String getVer(){
        return svnfileDefList.get(ColList_Ver);
    }
    public String getProj(){
        return svnfileDefList.get(ColList_ProjPackage);
    }
    
    public String getPath(String filekeyroot){
 
        return PathUtils.autoPathRoot(svnfileDefList.get(ColList_Path), filekeyroot);
       
    }
    public String getPath(){
        return getPath("");
        
    }
    public String getMan(){
        return svnfileDefList.get(ColList_Man);
       
    }
    public String getExcelName(){
        return svnfileDefList.get(ColList_FileName);
       
    }
    
    
    public void setKeyRoot(String keyroot){
        keyRoot = keyroot;
    }

}
