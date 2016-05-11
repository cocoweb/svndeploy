package com.foresee.xdeploy.utils.svn;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNDiffStatus;

import com.foresee.test.util.io.FileCopyUtil;
import com.foresee.xdeploy.file.FilesListItem;
import com.foresee.xdeploy.file.PropValue;
import com.foresee.xdeploy.utils.PathUtils;

public class SVNRepo {
    
    String svn_UserName="";
    String svn_password="";
    
    String svn_BaseURL="";
    
    SvnClient svnclient=null;


    private  SVNRepo() {
        svn_UserName=PropValue.getInstance().getProperty("svn.username");
        svn_password=PropValue.getInstance().getProperty("svn.password");
        
        svnclient = SvnClient.getInstance(svn_UserName, svn_password);
    }
    
    private SVNRepo(String svn_baseurl) {
        this();
        svn_BaseURL = svn_baseurl;
        
    }
    
   //static SVNRepository localsvn=null;
    
    public static SVNRepo getInstance(){
//        SVNRepository localsvn=null;
//        if(localsvn==null){
//            localsvn=new SVNRepository();
//        }
        return new SVNRepo();
    }
    
    public static SVNRepo getInstance(String svn_baseurl){
      return new SVNRepo(svn_baseurl);
  }

    
    /**
     * @param xUrl
     * @param xVersion
     * @param xPath
     * @param keyRootFolder
     * @return
     * @throws SVNException
     * @see com.foresee.xdeploy.utils.svn.SvnClient#svnExport(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public long Export(String xUrl, String xVersion, String xPath, String keyRootFolder) throws SVNException {
        return svnclient.svnExport(xUrl, xVersion, xPath, keyRootFolder);
    }
    
    public long Export(FilesListItem oItem) throws SVNException{
        
        String toPath = PathUtils.autoUrlToPath(oItem.getExchange().getSvnURL()
                , PropValue.getInstance().svntofolder, PropValue.getInstance().keyRootFolder);
        
        return Export(oItem,toPath);
    }
    
    public long Export(FilesListItem oItem,String toFilePath) throws SVNException{

        String sUrl = oItem.getExchange().getSvnURL() ;//expath.getTrunkURL(); // pv.svnurl + fromPath; //
                                            // svn库的文件绝对路径URL
        String sVer = oItem.getVer(); // aRow.get(ColList_Ver);
        //String toPath = PathUtils.autoUrlToPath(sUrl, PropValue.getInstance().svntofolder, PropValue.getInstance().keyRootFolder);
        
        return Export(sUrl, sVer, toFilePath, PropValue.getInstance().keyRootFolder);
        
    }

    /**
     * @param xUrl
     * @param startVersion
     * @return
     * @see com.foresee.xdeploy.utils.svn.SvnClient#CheckFileVersion(java.lang.String, java.lang.String)
     */
    public boolean CheckFileVersion(String xUrl, String startVersion) {
        return svnclient.CheckFileVersion(xUrl, startVersion);
    }
    
    /**
     * // export到临时目录 // 复制到svn工作区，提交
     * 
     * @param svnWorkspace
     * @param fromDir
     * @param sMessage
     */
    public static void CommitSvn(String svnWorkspace, String fromDir, String sMessage) {
        
        PropValue pv = PropValue.getInstance();

        SVNClientManager svnmanager = SVNUtil.authSvn(pv.svnurl, pv.getProperty("svn.username"), pv.getProperty("svn.password"));

        File f = new File(svnWorkspace);

        FileCopyUtil.copyFolder(fromDir, svnWorkspace);

        // 添加变更
        SVNUtil.addEntry(svnmanager, f);

        SVNCommitInfo info = SVNUtil.commit(svnmanager, f, false, sMessage);

        System.out.println("提交文件到svn：" + info + "@svnWorkspace\n " + sMessage);

    }

    /**
     * @param xUrl
     * @param startVersion
     * @param endVersion
     * @param svndiffkeyroot
     * @return
     * @throws SVNException
     * @see com.foresee.xdeploy.utils.svn.SvnClient#svnDiff(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public ArrayList<String> svnDiff(String xUrl, String startVersion, String endVersion, String svndiffkeyroot)
            throws SVNException {
        return svnclient.svnDiff(xUrl, startVersion, endVersion, svndiffkeyroot);
    }
    
    public ArrayList<SVNDiffStatus> Diff(){
        PropValue pv =PropValue.getInstance();
        
        String svnurl = pv.getProperty("svndiff.url");
        String startversion = pv.getProperty("svndiff.startversion");
        String endversion = pv.getProperty("svndiff.endversion");
        String svndiffkeyroot = pv.getProperty("svndiff.keyroot");

        System.out.println("startversion=" + startversion + ": endversion=" + endversion + ": svnURL=" + svnurl);
        
        try {
            return svnclient.svnDiff1(svnurl, startversion, endversion, svndiffkeyroot);
        } catch (SVNException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }
    
    public List<SvnResource> svnLogPathList(String xUrl, String startVersion, String endVersion, String svndiffkeyroot) throws SVNException{
    	return svnclient.getLogPathList(xUrl, startVersion, endVersion, svndiffkeyroot);
    }
    
	public List<SvnResource> LogPathList(){
		PropValue pv = PropValue.getInstance();

		String svnurl = pv.getProperty("svndiff.url");
		String startversion = pv.getProperty("svndiff.startversion");
		String endversion = pv.getProperty("svndiff.endversion");
		String svndiffkeyroot = pv.getProperty("svndiff.keyroot");

		System.out.println("startversion=" + startversion + ": endversion=" + endversion + ": svnURL=" + svnurl);
		try {
			return svnclient.getLogPathList(svnurl, startversion, endversion, svndiffkeyroot);
		} catch (SVNException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
