package com.foresee.xdeploy.file.tozip;

import com.foresee.xdeploy.file.FilesListItem;
import com.foresee.xdeploy.file.ToZipFile;
import com.foresee.xdeploy.file.WarFile;

public class NothingToZip extends BaseToZip  {

    @Override
    public int handleToZip(FilesListItem oitem, WarFile warfile, ToZipFile tozipfile) {
        // TODO Auto-generated method stub
        //啥都不干
        
        return -1;
    }

}
