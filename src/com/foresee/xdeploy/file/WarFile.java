package com.foresee.xdeploy.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.foresee.xdeploy.utils.Zip4jUtils;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

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

    public WarFile(String fileName) {
        this(new File(fileName));
    }

    public WarFile(File file) {
        warFile = file;
        try {
            warZipFile = new ZipFile(file);
        } catch (ZipException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getName() {
        return warFile.getName();
    }

    public String getPath() {
        return warFile.getPath();
    }

    /**
     * 根据Jar名称，获取Jar文件在war中的路径
     * 
     * @param jarName
     * @return
     */
    public String getJarPath(String jarName) {
        try {
            return Zip4jUtils.getFileNameFromZIP(warZipFile, jarName);
        } catch (ZipException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    public ZipFile getJarZipFile(String jarName) {
        return Zip4jUtils.getZipFileFromZIP(warZipFile, jarName);

    }
    
    public void copyJavaToZip(String toZip, ExchangePath exPath) {
        //ZipFile jarfile = null;
        try {
            ZipFile jarfile = getJarZipFile(exPath.JARName);

            // java文件中可能会有子类(如 aaaa$bbb.class)，需要检查,并生成list
            String javaName = exPath.FromPath.substring(0, exPath.FromPath.lastIndexOf("."));
            
            List<FileHeader> listJavaFile = Zip4jUtils.searchZipFiles(jarfile, javaName);
            
            ZipFile zipOutFile =new ZipFile(toZip);

            for (FileHeader fileheader : listJavaFile) {
                InputStream isfile = jarfile.getInputStream(fileheader);

                Zip4jUtils.AddStreamToZip(zipOutFile, isfile, exPath.ToZipPath);
                // "com.foresee.etax.bizfront/com/foresee/etax/bizfront/constant/EtaxBizFrontConstant.class"

                isfile.close();
            }

        } catch (ZipException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } 

    }


    /**
     * 复制java文件到指定的zip中
     * 
     * @param toZip
     * @param javafile
     * @param jarName
     * @return  0：成功   -1：失败
     */
    public int copyJavaToZip(String toZip, String javafile, String jarName) {
        try {
            
            ZipFile zipOutFile =new ZipFile(toZip);
            return copyJavaToZip(zipOutFile,javafile,jarName);

 
        } catch (ZipException e) {
            e.printStackTrace();
         } 
        
        return 0;

    }
    public int copyJavaToZip(ZipFile zipOutFile, String javafile, String jarName) {
        try {
            ZipFile jarfile = getJarZipFile(jarName);

            // java文件中可能会有子类(如 aaaa$bbb.class)，需要检查,并生成list
            String javaName = javafile.substring(0, javafile.lastIndexOf("."));
            
            List<FileHeader> listJavaFile = Zip4jUtils.searchZipFiles(jarfile, javaName);
            if(listJavaFile.size()<1) {
                return -1;
            }

            for (FileHeader fileheader : listJavaFile) {
                InputStream isfile = jarfile.getInputStream(fileheader);

                Zip4jUtils.AddStreamToZip(zipOutFile, isfile, jarName + "/" + fileheader.getFileName());
                // "com.foresee.etax.bizfront/com/foresee/etax/bizfront/constant/EtaxBizFrontConstant.class"

                isfile.close();
            }

        } catch (ZipException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } 
        
        return 0;

    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
