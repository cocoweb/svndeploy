package com.foresee.xdeploy.utils.testng;

import java.util.List;

import org.testng.annotations.Test;

import com.foresee.xdeploy.utils.ConditionHashMap;

public class ConditionHashMapTest {

  @Test
  public void getList() {
	  ConditionHashMap<String> mh = new ConditionHashMap<String>();
		for (int i = 0; i < 1000; i++) {
			mh.put("A_" + i, "AAAAAA" + i);
			mh.put("B_" + i, "BBBBBB" + i);
		}
		mh.put("C_9" , "CCCCCCC" + 9);
		mh.put("C_10" , "CCCCCCC" + 10);
		mh.put("C_22" , "CCCCCCC" + 22);
		mh.put("C_11" , "CCCCCCC" + 11);
		
		List<String> aa = mh.getList("22");
		
		System.out.println(aa);
		
  }
}
