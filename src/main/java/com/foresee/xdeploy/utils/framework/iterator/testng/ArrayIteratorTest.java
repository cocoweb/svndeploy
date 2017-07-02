package com.foresee.xdeploy.utils.framework.iterator.testng;

import org.testng.annotations.Test;

import com.foresee.xdeploy.utils.framework.iterator.ArrayIterator;

public class ArrayIteratorTest {

  @Test
  public void getCurrentObject() {
    String [] astr = {"aaa","bbb","ccc"};
    
    ArrayIterator it = new ArrayIterator("ASTR", astr);
    while(it.hasNext()){
        it.nextObj();
        System.out.println(it.getName()+":"+it.getValue());
    }
    
  }
}
