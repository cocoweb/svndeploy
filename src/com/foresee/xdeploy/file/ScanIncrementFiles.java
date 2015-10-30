package com.foresee.xdeploy.file;

import static com.foresee.xdeploy.file.ScanIncrementFiles.ExcelCols.ColExcel_Man;
import static com.foresee.xdeploy.file.ScanIncrementFiles.ExcelCols.ColExcel_Path;
import static com.foresee.xdeploy.file.ScanIncrementFiles.ExcelCols.ColExcel_ProjPackage;
import static com.foresee.xdeploy.file.ScanIncrementFiles.ExcelCols.ColExcel_ROWNo;
import static com.foresee.xdeploy.file.ScanIncrementFiles.ExcelCols.ColExcel_Ver;
import static com.foresee.xdeploy.file.ScanIncrementFiles.ListCols.ColList_Path;
import static com.foresee.xdeploy.file.ScanIncrementFiles.ListCols.ColList_ProjPackage;
import static com.foresee.xdeploy.file.ScanIncrementFiles.ListCols.ColList_Ver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.foresee.test.util.exfile.POIExcelUtil;
import com.foresee.test.util.io.File2Util;
import com.foresee.test.util.lang.StringUtil;
import com.foresee.xdeploy.utils.PathUtils;
import com.foresee.xdeploy.utils.excel.ExcelMoreUtil;
import com.foresee.xdeploy.utils.excel.ExcelMoreUtil.IHandleCopyRow;
import com.foresee.xdeploy.utils.excel.ExcelMoreUtil.IHandleScanRow;
import com.foresee.xdeploy.utils.excel.POIExcelMakerUtil;

/**
 * @author allan.xie 根据清单生成 版本号和路径
 *
 */
public class ScanIncrementFiles {
    public static final String SheetName = "功能清单";

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

    public ArrayList<String> fileList = new ArrayList<String>();

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
                return (o1.get(ColList_ProjPackage) + o1.get(ColList_Path) + o1.get(ColList_Ver)).compareTo(o2
                        .get(ColList_ProjPackage) + o2.get(ColList_Path) + o2.get(ColList_Ver));
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
            if (RowNo >= excelStartRow && !StringUtil.isEmpty(aRow.get(ColExcel_Path))) {
                // ArrayList<String> xrow = new ArrayList<String>();

                // 判断是否包含多个文件分隔
                if (aRow.get(ColExcel_Path).contains("\n")) {
                    for (String xfield : handlePathList(aRow.get(ColExcel_Path))) {
                        retList.add(handleLine(aRow.get(ColExcel_Ver), xfield, aRow.get(ColExcel_ProjPackage),
                                aRow.get(ColExcel_Man), xfile.getName()));
                    }

                } else {
                    retList.add(handleLine(aRow.get(ColExcel_Ver), aRow.get(ColExcel_Path),
                            aRow.get(ColExcel_ProjPackage), aRow.get(ColExcel_Man), xfile.getName()));
                }

            }
            RowNo++;
        }

        return retList;
    }
    
    public SvnFiles loadSvnFiles(File xfile) {
        final SvnFiles svnfiles = new SvnFiles();
        final String filename = xfile.getName();
        
        try {
            ExcelMoreUtil.scanExcelData(xfile.getPath(), SheetName, new IHandleScanRow(){
                HSSFRow localrow;

                private String getValue(int col){
                    return  POIExcelMakerUtil.getCellValue(localrow.getCell(col)).toString();
                }
                
                @Override
                public void handleRow(HSSFRow row, HSSFWorkbook fromWB) {
                    localrow =row;
                    
                    if ( !StringUtil.isEmpty(getValue(ColExcel_Path))) {
                        // ArrayList<String> xrow = new ArrayList<String>();

                        // 判断是否包含多个文件分隔
                        if (getValue( ColExcel_Path).contains("\n")) {
                            for (String xfield : handlePathList(getValue(ColExcel_Path))) {
                                svnfiles.addItem(getValue(ColExcel_Ver), xfield, getValue(ColExcel_ProjPackage),
                                        getValue(ColExcel_Man), filename);
                            }

                        } else {
                            svnfiles.addItem(getValue(ColExcel_Ver)
                                    , getValue(ColExcel_Path)
                                    , getValue(ColExcel_ProjPackage)
                                    , getValue(ColExcel_Man)
                                    , filename);
                        }

                    }
                   
                    
                }

                @Override
                public int skipRow() {
                    return 2;
                }
                
            });
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return svnfiles;
        
    }
    
    public  List<ArrayList<String>> loadSvnFilesList(File xfile) {
        
        return loadSvnFiles(xfile).SvnFileList;
        
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

    private static String handlePath(String sPath) {

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

    public static int iExcelRowCount = 1;

    public static ScanIncrementFiles scanListfile(String excelfile, String excelFolder, String scanOption,
            String xfilter, String tofilename) {
        ScanIncrementFiles xscanfiles = new ScanIncrementFiles(excelfile, excelFolder, scanOption);
        xscanfiles.retList = xscanfiles.loadExcelFiles(xfilter);

        if (!tofilename.isEmpty()) {

            try {
                // 生成excel
                for (String sfile : xscanfiles.fileList) {

                    ExcelMoreUtil.copyExcelDataToFile(sfile, tofilename, SheetName, new IHandleCopyRow() {
                        // copy row 本地代码实现回调

                        @Override
                        public void handleRow(HSSFRow targetRow, HSSFRow sourceRow, HSSFWorkbook targetWork,
                                HSSFWorkbook sourceWork) {
                            for (int i = sourceRow.getFirstCellNum(); i <= sourceRow.getLastCellNum(); i++) {
                                HSSFCell sourceCell = sourceRow.getCell(i);
                                HSSFCell targetCell = targetRow.getCell(i);

                                if (sourceCell != null) {
                                    if (targetCell == null) {
                                        targetCell = targetRow.createCell(i);
                                    }

                                    switch (i) { // 根据列号进行处理
                                    case ColExcel_ROWNo:
                                        targetCell.setCellValue(iExcelRowCount);
                                        break;
                                    case ColExcel_Path:
                                        targetCell.setCellValue(handlePath(sourceCell.getStringCellValue()));
                                        break;
                                    default:
                                        // 拷贝单元格，包括内容和样式
                                        ExcelMoreUtil.copyCell(targetCell, sourceCell, targetWork, sourceWork, null);

                                    }

                                }
                            }

                            iExcelRowCount++; // 行计数

                        }

                    });

                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return xscanfiles;
    }

    public static List<ArrayList<String>> scanListfile(String excelfile, String excelFolder, String scanOption,
            String xfilter) {
        ScanIncrementFiles xx = new ScanIncrementFiles(excelfile, excelFolder, scanOption);
        List<ArrayList<String>> retList = xx.loadExcelFiles(xfilter);

        // try {
        // // 生成excel
        // for (String sfile : xx.fileList) {
        // ExcelMoreUtil.copyExcelDataToFile(sfile,
        // "p:/因开发所致环境变更记录表模版 - 副本.xls");
        //
        // }
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }

        return retList;
    }

    public static void main(String[] args) throws Exception {
        ScanIncrementFiles xx = new ScanIncrementFiles("p:/因开发所致环境变更记录表模版-20150820-产品线-合并.xls", "P:\\workspace\\xls",
                BATCH);
        // for(ArrayList<String> aRow:xx.loadExcelFile()){
        // System.out.println(aRow );
        // }

        //xx.mergeListfile("p:/xxx.xls", "20150828");
        
        System.out.println(xx.loadSvnFilesList(new File("p:/因开发所致环境变更记录表模版-20150922-产品线-合并.xls")));

    }

}
