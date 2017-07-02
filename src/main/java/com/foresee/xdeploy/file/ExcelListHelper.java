package com.foresee.xdeploy.file;

import static com.foresee.xdeploy.file.base.XdeployBase.ExcelCols.ColExcel_Man;
import static com.foresee.xdeploy.file.base.XdeployBase.ExcelCols.ColExcel_Path;
import static com.foresee.xdeploy.file.base.XdeployBase.ExcelCols.ColExcel_ProjPackage;
import static com.foresee.xdeploy.file.base.XdeployBase.ExcelCols.ColExcel_Ver;
import static com.foresee.xdeploy.file.base.XdeployBase.ListCols.ColList_Path;
import static com.foresee.xdeploy.file.base.XdeployBase.ListCols.ColList_ProjPackage;
import static com.foresee.xdeploy.file.base.XdeployBase.ListCols.ColList_Ver;
import static com.foresee.xdeploy.file.base.XdeployBase.ListCols.ColList_Man;
import static com.foresee.xdeploy.file.base.XdeployBase.ListCols.ColList_FileName;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.foresee.xdeploy.file.base.XdeployBase;
import com.foresee.xdeploy.utils.CommonsUtil;
import com.foresee.xdeploy.utils.excel.ExcelMoreUtil;
import com.foresee.xdeploy.utils.excel.ExcelMoreUtil.IHandleCopyRow;
import com.foresee.xdeploy.utils.excel.ExcelMoreUtil.IHandleScanRow;
import com.foresee.xdeploy.utils.excel.POIExcelMakerUtil;

/**
 * Excel 文件清单的处理
 * 
 * @author allan
 *
 */
public class ExcelListHelper extends XdeployBase {

	public ExcelListHelper() {
	}

	/**
	 * 创建文件清单对象
	 * 
	 * @return
	 */
	public FilesList createFilesList() {
		return loadFilesList(new ExcelFiles());
	}

	public ToExcelFile toExcelFile = null;// 默认为null，不用合并excel

	public FilesList loadFilesList(ExcelFiles excelfiles) {
		if (PropValue.getInstance().getProperty("file.excel.merge").equals("true")) { // 判断是否需要合并excel
			// 生成excel输出文件，不打开false
			toExcelFile = ToExcelFile.createToExcelFile(false);
			toExcelFile.openExcel();
		}

		// iRowNum = 0;
		final FilesList svnfiles = new FilesList(excelfiles);

		for (String filepath : excelfiles.getExcelList()) {
			svnfiles.addAll(loadFilesList(new File(filepath)).SvnFileList);
			// if (excelfiles.mergeToFileName.isEmpty())
			// svnfiles.addAll( loadFilesList(new File(filepath),).SvnFileList);
			// else //同时合并excel文件
			// svnfiles.addAll(loadFilesList(filepath,
			// excelfiles.mergeToFileName).SvnFileList);
		}

		// 排序返回的清单
		Collections.sort(svnfiles.SvnFileList, new Comparator<ArrayList<String>>() {
			@Override
			public int compare(ArrayList<String> o1, ArrayList<String> o2) {
				//try{
					return (o1.get(ColList_Path) + String.format("%08d", Long.parseLong(o1.get(ColList_Ver)))+ o1.get(ColList_ProjPackage)).compareTo(
							o2.get(ColList_Path) + String.format("%08d", Long.parseLong(o2.get(ColList_Ver)))+ o2.get(ColList_ProjPackage));
					
//				}catch(NumberFormatException n){
//					System.out.println(">>> 版本号为空："+o2.get(ColList_Path)+"\n    "
//								+o2.get(ColList_FileName)+"#"+o2.get(ColList_Man)
//								+"\n"+n.getMessage());
//					n.printStackTrace();
//					
//				}
//					
//				return -1;
			}

		});

		return svnfiles;
	}

	/**
	 * 获取一个excel文件的内容
	 * 
	 * @param xfile
	 * @return
	 */
	public FilesList loadFilesList(File xfile) {
		final FilesList svnfiles = new FilesList();
		final String filename = xfile.getPath();

		try {

			ExcelMoreUtil.scanExcelData(filename, XdeployBase.SheetName, new IHandleScanRow() {
				@Override
				public void handleRow(HSSFRow row, HSSFWorkbook fromWB, int iCount) {
					addRowToList(svnfiles, row, filename);

					if (toExcelFile != null) {
						// 合并到新的Excel文件
						toExcelFile.copyRow(row, fromWB, iCount);
					}

				}

				@Override
				public int skipRow() {
					return 2;
				}

			});

			if (toExcelFile != null) {
				toExcelFile.writeAndClose();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return svnfiles;

	}

	public List<ArrayList<String>> loadSvnFilesList(ExcelFiles excelfiles) {
		return loadFilesList(excelfiles).SvnFileList;
	}

	public List<ArrayList<String>> loadSvnFilesList(File xfile) {

		return loadFilesList(xfile).SvnFileList;
	}

	private class ExcelRow {
		protected HSSFRow localrow;

		public ExcelRow(HSSFRow localrow) {
			super();
			this.localrow = localrow;
		}

		public String getValue(int col) {
			return CommonsUtil.ChangeUTF8Space(POIExcelMakerUtil.getCellValue(localrow.getCell(col)).toString());
		}
	}

	/**
	 * 添加到文件清单列表中
	 * 
	 * @param xsvnfiles
	 * @param xlocalrow
	 * @param filename
	 */
	protected void addRowToList(FilesList xsvnfiles, HSSFRow xlocalrow, String filename) {
		ExcelRow localrow = new ExcelRow(xlocalrow);

		for (String xfield : handlePathList(localrow.getValue(ColExcel_Path))) {
			// System.out.println("["+POIExcelMakerUtil.getCellValue(xlocalrow.getCell(ColExcel_Ver)).toString()+"]");
			// System.out.println("["+getValue(ColExcel_Ver)+"]");
			xsvnfiles.addItem(localrow.getValue(ColExcel_Ver), handlePath(xfield), localrow.getValue(ColExcel_ProjPackage),
					localrow.getValue(ColExcel_Man), filename);
		}

	}

	@Deprecated
	public FilesList loadFilesList0(File xfile) {
		// String tofilename
		final FilesList svnfiles = new FilesList();
		final String filename = xfile.getPath();

		try {
			if (this.toExcelFile == null) {

				ExcelMoreUtil.scanExcelData(filename, XdeployBase.SheetName, new IHandleScanRow() {
					@Override
					public void handleRow(HSSFRow row, HSSFWorkbook fromWB, int iCount) {
						addRowToList(svnfiles, row, filename);
					}

					@Override
					public int skipRow() {
						return 2;
					}

				});
			} else {
				ExcelMoreUtil.copyExcelDataToFile(filename, toExcelFile.excelFileName, XdeployBase.SheetName,
						new IHandleCopyRow() {
							// copy row 本地代码实现回调

							@Override
							public void handleRow(HSSFRow targetRow, HSSFRow sourceRow, HSSFWorkbook targetWork,
									HSSFWorkbook sourceWork, int iCount) {
								addRowToList(svnfiles, sourceRow, filename);

								// 合并到新的Excel文件
								toExcelFile.copyRow(targetRow, sourceRow, targetWork, sourceWork, iCount);

							}

						});

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return svnfiles;

	}

}