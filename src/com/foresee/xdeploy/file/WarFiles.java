package com.foresee.xdeploy.file;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.foresee.test.util.io.File2Util;

/**
 * @author Allan
 *
 *         Folder -->WARs -->WAR -->Jars -->Jar
 *
 */
public class WarFiles {
    public String folderPath = "";
    public String folderFilter = "";
    Collection<File> clFiles = null;
    
    Map<String,WarFile> mapWar = new HashMap<String,WarFile>();

    public WarFiles(String sFolderPath, String sFilter) {
        folderPath = sFolderPath;
        folderFilter = sFilter;

        // 遍历文件夹，并过滤
        clFiles = File2Util.getAllFiles(sFolderPath, sFilter);

    }

    public String getWar(String sWarName) {
        WarFile warfile = getWarFile(sWarName);
        return warfile.getPath();
    }

    public WarFile getWarFile(String sWarName) {
        if (sWarName.contains(","))
            sWarName = sWarName.split(",")[0];

        for (File xfile : clFiles) {
            if (xfile.getName().contains(sWarName)) {
                
                return getWarFileInstance(xfile, sWarName);
            }

        }
        return null;

    }
    
    public WarFile getWarFileInstance(File ofile,String sWarName){
        WarFile wf =null;
        if (mapWar.containsKey(ofile.getName())){
            wf =mapWar.get(ofile.getName());
        }else{
            wf = new WarFile(ofile);
            wf.setWarName(sWarName);
            
            mapWar.put(ofile.getName(), wf);
           
        }
        return wf;
        
    }
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder retstr = new StringBuilder();
        retstr.append("WarFiles :[");
        for (File xfile : clFiles) {

            retstr.append("\n        " + xfile.getPath());
        }

        return  retstr.toString()+"]" ;
    }
}
