/**
 * 
 */
package com.foresee.xdeploy.file.tozip;

import com.foresee.test.loadrunner.lrapi4j.lr;
import com.foresee.test.util.io.FileUtil;
import com.foresee.xdeploy.file.FilesListItem;
import com.foresee.xdeploy.file.ToZipFile;
import com.foresee.xdeploy.file.WarFile;
import com.foresee.xdeploy.file.rule.ExchangePath;
import com.foresee.xdeploy.utils.zip.Zip4jUtils;

/**
 * Svn 文件导出到ZIP
 * @author allan
 *
 */
public class SvnToZip extends BaseToZip {

	@Override
	public int handleToZip(final FilesListItem oitem, WarFile warfile, final ToZipFile tozipfile) {
        return scanPackages(oitem.getProjs(), tozipfile, new IHandlePackage() {

            @Override
            public int handlePackage(ToZipFile self, String pak) {
                return exportSvnToZip(oitem,tozipfile);
            }
        });
        
        
	}
	
    public int exportSvnToZip(FilesListItem sf,ToZipFile tozipfile){
        int retint = 0;
        ExchangePath expath = sf.getExchange();
        // 同时抽取java源文件加入到zip中（直接从svn获取）
        String tmpFilePath = expath.getToTempFilePath();
        // pv.tempPath + "/" + expath.getFileName();

        try {
        	tozipfile.SvnRepo.Export(sf, tmpFilePath);

            // svnclient.svnExport(expath.getSvnURL(), sf.getVer(),
            // tmpFilePath, pv.keyRootFolder);
            // 将文件添加到zip文件
            Zip4jUtils.zipFile(tmpFilePath, tozipfile.toZipFile, lr.eval_string(expath.getToZipFolderPath()));

            retint++;
            FileUtil.delFile(tmpFilePath);
        } catch (Exception e) {
            e.printStackTrace();
            retint--;
        }
        return retint;

    }


	
}
