package com.foresee.xdeploy.file.tozip;

import com.foresee.test.loadrunner.lrapi4j.lr;
import com.foresee.xdeploy.file.FilesListItem;
import com.foresee.xdeploy.file.ToZipFile;
import com.foresee.xdeploy.file.WarFiles;
import com.foresee.xdeploy.file.base.XdeployBase;

public abstract class BaseToZip implements ToZipStrategy {

	/**
	 * 处理package包的接口
	 * 
	 */
	public interface IHandlePackage {
	    public int handlePackage(ToZipFile self,String pak);
	}



	/**
	 * 扫描package字符串
	 * @param sProj
	 * @param handlepackage
	 * @return
	 */
	public int scanPackages(String[] Projs,ToZipFile tozipfile,IHandlePackage handlepackage){
	    int ret=0;
	    
	    for (String pak : Projs) {
	        // web工程参数保存
	        lr.save_string(pak, XdeployBase.LIST_Project);
	        
	        ret = handlepackage.handlePackage(tozipfile,pak);
	    }
	    return ret;
	    
	}



    @Override
	public int handleWarList(FilesListItem oitem, WarFiles warlist, ToZipFile tozipfile) {
		// do nothing
		return 0;
	}
}
