package com.foresee.xdeploy.utils.svn.testng;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.wc.SVNDiffStatus;

import com.foresee.test.util.lang.StringUtil;
import com.foresee.xdeploy.file.PropValue;
import com.foresee.xdeploy.file.rule.ExchangePath;
import com.foresee.xdeploy.utils.PathUtils;
import com.foresee.xdeploy.utils.svn.SvnClient;

public class SvnClientTest {
    SvnClient sc = null;
    PropValue pv = PropValue.getInstance("/svntools.properties");

    
    String fileurl = "https://nfsvn.foresee.com.cn/svn/GT3-NF-QGTGB/trunk/engineering/src/gt3nf/java/com.foresee.gt3nf.service/src/com/foresee/gt3nf/service/outerservice/backcaller/service/gt3/hxqz/sb/impl/qysds/QysdsKjqysdssbQcsCxService.java";

    @BeforeClass
    public void beforeClass() {
        sc = SvnClient.getInstance(pv.getProperty("svn.username"), pv.getProperty("svn.password"));
    }

    @Test
    public void svnExport() {
        try {
            sc.svnExport(
                    fileurl,
                    "6112", "p:/tmp/d");
        } catch (SVNException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Test
    public void svnVersion(){
        //clientManager.getLookClient().doGetRevisionProperties(repositoryRoot, revision)
        
        if (sc.CheckFileVersion( fileurl , "6114")){
			System.out.println("6113");
		}else{
			System.out.println("no such version 6113");
			
		};


    }
    
    public List<String> toLogMessage(List<SVNLogEntry> loglist){
    	List<String> retlist = new ArrayList();
    	for(SVNLogEntry entry:loglist){
    		retlist.add(entry.getRevision()+ " | "+StringUtil.trim(entry.getMessage())+"\n");
    	}
    	
    	return retlist;
    	
    }
    
    @Test
    public void svnLogRead(){
    	String xurl = "https://nfsvn.foresee.com.cn/svn/GT3-NF-QGTGB/trunk/engineering/src/gt3nf/java/com.foresee.gt3nf.service/src/com/foresee/gt3nf/service/outerservice/backcaller/service/gt3/hxqz/sb/impl";
    	try {
    		List<SVNLogEntry> loglist = new ArrayList<SVNLogEntry>();
			System.out.println(sc.getLogPathList(xurl, "8000", "10000", "",loglist));
			System.out.println(toLogMessage(loglist ));
	    	for(SVNLogEntry entry:loglist){
	    		for(SVNLogEntryPath entryPath: entry.getChangedPaths().values()){
	    			try {
						System.out.println(ExchangePath.exchange(entryPath.getPath()));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    			
	    		}

	    	}
			
			
		} catch (SVNException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @Test
    public void svnDiff(){
    	String xurl = "https://nfsvn.foresee.com.cn/svn/GT3-NF-QGTGB/trunk/engineering/src/gt3nf/java/com.foresee.gt3nf.service/src/com/foresee/gt3nf/service/outerservice/backcaller/service/gt3/hxqz/sb/impl";
    	String endver = "10000";
    	try {
    		ArrayList<SVNDiffStatus> ll = sc.svnDiff1(xurl, "5000", endver, "");
    		for(SVNDiffStatus s : ll){
    			long v = sc.svnExport(s.getURL().toString(), endver, "p:/yy"+PathUtils.autoPathRoot(s.getPath(),""));
    			
    			System.out.println("ver:"+Long.toString(v)+ " | "+ s.getURL().toString());
    			
//    	        sc.doExport(change.getURL(), destination, 
//        		this.endingRevision, this.endingRevision, null, true, SVNDepth.getInfinityOrEmptyDepth(true)); 
    		}
		} catch (SVNException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
