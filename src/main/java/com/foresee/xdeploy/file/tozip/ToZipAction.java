package com.foresee.xdeploy.file.tozip;

import com.foresee.xdeploy.file.FilesListItem;
import com.foresee.xdeploy.file.ToZipFile;
import com.foresee.xdeploy.file.WarFile;
import com.foresee.xdeploy.file.WarFiles;

/**
 * 调用方法：
 *   ToZipAction.CreateXXXXX(sf, warlist, this).operateYYYYY();
 *   
 * @author allan
 *
 */
public class ToZipAction extends BaseAction{
	
	protected ToZipAction(ToZipStrategy strategy) {
		super(strategy);
		
	}
	public static BaseAction Create(FilesListItem oitem, WarFile warfile, ToZipFile tozipfile, ToZipStrategy strategy) {
		return    new ToZipAction( strategy).init( oitem, warfile,tozipfile);
		
	}

	/**
	 * 创建文件 压缩到 ZIP的策略对象
	 * @param oitem
	 * @param tozipfile
	 * @return
	 */
	public static BaseAction Create4File(FilesListItem oitem, ToZipFile tozipfile){
		return Create(oitem,null,tozipfile,new FileToZip());
	}
	
	/**
	 * 创建SVN 文件压缩到ZIP的策略对象
	 * @param oitem
	 * @param tozipfile
	 * @return
	 */
	public static BaseAction Create4Svn(FilesListItem oitem, ToZipFile tozipfile){		
		return Create(oitem,null,tozipfile,new SvnToZip());
		
	}
	
	/**
	 * 创建WAR文件内容 输出到ZIP的策略对象
	 * @param oitem
	 * @param warfile
	 * @param tozipfile
	 * @return
	 */
	public static BaseAction Create4War(FilesListItem oitem, WarFile warfile, ToZipFile tozipfile){
		return Create(oitem,warfile,tozipfile,new WarCompositeToZip());		
		
	}
	
	/**
	 * 创建Warlist 多个war文件内容，输出到ZIP的策略对象
	 * @param oitem
	 * @param warlist
	 * @param tozipfile
	 * @return
	 */
	public static BaseAction Create4WarList(FilesListItem oitem, WarFiles warlist, ToZipFile tozipfile){
		BaseAction ret =  Create(oitem,null,tozipfile,new WarCompositeToZip());
		ret.owarlist = warlist;
		return ret;
	}

	

}
