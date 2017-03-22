package com.foresee.xdeploy;

import java.util.List;

import org.tmatesoft.svn.core.SVNException;

import com.foresee.test.util.io.FileUtil;
import com.foresee.test.util.lang.StringUtil;
import com.foresee.xdeploy.file.ExcelFiles;
import com.foresee.xdeploy.file.ExcelListHelper;
import com.foresee.xdeploy.file.FilesList;
import com.foresee.xdeploy.file.FilesListItem;
import com.foresee.xdeploy.file.PropValue;
import com.foresee.xdeploy.file.ToExcelFile;
import com.foresee.xdeploy.file.ToZipFile;
import com.foresee.xdeploy.file.WarFiles;
import com.foresee.xdeploy.file.rule.ExchangePath;
import com.foresee.xdeploy.file.rule.ResourceRule;
import com.foresee.xdeploy.utils.PathUtils;
import com.foresee.xdeploy.utils.svn.SVNRepo;
import com.foresee.xdeploy.utils.svn.SvnResource;

public class ListToFileHelper {
    public static final String BATCH = "BATCH";
    public static final String FILE = "FILE";

    public static PropValue pv = null;
    ExcelListHelper excellisthelper = new ExcelListHelper();;

    public ListToFileHelper() {
        this("/svntools.properties");
    }

    public ListToFileHelper(String propFileName) {
        pv = PropValue.getInstance(propFileName);
        //excellisthelper = new ExcelListHelper();
    }

 
    public void scanPrintList() {
        System.out.println("===========显示待处理文件清单=================");

        // 扫描并获取全部excel内容
        FilesList fileslist = excellisthelper.createFilesList();

        StringBuffer bugStr = new StringBuffer();
        String a1_Path = ""; // 用来比较上下路径的标记
        String lastStr = "";

        for (FilesListItem sf : fileslist) {
            String sPath = sf.getPath(pv.filekeyroot);
            //sf.setKeyRoot(pv.filekeyroot);
            
            String printStr ="";
            //检查web工程名
            try {
                sf.checkProject();
                printStr = sf.toString();
            } catch (Exception e) {

                // e.printStackTrace();
                System.out.println("   >>>" + e.getMessage() + "<<<  " + sf);
            }

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
        System.out.println("\n共有文件数量：" + Integer.toString(fileslist.size()));
        System.out.println("==空的版本号，将获取最新的版本。==请仔细检查清单格式，路径不对将无法从svn获取。");
        if (pv.getProperty("file.excel.merge").equals("true"))
            System.out.println(" >>>合并生成了EXCEL为：" + ToExcelFile.getOutExcelFileName());

        if (bugStr.length() > 0) {
            System.err.println("\n<<<<文件有重复>>>>请注意核对，如下：");
            System.err.println(bugStr);
        }
    }

    /**
     * 扫描清单文件， 从svn导出每一个文件到 指定目录
     */
    public void scanSvnToPath() {
        System.out.println("===========从svn库导出到临时目录，或者workspace=================");

        // 扫描并获取全部excel内容
        FilesList sfs = excellisthelper.createFilesList();

        // 排重
        sfs.removeDeuplicate();

        svnToPath(sfs);

        // export svn文件后，自动提交到基线分支
        if (pv.getProperty("svn.autocommit").equals("true"))
            SVNRepo.CommitSvn(pv.getProperty("svn.workspace"), pv.svntofolder, "提交文件：" + sfs.excelFiles);

    }

    public String svnToPath(FilesList fileslist) {

        SVNRepo svnrepo = SVNRepo.getInstance();
        ToZipFile tozipfile = new ToZipFile(svnrepo);

        String exportToPath = pv.svntofolder;

        int fileCount = 0;
        long lVer = -1;
        String sMessage = "";

        for (FilesListItem oItem : fileslist) {
            try {
                //TODO  这里不应该是trunk  ？？？
                //String fromPath = oItem.getPath("trunk"); // PathUtils.autoPathRoot(sf.getPath()
                // aRow.get(ColList_Path),
                // "trunk");
                ExchangePath expath = oItem.getExchange();

                String sUrl = expath.getSvnURL();// expath.getTrunkURL(); //
                                                 // pv.svnurl + fromPath; //
                                                 // svn库的文件绝对路径URL
                String sVer = oItem.getVer(); // aRow.get(ColList_Ver);
                //String toPath = PathUtils.autoUrlToPath(sUrl, exportToPath, pv.keyRootFolder);

                // 判断是否目录，目录就不操作
                if (PathUtils.isFolder(sUrl)) {
                    System.out.println("目录不处理" + sUrl);
                } else {
                    if (pv.getProperty("svn.version.verify").equals("true")) { // 必须校验版本号
                        if (svnrepo.CheckFileVersion(sUrl, sVer)) {

                            svnrepo.Export(oItem);
                            // xclient.svnExport(sUrl, sVer, toPath,
                            // pv.keyRootFolder);

                            System.out.println("export 版本：" + sVer + "|| url=" + sUrl);

                        } else {
                            System.err.println(" -->>>文件版本不存在：[" + sVer + "]" + sUrl);
                            continue;
                        }
                    } else {// 允许不校验版本号

                        if (StringUtil.isBlank(sVer)) {
                            sMessage = "export Last版本：";

                        } else if (svnrepo.CheckFileVersion(sUrl, sVer)) {
                            sMessage = "export 版本：";

                        } else {
                            sMessage = "export Last版本(原" + sVer + ")：";
                        }

                        try {
                            lVer = svnrepo.Export(oItem);
                            // xclient.svnExport(sUrl, sVer, toPath,
                            // pv.keyRootFolder);

                            System.out.println(sMessage + Long.toString(lVer) + "|| url=" + sUrl);

                        } catch (SVNException e) {
                            e.printStackTrace();
                            System.err.println(" -->>>文件版本不存在：[" + sVer + "]" + sUrl);
                            continue;
                        }

                    }

                    if (pv.getProperty("svn.tozip.enabled").equals("true")) 
                        // 将文件添加到zip文件
                        tozipfile.takeFileToZip(oItem);

                }
                fileCount++;
            } catch (SVNException e) {
                e.printStackTrace();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        System.out.println("\nTotal " + Integer.toString(fileCount) + " Files, Exported to path ="
                + PathUtils.addFolderEnd(exportToPath) + pv.svnkeyRoot);

        tozipfile.FileInfo();

        return exportToPath;

    }

    public void commitsvn() {
        ExcelFiles excelfiles = new ExcelFiles(pv);
        SVNRepo.CommitSvn(pv.getProperty("svn.workspace"), pv.svntofolder, "提交文件：" + excelfiles);

    }

    /**
     * 从war包导出文件到增量
     */
    public void scanWarToZip() {
        System.out.println("===========从指定压缩文件war、zip、jar 导出到zip文件=================");

        // 扫描并获取全部excel内容
        FilesList fileslist = excellisthelper.createFilesList();
        // 排重
        fileslist.removeDeuplicate();

        int fileCount = 0;

        SVNRepo svnrepo = SVNRepo.getInstance();
        ToZipFile tozipfile = new ToZipFile(svnrepo);

        // war包的清单
        WarFiles warlist = new WarFiles(pv.getProperty("zip.folder"), pv.getProperty("zip.folder.filter"));
        System.out.println("Loading.." + warlist);

        // 扫描excel文件的清单
        for (FilesListItem oitem : fileslist) {

            try {
                // 判断是否目录，目录就不操作
                if (PathUtils.isFolder(oitem.getPath())) {
                    System.out.println("   >>>目录不处理" + oitem.getPath());
                }else 
                    fileCount += tozipfile.takeWarFileToZip(warlist, oitem);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("\n    >>> 成功抽取文件数:" + fileCount);

        // 对zip文件进行检查，对比excel的文件，和zip中的文件
        tozipfile.FileInfo();
    }

    /**
     * 扫描清单文件，从指定目录 导出文件到 临时输出目录
     */
    public void scanWorkspaceToPath() {
        System.out.println("===========从指定目录 导出到临时目录，或者workspace=================");
        // 扫描并获取全部excel内容
        FilesList sfs = excellisthelper.createFilesList();

        String ciworkspace = pv.getProperty("ci.workspace");
        String citoFolder = pv.getProperty("ci.tofolder");
        String cikeyroot = pv.getProperty("ci.keyroot");

        // 扫描excel文件的清单
        for (FilesListItem sf : sfs) {

            try {
                String sPath = ciworkspace + sf.getPath(); // 源文件路径citoFolder
                String dPath = PathUtils.javaToclass(PathUtils.autoUrlToPath(sPath, citoFolder, cikeyroot)); // 目标路径

                if (sPath.indexOf(cikeyroot) > 0) {
                    // 创建目录
                    FileUtil.createFolder(FileUtil.getFolderPath(dPath));
                    FileUtil.Copy(sPath, dPath);
                } else {
                    System.err.println("ci.keyroot 配置错误，未包含在复制路径中！");
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 根据起始版本号，获取文件清单；从指定目录 导出到输出目录
     */

    public void svnDiffToPath() {
        System.out.println("===========根据起始版本号svndiff.startversion  svndiff.endversion，获取文件清单=================");

        SVNRepo svnrepo = SVNRepo.getInstance();

        List<SvnResource> alist = svnrepo.LogPathList();
        
        
//        String rootURL = pv.getProperty("svndiff.url");
//        rootURL = rootURL.substring(0, rootURL.lastIndexOf(pv.getProperty("svndiff.keyroot")));
       

        for (SvnResource sr : alist) {
        	String surl = sr.getUrl();
        	String spath = pv.getProperty("svn.tofolder") + sr.getPath();
        	
        	try {
				long v = svnrepo.Export(surl, sr.getVersion(), spath );
	            System.out.println("ver:"+Long.toString(v)+ " | "+ sr.getPath());
			} catch (SVNException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            //System.out.println(pv.getProperty("svndiff.url")+spath);


        }

        System.out.println(">>>变动文件数=" + Integer.toString(alist.size()));
        System.out.println("   文件保存在："+pv.getProperty("svn.tofolder") );
        
//        List<SVNDiffStatus> xlist=svnrepo.Diff();
//        for(SVNDiffStatus ss:xlist){
//        	System.out.println(ss.getPath());
//        }
//        System.out.println(">>>===变动文件数=" + Integer.toString(xlist.size()));

    }
    /**
     * 根据起始版本号，获取文件清单
     */

    public void svnDiffToExcel() {
        System.out.println("=========== -x 根据起始版本号svndiff.startversion  svndiff.endversion，获取文件清单=================");

        SVNRepo svnrepo = SVNRepo.getInstance();

        List<SvnResource> alist = svnrepo.LogPathList();
        
        ToExcelFile toExcelFile = ToExcelFile.createToExcelFile(true);
        
        int i=2;

        for (SvnResource sr : alist) {
//            String surl = sr.getUrl();
//            String spath = pv.getProperty("svn.tofolder") + sr.getPath();
            
            //long v = svnrepo.Export(surl, sr.getVersion(), spath );
            
            
            
            System.out.println("ver:"+sr.getVersion()+ " | "+ sr.getPath());

            if (toExcelFile != null) {
                toExcelFile.addRow(i++, sr.getSVNVersion(), sr.getPath());
            }
        }
        
        if (toExcelFile != null) {
            toExcelFile.close();
        }

        System.out.println(">>>变动文件数=" + Integer.toString(alist.size()));
        System.out.println("   Excel文件保存在："+toExcelFile.getOutExcelFileName() );
        

    }

    /**
     * 根据起始版本号，获取文件清单
     */

    public void svnDiffToList() {
        System.out.println("=========== -x 根据起始版本号svndiff.startversion  svndiff.endversion，获取文件清单=================");

        SVNRepo svnrepo = SVNRepo.getInstance();

        List<SvnResource> alist = svnrepo.LogPathList();
        
        //ToExcelFile toExcelFile = ToExcelFile.createToExcelFile(true);
        
        int i=2;

        for (SvnResource sr : alist) {
            String surl = sr.getUrl();
            String spath = pv.getProperty("svn.tofolder") + sr.getPath();
            
            //long v = svnrepo.Export(surl, sr.getVersion(), spath );
            
            ResourceRule rr=ResourceRule.getResourceRule(sr);
            
            
            System.out.println("ver:"+sr.getVersion()+" | Module:"+ rr.getModuleName()+" | Package:"+ rr.getPackageName() +" | "+ sr.getPath());

//            if (toExcelFile != null) {
//                toExcelFile.addRow(i++, sr.getSVNVersion(), sr.getPath());
//            }
        }
        
//        if (toExcelFile != null) {
//            toExcelFile.close();
//        }

        System.out.println(">>>变动文件数=" + Integer.toString(alist.size()));
        //System.out.println("   Excel文件保存在："+toExcelFile.getOutExcelFileName() );
        

    }
    
}
