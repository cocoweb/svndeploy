package com.foresee.xdeploy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import com.foresee.test.util.exfile.ExtProperties;
import com.foresee.test.util.io.FileUtil;
import com.foresee.test.util.lang.StringUtil;
import com.foresee.xdeploy.utils.PathUtils;

public class PropValue {

    private static ExtProperties xprop = null;

    // private static ExtProperties extProp = null;

    public String excelfile = "";
    public String excelFolder = "";
    public String excelfiletemplate = "";
    public String svnurl = "";
    public String svntofolder = "";
    public String keyRootFolder = "";
    public String filekeyroot = "";
    public String propFileName = "";
    public String excelFolderFilter = "";
    public String scanOption = ""; // 清单文件选项 默认BATCH为file.excel.folder目录下的批量，
    private Map<String, String> pkgmap = null;

    public PropValue(String strFileName) {
        propFileName = strFileName;
        initProp();
    }

    private void initProp() {
        xprop = getExtPropertiesInstance(propFileName);
        svnurl = getProperty("svn.url");
        svntofolder = getProperty("svn.tofolder");
        keyRootFolder = getProperty("svn.keyroot");

        excelfile = getProperty("file.excel");
        excelFolder = getProperty("file.excel.folder");
        excelFolderFilter = getProperty("file.excel.filter");
        filekeyroot = getProperty("file.keyroot");
        excelfiletemplate = getProperty("file.excel.template");

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

    public String getProperty(String key) {
        return StringUtil.trim( xprop.getProperty(key));
    }

    /**
     * 按照配置   * 转换路径
     * @param srcPath
     * @return
     */
    public String exchangePath(String srcPath){
	    for(String akey:pkgmap.keySet()){
	        //分离源路径 和 目标路径
	        String[] apath = StringUtil.split(pkgmap.get(akey), "|");
	        if(srcPath.contains(apath[0])){   //如果路径中包含了“源路径”
	            //return PathUtils.autoPathRoot(srcPath, xKeyRoot, string2)
	            return PathUtils.addFolderEnd(apath[1])
	                    +PathUtils.trimFolderStart(
	                            srcPath.substring(srcPath.indexOf(apath[0])+apath[0].length()));
	        }
	    }
	    
	    return PathUtils.trimFolderStart(srcPath);
	}

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        PropValue pv = new PropValue("/svntools.properties");
        System.out.println(pv.pkgmap);
        
        System.out.println(pv.exchangePath("/trunk/engineering/src/gt3nf/web/gt3nf-skin/WebContent/etax/script/module/sbzs/init/sbInit_fqdqdzcpcljjsb.js"));
        
         System.out.println(pv.exchangePath("/trunk/engineering/src/gt3nf/web/gt3nf-wsbs/WebContent/forms/TAX_910610010066.txt"));
       

    }

}
