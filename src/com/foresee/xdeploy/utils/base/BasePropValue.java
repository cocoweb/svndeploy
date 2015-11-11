package com.foresee.xdeploy.utils.base;

import java.util.Map;

import com.foresee.test.loadrunner.lrapi4j.lr;
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

    public static String eval_string(String paramString) {
		return lr.eval_string(paramString);
	}


	public static int save_string(String sValue, String ParaName) {
		 
		return lr.save_string(sValue, ParaName);
	}


	public String getProperty(String key) {
        return exprop.getProperty(key);
    }

    public Map<String, String> getSectionItems(String sectionName) {
        return exprop.getSectionItems(sectionName);
    }

}