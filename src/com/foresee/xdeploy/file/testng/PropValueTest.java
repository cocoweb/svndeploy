package com.foresee.xdeploy.file.testng;

import org.testng.annotations.Test;

import com.foresee.test.util.lang.StringUtil;
import com.foresee.xdeploy.file.PropValue;
import com.foresee.xdeploy.utils.ListUtil;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.collections4.collection.PredicatedCollection;
import org.apache.commons.collections4.iterators.FilterIterator;
import org.apache.commons.collections4.set.PredicatedSet;
import org.testng.annotations.BeforeClass;

public class PropValueTest {
	PropValue pv;

	@BeforeClass
	public void beforeClass() {
		pv = PropValue.getInstance();

	}

	@Test
	public void initProp() {
		throw new RuntimeException("Test not implemented");
	}

	public Iterator<Entry<String, String>> getIterator(final String skey, Map<String, String> omap) {
		return ListUtil.getMapIterator(omap, new Predicate<Entry<String, String>>() {
			@Override
			public boolean evaluate(Entry<String, String> entry) {

				return entry.getKey().indexOf(skey) == 0;
			}

		});

		// new FilterIterator<Entry<String, String>>(coll.iterator(),new
		// Predicate<Entry<String, String>>(){
		//
		// @Override
		// public boolean evaluate(Entry<String, String> entry) {
		//
		// return entry.getKey().indexOf(skey)==0;
		// }
		//
		// });
	}

	public Set<Entry<String, String>> getSet(final String skey, Set<Entry<String, String>> coll) {
		Set<Entry<String, String>> xx = SetUtils.predicatedSet(coll, new Predicate<Entry<String, String>>() {

			@Override
			public boolean evaluate(Entry<String, String> entry) {

				return entry.getKey().indexOf(skey) == 0;
			}

		});
		return xx;
	}
	
	

	@Test
	public void filterMap() {

		final String skey = "j.";
		
		final String srcPath = "/trunk/engineering/src/portal/java/com.foresee.portal.biz/src/com/foresee/portal/biz/sssp/bizdata/CommBjtzsPdfServiceImpl.java";


		// PredicatedCollection<Entry<String, String>> pc =
		// PredicatedCollection.predicatedCollection(pv.pkgmap.entrySet(), new
		// Predicate<Entry<String, String>>(){
		//
		// @Override
		// public boolean evaluate(Entry<String, String> entry) {
		//
		// return entry.getKey().indexOf(skey)==0;
		// }
		//
		// });

		Iterator<Entry<String, String>> fi = ListUtil.getMapIterator(pv.pkgmap, new Predicate<Entry<String, String>>() {
			@Override
			public boolean evaluate(Entry<String, String> entry) {

				return entry.getKey().indexOf(skey) == 0  && srcPath.contains(StringUtil.split(entry.getValue(), "|")[0]);
			}

		});

		while (fi.hasNext()) {
			Entry<String, String> ee = fi.next();
			System.out.println(ee);

		}
		//
		// for(Entry<String, String> ee : getSet(skey,pv.pkgmap.entrySet())){
		// System.out.println(ee);
		// }

	}
}
