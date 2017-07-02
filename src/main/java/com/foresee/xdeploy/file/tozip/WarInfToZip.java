package com.foresee.xdeploy.file.tozip;

import com.foresee.xdeploy.file.FilesListItem;
import com.foresee.xdeploy.file.ToZipFile;
import com.foresee.xdeploy.file.WarFile;

public class WarInfToZip extends WarBaseToZip  {

	@Override
	public int handleToZip(FilesListItem oitem, WarFile warfile, ToZipFile tozipfile) {
		 sPath =oitem.getExchange().getToZipPathNoRoot();
				
		 return super.handleToZip(oitem, warfile, tozipfile);
	}

}
