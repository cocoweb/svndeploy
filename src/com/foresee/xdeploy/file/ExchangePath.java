package com.foresee.xdeploy.file;

import com.foresee.test.util.lang.StringUtil;
import com.foresee.xdeploy.utils.PathUtils;

/**
 * 路径转换器
 * 
 * @author Allan 每一个srcPath（excel中的清单文件）都有以下几个对应环境的路径： 1、在svn主干中的路径 SrcPath
 *         2、在svn基线中的路径 3、在war包中的路径 FromPath 如果是jar里面的java文件，就包含两种： JARName a)
 *         war中jar文件的路径 b) jar里面的文件路径 4、输出到zip增量包里面的路径 ToZipPath 另外还包括几个容器路径
 *         1、excel增量清单路径 2、合并后的excel文件路径 3、svn主干URL 4、svn分支基线URL
 * 
 *         5、war包路径 6、临时jar包路径 7、输出zip增量包文件的路径
 * 
 *         JARName =gov.chinatax.gt3nf FromPath
 *         =gov/chinatax/gt3nf/sb/dkdjdsdjbg/entry/impl/DkdjdsdjSbService.class
 *         ToZipPath
 *         =gov.chinatax.gt3nf/gov/chinatax/gt3nf/sb/dkdjdsdjbg/entry/impl/
 *         DkdjdsdjSbService.class SrcPath
 *         =/trunk/engineering/src/gt3nf/java/gov.chinatax.gt3nf/src/gov/
 *         chinatax/gt3nf/sb/dkdjdsdjbg/entry/impl/DkdjdsdjSbService.java
 *
 */
public class ExchangePath {
    private static PropValue propvalue = null;

    public String JARName = "";
    public String FromPath = "";
    public String ToZipPath = "";
    public String SrcPath = "";
    public String MappingKey = "";

    public static void InitExchangePath(PropValue pv) {
        propvalue = pv;
    }

    public ExchangePath(String jARName, String fromPath, String toZipPath, String srcPath, String mappingKey) {
        this(jARName, fromPath, toZipPath, srcPath);
        MappingKey = mappingKey;
    }

    public ExchangePath(String jARName, String fromPath, String toZipPath, String srcPath) {
        super();
        JARName = jARName;
        FromPath = fromPath;
        ToZipPath = toZipPath;
        SrcPath = srcPath;
    }

    @Override
    public String toString() {
        return " JARName   =" + JARName + "\n FromPath  =" + FromPath + "\n ToZipPath =" + ToZipPath + "\n SrcPath   =" + SrcPath + "\n Key       ="
                + MappingKey;
    }

    public boolean inJar() {
        return MappingKey.indexOf("j.") == 0;
    }

    public boolean inWar() {
        return MappingKey.indexOf("w.") == 0;
    }

    /**
     * 根据配置转换路径
     * 
     * @param srcPath
     * @return
     */
    public static ExchangePath exchange(String srcPath) {

        for (String akey : propvalue.pkgmap.keySet()) {
            if (srcPath.contains(".java") || srcPath.contains(".xml")) { // 不是java文件就不处理

            }
            // 分离源路径 和 目标路径
            String[] apath = StringUtil.split(propvalue.pkgmap.get(akey), "|");
            if (akey.contains("j.") && srcPath.contains(apath[0])) { // 如果路径中包含了“源路径”
                String jarName = akey.substring(2);
                String fromPath = PathUtils.trimFolderStart(srcPath.substring(srcPath.indexOf(apath[0]) + apath[0].length())).replace(".java",
                        ".class");
                String toPath = PathUtils.addFolderEnd(apath[1]) + fromPath;

                return new ExchangePath(jarName, fromPath, toPath, srcPath, akey);
            }
        }
        return new ExchangePath("", "", "", PathUtils.trimFolderStart(srcPath));
    }

}