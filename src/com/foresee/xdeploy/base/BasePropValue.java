package com.foresee.xdeploy.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import com.foresee.test.util.exfile.ExtProperties;
import com.foresee.test.util.io.FileUtil;

public abstract class BasePropValue {

    private static ExtProperties xprop = null;
    public String propFileName = "";

    public BasePropValue(String strFileName) {
        super();
        propFileName = strFileName;
        xprop = getExtPropertiesInstance(propFileName);
    }
    

    protected static ExtProperties getExtPropertiesInstance(String propFile) {
        // synchronized表示同时只能一个线程进行实例化
        if (xprop == null) { // 如果两个进程同时进入时，同时创建很多实例，不符合单例
            synchronized (ExtProperties.class) {
                if (xprop == null) {
                    initxProperties(propFile);
                }
            }
        }
        return xprop;
    }

    /**
     * 读取配置文件
     * 
     * @param path
     */
    private static void initxProperties(String path) {
        if (xprop == null) {
            try {
                // 从用来加载类的搜索路径打开具有指定名称的资源，以读取该资源。此方法通过系统类加载器
                // InputStream in
                // =Thread.defaultThread().getContextClassLoader().getResourceAsStream(path);
    
                File xfile = FileUtil.lookupFileInClasspath(path);
    
                InputStream in = new FileInputStream(xfile.getAbsolutePath());
                // FileDefinition.class.getResourceAsStream(path);
                if (in == null) {
                    System.out.println("未找到 " + xfile.getAbsolutePath() + "配置文件！");
                }
                // 转换下，避免中文乱码
                System.out.println("==Load " + xfile.getAbsolutePath() + "文件！success");
    
                xprop = new ExtProperties();
                xprop.load(new InputStreamReader(in, "UTF-8"));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    
    }
    
    public static ExtProperties getExProp() {
        return xprop;
    }

    public String getProperty(String key) {
        return xprop.getProperty(key);
    }

    public Map<String, String> getSectionItems(String sectionName) {
        return xprop.getSectionItems(sectionName);
    }

}