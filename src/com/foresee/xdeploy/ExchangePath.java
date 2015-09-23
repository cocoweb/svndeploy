package com.foresee.xdeploy;

/**
 * @author Allan
 * 
 *   JARName   =gov.chinatax.gt3nf
     FromPath  =gov/chinatax/gt3nf/sb/dkdjdsdjbg/entry/impl/DkdjdsdjSbService.class
     ToZipPath =gov.chinatax.gt3nf/gov/chinatax/gt3nf/sb/dkdjdsdjbg/entry/impl/DkdjdsdjSbService.class
     SrcPath   =/trunk/engineering/src/gt3nf/java/gov.chinatax.gt3nf/src/gov/chinatax/gt3nf/sb/dkdjdsdjbg/entry/impl/DkdjdsdjSbService.java

 *
 */
public class ExchangePath{
    public String JARName="";
    public String FromPath="";
    public String ToZipPath="";
    public String SrcPath="";
    public ExchangePath(String jARName, String fromPath, String toZipPath, String srcPath) {
        super();
        JARName = jARName;
        FromPath = fromPath;
        ToZipPath = toZipPath;
        SrcPath = srcPath;
    }
    @Override
    public String toString() {
        return      " JARName   =" + JARName 
                + "\n FromPath  =" + FromPath 
                + "\n ToZipPath =" + ToZipPath 
                + "\n SrcPath   =" + SrcPath ;
    }
    
}