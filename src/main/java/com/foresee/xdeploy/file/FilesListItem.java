package com.foresee.xdeploy.file;

import static com.foresee.xdeploy.file.base.XdeployBase.ListCols.ColList_FileName;
import static com.foresee.xdeploy.file.base.XdeployBase.ListCols.ColList_Man;
import static com.foresee.xdeploy.file.base.XdeployBase.ListCols.ColList_Path;
import static com.foresee.xdeploy.file.base.XdeployBase.ListCols.ColList_ProjPackage;
import static com.foresee.xdeploy.file.base.XdeployBase.ListCols.ColList_Ver;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import com.foresee.test.util.lang.StringUtil;
import com.foresee.xdeploy.file.base.XdeployBase;
import com.foresee.xdeploy.file.rule.ExchangePath;
import com.foresee.xdeploy.utils.PathUtils;

public class FilesListItem extends XdeployBase {
    
    String FilePath="";
    ArrayList<String> svnfileDefList = null;
    FilesList parentFileslist =null;
    
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
    
    public String[] getProjs1(){
    	return StringUtil.split(getProj(),",");//, ",、，");
    }
    public String[] getProjs(){
        //清除空的数组项
        String[] astr = StringUtil.split(getProj(),",");
        
        for(int ii=astr.length-1;ii>=0 ;ii--){
            if(astr[ii].isEmpty()){
                astr = (String[] )ArrayUtils.removeElement(astr, ii); 
            }
        }
        return astr;//, ",、，");
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
    
    
    public boolean checkProject() throws Exception{
        List<String> alist = PropValue.getInstance().pkgList;
        boolean bret = false;
        ExchangePath ep = getExchange();
        
        String[] packages =getProjs();//StringUtil.split(getProj(),",、，"); 
        //判断web工程名是否在清单中
        for (String pak : packages) {
            if (alist.indexOf(pak)<0){
                bret = false;
                throw new Exception("无效的web工程名："+getProj());
            }else{
                //判断web工程名与mapping中是否一致
                if(ep.inWar() && !ep.MappingKey.contains(getProj())){
                    bret = false;
                    throw new Exception("无效的web工程名-："+getProj()+" | "+ep.MappingKey);
                }else{
                   bret =true;
                    
                }
            }
       
        }
        
        
        return bret;
        
    }
    
    ExchangePath ep =null;
    public ExchangePath getExchange(){
        if (ep==null){
            try {
                ep = ExchangePath.createExchange(this);
                		//ExchangePath.exchange(getPath());
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
