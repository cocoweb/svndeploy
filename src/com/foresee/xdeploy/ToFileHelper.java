package com.foresee.xdeploy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.tmatesoft.svn.core.SVNException;

import com.foresee.test.util.io.FileUtil;
import com.foresee.test.util.lang.DateUtil;
import com.foresee.test.util.lang.StringUtil;
import com.foresee.xdeploy.file.ExcelHelper;
import com.foresee.xdeploy.utils.PathUtils;
import com.foresee.xdeploy.utils.SvnClient;
import com.foresee.xdeploy.utils.ZipFileUtils;

public class ToFileHelper {
	PropValue pv =null;

    public ToFileHelper() {
        this("/svntools.properties");
    }
    public ToFileHelper(String strFileName) {
    	pv = new PropValue(strFileName);
       
        //initProp();
    }


    public void scanPrintList() {
        
        //生成excel输出文件名
        String sTofile=pv.excelfiletemplate.substring(0,pv.excelfiletemplate.indexOf("."))+"-"+ DateUtil.getCurrentDate("yyyyMMdd") +"-产品线-合并.xls";
        FileUtil.Copy(pv.excelfiletemplate, sTofile);
        
        List<ArrayList<String>> xlist=ExcelHelper
                .scanListfile(pv.excelfile, pv.excelFolder, pv.scanOption,pv.excelFolderFilter,sTofile)
                .retList;
        
        String a1="";
        StringBuffer bugStr=new StringBuffer();
        String lastStr="";
    
        for (ArrayList<String> aRow : xlist) {
            String sPath = PathUtils.autoPathRoot(aRow.get(1), pv.filekeyroot);
            String printStr = "Ver:[" + aRow.get(0) + "] |" +aRow.get(2)+"| "+sPath+ "  " +aRow.get(3)+" << "+aRow.get(4)+"\n";
            
            // 判断是否目录，目录就不操作
            if (PathUtils.isFolder(sPath)) {
                System.out.print("<<< 注意 >>> 清单包含有目录：\n" + printStr);
            }else{
                
                System.out.print(printStr);
            }
            
            //比较两个相邻的文件,相同标识重复
            if(sPath.equals(a1)) {
                bugStr.append(lastStr );
                bugStr.append(printStr);
                lastStr = "";
            }

            lastStr=printStr;
            a1=sPath;
            
        }
        System.out.println("\n共有文件数量："+Integer.toString(xlist.size()));
        System.out.println("==空的版本号，将获取最新的版本。==请仔细检查清单格式，路径不对将无法从svn获取。");
        System.out.println("合并生成了EXCEL为："+sTofile);
        
        if(bugStr.length()>0){
            System.out.println("\n<<<<文件有重复>>>>请注意核对，如下：");
            System.out.println(bugStr);
        }
        
    }

    /**
     * 扫描清单文件， 从svn导出每一个文件到 指定目录
     */
    public void scanSvnToPath() {
        SvnClient xclient = SvnClient.getInstance(pv.getProperty("svn.username"), pv.getProperty("svn.password"));
        int fileCount = 0;
    
        for (ArrayList<String> aRow : ExcelHelper.scanListfile(pv.excelfile, pv.excelFolder, pv.scanOption,pv.excelFolderFilter)) {
            try {
                String sUrl = pv.svnurl + PathUtils.autoPathRoot(aRow.get(1), "trunk");   //svn库的文件绝对路径URL
                String sVer = aRow.get(0);
                String toPath =  PathUtils.autoUrlToPath(sUrl, pv.svntofolder, pv.keyRootFolder);
                
                // 判断是否目录，目录就不操作
                if (PathUtils.isFolder(sUrl)) {
                    System.out.println("目录不处理" + sUrl);
                }else{
                    xclient.svnExport(sUrl, sVer,toPath, pv.keyRootFolder);
                    
                }
                fileCount++;
            } catch (SVNException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        System.out.println("\nTotal "+ Integer.toString(fileCount) +" Files, Exported to path ="+PathUtils.addFolderEnd(pv.svntofolder)+pv.keyRootFolder);
    
    }

    /**
     * 扫描清单文件，从指定目录 导出文件到 临时输出目录
     */
    public void scanWorkspaceToPath() {
        String ciworkspace = pv.getProperty("ci.workspace");
        String citoFolder = pv.getProperty("ci.tofolder");
        String cikeyroot = pv.getProperty("ci.keyroot");
    
        //扫描excel文件的清单
        //ScanIncrementFiles xx = new ScanIncrementFiles(excelfile, excelFolder, scanOption);
        for (ArrayList<String> aRow :  ExcelHelper.scanListfile(pv.excelfile, pv.excelFolder, pv.scanOption,pv.excelFolderFilter)) {
            try {
                String sPath = ciworkspace + aRow.get(1);                                   // 源文件路径citoFolder
                String dPath = javaToclass(PathUtils.autoUrlToPath(sPath, citoFolder, cikeyroot));       // 目标路径
    
                if (sPath.indexOf(cikeyroot) > 0) {
                    // 创建目录
                    FileUtil.createFolder(FileUtil.getFolderPath(dPath));
                    FileUtil.Copy(sPath, dPath);
                } else {
                    System.out.println("ci.keyroot 配置错误，未包含在复制路径中！");
                    break;
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    
    }
    
    private String javaToclass(String xPath){
        
        return xPath.endsWith(".java") ? StringUtil.trimEnd(xPath, ".java")+".class": xPath;
    }

    /**
     * 根据起始版本号，获取文件清单；从指定目录 导出到输出目录
     */
    public void svnDiffToPath(){
        String svnurl = pv.getProperty("svndiff.url");
        String startversion = pv.getProperty("svndiff.startversion");
        String endversion = pv.getProperty("svndiff.endversion");
        String svndiffkeyroot = pv.getProperty("svndiff.keyroot");
        
        System.out.println("startversion="+startversion+": endversion="+endversion+": svnURL="+svnurl);
        
        SvnClient xclient = SvnClient.getInstance(pv.getProperty("svn.username"), pv.getProperty("svn.password")) ;       
        try {
            ArrayList<String> alist = xclient.svnDiff(svnurl, startversion, endversion,svndiffkeyroot);
            Collections.sort(alist);  //排序
            
            for(String spath:alist){
                System.out.println(spath);
                
            }
            
            System.out.println("变动文件数="+Integer.toString(alist.size()));
        } catch (SVNException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
    }
    
    public void scanZipToPath() {
        String zipfile = pv.getProperty("zip.file");
        String zipfoler = pv.getProperty("zip.folder");
        String zipfolderfilter = pv.getProperty("zip.folder.filter");
        String ziptofolder = pv.getProperty("zip.tofolder");
        String zipkeyroot = pv.getProperty("zip.keyroot");
    
        //扫描excel文件的清单
        for (ArrayList<String> aRow : ExcelHelper.scanListfile(pv.excelfile, pv.excelFolder, pv.scanOption,pv.excelFolderFilter)) {
            try {
                String sProject= aRow.get(2);
                
                //判断清单中的工程名，是否包含在 war包中
                //包含就抽取到目标路径
                if(zipfile.contains(sProject)){
                    String sPath = PathUtils.autoPathRoot(aRow.get(1),zipkeyroot,"NOROOT");                // 源文件路径
                    String dPath = PathUtils.addFolderEnd(ziptofolder) +sProject+ PathUtils.addFolderStart(sPath);       // 目标路径
                    ZipFileUtils.getZipFile(zipfile, sPath, dPath);
                    System.out.println("抽取文件:"+dPath);
                     
                }
   
//                if (sPath.indexOf(cikeyroot) > 0) {
//                    // 创建目录
//                    FileUtil.createFolder(FileUtil.getFolderPath(dPath));
//                    FileUtil.Copy(sPath, dPath);
//                } else {
//                    System.out.println("ci.keyroot 配置错误，未包含在复制路径中！");
//                    break;
//                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    
    }
    
    /**
     * 自动合并excel文件
     */
    public void mergeExcel(){
//        List<ArrayList<String>> xlist=ScanIncrementFiles.mergeListfile(excelfile, excelFolder, scanOption,excelFolderFilter);
//        
//        String a1="";
//        StringBuffer bugStr=new StringBuffer();
//        String lastStr="";
//    
//        for (ArrayList<String> aRow : xlist) {
//            String sPath = PathUtils.autoPathRoot(aRow.get(1), filekeyroot);
//            String printStr = "Ver:[" + aRow.get(0) + "] |" +aRow.get(2)+"| "+sPath+ "  " +aRow.get(3)+" << "+aRow.get(4)+"\n";
//            
//            System.out.print(printStr);
//            
//            //比较两个相邻的文件,相同标识重复
//            if(sPath.equals(a1)) {
//                bugStr.append(lastStr );
//                bugStr.append(printStr);
//                lastStr = "";
//            }
//
//            lastStr=printStr;
//            a1=sPath;
//            
//        }
//        System.out.println("\n共有文件数量："+Integer.toString(xlist.size()));
//        System.out.println("==空的版本号，将获取最新的版本。");
//        System.out.println("==请仔细检查清单格式，路径不对将无法从svn获取。");
//        
//        if(bugStr.length()>0){
//            System.out.println("\n<<<<文件有重复>>>>请注意核对，如下：");
//            System.out.println(bugStr);
//        }
    	
    }
    
    
    public static void main(String[] args) {
        // TODO Auto-generated method stub
    	System.out.print( 	new ToFileHelper().pv.pkgmap);
    
    }

}
