package com.foresee.xdeploy.file;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.foresee.test.util.PathUtils;
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

    protected WarFile(String fileName) {
        this(new File(fileName));
    }

    protected WarFile(File file) {
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
        if(sf.isType(ExchangePath.Type_JAR)){
            retstr = PathUtils.getFileNameWithExt(getJarPath(sf.getExchange().JARName)) +" @ " +warZipFile.getFile().getName();
            
        }else if(sf.isType(ExchangePath.Type_WAR)){
            retstr = warZipFile.getFile().getName();
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
            //retint = copyFileToZip(zipOutFile.toZipFile, expath);
            if(expath.MappingKey.contains("META-INF")){  //w.META-INF 类型
                retint = copyFileToZip(zipOutFile.toZipFile, expath.getToZipPathNoRoot(), expath.getToZipPath(warName));
            }else  // w.类型
                retint = copyFileToZip(zipOutFile.toZipFile, expath.FromPath, expath.getToZipPath(warName));

            
        }else{
            retint = zipOutFile.exportSvnToZip( sf);
            
        }
        
        if (retint >=0) {
            System.out.println("     抽取 "+ (sf.isType(ExchangePath.Type_JAR)?"Class":"文件") 
                    +" : " + expath.getToZipPath() +" @ "+getSource(sf));
        } else
            System.err.println("    ！抽取失败  :" + expath+"\n  >>> @ "+getSource(sf));

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
                System.out.println("     抽取class :" + expath.getToZipPath());
            } else {
                System.err.println("   !!抽取失败  :\n" + expath);
            }

        } else {
            retint = copyFileToZip(zipOutFile, expath);

            if (retint == -1) {
                System.err.println("    ！抽取失败  :" + expath);
            } else
                System.out.println("     抽取文件  :" + expath.getToZipPath());
        }

        return retint;

    }
    @Deprecated
    private int copyFileToZip(ExchangePath expath) {
        try {

            if (expath.FromPath.isEmpty())
                return -1;

            ZipFile zipOutFile = new ZipFile(expath.getOutZipFileName());
            return copyFileToZip(zipOutFile, expath.FromPath, expath.getToZipPath());

        } catch (ZipException e) {
            e.printStackTrace();
        }

        return 0;

    }

    @Deprecated
    private int copyFileToZip(ZipFile zipOutFile, ExchangePath expath) {
        if (expath.FromPath.isEmpty())
            return -1;
        
        if(expath.MappingKey.contains("META-INF")){  //w.META-INF 类型
            return copyFileToZip(zipOutFile, expath.getToZipPath().substring(expath.getToZipPath().indexOf("/")+1), expath.getToZipPath(warName));
        }else  // c.类型
            return copyFileToZip(zipOutFile, expath.FromPath, expath.getToZipPath(warName));

    }

    private int copyFileToZip(ZipFile zipOutFile, String sPath, String dPath) {
        if (sPath.isEmpty())    return -1;

        return Zip4jUtils.ZipCopyFile2Zip(warZipFile, sPath, zipOutFile, dPath);

    }
    @Deprecated
    private int copyJavaToZip(ExchangePath exPath) {
        return copyJavaToZip(exPath.getOutZipFileName(), exPath.FromPath, exPath.JARName);
    }

    private int copyJavaToZip(ZipFile toZipFile, ExchangePath exPath) {
        return copyJavaToZip(toZipFile, exPath.FromPath, exPath.JARName);
    }
    @Deprecated
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


}
