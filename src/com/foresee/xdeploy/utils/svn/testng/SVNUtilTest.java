package com.foresee.xdeploy.utils.svn.testng;

import org.testng.annotations.Test;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;

import com.foresee.test.util.io.FileCopyUtil;
import com.foresee.xdeploy.file.PropValue;
import com.foresee.xdeploy.utils.svn.SVNUtil;

import java.io.File;

import org.testng.annotations.BeforeClass;

public class SVNUtilTest {
    String svnWorkspace="E:/svn_home/yyy/trunk";
    String tmpDir ="E:/tmp/workspace/tmp/e";
    
    PropValue pv = new PropValue("/svntools.properties");
    String svnUrl ="file:///F:/svnroot/sinasvn20120830/xxx/baseline"; 
    
    String svnroot = "file:///F:/svnroot/sinasvn20120830/xxx";
    
    SVNClientManager svnmanager = null;
    
  @BeforeClass
  public void beforeClass() {
      svnmanager = SVNUtil.authSvn(svnroot, "", "");
  }


  @Test
  public void commit() {
      
      File f = new File(svnWorkspace);
    
      FileCopyUtil.copyFolder(tmpDir, svnWorkspace);
      SVNUtil.addEntry(svnmanager, f);
      
      SVNCommitInfo info=SVNUtil.commit(svnmanager, f, false, "yes i'm coming!");
      
      System.out.println(info);
      
  }
  
  @Test
  public void checkout() {
      svnUrl="file:///F:/svnroot/tmp";
      
      File f = new File(svnWorkspace);
      try {
        SVNUtil.checkout(svnmanager, SVNURL.parseURIEncoded(svnUrl), SVNRevision.HEAD, f, SVNDepth.INFINITY);
    } catch (SVNException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
      
  }

}
