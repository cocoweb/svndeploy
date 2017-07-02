package com.foresee.xdeploy.file;

import static com.foresee.xdeploy.file.base.XdeployBase.ListCols.ColList_FileName;
import static com.foresee.xdeploy.file.base.XdeployBase.ListCols.ColList_Man;
import static com.foresee.xdeploy.file.base.XdeployBase.ListCols.ColList_Path;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.foresee.xdeploy.file.base.VerEmptyException;
import com.foresee.xdeploy.file.base.XdeployBase;
import com.foresee.xdeploy.utils.ListUtil;
import com.foresee.xdeploy.utils.base.BaseSwitchIterator;

public class FilesList extends XdeployBase implements Iterable<FilesListItem> {
    public List<ArrayList<String>> SvnFileList=new ArrayList<ArrayList<String>>();
    public ExcelFiles excelFiles;

    public FilesList(ExcelFiles excelfiles) {
        excelFiles= excelfiles;
       
    }

    public FilesList() {
       
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
        try {
			xrow.add(handleVerNo(xver));
		} catch (VerEmptyException e) {
			System.err.println(">>> 版本号为空："+xpath+"\n    " +xfilename+"#"+xman);
			e.printStackTrace();
		} // 版本号
        
        xrow.add(handlePath(xpath)); // 路径
        xrow.add(handleProjectName(xproj)); // 工程名
        xrow.add(handleProjectName(xman)); // 提交人
        xrow.add(xfilename); // 文件名
        
        SvnFileList.add(xrow);

        return xrow;
    }
    

    /**
     * @return
     * @see java.util.List#iterator()
     */
    @Override
    public Iterator<FilesListItem>  iterator() {
        return new FilesListIterator( SvnFileList.iterator(),this);
    }
    
    private class FilesListIterator extends BaseSwitchIterator<FilesListItem,ArrayList<String>>{

        FilesList filesList;

        public FilesListIterator(Iterator<ArrayList<String>> xiterator, FilesList Fileslist) {
            super(xiterator);
            filesList = Fileslist;
        }

        @Override
        public FilesListItem switchObject(ArrayList<String> xobj) {
            return new FilesListItem(xobj,filesList);
        }

      
    }
    
   public int size() {
        
        return SvnFileList.size();
    }

   public  List<ArrayList<String>> removeDeuplicate(){
       List<ArrayList<String>> newlist = new ArrayList<ArrayList<String>>();

       for(int z=0;z<SvnFileList.size();z++){
           ArrayList<String> tmplist1=SvnFileList.get(z);
           ArrayList<String> tmplist2= z+1>=SvnFileList.size()?null:SvnFileList.get(z+1);
           
           if(tmplist2==null||!tmplist1.get(ColList_Path).equals(tmplist2.get(ColList_Path))){
               newlist.add(tmplist1);
           }
       }
       
       SvnFileList.clear(); 
       SvnFileList.addAll(newlist);
       
       return newlist;
   }

	public List<ArrayList<String>> removeDeuplicate1() {
		return ListUtil.removeDeuplicate(SvnFileList, new Comparator<ArrayList<String>>() {

			@Override
			public int compare(ArrayList<String> o1, ArrayList<String> o2) {
				if (o2 == null || !o1.get(ColList_Path).equals(o2.get(ColList_Path))) {

					return 0;
				} else
					return -1;
			}

		});
	}

 
 

}
