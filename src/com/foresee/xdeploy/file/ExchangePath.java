package com.foresee.xdeploy.file;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.foresee.test.loadrunner.lrapi4j.lr;
import com.foresee.test.util.PathUtils;
import com.foresee.test.util.io.FileUtil;
import com.foresee.xdeploy.file.base.XdeployBase;

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
 *         FromPath=gov/chinatax/gt3nf/sb/dkdjdsdjbg/entry/impl/
 *         DkdjdsdjSbService.class </Br>
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
	private String ToZipPath = "";
	// * 1、在svn主干中的路径 SrcPath
	public String SrcPath = "";
	public String MappingKey = "";

	/**
	 * 对ExchangePath 路径转换器进行全局初始化 在系统启动时，需要调用一次
	 * 
	 * @param pv
	 */
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

	/**
	 * 根据配置转换路径 按照配置 * 转换路径 每一个srcPath（excel中的清单文件）都有以下几个对应环境的路径： 1、在svn主干中的路径
	 * 2、在svn基线中的路径 3、在war包中的路径 如果是jar里面的java文件，就包含两种：a) war中jar文件的路径
	 * b)jar里面的文件路径 4、输出到zip增量包里面的路径
	 * 
	 * @param srcPath
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public static ExchangePath exchange(String srcPath) throws Exception {
		if (propvalue == null)
			throw new Exception("PropValue 没有初始化！");

		ExchangePath ep = null;

		// 搜索mapping转换路径
		String[] xpath = MappingRule.getMappingRule(filelistitem).findSrcPath();
		if (Array.getLength(xpath) > 1) {

			String jarName = xpath[1];
			String fromPath = PathUtils
					.trimFolderStart(srcPath.substring(srcPath.indexOf(xpath[0]) + xpath[0].length()))
					.replace(".java", ".class");

			String toPath = parserToPath(fromPath, xpath[1]);
			// PathUtils.addFolderEnd(xpath[1]) + fromPath;

			ep = new ExchangePath(jarName, fromPath, toPath, srcPath, xpath[2]);

		} else {
			ep = new ExchangePath("", "", "", PathUtils.trimFolderStart(srcPath));
			throw new Exception(">>> 注意！！路径转换失败，可能mapping配置有问题，或者该文件路径特别:\n" + ep);
		}

		return ep;
	}

	private static FilesListItem filelistitem;

	public static ExchangePath createExchange(FilesListItem oitem) throws Exception {
		filelistitem = oitem;

		ExchangePath ep = exchange(oitem.getPath());

		return ep;
	}

	/**
	 * 处理输出路径，对其中的{FILEName}参数进行处理
	 * 
	 * @param frompath
	 * @param switchroot
	 * @return
	 */
	private static String parserToPath(String frompath, String switchroot) {
		if (switchroot.contains("{" + XdeployBase.LIST_FileName + "}")) {
			lr.save_string(PathUtils.getFileNameWithExt(frompath), XdeployBase.LIST_FileName);

			return lr.eval_string(switchroot);
		} else {
			return PathUtils.addFolderEnd(switchroot) + frompath;
		}
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("-------")
				.append("\n Key           = <" + MappingKey + "> FileType:[" + getPathType() + "] JARName = " + JARName)
				.append("\n   SrcPath     =" + SrcPath).append("\n   FromWarPath =" + FromPath)
				.append("\n   ToZipPath   =" + getToZipPath())
				.append("\n   SvnURL      =" + getSvnURL()).toString();
		// return "-------" + "\n Key = <" + MappingKey + "> FileType:[" +
		// getPathType() + "] JARName = "
		// + JARName + "\n SrcPath =" + SrcPath + "\n FromWarPath =" + FromPath
		// + "\n ToZipPath ="
		// + getToZipPath()
		// // + "\n TrunkURL =" + getTrunkURL(SrcPath)
		// + "\n SvnURL =" + getSvnURL()
		// // + "\n ToExcelFile =" + ExcelFiles.getOutExcelFileName()
		// // + "\n ToZipFile =" + ToZipFile.getOutZipFileName()
		// ;
	}

	public Map<String, String> toMap() {
		Map<String, String> retmap = new HashMap<String, String>();
		retmap.put("JARName", JARName);
		retmap.put("FromPath", FromPath);
		retmap.put("ToZipPath", getToZipPath());
		retmap.put("SrcPath", SrcPath);
		retmap.put("Key", MappingKey);
		retmap.put("TrunkUrl", getTrunkURL(SrcPath));
		retmap.put("ToExcelFile", ToExcelFile.getOutExcelFileName());
		retmap.put("ToZipFile", ToZipFile.getOutZipFileName());

		return retmap;
	}

	/**
	 * @return 该文件在jar里面
	 */
	public boolean inJar() {
		return MappingKey.indexOf("j.") == 0;
	}

	/**
	 * @return 该文件在war里面
	 */
	public boolean inWar() {
		return MappingKey.indexOf("w.") == 0;
	}

	/**
	 * @return 获取该文件转换器的路径类型 war、jar、chg
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
	 * @return 如果是Java文件，即返回true
	 */
	public boolean isJava() {

		return SrcPath.lastIndexOf(".java") > 0;
	}

	/**
	 * 根据输入的根目录，获取输出到zip中的相对路径
	 * 
	 * @param keyRoot
	 * @return
	 */
	public String getToZipPath(String keyRoot) {
		return keyRoot + getToZipPath().substring(getToZipPath().indexOf("/"));

	}
	// expath.getToZipPath().substring(expath.getToZipPath().indexOf("/")+1)

	/**
	 * @return 去掉头部 root/ 的路径
	 */
	public String getToZipPathNoRoot() {
		return getToZipPath().substring(getToZipPath().indexOf("/") + 1);
	}

	/**
	 * 获取输出到zip中的相对路径
	 * 
	 * @return the toZipPath
	 */
	public String getToZipPath() {
		return lr.eval_string(ToZipPath);
	}

	/**
	 * @return 输出到zip时的 相对目录路径（不含文件名）
	 */
	public String getToZipFolderPath() {
		return FileUtil.getFolderPath(getToZipPath());
	}

	/**
	 * @param keyRoot
	 *            指定根目录
	 * @return 输出到zip时的 相对目录路径（不含文件名）
	 */
	public String getToZipFolderPath(String keyRoot) {
		String ss = getToZipFolderPath();
		if (ss.indexOf("/") > 0)
			ss = ss.substring(ss.indexOf("/"));

		return keyRoot + ss;
	}

	public static String getTrunkURL(String srcPath) {
		String fromPath = PathUtils.autoPathRoot(srcPath, "trunk"); // ??
		// svn库的文件绝对路径URL
		String sUrl = PathUtils.trimFolderEnd(propvalue.svnurl) + fromPath;

		return sUrl;

	}

	/**
	 * @return svn主干URL，绝对路径
	 */
	public String getTrunkURL() {
		return getTrunkURL(SrcPath);
	}

	/**
	 * @return svn绝对路径，svn.url+相对路径
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
	 * @return 文件记录单纯的文件名 如 xxxxx.jsp
	 */
	public String getFileName() {
		return PathUtils.getFileNameWithExt(SrcPath);
	}

	/**
	 * @return 返回临时目录下的文件绝对路径
	 */
	public String getToTempFilePath() {
		return PathUtils.addFolderEnd(propvalue.tempPath) + getFileName();
	}

	/**
	 * @return export ，copy 输出时的文件绝对路径
	 */
	public String getToFilePath() {
		return PathUtils.autoUrlToPath(getSvnURL(), propvalue.svntofolder, propvalue.keyRootFolder);
	}

	/**
	 * @return svn工作区的绝对路径 ci.workspace
	 */
	public String getWorkspaceFilePath() {
		return propvalue.getProperty("ci.workspace") + PathUtils.autoPathRoot(FromPath, propvalue.filekeyroot);
	}


	public static void main(String[] args) {
		InitExchangePath(PropValue.getInstance("/svntools.properties"));

		// System.out.println(exchange("/trunk/engineering/src/tax/java/com.foresee.tax.service/src/com/foresee/tax/service/gt3/bigdata/constants/DsjclRwConstant.java"));
		// System.out.println(ExchangePath.propvalue.exchangeFilePath("/trunk/engineering/src/tax/java/com.foresee.tax.service/src/com/foresee/tax/service/gt3/bigdata/constants/DsjclRwConstant.java"));
		// System.out.println(exchange("/trunk/engineering/src/gt3nf/web/gt3nf-skin/WebContent/etax/script/module/sbzs/init/sbInit_fqdqdzcpcljjsb.js"));
		// System.out.println(exchange("trunk/engineering/src/gt3nf/web/gt3nf-task/.settings/org.eclipse.wst.common.component"));
		// System.out.println(ExchangePath.propvalue.exchangePath("/trunk/engineering/src/gt3nf/web/gt3nf-skin/WebContent/etax/script/module/sbzs/init/sbInit_fqdqdzcpcljjsb.js"));
		try {
			System.out.println(
					exchange("/trunk/engineering/src/gt3nf/web/gt3nf-wsbs/WebContent/forms/TAX_910610010066.txt"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println(exchange("trunk/engineering/src/gt3nf/web/gt3nf-task/.settings/org.eclipse.wst.common.component"));
	}

}
