package com.foresee.xdeploy.file.rule;

public class PackageType {
    public static final String Type_WAR = "WAR";
    public static final String Type_JAR = "JAR";
    public static final String Type_CHG = "CHG";
    public static final String Type_NON = "NON";    //加入不发布类型（只进入基线，不发布版本）
    
    
    /**
     * @return 获取该文件转换器的路径类型 war、jar、chg
     */
    public static String getPathType(String srcPath) {
        if (srcPath.indexOf("-web") > 0 )
            return Type_WAR;
        else if (srcPath.lastIndexOf(".project") > 0)
            return Type_NON;
        else if (srcPath.indexOf("-java") > 0 ||srcPath.lastIndexOf(".java") > 0 )
            return Type_JAR;
        else
            return Type_CHG;
    }


}
