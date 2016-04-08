package com.foresee.xdeploy;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNClientManager;

import com.foresee.test.util.io.FileCopyUtil;
import com.foresee.test.util.io.FileUtil;
import com.foresee.test.util.lang.StringUtil;
import com.foresee.xdeploy.file.ExcelFiles;
import com.foresee.xdeploy.file.ExcelSvnHelper;
import com.foresee.xdeploy.file.ExchangePath;
import com.foresee.xdeploy.file.PropValue;
import com.foresee.xdeploy.file.SvnFile;
import com.foresee.xdeploy.file.SvnFiles;
import com.foresee.xdeploy.file.ToZipFile;
import com.foresee.xdeploy.file.WarFile;
import com.foresee.xdeploy.file.WarFiles;
import com.foresee.xdeploy.utils.PathUtils;
import com.foresee.xdeploy.utils.svn.SVNUtil;
import com.foresee.xdeploy.utils.svn.SvnClient;
import com.foresee.xdeploy.utils.zip.Zip4jUtils;

import net.lingala.zip4j.core.ZipFile;

public class ListToFileHelper {
    public static final String BATCH = "BATCH";
    public static final String FILE = "FILE";

    public static PropValue pv = null;
    ExcelSvnHelper excelsvnhelper = null;

    public ListToFileHelper() {
        this("/svntools.properties");
    }

    public ListToFileHelper(String propFileName) {
        pv = PropValue.getInstance(propFileName);
        excelsvnhelper = new ExcelSvnHelper();
    }

    private SvnFiles loadSvnFiles() {
        ExcelFiles excelfiles = new ExcelFiles(pv);

        // 扫描并获取全部excel内容
        return excelsvnhelper.loadSvnFiles(excelfiles);

    }

    public void scanPrintList() {
        System.out.println("===========显示待处理文件清单=================");

        // 扫描并获取全部excel内容
        SvnFiles sfs = loadSvnFiles();
        
        //sfs.removeDeuplicate();

        printList(sfs);

    }

    public void printList(SvnFiles svnfiles) {
        StringBuffer bugStr = new StringBuffer();
        String a1_Path = ""; // 用来比较上下路径的标记
        String lastStr = "";

        for (SvnFile sf : svnfiles) {
            String sPath = sf.getPath(pv.filekeyroot);
            sf.setKeyRoot(pv.filekeyroot);
            try {
                sf.checkProject();
            } catch (Exception e) {
                
                //e.printStackTrace();
               System.out.println( "   >>>"+e.getMessage()+"<<<  "+sf);
            }
            String printStr = sf.toString();

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
        System.out.println("\n共有文件数量：" + Integer.toString(svnfiles.size()));
        System.out.println("==空的版本号，将获取最新的版本。==请仔细检查清单格式，路径不对将无法从svn获取。");
        if (pv.getProperty("file.excel.merge").equals("true"))
             System.out.println(" >>>合并生成了EXCEL为：" + ExcelFiles.genOutExcelFileName());

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

        // 扫描并获取全部excel内容
        SvnFiles sfs = loadSvnFiles();
        
        // 排重
        sfs.removeDeuplicate();

        svnToPath(sfs);

        // export svn文件后，自动提交到基线分支
        if (pv.getProperty("svn.autocommit").equals("true"))
            commitsvn(pv.getProperty("svn.workspace"), pv.svntofolder, "提交文件：" + sfs.excelFiles);

    }

    public String svnToPath(SvnFiles svnfiles) {
        SvnClient xclient = SvnClient.getInstance(pv.getProperty("svn.username"), pv.getProperty("svn.password"));
        ToZipFile tozipfile = new ToZipFile(xclient);
        
        //String zipFileName = pv.genOutZipFileName();
        String exportToPath = pv.svntofolder;

        int fileCount = 0;
        long lVer = -1;
        String sMessage = "";

        for (SvnFile svnfile : svnfiles) {

            // for (ArrayList<String> aRow :
            // ScanIncrementFiles.scanListfile(pv.excelfile, pv.excelFolder,
            // pv.scanOption, pv.excelFolderFilter)) {
            try {
                String fromPath = svnfile.getPath("trunk"); // PathUtils.autoPathRoot(sf.getPath()
                // aRow.get(ColList_Path),
                // "trunk");
                ExchangePath expath = ExchangePath.exchange(fromPath);

                String sUrl = expath.getSvnURL() ;//expath.getTrunkURL(); // pv.svnurl + fromPath; //
                                                    // svn库的文件绝对路径URL
                String sVer = svnfile.getVer(); // aRow.get(ColList_Ver);
                String toPath = PathUtils.autoUrlToPath(sUrl, exportToPath, pv.keyRootFolder);

                // 判断是否目录，目录就不操作
                if (PathUtils.isFolder(sUrl)) {
                    System.out.println("目录不处理" + sUrl);
                } else {
                    if (pv.getProperty("svn.version.verify").equals("true")) { // 必须校验版本号
                        if (xclient.CheckFileVersion(sUrl, sVer)) {

                            xclient.svnExport(sUrl, sVer, toPath, pv.keyRootFolder);

                            System.out.println("export 版本：" + sVer + "|| url=" + sUrl);

                        } else {
                            System.err.println(" -->>>文件版本不存在：[" + sVer + "]" + sUrl);
                            continue;
                        }
                    } else {// 允许不校验版本号

                        if (StringUtil.isBlank(sVer)) {
                            sMessage = "export Last版本：";

                        } else if (xclient.CheckFileVersion(sUrl, sVer)) {
                            sMessage = "export 版本：";

                        } else {
                            sMessage = "export Last版本(原" + sVer + ")：";
                        }

                        try {
                            lVer = xclient.svnExport(sUrl, sVer, toPath, pv.keyRootFolder);

                            System.out.println(sMessage + Long.toString(lVer) + "|| url=" + sUrl);

                        } catch (SVNException e) {
                            e.printStackTrace();
                            System.err.println(" -->>>文件版本不存在：[" + sVer + "]" + sUrl);
                        }

                    }

                 
                    if (pv.getProperty("svn.tozip.enabled").equals("true")) {
                        // 将文件添加到zip文件
                        tozipfile.addToZip(toPath, expath, svnfile);

                    }

                }
                fileCount++;
            } catch (SVNException e) {
                e.printStackTrace();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        System.out.println("\nTotal " + Integer.toString(fileCount) + " Files, Exported to path ="
                + PathUtils.addFolderEnd(exportToPath) + pv.keyRootFolder);

        Zip4jUtils.InfoZipFile(tozipfile.toZipPath);

        return exportToPath;

    }

//    private void addToZip(String toPath, String zipFileName, ExchangePath expath, SvnFile svnfile) {
//
//        String[] packages =StringUtil.split(svnfile.getProj(),",、，"); 
//        // if (spack.contains(","))
//        // pakages = spack.split(",");
//
//        for (String pak : packages) {
//            //System.out.println(pak + "@@" + expath.getToZipFolderPath());
//            if(new File(toPath).exists())
//                Zip4jUtils.zipFile(toPath, zipFileName, expath.getToZipFolderPath(pak));//??
//        }
//
//    }

    public void commitsvn() {
        ExcelFiles excelfiles = new ExcelFiles(pv);
        commitsvn(pv.getProperty("svn.workspace"), pv.svntofolder, "提交文件：" + excelfiles);

    }

    /**
     * // export到临时目录 // 复制到svn工作区，提交
     * 
     * @param svnWorkspace
     * @param fromDir
     * @param sMessage
     */
    private void commitsvn(String svnWorkspace, String fromDir, String sMessage) {

        SVNClientManager svnmanager = SVNUtil.authSvn(pv.svnurl, pv.getProperty("svn.username"), pv.getProperty("svn.password"));

        File f = new File(svnWorkspace);

        FileCopyUtil.copyFolder(fromDir, svnWorkspace);

        // 添加变更
        SVNUtil.addEntry(svnmanager, f);

        SVNCommitInfo info = SVNUtil.commit(svnmanager, f, false, sMessage);

        System.out.println("提交文件到svn：" + info + "@svnWorkspace\n " + sMessage);

    }

    /**
     * 从war包导出文件到增量
     */
    public void scanWarToZip() {
        System.out.println("===========从指定压缩文件war、zip、jar 导出到zip文件=================");

        // 扫描并获取全部excel内容
        SvnFiles sfs = loadSvnFiles();
        // 排重
        sfs.removeDeuplicate();

        warToZip(sfs);

    }

    public void warToZip(SvnFiles svnfiles) {

        int fileCount = 0;

        SvnClient xclient = SvnClient.getInstance(pv.getProperty("svn.username"), pv.getProperty("svn.password"));

        ToZipFile tozipfile = new ToZipFile(xclient);

        // war包的清单
        WarFiles warlist = new WarFiles(pv.getProperty("zip.folder"), pv.getProperty("zip.folder.filter"));
        System.out.println("Loading.." + warlist);
        

        // 扫描excel文件的清单
        for (SvnFile svnfile : svnfiles) {

            try {
                fileCount += tozipfile.addToZip(warlist, svnfile);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("\n    >>> 成功抽取文件数:" + fileCount);

        //   对zip文件进行检查，对比excel的文件，和zip中的文件
        tozipfile.FileInfo();

        //Zip4jUtils.InfoZipFile(tozipfile.toZipPath);

    }


    /**
     * 扫描清单文件，从指定目录 导出文件到 临时输出目录
     */
    public void scanWorkspaceToPath() {
        System.out.println("===========从指定目录 导出到临时目录，或者workspace=================");
        // 扫描并获取全部excel内容
        SvnFiles sfs = loadSvnFiles();

        String ciworkspace = pv.getProperty("ci.workspace");
        String citoFolder = pv.getProperty("ci.tofolder");
        String cikeyroot = pv.getProperty("ci.keyroot");

        // 扫描excel文件的清单
        // ScanIncrementFiles xx = new ScanIncrementFiles(excelfile,
        // excelFolder, scanOption);
        // for (ArrayList<String> aRow :
        // ScanIncrementFiles.scanListfile(pv.excelfile, pv.excelFolder,
        // pv.scanOption, pv.excelFolderFilter)) {
        for (SvnFile sf : sfs) {

            try {
                String sPath = ciworkspace + sf.getPath(); // 源文件路径citoFolder
                String dPath = PathUtils.javaToclass(PathUtils.autoUrlToPath(sPath, citoFolder, cikeyroot)); // 目标路径

                if (sPath.indexOf(cikeyroot) > 0) {
                    // 创建目录
                    FileUtil.createFolder(FileUtil.getFolderPath(dPath));
                    FileUtil.Copy(sPath, dPath);
                } else {
                    System.err.println("ci.keyroot 配置错误，未包含在复制路径中！");
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

}
