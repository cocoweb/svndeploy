package com.foresee.xdeploy.file.tozip;

import com.foresee.xdeploy.file.FilesListItem;
import com.foresee.xdeploy.file.ToZipFile;
import com.foresee.xdeploy.file.WarFile;
import com.foresee.xdeploy.file.WarFiles;
import com.foresee.xdeploy.file.rule.ExchangePath;
import com.foresee.xdeploy.file.rule.PackageType;


public class WarCompositeToZip extends BaseToZip {
	ExchangePath expath;

	/*
	 * 单个war文件处理 (non-Javadoc)
	 * 
	 * @see
	 * com.foresee.xdeploy.file.tozip.ToZipStrategy#handleToZip(com.foresee.
	 * xdeploy.file.FilesListItem, com.foresee.xdeploy.file.WarFile,
	 * com.foresee.xdeploy.file.ToZipFile)
	 */
	@Override
	public int handleToZip(FilesListItem oitem, WarFile warfile, ToZipFile tozipfile) {

		int fileCount = 0;
		if (warfile != null) {
			fileCount += copyToZip(oitem, warfile, tozipfile);

			if (expath.isJava()) {
				// 同时抽取java源文件加入到zip中（直接从svn获取）
				fileCount += new SvnToZip().exportSvnToZip(oitem, tozipfile);
				// tozipfile.exportSvnToZip(oitem,"");

			}

		} else {
			System.err.println("   !!没能抽取  :" + expath.SrcPath + " @ " + oitem.getProj());
		}

		return fileCount;
	}

	/*
	 * WarList处理
	 * 
	 * @see com.foresee.xdeploy.file.tozip.BaseToZip#handleWarList(com.foresee.
	 * xdeploy.file.FilesListItem, com.foresee.xdeploy.file.WarFiles,
	 * com.foresee.xdeploy.file.ToZipFile)
	 */
	@Override
	public int handleWarList(final FilesListItem oitem, final WarFiles warlist, ToZipFile tozipfile) {
		expath = oitem.getExchange();
		int fileCount = 0;

		fileCount = scanPackages(oitem.getProjs(), tozipfile, new IHandlePackage() {

			@Override
			public int handlePackage(ToZipFile self, String pak) {
				// 判断清单中的工程名，是否包含在 war包中
				// 包含就抽取到目标路径
				WarFile warfile = warlist.getWarFile(pak);

				return handleToZip(oitem, warfile, self);
				// ToZipAction.Create4War(oitem, warfile, self).operate();

			}
		});

		return fileCount;

	}

	protected int copyToZip(FilesListItem oitem, WarFile warfile, ToZipFile tozipfile) {
		ToZipStrategy ret = getStrategy(oitem);

		int retint = ret.handleToZip(oitem, warfile, tozipfile);

		// PackageType expath=oitem.getExchange();

		if (retint >= 0) {
			System.out.println("     抽取 " + (oitem.isType(PackageType.Type_JAR) ? "Class" : "文件") + " : "
					+ expath.getToZipPath() + " @ " + warfile.getSource(oitem));
		} else
			System.err.println("    ！抽取失败  :" + expath + "\n  >>> @ " + warfile.getSource(oitem));
		return retint;
	}

	protected ToZipStrategy getStrategy(FilesListItem oitem) {
		ToZipStrategy ret = null;

		// 判断文件类型 war、jar、chg
		if (oitem.isType(PackageType.Type_JAR)) {
			// 从jar抽取class、xml
			// java文件 或者 在mapping.j中找到的文件

			// 同时抽取java源文件加入到zip中
			ret = new JarToZip();

		} else if (oitem.isType(PackageType.Type_WAR)) {
			if (expath.MappingKey.contains("META-INF")) { // w.META-INF 类型
				ret = new WarInfToZip();
			} else // w.类型
				ret = new WarFileToZip();
		}else if(oitem.isType(PackageType.Type_NON)){
		    ret = new NothingToZip();     //啥都不干对象

		} else {
			ret = new SvnToZip();

		}

		return ret;
	}

}
