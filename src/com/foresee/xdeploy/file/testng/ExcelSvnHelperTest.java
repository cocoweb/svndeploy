package com.foresee.xdeploy.file.testng;

import java.io.File;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;

import com.foresee.xdeploy.file.ExcelSvnHelper;

public class ExcelSvnHelperTest {
    ExcelSvnHelper efh;
  @BeforeClass
  public void beforeClass() {
      efh = new ExcelSvnHelper();
  }
    
  @Test
  public void loadSvnFilesList() {
      
      System.out.println(efh.loadSvnFilesList(new File("p:/因开发所致环境变更记录表模版-20150922-产品线-合并.xls")));
  }

}
