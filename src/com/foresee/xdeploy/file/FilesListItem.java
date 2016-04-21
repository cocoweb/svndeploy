package com.foresee.xdeploy.file;

import static com.foresee.xdeploy.file.base.XdeployBase.ListCols.ColList_FileName;
import static com.foresee.xdeploy.file.base.XdeployBase.ListCols.ColList_Man;
import static com.foresee.xdeploy.file.base.XdeployBase.ListCols.ColList_Path;
import static com.foresee.xdeploy.file.base.XdeployBase.ListCols.ColList_ProjPackage;
import static com.foresee.xdeploy.file.base.XdeployBase.ListCols.ColList_Ver;

import java.util.ArrayList;
import java.util.List;

import com.foresee.test.util.lang.StringUtil;
import com.foresee.xdeploy.file.base.XdeployBase;
import com.foresee.xdeploy.utils.PathUtils;

public class FilesListItem extends XdeployBase {
    
    String FilePath="";
    ArrayList<String> svnfileDefList = null;
    FilesList parentFileslist =null;
    //String keyRoot="";
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Ver:[" + getVer() + "] |"
                + getProj() + "| " 
                + getPath(PropValue.getInstance().filekeyroot) + "  " 
                + getMan()
                + " << " + getExcelName() + "\n";

    }

    public FilesListItem(ArrayList<String> listString, FilesList fileslist) {
        svnfileDefList =  listString;
        parentFileslist = fileslist;
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
    
    
//    public void setKeyRoot(String keyroot){
//        keyRoot = keyroot;
//    }
    
    
    public boolean checkProject() throws Exception{
        List<String> alist = PropValue.getInstance().pkgList;
        boolean bret = false;
        
        String[] packages =StringUtil.split(getProj(),",、，"); 
        

        for (String pak : packages) {
            if (alist.indexOf(pak)<0){
                bret = false;
                throw new Exception("无效的web工程名："+getProj());
            }else bret =true;
       
        }
        
        return bret;
        
    }
    
    ExchangePath ep =null;
    public ExchangePath getExchange(){
        if (ep==null){
            try {
                ep = ExchangePath.exchange(getPath());
            } catch (Exception e) {
                
                e.printStackTrace();
            }
        }
        return ep;
    }
    
    public boolean isType(String sPathType){
        return getExchange().getPathType().equals(sPathType);
    }



}
