package com.foresee.xdeploy.framework.iterator;

import java.lang.reflect.Array;

public class ArrayIterator extends ObjectIterator {
    
    private final String name;
    private final Object array;
 
    public ArrayIterator(String name, Object array) {
        this.name = name;
        this.array = array;
        //this.length = Array.getLength(array);
    }
 
 
    @Override
    public String getName() {
        return name + "[" + nextIndex + "]";
    }
 

 
    @Override
    public int getLength() {
        
        return Array.getLength(array);
    }

    @Override
    public Object getCurrentObject() {
       
        return Array.get(array, nextIndex);
    }
 
}