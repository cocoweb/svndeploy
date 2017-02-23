package com.foresee.xdeploy.file;

import com.foresee.xdeploy.utils.svn.SvnResource;

/**
 * @author allan
 * 
 * svn资源 的类别处理
 * 包括：jar、war、目标目录
 *
 */
public class ResourceRule {
    private SvnResource svnResource;
    public SvnResource getSvnResource() {
        return svnResource;
    }
    public void setSvnResource(SvnResource svnResource) {
        this.svnResource = svnResource;
    }
    public  static ResourceRule getResourceRule(SvnResource sr){
        
        ResourceRule rr = new ResourceRule();
        rr.setSvnResource(sr);
        return rr ;
        
    }
    public String getUrl() {
        return svnResource.getUrl();
    }
    public long getSVNVersion() {
        return svnResource.getSVNVersion();
    }
    public String getPath() {
        return svnResource.getPath();
    }
    
    public String getModuleName(){
        String srcpath = getPath();
        int x1 = srcpath.indexOf("/",2)+1;
        int x2 = srcpath.indexOf("/", x1);
        
        return srcpath.substring(x1, x2);
        
    }
    
    public String getPackageName(){
        String srcpath = getPath();
        int x1 = srcpath.indexOf("/", 5)+1;
        int x2 = srcpath.indexOf("/", x1);
        
        return srcpath.substring(x1, x2);
    }
    
    /**
     * @return 获取该文件转换器的路径类型 war、jar、chg
     */
    public String getPathType() {
        if (getPath().lastIndexOf(".java") > 0 ||getPath().lastIndexOf("-java") > 0 )
            return PackageType.Type_JAR;
        else if (getPath().lastIndexOf(".project") > 0)
            return PackageType.Type_NON;
        else
            return PackageType.Type_CHG;
    }

}
