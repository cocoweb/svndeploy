package com.foresee.xdeploy.framework.iterator;

import java.util.Iterator;

public interface IObjectIterator extends Iterator<Object> {
    boolean nextObj();
    String getName();
    Object getValue();
    
}