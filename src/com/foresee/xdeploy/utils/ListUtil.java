package com.foresee.xdeploy.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.iterators.FilterIterator;

import com.foresee.test.loadrunner.lrapi4j.lr;
import com.foresee.test.util.lang.StringUtil;
import com.foresee.xdeploy.file.ToZipFile;
import com.foresee.xdeploy.file.ToZipFile.IHandlePackage;

public class ListUtil {
	/**
	 * 顺序列表排重
	 * 
	 * @param list
	 * @return
	 */
	public static <T> List<T> removeDeuplicate(List<T> list, Comparator<T> comparator) {
		List<T> newlist = new ArrayList<T>();

		for (int z = 0; z < list.size(); z++) {
			T obj1 = list.get(z);
			T obj2 = z + 1 >= list.size() ? null : list.get(z + 1);

			if (comparator.compare(obj1, obj2) != 0) {
				// if(obj2==null||!obj1.get(ColList_Path).equals(obj2.get(ColList_Path))){
				newlist.add(obj1);
			}
		}

		list.clear();
		list.addAll(newlist);

		return newlist;
	}

	public static <T> List<T> removeDeuplicate(List<T> list) {
		return ListUtil.removeDeuplicate(list, new Comparator<T>() {

			@Override
			public int compare(T o1, T o2) {
				return o2 == null ? -1 : (o1.equals(o2) ? 0 : -1);

				// if (o2 == null || !o1.equals(o2)) {
				//
				// return 0;
				// } else
				// return -1;
			}

		});
	}

	/**
	 * 处理package包的接口
	 * 
	 */
	public interface IHandleScan<T> {
		public int handleItem(T pak);
	}

	public static <T> int scanArray(T[] Projs, IHandleScan<T> handlescan) {
		int ret = 0;

		for (T pak : Projs) {
			if (handlescan != null)
				ret = handlescan.handleItem(pak);
		}
		return ret;

	}
	public static <T> int scanList(List<T> Projs, IHandleScan<T> handlescan) {
		int ret = 0;

		for (T pak : Projs) {
			if (handlescan != null)
				ret = handlescan.handleItem(pak);
		}
		return ret;

	}
	
	public interface ICheck<T>  {
		
		public boolean check(T c);
	
	}

	public static <K,V> Entry<K,V> findMapEntry(Map<K,V> omap,ICheck<Entry<K, V>> check){
        for(Iterator<Entry<K, V>> xiter = omap.entrySet().iterator();xiter.hasNext();){	
    		
     		Entry<K, V> entry = xiter.next();
    		
    		if (check.check(entry)){
    			return entry;
    		}
    		
//    		// 分离源路径 和 目标路径
//            String[] apath = StringUtil.split(entry.getValue(), "|");
//            if (srcPath.contains(apath[0])) {
//                // 如果路径中包含了“源路径”
//                return new String[] { apath[0], apath[1], entry.getKey() };
//            }
    	}
		return null;
		
	}
	
	@SuppressWarnings("rawtypes")
	public static <T> Iterator<T> getMapIterator(Map omap,Predicate<T> pp){
		  return new FilterIterator<T>(omap.entrySet().iterator(),pp
//				  new Predicate<Entry<String, String>>(){
//
//				@Override
//				public boolean evaluate(Entry<String, String> entry) {
//					
//					return entry.getKey().indexOf(skey)==0;
//				}
//				  
//			  }
		  );
	  }
	

}
