package com.foresee.xdeploy.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

public class Zip4jUtils {

    public Zip4jUtils() {
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
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE); // set
                                                                          // compression
                                                                          // method
                                                                          // to
                                                                          // deflate
                                                                          // compression
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            zipFile.addFiles(filesToAdd, parameters);

        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

    public static void zipFile(String srcFile, String destZIP, String rootFolderInZip) {
        try {

            ZipFile zipFile = new ZipFile(destZIP);

            ArrayList<File> filesToAdd = new ArrayList<File>();
            filesToAdd.add(new File(srcFile));
            // filesToAdd.add(new File("c:\\ZipTest\\myvideo.avi"));
            // filesToAdd.add(new File("c:\\ZipTest\\mysong.mp3"));

            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE); // set
                                                                          // compression
                                                                          // method
                                                                          // to
                                                                          // deflate
                                                                          // compression
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            parameters.setRootFolderInZip(rootFolderInZip);

            zipFile.addFiles(filesToAdd, parameters);

        } catch (ZipException e) {
            e.printStackTrace();
        }

    }

    public static void InfoZipFile(String zipFileName)   {
        final int javaCount[] = { 0 };
        final int fileCount[] = { 0 };

        scanZipFiles(zipFileName, new IHandleZipFile() {
            @Override
            public void handleFile(FileHeader fileHeader, ZipFile zipFile) {
                fileCount[0]++;
                if (fileHeader.getFileName().endsWith(".java"))
                    javaCount[0]++;

            }

        });

        System.out.println("\n>>>> ZIP 文件 " + zipFileName + " 共有压缩文件数：" + fileCount[0]);
        System.out.println("  >> 其中Java文件：" + javaCount[0]+"\n");

    }

    public static void ListAllFilesInZipFile(String zipFileName) {

        scanZipFiles(zipFileName, new IHandleZipFile() {
            @Override
            public void handleFile(FileHeader fileHeader, ZipFile zipFile) {

                // FileHeader contains all the properties of the file
                // System.out.println("****File Details for: " +
                // fileHeader.getFileName() + "*****");
                System.out.println("Name: " + fileHeader.getFileName());
                System.out.println("Compressed Size: " + fileHeader.getCompressedSize());
                System.out.println("Uncompressed Size: " + fileHeader.getUncompressedSize());
                // System.out.println("CRC: " + fileHeader.getCrc32());
                System.out.println("************************************************************");

                // Various other properties are available in FileHeader. Please
                // have a look at FileHeader
                // class to see all the properties

            }

        });
    }

    /**
     * 扫描Zip中的文件的回调接口
     *
     */
    public interface IHandleZipFile {
        public void handleFile(FileHeader fileHeader, ZipFile zipFile);

    }

    public static void scanZipFiles(String zipFileName, IHandleZipFile xhandleZipFile) {
        try {
            // Initiate ZipFile object with the path/name of the zip file.
            ZipFile zipFile = new ZipFile(zipFileName);

            // Get the list of file headers from the zip file
            @SuppressWarnings("unchecked")
            List<FileHeader> fileHeaderList = zipFile.getFileHeaders();

            // Loop through the file headers
            for (int i = 0; i < fileHeaderList.size(); i++) {
                FileHeader fileHeader = fileHeaderList.get(i);

                if (xhandleZipFile != null) {
                    xhandleZipFile.handleFile(fileHeader, zipFile);

                } else {

                }
            }

        } catch (ZipException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws Exception {
        //
        // ZipFile xzip = new ZipFile("d:/gt3nf-skin-2.0.0.3444.war");

        // ZipFileUtils.ZipDecompress(frompath, topath);
        // for(Iterator e: xzip.entries().){
        //
        // }

        // ZipFileUtils.readZipFile("d:/gt3nf-skin-2.0.0.3444.war");
        // ZipFileUtils.getZipFile("d:/gt3nf-skin-2.0.0.3444.war",
        // "www/style/images-swzj-01/atable-01/ico_fujian.gif",
        // "p:/tmp/www/style/images-swzj-01/atable-01/ico_fujian.gif");
        ListAllFilesInZipFile("p:\\tmp\\QGTG-YHCS.20153121 1009.zip");
        InfoZipFile("p:\\tmp\\QGTG-YHCS.20153121 1009.zip");

    }

}
