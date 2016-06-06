#Mode I

**usage: ListToFile -s | -d | -h | -z | -l     [-P <name=value> | --propfile <PropertiesFile>]  [-B | --befile]**

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
    
    
#Mode II    

**Example：java ListToFile.class FROMSVN**

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

##TODO:

	1、svn自动迁移基线，update，export，checkin
	2、日志、输出文件优化
	3、比对检查zip、excel

##Release Note
    V0.39-201606
    1、修改版本号为空时的检查提示
    2、完善Keyroot逻辑
    3、完善制动 commit逻辑
    
    
    V0.38-20160529
     分离出mapping.properties文件，减少误操作
    重构相关代码

    V0.37-20160528
            添加jar包容错，加入mappingx增强   
    mappingx.w.META-INF.{WEBProject}   =/web/{WEBProject}/src/META-INF|{WEBProject}/WEB-INF/classes/META-INF
	mappingx.w.{WEBProject}0            ={WEBProject}/WebContent|{WEBProject}
	mappingx.w.{WEBProject}1            ={WEBProject}/src|{WEBProject}
	mappingx.j.{JARName}             ={JARName}/src|{JARName}

    
    v0.36-20160526
            修改版本号排序问题
    

	v0.34-20160514
	1、修改版本字符，后utf8空字符处理
	2、完善Diff版本差异文件输出
	
	v0.32-20160427
	改善窗口显示性能
	
	v0.31-20160426
	1、增加Mappingx扩展未知war包的处理
	mappingx.w.META-INF.warname   =/web/{WEBProject}/src/META-INF|{WEBProject}/WEB-INF/classes/META-INF
	mappingx.w.warname0            ={WEBProject}/WebContent|{WEBProject}
	mappingx.w.warname1            ={WEBProject}/src|{WEBProject}
	
	2、增加抽取jar包的保存文件路径处理
	mapping.c.repository         =src/repository/com.foresee/public/repository|{WEBProject}/WEB-INF/libs/{FILEName}
	
	
	
	v0.30-20160425
	增加mapping路径参数化功能，  {WEBProject}为excel的工程名
	mapping.c.repository     =src/repository/com.foresee/public/repository|{WEBProject}/WEB-INF/libs
	
	v0.29-20160422
	1、列出清单时，加入web工程判断，是否正确
	2、fromzip目录不做处理,改善错误日志输出
	
	v0.28-20160421
	修改路径匹配是报index错误问题
	
	v0.27-20160408
	1、fromsvn/fromzip时自动清单去重  
	2、fromsvn/fromzip 及窗口模式下的zip文件重复的问题
	3、增加路径转换 mapping.c; fromzip直接从svn获取文件
	      #路径转换
	     mapping.c.vfs_home                =src/portal/vfs_home|vfs_home
	     
	4、mapping搜索顺序：c.-w.META-INF-w.-j.
	    #w.META-INF, 从war中抽取文件（源代码编译打包后，路径发生了变化）
		mapping.w.META-INF.gt3nf-admin    =/portal/web/gt3nf-admin/src/META-INF|gt3nf-admin/WEB-INF/classes/META-INF
		mapping.w.META-INF.remind-web    =/portal/web/remind-web/src/META-INF|remind-web/WEB-INF/classes/META-INF
		mapping.w.META-INF.gt3nf-portal    =/portal/web/gt3nf-portal/src/META-INF|gt3nf-portal/WEB-INF/classes/META-INF
	   
	5、重构zip、war文件的处理
	
	
	v0.26-20160405
	修改清单第一个无效，导致无法导出svn问题
	加入web工程检查，定义在配置文件中的  #package相关，比如war、jar   package.list=
	
	v0.24-
	war包导出增量几个问题：
	1、多个工程要有多个文件，从哪个包里来的，放到哪个目录去（依赖清单web工程字段）
	2、war包中的jar抽取bug问题
	
	v0.23-20160321
	可手动、制动提交svn代码
	需设定指向svn workspace
	启动时无参数即打开窗口界面
	
	v0.22-20160316
	增加版本号检查开关，当版本号为空时，取最新的版本
	增加按钮型功能
	
	v0.21-20160312
	jar执行后，会弹出一个窗口，日志输出到窗口
	在执行完毕后，关闭窗口会写入到日志文件中
	文件定义在log4j.properties
	
	v0.20-20160226
	修改合并excel文件bug
	
	v0.19-20160122
	修改清单排序和查重——路径、版本、包名（原来是：包名、路径、版本）
	
	v0.18-20151102
	修改，排除excel空行中有空格的情况
	
	v0.17-20151101
	增加输出svn时的版本检查，如果文件没有对应的版本号，将提示并跳过该文件
	
	v0.15-20150927
	1、从war输出zip中，包含java源文件
	2、全参数化
	3、修改报keyroot 的错误
	4、兼容 -h  的命令行模式
	
	
	v0.13-20150925
	1、修改生成zip的bug
	2、添加mapping
	
	v0.12-20150924
	1、实现从war提取文件
	2、改善部分提示信息
	3、mapping配置增加了 j.  w.  的样式，代表 jar、war
	  
	v0.11-20150922
	1、优化了 FROMZIP，可以直接从多个war包提取文件，直接输出到ZIP
	2、优化ZIP文件名
	3、配置文件支持{xxx}模板变量方式
	  
	v0.10-20150921
	1、增加增量包mapping配置
	2、重构excel合并处理，
	   允许开启 file.excel.merge=true 
	   重置行号、路径格式
	  
	v0.9-20150919
	1、建立路径到发布包映射配置
	2、直接生成增量发布包 zip 文件
	   svn.tozip.enabled=true
	   zip.tofolder=p:/tmp/e
	
	   使用java ListToFile.class FROMSVN 时，生成zip
	  
	v0.8-20150909
	1、自动按照模板合并excel文件 
	配置文件需要加入 file.excel.template=p:/因开发所致环境变更记录表模版.xls
	
	v0.7-20150901
	1、列表提示存在空目录   <<< 注意 >>> 清单包含有目录：
	
	v0.6-20150828
	1、添加日志输出时间
	2、兼容excel单行包含多个文件
	
	v0.5-20150826
	1、列表显示时，加上提交人姓名
	2、增加excel地址列容错
	3、优化排序，避免高版本被低版本覆盖
	
	v0.4-20150825
	1、增加列表显示时，文件重复报告
	
	v0.3
	1、增加zip文件提取
	2、实现输出清单按照工程名、路径名排序
	3、忽略excel文件中的空行
	4、优化keyroot处理
	   #下面代表着读取清单路径时,目录开头=/[file.keyroot]/...=/engineering/...  (包括LIST显示)
	file.keyroot=engineering
	   #下面代表着输出目录=[svn.tofolder]/[svn.keyroot]=p:/tmp/d/engineering
	svn.tofolder=p:/tmp/d
	svn.keyroot=engineering
	   #下面代表着生成版本差异清单时,目录开头=[svndiff.keyroot]/...=src/ 
	svndiff.keyroot=src
	
	v0.2-20150825   修改：
	1、增加DIFFVER 版本增量清单输出
	2、修改keyroot处理BUG
