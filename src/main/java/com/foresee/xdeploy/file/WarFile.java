package com.foresee.xdeploy.file;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.foresee.test.util.PathUtils;
import com.foresee.xdeploy.file.rule.PackageType;
import com.foresee.xdeploy.utils.zip.Zip4jUtils;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

/**
 * War file对象类
 * 
 * @author Administrator
 *
 */
public class WarFile {
    // public String warFileName ="";
    public File warFile = null;

    public ZipFile warZipFile = null;

    private String warName = "";

    protected WarFile(String fileName) {
        this(new File(fileName));
    }

    protected WarFile(File file) {
        warFile = file;
        try {
            warZipFile = new ZipFile(file);
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }
    public WarFile(File file,String warname) {
        this(file);
        setWarName(warname);
    }

    /**
     * @return war包名
     */
    public String getWarName() {
        return warName;
    }

    /**
     * 设置war包名
     * @param warname
     */
    public void setWarName(String warname) {
        warName = warname;
    }

    /**
     * @return War文件名
     */
    public String getName() {
        return warFile.getName();
    }

    /**
     * @return War文件绝对路径
     */
    public String getPath() {
        return warFile.getPath();
    }
    
    /**
     * 获取一个清单记录的文件来源定位 字符串
     * 
     * @param sf
     * @return
     * remind-web-2.0.23100.168.04098.00-15972.war
     * <br>com.foresee.portal.biz-2.0.15803.jar @ gt3nf-admin-2.0.23100.168.04098.00-15972.war
     * <br>SVN <<https://nfsvn.foresee.com.cn/svn/GT3-NF-QGTGB/trunk/engineering/src/portal/vfs_home/bdmbdy/xml/dm_dj_cyqyjbb.xml
     */
    public String getSource(FilesListItem sf){
        String retstr = "";
        if(sf.isType(PackageType.Type_JAR)){
            retstr = PathUtils.getFileNameWithExt(getJarPath(sf.getExchange().JARName)) +" @ " +warZipFile.getFile().getName();
            
        }else if(sf.isType(PackageType.Type_WAR)){
            retstr = warZipFile.getFile().getName();
        }else if(sf.isType(PackageType.Type_NON)){
            retstr =sf.getPath();
        }else
            retstr = " SVN <<"+sf.getExchange().getSvnURL();
        
        return retstr;
        
    }
    
    /**
     * Jar文件名缓存列表map
     */
    Map<String,String> JarMap = new HashMap<String,String>();
    /**
     * jar文件 ZipFile对象缓存 map
     */
    Map<String,ZipFile> JarFileMap = new HashMap<String,ZipFile>();

    /**
     * 根据Jar名称，获取Jar文件在war中的路径
     * 
     * @param jarName
     * @return
     */
    public String getJarPath(String jarName) {
        try {
            if(JarMap.containsKey(jarName))
                return JarMap.get(jarName);
            else {
                String filename = Zip4jUtils.getFileNameFromZIP(warZipFile, jarName);
                JarMap.put(jarName, filename);
            
                return filename;
                
            }
        } catch (ZipException e) {
            e.printStackTrace();
        }
        return "";
    }
    

    /**
     * 获取Jar文件对象 ZipFile
     * 每个Jar只创建一次
     * @param jarName
     * @return
     */
    public ZipFile getJarZipFile(String jarName) {
        ZipFile tmpZip=null;
        if (JarFileMap.containsKey(jarName)){
            tmpZip= JarFileMap.get(jarName);
        }else{
            tmpZip= Zip4jUtils.getZipFileFromZIP(warZipFile, jarName);
            JarFileMap.put(jarName, tmpZip);
        }
        return tmpZip;
        
    }


}
