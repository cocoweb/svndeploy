package com.foresee.xdeploy.testng;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.foresee.xdeploy.ListToFileHelper;

public class ListToFileHelperTest {
    ListToFileHelper listTofileHelper;
    @BeforeClass
    public void beforeClass() {
         listTofileHelper = new ListToFileHelper();
    }


    @Test
    public void displayList() {
      throw new RuntimeException("Test not implemented");
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
