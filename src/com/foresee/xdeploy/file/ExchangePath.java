package com.foresee.xdeploy.file;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

import com.foresee.test.util.PathUtils;
import com.foresee.test.util.io.FileUtil;
import com.foresee.test.util.lang.StringUtil;

/**
 * 路径转换器
 * 
 * @author Allan
 * 
 *         每一个srcPath（excel中的清单文件）都有以下几个对应环境的路径： </Br>
 *         1、在svn主干中的路径 SrcPath</Br>
 *         2、在svn基线中的路径 </Br>
 *         3、在war包中的路径 FromPath </Br>
 *         如果是jar里面的java文件，就包含两种： JARName </Br>
 *         a) war中jar文件的路径 </Br>
 *         b) jar里面的文件路径 </Br>
 *         4、输出到zip增量包里面的路径 ToZipPath </Br>
 *         
 *         另外还包括几个容器路径</Br>
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
 *         FromPath=gov/chinatax/gt3nf/sb/dkdjdsdjbg/entry/impl/DkdjdsdjSbService.class
 *         </Br>
 *         ToZipPath
 *         =gov.chinatax.gt3nf/gov/chinatax/gt3nf/sb/dkdjdsdjbg/entry/impl/
 *         DkdjdsdjSbService.class </Br>
 *         SrcPath
 *         =/trunk/engineering/src/gt3nf/java/gov.chinatax.gt3nf/src/gov/
 *         chinatax/gt3nf/sb/dkdjdsdjbg/entry/impl/DkdjdsdjSbService.java</Br>
 *
 */
public class ExchangePath {
    private static PropValue propvalue = null;

    public static final String Type_WAR = "WAR";
    public static final String Type_JAR = "JAR";
    public static final String Type_CHG = "CHG";

    public String JARName = "";
    public String FromPath = "";
    public String ToZipPath = "";
    // * 1、在svn主干中的路径 SrcPath
    public String SrcPath = "";
    public String MappingKey = "";

    // 顺序搜索：c-w.META-INF-w-j
    static String[] sortaStr = { "c.", "w.META-INF", "w.", "j." };

    /**
     * 对ExchangePath 路径转换器进行全局初始化
     * 在系统启动时，需要调用一次
     * @param pv
     */
    public static void InitExchangePath(PropValue pv) {
        propvalue = pv;
        String sortmapping = propvalue.getProperty("sortmapping","c.,w.META-INF,w.,j.");
        if (!sortmapping.isEmpty()){
            sortaStr=sortmapping.split(",");
        }

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

    /**
     * 根据配置转换路径
     *  按照配置 * 转换路径 每一个srcPath（excel中的清单文件）都有以下几个对应环境的路径：
     *  1、在svn主干中的路径
     * 2、在svn基线中的路径 3、在war包中的路径 如果是jar里面的java文件，就包含两种：a) war中jar文件的路径
     * b)jar里面的文件路径 4、输出到zip增量包里面的路径
     * 
     * @param srcPath
     * @return
     * @throws Exception
     */
    public static ExchangePath exchange(String srcPath) throws Exception {
        if (propvalue == null)
            throw new Exception("PropValue 没有初始化！");

        ExchangePath ep = null;
        
        //搜索mapping转换路径
        String[] xpath = findSrcPath(srcPath);
        if (Array.getLength(xpath) > 1) {

            String jarName = xpath[1];
            String fromPath = PathUtils.trimFolderStart(srcPath.substring(srcPath.indexOf(xpath[0]) + xpath[0].length()))
                    .replace(".java", ".class");
            String toPath = PathUtils.addFolderEnd(xpath[1]) + fromPath;

            ep = new ExchangePath(jarName, fromPath, toPath, srcPath, xpath[2]);

        } else
            ep = new ExchangePath("", "", "", PathUtils.trimFolderStart(srcPath));

        return ep;
    }

    @Override
    public String toString() {
        return "-------" + "\n JARName     =" + JARName + "\n FromPath    =" + FromPath + "\n ToZipPath   =" + getToZipPath()
                + "\n SrcPath     =" + SrcPath + "\n Key         =" + MappingKey
                // + "\n TrunkURL =" + getTrunkURL(SrcPath)
                + "\n SvnURL      =" + getSvnURL() + "\n ToExcelFile =" + ExcelFiles.getOutExcelFileName() + "\n ToZipFile   ="
                + ToZipFile.getOutZipFileName() + "\n FileType    =" + getPathType();
    }

    public Map<String, String> toMap() {
        Map<String, String> retmap = new HashMap<String, String>();
        retmap.put("JARName", JARName);
        retmap.put("FromPath", FromPath);
        retmap.put("ToZipPath", ToZipPath);
        retmap.put("SrcPath", SrcPath);
        retmap.put("Key", MappingKey);
        retmap.put("TrunkUrl", getTrunkURL(SrcPath));
        retmap.put("ToExcelFile", ExcelFiles.getOutExcelFileName());
        retmap.put("ToZipFile", ToZipFile.getOutZipFileName());

        return retmap;
    }

    /**
     * 获取输出到zip中的相对路径
     * @return the toZipPath
     */
    public String getToZipPath() {
        return ToZipPath;
    }

    /**
     * 根据输入的根目录，获取输出到zip中的相对路径
     * @param keyRoot
     * @return  
     */
    public String getToZipPath(String keyRoot) {
        return keyRoot + ToZipPath.substring(ToZipPath.indexOf("/"));

    }

    /**
     * @return 该文件在jar里面
     */
    public boolean inJar() {
        return MappingKey.indexOf("j.") == 0;
    }

    /**
     * @return  该文件在war里面
     */
    public boolean inWar() {
        return MappingKey.indexOf("w.") == 0;
    }

    /**
     * @return 获取该文件转换器的路径类型
     * war、jar、chg
     */
    public String getPathType() {
        if (inWar())
            return Type_WAR;
        else if (SrcPath.lastIndexOf(".java") > 0 || inJar())
            return Type_JAR;
        else
            return Type_CHG;
    }

    /**
     * @return  如果是Java文件，即返回true
     */
    public boolean isJava() {

        return SrcPath.lastIndexOf(".java") > 0;
    }

    /**
     * 在mapping列表中搜索转换串
     * @param srcPath
     * @return
     */
    private static String[] findSrcPath(String srcPath) {

        for (String s : sortaStr) { // 依次搜索

            String[] astr = findSrcPath(srcPath, s);
            if (Array.getLength(astr) > 2)
                return astr;
        }
        return new String[] {};
    }

    /**
     * 在过滤mapping列表中搜索匹配的转换串
     * 
     * @param srcPath
     * @param skey   过滤mapping列表的key
     * @return  数组，[0]=原匹配串，[1]=转换串，[2]=mapping关键字名
     */
    private static String[] findSrcPath(String srcPath, String skey) {

        for (String akey : propvalue.pkgmap.keySet()) {
            if (akey.indexOf(skey)==0){
                // 分离源路径 和 目标路径
                String[] apath = StringUtil.split(propvalue.pkgmap.get(akey), "|");
                if (srcPath.contains(apath[0])) {
                    // 如果路径中包含了“源路径”
                    return new String[] { apath[0], apath[1], akey };
                }

            }
        }

        return new String[] {};
    }
    /**
     * skey 可以支持 用-号分隔的左右过滤符号
     * 
     * @param srcPath
     * @param skey
     * @return
     */
    private static String[] findSrcPath0(String srcPath, String skey) {
      String lkey = "", rkey = "";
      if (skey.contains("-")) {
          String[] atmp = skey.split("-");
          lkey = atmp[0];
          rkey = atmp[1];

      } else
          lkey = skey;

      for (String akey : propvalue.pkgmap.keySet()) {
          if ((akey.indexOf(lkey) == 0 && rkey.isEmpty())
                  || (akey.indexOf(lkey) == 0 && (!rkey.isEmpty() && akey.lastIndexOf(rkey) > 0))) {
              // 分离源路径 和 目标路径
              String[] apath = StringUtil.split(propvalue.pkgmap.get(akey), "|");
              if (srcPath.contains(apath[0])) {
                  // 如果路径中包含了“源路径”
                  return new String[] { apath[0], apath[1], akey };
              }

          }
      }

      return new String[] {};
  }

    /**
     * @return 输出到zip时的 相对目录路径（不含文件名）
     */
    public String getToZipFolderPath() {
        return FileUtil.getFolderPath(ToZipPath);
    }

    /**
     * @param keyRoot 指定根目录
     * @return  输出到zip时的 相对目录路径（不含文件名）
     */
    public String getToZipFolderPath(String keyRoot) {
        String ss = getToZipFolderPath();
        return keyRoot + ss.substring(ss.indexOf("/"));
    }

    public static String getTrunkURL(String srcPath) {
        String fromPath = PathUtils.autoPathRoot(srcPath, "trunk"); // ??
        // svn库的文件绝对路径URL
        String sUrl = propvalue.svnurl + fromPath;

        return sUrl;

    }

    /**
     * @return svn主干URL，绝对路径
     */
    public String getTrunkURL() {
        return getTrunkURL(SrcPath);
    }

    /**
     * @return  svn绝对路径，svn.url+相对路径
     */
    public String getSvnURL() {
        return propvalue.svnurl + PathUtils.autoPathRoot(SrcPath, propvalue.keyRootFolder);
    }

    /**
     * @return 获取输出的zip文件路径
     */
    public String getOutZipFileName() {
        return getOutZipFile();
    }

    /**
     * @return 获取输出的zip文件路径
     */
    public static String getOutZipFile() {
        return ToZipFile.getOutZipFileName();
    }

    /**
     * @return  文件记录单纯的文件名  如 xxxxx.jsp
     */
    public String getFileName() {
        return PathUtils.getFileNameWithExt(SrcPath);
    }
    
    /**
     * @return 返回临时目录下的文件绝对路径
     */
    public String getToTempFilePath(){
        return propvalue.tempPath + "/" + getFileName();
    }
    
    /**
     * @return export ，copy 输出时的文件绝对路径
     */
    public String  getToFilePath(){
        return PathUtils.autoUrlToPath(getSvnURL(), propvalue.svntofolder, propvalue.keyRootFolder);
    }
    
    
    /**
     * @return svn工作区的绝对路径  ci.workspace
     */
    public String getWorkspaceFilePath(){
        return propvalue.getProperty("ci.workspace") 
                + PathUtils.autoPathRoot(FromPath, propvalue.filekeyroot);
    }

    public static void main(String[] args) {
        InitExchangePath(PropValue.getInstance("/svntools.properties"));

        // System.out.println(exchange("/trunk/engineering/src/tax/java/com.foresee.tax.service/src/com/foresee/tax/service/gt3/bigdata/constants/DsjclRwConstant.java"));
        // System.out.println(ExchangePath.propvalue.exchangeFilePath("/trunk/engineering/src/tax/java/com.foresee.tax.service/src/com/foresee/tax/service/gt3/bigdata/constants/DsjclRwConstant.java"));
        // System.out.println(exchange("/trunk/engineering/src/gt3nf/web/gt3nf-skin/WebContent/etax/script/module/sbzs/init/sbInit_fqdqdzcpcljjsb.js"));
        // System.out.println(exchange("trunk/engineering/src/gt3nf/web/gt3nf-task/.settings/org.eclipse.wst.common.component"));
        // System.out.println(ExchangePath.propvalue.exchangePath("/trunk/engineering/src/gt3nf/web/gt3nf-skin/WebContent/etax/script/module/sbzs/init/sbInit_fqdqdzcpcljjsb.js"));
        try {
            System.out.println(exchange("/trunk/engineering/src/gt3nf/web/gt3nf-wsbs/WebContent/forms/TAX_910610010066.txt"));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // System.out.println(exchange("trunk/engineering/src/gt3nf/web/gt3nf-task/.settings/org.eclipse.wst.common.component"));
    }

}

