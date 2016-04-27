package com.foresee.xdeploy.file.testng;

import org.testng.annotations.Test;

import com.foresee.xdeploy.file.ExchangePath;
import com.foresee.xdeploy.file.PropValue;

import org.testng.annotations.BeforeClass;

public class ExchangePathTest {
    PropValue pv = null;
  @BeforeClass
  public void beforeClass() {
      pv = PropValue.getInstance("/svntools.properties");
  }


  @Test
  public void exchange() {
     
    try {
//        xx = ExchangePath.exchange("/trunk/engineering/src/gt3nf/web/gt3nf-skin/WebContent/etax/script/attachment/attachment.js");
//      System.out.println(xx);
//      
//      System.out.println(xx.getToZipFolderPath("service"));
//      System.out.println(ExchangePath.exchange("/trunk/engineering/src/gt3nf/web/gt3nf-wsbs/WebContent/forms/TAX_910610010066.txt"));
//      System.out.println(ExchangePath.exchange("trunk/engineering/src/portal/web/gt3nf-admin/src/META-INF/conf/properties/ajaxUpload.filetype.properties"));
      System.out.println(ExchangePath.exchange("/trunk/engineering/src/tycx/web/tycx-service/WebContent/WEB-INF/web.xml"));
      System.out.println(ExchangePath.exchange("/trunk/engineering/src/tycx/java/com.foresee.tycx.service/ivy.xml"));
    } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
      
  }
  
  @Test
  public void getToZipFolderPath(){
      try {
        ExchangePath ep =ExchangePath.exchange("/engineering/src/repository/com.foresee/public/repository/asm/asm/jars/asm-1.5.3.jar");
        
        System.out.println(ep);
        
        System.out.println(ep.getToZipFolderPath("ddd"));
    } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
      
      
  }
}
