package com.foresee.xdeploy.tmp;

import java.io.InputStream;
import java.util.List;

import com.foresee.xdeploy.file.ExchangePath;
import com.foresee.xdeploy.file.FilesListItem;
import com.foresee.xdeploy.file.ToZipFile;
import com.foresee.xdeploy.utils.zip.Zip4jUtils;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

public class tmpWarFile {

	/**
	 * 将文件加入到指定的zip中
	 * 
	 * @param zipOutFile
	 * @param sf
	 * @return
	 */@Deprecated
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

	@Deprecated
	private int copyFileToZip(ZipFile zipOutFile, String sPath, String dPath) {
	    if (sPath.isEmpty())    return -1;
	
	    return Zip4jUtils.ZipCopyFile2Zip(warZipFile, sPath, zipOutFile, dPath);
	
	}

	@Deprecated
	private int copyJavaToZip(ExchangePath exPath) {
	    return copyJavaToZip(exPath.getOutZipFileName(), exPath.FromPath, exPath.JARName);
	}

	@Deprecated
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
	 */@Deprecated
	private int copyJavaToZip(String toZip, String javafile, String jarName) {
	    try {
	
	        ZipFile zipOutFile = new ZipFile(toZip);
	        return copyJavaToZip(zipOutFile, javafile, jarName);
	
	    } catch (ZipException e) {
	        e.printStackTrace();
	    }
	
	    return 0;
	
	}

	@Deprecated
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
