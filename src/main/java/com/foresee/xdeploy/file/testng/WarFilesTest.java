package com.foresee.xdeploy.file.testng;

import org.testng.annotations.Test;

import com.foresee.xdeploy.file.PropValue;
import com.foresee.xdeploy.file.WarFiles;

import org.testng.annotations.BeforeClass;

public class WarFilesTest {
    WarFiles warlist = null;
    PropValue pv = null;

    @BeforeClass
    public void beforeClass() {
        pv = PropValue.getInstance("/svntools.properties");

        // war包的清单
        warlist = new WarFiles(pv.getProperty("zip.folder"), pv.getProperty("zip.folder.filter"));
    }

    @Test
    public void toStringtest() {
        System.out.println("Loading.." + warlist);
    }
}
