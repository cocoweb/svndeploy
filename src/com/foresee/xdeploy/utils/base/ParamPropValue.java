package com.foresee.xdeploy.utils.base;

import java.util.Iterator;
import java.util.Properties;

import com.foresee.test.util.exfile.ExtProperties;
import com.foresee.test.util.lang.StringUtil;

public class ParamPropValue extends BasePropValue {

	private static Properties argsProp = null;

	/**
	 * @return the argsProp
	 */
	public static Properties getArgsProp() {
	    return argsProp;
	}

	/**
	 * @param argsProp the argsProp to set
	 */
	public static void setArgsProp(Properties argsProp) {
		ParamPropValue.argsProp = argsProp;
	}

	public ParamPropValue(String strFileName) {
		super(strFileName);
        savePara( getExProp());
	}

	/**
	 * 保存properties中键值作为参数，{xxx}既可以使用
	 * @param extprop
	 */
	protected void savePara(ExtProperties extprop) {
	
	    Iterator<Object> iter = extprop.keySet().iterator();
	
	    while (iter.hasNext()) {
	        String skey = StringUtil.trim(iter.next().toString());
	        save_string(StringUtil.trim(extprop.getProperty(skey)), skey);
	    }
	
	}

	/**
	 * 使用{xxx}进行参数替换，并返回值
	 * @param key
	 */
	@Override
	public String getProperty(String key) {
	    String sValue = "";
	    if (argsProp!=null)  //参数值保存下来,可以覆盖参数文件的内容
	        sValue = argsProp.getProperty(key,"");
	    
	    if (sValue.isEmpty())  
	       sValue = eval_string(StringUtil.trim(super.getProperty(key)));
	    
	     return sValue;
	}
}