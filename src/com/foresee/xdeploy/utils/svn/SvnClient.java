package com.foresee.xdeploy.utils.svn;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.ISVNDiffStatusHandler;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNDiffStatus;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatusType;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import com.foresee.test.util.lang.StringUtil;
import com.foresee.xdeploy.file.PropValue;
import com.foresee.xdeploy.utils.PathUtils;

public class SvnClient {
	private static volatile SvnClient svnClient = null;
	private String userName;
	private String password;
	SVNClientManager clientManager = null;

	private SvnClient() {
		SVNRepositoryFactoryImpl.setup();
		FSRepositoryFactory.setup();
		DAVRepositoryFactory.setup();
	}

	public static SvnClient getInstance() {
		if (svnClient == null) {
			svnClient = new SvnClient();
		}
		return svnClient;
	}

	public static SvnClient getInstance(String sUserName, String sPassword) {
		SvnClient xClient = getInstance();
		xClient.userName = sUserName;
		xClient.password = sPassword;

		xClient.init();

		return xClient;
	}

	private void init() {
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(userName, password);

		ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
		// 创建SVNClientManager的实例
		clientManager = SVNClientManager.newInstance((DefaultSVNOptions) options, authManager);

	}

	/**
	 * Svn export tool
	 * 
	 * @param xUrl
	 *            svn库URL
	 * @param xVersion
	 *            版本号
	 * @param xPath
	 *            输出的路径
	 * @param keyRootFolder
	 *            svn库保存路径时的关键字，即根目录
	 *            如：https://nfsvn.foresee.com.cn/svn/GT3-NF-QGTGB
	 *            /branch/20150812
	 *            /engineering/src/gt3nf/web/gt3nf-skin/WebContent
	 *            /etax/script/module/sbzs/init/sbInit_ccstool.js keyFolder =
	 *            branch or engineering or src
	 * @throws SVNException
	 */
	public long svnExport(String xUrl, String xVersion, String xPath, String keyRootFolder) throws SVNException {

		SVNURL url = SVNURL.parseURIEncoded(xUrl);

		File dstPath = new File(xPath);

		// 如果版本号为空，就使用-1，代表获取最新的版本or Head
		SVNRevision revision = SVNRevision.create(StringUtil.isBlank(xVersion) ? -1 : Integer.parseInt(xVersion));

		long re = clientManager.getUpdateClient().doExport(url, dstPath, revision, revision, null, true,
				SVNDepth.fromRecurse(true));
		
		return re;

		//System.out.println("export 版本：" + Long.toString(re) + "|| url=" + url);

	}
	
	

	private static ArrayList<String> changeList = new ArrayList<String>();
	private static String svnDiffKeyroot = "";

	class DiffHandler implements ISVNDiffStatusHandler {

		@Override
		public void handleDiffStatus(SVNDiffStatus diffStatus) throws SVNException {

			if (diffStatus.getKind() == SVNNodeKind.FILE
					&& (diffStatus.getModificationType() == SVNStatusType.STATUS_ADDED
							|| diffStatus.getModificationType() == SVNStatusType.STATUS_MODIFIED)) {
				changeList.add(statusToString(diffStatus));
				// System.out.println(statusToString(diffStatus));
			}

		}

		public String statusToString(SVNDiffStatus diffStatus) {
			return PathUtils.autoPathRoot(diffStatus.getPath(), svnDiffKeyroot); // ()
																					// +diffStatus.getURL().toString();

		}

	}

	public ArrayList<String> svnDiff(String xUrl, String startVersion, String endVersion, String svndiffkeyroot)
			throws SVNException {
		svnDiffKeyroot = svndiffkeyroot;

		SVNURL url = SVNURL.parseURIEncoded(xUrl);
		// 起始版本号-1，确保包含起始版本号的变动，否则会不能包含起始版本号的变动
		SVNRevision startRevision = SVNRevision.create(autoRevision(startVersion) - 1);
		SVNRevision endRevision = SVNRevision.create(autoRevision(endVersion));

		// OutputStream out = new ByteArrayOutputStream();

		clientManager.getDiffClient().doDiffStatus(url, startRevision, endRevision, startRevision,
				SVNDepth.fromRecurse(true), true, new DiffHandler());

		return changeList;

	}

	private long autoRevision(String revision) {
		long iVer = -1;

		// 如果版本号为空，就使用-1，代表获取最新的版本or Head
		if (!StringUtil.isBlank(revision))
			iVer = Long.parseLong(revision);

		return iVer;

	}

	private static class ImplISVNLogHandler implements ISVNLogEntryHandler {
		private StringBuffer sb;

		public ImplISVNLogHandler(StringBuffer sb) {
			this.sb = sb;
		}

		public void handleLogEntry(SVNLogEntry logEntry) throws SVNException {
			sb.append("++++++++++++++++++++++" + "\t");
			sb.append("Revision:" + logEntry.getRevision() + "\t");
			sb.append(logEntry.getDate().toString() + "\t");
			sb.append(logEntry.getAuthor().toString() + "\t");
			sb.append("++++++++++++++++++++++" + "\t \n");
			sb.append(logEntry.getMessage() + ":" + "\n");
			sb.append("\n"); // 拿到chagePaths
			Map<String, SVNLogEntryPath> changedPaths = logEntry.getChangedPaths(); // 循环Path路径，放入String
			// Buffer，供写文件使用
			for (Iterator<String> changedPathsIter = changedPaths.keySet().iterator(); changedPathsIter.hasNext();) {
				sb.append((String) changedPathsIter.next() + "\n");
			}
			sb.append("\n \n");
		}
	}

	public void svnLogRead(String xUrl, String startVersion, String endVersion) throws SVNException {
		SVNURL url = SVNURL.parseURIEncoded(xUrl);
		SVNRevision startRevision = SVNRevision.create(autoRevision(startVersion));
		SVNRevision endRevision = SVNRevision.create(autoRevision(endVersion));

		StringBuffer sB = new StringBuffer();

		clientManager.getLogClient().doLog(url, null, startRevision, startRevision, endRevision, false, true, 100,
				new ImplISVNLogHandler(sB));

		System.out.println(sB);

	}

	public boolean CheckFileVersion(String xUrl, String startVersion) {
		// clientManager.getLogClient().
		// clientManager.getLookClient().doGetRevisionProperties(repositoryRoot,
		// revision)
		final boolean[] ret = {false};
		try {
			SVNURL url = SVNURL.parseURIEncoded(xUrl);
			final SVNRevision startRevision = SVNRevision.create(autoRevision(startVersion));

			clientManager.getLogClient().doLog(url, null, startRevision, startRevision, startRevision, false, true, 100,
					new ISVNLogEntryHandler() {
						@Override
						public void handleLogEntry(SVNLogEntry logEntry) throws SVNException {
							ret[0] = ret[0] || (startRevision.getNumber() == logEntry.getRevision());
						}

					});
		} catch (SVNException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret[0];
	}

	public static void main(String[] args) {
		try {
		    
		    PropValue pv = new PropValue("/svntools.properties");
			// SvnClient.getInstance(pv.getProperty("svn.username"), pv.getProperty("svn.password")).svnExport("https://nfsvn.foresee.com.cn/svn/GT3-NF-QGTGB/branch/20150812/engineering/src/gt3nf/web/gt3nf-skin/WebContent/etax/script/module/sbzs/init/sbInit_ccstool.js",
			// "3063", "p:/tmp/c/","branch");
			ArrayList<String> xlist = SvnClient.getInstance(pv.getProperty("svn.username"), pv.getProperty("svn.password"))
					.svnDiff("https://nfsvn.foresee.com.cn/svn/GT3-NF-QGTGB/branch/20150812", "3395", "3442", "src");

			for (String sfile : xlist) {
				SvnClient.getInstance(pv.getProperty("svn.username"), pv.getProperty("svn.password"))
						.svnLogRead("https://nfsvn.foresee.com.cn/svn/GT3-NF-QGTGB/trunk/" + sfile, "3395", "3442");
			}
		} catch (SVNException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
