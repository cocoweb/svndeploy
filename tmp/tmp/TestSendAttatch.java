package com.foresee.xdeploy.tmp;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.io.ZipOutputStream;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipModel;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.unzip.UnzipEngine;
import net.lingala.zip4j.util.Zip4jConstants;
import net.lingala.zip4j.zip.ZipEngine;

public class TestSendAttatch extends ZipEngine {
    private ZipModel zipModel;

    public TestSendAttatch(ZipModel zipModel) throws ZipException {
        super(zipModel);
        this.zipModel = zipModel;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        TestSendAttatch att = null;
        try {
            att = new TestSendAttatch(new ZipModel());
            att.doZipEnc();
            System.out.println("成功");
        } catch (Exception e) {
            System.out.println("失败");
            e.printStackTrace();
        }
    }

    public void doZipEnc() {
        try {
            // 将压缩流写到内存
            ByteArrayOutputStream saos = new ByteArrayOutputStream();
            // 本地测试
            FileOutputStream f = new FileOutputStream("d:\\test1.zip", true);

            for (int i = 0; i < 1; i++) {
                String passwd = "111";
                ZipParameters parameters = new ZipParameters();
                parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE); // 压缩方式
                parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL); // 压缩级别
                parameters.setSourceExternalStream(true);
                parameters.setFileNameInZip("aaaa_" + i + ".xls");// 后缀可以更改的
                if (!"".equals(passwd)) {
                    parameters.setEncryptFiles(true);
                    parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD); // 加密方式
                    parameters.setPassword(passwd.toCharArray());
                }
                addNewStreamToZipTmp(saos, parameters);
            }
            saos.writeTo(f);
            f.close();
            saos.close();
            // 文件大小
            System.out.println("size:" + (saos.toByteArray().length / 1024));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addNewStreamToZipTmp(ByteArrayOutputStream baos, ZipParameters parameters) throws ZipException {

        ZipOutputStream outputStream = null;
        try {
            outputStream = new ZipOutputStream(baos, this.zipModel);
            if (zipModel.getEndCentralDirRecord() == null) {
                throw new ZipException("invalid end of central directory record");
            }
            checkParameters(parameters);
            outputStream.putNextEntry(null, parameters);
            if (!parameters.getFileNameInZip().endsWith("/") && !parameters.getFileNameInZip().endsWith("\\")) {
                for (int i = 0; i < 10; i++) {
                    outputStream.write("ccccccccccccccccccccc".getBytes());
                    outputStream.flush();
                }
            }
            outputStream.closeEntry();
            outputStream.finish();
        } catch (ZipException e) {
            throw e;
        } catch (Exception e) {
            throw new ZipException(e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {

                }
            }
        }
    }
    
    public void getFileFromZipStream(ZipInputStream zis, String filename) throws ZipException{
        FileHeader fileHeader = null;
        
        UnzipEngine unzipEngine = new UnzipEngine(zipModel, fileHeader);
        unzipEngine.getInputStream();
    }

    private void checkParameters(ZipParameters parameters) throws ZipException {

        if (parameters == null) {
            throw new ZipException("cannot validate zip parameters");
        }

        if ((parameters.getCompressionMethod() != Zip4jConstants.COMP_STORE)
                && parameters.getCompressionMethod() != Zip4jConstants.COMP_DEFLATE) {
            throw new ZipException("unsupported compression type");
        }

        if (parameters.getCompressionMethod() == Zip4jConstants.COMP_DEFLATE) {
            if (parameters.getCompressionLevel() < 0 && parameters.getCompressionLevel() > 9) {
                throw new ZipException(
                        "invalid compression level. compression level dor deflate should be in the range of 0-9");
            }
        }

        if (parameters.isEncryptFiles()) {
            if (parameters.getEncryptionMethod() != Zip4jConstants.ENC_METHOD_STANDARD
                    && parameters.getEncryptionMethod() != Zip4jConstants.ENC_METHOD_AES) {
                throw new ZipException("unsupported encryption method");
            }

            if (parameters.getPassword() == null || parameters.getPassword().length <= 0) {
                throw new ZipException("input password is empty or null");
            }
        } else {
            parameters.setAesKeyStrength(-1);
            parameters.setEncryptionMethod(-1);
        }
    }

}