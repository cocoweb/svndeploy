package com.foresee.xdeploy.file;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.foresee.xdeploy.utils.zip.Zip4jUtils;

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

    private String warName = "";

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
    public WarFile(File file,String warname) {
        this(file);
        setWarName(warname);
    }

    public String getWarName() {
        return warName;
    }

    public void setWarName(String warname) {
        warName = warname;
    }

    public String getName() {
        return warFile.getName();
    }

    public String getPath() {
        return warFile.getPath();
    }
    
    public String getSource(FilesListItem sf){
        String retstr = "";
        if(sf.isType(ExchangePath.Type_JAR)){
            retstr = getJarPath(sf.getExchange().JARName) +" @ " +warZipFile.getFile().getName();
            
        }else if(sf.isType(ExchangePath.Type_WAR)){
            retstr = warZipFile.getFile().getName();
        }else
            retstr = " SVN <<"+sf.getExchange().getSvnURL();
        
        return retstr;
        
    }
    
    Map<String,String> JarMap = new HashMap<String,String>();

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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }
    
    Map<String,ZipFile> JarFileMap = new HashMap<String,ZipFile>();

    public ZipFile getJarZipFile(String jarName) {
        ZipFile tmpZip=null;
        if (JarFileMap.containsKey(jarName)){
            tmpZip= JarFileMap.get(jarName);
        }else{
            tmpZip= Zip4jUtils.getZipFileFromZIP(warZipFile, jarName);
            JarFileMap.put(jarName, tmpZip);
        }
        return tmpZip;
        
        //return Zip4jUtils.getZipFileFromZIP(warZipFile, jarName);

    }
    /**
     * 将文件加入到指定的zip中
     * 
     * @param zipOutFile
     * @param sf
     * @return
     */
    public int copyToZip(ToZipFile zipOutFile, FilesListItem sf) {
        int retint = -1;
        
        
        ExchangePath expath=sf.getExchange();
        
        //判断文件类型 war、jar、chg
        if(sf.isType(ExchangePath.Type_JAR)){
            // 从jar抽取class、xml
            // java文件 或者 在mapping.j中找到的文件
            
            // 同时抽取java源文件加入到zip中
            retint = copyJavaToZip(zipOutFile.toZipFile, expath);
            
        }else if(sf.isType(ExchangePath.Type_WAR)){
            retint = copyFileToZip(zipOutFile.toZipFile, expath);
            
        }else{
            retint = zipOutFile.exportSvnToZip( sf);
            
        }
        
        if (retint >=0) {
            System.out.println("     抽取 "+ (sf.isType(ExchangePath.Type_JAR)?"Class":"文件") 
                    +" : " + expath.ToZipPath +" @ "+getSource(sf));
        } else
            System.err.println("    ！抽取失败  :" + expath);

        return retint;

    }

    @Deprecated
    public int copyToZip(ZipFile zipOutFile, ExchangePath expath) {
        int retint = -1;
        
        
        //判断文件类型 war、jar、chg


        // 从jar抽取class、xml
        // java文件 或者 在mapping.j中找到的文件
        if (expath.SrcPath.lastIndexOf(".java") > 0 || expath.inJar()) {

            // 同时抽取java源文件加入到zip中
            retint = copyJavaToZip(zipOutFile, expath);

            if (retint == 0) {
                System.out.println("     抽取class :" + expath.ToZipPath);
            } else {
                System.err.println("   !!抽取失败  :\n" + expath);
            }

        } else {
            retint = copyFileToZip(zipOutFile, expath);

            if (retint == -1) {
                System.err.println("    ！抽取失败  :" + expath);
            } else
                System.out.println("     抽取文件  :" + expath.ToZipPath);
        }

        return retint;

    }

    private int copyFileToZip(ExchangePath expath) {
        try {

            if (expath.FromPath.isEmpty())
                return -1;

            ZipFile zipOutFile = new ZipFile(expath.getOutZipFileName());
            return copyFileToZip(zipOutFile, expath.FromPath, expath.ToZipPath);

        } catch (ZipException e) {
            e.printStackTrace();
        }

        return 0;

    }

    private int copyFileToZip(ZipFile zipOutFile, ExchangePath expath) {
        if (expath.FromPath.isEmpty())
            return -1;
        
        if(expath.MappingKey.contains("META-INF")){
            return copyFileToZip(zipOutFile, expath.ToZipPath.substring(expath.ToZipPath.indexOf("/")+1), expath.getToZipPath(warName));
        }else
            return copyFileToZip(zipOutFile, expath.FromPath, expath.getToZipPath(warName));

    }

    private int copyFileToZip(ZipFile zipOutFile, String sPath, String dPath) {

        return Zip4jUtils.ZipCopyFile2Zip(warZipFile, sPath, zipOutFile, dPath);

    }

    private int copyJavaToZip(ExchangePath exPath) {
        return copyJavaToZip(exPath.getOutZipFileName(), exPath.FromPath, exPath.JARName);
    }

    private int copyJavaToZip(ZipFile toZipFile, ExchangePath exPath) {
        return copyJavaToZip(toZipFile, exPath.FromPath, exPath.JARName);
    }

    private int copyJavaToZip(String toZip, ExchangePath exPath) {
        return copyJavaToZip(toZip, exPath.FromPath, exPath.JARName);

        // //ZipFile jarfile = null;
        // try {
        // ZipFile jarfile = getJarZipFile(exPath.JARName);
        //
        // // java文件中可能会有子类(如 aaaa$bbb.class)，需要检查,并生成list
        // String javaName = exPath.FromPath.substring(0,
        // exPath.FromPath.lastIndexOf("."));
        //
        // List<FileHeader> listJavaFile = Zip4jUtils.searchZipFiles(jarfile,
        // javaName);
        //
        // ZipFile zipOutFile =new ZipFile(toZip);
        //
        // for (FileHeader fileheader : listJavaFile) {
        // InputStream isfile = jarfile.getInputStream(fileheader);
        //
        // Zip4jUtils.AddStreamToZip(zipOutFile, isfile, exPath.ToZipPath);
        // //
        // "com.foresee.etax.bizfront/com/foresee/etax/bizfront/constant/EtaxBizFrontConstant.class"
        //
        // isfile.close();
        // }
        //
        // } catch (ZipException e) {
        // e.printStackTrace();
        // } catch (IOException e) {
        // e.printStackTrace();
        // }

    }

    /**
     * 复制java文件到指定的zip中
     * 
     * @param toZip
     * @param javafile
     * @param jarName
     * @return 0：成功 -1：失败
     */
    private int copyJavaToZip(String toZip, String javafile, String jarName) {
        try {

            ZipFile zipOutFile = new ZipFile(toZip);
            return copyJavaToZip(zipOutFile, javafile, jarName);

        } catch (ZipException e) {
            e.printStackTrace();
        }

        return 0;

    }

    private int copyJavaToZip(ZipFile zipOutFile, String javafile, String jarName) {
        int retint=0;
        try {
            ZipFile jarfile = getJarZipFile(jarName);

            // java文件中可能会有子类(如 aaaa$bbb.class)，需要检查,并生成list
            String javaName = javafile.substring(0, javafile.lastIndexOf("."));

            List<FileHeader> listJavaFile = Zip4jUtils.searchZipFiles(jarfile, javaName);
            if (listJavaFile.size() < 1) {
                return retint-1;
            }

            for (FileHeader fileheader : listJavaFile) {
                InputStream isfile = jarfile.getInputStream(fileheader);

                Zip4jUtils.AddStreamToZip(zipOutFile, isfile, jarName + "/" + fileheader.getFileName());
                // "com.foresee.etax.bizfront/com/foresee/etax/bizfront/constant/EtaxBizFrontConstant.class"
                
                retint++;

                isfile.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            retint--;
        } 

        return retint;

    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
