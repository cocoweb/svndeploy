package com.foresee.xdeploy.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import com.foresee.test.loadrunner.lrapi4j.lr;
import com.foresee.test.util.exfile.ExtProperties;
import com.foresee.test.util.io.FileUtil;
import com.foresee.test.util.lang.DateUtil;
import com.foresee.test.util.lang.StringUtil;
import com.foresee.xdeploy.utils.PathUtils;

public class PropValue {

    private static ExtProperties xprop = null;
    
    public static Properties argsProp =null;

    // private static ExtProperties extProp = null;

    public String workspace = "";
    public String tempPath = "";

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
    public Map<String, String> pkgmap = null;

    public PropValue(String strFileName) {
        propFileName = strFileName;
        initProp();
        ExchangePath.InitExchangePath(this);   //初始化路径转换器
    }

    private void savePara(ExtProperties extprop) {

        Iterator<Object> iter = extprop.keySet().iterator();

        while (iter.hasNext()) {
            String skey = StringUtil.trim(iter.next().toString());
            lr.save_string(StringUtil.trim(extprop.getProperty(skey)), skey);
        }

    }

    private void initProp() {
        xprop = getExtPropertiesInstance(propFileName);
        savePara(xprop);

        workspace = getProperty("workspace");
        tempPath = getProperty("temppath");

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

    private static ExtProperties getExtPropertiesInstance(String propFile) {
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
        String sValue = "";
        if (argsProp!=null)
            sValue = argsProp.getProperty(key,"");
        
        if (sValue.isEmpty())
           sValue = lr.eval_string(StringUtil.trim(xprop.getProperty(key)));
        
         return sValue;
    }


    
    private static String outexcelfilename="";
    public String genOutExcelFileName(){
        if(outexcelfilename==""){
            outexcelfilename= excelfiletemplate.substring(0,  excelfiletemplate.indexOf(".")) 
                + "-" + DateUtil.getCurrentDate("yyyyMMdd")
                + "-产品线-合并.xls";
        }
        return outexcelfilename;
    }
    
    private static String outzipfilename ="";
    
    public String genOutZipFileName(){
        if(outzipfilename==""){
       // return PathUtils.addFolderEnd(pv.getProperty("zip.tofolder")) + "QGTG-YHCS." + DateUtil.getCurrentDate("yyyyMMdd-HHmm") + ".zip";
            outzipfilename= PathUtils.addFolderEnd( getProperty("zip.tofolder")) 
                + "QGTG-YHCS." + DateUtil.getCurrentDate("yyyyMMdd-HHmm")
                + ".zip";
        }
        return outzipfilename;
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        PropValue pv = new PropValue("/svntools.properties");
        System.out.println(pv.pkgmap);

//        System.out.println(
//                pv.exchangePath("/trunk/engineering/src/gt3nf/web/gt3nf-skin/WebContent/etax/script/module/sbzs/init/sbInit_fqdqdzcpcljjsb.js"));
//
//        System.out.println(pv.exchangeWarPath("/trunk/engineering/src/gt3nf/web/gt3nf-wsbs/WebContent/forms/TAX_910610010066.txt"));
//        System.out.println(pv.exchangePath("/trunk/engineering/src/tax/java/com.foresee.tax.service/src/com/foresee/tax/service/gt3/bigdata/constants/DsjclRwConstant.java"));

        System.out.println(pv.excelFolder);
        
        ExchangePath aa = ExchangePath.exchange("/trunk/engineering/src/gt3nf/java/gov.chinatax.gt3nf/src/gov/chinatax/gt3nf/sb/dkdjdsdjbg/entry/impl/DkdjdsdjSbService.java");
        
        System.out.println(aa);

    }

}
