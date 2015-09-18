package com.foresee.xdeploy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import com.foresee.test.util.exfile.ExtProperties;
import com.foresee.test.util.io.FileUtil;

public class PropValue {

	private static ExtProperties xprop = null;
	
    //private static ExtProperties extProp = null;

	public String excelfile = "";
	public String excelFolder = "";
	public String excelfiletemplate = "";
	public String svnurl = "";
	public String svntofolder = "";
	public String keyRootFolder = "";
	public String filekeyroot = "";
	public String propFileName = "";
	public String excelFolderFilter="";
	public String scanOption = ""; // 清单文件选项 默认BATCH为file.excel.folder目录下的批量，
	Map<String,String> pkgmap=null;
	
    public PropValue(String strFileName) {
    	 propFileName = strFileName;
    	 initProp();
    }

	private void initProp() {
        xprop = getExtPropertiesInstance(propFileName);
        svnurl = xprop.getProperty("svn.url");
        svntofolder = xprop.getProperty("svn.tofolder");
        keyRootFolder = xprop.getProperty("svn.keyroot");
    
        excelfile = xprop.getProperty("file.excel");
        excelFolder = xprop.getProperty("file.excel.folder");
        excelFolderFilter = xprop.getProperty("file.excel.filter");
        filekeyroot = xprop.getProperty("file.keyroot");
        excelfiletemplate=xprop.getProperty("file.excel.template");
        
        pkgmap = xprop.getSectionItems("mapping");
        
       // xprop.
        
    }
	
    public static ExtProperties getExtPropertiesInstance(String propFile) {
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
     * @param path
     */
    private static void initxProperties(String path) {
        if (xprop == null) {
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

                xprop = new ExtProperties();
                xprop.load(new InputStreamReader(in, "UTF-8"));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

	public String getProperty(String key) {
		return xprop.getProperty(key);
	}

}
