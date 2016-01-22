package com.foresee.xdeploy.file;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.foresee.xdeploy.utils.base.BaseSwitchIterator;

public class SvnFiles extends XdeployBase implements Iterable<SvnFile> {
    public List<ArrayList<String>> SvnFileList=new ArrayList<ArrayList<String>>();

    public SvnFiles() {
       
    }

    /**
     * @param arg0
     * @return
     * @see java.util.List#addAll(java.util.Collection)
     */
    public boolean addAll(Collection<? extends ArrayList<String>> arg0) {
        return SvnFileList.addAll(arg0);
    }
    
    public ArrayList<String> addItem(String xver, String xpath, String xproj, String xman, String xfilename) {
        ArrayList<String> xrow = new ArrayList<String>();
        xrow.add(handleVerNo(xver)); // 版本号
        xrow.add(handlePath(xpath)); // 路径
        xrow.add(handleProjectName(xproj)); // 工程名
        xrow.add(handleProjectName(xman)); // 提交人
        xrow.add(xfilename); // 文件名
        
        SvnFileList.add(xrow);

        return xrow;
    }
    
    

//    private List<String> handlePathList(String sPath) {
//        String[] xstr = StringUtil.split(sPath);
//        return Arrays.asList(xstr);
//    }

    /**
     * @return
     * @see java.util.List#iterator()
     */
    @Override
    public Iterator<SvnFile>  iterator() {
        return new SvnFilesIterator( SvnFileList.iterator(),this);
    }
    
    private class SvnFilesIterator extends BaseSwitchIterator<SvnFile,ArrayList<String>>{
        SvnFiles svnfiles;

        public SvnFilesIterator(Iterator<ArrayList<String>> xiterator, SvnFiles svnFiles) {
            super(xiterator);
            svnfiles = svnFiles;
        }

        @Override
        public SvnFile switchObject(ArrayList<String> xobj) {
            return  new SvnFile(xobj,svnfiles);
        }

      
    }
    
   public int size() {
        
        return SvnFileList.size();
    }

    
    public static void main(String[] args) throws Exception {
     }

 

}
