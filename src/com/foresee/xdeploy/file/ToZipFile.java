package com.foresee.xdeploy.file;

import java.io.File;
import java.util.Date;

import com.foresee.test.util.lang.DateUtil;
import com.foresee.xdeploy.file.base.XdeployBase;
import com.foresee.xdeploy.file.tozip.ToZipAction;
import com.foresee.xdeploy.utils.PathUtils;
import com.foresee.xdeploy.utils.svn.SVNRepo;
import com.foresee.xdeploy.utils.zip.Zip4jUtils;

import net.lingala.zip4j.core.ZipFile;

/**
 * 封装输出ZIP相关逻辑
 * @author allan
 *
 */
public class ToZipFile extends XdeployBase {
    public String toZipPath = "";
    public ZipFile toZipFile = null;
    PropValue pv = null;

    public SVNRepo SvnRepo = null;

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
    	return ToZipAction.Create4WarList(sf, warlist, this).operateList();
    }
    
    


    /**
     * 将文件添加到zip
     * 
     * @param svnfile
     */
    //TODO
    public void takeFileToZip( final FilesListItem svnfile) {
    	ToZipAction.Create4File(svnfile, this).operate();
    	
    }



    public int exportSvnToZip(final FilesListItem sf) {
    	
    	return ToZipAction.Create4Svn(sf, this).operate();

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
