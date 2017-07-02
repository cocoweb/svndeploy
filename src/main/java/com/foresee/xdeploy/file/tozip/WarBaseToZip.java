package com.foresee.xdeploy.file.tozip;

import com.foresee.xdeploy.file.FilesListItem;
import com.foresee.xdeploy.file.ToZipFile;
import com.foresee.xdeploy.file.WarFile;
import com.foresee.xdeploy.utils.zip.Zip4jUtils;

public abstract class WarBaseToZip extends BaseToZip{
	protected String sPath ="";
	protected String dPath ="";

	@Override
	public int handleToZip(FilesListItem oitem, WarFile warfile, ToZipFile tozipfile) {
		if (sPath.isEmpty())    return -1;

		dPath = oitem.getExchange().getToZipPath(warfile.getWarName());
	
	    return Zip4jUtils.ZipCopyFile2Zip(warfile.warZipFile, sPath , tozipfile.toZipFile, dPath);
	}

}