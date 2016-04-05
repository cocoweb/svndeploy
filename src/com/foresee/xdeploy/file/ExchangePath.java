package com.foresee.xdeploy.file;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

import com.foresee.test.util.io.FileUtil;
import com.foresee.test.util.lang.StringUtil;
import com.foresee.test.util.PathUtils;

/**
 * 路径转换器
 * 
 * @author Allan 
 * 
 * 每一个srcPath（excel中的清单文件）都有以下几个对应环境的路径： </Br>
 * 1、在svn主干中的路径                         SrcPath</Br>
 *         2、在svn基线中的路径 </Br>
 *         3、在war包中的路径                              FromPath </Br>
 *         如果是jar里面的java文件，就包含两种：          JARName </Br>
 *         a) war中jar文件的路径 </Br>
 *         b) jar里面的文件路径 </Br>
 *         4、输出到zip增量包里面的路径                  ToZipPath </Br>
 *   另外还包括几个容器路径</Br>
 *         1、excel增量清单路径 </Br>
 *         2、合并后的excel文件路径 </Br>
 *         3、svn主干URL </Br>
 *         4、svn分支基线URL</Br>
 * 
 *         5、war包路径 </Br>
 *         6、临时jar包路径 </Br>
 *         7、输出zip增量包文件的路径</Br>
 * 
 *         JARName =gov.chinatax.gt3nf </Br>
 *         FromPath =gov/chinatax/gt3nf/sb/dkdjdsdjbg/entry/impl/DkdjdsdjSbService.class</Br>
 *         ToZipPath =gov.chinatax.gt3nf/gov/chinatax/gt3nf/sb/dkdjdsdjbg/entry/impl/DkdjdsdjSbService.class </Br>
 *         SrcPath  =/trunk/engineering/src/gt3nf/java/gov.chinatax.gt3nf/src/gov/chinatax/gt3nf/sb/dkdjdsdjbg/entry/impl/DkdjdsdjSbService.java</Br>
 *
 */
public class ExchangePath {
    private static PropValue propvalue = null;

    public String JARName = "";
    public String FromPath = "";
    public String ToZipPath = "";
    //* 1、在svn主干中的路径                         SrcPath
    public String SrcPath = "";
    public String MappingKey = "";

    public static void InitExchangePath(PropValue pv) {
        propvalue = pv;
    }

    private ExchangePath(String jARName, String fromPath, String toZipPath, String srcPath, String mappingKey) {
        this(jARName, fromPath, toZipPath, srcPath);
        MappingKey = mappingKey;
    }

    private ExchangePath(String jARName, String fromPath, String toZipPath, String srcPath) {
        super();
        JARName = jARName;
        FromPath = fromPath;
        ToZipPath = toZipPath;
        SrcPath = srcPath;
    }

    @Override
    public String toString() {
        return  "-------"  
                + "\n JARName     =" + JARName
                + "\n FromPath    =" + FromPath
                + "\n ToZipPath   =" + ToZipPath
                + "\n SrcPath     =" + SrcPath
                + "\n Key         =" + MappingKey
                + "\n TrunkURL    =" + getTrunkURL(SrcPath)
                + "\n ToExcelFile =" + propvalue.genOutExcelFileName()
                + "\n ToZipFile   =" + propvalue.genOutZipFileName();
    }
    
    public Map<String,String> toMap(){
        Map<String,String> retmap = new HashMap<String,String>();
        retmap.put("JARName", JARName);
        retmap.put("FromPath", FromPath);
        retmap.put("ToZipPath", ToZipPath);
        retmap.put("SrcPath", SrcPath);
        retmap.put("Key", MappingKey);
        retmap.put("TrunkUrl", getTrunkURL(SrcPath));
        retmap.put("ToExcelFile", propvalue.genOutExcelFileName());
        retmap.put("ToZipFile", propvalue.genOutZipFileName());
        
        
        return retmap;
    }

    /**
     * @return the toZipPath
     */
    public String getToZipPath() {
        return ToZipPath;
    }
    public String getToZipPath(String keyRoot) {
        return keyRoot  + ToZipPath.substring(ToZipPath.indexOf("/"));
        
    }

    public boolean inJar() {
        return MappingKey.indexOf("j.") == 0;
    }

    public boolean inWar() {
        return MappingKey.indexOf("w.") == 0;
    }
    
    
    public boolean isJava() {
        
        return SrcPath.lastIndexOf(".java") > 0;
    }

    private static String[] findSrcPath(String srcPath){
        for (String akey : propvalue.pkgmap.keySet()) {
            // 分离源路径 和 目标路径
            String[] apath = StringUtil.split(propvalue.pkgmap.get(akey), "|");
            if (srcPath.contains(apath[0])) {
                // 如果路径中包含了“源路径”
                return new String[]{apath[0],apath[1],akey};
            }
        }
        
        return new String[]{};
    }

    /**
     * 根据配置转换路径
     * 
     * @param srcPath
     * @return
     */
    public static ExchangePath exchange(String srcPath) {
        ExchangePath ep =null;
        
        String[] xpath = findSrcPath(srcPath);
        if (Array.getLength(xpath)>1){
            
            String jarName = xpath[1];
            String fromPath = PathUtils.trimFolderStart(
                  srcPath.substring(srcPath.indexOf(xpath[0]) + xpath[0].length())).replace(".java",".class");
            String toPath = PathUtils.addFolderEnd(xpath[1]) + fromPath;
            
            ep=new ExchangePath(jarName, fromPath, toPath, srcPath, xpath[2]);
            
        }else
            ep=new ExchangePath("", "", "", PathUtils.trimFolderStart(srcPath) );
        
        
        return ep;
    }
    
    
    public String getToZipFolderPath(){
        return FileUtil.getFolderPath(ToZipPath);
    }
    public String getToZipFolderPath(String keyRoot){
        String ss = getToZipFolderPath();
        return keyRoot + ss.substring(ss.indexOf("/"));
    }

    public static String getTrunkURL(String srcPath){
        String fromPath = PathUtils.autoPathRoot(srcPath, "trunk");
        // svn库的文件绝对路径URL
        String sUrl = propvalue.svnurl +   fromPath;
        
        return sUrl;

    }
    
    public String getTrunkURL(){
        return getTrunkURL(SrcPath);
    }
    
    public String getOutZipFileName(){
        return getOutZipFile();
    }
    
    public static String getOutZipFile(){
        return propvalue.genOutZipFileName();
    }
    
    public String getFileName(){
        return PathUtils.getFileNameWithExt(SrcPath);
    }
    
   public static void main(String[] args) {
        InitExchangePath( new PropValue("/svntools.properties"));
        
       System.out.println(exchange("/trunk/engineering/src/tax/java/com.foresee.tax.service/src/com/foresee/tax/service/gt3/bigdata/constants/DsjclRwConstant.java"));
        //System.out.println(ExchangePath.propvalue.exchangeFilePath("/trunk/engineering/src/tax/java/com.foresee.tax.service/src/com/foresee/tax/service/gt3/bigdata/constants/DsjclRwConstant.java"));
        System.out.println(exchange("/trunk/engineering/src/gt3nf/web/gt3nf-skin/WebContent/etax/script/module/sbzs/init/sbInit_fqdqdzcpcljjsb.js"));
        System.out.println(exchange("trunk/engineering/src/gt3nf/web/gt3nf-task/.settings/org.eclipse.wst.common.component"));
       // System.out.println(ExchangePath.propvalue.exchangePath("/trunk/engineering/src/gt3nf/web/gt3nf-skin/WebContent/etax/script/module/sbzs/init/sbInit_fqdqdzcpcljjsb.js"));
        //System.out.println(exchange("/trunk/engineering/src/gt3nf/web/gt3nf-wsbs/WebContent/forms/TAX_910610010066.txt"));

    }

 
}   

/**
     * 按照配置 * 转换路径
     * 每一个srcPath（excel中的清单文件）都有以下几个对应环境的路径：
     * 1、在svn主干中的路径
     * 2、在svn基线中的路径
     * 3、在war包中的路径
     *      如果是jar里面的java文件，就包含两种：a) war中jar文件的路径 b)jar里面的文件路径
     * 4、输出到zip增量包里面的路径
     * 
     * 
     * @param srcPath
     * @return
     */
//    public static String exchange(String srcPath) {
//        for (String akey : pkgmap.keySet()) {
//            // 分离源路径 和 目标路径
//            String[] apath = StringUtil.split(pkgmap.get(akey), "|");
//            if (srcPath.contains(apath[0]) && akey.contains("w.")) { // 如果路径中包含了“源路径”
//                // return PathUtils.autoPathRoot(srcPath, xKeyRoot, string2)
//                return PathUtils.addFolderEnd(apath[1]) + PathUtils.trimFolderStart(srcPath.substring(srcPath.indexOf(apath[0]) + apath[0].length()));
//            }else if(srcPath.contains(apath[0])){
//                return PathUtils.addFolderEnd(apath[1]) + PathUtils.trimFolderStart(srcPath.substring(srcPath.indexOf(apath[0]) + apath[0].length()));
//            }
//        }
//
//        return PathUtils.trimFolderStart(srcPath);
//    }
    /**
     * 按照配置 * 转换路径
     * 每一个srcPath（excel中的清单文件）都有以下几个对应环境的路径：
     * 1、在svn主干中的路径
     * 2、在svn基线中的路径
     * 3、在war包中的路径
     *      如果是jar里面的java文件，就包含两种：a) war中jar文件的路径 b)jar里面的文件路径
     * 4、输出到zip增量包里面的路径
     * 
     * 
     * @param srcPath
     * @return
     */
//    public String exchangePath(String srcPath) {
//        for (String akey : pkgmap.keySet()) {
//            // 分离源路径 和 目标路径
//            String[] apath = StringUtil.split(pkgmap.get(akey), "|");
//            if (srcPath.contains(apath[0]) && akey.contains("w.")) { // 如果路径中包含了“源路径”
//                // return PathUtils.autoPathRoot(srcPath, xKeyRoot, string2)
//                return PathUtils.addFolderEnd(apath[1]) + PathUtils.trimFolderStart(srcPath.substring(srcPath.indexOf(apath[0]) + apath[0].length()));
//            }else if(srcPath.contains(apath[0])){
//                return PathUtils.addFolderEnd(apath[1]) + PathUtils.trimFolderStart(srcPath.substring(srcPath.indexOf(apath[0]) + apath[0].length()));
//            }
//        }
//
//        return PathUtils.trimFolderStart(srcPath);
//    }
//
//    public String exchangeWarPath(String srcPath) {
//        srcPath = exchangePath(srcPath);
//        return srcPath.substring(srcPath.indexOf("/") + 1);
//
//    }
//    
//    /**
//     * 根据配置转换路径
//     * 
//     * @param srcPath
//     * @return  str[0]=key  or  jarname
//     *          str[1]=fromPath  jar包中的源路径
//     *          str[2]=topath  转换后的Path,保存到Zip中
//     *          str[3]=srcpath  原始路径
//     */
//    public ExchangePath exchangeJarPath(String srcPath) {
//        if (srcPath.contains(".java")||srcPath.contains(".xml")) {  // 不是java文件就不处理
//
//            for (String akey : pkgmap.keySet()) {
//                // 分离源路径 和 目标路径
//                String[] apath = StringUtil.split(pkgmap.get(akey), "|");
//                if (akey.contains("j.")&& srcPath.contains(apath[0])) { // 如果路径中包含了“源路径”
//                    String jarName = akey.substring(2);
//                    String fromPath = PathUtils.trimFolderStart(srcPath.substring(srcPath.indexOf(apath[0]) + apath[0].length()))
//                            .replace(".java", ".class");
//                    String toPath = PathUtils.addFolderEnd(apath[1])+fromPath;
//                    
//                    return new ExchangePath(jarName,fromPath,toPath,srcPath,akey);
//                }
//            }
//        }
//        return new ExchangePath("", "", "", PathUtils.trimFolderStart(srcPath) );
//    }
//    
//    public ExchangePath exchangeFilePath(String srcPath) {
//        if (srcPath.contains(".java")||srcPath.contains(".xml")) {  // 不是java、xml文件就不处理
//
//            for (String akey : pkgmap.keySet()) {
//                // 分离源路径 和 目标路径
//                String[] apath = StringUtil.split(pkgmap.get(akey), "|");
//                if ( akey.contains("j.")&& srcPath.contains(apath[0])) { // 如果路径中包含了“源路径”
//                    String jarName = akey.substring(2);
//                    String fromPath = PathUtils.trimFolderStart(srcPath.substring(srcPath.indexOf(apath[0]) + apath[0].length()))
//                            .replace(".java", ".class");
//                    String toPath = PathUtils.addFolderEnd(apath[1])+fromPath;
//                    
//                    return new ExchangePath(jarName,fromPath,toPath,srcPath,akey);
//                }
//            }
//        }
//        return new ExchangePath("", "", "", PathUtils.trimFolderStart(srcPath) );
//    }
    