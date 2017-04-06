package com.foresee.xdeploy.file.rule;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.iterators.FilterIterator;

import com.foresee.test.loadrunner.lrapi4j.lr;
import com.foresee.test.util.lang.StringUtil;
import com.foresee.xdeploy.file.FilesListItem;
import com.foresee.xdeploy.file.PropValue;
import com.foresee.xdeploy.file.base.XdeployBase;
import com.foresee.xdeploy.utils.ConditionHashMap;
import com.foresee.xdeploy.utils.ICheck;
import com.foresee.xdeploy.utils.base.ParamPropValue;

public class MappingRule {
	// 顺序搜索：c-w.META-INF-w-j
	private static String[] sortaStr = { "c." ,"w.META-INF", "w.", "j."};
	
	private static String[] jarNamePrefix = { "com.foresee." ,"gov.chinatax."};
	
	private static String[] ignoreFilter ={"/.project","/.settings/"};
	
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
	
//	private void setPropToArray(String[] toStrArray,String sKey,String sDefault){
//	    
//	    String ss = ppv.getProperty(sKey, sDefault);
//        if (!ss.isEmpty()) {
//            toStrArray = ss.split(",");
//        }
//	}
    private String[]  getPropToArray(String sKey,String sDefault){
        String[] toStrArray={};
        String ss = ppv.getProperty(sKey, sDefault);
        if (!ss.isEmpty()) {
            toStrArray = ss.split(",");
        }
        return toStrArray;
    }

	private MappingRule() {
		super();
		// 初始化mapping规则对象
		ppv = new ParamPropValue("/mapping.properties");

		mappingx = ppv.getSectionItems("mappingx");
		mapping  = ppv.getSectionItems("mapping");
		
//        setPropToArray(sortaStr,"sortmapping", "c.,w.META-INF,w.,j.");
//        setPropToArray(jarNamePrefix,"jarname.prefix", "com.foresee.,gov.chinatax.");
//		setPropToArray(ignoreFilter,"ignore.filter","/.project,/.settings/");
		
		sortaStr=getPropToArray("sortmapping", "c.,w.META-INF,w.,j.");
		jarNamePrefix=getPropToArray("jarname.prefix", "com.foresee.,gov.chinatax.");
		ignoreFilter=getPropToArray("ignore.filter","/.project,/.settings/");

//		String sortmapping = ppv.getProperty("sortmapping", "c.,w.META-INF,w.,j.");
//		if (!sortmapping.isEmpty()) {
//			sortaStr = sortmapping.split(",");
//		}
//		
//		
//		String jarnameprefix = ppv.getProperty("jarname.prefix", "com.foresee.,gov.chinatax.");
//        if (!jarnameprefix.isEmpty()) {
//            jarNamePrefix = jarnameprefix.split(",");
//        }
	}
	private static MappingRule mappingrule=null;
	public static MappingRule getMappingrule() {
	    if (mappingrule==null){
            mappingrule = new MappingRule();
            
        }
        return mappingrule;
    }

    /**
	 * 工厂构造函数
	 * @param oitem
	 * @return
	 */
	public  static MappingRule getMappingRule(FilesListItem oitem){
		
	    getMappingrule().filelistitem = oitem;
		
		// 保存工程名，作为项目参数
		lr.save_string(oitem.getProj(), XdeployBase.LIST_Project);

        return getMappingrule();
		
	}
	
	/**
	 * 在过滤mapping列表中搜索匹配的转换串
	 * 
	 * @param srcPath
	 * @param skey
	 *            过滤mapping列表的key
	 * @return 数组，[0]=原匹配串，[1]=转换串，[2]=mapping关键字名
	 */

	public String[] findSrcPath() {
		Entry<String, String> xentry =  findSrcPath(filelistitem.getPath());
		
		if (xentry != null) {
			String[] apath = StringUtil.split(lr.eval_string(xentry.getValue()), "|");
			if(Array.getLength(apath)==2)
			    return new String[] { apath[0], apath[1], lr.eval_string(xentry.getKey()) };
		}
		
		return new String[] {};
	}

	/**
	 * 在mapping列表中搜索转换串
	 * 
	 * @param srcPath
	 * @return Entry
	 */
	public Entry<String, String> findSrcPath(final String srcPath) {
	
		for (final String skey : getMappingrule().sortaStr) { // 依次搜索
	
			Entry<String, String> xentry =  ConditionHashMap.findMapEntry(mapping, new ICheck<Entry<String, String>>() {
				@Override
				public boolean check(Entry<String, String> entry) {
					return entry.getKey().indexOf(skey) == 0
							&& srcPath.contains(StringUtil.split(entry.getValue(), "|")[0]);
				}
	
			});
	
			if (xentry != null) return xentry;
		}
	
		return findSrcPathX(srcPath);
	}
	
	/**
	 * 搜索mappingx 配置列表
	 * 
	 * @param srcPath
	 * @return  Entry
	 */
	public Entry<String, String> findSrcPathX(final String srcPath) {
		for (final String skey : getMappingrule().sortaStr) { // 依次搜索
			
			if(skey.equals("j.")||skey.equals("c."))   //如果是jar，保存jar名字 到 {JARName}
			     lr.save_string(parserJarName(srcPath), XdeployBase.LIST_JARName);
	
			Entry<String, String> xentry =  ConditionHashMap.findMapEntry(mappingx,new ICheck<Entry<String, String>>() {
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
			
			if (xentry!=null) return xentry;
			
		}
		
		return null;
	}

//	public String[] findSrcPathX1(final String srcPath) {
//		for (final String skey : sortaStr) { // 依次搜索
//			
//			if(skey.equals("j."))   //如果是jar，保存jar名字 到 {JARName}
//			     lr.save_string(parserJarName(srcPath), XdeployBase.LIST_JARName);
//	
//			Entry<String, String> xentry = ListUtil.findMapEntry(mappingx,
//					new ICheck<Entry<String, String>>() {
//						@Override
//						public boolean check(Entry<String, String> entry) {
//							if (entry.getKey().indexOf(skey) == 0) {
//								// 可能存在多个web工程
//								for (String pak : filelistitem.getProjs()) { // StringUtil.split(packages,
//																				// ",、，")){
//									// 临时存放WEBProject
//									lr.save_string(pak, XdeployBase.LIST_Project);
//									
//									return srcPath.contains(StringUtil.split(lr.eval_string(entry.getValue()), "|")[0]);
//								}
//	
//							}
//	
//							return false;
//						}
//	
//					});
//			
//			if (xentry != null) {
//				String[] apath = StringUtil.split(lr.eval_string(xentry.getValue()), "|");
//				return new String[] { apath[0], apath[1], lr.eval_string(xentry.getKey()) };
//			}
//	
//			
//	
//		}
//	
//		return new String[] {};
//	
//	}

//	public static String parserJarName1(String srcPath){
//		//获取起始位置
//		int jarStartIndex = srcPath.indexOf("com.foresee.");
//		if(jarStartIndex <0) jarStartIndex = srcPath.indexOf("gov.chinatax.");
//		
//		if(jarStartIndex >= 0){
//			//获取结束位置
//			int jarEndIndex = srcPath.indexOf("/", jarStartIndex);
//			
//			return jarEndIndex>0 ?srcPath.substring(jarStartIndex, jarEndIndex)
//			        :srcPath.substring(jarStartIndex);
//			
//		}
//		
//		return "";
//		
//	}
	
	/**
	 * 从路径中提取Jar包的名字
	 *    形如：com.foresee.xxxx
	 * 
	 * @param srcPath
	 * @return
	 */
	public static String parserJarName(String srcPath){
	    
	    for(String sprefix : getMappingrule().jarNamePrefix){
	        int jarStartIndex = srcPath.indexOf(StringUtil.trim(sprefix));
	        if(jarStartIndex >= 0){
	            //获取结束位置
	            int jarEndIndex = srcPath.indexOf("/", jarStartIndex);
	            
	            return jarEndIndex>0 ?srcPath.substring(jarStartIndex, jarEndIndex)
	                    :srcPath.substring(jarStartIndex);
	        }
	    }
	    
	    
	    return "";
	}
	
//     public static boolean checkIgnore0(String spath){
//        
//        
//        return spath.lastIndexOf("/.project") > 0|| spath.contains("/.settings/");
//    }

    /**
     * 检查该路径是否可以忽略转换
     * 
     * @param spath
     * @return
     */
    public static boolean checkIgnore(String spath){
       
        for(String sfilter : getMappingrule().ignoreFilter){
            if(spath.contains(StringUtil.trim(sfilter))) 
                return true;
            
        }
        
        return false;
        //spath.lastIndexOf("/.project") > 0|| spath.contains("/.settings/");
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

	private  String[] findSrcPath2(final String srcPath, final String skey) {
	
		Entry<String, String> xentry = ConditionHashMap.findMapEntry(mapping, new ICheck<Entry<String, String>>() {
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
