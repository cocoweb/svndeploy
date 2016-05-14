package com.foresee.xdeploy.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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



}
