package com.foresee.xdeploy.file.base;

import java.util.Arrays;
import java.util.List;

import com.foresee.test.util.lang.StringUtil;
import com.foresee.xdeploy.utils.PathUtils;

public abstract class XdeployBase {

    public interface ListCols {
        // List列字段序号
        public static final int ColList_Ver = 0;
        public static final int ColList_Path = 1;
        public static final int ColList_ProjPackage = 2;
        public static final int ColList_Man = 3;
        public static final int ColList_FileName = 4;
    
    }

    public interface ExcelCols {
        // Excel列字段序号
        public static final int ColExcel_ROWNo = 0;
        public static final int ColExcel_Ver = 3;
        public static final int ColExcel_Path = 6;
        public static final int ColExcel_ProjPackage = 7;
        public static final int ColExcel_Man = 13;
    
    }

    public static final String LIST_Project = "WEBProject";
    
    public static final String LIST_FileName = "FILEName";


    public static String handlePath(String sPath) {
    
        // 截取到Trunk
        return PathUtils.addFolderStart(StringUtil.trim(sPath));
    }


    public static List<String> handlePathList(String sPath) {
        String[] xstr = StringUtil.split(sPath);
        return Arrays.asList(xstr);
    }

    public static String handleProjectName(String xname) {
        // xname = xname.replaceAll( "[\\p{P}+~$`^=|<>～｀＄＾＋＝｜＜＞￥×^_]" , ",");
        // //StringUtil.replaceAll(xname,"、",",");
        xname = xname.replaceAll("[、，]", ","); // StringUtil.replaceAll(xname,"、",",");
        xname = xname.replaceAll("[_]", "-");
        return StringUtil.trim(xname);
    }

    public static String handleVerNo(String sVerNo) {
        return StringUtil.trim(sVerNo, "#");
    }


}