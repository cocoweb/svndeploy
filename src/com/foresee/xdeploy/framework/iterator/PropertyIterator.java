package com.foresee.xdeploy.framework.iterator;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

public class PropertyIterator extends ObjectIterator{
    
    private final Object object;
    private final PropertyDescriptor[] properties;
    private PropertyDescriptor currentProperty;
 
    public PropertyIterator(Object object) {
        this.object = object;
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass());
            properties = beanInfo.getPropertyDescriptors();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
 
 
    @Override
    public String getName() {
        if (currentProperty == null) {
            return null;
        }
        return currentProperty.getName();
    }
 
    @Override
    public Object getValue() {
        try {
            if (currentProperty == null) {
                return null;
            }
            return currentProperty.getReadMethod().invoke(object);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    @Override
    public int getLength() {
        return properties.length;
    }

    @Override
    public Object getCurrentObject() {
        currentProperty = properties[nextIndex];
        if (currentProperty.getReadMethod() == null || "class".equals(currentProperty.getName())) {
            return nextObj();
        }
        //currentElement = currentProperty;
        return currentProperty;
    }


 
}