package com.foresee.xdeploy.utils.testng;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.tmatesoft.svn.core.SVNException;

import com.foresee.xdeploy.file.PropValue;
import com.foresee.xdeploy.utils.svn.SvnClient;

public class SvnClientTest {
    SvnClient sc = null;
    PropValue pv = new PropValue("/svntools.properties");
    
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
                    "6112", "p:/tmp/d", "branch");
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
}
