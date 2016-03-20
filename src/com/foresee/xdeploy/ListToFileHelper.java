package com.foresee.xdeploy;

import java.io.File;

import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNClientManager;

import com.foresee.test.util.io.FileCopyUtil;
import com.foresee.test.util.lang.StringUtil;
import com.foresee.xdeploy.file.ExcelFiles;
import com.foresee.xdeploy.file.ExcelSvnHelper;
import com.foresee.xdeploy.file.ExchangePath;
import com.foresee.xdeploy.file.PropValue;
import com.foresee.xdeploy.file.SvnFile;
import com.foresee.xdeploy.file.SvnFiles;
import com.foresee.xdeploy.utils.PathUtils;
import com.foresee.xdeploy.utils.svn.SVNUtil;
import com.foresee.xdeploy.utils.svn.SvnClient;
import com.foresee.xdeploy.utils.zip.Zip4jUtils;

public class ListToFileHelper {
    public static PropValue pv = null;
    ExcelSvnHelper excelsvnhelper = null;

    public ListToFileHelper() {
        this("/svntools.properties");
    }

    public ListToFileHelper(String propFileName) {
        pv = new PropValue(propFileName);
        excelsvnhelper = new ExcelSvnHelper();
    }

    public void scanPrintList() {
        System.out.println("===========显示待处理文件清单=================");

        ExcelFiles excelfiles = new ExcelFiles(pv);

        // 扫描并获取全部excel内容
        SvnFiles sf = excelsvnhelper.loadSvnFiles(excelfiles);

        displayList(sf);

    }

    public void displayList(SvnFiles svnfiles) {
        StringBuffer bugStr = new StringBuffer();
        String a1_Path = ""; // 用来比较上下路径的标记
        String lastStr = "";

        for (SvnFile sf : svnfiles) {
            String sPath = sf.getPath(pv.filekeyroot);
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
        // if (pv.getProperty("file.excel.merge").equals("true"))
        // System.out.println(" >>>合并生成了EXCEL为：" + sTofile);

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

        ExcelFiles excelfiles = new ExcelFiles(pv);// ???

        // 扫描并获取全部excel内容
        SvnFiles sfs = excelsvnhelper.loadSvnFiles(excelfiles);

        svnExportPath(sfs);
        
        // export svn文件后，自动提交到基线分支
        if(pv.getProperty("svn.autocommit").equals("true"))
            commitsvn(pv.getProperty("svn.workspace"), pv.svntofolder, "提交文件："+excelfiles);


    }

    public String svnExportPath(SvnFiles svnfiles) {
        SvnClient xclient = SvnClient.getInstance(pv.getProperty("svn.username"), pv.getProperty("svn.password"));
        String zipFileName = pv.genOutZipFileName();
        String exportToPath = pv.svntofolder;
        
        int fileCount = 0;
        long lVer = -1;
        String sMessage = "";

        for (SvnFile sf : svnfiles) {

            // for (ArrayList<String> aRow :
            // ScanIncrementFiles.scanListfile(pv.excelfile, pv.excelFolder,
            // pv.scanOption, pv.excelFolderFilter)) {
            try {
                String fromPath = sf.getPath("trunk"); // PathUtils.autoPathRoot(sf.getPath()
                                                       // aRow.get(ColList_Path),
                                                       // "trunk");
                ExchangePath expath = ExchangePath.exchange(fromPath);

                String sUrl = expath.getTrunkURL(); // pv.svnurl + fromPath; //
                                                    // svn库的文件绝对路径URL
                String sVer = sf.getVer(); // aRow.get(ColList_Ver);
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
                            System.out.println(" -->>>文件版本不存在：[" + sVer + "]" + sUrl);
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
                            System.out.println(" -->>>文件版本不存在：[" + sVer + "]" + sUrl);
                        }

                    }

                    if (pv.getProperty("svn.tozip.enabled").equals("true")) {
                        // 将文件添加到zip文件
                        Zip4jUtils.zipFile(toPath, zipFileName, expath.getToZipFolderPath());
                        // FileUtil.getFolderPath(pv.exchangePath(fromPath)));

                    }

                }
                fileCount++;
            } catch (SVNException e) {
                e.printStackTrace();
            }
        }

        

        System.out.println("\nTotal " + Integer.toString(fileCount) + " Files, Exported to path ="
                + PathUtils.addFolderEnd(exportToPath) + pv.keyRootFolder);

        Zip4jUtils.InfoZipFile(zipFileName);
        
        
        return exportToPath;

    }
    public void commitsvn(){
        ExcelFiles excelfiles = new ExcelFiles(pv);
        commitsvn(pv.getProperty("svn.workspace"), pv.svntofolder, "提交文件："+excelfiles);
       
    }
    
    /**
     * //    export到临时目录
        //   复制到svn工作区，提交
     * @param svnWorkspace
     * @param fromDir
     * @param sMessage
     */
    private void commitsvn(String svnWorkspace, String fromDir, String sMessage){

        SVNClientManager svnmanager = SVNUtil.authSvn(pv.svnurl, pv.getProperty("svn.username"), pv.getProperty("svn.password"));
        
        File f = new File(svnWorkspace);
        
        FileCopyUtil.copyFolder(fromDir, svnWorkspace);
        
        //添加变更
        SVNUtil.addEntry(svnmanager, f);
        
        SVNCommitInfo info=SVNUtil.commit(svnmanager, f, false, sMessage);
        
        System.out.println("提交文件到svn："+info+"@svnWorkspace\n "+sMessage);
        
        
      
    }

}
