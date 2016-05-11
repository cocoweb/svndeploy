package com.foresee.xdeploy.utils.svn;


/**
 * 资源对象
 * @author <a href="xiangxji@gmail.com">xiangxji</a>
 * @since 2010-03-27
 */
public class SvnResource implements Comparable<SvnResource>{
    /**
     * 相对仓库根目录的路径
     */
    private String path;
    /**
     * 文件/文件夹的名称
     */
    private String name;
    /**
     * 是否是文件 文件：true | 文件夹：false
     */
    private boolean isFile;
    /**
     * 版本号
     */
    private long SVNVersion;
    /**
     * 本地路径
     */
    private String localPath;
    
    private String url;
    
    public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public long getSVNVersion() {
        return SVNVersion;
    }

    public void setSVNVersion(long sVNVersion) {
        SVNVersion = sVNVersion;
    }
    public String getVersion(){
    	return Long.toString(getSVNVersion());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getFile() {
        return isFile;
    }

    public void setFile(boolean isFile) {
        this.isFile = isFile;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

	@Override
	public String toString() {
		return "SvnResource [path=" + path + ", SVNVersion=" + SVNVersion + "]";
	}

	@Override
	public int compareTo(SvnResource o) {
		
		 int num = o==null ? 0:getPath().compareTo(o.getPath());
				 //new Integer(this.age).compareTo(new Integer(o.age));
		  
//		  if(num == 0){
//		   return this.name.compareTo(o.name);
//		  }
//		  return num;

		return num;
	}

}
