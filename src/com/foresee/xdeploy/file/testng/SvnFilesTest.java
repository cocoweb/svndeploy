package com.foresee.xdeploy.file.testng;

import java.io.File;
import java.util.Iterator;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.foresee.xdeploy.file.ExcelListHelper;
import com.foresee.xdeploy.file.FilesListItem;
import com.foresee.xdeploy.file.FilesList;

public class SvnFilesTest {
    ExcelListHelper efh;

    @BeforeClass
    public void beforeClass() {
        efh = new ExcelListHelper();
    }

    @Test
    public void iterator() {
        FilesList sflist = efh.loadFilesList(new File("p:/因开发所致环境变更记录表模版-20150823-杜英恒-产品线.xls"));
        Iterator<FilesListItem> it = sflist.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().getPath());
        }
        System.out.println();

        for (FilesListItem sf : sflist) {
            System.out.print(sf);
        }
    }
}
