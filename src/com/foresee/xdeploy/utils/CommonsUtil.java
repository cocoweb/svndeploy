package com.foresee.xdeploy.utils;

import java.io.UnsupportedEncodingException;

public class CommonsUtil{
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
	
	private static String UTFSpace ;
	static {
		try {
			byte ubytes[] = {(byte) 0xC2,(byte) 0xA0};
			UTFSpace= new String(ubytes,"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 处理UTF8的变态空格 ，转换为20空格
	 * @param targetStr
	 * @return
	 */
	public static String ChangeUTF8Space(String targetStr)
    {

		return targetStr.replaceAll(UTFSpace, " ");
        
//        try
//        {
//            String currentStr = String.Empty;
//            byte[] utf8Space = new byte[] { 0xc2, 0xa0 };
//            String tempSpace = Encoding.GetEncoding("UTF-8").GetString(utf8Space);
//            currentStr = targetStr.Replace(tempSpace, " ");
//            return currentStr;
//        }
//        catch (Exception ex)
//        {
//            return targetStr;
//        }
    }

}
