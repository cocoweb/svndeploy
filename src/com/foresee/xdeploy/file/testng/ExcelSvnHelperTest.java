
package com.foresee.xdeploy.file.testng;

import java.io.File;
import java.util.Iterator;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.foresee.xdeploy.file.ExcelFiles;
import com.foresee.xdeploy.file.ExcelListHelper;
import com.foresee.xdeploy.file.FilesList;
import com.foresee.xdeploy.file.FilesListItem;
import com.foresee.xdeploy.file.PropValue;

public class ExcelSvnHelperTest {
    ExcelListHelper efh;
    PropValue pv = null;

    @BeforeClass
    public void beforeClass() {
        pv = PropValue.getInstance("/svntools.properties");
        efh = new ExcelListHelper();

    }

    @Test
    public void loadSvnFilesList() {

        System.out.println(efh.loadSvnFilesList(new File("p:/因开发所致环境变更记录表模版-20150823-杜英恒-产品线.xls")));
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
        FilesList sf = efh.loadFilesList(new ExcelFiles(pv));
        
        Iterator<FilesListItem> it = sf.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().getPath());
        }


    }


}

