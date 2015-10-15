package com.foresee.xdeploy.utils.base;

import java.util.Iterator;

/**
 * @author allan.xie
 * 
 * F 类对象iterator ，转换为 T类对象 的iterator
 * 
 * 比如某列表 、集合类 list<String>, 转换为 某files类对象的集合
 *
 * <br/>包装模式，比如：
 *  new SvnFilesIterator( SvnFileList.iterator())
 * @param <T>
 * @param <F>
 */
public abstract class BaseSwitchIterator<T,F> implements Iterator<T> {
        private Iterator<F> itor = null;

        public BaseSwitchIterator(Iterator<F> xiterator) {
            itor = (Iterator<F>) xiterator;
            
        }
        @Override
        public boolean hasNext(){
            
            return itor.hasNext();
        }
        public void remove(){
            itor.remove();
        }
        
        @Override
        public  T next(){
            return   switchObject(itor.next());
        }
        
        public abstract T switchObject(F xobj) ;
    }

