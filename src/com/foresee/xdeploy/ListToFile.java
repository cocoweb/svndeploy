package com.foresee.xdeploy;

import com.foresee.test.util.lang.DateUtil;
import com.foresee.xdeploy.file.ScanIncrementFiles;

public class ListToFile {
    public ListToFile() {
    }

    
    public static void echoCommandInfo() {
        
        System.out.println("We need some arguments!");
        System.out.println("Example：java ListToFile.class FROMSVN");
        System.out.println("  args1 = []|[LIST]|[FROMSVN]|[FROMCI][DIFFVER][help]");
        System.out.println("          []       :无参数时同LIST，显示待处理文件清单和版本");
        System.out.println("          [LIST]   :显示待处理文件清单和版本");
        System.out.println("          [FROMSVN]:从svn库svn.tofolder导出到临时目录svn.tofolder，或者workspace ,or ZIP");
        System.out.println("          [FROMZIP]:从指定压缩文件war、zip、jar 导出到临时目录");
        System.out.println("          [FROMCI] :从指定目录ci.workspace 导出到临时目录ci.tofolder;  其中的.java 将替换为.class");
        System.out.println("          [DIFFVER]: 根据起始版本号svndiff.startversion  svndiff.endversion，获取文件清单；从指定目录ci.workspace 导出到输出目录ci.tofolder;  其中的.java 将替换为.class");
        System.out.println("          [help]   :显示这里的信息");

        System.out.println("  args2 = []|[BATCH]|[FILE]");
        System.out.println("          []       :默认批量BATCH处理excel，扫描目录file.excel.folder下的所有");
        System.out.println("          [BATCH]  :批量处理excel，扫描目录file.excel.folder下的所有");
        System.out.println("          [FILE]   :批量处理excel，扫描目录file.excel.folder下的所有");

        System.out.println("  args3 = Properties File Name, Default is [svntools.properties]");

    }
    
    public static void actionOptions(String[] args){
        // 根据参数设置Properties文件
        ToFileHelper listTofileHelper =  args.length > 2 ? new ToFileHelper(args[2]) : new ToFileHelper();
        
        if ((args.length == 0) || "LIST".equals(args[0].toUpperCase())) {
            listTofileHelper.pv.scanOption = args.length > 1 ? args[1] : ScanIncrementFiles.BATCH;
            
            System.out.println("===========显示待处理文件清单=================");
            // xListtofile.scanOption = "FILE";
            listTofileHelper.scanPrintList();

        } else if (args.length > 0 && "HELP".equals(args[0].toUpperCase())) {
            echoCommandInfo();
        } else if (args.length > 0) {
            String cmdOption = args[0];

            listTofileHelper.pv.scanOption = args.length > 1 ? args[1] : ScanIncrementFiles.BATCH;  //单文件or 批量

            if (cmdOption.toUpperCase().equals("FROMSVN")) { // 从svn库导出到临时目录，或者workspace

                System.out.println("===========从svn库导出到临时目录，或者workspace=================");

                listTofileHelper.scanSvnToPath();

            } else if (cmdOption.toUpperCase().equals("FROMCI")) { // //从指定目录
                                                                   // 导出到临时目录，或者workspace
                System.out.println("===========从指定目录 导出到临时目录，或者workspace=================");
                listTofileHelper.scanWorkspaceToPath();

            } else if (cmdOption.toUpperCase().equals("DIFFVER")) { // //从指定目录
                System.out.println("===========根据起始版本号svndiff.startversion  svndiff.endversion，获取文件清单=================");
                listTofileHelper.svnDiffToPath();
            } else if (cmdOption.toUpperCase().equals("FROMZIP")) { // //从指定目录
                System.out.println("===========从指定压缩文件war、zip、jar 导出到临时目录=================");
                listTofileHelper.scanZipToPath();
            } else
                echoCommandInfo();
                
        }else
          echoCommandInfo();

    }

    public static void main(String[] args) {
        
        actionOptions(args);
        
        System.out.println("Run at >>> "+DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
        
        // System.out.print(PropFile.getExtPropertiesInstance().getProperty("file.excel"));

    }

}
