package com.foresee.xdeploy.testng;

import java.util.Properties;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.foresee.test.util.lang.StringUtil;
import com.foresee.xdeploy.ListToFileHelper;
import com.foresee.xdeploy.file.PropValue;

public class ListToFileHelperTest {
    ListToFileHelper listTofileHelper;
    @BeforeClass
    public void beforeClass() {
         listTofileHelper = new ListToFileHelper();
    }


    @Test
    public void tt() {
        String spack = "gt3nf-wsbs、gt3nf-service";
        String[] pakages=StringUtil.split(spack,",、，");
        
        System.out.println(pakages[0]+"||"+pakages[1]);

    }

    @Test
    public void scanPrintList() {
        listTofileHelper.scanPrintList();
    }

    @Test
    public void scanSvnToPath() {
        listTofileHelper.scanSvnToPath();
    }
    
    @Test
    public void svnDiffToPath() {
    	listTofileHelper.svnDiffToPath();
    	
    	
    }
    
    @Test
    public void svnDiffToExcel() {
        
        
        Properties prop =new Properties();
        prop.setProperty("svndiff.url", "https://svn.foresee.com.cn/svn/taxcp/trunk/engineering/src");
        prop.setProperty("svndiff.startversion", "5700");
        prop.setProperty("svndiff.endversion", "5800");
        prop.setProperty("svn.username", "xieying@foresee.cn");
        prop.setProperty("svn.password", "xieying,1");
        
        PropValue.setArgsProp(prop);
        
        listTofileHelper.svnDiffToList();
    }
}
