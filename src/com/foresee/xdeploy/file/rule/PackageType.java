package com.foresee.xdeploy.file;

public interface PackageType {
    public static final String Type_WAR = "WAR";
    public static final String Type_JAR = "JAR";
    public static final String Type_CHG = "CHG";
    public static final String Type_NON = "NON";    //加入不发布类型（只进入基线，不发布版本）

}
