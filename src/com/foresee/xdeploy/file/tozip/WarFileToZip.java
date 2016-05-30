/**
 * 
 */
package com.foresee.xdeploy.file.tozip;

import com.foresee.xdeploy.file.FilesListItem;
import com.foresee.xdeploy.file.ToZipFile;
import com.foresee.xdeploy.file.WarFile;

/**
 * 处理war文件，压缩到zip
 * @author allan
 *
 */
public class WarFileToZip extends WarBaseToZip {

	@Override
	public int handleToZip(FilesListItem oitem, WarFile warfile, ToZipFile tozipfile) {
	    sPath = oitem.getExchange().SrcPath;
		return super.handleToZip(oitem, warfile, tozipfile);
	}

	

}
