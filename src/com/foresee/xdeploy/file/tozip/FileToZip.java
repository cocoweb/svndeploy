/**
 * 
 */
package com.foresee.xdeploy.file.tozip;

import java.io.File;

import com.foresee.xdeploy.file.FilesListItem;
import com.foresee.xdeploy.file.ToZipFile;
import com.foresee.xdeploy.file.WarFile;

import com.foresee.xdeploy.utils.zip.Zip4jUtils;

/**
 * Svn 文件导出到ZIP
 * @author allan
 *
 */
public class FileToZip extends BaseToZip {

	@Override
	public int handleToZip(final FilesListItem oitem, WarFile warfile, final ToZipFile tozipfile) {
        return scanPackages(oitem.getProjs(), tozipfile, new IHandlePackage() {

            @Override
            public int handlePackage(ToZipFile self, String pak) {
                if (new File(oitem.getExchange().getToFilePath()).exists())
                    Zip4jUtils.zipFile(oitem.getExchange().getToFilePath(), self.toZipPath, oitem.getExchange().getToZipFolderPath(pak));// ??
                return 0;
                //return tozipfile.exportSvnToZip(oitem,pak);
            }

        }

        );
	}

	
}
