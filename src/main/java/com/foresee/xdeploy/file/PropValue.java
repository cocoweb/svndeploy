package com.foresee.xdeploy.file;

import java.util.Arrays;
import java.util.List;

import com.foresee.xdeploy.file.rule.ExchangePath;
import com.foresee.xdeploy.utils.base.ParamPropValue;

public class PropValue extends ParamPropValue  {

    // private static ExtProperties extProp = null;

    public String workspace = "";
    public String tempPath = "";

    public String excelfile = "";
    public String excelFolder = "";
    public String excelfiletemplate = "";
    public String svnurl = "";
    public String svntofolder = "";
    
    
    public String svnkeyRoot = "";
    public String filekeyroot = "";
    public String svnworkspacekeyRoot = "";
    
    
    public String excelFolderFilter = "";
    public String scanOption = ""; // 清单文件选项 默认BATCH为file.excel.folder目录下的批量，
    
    public List<String> pkgList = null;

    protected PropValue(String strFileName) {
        super(strFileName);
        initProp();
    }
    
    private static PropValue SingletonPV=null;
    
    public static synchronized PropValue getInstance(String strFileName){
        if (SingletonPV==null){
            SingletonPV = new PropValue(strFileName);
        }
        
        return SingletonPV;
    }
    
    public static synchronized PropValue getInstance(){
        return getInstance("/svntools.properties");
    }

   
    protected void initProp() {

        workspace = getProperty("workspace");
        tempPath = getProperty("temppath");

        svnurl = getProperty("svn.url");
        svntofolder = getProperty("svn.tofolder");
        svnkeyRoot = getProperty("svn.keyroot");

        excelfile = getProperty("file.excel");
        excelFolder = getProperty("file.excel.folder");
        excelFolderFilter = getProperty("file.excel.filter");
        filekeyroot = getProperty("file.keyroot");
        excelfiletemplate = getProperty("file.excel.template");
        
        svnworkspacekeyRoot=getProperty("svn.workspace.keyroot");
        
        pkgList = Arrays.asList(getProperty("package.list").split(","));     

        ExchangePath.InitExchangePath(this);   //初始化路径转换器

    }


    public static void main(String[] args) {
       PropValue         pv = PropValue.getInstance("/svntools.properties");

       // System.out.println(pv.pkgmap);

//        System.out.println(
//                pv.exchangePath("/trunk/engineering/src/gt3nf/web/gt3nf-skin/WebContent/etax/script/module/sbzs/init/sbInit_fqdqdzcpcljjsb.js"));
//
//        System.out.println(pv.exchangeWarPath("/trunk/engineering/src/gt3nf/web/gt3nf-wsbs/WebContent/forms/TAX_910610010066.txt"));
//        System.out.println(pv.exchangePath("/trunk/engineering/src/tax/java/com.foresee.tax.service/src/com/foresee/tax/service/gt3/bigdata/constants/DsjclRwConstant.java"));

        System.out.println(pv.excelFolder);
        System.out.println(pv.tempPath);
        
        System.out.println(pv.getProperty("zip.tofolder"));
        
        ExchangePath aa;
        try {
            aa = ExchangePath.exchange("/trunk/engineering/src/gt3nf/java/gov.chinatax.gt3nf/src/gov/chinatax/gt3nf/sb/dkdjdsdjbg/entry/impl/DkdjdsdjSbService.java");
            System.out.println(aa);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        

    }

}
