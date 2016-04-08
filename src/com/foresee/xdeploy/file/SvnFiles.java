package com.foresee.xdeploy.file;

import static com.foresee.xdeploy.file.XdeployBase.ListCols.ColList_Path;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.foresee.xdeploy.utils.base.BaseSwitchIterator;

public class SvnFiles extends XdeployBase implements Iterable<SvnFile> {
    public List<ArrayList<String>> SvnFileList=new ArrayList<ArrayList<String>>();
    public ExcelFiles excelFiles;

    public SvnFiles(ExcelFiles excelfiles) {
        excelFiles= excelfiles;
       
    }

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
        /* (non-Javadoc)
         * @see com.foresee.xdeploy.utils.base.BaseSwitchIterator#hasNext()
         */
        @Override
        public boolean hasNext() {
            // TODO Auto-generated method stub
            return super.hasNext();
        }

        /* (non-Javadoc)
         * @see com.foresee.xdeploy.utils.base.BaseSwitchIterator#next()
         */
        @Override
        public SvnFile next() {
            // TODO Auto-generated method stub
            return super.next();
        }

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

   public void removeDeuplicate(){
       List<ArrayList<String>> newlist = new ArrayList<ArrayList<String>>();

       for(int z=0;z<SvnFileList.size();z++){
           ArrayList<String> tmplist1=SvnFileList.get(z);
           ArrayList<String> tmplist2= z+1>=SvnFileList.size()?null:SvnFileList.get(z+1);
           
           if(tmplist2==null||!tmplist1.get(ColList_Path).equals(tmplist2.get(ColList_Path))){
               newlist.add(tmplist1);
           }else{
               //newlist.add(tmplist2);
               //z++;
           }
       }
       
       SvnFileList.clear(); 
       SvnFileList.addAll(newlist);
   }

    public static void main(String[] args) throws Exception {
     }

 

}
