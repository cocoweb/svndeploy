package com.foresee.xdeploy.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.foresee.test.util.exfile.ExtProperties;
import com.foresee.test.util.io.FileUtil;

public class PropFile {
    private static ExtProperties extProp = null;

    public PropFile() {
        // TODO Auto-generated constructor stub
    }

    // 静态方法访问时，直接访问不需要实例化
    public static ExtProperties getExtPropertiesInstance() {

        return getExtPropertiesInstance("/svntools.properties");
    }
    public static ExtProperties getExtPropertiesInstance(String propFile) {
        // synchronized表示同时只能一个线程进行实例化
        if (extProp == null) { // 如果两个进程同时进入时，同时创建很多实例，不符合单例
            synchronized (ExtProperties.class) {
                if (extProp == null) {
                    initxProperties(propFile);
                }
            }
        }
        return extProp;
    }
    
    /**
     * 读取配置文件
     * @param path
     */
    private static void initxProperties(String path) {
        if (extProp == null) {
            try {
                // 从用来加载类的搜索路径打开具有指定名称的资源，以读取该资源。此方法通过系统类加载器
                // InputStream in
                // =Thread.defaultThread().getContextClassLoader().getResourceAsStream(path);
                
                File xfile = FileUtil.lookupFileInClasspath(path);

                
                InputStream in =new FileInputStream(xfile.getAbsolutePath());
                        //FileDefinition.class.getResourceAsStream(path);
                if (in == null) {
                    System.out.println("未找到 " + xfile.getAbsolutePath() + "配置文件！");
                }
                // 转换下，避免中文乱码
                System.out.println("==Load " + xfile.getAbsolutePath() + "文件！success");

                extProp = new ExtProperties();
                extProp.load(new InputStreamReader(in, "UTF-8"));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }
}
