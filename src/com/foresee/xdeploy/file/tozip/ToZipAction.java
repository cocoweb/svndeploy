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
public class ToZipAction{
	private FilesListItem ofilelistitem=null;
	private ToZipFile otozipfile=null;
	private WarFile owarfile=null;
	
	private  WarFiles owarlist=null;
	private ToZipStrategy strategy;
	
	//构造函数，要你使用哪个妙计
	protected ToZipAction(ToZipStrategy strategy){
		this.strategy = strategy;
	}
	protected void init(FilesListItem oitem, WarFile warfile, ToZipFile tozipfile){
		ofilelistitem = oitem;
		otozipfile    = tozipfile;
		owarfile      = warfile;
	}
	
	public static ToZipAction Create(FilesListItem oitem,WarFile warfile, ToZipFile tozipfile,ToZipStrategy strategy){
		ToZipAction ret =   new ToZipAction( strategy);
		ret.init( oitem, warfile,tozipfile);
		
		return ret;
		
	}
	
	/**
	 * 创建文件 压缩到 ZIP的策略对象
	 * @param oitem
	 * @param tozipfile
	 * @return
	 */
	public static ToZipAction Create4File(FilesListItem oitem, ToZipFile tozipfile){
		return Create(oitem,null,tozipfile,new FileToZip());
	}
	
	/**
	 * 创建SVN 文件压缩到ZIP的策略对象
	 * @param oitem
	 * @param tozipfile
	 * @return
	 */
	public static ToZipAction Create4Svn(FilesListItem oitem, ToZipFile tozipfile){		
		return Create(oitem,null,tozipfile,new SvnToZip());
		
	}
	
	/**
	 * 创建WAR文件内容 输出到ZIP的策略对象
	 * @param oitem
	 * @param warfile
	 * @param tozipfile
	 * @return
	 */
	public static ToZipAction Create4War(FilesListItem oitem, WarFile warfile, ToZipFile tozipfile){
		return Create(oitem,warfile,tozipfile,new WarCompositeToZip());		
		
	}
	
	/**
	 * 创建Warlist 多个war文件内容，输出到ZIP的策略对象
	 * @param oitem
	 * @param warlist
	 * @param tozipfile
	 * @return
	 */
	public static ToZipAction Create4WarList(FilesListItem oitem, WarFiles warlist, ToZipFile tozipfile){
		ToZipAction ret =  Create(oitem,null,tozipfile,new WarCompositeToZip());
		ret.owarlist = warlist;
		return ret;
	}
	
	/**
	 * 执行Tozip策略
	 * @return
	 */
	public int operate(){
		return strategy.handleToZip(ofilelistitem, owarfile, otozipfile);
	}
	
	public int operateList(){
		return strategy.handleWarList(ofilelistitem, owarlist, otozipfile);
		
	}

	

}
