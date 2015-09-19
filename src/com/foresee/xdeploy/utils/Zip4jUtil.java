package com.foresee.xdeploy.utils;

import java.io.File;
import java.util.ArrayList;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

public class Zip4jUtil {

    public Zip4jUtil() {
        // TODO Auto-generated constructor stub
    }
    public void AddFilesDeflateComp() {
        try {
             
            ZipFile zipFile = new ZipFile("c:\\ZipTest\\AddFilesDeflateComp.zip");
             
            ArrayList<File> filesToAdd = new ArrayList<File>();
            filesToAdd.add(new File("c:\\ZipTest\\sample.txt"));
            filesToAdd.add(new File("c:\\ZipTest\\myvideo.avi"));
            filesToAdd.add(new File("c:\\ZipTest\\mysong.mp3"));
             
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE); // set compression method to deflate compression
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            zipFile.addFiles(filesToAdd, parameters);
             
        } catch (ZipException e) {
            e.printStackTrace();
        }  
    }
    
    public static void zipFile(String srcFile, String destZIP, String rootFolderInZip){
        try {
            
            ZipFile zipFile = new ZipFile(destZIP);
             
            ArrayList<File> filesToAdd = new ArrayList<File>();
            filesToAdd.add(new File(srcFile));
//            filesToAdd.add(new File("c:\\ZipTest\\myvideo.avi"));
//            filesToAdd.add(new File("c:\\ZipTest\\mysong.mp3"));
             
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE); // set compression method to deflate compression
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            parameters.setRootFolderInZip(rootFolderInZip);
            
            zipFile.addFiles(filesToAdd, parameters);
             
        } catch (ZipException e) {
            e.printStackTrace();
        }  
       
        
        
    }

}
