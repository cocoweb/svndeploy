package com.foresee.xdeploy.utils.testng;

import java.util.Map.Entry;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.foresee.test.util.lang.StringUtil;
import com.foresee.xdeploy.file.PropValue;
import com.foresee.xdeploy.utils.ConditionHashMap;

public class ListUtilTest {
	PropValue pv;

	@BeforeClass
	public void beforeClass() {
		pv = PropValue.getInstance();
	}
	
	
	public Entry<String, String> findMapEntry(final String srcPath){
		return ConditionHashMap.findMapEntry(pv.getSectionItems("mapping"), new  com.foresee.xdeploy.utils.ICheck<Entry<String, String>>(){

			@Override
			public boolean check(Entry<String, String> entry) {
	    		// 分离源路径 和 目标路径
	            String[] apath = StringUtil.split(entry.getValue(), "|");
	            return srcPath.contains(apath[0]) ;
			}
			
		});
	}

	@Test
	public void findMapEntry() {
		
		final String srcPath = "/trunk/engineering/src/portal/java/com.foresee.portal.biz/src/com/foresee/portal/biz/sssp/bizdata/CommBjtzsPdfServiceImpl.java";
		
		Entry<String, String> xentry =findMapEntry(srcPath);
		
		System.out.println(xentry);
		
		
		
	}
}
