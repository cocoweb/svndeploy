fsdeploy
================

ListToFile -s|-d|-h|-z|-l  [-P <name=value> | --propfile <PropertiesFile>]  [-B | --befile]
  参数说明：
    -s,--fromsvn                   from svn to; 从svn库svn.tofolder导出到临时目录svn.tofolder，或者workspace
    -d,--fordiffver                for version diffrent; 根据起始版本号svndiff.startversion
                                   svndiff.endversion，获取文件清单；从指定目录ci.workspace 导出到输出目录ci.tofolder;  其中的.java 将替换为.class
    -h,--help                      显示这里的参数信息
    -z,--fromzip                   from zip to; 从指定压缩文件war、zip、jar 导出到临时目录
    -l,--list                      for list; 显示待处理文件清单和版本
    -P <name=value>                Use value for given property: -Pp1=v1 -Pp2=v2 ...
       --propfile <PropertiesFile> Properties File Name, Default is [svntools.properties]
    -B,--bebatch                   [默认]批量处理excel，扫描目录file.excel.folder下的所有
       --befile                    指定excel文件，扫描file.excel清单

  Example:
    java ListToFile --fromsvn or  java ListToFile -s
    java ListToFile --list    or  java ListToFile -l
    java ListToFile -l -propfile custom.properties
    java ListToFile -s -Pworkspace=d:/gogo -Pfile.excel=d:/gogo/aaa.xls

========
Example：java ListToFile.class FROMSVN
  args1 = []|[LIST]|[FROMSVN]|[FROMCI][DIFFVER][help]
          []       :无参数时同LIST，显示待处理文件清单和版本
          [LIST]   :显示待处理文件清单和版本
          [FROMSVN]:从svn库svn.tofolder导出到临时目录svn.tofolder，或者workspace
          [FROMZIP]:从指定压缩文件war、zip、jar 导出到临时目录
          [FROMCI] :从指定目录ci.workspace 导出到临时目录ci.tofolder;  其中的.java 将替换为.class
          [DIFFVER]: 根据起始版本号svndiff.startversion  svndiff.endversion，获取文件清单；从指定目录ci.workspace 导出到输出目录ci.tofolder;  其中的.java 将替换为.class
          [help]   :显示这里的信息
  args2 = []|[BATCH]|[FILE]
          []       :默认批量BATCH处理excel，扫描目录file.excel.folder下的所有
          [BATCH]  :批量处理excel，扫描目录file.excel.folder下的所有
          [FILE]   :批量处理excel，扫描目录file.excel.folder下的所有
  args3 = Properties File Name, Default is [svntools.properties]
