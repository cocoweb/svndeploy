package com.foresee.xdeploy.utils.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.foresee.xdeploy.utils.PathUtils;
import com.foresee.xdeploy.utils.tmp.StreamUtils;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.io.ZipOutputStream;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.unzip.UnzipUtil;
import net.lingala.zip4j.util.Zip4jConstants;

public class Zip4jUtils {

    static final int BUFF_SIZE = 4096;

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

            // set compression method to deflate compression
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            zipFile.addFiles(filesToAdd, parameters);

        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

    public void ExtractSelectFilesWithInputStream(String zipFileName, String fileName, String destinationPath) {

        ZipInputStream is = null;
        OutputStream os = null;

        try {
            // Initiate the ZipFile
            ZipFile zipFile = new ZipFile(zipFileName);
            // String destinationPath = "c:\\ZipTest";

            // If zip file is password protected then set the password
            if (zipFile.isEncrypted()) {
                zipFile.setPassword("password");
            }

            // Get the FileHeader of the File you want to extract from the
            // zip file. Input for the below method is the name of the file
            // For example: 123.txt or abc/123.txt if the file 123.txt
            // is inside the directory abc
            FileHeader fileHeader = zipFile.getFileHeader(fileName);

            if (fileHeader != null) {

                // Build the output file
                String outFilePath = destinationPath + System.getProperty("file.separator") + fileHeader.getFileName();
                File outFile = new File(outFilePath);

                // Checks if the file is a directory
                if (fileHeader.isDirectory()) {
                    // This functionality is up to your requirements
                    // For now I create the directory
                    outFile.mkdirs();
                    return;
                }

                // Check if the directories(including parent directories)
                // in the output file path exists
                File parentDir = outFile.getParentFile();
                if (!parentDir.exists()) {
                    parentDir.mkdirs(); // If not create those directories
                }

                // Get the InputStream from the ZipFile
                is = zipFile.getInputStream(fileHeader);
                // Initialize the output stream
                os = new FileOutputStream(outFile);

                int readLen = -1;
                byte[] buff = new byte[BUFF_SIZE];

                // Loop until End of File and write the contents to the output
                // stream
                while ((readLen = is.read(buff)) != -1) {
                    os.write(buff, 0, readLen);
                }

                // Closing inputstream also checks for CRC of the the just
                // extracted file.
                // If CRC check has to be skipped (for ex: to cancel the unzip
                // operation, etc)
                // use method is.close(boolean skipCRCCheck) and set the flag,
                // skipCRCCheck to false
                // NOTE: It is recommended to close outputStream first because
                // Zip4j throws
                // an exception if CRC check fails
                is.close();

                // Close output stream
                os.close();

                // To restore File attributes (ex: last modified file time,
                // read only flag, etc) of the extracted file, a utility class
                // can be used as shown below
                UnzipUtil.applyFileAttributes(fileHeader, outFile);

                System.out.println("Done extracting: " + fileHeader.getFileName());
            } else {
                System.err.println("FileHeader does not exist-->" + fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeDirFromZipArchive(String file, String removeDir) throws ZipException {
        // 创建ZipFile并设置编码
        ZipFile zipFile = new ZipFile(file);
        zipFile.setFileNameCharset("GBK");

        // 给要删除的目录加上路径分隔符
        if (!removeDir.endsWith(File.separator))
            removeDir += File.separator;

        // 如果目录不存在, 直接返回
        FileHeader dirHeader = zipFile.getFileHeader(removeDir);
        if (null == dirHeader)
            return;

        // 遍历压缩文件中所有的FileHeader, 将指定删除目录下的子文件名保存起来
        @SuppressWarnings("unchecked")
        List<FileHeader> headersList = zipFile.getFileHeaders();
        List<String> removeHeaderNames = new ArrayList<String>();
        for (int i = 0, len = headersList.size(); i < len; i++) {
            FileHeader subHeader = (FileHeader) headersList.get(i);
            if (subHeader.getFileName().startsWith(dirHeader.getFileName()) && !subHeader.getFileName().equals(dirHeader.getFileName())) {
                removeHeaderNames.add(subHeader.getFileName());
            }
        }
        // 遍历删除指定目录下的所有子文件, 最后删除指定目录(此时已为空目录)
        for (String headerNameString : removeHeaderNames) {
            zipFile.removeFile(headerNameString);
        }
        zipFile.removeFile(dirHeader);
    }

    /**
     * 将InputStream存入zip文件，并指定zip中的路径和文件名
     * 
     * @param zipFileName
     * @param isFile
     *            存入zip的InputStream
     * @param inzipName
     */
    public static void AddStreamToZip(String zipFileName, InputStream isFile, String inzipName) {
        if (isFile == null)
            return;

        try {
            // Initiate ZipFile object with the path/name of the zip file.
            // Zip file may not necessarily exist. If zip file exists, then
            // all these files are added to the zip file. If zip file does not
            // exist, then a new zip file is created with the files mentioned
            ZipFile zipFile = new ZipFile(zipFileName);
            AddStreamToZip(zipFile,isFile,inzipName);
 
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static void AddStreamToZip(ZipFile zipFile, InputStream isFile, String inzipName) {
        if (isFile == null)
            return;

        try {

            // Initiate Zip Parameters which define various properties such
            // as compression method, etc. More parameters are explained in
            // other
            // examples
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);

            // below two parameters have to be set for adding content to a zip
            // file
            // directly from a stream

            // this would be the name of the file for this entry in the zip file
            if (!inzipName.isEmpty())
                parameters.setFileNameInZip(inzipName);
            else
                parameters.setFileNameInZip("oo/kk/ppp");

            // we set this flag to true. If this flag is true, Zip4j identifies
            // that
            // the data will not be from a file but directly from a stream
            parameters.setSourceExternalStream(true);

            // if (!rootPath.isEmpty())
            // parameters.setRootFolderInZip(rootPath);

            // Creates a new entry in the zip file and adds the content to the
            // zip file
            zipFile.addStream(isFile, parameters);

        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
        
    /**
     * 添加文件到zip中
     * 
     * @param zipFileName
     * @param fileName
     */
    public void AddFileToZip(String zipFileName, String fileName) {

        InputStream is = null;

        try {

            // For this example I use a FileInputStream but in practise this can
            // be
            // any inputstream
            is = new FileInputStream(fileName);

            AddStreamToZip(zipFileName, is, PathUtils.getFileNameWithExt(fileName));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据输入流，生成zipfile对象
     * 
     * @param isZip
     * @return
     * @throws ZipException
     */
    public static ZipFile genZipFileFromStream(InputStream isZip) throws ZipException {
    
        return new ZipFile(StreamUtils.StreamToTempFile(isZip));
    
    }
    
    public static ZipFile genZipFile(String toZip){
        try {
            return new ZipFile(toZip);
        } catch (ZipException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从ZIP的压缩文件中，搜索指定文件名的文件路径
     * 
     * @param zipfile
     * @param filename
     * @return
     * @throws ZipException
     */
    public static String getFileNameFromZIP(ZipFile zipfile, String filename) throws ZipException {
        // Get the list of file headers from the zip file
        @SuppressWarnings("unchecked")
        List<FileHeader> fileHeaderList = zipfile.getFileHeaders();
    
        for (FileHeader fh : fileHeaderList) {
            if (fh.getFileName().contains(filename)) {
    
                return fh.getFileName();
            }
        }
    
        return "";
    
    }

    /**
     * 从zip文件中获取某个文件的stream
     * 
     * @param zipFileName
     * @param fileName
     * @return 返回zip中文件stream
     */
    public static ZipInputStream getFileStreamFromZip(String zipFileName, String fileName) {
        try {
            // Initiate the ZipFile
            ZipFile zipFile = new ZipFile(zipFileName);
            // String destinationPath = "c:\\ZipTest";
            return getFileStreamFromZip(zipFile, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public static ZipInputStream getFileStreamFromZip(ZipFile zipFile, String fileName) {
        try {
            // Get the FileHeader of the File you want to extract from the
            // zip file. Input for the below method is the name of the file
            // For example: 123.txt or abc/123.txt if the file 123.txt
            // is inside the directory abc
            FileHeader fileHeader = zipFile.getFileHeader(fileName);

            if (fileHeader != null) {

                // zipFile.extractFile(fileHeader, destPath);

                // Get the InputStream from the ZipFile
                return zipFile.getInputStream(fileHeader);

            } else {
                System.err.println("File does not exist-->" + fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 从zip流中，抽取文件，并返回stream
     * 
     * @param isZip
     * @param fileName
     * @return
     */
    public static ZipInputStream getFileStreamFromStream(InputStream isZip, String fileName) {
        // ZipInputStream zis = getFileStreamFromZip(zipFileName, fileName);
        File zFile = StreamUtils.StreamToTempFile(isZip);

        return getFileStreamFromZip(zFile.getPath(), fileName);

    }

    /**
     * 在ZIP文件中，查找并获取 内部zip文件对象
     * @param fromzipFile
     * @param fileName  内部zip文件，如：jar、zip...
     * @return
     */
    public static ZipFile getZipFileFromZIP(ZipFile fromzipFile, String fileName){
        InputStream isJar = null;
        try {
            isJar = getFileStreamFromZip(fromzipFile, getFileNameFromZIP(fromzipFile,fileName));
            return genZipFileFromStream(isJar);
        } catch (ZipException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (isJar != null)
                    isJar.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    
    }

    /**
     * 从zip文件中 copy指定文件，到另外一个zip文件的指定路径
     * 
     * @param fromZIP
     * @param fromFile
     * @param toZIP
     * @param toFile
     */
    public static void ZipCopyFile2Zip(String fromZIP, String fromFile, String toZIP, String toFile) {
 
        try {
            // Initiate the ZipFile
            ZipFile fromZipFile = new ZipFile(fromZIP);
            ZipCopyFile2Zip(fromZipFile, fromFile, toZIP, toFile);

        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
    public static void ZipCopyFile2Zip(ZipFile fromZipFile, String fromFile, String toZipFile, String toFile) {
       try {
            
            ZipFile zipFile = new ZipFile(toZipFile);
            
            ZipCopyFile2Zip(fromZipFile, fromFile, zipFile, toFile);


        } catch (Exception e) {
            e.printStackTrace();
        } 
       
    }
    public static int ZipCopyFile2Zip(ZipFile fromZipFile, String fromFile, ZipFile toZipFile, String toFile) {
        int retint=0;
        ZipInputStream is = null;

        try {

            // Get the FileHeader of the File you want to extract from the
            // zip file. Input for the below method is the name of the file
            // For example: 123.txt or abc/123.txt if the file 123.txt
            // is inside the directory abc
            FileHeader fileHeader = fromZipFile.getFileHeader(fromFile);

            if (fileHeader != null) {

                // Get the InputStream from the ZipFile
                is = fromZipFile.getInputStream(fileHeader);

                AddStreamToZip(toZipFile, is, toFile);
                
                retint++;

            }else{
                System.err.println("   !!未找到："+fromFile+"@"+fromZipFile.getFile().getName());
                retint=-1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            retint=-1;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return retint;
       
    }

    /**
     * 创建zip文件 使用文件列表
     * 
     * @param zipfileName
     * @param filesToAdd
     */
    public static void CreateZipWithFilelist(String zipfileName, ArrayList<File> filesToAdd) {

        // Input and OutputStreams are defined outside of the try/catch block
        // to use them in the finally block
        ZipOutputStream outputStream = null;
        InputStream inputStream = null;

        try {
            // Prepare the files to be added
            // ArrayList filesToAdd = new ArrayList();
            // filesToAdd.add(new File("c:\\ZipTest\\sample.txt"));
            // filesToAdd.add(new File("c:\\ZipTest\\myvideo.avi"));
            // filesToAdd.add(new File("c:\\ZipTest\\mysong.mp3"));

            // Initiate output stream with the path/file of the zip file
            // Please note that ZipOutputStream will overwrite zip file if it
            // already exists
            outputStream = new ZipOutputStream(new FileOutputStream(new File(zipfileName)));

            // Initiate Zip Parameters which define various properties such
            // as compression method, etc. More parameters are explained in
            // other
            // examples
            ZipParameters parameters = new ZipParameters();

            // Deflate compression or store(no compression) can be set below
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);

            // Set the compression level. This value has to be in between 0 to 9
            // Several predefined compression levels are available
            // DEFLATE_LEVEL_FASTEST - Lowest compression level but higher speed
            // of compression
            // DEFLATE_LEVEL_FAST - Low compression level but higher speed of
            // compression
            // DEFLATE_LEVEL_NORMAL - Optimal balance between compression
            // level/speed
            // DEFLATE_LEVEL_MAXIMUM - High compression level with a compromise
            // of speed
            // DEFLATE_LEVEL_ULTRA - Highest compression level but low speed
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

            // This flag defines if the files have to be encrypted.
            // If this flag is set to false, setEncryptionMethod, as described
            // below,
            // will be ignored and the files won't be encrypted
            parameters.setEncryptFiles(true);

            // Zip4j supports AES or Standard Zip Encryption (also called
            // ZipCrypto)
            // If you choose to use Standard Zip Encryption, please have a look
            // at example
            // as some additional steps need to be done while using
            // ZipOutputStreams with
            // Standard Zip Encryption
            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);

            // If AES encryption is used, this defines the key strength
            parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);

            // self descriptive
            parameters.setPassword("YourPassword");

            // Now we loop through each file and read this file with an
            // inputstream
            // and write it to the ZipOutputStream.
            for (int i = 0; i < filesToAdd.size(); i++) {
                File file = (File) filesToAdd.get(i);

                // This will initiate ZipOutputStream to include the file
                // with the input parameters
                outputStream.putNextEntry(file, parameters);

                // If this file is a directory, then no further processing is
                // required
                // and we close the entry (Please note that we do not close the
                // outputstream yet)
                if (file.isDirectory()) {
                    outputStream.closeEntry();
                    continue;
                }

                // Initialize inputstream
                inputStream = new FileInputStream(file);
                byte[] readBuff = new byte[4096];
                int readLen = -1;

                // Read the file content and write it to the OutputStream
                while ((readLen = inputStream.read(readBuff)) != -1) {
                    outputStream.write(readBuff, 0, readLen);
                }

                // Once the content of the file is copied, this entry to the zip
                // file
                // needs to be closed. ZipOutputStream updates necessary header
                // information
                // for this file in this step
                outputStream.closeEntry();

                inputStream.close();
            }

            // ZipOutputStream now writes zip header information to the zip file
            outputStream.finish();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 压缩文件
     * 
     * @param srcFile
     * @param destZIP
     * @param rootFolderInZip
     */
    public static void zipFile(String srcFile, String destZIP, String rootFolderInZip) {
        try {

            ZipFile zipFile = new ZipFile(destZIP);
            
            zipFile(srcFile,zipFile,rootFolderInZip);

        } catch (ZipException e) {
            e.printStackTrace();
        }

    }
    public static void zipFile(String srcFile, ZipFile zipFile, String rootFolderInZip) {
        try {

            ArrayList<File> filesToAdd = new ArrayList<File>();
            filesToAdd.add(new File(srcFile));
            // filesToAdd.add(new File("c:\\ZipTest\\myvideo.avi"));
            // filesToAdd.add(new File("c:\\ZipTest\\mysong.mp3"));

            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            parameters.setRootFolderInZip(rootFolderInZip);

            zipFile.addFiles(filesToAdd, parameters);

        } catch (ZipException e) {
            e.printStackTrace();
        }
        
    }

    /**
     * 显示zip文件信息
     * 
     * @param zipFileName
     */
    public static void InfoZipFile(String zipFileName) {
        final int javaCount[] = { 0 };
        final int fileCount[] = { 0 };

        scanZipFiles(zipFileName, new IHandleZipFile() {
            @Override
            public void handleFile(FileHeader fileHeader, ZipFile zipFile) {
                fileCount[0]++;
                if (fileHeader.getFileName().endsWith(".java")||fileHeader.getFileName().endsWith(".class"))
                    javaCount[0]++;

            }

        });

        System.out.println("\n>>>> ZIP 文件 " + zipFileName + " 共有压缩文件数：" + fileCount[0]);
        System.out.println("  >> 其中Java文件：" + javaCount[0] + "\n");

    }

    /**
     * 显示zip文件列表和zip文件信息
     * 
     * @param zipFileName
     */
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

        InfoZipFile(zipFileName);
    }

    /**
     * 扫描Zip中的文件的回调接口
     *
     */
    public interface IHandleZipFile {
        public void handleFile(FileHeader fileHeader, ZipFile zipFile);

    }

    /**
     * 扫描zip所有文件，并回调传入的接口
     * 
     * @param zipFileName
     * @param xhandleZipFile
     *            回调接口
     */
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

    public static List<FileHeader> searchZipFiles(ZipFile zipfile, String filename) throws ZipException {
        // Get the list of file headers from the zip file
        @SuppressWarnings("unchecked")
        List<FileHeader> fileHeaderList = zipfile.getFileHeaders();
        List<FileHeader> retList = new ArrayList<FileHeader>();
        for (FileHeader fh : fileHeaderList) {

            if (fh.getFileName().contains(filename)) {
                retList.add(fh);
            }
        }

        return retList;

    }
    
    public static void copyJavaToZip(String warFile, String toZip, String javafile, String jarName) {
        ZipFile zipfile = null;
        InputStream isJar = null;
        try {
            isJar = getFileStreamFromZip(warFile, getFileNameFromZIP(new ZipFile(warFile), jarName));
            zipfile = genZipFileFromStream(isJar);

            // java文件中可能会有子类，需要检查,并生成list
            String javaName = javafile.substring(0, javafile.lastIndexOf(".java"));
            List<FileHeader> listJavaFile = searchZipFiles(zipfile, javaName);

            for (FileHeader fileheader : listJavaFile) {
                InputStream isfile = zipfile.getInputStream(fileheader);

                AddStreamToZip(toZip, isfile, jarName + "/" + fileheader.getFileName());
                // "com.foresee.etax.bizfront/com/foresee/etax/bizfront/constant/EtaxBizFrontConstant.class"

                isfile.close();
            }

        } catch (ZipException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (isJar != null)
                    isJar.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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

        // ListAllFilesInZipFile("p:\\tmp\\e\\QGTG-YHCS.20150922-1100.zip");
        // InfoZipFile("P:\\workspace0.10\\20150921\\QGTG-YHCS.20155621
        // 1509.zip");

        // ZipCopyFile2Zip("E:/Open
        // Source/Java/zip4j_1.3.2/zip4j_examples_1.3.2.zip",
        // "Zip4jExamples/src/net/lingala/zip4j/examples/zip/CreateSplitZipFile.java",
        // "p:/a/a.zip",
        // "examples/zip/CreateSplitZipFile.java");

        // 打开war包

        // InputStream iswar =
        // getFileStreamFromZip("d:/tmp/gt3nf-wsbs-2.0.23100.156.04013.00-5261.war"
        // ,"WEB-INF/lib/com.foresee.etax.bizfront.jar");
        //
        // InputStream isfile =
        // getFileStreamFromStream(iswar,"com/foresee/etax/bizfront/constant/EtaxBizFrontConstant.class");
        //
        //
        // AddStreamToZip("p:/zz.zip", isfile,
        // "com.foresee.etax.bizfront/com/foresee/etax/bizfront/constant/EtaxBizFrontConstant.class");
        copyJavaToZip("d:/tmp/gt3nf-wsbs-2.0.23100.156.04013.00-5261.war", "p:/zzz.zip",
                "com/foresee/etax/bizfront/constant/EtaxBizFrontConstant.java", "com.foresee.etax.bizfront");
    }

}
