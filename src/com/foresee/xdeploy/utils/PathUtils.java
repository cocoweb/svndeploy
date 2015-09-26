package com.foresee.xdeploy.utils;

import com.foresee.test.util.lang.StringUtil;

public class PathUtils {

	public PathUtils() {
		// TODO Auto-generated constructor stub
	}

	// /**
	// * 根据URL、文件路径，自动生成目标目录
	// *
	// * @param xUrl
	// * @param xPath
	// * @param xkey
	// * @return
	// */
	// public static String autoUrlRoot(String xUrl, String xPath, String xKey)
	// {
	//
	// return StringUtil.trimEnd(StringUtil.replaceAll(xPath, "\\", "/"), "/")
	// + xUrl.substring(xUrl.indexOf(xKey) - 1);
	// }

	/**
	 * 根据URL，Path头，返回本地绝对路径
	 * 
	 * @param xUrl
	 * @param xPath
	 * @param xKey
	 * @return StringUtil.trimEnd(StringUtil.replaceAll(citoFolder,
	 *         /\", "/"), "/") + sPath.substring(sPath.indexOf(keyRootFolder) -
	 *         1);
	 * 
	 */
	public static String autoUrlToPath(String xUrl, String xPath, String xKey) {

		// xPath = StringUtil.trimEnd(StringUtil.replaceAll(xPath, "\\", "/"),
		// "/");
		// 去掉绝对路径路径尾部 /, 替换路径中的\ 为 /

		return trimFolderEnd(xPath) + autoPathRoot(xUrl, xKey);
		// + xUrl.substring(xUrl.indexOf(xKey) - 1);
	}

	/**
	 * 根据指定root目录, 去掉路径头, 返回相对路径
	 * 
	 * @param xPath
	 * @param xKeyRoot
	 * @return
	 */
	public static String autoPathRoot(String xPath, String xKeyRoot) {
		if (xPath.indexOf(xKeyRoot) >= 0) {
			return addFolderStart(xPath.substring(xPath.indexOf(xKeyRoot)));
		} else {
			System.out.println("keyroot 配置错误，" + xKeyRoot + "未包含在URL中: " + xPath);
			return xPath;
		}

		// return StringUtil.trimEnd(StringUtil.replaceAll(xPath, "\\", "/"),
		// "/")
		// + xUrl.substring(xUrl.indexOf(xKey) - 1);
	}

	/**
	 * 根据指定root目录, 去掉路径头, 返回相对路径 并且不包含root 和 /
	 * 
	 * @param xPath
	 * @param xKeyRoot
	 * @param string2
	 * @return
	 */
	public static String autoPathRoot(String xPath, String xKeyRoot, String string2) {
		if (xPath.indexOf(xKeyRoot) >= 0) {
			return trimFolderStart(xPath.substring(xPath.indexOf(xKeyRoot) + xKeyRoot.length()));
		} else {
			System.out.println("keyroot 配置错误，" + xKeyRoot + "未包含在URL中: " + xPath);
			return xPath;
		}
	}

	/**
	 * 去掉路径头部 /, 替换路径中的\ 为 /
	 * 
	 * @param xPath
	 * @return
	 */
	public static String trimFolderStart(String xPath) {
		return StringUtil.trimStart(StringUtil.replaceAll(xPath, "\\", "/"), "/");

	}

	/**
	 * 去掉路径尾部 /, 替换路径中的\ 为 /
	 * 
	 * @param xPath
	 * @return
	 */
	public static String trimFolderEnd(String xPath) {
		return StringUtil.trimEnd(StringUtil.replaceAll(xPath, "\\", "/"), "/");

	}

	/**
	 * 添加路径头部 /, 替换路径中的\ 为 /
	 * 
	 * @param xPath
	 * @return
	 */
	public static String addFolderStart(String xPath) {
		String sp = StringUtil.replaceAll(xPath, "\\", "/");

		return sp.startsWith("/") ? sp : "/" + sp;

	}

	/**
	 * 添加路径尾部 /, 替换路径中的\ 为 /
	 * 
	 * @param xPath
	 * @return
	 */
	public static String addFolderEnd(String xPath) {
		String sp = StringUtil.replaceAll(xPath, "\\", "/");

		return sp.endsWith("/") ? sp : sp + "/";

	}

	/**
	 * 判断路径是否目录
	 * 
	 * @param xPath
	 * @return
	 */
	public static boolean isFolder(String xPath) {
		return xPath.substring(xPath.lastIndexOf("/")).indexOf(".") < 0;
	}

	private static String pathSeparator = "/", extensionSeparator = ".";

	public static String getExtension(String fullPath) {
		int dot = fullPath.lastIndexOf(extensionSeparator);
		return dot>0 ?fullPath.substring(dot + 1):"";
	}

	public static String getFileName(String fullPath) { // gets filename without
													// extension
		int dot = fullPath.lastIndexOf(extensionSeparator);
		int sep = lastIndexOf(fullPath);
		return (dot>sep && sep>0)?fullPath.substring(sep + 1, dot):"";
	}

	public static String getFileNameWithExt(String fullPath) { // gets filename with
														// extension
		// int dot = fullPath.lastIndexOf(extensionSeparator);
		int sep = lastIndexOf(fullPath);
		return sep>0?fullPath.substring(sep + 1):"";
	}

	public static String getPath(String fullPath) {
		int sep = lastIndexOf(fullPath);
		return sep>0?fullPath.substring(0, sep):"";
	}
	
	public static int lastIndexOf(String sp){
		if(sp.contains("/")){
			return sp.lastIndexOf("/");
		}if (sp.contains("\\")){
			return sp.lastIndexOf("\\");
		}else
			return -1;
			
	}

	public static void main(String[] args) {
		System.out.println(addFolderEnd("aaa/bbb/ccc\\ddd/"));
		System.out.println(addFolderStart("aaa/bbb/ccc\\ddd/"));
		System.out.println(trimFolderEnd("aaa/bbb/ccc\\ddd/"));
		System.out.println(trimFolderStart("aaa/bbb/ccc\\ddd/"));

		System.out.println(autoPathRoot("aaa/bbb/ccc\\ddd/", "ccc"));
		System.out.println(autoUrlToPath("aaa/bbb/ccc\\ddd/", "c:\\zz", "ccc"));
		
		System.out.println(getFileNameWithExt("E:\\Open Source\\Java\\zip4j_1.3.2\\zip4j_examples_eclipse_1.3.2.zip"));
		System.out.println(getFileName("E:/Open Source/Java/zip4j_1.3.2/zip4j_examples_eclipse_1.3.2.zip"));
		System.out.println(getPath("E:/Open Source/Java/zip4j_1.3.2/zip4j_examples_eclipse_1.3.2.zip"));

	}

}
