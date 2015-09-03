package com.foresee.xdeploy.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class FileSystem {
    private ArrayList<String> fileList = new ArrayList<String>();
    
    public String pathName;

    public FileSystem(String path) {
        pathName = path;

        
    }

    private void print(String message) {
        System.out.println(message);
    }

    private int fileCount =0;
    public int listFiles(String strPath) {
        File dir = new File(strPath);
        if (dir != null && dir.exists()) {
            if (dir.isDirectory()) {
                File[] files;

                try {
                    files = dir.listFiles();
                } catch (SecurityException e) {
                    files = null;
                    e.printStackTrace();
                }

                if (files == null) {
                    return 0;
                } else {
                    for (int i = 0; i < files.length; i++) {
                        String strFileName = files[i].getAbsolutePath();
                        if (files[i].isDirectory()) {
                            this.print("D--:" + strFileName);
                            this.listFiles(files[i].getAbsolutePath());
                        } else {
                            fileCount++;
                            this.print("F--:" + strFileName);
                            fileList.add(files[i].getAbsolutePath());
                        }
                    }
                }
            } else {
                this.print("F--:" + dir.getAbsolutePath());
            }
        } else {
            this.print("FileNotExist:" + dir.getAbsolutePath());
        }
        
        return fileCount;
    }

    private boolean checkDir(File dir) {

        if (dir == null) {
            this.print("dirPath is null");
            return false;
        } else if (!dir.exists()) {
            this.print("dirPath: " + dir.getAbsolutePath() + " doesn't exist.");
            return false;
        } else if (!dir.isDirectory()) {
            this.print("dirPath: " + dir.getAbsolutePath() + " is not a directory.");
            return false;
        } else {
            return true;
        }
    }

    /**
     * 类似与windows操作系统的xCopy，递归拷贝整个源目录到目标目录。源目录和目标目录必须已经存在。
     *
     * @param srcDirPath
     * @param destDirPath
     */
    public void xCopy(String srcDirPath, String destDirPath) {
        File srcDir = new File(srcDirPath);
        File destDir = new File(destDirPath);
        if (this.checkDir(srcDir) && this.checkDir(destDir)) {
            File[] files;

            try {
                files = srcDir.listFiles();
            } catch (SecurityException e) {
                files = null;
                this.print("xCopy breaked: can't listFiles,may be caused by:");
                e.printStackTrace();
                return;
            }

            if (files == null) {
                return;
            } else {
                for (int i = 0; i < files.length; i++) {
                    String fileName = files[i].getName();
                    String absoluteFileName = files[i].getAbsolutePath();

                    if (files[i].isDirectory()) {
                        // 下一次递归的源目录
                        String subSrcDir = srcDir.getPath() + File.separator + fileName;

                        // 下一次递归的目的目录
                        String subDestDir = destDir.getPath() + File.separator + fileName;

                        try {
                            new File(subDestDir).mkdir();
                        } catch (SecurityException e) {
                            this.print("can't mkdir in path : " + subDestDir);
                            this.print("xCopy breaked cause by: ");
                            e.printStackTrace();
                            return;
                        }

                        xCopy(subSrcDir, subDestDir);
                    } else {

                        String destFileName = destDirPath + File.separator + fileName;
                        copyFile(absoluteFileName, destFileName);
                    }
                }
            }
        }
    }

    /**
     * 简单复制单个文件到目标路径。目标路径下的该文件必须有可写权限
     *
     * @param srcFilePath
     * @param desFilePath
     */
    public void copyFile(String srcFilePath, String desFilePath) {
        int byteread = 0;
        InputStream in = null;
        FileOutputStream out = null;

        try {
            in = new FileInputStream(srcFilePath);
            out = new FileOutputStream(desFilePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        byte[] buffer = new byte[1024];

        try {
            while ((byteread = in.read(buffer)) != -1) {
                out.write(buffer, 0, byteread);
            }

            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        FileSystem fs ;
        if (args.length == 1) {
            fs = new FileSystem(args[0]);
        } else {
            fs = new FileSystem("p:/tmp");
        }
        long a = System.currentTimeMillis();
        fs.listFiles(fs.pathName);
        fs.print("total File Count="+ Integer.toString(fs.fileCount) +"; TimeCost:" + (System.currentTimeMillis() - a) + " Millis");

        
       // fs.xCopy(fs.pathName, "p:\\temp");
    }
}