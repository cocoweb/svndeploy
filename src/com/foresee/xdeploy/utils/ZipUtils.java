package com.foresee.xdeploy.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * ZIP压缩工具
 * 
 * @author <a href="mailto:zlex.dongliang@gmail.com">梁栋</a>
 * @since 1.0
 */
public class ZipUtils {

    public static final String EXT = ".zip";
    private static final String BASE_DIR = "";

    // 符号"/"用来作为目录标识判断符
    private static final String PATH = "/";
    private static final int BUFFER = 1024;

    /**
     * 压缩
     * 
     * @param srcFile
     * @throws Exception
     */
    public static void compress(File srcFile) throws Exception {
        String name = srcFile.getName();
        String basePath = srcFile.getParent();
        String destPath = basePath + name + EXT;
        compress(srcFile, destPath);
    }

    /**
     * 压缩
     * 
     * @param srcFile
     *            源路径
     * @param destPath
     *            目标路径
     * @throws Exception
     */
    public static void compress(File srcFile, File destFile) throws Exception {

        // 对输出文件做CRC32校验
        CheckedOutputStream cos = new CheckedOutputStream(new FileOutputStream(destFile), new CRC32());

        ZipOutputStream zos = new ZipOutputStream(cos);

        compress(srcFile, zos, BASE_DIR);

        zos.flush();
        zos.close();
    }

    /**
     * 压缩文件
     * 
     * @param srcFile
     * @param destPath
     * @throws Exception
     */
    public static void compress(File srcFile, String destPath) throws Exception {
        compress(srcFile, new File(destPath));
    }

    public static void compress(File srcFile, String destPath, String basePath) throws Exception {
        // 对输出文件做CRC32校验
        CheckedOutputStream cos = new CheckedOutputStream(new FileOutputStream(new File(destPath)), new CRC32());

        ZipOutputStream zos = new ZipOutputStream(cos);

        compress(srcFile, zos, basePath);

        zos.flush();
        zos.close();
    }

    /**
     * 压缩
     * 
     * @param srcFile
     *            源路径
     * @param zos
     *            ZipOutputStream
     * @param basePath
     *            压缩包内相对路径
     * @throws Exception
     */
    private static void compress(File srcFile, ZipOutputStream zos, String basePath) throws Exception {
        if (srcFile.isDirectory()) {
            compressDir(srcFile, zos, basePath);
        } else {
            compressFile(srcFile, zos, basePath);
        }
    }

    /**
     * 压缩
     * 
     * @param srcPath
     * @throws Exception
     */
    public static void compress(String srcPath) throws Exception {
        File srcFile = new File(srcPath);

        compress(srcFile);
    }

    /**
     * 文件压缩
     * 
     * @param srcPath
     *            源文件路径
     * @param destPath
     *            目标文件路径
     * 
     */
    public static void compress(String srcPath, String destPath) throws Exception {
        File srcFile = new File(srcPath);

        compress(srcFile, destPath);
    }

    /**
     * 压缩目录
     * 
     * @param dir
     * @param zos
     * @param basePath
     * @throws Exception
     */
    private static void compressDir(File dir, ZipOutputStream zos, String basePath) throws Exception {

        File[] files = dir.listFiles();

        // 构建空目录
        if (files.length < 1) {
            ZipEntry entry = new ZipEntry(basePath + dir.getName() + PATH);

            zos.putNextEntry(entry);
            zos.closeEntry();
        }

        for (File file : files) {

            // 递归压缩
            compress(file, zos, basePath + dir.getName() + PATH);

        }
    }

    /**
     * 文件压缩
     * 
     * @param file
     *            待压缩文件
     * @param zos
     *            ZipOutputStream
     * @param dir
     *            压缩文件中的当前路径
     * @throws Exception
     */
    private static void compressFile(File file, ZipOutputStream zos, String dir) throws Exception {

        /**
         * 压缩包内文件名定义
         * 
         * <pre>
         * 如果有多级目录，那么这里就需要给出包含目录的文件名 
         * 如果用WinRAR打开压缩包，中文名将显示为乱码
         * </pre>
         */
        ZipEntry entry = new ZipEntry(dir + file.getName());

        zos.putNextEntry(entry);

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));

        int count;
        byte data[] = new byte[BUFFER];
        while ((count = bis.read(data, 0, BUFFER)) != -1) {
            zos.write(data, 0, count);
        }
        bis.close();

        zos.closeEntry();
    }

    /**
     * 设置缓冲值
     */
    // static final int BUFFER = 8192;

    private static final String ALGORITHM = "PBEWithMD5AndDES";

    public static void zip(String zipFileName, String inputFile, String pwd) throws Exception {
        zip(zipFileName, new File(inputFile), pwd);
    }

    /**
     * 功能描述：压缩指定路径下的所有文件
     * 
     * @param zipFileName
     *            压缩文件名(带有路径)
     * @param inputFile
     *            指定压缩文件夹
     * @return
     * @throws Exception
     */
    public static void zip(String zipFileName, String inputFile) throws Exception {
        zip(zipFileName, new File(inputFile), null);
    }

    /**
     * 功能描述：压缩文件对象
     * 
     * @param zipFileName
     *            压缩文件名(带有路径)
     * @param inputFile
     *            文件对象
     * @return
     * @throws Exception
     */
    public static void zip(String zipFileName, File inputFile, String pwd) throws Exception {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
        zip(out, inputFile, "", pwd);
        out.close();
    }

    /**
     * 
     * @param out
     *            压缩输出流对象
     * @param file
     * @param base
     * @throws Exception
     */
    public static void zip(ZipOutputStream outputStream, File file, String base, String pwd) throws Exception {
        if (file.isDirectory()) {
            File[] fl = file.listFiles();
            outputStream.putNextEntry(new ZipEntry(base + "/"));
            base = base.length() == 0 ? "" : base + "/";
            for (int i = 0; i < fl.length; i++) {
                zip(outputStream, fl[i], base + fl[i].getName(), pwd);
            }
        } else {
            outputStream.putNextEntry(new ZipEntry(base));
            FileInputStream inputStream = new FileInputStream(file);
            // 普通压缩文件
            if (pwd == null || pwd.trim().equals("")) {
                int b;
                while ((b = inputStream.read()) != -1) {
                    outputStream.write(b);
                }
                inputStream.close();
            }
            // 给压缩文件加密
            else {
                PBEKeySpec keySpec = new PBEKeySpec(pwd.toCharArray());
                SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
                SecretKey passwordKey = keyFactory.generateSecret(keySpec);
                byte[] salt = new byte[8];
                Random rnd = new Random();
                rnd.nextBytes(salt);
                int iterations = 100;
                PBEParameterSpec parameterSpec = new PBEParameterSpec(salt, iterations);
                Cipher cipher = Cipher.getInstance(ALGORITHM);
                cipher.init(Cipher.ENCRYPT_MODE, passwordKey, parameterSpec);
                outputStream.write(salt);
                byte[] input = new byte[64];
                int bytesRead;
                while ((bytesRead = inputStream.read(input)) != -1) {
                    byte[] output = cipher.update(input, 0, bytesRead);
                    if (output != null) {
                        outputStream.write(output);
                    }
                }
                byte[] output = cipher.doFinal();
                if (output != null) {
                    outputStream.write(output);
                }
                inputStream.close();
                outputStream.flush();
                outputStream.close();
            }
        }
        file.delete();
    }

    public static void unzip(String zipFileName, String outputDirectory) throws Exception {
        ZipInputStream inputStream = new ZipInputStream(new FileInputStream(zipFileName));
        unzip(inputStream, outputDirectory, null);
    }

    /**
     * 功能描述：将压缩文件解压到指定的文件目录下
     * 
     * @param zipFileName
     *            压缩文件名称(带路径)
     * @param outputDirectory
     *            指定解压目录
     * @return
     * @throws Exception
     */
    public static void unzip(String zipFileName, String outputDirectory, String pwd) throws Exception {
        ZipInputStream inputStream = new ZipInputStream(new FileInputStream(zipFileName));
        unzip(inputStream, outputDirectory, pwd);
    }

    public static void unzip(File zipFile, String outputDirectory, String pwd) throws Exception {
        ZipInputStream inputStream = new ZipInputStream(new FileInputStream(zipFile));
        unzip(inputStream, outputDirectory, pwd);
    }

    public static void unzip(ZipInputStream inputStream, String outputDirectory, String pwd) throws Exception {
        ZipEntry zipEntry = null;
        FileOutputStream outputStream = null;
        try {
            while ((zipEntry = inputStream.getNextEntry()) != null) {
                if (zipEntry.isDirectory()) {
                    String name = zipEntry.getName();
                    name = name.substring(0, name.length() - 1);
                    File file = new File(outputDirectory + File.separator + name);
                    file.mkdir();
                } else {
                    File file = new File(outputDirectory + File.separator + zipEntry.getName());
                    file.createNewFile();
                    outputStream = new FileOutputStream(file);
                    // 普通解压缩文件
                    if (pwd == null || pwd.trim().equals("")) {
                        int b;
                        while ((b = inputStream.read()) != -1) {
                            outputStream.write(b);
                        }
                        outputStream.close();
                    }
                    // 解压缩加密文件
                    else {
                        PBEKeySpec keySpec = new PBEKeySpec(pwd.toCharArray());
                        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
                        SecretKey passwordKey = keyFactory.generateSecret(keySpec);
                        byte[] salt = new byte[8];
                        inputStream.read(salt);
                        int iterations = 100;
                        PBEParameterSpec parameterSpec = new PBEParameterSpec(salt, iterations);
                        Cipher cipher = Cipher.getInstance(ALGORITHM);
                        cipher.init(Cipher.DECRYPT_MODE, passwordKey, parameterSpec);
                        byte[] input = new byte[64];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(input)) != -1) {
                            byte[] output = cipher.update(input, 0, bytesRead);
                            if (output != null) {
                                outputStream.write(output);
                            }
                        }
                        byte[] output = cipher.doFinal();
                        if (output != null) {
                            outputStream.write(output);
                        }
                        outputStream.flush();
                        outputStream.close();
                    }
                }
            }
            inputStream.close();
        } catch (IOException ex) {
            throw new Exception("解压读取文件失败");
        } catch (Exception ex) {
            throw new Exception("解压文件密码不正确");
        } finally {
            inputStream.close();
            outputStream.flush();
            outputStream.close();
        }

    }
}