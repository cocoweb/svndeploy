package com.foresee.xdeploy;

import static com.foresee.xdeploy.file.ScanIncrementFiles.ListCols.ColList_FileName;
import static com.foresee.xdeploy.file.ScanIncrementFiles.ListCols.ColList_Man;
import static com.foresee.xdeploy.file.ScanIncrementFiles.ListCols.ColList_Path;
import static com.foresee.xdeploy.file.ScanIncrementFiles.ListCols.ColList_ProjPackage;
import static com.foresee.xdeploy.file.ScanIncrementFiles.ListCols.ColList_Ver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.tmatesoft.svn.core.SVNException;

import com.foresee.test.util.io.FileUtil;
import com.foresee.xdeploy.file.ExchangePath;
import com.foresee.xdeploy.file.PropValue;
import com.foresee.xdeploy.file.ScanIncrementFiles;
import com.foresee.xdeploy.file.WarFile;
import com.foresee.xdeploy.file.WarFiles;
import com.foresee.xdeploy.utils.FileSystem;
import com.foresee.xdeploy.utils.PathUtils;
import com.foresee.xdeploy.utils.svn.SvnClient;
import com.foresee.xdeploy.utils.zip.Zip4jUtils;
import com.foresee.xdeploy.utils.zip.ZipFileUtils;

import net.lingala.zip4j.core.ZipFile;

public class ToFileHelper {
    PropValue pv = null;

    public ToFileHelper() {
        this("/svntools.properties");
    }

    public ToFileHelper(String strFileName) {
        pv = new PropValue(strFileName);
    }

    public void scanPrintList() {
        System.out.println("===========显示待处理文件清单=================");

        
        String sTofile = ""; // 默认为""，不用合并excel

        if (pv.getProperty("file.excel.merge").equals("true")) { // 判断是否需要合并excel
            // 生成excel输出文件名
            sTofile = pv.genOutExcelFileName();
            // 生成合并的excel文件
            FileUtil.Copy(pv.excelfiletemplate, sTofile);
        }

        // 扫描并获取全部excel内容
        ScanIncrementFiles scanFiles = ScanIncrementFiles.scanListfile(pv.excelfile, pv.excelFolder, pv.scanOption, pv.excelFolderFilter, sTofile);

        StringBuffer bugStr = new StringBuffer();
        String a1_Path = ""; // 用来比较上下路径的标记
        String lastStr = "";

        for (ArrayList<String> aRow : scanFiles.retList) {
            String sPath = PathUtils.autoPathRoot(aRow.get(ColList_Path), pv.filekeyroot);
            String printStr = "Ver:[" + aRow.get(ColList_Ver) + "] |"
                    + aRow.get(ColList_ProjPackage) + "| " 
                    + sPath + "  " + aRow.get(ColList_Man)
                    + " << " + aRow.get(ColList_FileName) + "\n";

            // 判断是否目录，目录就不操作
            if (PathUtils.isFolder(sPath)) {
                System.out.print("<<< 注意 >>> 清单包含有目录：\n" + printStr);
            } else {

                System.out.print(printStr);
            }

            // 比较两个相邻的文件,相同标识重复
            if (sPath.equals(a1_Path)) {
                bugStr.append(lastStr);
                bugStr.append(printStr);
                lastStr = "";
            }

            lastStr = printStr;
            a1_Path = sPath;

        }
        System.out.println("\n共有文件数量：" + Integer.toString(scanFiles.retList.size()));
        System.out.println("==空的版本号，将获取最新的版本。==请仔细检查清单格式，路径不对将无法从svn获取。");
        if (pv.getProperty("file.excel.merge").equals("true")) 
            System.out.println("   >>>合并生成了EXCEL为：" + sTofile);

        if (bugStr.length() > 0) {
            System.err.println("\n<<<<文件有重复>>>>请注意核对，如下：");
            System.err.println(bugStr);
        }

    }

    /**
     * 扫描清单文件， 从svn导出每一个文件到 指定目录
     */
    public void scanSvnToPath() {
        System.out.println("===========从svn库导出到临时目录，或者workspace=================");

        SvnClient xclient = SvnClient.getInstance(pv.getProperty("svn.username"), pv.getProperty("svn.password"));

        String zipFileName = pv.genOutZipFileName();
        int fileCount = 0;

        for (ArrayList<String> aRow : ScanIncrementFiles.scanListfile(pv.excelfile, pv.excelFolder, pv.scanOption, pv.excelFolderFilter)) {
            try {
                String fromPath = PathUtils.autoPathRoot(aRow.get(ColList_Path), "trunk");
                ExchangePath expath = ExchangePath.exchange(fromPath);
                
                String sUrl = expath.getTrunkURL();   //pv.svnurl + fromPath; // svn库的文件绝对路径URL
                String sVer = aRow.get(ColList_Ver);
                String toPath = PathUtils.autoUrlToPath(sUrl, pv.svntofolder, pv.keyRootFolder);

                // 判断是否目录，目录就不操作
                if (PathUtils.isFolder(sUrl)) {
                    System.out.println("目录不处理" + sUrl);
                } else {
                    if (xclient.CheckFileVersion(sUrl, sVer)){
                    	
						xclient.svnExport(sUrl, sVer, toPath, pv.keyRootFolder);

						if (pv.getProperty("svn.tozip.enabled").equals("true")) {
							// 将文件添加到zip文件
							Zip4jUtils.zipFile(toPath, zipFileName, expath.getToZipFolderPath());
							// FileUtil.getFolderPath(pv.exchangePath(fromPath)));

						}
                    }else{
                    	System.out.println(" -->>>文件版本不存在：[" +sVer+"]"+ sUrl);
                    }
                	

                }
                fileCount++;
            } catch (SVNException e) {
                e.printStackTrace();
            }
        }
        
        // TODO export svn文件后，自动提交到基线分支


        System.out.println(
                "\nTotal " + Integer.toString(fileCount) + " Files, Exported to path =" + PathUtils.addFolderEnd(pv.svntofolder) + pv.keyRootFolder);

        Zip4jUtils.InfoZipFile(zipFileName);

    }

    /**
     * 扫描清单文件，从指定目录 导出文件到 临时输出目录
     */
    public void scanWorkspaceToPath() {
        System.out.println("===========从指定目录 导出到临时目录，或者workspace=================");

        String ciworkspace = pv.getProperty("ci.workspace");
        String citoFolder = pv.getProperty("ci.tofolder");
        String cikeyroot = pv.getProperty("ci.keyroot");

        // 扫描excel文件的清单
        // ScanIncrementFiles xx = new ScanIncrementFiles(excelfile,
        // excelFolder, scanOption);
        for (ArrayList<String> aRow : ScanIncrementFiles.scanListfile(pv.excelfile, pv.excelFolder, pv.scanOption, pv.excelFolderFilter)) {
            try {
                String sPath = ciworkspace + aRow.get(ColList_Path); // 源文件路径citoFolder
                String dPath = PathUtils.javaToclass(PathUtils.autoUrlToPath(sPath, citoFolder, cikeyroot)); // 目标路径

                if (sPath.indexOf(cikeyroot) > 0) {
                    // 创建目录
                    FileUtil.createFolder(FileUtil.getFolderPath(dPath));
                    FileUtil.Copy(sPath, dPath);
                } else {
                    System.out.println("ci.keyroot 配置错误，未包含在复制路径中！");
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 根据起始版本号，获取文件清单；从指定目录 导出到输出目录
     */
    public void svnDiffToPath() {
        System.out.println("===========根据起始版本号svndiff.startversion  svndiff.endversion，获取文件清单=================");

        
        String svnurl = pv.getProperty("svndiff.url");
        String startversion = pv.getProperty("svndiff.startversion");
        String endversion = pv.getProperty("svndiff.endversion");
        String svndiffkeyroot = pv.getProperty("svndiff.keyroot");

        System.out.println("startversion=" + startversion + ": endversion=" + endversion + ": svnURL=" + svnurl);

        SvnClient xclient = SvnClient.getInstance(pv.getProperty("svn.username"), pv.getProperty("svn.password"));
        try {
            ArrayList<String> alist = xclient.svnDiff(svnurl, startversion, endversion, svndiffkeyroot);
            Collections.sort(alist); // 排序

            for (String spath : alist) {
                System.out.println(spath);

            }

            System.out.println("变动文件数=" + Integer.toString(alist.size()));
        } catch (SVNException e) {
            e.printStackTrace();
        }

    }

    public void scanWarToZip() {
        System.out.println("===========从指定压缩文件war、zip、jar 导出到zip文件=================");
        
        String zipfile = pv.getProperty("zip.file");
        String zipfoler = pv.getProperty("zip.folder");
        String zipfolderfilter = pv.getProperty("zip.folder.filter");
        
        int fileCount =0;

        SvnClient xclient = SvnClient.getInstance(pv.getProperty("svn.username"), pv.getProperty("svn.password"));
        
        String toZip = pv.genOutZipFileName();
        ZipFile toZipFile = Zip4jUtils.genZipFile(toZip);

        WarFiles warlist = new WarFiles(zipfoler, zipfolderfilter);

        // 扫描excel文件的清单
        for (ArrayList<String> aRow : ScanIncrementFiles.scanListfile(pv.excelfile, pv.excelFolder, pv.scanOption, pv.excelFolderFilter)) {
            try {
                String sProject = aRow.get(ColList_ProjPackage);
                String srcPath = aRow.get(ColList_Path);

                ExchangePath expath = ExchangePath.exchange(srcPath);   //pv.exchangeJarPath(srcPath);
                
                // 判断清单中的工程名，是否包含在 war包中
                // 包含就抽取到目标路径
                WarFile warfile = warlist.getWarFile(sProject);
                if (warfile!=null) {
                    if(warfile.copyToZip(toZipFile, expath)==0)
                        fileCount++;
                    
                    if(expath.isJava()){  
                        //同时抽取java源文件加入到zip中 
                        String tmpFilePath = pv.tempPath+"/"+expath.getFileName();
                        
                        xclient.svnExport(expath.getTrunkURL(), aRow.get(ColList_Ver), tmpFilePath, pv.keyRootFolder);
                        // 将文件添加到zip文件
                        Zip4jUtils.zipFile(tmpFilePath, toZipFile,expath.getToZipFolderPath()); 
                        fileCount++;
                        
                        FileUtil.delFile(tmpFilePath);
                        
                   }
                    
//                    if (srcPath.lastIndexOf(".java") > 0||expath.inJar()) { // 从jar抽取class、xml
//                        
//                         同时抽取java源文件加入到zip中 
//
//                        
//                       if (warfile.copyJavaToZip(toZipFile, expath)==0){
//
//                             System.out.println("     抽取class :" + expath.ToZipPath);
//                             fileCount++;
//                        }else{
//                            System.err.println("   !!抽取失败  :\n" + expath);
//                        }
//
//                    } else {
////                        String sPath = expath.FromPath;   //pv.exchangeWarPath(srcPath);
////                        String dPath = expath.ToZipPath;  //pv.exchangePath(srcPath);
////
////                        Zip4jUtils.ZipCopyFile2Zip(warfile.warZipFile, sPath, toZipFile, dPath);
//                        warfile.copyFileToZip(toZipFile,expath);
//                        
//                        System.out.println("     抽取文件  :" + expath.ToZipPath);
//                        fileCount++;
//                    }
                } else {
                    System.err.println("   !!没能抽取  :" + srcPath);
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        System.out.println("\n    >>> 成功抽取文件数:" + fileCount);
        
        // TODO 对zip文件进行检查，对比excel的文件，和zip中的文件

        Zip4jUtils.InfoZipFile(toZip);


    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

    public void scanZipToPath() {
        String zipfile = pv.getProperty("zip.file");
        String zipfoler = pv.getProperty("zip.folder");
        String zipfolderfilter = pv.getProperty("zip.folder.filter");
        String ziptofolder = pv.getProperty("zip.tofolder");
        String zipkeyroot = pv.getProperty("zip.keyroot");
    
        // 扫描excel文件的清单
        for (ArrayList<String> aRow : ScanIncrementFiles.scanListfile(pv.excelfile, pv.excelFolder, pv.scanOption, pv.excelFolderFilter)) {
            try {
                String sProject = aRow.get(ColList_ProjPackage);
    
                // 判断清单中的工程名，是否包含在 war包中
                // 包含就抽取到目标路径
                if (zipfile.contains(sProject)) {
                    String sPath = PathUtils.autoPathRoot(aRow.get(ColList_Path), zipkeyroot, "NOROOT"); // 源文件路径
                    String dPath = PathUtils.addFolderEnd(ziptofolder) + sProject + PathUtils.addFolderStart(sPath); // 目标路径
                    ZipFileUtils.getZipFile(zipfile, sPath, dPath);
                    System.out.println("抽取文件:" + dPath);
    
                }
    
                // if (sPath.indexOf(cikeyroot) > 0) {
                // // 创建目录
                // FileUtil.createFolder(FileUtil.getFolderPath(dPath));
                // FileUtil.Copy(sPath, dPath);
                // } else {
                // System.out.println("ci.keyroot 配置错误，未包含在复制路径中！");
                // break;
                // }
            } catch (Exception e) {
               e.printStackTrace();
            }
        }
    
    }

    /**
     * 自动合并excel文件
     */
    public void mergeExcel() {
        // List<ArrayList<String>>
        // xlist=ScanIncrementFiles.mergeListfile(excelfile, excelFolder,
        // scanOption,excelFolderFilter);
        //
        // String a1="";
        // StringBuffer bugStr=new StringBuffer();
        // String lastStr="";
        //
        // for (ArrayList<String> aRow : xlist) {
        // String sPath = PathUtils.autoPathRoot(aRow.get(1), filekeyroot);
        // String printStr = "Ver:[" + aRow.get(0) + "] |"
        // +aRow.get(2)+"| "+sPath+ " " +aRow.get(3)+" << "+aRow.get(4)+"\n";
        //
        // System.out.print(printStr);
        //
        // //比较两个相邻的文件,相同标识重复
        // if(sPath.equals(a1)) {
        // bugStr.append(lastStr );
        // bugStr.append(printStr);
        // lastStr = "";
        // }
        //
        // lastStr=printStr;
        // a1=sPath;
        //
        // }
        // System.out.println("\n共有文件数量："+Integer.toString(xlist.size()));
        // System.out.println("==空的版本号，将获取最新的版本。");
        // System.out.println("==请仔细检查清单格式，路径不对将无法从svn获取。");
        //
        // if(bugStr.length()>0){
        // System.out.println("\n<<<<文件有重复>>>>请注意核对，如下：");
        // System.out.println(bugStr);
        // }
    
    }

    public void copyFiletoZip(String fromPath, String toPath) {
        if (pv.getProperty("svn.tozip.enabled").equals("true")) {
            // 将文件copy到临时目录
            FileSystem.copyFolderExchange(toPath, "p:/tmp/zz/");
            // FileUtil.getFolderPath(pv.exchangePath(fromPath))
        }
    
        if (pv.getProperty("svn.tozip.enabled").equals("true")) {
            try {
                ZipFileUtils.ZipFiles(new File("p:/zz.zip"), "", new File("p:/tmp/zz/"));
            } catch (IOException e) {
                e.printStackTrace();
            }
    
        }
    
    }

}
