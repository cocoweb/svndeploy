package com.foresee.xdeploy.file;

import java.io.File;
import java.util.Collection;

import com.foresee.test.util.io.File2Util;

/**
 * @author Allan
 *
 * Folder  -->WARs  -->WAR  -->Jars  -->Jar
 *
 */
public class WarFiles{
    public String folderPath ="";
    public String folderFilter="";
    Collection<File> clFiles=null;
    
    public WarFiles(String sFolderPath,String sFilter){
        folderPath = sFolderPath;
        folderFilter=sFilter;
        
        // 遍历文件夹，并过滤
        clFiles = File2Util.getAllFiles(sFolderPath, sFilter);

    }
    
    public String getWar(String sWarName){
        WarFile warfile = getWarFile(sWarName);
        return warfile.getPath();
    }
    
    public WarFile getWarFile(String sWarName){
        if(sWarName.contains(",")) sWarName=sWarName.split(",")[0];
        
        for (File xfile : clFiles) {
            if (xfile.getName().contains(sWarName))
            {
                WarFile wf = new WarFile(xfile) ;
                wf.setWarName(sWarName);
                return wf;
            }

        }
        return null;
    	
    }
}
