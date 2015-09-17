package com.foresee.xdeploy.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.foresee.test.util.exfile.POIExcelUtil;
import com.foresee.test.util.io.File2Util;
import com.foresee.test.util.lang.StringUtil;
import com.foresee.xdeploy.utils.PathUtils;

/**
 * @author allan.xie 根据清单生成 版本号和路径
 *
 */
public class ScanIncrementFiles {
    private static final String SheetName = "功能清单";

    ArrayList<String> fileList = new ArrayList<String>();

    public static final String BATCH = "BATCH";
    public static final String FILE = "FILE";

    public String sfilePath;
    public String sFolderPath;

    public String scanOption = BATCH; // 默认批量扫描

    public int excelStartRow = 2; // excel 起始行

    public ScanIncrementFiles(String spath) {
        sfilePath = spath;
        scanOption = FILE;
    }

    public ScanIncrementFiles(String spath, String scanoption) {
        sFolderPath = spath;
        scanOption = scanoption;
    }

    public ScanIncrementFiles(String file, String folder, String scanoption) {
        sfilePath = file;
        sFolderPath = folder;
        scanOption = scanoption;
    }

    public List<ArrayList<String>> loadExcelFiles() {
        return loadExcelFiles("");
    }

    public List<ArrayList<String>> loadExcelFile() {
        System.out.println("Loading List >>> " + sfilePath);

        return loadExcelFile(new File(sfilePath));
    }

    public List<ArrayList<String>> loadExcelFiles(String sFilter) {
        List<ArrayList<String>> retList = null;

        if (scanOption.equals(FILE)) {
            retList = loadExcelFile();
        } else {
            retList = new ArrayList<ArrayList<String>>();

            // 遍历文件夹，并过滤
            Collection<File> clFiles = File2Util.getAllFiles(sFolderPath, sFilter);
            for (File xfile : clFiles) {
                System.out.println("Loading List >>> " + xfile.getPath());
                fileList.add(xfile.getPath());

                retList.addAll(loadExcelFile(xfile));
            }

        }
        // 排序返回的清单
        Collections.sort(retList, new Comparator<ArrayList<String>>() {
            @Override
            public int compare(ArrayList<String> o1, ArrayList<String> o2) {
                return (o1.get(2) + o1.get(1) + o1.get(0)).compareTo(o2.get(2) + o2.get(1) + o2.get(0));
            }

        });

        return retList;
    }

    private List<ArrayList<String>> readExcel(File xfile) {
        try {
            return POIExcelUtil.loadExcelFile(xfile, SheetName);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

    }

    private List<ArrayList<String>> loadExcelFile(File xfile) {
        List<ArrayList<String>> filecontent = readExcel(xfile);

        List<ArrayList<String>> retList = new ArrayList<ArrayList<String>>();

        int RowNo = 0;
        for (ArrayList<String> aRow : filecontent) {
            if (RowNo >= excelStartRow && !StringUtil.isEmpty(aRow.get(6))) {
                // ArrayList<String> xrow = new ArrayList<String>();

                // 判断是否包含多个文件分隔
                if (aRow.get(6).contains("\n")) {
                    for (String xfield : handlePathList(aRow.get(6))) {
                        retList.add(handleLine(aRow.get(3), xfield, aRow.get(7), aRow.get(13), xfile.getName()));
                    }

                } else {
                    retList.add(handleLine(aRow.get(3), aRow.get(6), aRow.get(7), aRow.get(13), xfile.getName()));
                }

            }
            RowNo++;
        }

        return retList;
    }

    private ArrayList<String> handleLine(String xver, String xpath, String xproj, String xman, String xfilename) {
        ArrayList<String> xrow = new ArrayList<String>();
        xrow.add(handleVerNo(xver)); // 版本号
        xrow.add(handlePath(xpath)); // 路径
        xrow.add(handleProjectName(xproj)); // 工程名
        xrow.add(handleProjectName(xman)); // 提交人
        xrow.add(xfilename); // 文件名

        return xrow;
    }

    private String handleProjectName(String xname) {
        // xname = xname.replaceAll( "[\\p{P}+~$`^=|<>～｀＄＾＋＝｜＜＞￥×^_]" , ",");
        // //StringUtil.replaceAll(xname,"、",",");
        xname = xname.replaceAll("[、，]", ","); // StringUtil.replaceAll(xname,"、",",");
        xname = xname.replaceAll("[_]", "-");
        return xname;
    }

    // 整理数据
    private String handleVerNo(String sVerNo) {
        return StringUtil.trim(sVerNo, "#");
    }

    private List<String> handlePathList(String sPath) {
        String[] xstr = StringUtil.split(sPath);
        return Arrays.asList(xstr);
    }

    private String handlePath(String sPath) {

        // 截取到Trunk
        return PathUtils.addFolderStart(StringUtil.trim(sPath));

        // int i = str.indexOf("/trunk");
        // if(i>=0){
        // return "/"+str.substring(i+1);
        // }else
        // return "/"+str;

    }
    
    public List<ArrayList<String>> retList;

    
    public List<ArrayList<String>> mergeListfile(String toExcelfile, String excelFolderFilter) {
        // 遍历文件夹，并过滤, String excelFolder, String excelFolderFilter
        Collection<File> clFiles = File2Util.getAllFiles(sFolderPath, excelFolderFilter);
        for (File xfile : clFiles) {
            System.out.println("Loading List >>> " + xfile.getPath());

            List<ArrayList<String>> filecontent = readExcel(xfile);
            // retList.addAll(loadExcelFile(xfile));
        }

        return null;
    }

    public static void main(String[] args) throws Exception {
        ScanIncrementFiles xx = new ScanIncrementFiles("p:/因开发所致环境变更记录表模版-20150820-产品线-合并.xls", "P:\\workspace\\xls",
                BATCH);
        // for(ArrayList<String> aRow:xx.loadExcelFile()){
        // System.out.println(aRow );
        // }

        xx.mergeListfile("p:/xxx.xls", "20150828");

    }

}
