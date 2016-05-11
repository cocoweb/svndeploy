package com.foresee.xdeploy.file;

import java.io.File;
import java.util.Date;

import com.foresee.test.loadrunner.lrapi4j.lr;
import com.foresee.test.util.io.FileUtil;
import com.foresee.test.util.lang.DateUtil;
import com.foresee.test.util.lang.StringUtil;
import com.foresee.xdeploy.file.base.XdeployBase;
import com.foresee.xdeploy.utils.PathUtils;
import com.foresee.xdeploy.utils.svn.SVNRepo;
import com.foresee.xdeploy.utils.zip.Zip4jUtils;

import net.lingala.zip4j.core.ZipFile;

public class ToZipFile extends XdeployBase {
    public String toZipPath = "";
    public ZipFile toZipFile = null;
    PropValue pv = null;

    SVNRepo SvnRepo = null;

    protected ToZipFile(String zipPath, PropValue propvalue) {
        toZipPath = zipPath;
        toZipFile = Zip4jUtils.genZipFile(zipPath);

        pv = propvalue;
    }

    protected ToZipFile(PropValue propvalue) {
        this(ToZipFile.getNewOutZipFileName(), propvalue);

    }

    protected ToZipFile() {
        this(PropValue.getInstance());
    }

//    public ToZipFile(SvnClient xclient) {
//        this(PropValue.getInstance());
//        svnclient = xclient;
//    }
    
    public ToZipFile(SVNRepo svnrepo) {
        this(PropValue.getInstance());
        SvnRepo = svnrepo;
    }


    /**
     * 从war包提取文件，并添加到zip
     * 
     * @param warlist
     * @param xclient
     * @param sf
     * @return
     */
    public int takeWarFileToZip(final WarFiles warlist, final FilesListItem sf) {
        int fileCount = 0;
        final ExchangePath expath = sf.getExchange();
        fileCount=scanPackages(sf.getProjs(),new IHandlePackage(){

            @Override
            public int handlePackage(ToZipFile self, String pak) {
                int fileCount = 0;
                // 判断清单中的工程名，是否包含在 war包中
                // 包含就抽取到目标路径
                WarFile warfile = warlist.getWarFile(pak);
                if (warfile != null) {
                    fileCount += warfile.copyToZip(self, sf);
    
                    if (expath.isJava()) {
                        // 同时抽取java源文件加入到zip中（直接从svn获取）
                        fileCount += exportSvnToZip(sf,pak);
    
                    }
    
                } else {
                    System.err.println("   !!没能抽取  :" + expath.SrcPath + " @ " + pak);
                }
                
                return fileCount;
            }
            
        });

        return fileCount;
    }


    /**
     * 将文件添加到zip
     * 
     * @param svnfile
     */
    public void takeFileToZip( final FilesListItem svnfile) {
        scanPackages(svnfile.getProjs(),new IHandlePackage(){

            @Override
            public int handlePackage(ToZipFile self, String pak) {
                if (new File(svnfile.getExchange().getToFilePath()).exists())
                    Zip4jUtils.zipFile(svnfile.getExchange().getToFilePath(), toZipPath, svnfile.getExchange().getToZipFolderPath(pak));// ??
                return 0;
                
            }
        });
     
    }

    
    public int exportSvnToZip(FilesListItem sf,String pak){
        int retint = 0;
        ExchangePath expath = sf.getExchange();
        // 同时抽取java源文件加入到zip中（直接从svn获取）
        String tmpFilePath = expath.getToTempFilePath();
        // pv.tempPath + "/" + expath.getFileName();

        try {
            SvnRepo.Export(sf, tmpFilePath);

            // svnclient.svnExport(expath.getSvnURL(), sf.getVer(),
            // tmpFilePath, pv.keyRootFolder);
            // 将文件添加到zip文件
            Zip4jUtils.zipFile(tmpFilePath, toZipFile, lr.eval_string(expath.getToZipFolderPath()));

            retint++;
            FileUtil.delFile(tmpFilePath);
        } catch (Exception e) {
            e.printStackTrace();
            retint--;
        }
        return retint;

    }

    public int exportSvnToZip(final FilesListItem sf) {
        int retint = 0;

        retint = scanPackages(sf.getProjs(), new IHandlePackage() {

            @Override
            public int handlePackage(ToZipFile self, String pak) {
                return exportSvnToZip(sf,pak);
            }

        }

        );

        return retint;
    }
    /**
     * 处理package包的接口
     * 
     */
    public interface IHandlePackage {
        public int handlePackage(ToZipFile self,String pak);
    }
    
    /**
     * 扫描package字符串
     * @param sProj
     * @param handlepackage
     * @return
     */
    public int scanPackages(String[] Projs,IHandlePackage handlepackage){
        int ret=0;
        //String[] packages = StringUtil.split(sProj, ",、，");
        
        for (String pak : Projs) {
            // web工程参数保存
            lr.save_string(pak, LIST_Project);
            
            ret = handlepackage.handlePackage(this,pak);
        }
        return ret;
        
    }

    /**
     * 将文件添加到zip
     * 
     * @param fromFilePath
     * @param expath
     * @param svnfile
     */@Deprecated
    private void addToZip(String fromFilePath, ExchangePath expath, FilesListItem svnfile) {

        String[] packages = StringUtil.split(svnfile.getProj(), ",、，");

        for (String pak : packages) {
            // System.out.println(pak + "@@" + expath.getToZipFolderPath());
            if (new File(fromFilePath).exists())
                Zip4jUtils.zipFile(fromFilePath, toZipPath, expath.getToZipFolderPath(pak));// ??
        }

    }
    private static String outzipfilename = "";

    /**
     * 获取输出的zip文件名
     * @param propvalue
     * @return
     */
    public static String getOutZipFileName(PropValue propvalue) {
        if (outzipfilename == "") {
            // return PathUtils.addFolderEnd(pv.getProperty("zip.tofolder")) +
            // "QGTG-YHCS." + DateUtil.getCurrentDate("yyyyMMdd-HHmm") + ".zip";
            outzipfilename = PathUtils.addFolderEnd(propvalue.getProperty("zip.tofolder")) + "QGTG-YHCS."
                    + DateUtil.format(new Date(), "yyyyMMdd-HHmm") + ".zip";

        }
        return outzipfilename;
    }

    public static String getOutZipFileName() {
        return getOutZipFileName(PropValue.getInstance());
    }

    /**
     * 重新生成新的zip文件
     * @return  zip文件名
     */
    public static String getNewOutZipFileName() {
        outzipfilename = ""; // 重新生成新的zip文件
        String ss = getOutZipFileName(PropValue.getInstance());
       // System.out.println(ss);
        return ss;
    }

    /**
     * 输出zip文件信息
     */
    public void FileInfo() {
        // 对zip文件进行检查，对比excel的文件，和zip中的文件
        if (new File(toZipPath).exists())
            Zip4jUtils.InfoZipFile(toZipPath);
    }

}
