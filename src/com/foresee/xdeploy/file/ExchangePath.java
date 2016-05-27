package com.foresee.xdeploy.file;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.iterators.FilterIterator;

import com.foresee.test.loadrunner.lrapi4j.lr;
import com.foresee.test.util.PathUtils;
import com.foresee.test.util.io.FileUtil;
import com.foresee.test.util.lang.StringUtil;
import com.foresee.xdeploy.file.base.XdeployBase;
import com.foresee.xdeploy.utils.ListUtil;
import com.foresee.xdeploy.utils.ListUtil.ICheck;

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

	// 顺序搜索：c-w.META-INF-w-j
	static String[] sortaStr = { "c.", "w.META-INF", "w.", "j." };

	/**
	 * 对ExchangePath 路径转换器进行全局初始化 在系统启动时，需要调用一次
	 * 
	 * @param pv
	 */
	public static void InitExchangePath(PropValue pv) {
		propvalue = pv;
		String sortmapping = propvalue.getProperty("sortmapping", "c.,w.META-INF,w.,j.");
		if (!sortmapping.isEmpty()) {
			sortaStr = sortmapping.split(",");
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
		String[] xpath = findSrcPath1(srcPath);
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
		// 保存工程名，作为项目参数
		lr.save_string(oitem.getProj(), XdeployBase.LIST_Project);
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
	
	/**
	 * 从路径中提取Jar包的名字
	 *    形如：com.foresee.xxxx
	 * 
	 * @param srcPath
	 * @return
	 */
	public static String parserJarName(String srcPath){
		//获取起始位置
		int jarStartIndex = srcPath.indexOf("com.foresee.");
		if(jarStartIndex <0) jarStartIndex = srcPath.indexOf("gov.chinatax.");
		
		if(jarStartIndex >= 0){
			//获取结束位置
			int jarEndIndex = srcPath.indexOf("/", jarStartIndex);
			
			return srcPath.substring(jarStartIndex, jarEndIndex);
			
		}
		
		return "";
		
	}

	@Override
	public String toString() {
		return "-------" + "\n Key           = <" + MappingKey + "> FileType:[" + getPathType() + "] JARName = "
				+ JARName + "\n   SrcPath     =" + SrcPath + "\n   FromWarPath =" + FromPath + "\n   ToZipPath   ="
				+ getToZipPath()
				// + "\n TrunkURL =" + getTrunkURL(SrcPath)
				+ "\n   SvnURL      =" + getSvnURL()
		// + "\n ToExcelFile =" + ExcelFiles.getOutExcelFileName()
		// + "\n ToZipFile =" + ToZipFile.getOutZipFileName()
		;
	}

	public Map<String, String> toMap() {
		Map<String, String> retmap = new HashMap<String, String>();
		retmap.put("JARName", JARName);
		retmap.put("FromPath", FromPath);
		retmap.put("ToZipPath", getToZipPath());
		retmap.put("SrcPath", SrcPath);
		retmap.put("Key", MappingKey);
		retmap.put("TrunkUrl", getTrunkURL(SrcPath));
		retmap.put("ToExcelFile", ExcelFiles.getOutExcelFileName());
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
	 * 获取集合中满足条件的iterator
	 * 
	 * @param skey
	 * @param coll
	 * @return
	 */
	public static Iterator<Entry<String, String>> getIter(final String skey, Collection<Entry<String, String>> coll) {
		return new FilterIterator<Entry<String, String>>(coll.iterator(), new Predicate<Entry<String, String>>() {

			@Override
			public boolean evaluate(Entry<String, String> entry) {

				return entry.getKey().indexOf(skey) == 0;
			}

		});
	}

	/**
	 * 在mapping列表中搜索转换串
	 * 
	 * @param srcPath
	 * @return 数组，[0]=原匹配串，[1]=转换串，[2]=mapping关键字名
	 */
	private static String[] findSrcPath1(final String srcPath) {

		for (final String skey : sortaStr) { // 依次搜索

			Entry<String, String> xentry = ListUtil.findMapEntry(propvalue.pkgmap, new ICheck<Entry<String, String>>() {
				@Override
				public boolean check(Entry<String, String> entry) {
					return entry.getKey().indexOf(skey) == 0
							&& srcPath.contains(StringUtil.split(entry.getValue(), "|")[0]);
				}

			});

			if (xentry != null) {
				String[] apath = StringUtil.split(xentry.getValue(), "|");
				return new String[] { apath[0], apath[1], xentry.getKey() };
			}
		}

		return findSrcPathX1(srcPath);
		// return new String[] {};
	}

	private static String[] findSrcPathX1(final String srcPath) {
		for (final String skey : sortaStr) { // 依次搜索
			
			if(skey.equals("j."))   //如果是jar，保存jar名字 到 {JARName}
			     lr.save_string(parserJarName(srcPath), XdeployBase.LIST_JARName);

			Entry<String, String> xentry = ListUtil.findMapEntry(propvalue.getSectionItems("mappingx"),
					new ICheck<Entry<String, String>>() {
						@Override
						public boolean check(Entry<String, String> entry) {
							if (entry.getKey().indexOf(skey) == 0) {
								// 可能存在多个web工程
								for (String pak : filelistitem.getProjs()) { // StringUtil.split(packages,
																				// ",、，")){
									// 临时存放WEBProject
									lr.save_string(pak, XdeployBase.LIST_Project);
									
									return srcPath.contains(StringUtil.split(lr.eval_string(entry.getValue()), "|")[0]);
								}

							}

							return false;
						}

					});
			
			if (xentry != null) {
				String[] apath = StringUtil.split(lr.eval_string(xentry.getValue()), "|");
				return new String[] { apath[0], apath[1], lr.eval_string(xentry.getKey()) };
			}

			

		}

		return new String[] {};

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
		return propvalue.tempPath + "/" + getFileName();
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

	private static String[] findSrcPath(String srcPath) {
	
		for (String s : sortaStr) { // 依次搜索
	
			String[] astr = findSrcPath(srcPath, s);
			if (Array.getLength(astr) > 2)
				return astr;
		}
	
		return findSrcPathX(srcPath);
		// return new String[] {};
	}

	/**
	 * 在过滤mapping列表中搜索匹配的转换串
	 * 
	 * @param srcPath
	 * @param skey
	 *            过滤mapping列表的key
	 * @return 数组，[0]=原匹配串，[1]=转换串，[2]=mapping关键字名
	 */
	private static String[] findSrcPath(final String srcPath, final String skey) {
	
		Entry<String, String> xentry = ListUtil.findMapEntry(propvalue.pkgmap, new ICheck<Entry<String, String>>() {
			@Override
			public boolean check(Entry<String, String> entry) {
				return entry.getKey().indexOf(skey) == 0
						&& srcPath.contains(StringUtil.split(entry.getValue(), "|")[0]);
			}
	
		});
	
		if (xentry != null) {
			String[] apath = StringUtil.split(xentry.getValue(), "|");
			return new String[] { apath[0], apath[1], xentry.getKey() };
		}
	
		// //Iterator<Entry<String, String>> xiter =
		// getIter(skey,propvalue.pkgmap.entrySet());
		// //while(xiter.hasNext()){
		// for(Iterator<Entry<String, String>> xiter =
		// getIter(skey,propvalue.pkgmap.entrySet());xiter.hasNext();){
		//
		// Entry<String, String> entry = xiter.next();
		//
		// // 分离源路径 和 目标路径
		// String[] apath = StringUtil.split(entry.getValue(), "|");
		// if (srcPath.contains(apath[0])) {
		// // 如果路径中包含了“源路径”
		// return new String[] { apath[0], apath[1], entry.getKey() };
		// }
		// }
	
		// for(Entry<String, String> entry:propvalue.pkgmap.entrySet()){
		// if (entry.getKey().indexOf(skey)==0){
		// // 分离源路径 和 目标路径
		// String[] apath = StringUtil.split(entry.getValue(), "|");
		// if (srcPath.contains(apath[0])) {
		// // 如果路径中包含了“源路径”
		// return new String[] { apath[0], apath[1], entry.getKey() };
		// }
		//
		// }
		//
		// }
	
		return new String[] {};
	}

	// private static Entry<String, String> findSrcPath1(final String srcPath,
	// final String skey) {
	//
	// return ListUtil.findMapEntry(propvalue.pkgmap, new ICheck<Entry<String,
	// String>>(){
	// @Override
	// public boolean check(Entry<String, String> entry) {
	// return entry.getKey().indexOf(skey) == 0 &&
	// srcPath.contains(StringUtil.split(entry.getValue(), "|")[0]) ;
	// }
	//
	// });
	//
	// }
	
	/**
	 * 搜索mappingx 配置列表
	 * 
	 * @param srcPath
	 * @return
	 */
	private static String[] findSrcPathX(String srcPath) {
		for (String s : sortaStr) { // 依次搜索
	
			for (Entry<String, String> entry : propvalue.getSectionItems("mappingx").entrySet()) {
				if (entry.getKey().indexOf(s) == 0) {
	
					// 可能存在多个web工程
					for (String pak : filelistitem.getProjs()) {// StringUtil.split(packages,
																// ",、，")){
						// 临时存放WEBProject
						lr.save_string(pak, XdeployBase.LIST_Project);
	
						// 分离源路径 和 目标路径
						String[] apath = StringUtil.split(lr.eval_string(entry.getValue()), "|");
						if (srcPath.contains(apath[0])) {
							// 如果路径中包含了“源路径”
							return new String[] { apath[0], apath[1], lr.eval_string(entry.getKey()) };
						}
					}
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
	
		for (Entry<String, String> entry : propvalue.pkgmap.entrySet()) {
			// for (String akey : propvalue.pkgmap.keySet()) {
			if ((entry.getValue().indexOf(lkey) == 0 && rkey.isEmpty()) || (entry.getValue().indexOf(lkey) == 0
					&& (!rkey.isEmpty() && entry.getValue().lastIndexOf(rkey) > 0))) {
				// 分离源路径 和 目标路径
				String[] apath = StringUtil.split(entry.getValue(), "|");
				if (srcPath.contains(apath[0])) {
					// 如果路径中包含了“源路径”
					return new String[] { apath[0], apath[1], entry.getValue() };
				}
	
			}
		}
	
		return new String[] {};
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
