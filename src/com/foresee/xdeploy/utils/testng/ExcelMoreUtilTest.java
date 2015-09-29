package com.foresee.xdeploy.utils.testng;

import java.io.IOException;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;

import com.foresee.xdeploy.utils.ExcelMoreUtil;

public class ExcelMoreUtilTest {
  @BeforeClass
  public void beforeClass() {
  }

  @AfterClass
  public void afterClass() {
  }


  @Test
  public void scanExcelData() {
      try {
        ExcelMoreUtil.scanExcelData("p:/tmp/xls/因开发所致环境变更记录表模版-20150925产品线-合并.xls"
                  ,"功能清单",null);
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
  }
}
