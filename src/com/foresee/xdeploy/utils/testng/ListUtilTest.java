package com.foresee.xdeploy.utils.testng;

import org.testng.annotations.Test;

import com.foresee.test.util.lang.StringUtil;
import com.foresee.xdeploy.file.PropValue;
import com.foresee.xdeploy.utils.ListUtil;
import com.foresee.xdeploy.utils.ListUtil.ICheck;

import java.util.Map.Entry;

import org.testng.annotations.BeforeClass;

public class ListUtilTest {
	PropValue pv;

	@BeforeClass
	public void beforeClass() {
		pv = PropValue.getInstance();
	}
	
	
	public Entry<String, String> findMapEntry(final String srcPath){
		return ListUtil.findMapEntry(pv.getSectionItems("mapping"), new ICheck<Entry<String, String>>(){

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
