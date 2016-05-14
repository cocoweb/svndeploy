package com.foresee.xdeploy.utils;

public class StringUtil{
	/**
	 * 转换数字串，去除其中的非数字字符
	 * @param str
	 * @return
	 */
	public static String toNumericString(String str) {
	
		return filterCharToString(str,new ICheck(){

			@Override
			public boolean check(char c) {
				
				return Character.isDigit(c);
			}
			
		});
	}
	
	public interface ICheck  {
		
		public boolean check(char c);
	
	}

	/**
	 * 转换数字串，去除其中不满足 check条件的字符
	 * @param str
	 * @param chk
	 * @return
	 */
	public static String filterCharToString(String str ,ICheck  chk){
		if (str == null) {
			return null;
		}
	
		int length = str.length();
		StringBuilder builder = new StringBuilder();
	
		for (int i = 0; i < length; i++) {
			if (chk.check( str.charAt(i))) {
				builder.append(str.charAt(i));
			}
		}
	
		return builder.toString();
	}

}
