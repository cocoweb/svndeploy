package com.foresee.xdeploy.file.testng;

import java.io.File;
import java.util.Iterator;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.foresee.xdeploy.file.ExcelListHelper;
import com.foresee.xdeploy.file.FilesListItem;
import com.foresee.xdeploy.file.PropValue;
import com.foresee.xdeploy.file.FilesList;

public class FilesListTest {
    ExcelListHelper efh;
    PropValue pv = null;

    @BeforeClass
    public void beforeClass() {
        pv = PropValue.getInstance("/svntools.properties");
        efh = new ExcelListHelper();
    }

    @Test
    public void iterator() {
        FilesList sflist = efh.loadFilesList(new File("E:/tmp/workspace/xls/因开发所致环境变更记录表模版-20160331-杜英恒-涉税文书.xls"));
        Iterator<FilesListItem> it = sflist.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().getPath());
        }
        System.out.println();

        for (FilesListItem sf : sflist) {
            System.out.print(sf.getExchange()+"\n");
        }
    }
}
