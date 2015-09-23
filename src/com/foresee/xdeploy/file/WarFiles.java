package com.foresee.xdeploy.file;

import java.io.File;
import java.util.Collection;

import com.foresee.test.util.io.File2Util;

public class WarFiles{
    public String folderPath =null;
    Collection<File> clFiles=null;
    
    public WarFiles(String sFolderPath,String sFilter){
        folderPath = sFolderPath;
        // 遍历文件夹，并过滤
        clFiles = File2Util.getAllFiles(sFolderPath, sFilter);

    }
    
    public String getWar(String sProject){
        for (File xfile : clFiles) {
            if (xfile.getName().contains(sProject))
                return xfile.getPath() ;

        }
        return "";
    }
}
