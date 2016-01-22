
package com.foresee.xdeploy.file.testng;

import java.io.File;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.foresee.xdeploy.ListToFileHelper;
import com.foresee.xdeploy.file.ExcelFiles;
import com.foresee.xdeploy.file.ExcelSvnHelper;
import com.foresee.xdeploy.file.PropValue;
import com.foresee.xdeploy.file.SvnFiles;

public class ExcelSvnHelperTest {
    ExcelSvnHelper efh;
    PropValue pv = null;

    @BeforeClass
    public void beforeClass() {
        pv = new PropValue("/svntools.properties");
        efh = new ExcelSvnHelper();

    }

    @Test
    public void loadSvnFilesList() {

        System.out.println(efh.loadSvnFilesList(new File("p:/因开发所致环境变更记录表模版-20150922-产品线-合并.xls")));
    } 

    @Test
    public void loadSvnFilesList1() {
        System.out.println("===========显示待处理文件清单=================");

        String sTofile = ""; // 默认为""，不用合并excel

        // if (pv.getProperty("file.excel.merge").equals("true")) { //
        // 判断是否需要合并excel
        // // 生成excel输出文件名
        // sTofile = pv.genOutExcelFileName();
        // // 生成合并的excel文件
        // FileUtil.Copy(pv.excelfiletemplate, sTofile);
        // }

        // 扫描并获取全部excel内容
        //ScanIncrementFiles scanFiles = ScanIncrementFiles.scanListfile(pv.excelfile, pv.excelFolder, pv.scanOption, pv.excelFolderFilter, sTofile);
        SvnFiles sf = efh.loadSvnFiles(new ExcelFiles(pv));
        
        //ListToFileHelper.displayList(sf);

    }


}

