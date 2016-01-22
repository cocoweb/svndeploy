package com.foresee.xdeploy;

import org.tmatesoft.svn.core.SVNException;

import com.foresee.xdeploy.file.ExcelFiles;
import com.foresee.xdeploy.file.ExcelSvnHelper;
import com.foresee.xdeploy.file.ExchangePath;
import com.foresee.xdeploy.file.PropValue;
import com.foresee.xdeploy.file.SvnFile;
import com.foresee.xdeploy.file.SvnFiles;
import com.foresee.xdeploy.utils.PathUtils;
import com.foresee.xdeploy.utils.svn.SvnClient;
import com.foresee.xdeploy.utils.zip.Zip4jUtils;

public class ListToFileHelper {
    static PropValue pv = null;
    ExcelSvnHelper excelsvnhelper=null;

    public ListToFileHelper() {
        this("/svntools.properties");
    }

    public ListToFileHelper(String propFileName) {
        pv = new PropValue(propFileName);
        excelsvnhelper = new ExcelSvnHelper();
    }

    public void scanPrintList() {
        System.out.println("===========显示待处理文件清单=================");
        
        ExcelFiles excelfiles= new ExcelFiles(pv);

        // 扫描并获取全部excel内容
        SvnFiles sf = excelsvnhelper.loadSvnFiles(excelfiles);

        displayList(sf);

    }

    
    public  void displayList(SvnFiles svnfiles) {
        StringBuffer bugStr = new StringBuffer();
        String a1_Path = ""; // 用来比较上下路径的标记
        String lastStr = "";
        
        for(SvnFile sf:svnfiles){
            String sPath = sf.getPath( pv.filekeyroot);
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

        SvnClient xclient = SvnClient.getInstance(pv.getProperty("svn.username"), pv.getProperty("svn.password"));
        ExcelFiles excelfiles= new ExcelFiles(pv);//???
        
        // 扫描并获取全部excel内容
        SvnFiles sfs = excelsvnhelper.loadSvnFiles(excelfiles);

        String zipFileName = pv.genOutZipFileName();
        int fileCount = 0;
        
        for(SvnFile sf:sfs){

        //for (ArrayList<String> aRow : ScanIncrementFiles.scanListfile(pv.excelfile, pv.excelFolder, pv.scanOption, pv.excelFolderFilter)) {
            try {
                String fromPath = sf.getPath("trunk");   //PathUtils.autoPathRoot(sf.getPath() aRow.get(ColList_Path), "trunk");
                ExchangePath expath = ExchangePath.exchange(fromPath);
                
                String sUrl = expath.getTrunkURL();   //pv.svnurl + fromPath; // svn库的文件绝对路径URL
                String sVer = sf.getVer();  //aRow.get(ColList_Ver);
                String toPath =PathUtils.autoUrlToPath(sUrl, pv.svntofolder, pv.keyRootFolder);

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


}
