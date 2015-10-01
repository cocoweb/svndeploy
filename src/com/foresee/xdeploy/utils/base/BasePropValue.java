package com.foresee.xdeploy.utils.base;

import java.util.Map;

import com.foresee.test.util.exfile.ExtProperties;

public abstract class BasePropValue {

    private static ExtProperties exprop = null;
 

    /**
     * @param xprop the xprop to set
     */
    public static void setExProp(ExtProperties xprop) {
        BasePropValue.exprop = xprop;
    }

    public String propFileName = "";

    public BasePropValue(String strFileName) {
        super();
        propFileName = strFileName;
        exprop = ExtPropertiesFactory.getExtPropertiesInstance(propFileName,exprop);
    }
    

    public static ExtProperties getExProp() {
        return exprop;
    }

    public String getProperty(String key) {
        return exprop.getProperty(key);
    }

    public Map<String, String> getSectionItems(String sectionName) {
        return exprop.getSectionItems(sectionName);
    }

}