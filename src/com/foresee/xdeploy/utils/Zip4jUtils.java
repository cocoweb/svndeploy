package com.foresee.xdeploy.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.foresee.test.util.io.FileUtil;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.unzip.UnzipUtil;
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

	private final int BUFF_SIZE = 4096;

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

	public static void AddStreamToZip(String zipFileName, InputStream isFile, String inzipName, String rootPath) {
		if (isFile == null)
			return;

		try {
			// Initiate ZipFile object with the path/name of the zip file.
			// Zip file may not necessarily exist. If zip file exists, then
			// all these files are added to the zip file. If zip file does not
			// exist, then a new zip file is created with the files mentioned
			ZipFile zipFile = new ZipFile(zipFileName);

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

			if (!rootPath.isEmpty())
				parameters.setRootFolderInZip(rootPath);

			// For this example I use a FileInputStream but in practise this can
			// be
			// any inputstream
			// is = new FileInputStream(fileName);

			// Creates a new entry in the zip file and adds the content to the
			// zip file
			zipFile.addStream(isFile, parameters);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void AddStreamToZip(String zipFileName, String fileName) {

		InputStream is = null;

		try {

			// For this example I use a FileInputStream but in practise this can
			// be
			// any inputstream
			is = new FileInputStream(fileName);

			AddStreamToZip(zipFileName, is, PathUtils.getFileNameWithExt(fileName), "");

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

	public static void ZipCopyFile2Zip(String fromZIP, String fromFile, String toZIP, String toFile) {
		ZipInputStream is = null;

		try {
			// Initiate the ZipFile
			ZipFile fromZipFile = new ZipFile(fromZIP);

			// Get the FileHeader of the File you want to extract from the
			// zip file. Input for the below method is the name of the file
			// For example: 123.txt or abc/123.txt if the file 123.txt
			// is inside the directory abc
			FileHeader fileHeader = fromZipFile.getFileHeader(fromFile);

			if (fileHeader != null) {

				// Get the InputStream from the ZipFile
				is = fromZipFile.getInputStream(fileHeader);

				AddStreamToZip(toZIP, is, PathUtils.getFileNameWithExt(fromFile), PathUtils.getPath(toFile));

			}
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

	public static void InfoZipFile(String zipFileName) {
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
		System.out.println("  >> 其中Java文件：" + javaCount[0] + "\n");

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

		// ListAllFilesInZipFile("p:\\tmp\\QGTG-YHCS.20153121 1009.zip");
		// InfoZipFile("p:\\tmp\\QGTG-YHCS.20153121 1009.zip");

		ZipCopyFile2Zip("E:/Open Source/Java/zip4j_1.3.2/zip4j_examples_1.3.2.zip",
				"Zip4jExamples/src/net/lingala/zip4j/examples/zip/CreateSplitZipFile.java", "p:/a/a.zip", "examples/zip/CreateSplitZipFile.java");

	}

}
