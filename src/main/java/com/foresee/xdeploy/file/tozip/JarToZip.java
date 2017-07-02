/**
 * 
 */
package com.foresee.xdeploy.file.tozip;

import java.io.InputStream;
import java.util.List;

import com.foresee.xdeploy.file.FilesListItem;
import com.foresee.xdeploy.file.ToZipFile;
import com.foresee.xdeploy.file.WarFile;
import com.foresee.xdeploy.utils.zip.Zip4jUtils;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.FileHeader;

/**
 * // 从jar抽取class、xml // java文件 或者 在mapping.j中找到的文件
 * 
 * // 同时抽取java源文件加入到zip中
 * 
 * @author allan
 *
 */
public class JarToZip extends BaseToZip {

	@Override
	public int handleToZip(FilesListItem oitem, WarFile warfile, ToZipFile tozipfile) {
		int retint = 0;
		String javafile = oitem.getExchange().FromPath;
		String jarName = oitem.getExchange().JARName;

		try {
			ZipFile jarfile = warfile.getJarZipFile(jarName);

			// java文件中可能会有子类(如 aaaa$bbb.class)，需要检查,并生成list
			String javaName = javafile.substring(0, javafile.lastIndexOf("."));

			List<FileHeader> listJavaFile = Zip4jUtils.searchZipFiles(jarfile, javaName);
			if (listJavaFile.size() < 1) {
				return retint - 1;
			}

			for (FileHeader fileheader : listJavaFile) {
				InputStream isfile = jarfile.getInputStream(fileheader);

				Zip4jUtils.AddStreamToZip(tozipfile.toZipFile, isfile, jarName + "/" + fileheader.getFileName());
				// "com.foresee.etax.bizfront/com/foresee/etax/bizfront/constant/EtaxBizFrontConstant.class"

				retint++;

				isfile.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
			retint--;
		}

		return retint;
	}

}
