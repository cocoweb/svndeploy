package com.foresee.xdeploy.file.testng;

import static com.foresee.xdeploy.file.ScanIncrementFiles.ListCols.ColList_FileName;
import static com.foresee.xdeploy.file.ScanIncrementFiles.ListCols.ColList_Man;
import static com.foresee.xdeploy.file.ScanIncrementFiles.ListCols.ColList_Path;
import static com.foresee.xdeploy.file.ScanIncrementFiles.ListCols.ColList_ProjPackage;
import static com.foresee.xdeploy.file.ScanIncrementFiles.ListCols.ColList_Ver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;

import com.foresee.test.util.io.FileUtil;
import com.foresee.xdeploy.file.ExcelFiles;
import com.foresee.xdeploy.file.ExcelSvnHelper;
import com.foresee.xdeploy.file.PropValue;
import com.foresee.xdeploy.file.ScanIncrementFiles;
import com.foresee.xdeploy.file.SvnFiles;
import com.foresee.xdeploy.utils.PathUtils;

public class ExcelSvnHelperTest {
    ExcelSvnHelper efh;
    PropValue pv = null;

    @BeforeClass
    public void beforeClass() {
        pv = new PropValue("/svntools.properties");
        efh = new ExcelSvnHelper();

    }

    @Test
    public void loadSvnFilesList() {

        System.out.println(efh.loadSvnFilesList(new File("p:/因开发所致环境变更记录表模版-20150922-产品线-合并.xls")));
    }

    @Test
    public void loadSvnFilesList1() {
        System.out.println("===========显示待处理文件清单=================");

        String sTofile = ""; // 默认为""，不用合并excel

        // if (pv.getProperty("file.excel.merge").equals("true")) { //
        // 判断是否需要合并excel
        // // 生成excel输出文件名
        // sTofile = pv.genOutExcelFileName();
        // // 生成合并的excel文件
        // FileUtil.Copy(pv.excelfiletemplate, sTofile);
        // }

        // 扫描并获取全部excel内容
        //ScanIncrementFiles scanFiles = ScanIncrementFiles.scanListfile(pv.excelfile, pv.excelFolder, pv.scanOption, pv.excelFolderFilter, sTofile);
        SvnFiles sf = efh.loadSvnFiles(new ExcelFiles(pv.excelFolder, pv.excelFolderFilter));
        
        displayList(sf.SvnFileList);

    }

    public void displayList(List<ArrayList<String>> filelist) {
        StringBuffer bugStr = new StringBuffer();
        String a1_Path = ""; // 用来比较上下路径的标记
        String lastStr = "";

        for (ArrayList<String> aRow : filelist) {
            String sPath = PathUtils.autoPathRoot(aRow.get(ColList_Path), pv.filekeyroot);
            String printStr = "Ver:[" + aRow.get(ColList_Ver) + "] |" + aRow.get(ColList_ProjPackage) + "| " + sPath + "  " + aRow.get(ColList_Man)
                    + " << " + aRow.get(ColList_FileName) + "\n";

            // 判断是否目录，目录就不操作
            if (PathUtils.isFolder(sPath)) {
                System.out.print("<<< 注意 >>> 清单包含有目录：\n" + printStr);
            } else {

                System.out.print(printStr);
            }

            // 比较两个相邻的文件,相同标识重复
            if (sPath.equals(a1_Path)) {
                bugStr.append(lastStr);
                bugStr.append(printStr);
                lastStr = "";
            }

            lastStr = printStr;
            a1_Path = sPath;

        }
        System.out.println("\n共有文件数量：" + Integer.toString(filelist.size()));
        System.out.println("==空的版本号，将获取最新的版本。==请仔细检查清单格式，路径不对将无法从svn获取。");
        // if (pv.getProperty("file.excel.merge").equals("true"))
        // System.out.println(" >>>合并生成了EXCEL为：" + sTofile);

        if (bugStr.length() > 0) {
            System.err.println("\n<<<<文件有重复>>>>请注意核对，如下：");
            System.err.println(bugStr);
        }
    }
}
