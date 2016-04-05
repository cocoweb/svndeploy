package com.foresee.xdeploy.testng;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.foresee.test.util.lang.StringUtil;
import com.foresee.xdeploy.ListToFileHelper;

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
}
