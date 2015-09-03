package com.foresee.xdeploy.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipFileUtils {

    public ZipFileUtils() {
        // TODO Auto-generated constructor stub
    }

    /** 创建一个压缩文件，from为文件夹路径，to为创建好后压缩文件路径 */
    public void CreateZip(String from, String to) throws IOException {
        List<File> list = getFiles(from);
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(new File(to)));
        for (File f : list) {
            InputStream in = new FileInputStream(f);
            String name = getRelName(from, f);

            ZipEntry en = new ZipEntry(new File(from).getName() + "/" + name);
            en.setSize(f.length());

            out.putNextEntry(en);
            out.setComment("中文测试");

            int len = 0;
            byte[] buffer = new byte[1024];
            while (-1 != (len = in.read(buffer))) {
                out.write(buffer, 0, len);
            }
            in.close();
        }
        out.close();
    }

    /** 获取文件的相对路径 */
    private String getRelName(String from, File f) {
        // TODO Auto-generated method stub
        String a = f.getAbsolutePath().replace(from + "\\", "");
        a = a.replace("\\", "/");
        System.out.println(from + "---" + a);
        return a;
    }

    /** 获取路径下所有文件，包括文件夹下的 */
    private List<File> getFiles(String sou) {
        List<File> list = new ArrayList<File>();
        File f = new File(sou);
        File files[] = f.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                list.add(file);
            } else {
                list.addAll(getFiles(file.getPath()));
            }
        }
        return list;
    }

    /** 第一个参数是压缩文件路径，第二个参数是要解压的文件夹路径，文件夹可以不存在会自动生成 */
    public static void ZipDecompress(String frompath, String topath) throws IOException {
        ZipFile zf = new ZipFile(new File(frompath));
        InputStream inputStream;
        Enumeration<? extends ZipEntry> en = zf.entries();
        while (en.hasMoreElements()) {
            ZipEntry zn = (ZipEntry) en.nextElement();
            if (!zn.isDirectory()) {
                inputStream = zf.getInputStream(zn);
                File f = new File(topath + zn.getName());
                File file = f.getParentFile();
                file.mkdirs();
                System.out.println(zn.getName() + "---" + zn.getSize());

                FileOutputStream outputStream = new FileOutputStream(topath + zn.getName());
                int len = 0;
                byte bufer[] = new byte[1024];
                while (-1 != (len = inputStream.read(bufer))) {
                    outputStream.write(bufer, 0, len);
                }
                outputStream.close();
            }
        }
    }
    public static void readZipFile(String file) throws Exception {  
        ZipFile zf = new ZipFile(file);  
        InputStream in = new BufferedInputStream(new FileInputStream(file));  
        ZipInputStream zin = new ZipInputStream(in);  
        ZipEntry ze;  
        while ((ze = zin.getNextEntry()) != null) {  
            if (ze.isDirectory()) {
            } else {  
                System.err.println("file - " + ze.getName() + " : "  
                        + ze.getSize() + " bytes");  
                long size = ze.getSize();  
                if (size > 0) {  
                    BufferedReader br = new BufferedReader(  
                            new InputStreamReader(zf.getInputStream(ze)));  
                    String line;  
                    while ((line = br.readLine()) != null) {  
                        System.out.println(line);  
                    }  
                    br.close();  
                }  
                System.out.println();  
            }  
        }  
        zin.closeEntry();  
    }  

    /**
     * 压缩文件-由于out要在递归调用外,所以封装一个方法用来 调用ZipFiles(ZipOutputStream out,String
     * path,File... srcFiles)
     * 
     * @param zip
     * @param path
     * @param srcFiles
     * @throws IOException
     * @author isea533
     */
    public static void ZipFiles(File zip, String path, File... srcFiles) throws IOException {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zip));
        ZipFiles(out, path, srcFiles);
        out.close();
        System.out.println("*****************压缩完毕*******************");
    }

    /**
     * 压缩文件-File
     * 
     * @param zipFile
     *            zip文件
     * @param srcFiles
     *            被压缩源文件
     * @author isea533
     */
    public static void ZipFiles(ZipOutputStream out, String path, File... srcFiles) {
        path = path.replaceAll("\\*", "/");
        if (!path.endsWith("/")) {
            path += "/";
        }
        byte[] buf = new byte[1024];
        try {
            for (int i = 0; i < srcFiles.length; i++) {
                if (srcFiles[i].isDirectory()) {
                    File[] files = srcFiles[i].listFiles();
                    String srcPath = srcFiles[i].getName();
                    srcPath = srcPath.replaceAll("\\*", "/");
                    if (!srcPath.endsWith("/")) {
                        srcPath += "/";
                    }
                    out.putNextEntry(new ZipEntry(path + srcPath));
                    ZipFiles(out, path + srcPath, files);
                } else {
                    FileInputStream in = new FileInputStream(srcFiles[i]);
                    System.out.println(path + srcFiles[i].getName());
                    out.putNextEntry(new ZipEntry(path + srcFiles[i].getName()));
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    out.closeEntry();
                    in.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解压到指定目录
     * 
     * @param zipPath
     * @param descDir
     * @author isea533
     */
    public static void unZipFiles(String zipPath, String descDir) throws IOException {
        unZipFiles(new File(zipPath), descDir);
    }

    /**
     * 解压文件到指定目录
     * 
     * @param zipFile
     * @param descDir
     * @author isea533
     */
    @SuppressWarnings("rawtypes")
    public static void unZipFiles(File zipFile, String descDir) throws IOException {
        File pathFile = new File(descDir);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        ZipFile zip = new ZipFile(zipFile);
        for (Enumeration entries = zip.entries(); entries.hasMoreElements();) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            String zipEntryName = entry.getName();
            InputStream in = zip.getInputStream(entry);
            String outPath = (descDir + zipEntryName).replaceAll("\\*", "/");
            ;
            // 判断路径是否存在,不存在则创建文件路径
            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
            if (!file.exists()) {
                file.mkdirs();
            }
            // 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
            if (new File(outPath).isDirectory()) {
                continue;
            }
            // 输出文件路径信息
            System.out.println(outPath);

            OutputStream out = new FileOutputStream(outPath);
            byte[] buf1 = new byte[1024];
            int len;
            while ((len = in.read(buf1)) > 0) {
                out.write(buf1, 0, len);
            }
            in.close();
            out.close();
        }
        System.out.println("******************解压完毕********************");
    }

    public static void zipFileRead(String file,String saveRootDirectory) {
        try {
            ZipFile zipFile = new ZipFile(file);
            @SuppressWarnings("unchecked")
            Enumeration<ZipEntry> enu = (Enumeration<ZipEntry>) zipFile.entries();
            while (enu.hasMoreElements()) {
                ZipEntry zipElement = (ZipEntry) enu.nextElement();
                InputStream read = zipFile.getInputStream(zipElement);
                String fileName = zipElement.getName();
                if (fileName != null && fileName.indexOf(".") != -1) {//是否为文件 （文件带有路径如：/images/a.jpg）
                    execute(zipElement,read,saveRootDirectory);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void execute(ZipEntry ze, InputStream read,String saveRootDirectory)
            throws FileNotFoundException, IOException {
        //如果只读取图片，自行判断就OK.
        String fileName = ze.getName();
//        if(fileName.lastIndexOf(".jpg")!= -1 || fileName.lastIndexOf(".bmp")!= -1 
//            || fileName.lastIndexOf(".jpeg")!= -1){//指定要解压出来的文件格式（这些格式可抽取放置在集合或String数组通过参数传递进来，方法更通用）
            File file = new File(saveRootDirectory + fileName);
            if (!file.exists()) {
                File rootDirectoryFile = new File(file.getParent());
                //创建目录
                if (!rootDirectoryFile.exists()) {
                    boolean ifSuccess = rootDirectoryFile.mkdirs();
                    if (ifSuccess) {
                        System.out.println("文件夹创建成功!");
                    } else {
                        System.out.println("文件创建失败!");
                    }
                }
                //创建文件
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //写入文件
            BufferedOutputStream write = new BufferedOutputStream(new FileOutputStream(file));
            int cha = 0;
            while ((cha = read.read()) != -1) {
                write.write(cha);
            }
            //要注意IO流关闭的先后顺序
            write.flush();
            write.close();
            read.close();
//        }
    }
    
    
    private static File checkFile(File xfile){
        
        if (!xfile.exists()) {
            File rootDirectoryFile = new File(xfile.getParent());
            //创建目录
            if (!rootDirectoryFile.exists()) {
                boolean ifSuccess = rootDirectoryFile.mkdirs();
                if (ifSuccess) {
                    System.out.println("文件夹创建成功!");
                } else {
                    System.out.println("文件创建失败!");
                }
            }
            //创建文件
            try {
                xfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return xfile;
    }
    
    /**
     * 抽取ZIP 文件内容
     */
    public static void getZipFile(String szipPath, String srcPath, String outPath) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        ZipInputStream zis = null;
        File zipFile = new File(szipPath);
        try {
            fis = new FileInputStream(zipFile);
            zis = new ZipInputStream(fis);
            ZipEntry zipEntry = null;
            //zipEntry.
            while((zipEntry = zis.getNextEntry()) != null){
                String name = zipEntry.getName().toLowerCase();
                if((name.endsWith("/" + srcPath) ) ||            //&& name.contains("drawable") && name.contains("res")
                        (name.endsWith( srcPath))){          // && name.contains("raw") && name.contains("res")
                    fos = new FileOutputStream(checkFile(new File(outPath)));
                    byte[] buffer = new byte[1024];
                    int n = 0;
                    while((n = zis.read(buffer, 0, buffer.length)) != -1){
                        fos.write(buffer, 0, n);
                    }
                    try {
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                }
            }
            fos = null;
            zis.closeEntry();
            zipEntry = null;
            //System.out.println("抽取成功"+outPath);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (zis != null) {
                    zis.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        //
        // ZipFile xzip = new ZipFile("d:/gt3nf-skin-2.0.0.3444.war");

        //ZipFileUtils.ZipDecompress(frompath, topath);
        // for(Iterator e: xzip.entries().){
        //
        // }
        
        //ZipFileUtils.readZipFile("d:/gt3nf-skin-2.0.0.3444.war");
        ZipFileUtils.getZipFile("d:/gt3nf-skin-2.0.0.3444.war", "www/style/images-swzj-01/atable-01/ico_fujian.gif", "p:/tmp/www/style/images-swzj-01/atable-01/ico_fujian.gif");
        

    }

}
