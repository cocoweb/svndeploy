package com.foresee.xdeploy;

import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.foresee.test.util.lang.DateUtil;
import com.foresee.xdeploy.file.PropValue;
import com.foresee.xdeploy.file.ScanIncrementFiles;
import com.foresee.xdeploy.win.Mainwin;

public class ListToFile {
    static Logger log  = Logger.getLogger(ListToFile.class );
    
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
        System.out
                .println("          [DIFFVER]: 根据起始版本号svndiff.startversion  svndiff.endversion，获取文件清单；从指定目录ci.workspace 导出到输出目录ci.tofolder;  其中的.java 将替换为.class");
        System.out.println("          [help]   :显示这里的信息");

        System.out.println("  args2 = []|[BATCH]|[FILE]");
        System.out.println("          []       :默认批量BATCH处理excel，扫描目录file.excel.folder下的所有");
        System.out.println("          [BATCH]  :批量处理excel，扫描目录file.excel.folder下的所有");
        System.out.println("          [FILE]   :批量处理excel，扫描目录file.excel.folder下的所有");

        System.out.println("  args3 = Properties File Name, Default is [svntools.properties]");

    }

    public static void actionOptions(String[] args) {
        // 根据参数设置Properties文件
        ToFileHelper listTofileHelper = args.length > 2 ? new ToFileHelper(args[2]) : new ToFileHelper();

        if ((args.length == 0) || "LIST".equals(args[0].toUpperCase())) {
            listTofileHelper.pv.scanOption = args.length > 1 ? args[1] : ScanIncrementFiles.BATCH;
            // xListtofile.scanOption = "FILE";
            listTofileHelper.scanPrintList();

        } else if (args.length > 0) {
            String cmdOption = args[0];

            listTofileHelper.pv.scanOption = args.length > 1 ? args[1] : ScanIncrementFiles.BATCH; // 单文件or
                                                                                                   // 批量

            if (cmdOption.toUpperCase().equals("FROMSVN")) { // 从svn库导出到临时目录，或者workspace
                listTofileHelper.scanSvnToPath();
            } else if (cmdOption.toUpperCase().equals("FROMCI")) { // //从指定目录
                                                                   // 导出到临时目录，或者workspace
                listTofileHelper.scanWorkspaceToPath();
            } else if (cmdOption.toUpperCase().equals("DIFFVER")) { // //从指定目录
                listTofileHelper.svnDiffToPath();
            } else if (cmdOption.toUpperCase().equals("FROMZIP")) { // //从指定目录
                listTofileHelper.scanWarToZip(); // scanZipToPath();
            } else
                echoCommandInfo();
        } else if (args.length > 0 && "HELP".equals(args[0].toUpperCase())) {
            echoCommandInfo();
        } else
            echoCommandInfo();

    }

    public static Options cmdCLIOptions() {
        Options options = new Options();

        // options.addOption(Option.builder("").desc(
        // "Command is :LIST or FROMSVN or FROMZIP" ).hasArg()
        // .argName("Command").required().build());

        OptionGroup og1 = new OptionGroup();
        og1.setRequired(true);
        og1.addOption(Option.builder("h").longOpt("help").desc("显示这里的参数信息").hasArg(false).required(false).build());
        og1.addOption(Option.builder("l").longOpt("list").desc("for list; 显示待处理文件清单和版本").hasArg(false).required(false).build());
        og1.addOption(Option.builder("s").longOpt("fromsvn")
                .desc("from svn to; 从svn库svn.tofolder导出到临时目录svn.tofolder，或者workspace").hasArg(false).required(false).build());
        og1.addOption(Option.builder("z").longOpt("fromzip").desc("from zip to; 从指定压缩文件war、zip、jar 导出到临时目录").hasArg(false)
                .required(false).build());
        og1.addOption(Option
                .builder("d")
                .longOpt("fordiffver")
                .desc("for version diffrent; 根据起始版本号svndiff.startversion  svndiff.endversion，获取文件清单；从指定目录ci.workspace 导出到输出目录ci.tofolder;  其中的.java 将替换为.class")
                .hasArg(false).required(false).build());
        options.addOptionGroup(og1);

        OptionGroup og0 = new OptionGroup();
        og0.setRequired(false);
        og0.addOption(Option.builder("P").desc("Use value for given property: -Pp1=v1 -Pp2=v2 ...").argName("name=value")
                .valueSeparator().hasArgs().optionalArg(false).required(false).build());
        og0.addOption(Option.builder().longOpt("propfile").desc("Properties File Name, Default is [svntools.properties]")
                .hasArg(true).argName("PropertiesFile").optionalArg(false).required(false).build());
        options.addOptionGroup(og0);

        OptionGroup og2 = new OptionGroup();
        og2.setRequired(false);
        og2.addOption(Option.builder("B").longOpt("bebatch").desc("[默认]批量处理excel，扫描目录file.excel.folder下的所有").build());
        og2.addOption(Option.builder().longOpt("befile").desc("指定excel文件，扫描file.excel清单").build());
        options.addOptionGroup(og2);

        return options;

    }

    static HelpFormatter hf = new HelpFormatter();

    public static void helpPrint(Options options) {

        hf.setWidth(120);
        hf.setOptionComparator(new Comparator<Option>() { // 比较排序参数

            @Override
            public int compare(Option arg0, Option arg1) {
                // return arg0.getKey().compareToIgnoreCase(arg1.getKey());

                // 返回0，直接使用加入options的顺序
                return 0;
            }
        });

        hf.setLeftPadding(4);
        hf.setDescPadding(1);
        hf.printHelp("ListToFile", "  参数说明：", options
              , "\n  Example: \n"
                + "    java ListToFile --fromsvn or  java ListToFile -s \n"
                + "    java ListToFile --list    or  java ListToFile -l\n" 
                + "    java ListToFile -l -propfile custom.properties\n"
                + "    java ListToFile -s -Pworkspace=d:/gogo -Pfile.excel=d:/gogo/aaa.xls\n" 
                ,true);

    }

    public static void parserCLICmd(Options options, String[] args) {

        CommandLine cmds = null;
        Properties prop = null;
        DefaultParser parser = new DefaultParser();
        try {

            // ListToFile [-B | --befile] -d | -h | -l | -s | -z [-P
            // <name=value> | --propertiesfile <PropertiesFile>]
            cmds = parser.parse(options, args, prop, true);

            if (cmds.hasOption('h')) {
                // 打印使用帮助
                helpPrint(options);
                //return;
            }

            ToFileHelper listTofileHelper = null;

            // 属性组 [-P <name=value> | --propertiesfile <PropertiesFile>]
            if (cmds.hasOption("propfile")) {
                listTofileHelper = new ToFileHelper(cmds.getOptionValue("propfile"));

            } else {
                listTofileHelper = new ToFileHelper();

                if (cmds.hasOption("P")) {  //提取参数
                    PropValue.setArgsProp(cmds.getOptionProperties("P"));

                    //System.out.println(listTofileHelper.pv.argsProp);
                }
            }

            // 是否批量清单 [-B | --befile]
            if (cmds.hasOption("befile")) {
                listTofileHelper.pv.scanOption = ScanIncrementFiles.FILE;
            } else
                listTofileHelper.pv.scanOption = ScanIncrementFiles.BATCH;

            // 互斥命令组 -d | -h | -l | -s | -z
            if (cmds.hasOption('d')) {
                listTofileHelper.svnDiffToPath();
            } else if (cmds.hasOption('l')) {
                listTofileHelper.scanPrintList();
            } else if (cmds.hasOption('s')) {
                listTofileHelper.scanSvnToPath();
            } else if (cmds.hasOption('z')) {
                listTofileHelper.scanWarToZip();
            }
            
            System.out.println("   args:  svn.url            ="+listTofileHelper.pv.getProperty("svn.url"));
            System.out.println("   args:  file.excel.merge   ="+listTofileHelper.pv.getProperty("file.excel.merge"));

            // 打印opts的名称和值
            System.out.println("--------------------------------------");
            Option[] opts = cmds.getOptions();
            if (opts != null) {
                for (Option opt1 : opts) {
                    String name = opt1.getOpt();
                    String lname= opt1.getLongOpt();
                    if (StringUtils.isEmpty(lname))
                        lname = name;
                    String value = cmds.getOptionValue(lname);
                    System.out.println(lname + "=>" + value);
                }
            }

            String[] xargs = cmds.getArgs();
            if (Array.getLength(xargs) > 0) {
                System.out.println("Arguments : + Arrays.toString(xargs)" + xargs[0]);
            }

        } catch (ParseException e) {
            String mess = e.getLocalizedMessage();
            hf.printHelp("Error==>" + mess + "\ntestAPP", options, true);
            // hf.printHelp("\nError==>"+e.getLocalizedMessage(),);
            // System.out.println("\nError==>"+e.getLocalizedMessage());
            // e.printStackTrace();.substring(0,mess.indexOf("[")-2)

        }

    }

    public static void main(String[] args) {
        //ConsoleTextArea.showForm();
        //ConsoleString.createConsole();
        
        if (args.length <=0 ){
            Mainwin.showForm();
        }else if(args[0].startsWith("-")) {
            // 使用带- 的命令行模式
            parserCLICmd(cmdCLIOptions(), args);
        } else
            actionOptions(args);

        System.out.println("\nRun at >>> " + DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"));

        // System.out.print(PropFile.getExtPropertiesInstance().getProperty("file.excel"));
        
       //log.info(ConsoleString.Content.toString()); 
       //log.info(ConsoleTextArea.getString());
       //System.exit(0);

    }

}
