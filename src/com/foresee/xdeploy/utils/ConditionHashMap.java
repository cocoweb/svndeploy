package com.foresee.xdeploy.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;



/**
 * 条件查找、模糊定位的map
 * @author allan
 *
 * @param <V>
 */
public class ConditionHashMap<V> extends ConcurrentHashMap<String, V> {

	@Override
	public V get(Object key) {
		// TODO Auto-generated method stub
		return super.get(key);
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = 9069045100992391440L;

	public static <K,V> Entry<K,V> findMapEntry(Map<K,V> omap,ICheck<Entry<K, V>> check){
	    for(Iterator<Entry<K, V>> xiter = omap.entrySet().iterator();xiter.hasNext();){	
			
	 		Entry<K, V> entry = xiter.next();
			
			if (check.check(entry)){
				return entry;
			}
		}
		return null;
		
	}
	
	public Entry<String,V> findEntry(ICheck<Entry<String, V>> check){
		
		
		return findMapEntry(this,check);
		
	}
	
	
	ICheck<Entry<String, V>> localCheck=null;
	public ConditionHashMap<V> setCheck(ICheck<Entry<String, V>> check){
		localCheck = check;
		
		return this;
	}
	
//	protected ICheck<Entry<String, V>> validCheck(ICheck<Entry<String, V>> check){
//		if(localCheck ==null){
//			return check;
//		}else{
//			return localCheck;
//		}
//	}
	
	public List<V> getList(final String key) {
		return getList(key, new ICheck<Entry<String, V>>(){

			@Override
			public boolean check(Entry<String, V> c) {
				
				return ((String) c.getKey()).indexOf(key) != -1;
			}
			
		});
		
	}
	
	public List<V> getList(String key,  ICheck<Entry<String, V>> check) {
		List<V> value = new ArrayList<V>();
		// 是否为模糊搜索
		if (check!=null) {
			for(Iterator<Entry<String, V>> xiter = this.entrySet().iterator();xiter.hasNext();){	
				
		 		Entry<String, V> entry = xiter.next();
				
				if (check.check(entry)){
					value.add(entry.getValue());
				}
			}
			
//			List<String> keyList = new ArrayList<String>();
//			TreeSet<String> treeSet = (TreeSet<String>) this.keySet();
//			for (String string : treeSet) {
//			
//				// 通过排序后,key是有序的.
//				if (string.indexOf(key) != -1) {
//					keyList.add(string);
//					value.add(this.get(string));
//				} else if (string.indexOf(key) == -1 && keyList.size() == 0) {
//					// 当不包含这个key时而且key.size()等于0时,说明还没找到对应的key的开始
//					continue;
//				} else {
//					// 当不包含这个key时而且key.size()大于0时,说明对应的key到当前这个key已经结束.不必要在往下找
//					break;
//				}
//			}
//			keyList.clear();
//			keyList = null;
		} else {
			value.add(this.get(key));
		}
		return value;
	}

	
	public List<V> get (String key, boolean like) {
		List<V> list = null;
		if (like) {
			list = new ArrayList<V>();
			Object[] a = null;
			Set<String> set = this.keySet();
			a =  set.toArray();
			Arrays.sort(a, null);

			for (int i = 0; i < a.length; i++) {
				if (a[i].toString().indexOf(key) == -1) {
					continue;
				} else {
					list.add(this.get(a[i]));
				}
			}
		}
		return list;
	}


	public Set<String> sortedKeySet() {
		Set<String> set = super.keySet();
		TreeSet<String> tSet = null;
		if (set != null) {
			// 对已存在的key进行排序
			tSet = new TreeSet<String>(set);
		}
		return tSet;
	}

	
}
