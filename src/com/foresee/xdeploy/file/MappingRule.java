package com.foresee.xdeploy.file;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.iterators.FilterIterator;

import com.foresee.test.loadrunner.lrapi4j.lr;
import com.foresee.test.util.lang.StringUtil;
import com.foresee.xdeploy.file.base.XdeployBase;
import com.foresee.xdeploy.utils.ListUtil;
import com.foresee.xdeploy.utils.ListUtil.ICheck;
import com.foresee.xdeploy.utils.base.ParamPropValue;

public class MappingRule {
	// 顺序搜索：c-w.META-INF-w-j
	private static String[] sortaStr = { "c.", "w.META-INF", "w.", "j." };
	
	private FilesListItem filelistitem=null;
	
	private ParamPropValue ppv;
	

	private Map<String, String> mappingx;


	private Map<String, String> mapping;

	public Map<String, String> getMappingx() {
		return mappingx;
	}

	public Map<String, String> getMapping() {
		return mapping;
	}

	private MappingRule() {
		super();
		// 初始化mapping规则对象
		ppv = new ParamPropValue("/mapping.properties");

		mappingx = ppv.getSectionItems("mappingx");
		mapping  = ppv.getSectionItems("mapping");

		String sortmapping = ppv.getProperty("sortmapping", "c.,w.META-INF,w.,j.");
		if (!sortmapping.isEmpty()) {
			sortaStr = sortmapping.split(",");
		}

	}
	private static MappingRule mappingrule=null;
	public  static MappingRule getMappingRule(FilesListItem oitem){
		
		if (mappingrule==null){
			mappingrule = new MappingRule();
			
        }
        
		mappingrule.filelistitem = oitem;
		
		// 保存工程名，作为项目参数
		lr.save_string(oitem.getProj(), XdeployBase.LIST_Project);

        return mappingrule;
		
	}
	
	public String[] findSrcPath() {
		return findSrcPath(filelistitem.getPath());
	}

	/**
	 * 在mapping列表中搜索转换串
	 * 
	 * @param srcPath
	 * @return 数组，[0]=原匹配串，[1]=转换串，[2]=mapping关键字名
	 */
	public String[] findSrcPath(final String srcPath) {
	
		for (final String skey : sortaStr) { // 依次搜索
	
			Entry<String, String> xentry = ListUtil.findMapEntry(mapping, new ICheck<Entry<String, String>>() {
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
	
		return findSrcPathX(srcPath);
	}

	public String[] findSrcPathX(final String srcPath) {
		for (final String skey : sortaStr) { // 依次搜索
			
			if(skey.equals("j."))   //如果是jar，保存jar名字 到 {JARName}
			     lr.save_string(parserJarName(srcPath), XdeployBase.LIST_JARName);
	
			Entry<String, String> xentry = ListUtil.findMapEntry(mappingx,
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

	private  String[] findSrcPath2(String srcPath) {
	
		for (String s : sortaStr) { // 依次搜索
	
			String[] astr = findSrcPath2(srcPath, s);
			if (Array.getLength(astr) > 2)
				return astr;
		}
	
		return findSrcPathX2(srcPath);
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
	private  String[] findSrcPath2(final String srcPath, final String skey) {
	
		Entry<String, String> xentry = ListUtil.findMapEntry(mapping, new ICheck<Entry<String, String>>() {
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

	/**
	 * 搜索mappingx 配置列表
	 * 
	 * @param srcPath
	 * @return
	 */
	private  String[] findSrcPathX2(String srcPath) {
		for (String s : sortaStr) { // 依次搜索
	
			for (Entry<String, String> entry : PropValue.getInstance().getSectionItems("mappingx").entrySet()) {
				if (entry.getKey().indexOf(s) == 0) {
	
					// 可能存在多个web工程
					for (String pak : mappingrule.filelistitem.getProjs()) {// StringUtil.split(packages,
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
	private  String[] findSrcPath0(String srcPath, String skey) {
		String lkey = "", rkey = "";
		if (skey.contains("-")) {
			String[] atmp = skey.split("-");
			lkey = atmp[0];
			rkey = atmp[1];
	
		} else
			lkey = skey;
	
		for (Entry<String, String> entry : mapping.entrySet()) {
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

}
