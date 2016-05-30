package com.foresee.xdeploy.file.tozip;

import com.foresee.xdeploy.file.FilesListItem;
import com.foresee.xdeploy.file.ToZipFile;
import com.foresee.xdeploy.file.WarFile;
import com.foresee.xdeploy.file.WarFiles;

public interface ToZipStrategy {
    /**
     * 策略方法
     * @param oitem 
     */
    public int handleToZip(FilesListItem oitem,WarFile warfile, ToZipFile tozipfile);
    public int handleWarList(final FilesListItem oitem, final WarFiles warlist, ToZipFile tozipfile);
}
