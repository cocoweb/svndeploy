package com.foresee.xdeploy.file.tozip;

import com.foresee.xdeploy.file.FilesListItem;
import com.foresee.xdeploy.file.ToZipFile;
import com.foresee.xdeploy.file.WarFile;
import com.foresee.xdeploy.file.WarFiles;

public abstract class BaseAction {

	protected FilesListItem ofilelistitem = null;
	protected ToZipFile otozipfile = null;
	protected WarFile owarfile = null;
	protected WarFiles owarlist=null;
	protected ToZipStrategy strategy;
	
	//构造函数，要你使用哪个妙计
	protected BaseAction(ToZipStrategy strategy) {
		super();
		this.strategy = strategy;
	}


	protected BaseAction init(FilesListItem oitem, WarFile warfile, ToZipFile tozipfile) {
		ofilelistitem = oitem;
		otozipfile    = tozipfile;
		owarfile      = warfile;
		
		return this;
	}

	/**
	 * 执行Tozip策略
	 * @return
	 */
	public int operate() {
		return strategy.handleToZip(ofilelistitem, owarfile, otozipfile);
	}

	public int operateList() {
		return strategy.handleWarList(ofilelistitem, owarlist, otozipfile);
		
	}

}