package com.foresee.xdeploy.utils.svn;


/**
 * 资源对象
 * @author <a href="xiangxji@gmail.com">xiangxji</a>
 * @since 2010-03-27
 */
public class SvnResource {
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

}
