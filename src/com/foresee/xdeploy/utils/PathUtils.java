package com.foresee.xdeploy.utils;

import com.foresee.test.util.lang.StringUtil;

public class PathUtils {

    public PathUtils() {
        // TODO Auto-generated constructor stub
    }

//    /**
//     * 根据URL、文件路径，自动生成目标目录
//     * 
//     * @param xUrl
//     * @param xPath
//     * @param xkey
//     * @return
//     */
//    public static String autoUrlRoot(String xUrl, String xPath, String xKey) {
//    
//        return StringUtil.trimEnd(StringUtil.replaceAll(xPath, "\\", "/"), "/")
//                + xUrl.substring(xUrl.indexOf(xKey) - 1);
//    }
    
    
    /**
     * 根据URL，Path头，返回本地绝对路径
     * 
     * @param xUrl
     * @param xPath
     * @param xKey
     * @return
     *                         StringUtil.trimEnd(StringUtil.replaceAll(citoFolder, "\\", "/"), "/")
                        + sPath.substring(sPath.indexOf(keyRootFolder) - 1);                       

     */
    public static String autoUrlToPath(String xUrl, String xPath, String xKey) {
        
       // xPath = StringUtil.trimEnd(StringUtil.replaceAll(xPath, "\\", "/"), "/"); 
        //去掉绝对路径路径尾部 /, 替换路径中的\ 为 /
        
        return trimFolderEnd(xPath) +autoPathRoot(xUrl,xKey); 
                //+ xUrl.substring(xUrl.indexOf(xKey) - 1);
    }
    
    /**
     * 根据指定root目录, 去掉路径头, 返回相对路径
     * @param xPath
     * @param xKeyRoot
     * @return
     */
    public static String autoPathRoot(String xPath, String xKeyRoot) {
        if (xPath.indexOf(xKeyRoot) > 0) {
            return addFolderStart(xPath.substring(xPath.indexOf(xKeyRoot)));
        } else {
            System.out.println("keyroot 配置错误，"+xKeyRoot+"未包含在URL中: "+xPath);
            return xPath;
        }

//        return StringUtil.trimEnd(StringUtil.replaceAll(xPath, "\\", "/"), "/")
//                + xUrl.substring(xUrl.indexOf(xKey) - 1);
    }
    
    
    /**
     * 根据指定root目录, 去掉路径头, 返回相对路径
     * 并且不包含root 和 /
     * 
     * @param xPath
     * @param xKeyRoot
     * @param string2
     * @return
     */
    public static String autoPathRoot(String xPath, String xKeyRoot, String string2) {
        if (xPath.indexOf(xKeyRoot) > 0) {
            return trimFolderStart(xPath.substring(xPath.indexOf(xKeyRoot)+xKeyRoot.length()));
        } else {
            System.out.println("keyroot 配置错误，"+xKeyRoot+"未包含在URL中: "+xPath);
            return xPath;
        }
    }
    
    /**
     *  去掉路径头部 /, 替换路径中的\ 为 /
     * @param xPath
     * @return
     */
    public static String trimFolderStart(String xPath){
        return StringUtil.trimStart(StringUtil.replaceAll(xPath, "\\", "/"), "/");  
        
    }
    
    /**
     *  去掉路径尾部 /, 替换路径中的\ 为 /
     * @param xPath
     * @return
     */
    public static String trimFolderEnd(String xPath){
        return StringUtil.trimEnd(StringUtil.replaceAll(xPath, "\\", "/"), "/");  
        
    }
    
    /**
     * 添加路径头部 /, 替换路径中的\ 为 /
     * @param xPath
     * @return
     */
    public static String addFolderStart(String xPath){
        String sp =StringUtil.replaceAll(xPath, "\\", "/");
       
        return  sp.startsWith("/")?sp:"/"+sp;  
        
    }
    /**
     * 添加路径尾部 /, 替换路径中的\ 为 /
     * @param xPath
     * @return
     */
    public static String addFolderEnd(String xPath){
        String sp =StringUtil.replaceAll(xPath, "\\", "/");
       
        return  sp.endsWith("/")?sp:sp+"/";  
        
    }
    
    /**
     * 判断路径是否目录
     * @param xPath
     * @return
     */
    public static boolean isFolder(String xPath){
    	return xPath.substring(xPath.lastIndexOf("/")).indexOf(".")<0;
    }

    public static void main(String[] args) {
        System.out.println(addFolderEnd("aaa/bbb/ccc\\ddd/"));
        System.out.println(addFolderStart("aaa/bbb/ccc\\ddd/"));
        System.out.println(trimFolderEnd("aaa/bbb/ccc\\ddd/"));
        System.out.println(trimFolderStart("aaa/bbb/ccc\\ddd/"));
    
        System.out.println(autoPathRoot("aaa/bbb/ccc\\ddd/", "ccc"));
        System.out.println(autoUrlToPath("aaa/bbb/ccc\\ddd/", "c:\\zz", "ccc"));
        
    }


}
