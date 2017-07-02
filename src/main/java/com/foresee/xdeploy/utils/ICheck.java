package com.foresee.xdeploy.utils;

/**
 * 作为条件检查接口
 * 
 * @author allan
 *
 */
public interface ICheck<T>  {
	
	public boolean check(T c);

}