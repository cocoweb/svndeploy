package com.foresee.xdeploy.file.testng;

import java.io.File;
import java.util.Iterator;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.foresee.xdeploy.file.ExcelSvnHelper;
import com.foresee.xdeploy.file.SvnFile;
import com.foresee.xdeploy.file.SvnFiles;

public class SvnFilesTest {
    ExcelSvnHelper efh;

    @BeforeClass
    public void beforeClass() {
        efh = new ExcelSvnHelper();
    }

    @Test
    public void iterator() {
        SvnFiles sflist = efh.loadSvnFiles(new File("p:/因开发所致环境变更记录表模版-20150922-产品线-合并.xls"));
        Iterator<SvnFile> it = sflist.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().getPath());
        }
        System.out.println();

        for (SvnFile sf : sflist) {
            System.out.print(sf);
        }
    }
}
