package com.foresee.xdeploy.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtils {
    static final int BUFF_SIZE = 4096;

    public StreamUtils() {
        // TODO Auto-generated constructor stub
    }
    
    public static void InputStreamToOutputStream(InputStream is,OutputStream os) throws IOException{
        int readLen = -1;
        byte[] buff = new byte[BUFF_SIZE];

        // Loop until End of File and write the contents to the output
        // stream
        while ((readLen = is.read(buff)) != -1) {
            os.write(buff, 0, readLen);
        }
        
        
    }

    public static void writeInputStreamToFile(InputStream is, File outFile) {
        OutputStream os = null;

        try {
            // Check if the directories(including parent directories)
            // in the output file path exists
            File parentDir = outFile.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs(); // If not create those directories
            }

            // Initialize the output stream
            os = new FileOutputStream(outFile);
            
            InputStreamToOutputStream(is,os);



        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            // Close output stream
            try {
                os.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }

    }

    public static File CreateTempFile() {
        // 创建临时文件
        File temp = null;
        try {
            temp = File.createTempFile("tmpjar", ".zip");
            // 在程序退出时删除临时文件
            temp.deleteOnExit();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return temp;

    }

    public static File StreamToTempFile(InputStream iswar) {
        File tmpfile = CreateTempFile();
        writeInputStreamToFile(iswar, tmpfile);

        return tmpfile;

    }

}
