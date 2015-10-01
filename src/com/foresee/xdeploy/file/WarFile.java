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
    
    public int copyToZip(ZipFile zipOutFile, ExchangePath expath){
        int retint = -1;
        
        if (expath.SrcPath.lastIndexOf(".java") > 0||expath.inJar()) { // 从jar抽取class、xml
            
            // TODO  同时抽取java源文件加入到zip中 
            retint =  copyJavaToZip(zipOutFile, expath);
            
            if (retint==0){
                  System.out.println("     抽取class :" + expath.ToZipPath);
             }else{
                 System.err.println("   !!抽取失败  :\n" + expath);
             }

         } else {
             retint = copyFileToZip(zipOutFile,expath);
             
             if (retint==-1){
                System.out.println("    ！抽取失败  :" + expath); 
             }else             
                System.out.println("     抽取文件  :" + expath.ToZipPath);
         }
        
        return retint;

    }
    
    private int copyFileToZip(ExchangePath expath){
        try {
            
            if(expath.FromPath.isEmpty()) return -1;
            
            ZipFile zipOutFile =new ZipFile(expath.getOutZipFileName());
            return copyFileToZip(zipOutFile,expath.FromPath,expath.ToZipPath);

 
        } catch (ZipException e) {
            e.printStackTrace();
         } 
        
        return 0;
        
    }
    
    private int copyFileToZip(ZipFile zipOutFile, ExchangePath expath){
        if(expath.FromPath.isEmpty()) return -1;
         return copyFileToZip(zipOutFile,expath.FromPath,expath.ToZipPath);
         
     }
    
    private int copyFileToZip(ZipFile zipOutFile, String sPath,String dPath){
        Zip4jUtils.ZipCopyFile2Zip(warZipFile, sPath, zipOutFile, dPath);
        
        return 0;
        
    }
    
    
    private int copyJavaToZip(ExchangePath exPath) {
        return copyJavaToZip(exPath.getOutZipFileName(),exPath.FromPath,exPath.JARName);
    }
    
    private int copyJavaToZip(ZipFile toZipFile, ExchangePath exPath) {
        return copyJavaToZip(toZipFile,exPath.FromPath,exPath.JARName);
    }


    
    private int copyJavaToZip(String toZip, ExchangePath exPath) {
        return copyJavaToZip(toZip,exPath.FromPath,exPath.JARName);
        
//        //ZipFile jarfile = null;
//        try {
//            ZipFile jarfile = getJarZipFile(exPath.JARName);
//
//            // java文件中可能会有子类(如 aaaa$bbb.class)，需要检查,并生成list
//            String javaName = exPath.FromPath.substring(0, exPath.FromPath.lastIndexOf("."));
//            
//            List<FileHeader> listJavaFile = Zip4jUtils.searchZipFiles(jarfile, javaName);
//            
//            ZipFile zipOutFile =new ZipFile(toZip);
//
//            for (FileHeader fileheader : listJavaFile) {
//                InputStream isfile = jarfile.getInputStream(fileheader);
//
//                Zip4jUtils.AddStreamToZip(zipOutFile, isfile, exPath.ToZipPath);
//                // "com.foresee.etax.bizfront/com/foresee/etax/bizfront/constant/EtaxBizFrontConstant.class"
//
//                isfile.close();
//            }
//
//        } catch (ZipException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } 

    }


    /**
     * 复制java文件到指定的zip中
     * 
     * @param toZip
     * @param javafile
     * @param jarName
     * @return  0：成功   -1：失败
     */
    private int copyJavaToZip(String toZip, String javafile, String jarName) {
        try {
            
            ZipFile zipOutFile =new ZipFile(toZip);
            return copyJavaToZip(zipOutFile,javafile,jarName);

 
        } catch (ZipException e) {
            e.printStackTrace();
         } 
        
        return 0;

    }
    private int copyJavaToZip(ZipFile zipOutFile, String javafile, String jarName) {
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
