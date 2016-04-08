package com.foresee.xdeploy.file;

import java.io.File;
import java.util.Date;

import org.tmatesoft.svn.core.SVNException;

import com.foresee.test.util.io.FileUtil;
import com.foresee.test.util.lang.DateUtil;
import com.foresee.test.util.lang.StringUtil;
import com.foresee.xdeploy.utils.PathUtils;
import com.foresee.xdeploy.utils.svn.SvnClient;
import com.foresee.xdeploy.utils.zip.Zip4jUtils;

import net.lingala.zip4j.core.ZipFile;

public class ToZipFile extends XdeployBase {
    public String toZipPath = "";
    public ZipFile toZipFile = null;
    PropValue pv = null;
    
    public SvnClient svnclient=null;

    public ToZipFile(String zipPath, PropValue propvalue) {
        toZipPath = zipPath;
        toZipFile = Zip4jUtils.genZipFile(zipPath);

        pv = propvalue;
    }

    public ToZipFile(PropValue propvalue) {
        this(ToZipFile.getOutZipFileName(), propvalue);

    }

    public ToZipFile() {
        this(PropValue.getInstance());
    }
    
    public ToZipFile(SvnClient xclient) {
        this(PropValue.getInstance());
        svnclient = xclient;
    }

    /**
     * 从war包提取文件，并添加到zip
     * 
     * @param warlist
     * @param xclient
     * @param sf
     * @return
     */
    public int addToZip(WarFiles warlist,  SvnFile sf) {
        int fileCount = 0;

        ExchangePath expath=sf.getExchange();

        String[] packages = StringUtil.split(sf.getProj(), ",、，"); // sf.getProj().split(",");
        for (String pak : packages) {
            // 判断清单中的工程名，是否包含在 war包中
            // 包含就抽取到目标路径
            WarFile warfile = warlist.getWarFile(pak);
            if (warfile != null) {
                fileCount+= warfile.copyToZip(this, sf);
                
                if (expath.isJava()) {
                    // 同时抽取java源文件加入到zip中（直接从svn获取）
                    fileCount+=exportSvnToZip( sf);
                    
                }

            } else {
                System.err.println("   !!没能抽取  :" + expath.SrcPath+" @ "+ pak);
            }

        }
        return fileCount;
    }
    
    public int exportSvnToZip( SvnFile sf){
        int retint=0;
        
        ExchangePath expath=sf.getExchange();
        // 同时抽取java源文件加入到zip中（直接从svn获取）
        String tmpFilePath = pv.tempPath + "/" + expath.getFileName();

        try {
            svnclient.svnExport(expath.getSvnURL(), sf.getVer(), tmpFilePath, pv.keyRootFolder);
//            if(expath.getType().equals(ExchangePath.Type_CHG)){
//                Zip4jUtils.zipFile(tmpFilePath, toZipFile, expath.getToZipFolderPath());
//                
//            }else{
                // 将文件添加到zip文件
                Zip4jUtils.zipFile(tmpFilePath, toZipFile, expath.getToZipFolderPath());
//            }

            retint++;
            FileUtil.delFile(tmpFilePath);
        } catch (Exception e) {
            e.printStackTrace();
            retint--;
        }
        
        
        return retint;
    }

    /**
     * 将文件添加到zip
     * 
     * @param fromFilePath
     * @param expath
     * @param svnfile
     */
    public void addToZip(String fromFilePath, ExchangePath expath, SvnFile svnfile) {

        String[] packages = StringUtil.split(svnfile.getProj(), ",、，");

        for (String pak : packages) {
            // System.out.println(pak + "@@" + expath.getToZipFolderPath());
            if (new File(fromFilePath).exists())
                Zip4jUtils.zipFile(fromFilePath, toZipPath, expath.getToZipFolderPath(pak));// ??
        }

    }

    private static String outzipfilename = "";

    public static String genOutZipFileName(PropValue propvalue) {
        if (outzipfilename == "") {
            // return PathUtils.addFolderEnd(pv.getProperty("zip.tofolder")) +
            // "QGTG-YHCS." + DateUtil.getCurrentDate("yyyyMMdd-HHmm") + ".zip";
            outzipfilename = PathUtils.addFolderEnd(propvalue.getProperty("zip.tofolder")) + "QGTG-YHCS."
                    + DateUtil.format(new Date(), "yyyyMMdd-HHmm") + ".zip";
            
        }
        return outzipfilename;
    }
    public static String genOutZipFileName(){
        return genOutZipFileName(PropValue.getInstance());
    }
    public static String getOutZipFileName(){
        outzipfilename = "";   //重新生成新的zip文件
        String ss = genOutZipFileName(PropValue.getInstance());
        System.out.println(ss);
        return ss;
    }

    public void FileInfo() {
        // 对zip文件进行检查，对比excel的文件，和zip中的文件
        if (new File(toZipPath).exists())   Zip4jUtils.InfoZipFile(toZipPath);
    }

}
