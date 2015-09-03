package com.foresee.xdeploy.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * /**
 * POI工具类 功能点： 
 * 1、实现excel的sheet复制，复制的内容包括单元的内容、样式、注释
 * 2、setMForeColor修改HSSFColor.YELLOW的色值，setMBorderColor修改PINK的色值
 * 
 * @author Administrator
 *
 * Excel util, create excel sheet, cell and style.
 * 
 * @param <T>
 *            generic class.
 */
public class ExcelmoreUtil<T> {

	public HSSFCellStyle getCellStyle(HSSFWorkbook workbook, boolean isHeader) {
		HSSFCellStyle style = workbook.createCellStyle();
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style.setLocked(true);
		if (isHeader) {
			style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
			style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			HSSFFont font = workbook.createFont();
			font.setColor(HSSFColor.BLACK.index);
			font.setFontHeightInPoints((short) 12);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			style.setFont(font);
		}
		return style;
	}

	public void generateHeader(HSSFWorkbook workbook, HSSFSheet sheet, String[] headerColumns) {
		HSSFCellStyle style = getCellStyle(workbook, true);
		Row row = sheet.createRow(0);
		row.setHeightInPoints(30);
		for (int i = 0; i < headerColumns.length; i++) {
			Cell cell = row.createCell(i);
			String[] column = headerColumns[i].split("_#_");
			sheet.setColumnWidth(i, Integer.valueOf(column[1]));
			cell.setCellValue(column[0]);
			cell.setCellStyle(style);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public HSSFSheet creatAuditSheet(HSSFWorkbook workbook, String sheetName, List<T> dataset, String[] headerColumns,
			String[] fieldColumns) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException,
					InvocationTargetException {

		HSSFSheet sheet = workbook.createSheet(sheetName);
		sheet.protectSheet("1234");// 设置Excel保护密码

		generateHeader(workbook, sheet, headerColumns);
		HSSFCellStyle style = getCellStyle(workbook, false);
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
		int rowNum = 0;
		for (T t : dataset) {
			rowNum++;
			Row row = sheet.createRow(rowNum);
			row.setHeightInPoints(25);
			for (int i = 0; i < fieldColumns.length; i++) {
				String fieldName = fieldColumns[i];

				String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
				try {
					Class clazz = t.getClass();
					Method getMethod;
					getMethod = clazz.getMethod(getMethodName, new Class[] {});
					Object value = getMethod.invoke(t, new Object[] {});
					String cellValue = "";
					if (value instanceof Date) {
						Date date = (Date) value;
						cellValue = sd.format(date);
					} else {
						cellValue = null != value ? value.toString() : "";
					}
					Cell cell = row.createCell(i);
					cell.setCellStyle(style);
					cell.setCellValue(cellValue);

				} catch (Exception e) {

				}
			}
		}
		return sheet;
	}

	/**
	 * 功能：拷贝sheet 实际调用 copySheet(targetSheet, sourceSheet, targetWork,
	 * sourceWork, true)
	 * 
	 * @param targetSheet
	 * @param sourceSheet
	 * @param targetWork
	 * @param sourceWork
	 */
	public static void copySheet(HSSFSheet targetSheet, HSSFSheet sourceSheet, HSSFWorkbook targetWork,
			HSSFWorkbook sourceWork) throws Exception {
		if (targetSheet == null || sourceSheet == null || targetWork == null || sourceWork == null) {
			throw new IllegalArgumentException(
					"调用PoiUtil.copySheet()方法时，targetSheet、sourceSheet、targetWork、sourceWork都不能为空，故抛出该异常！");
		}
		copySheet(targetSheet, sourceSheet, targetWork, sourceWork, true);
	}

	/**
	 * 功能：拷贝sheet
	 * 
	 * @param targetSheet
	 * @param sourceSheet
	 * @param targetWork
	 * @param sourceWork
	 * @param copyStyle
	 *            boolean 是否拷贝样式
	 */
	public static void copySheet(HSSFSheet targetSheet, HSSFSheet sourceSheet, HSSFWorkbook targetWork,
			HSSFWorkbook sourceWork, boolean copyStyle) throws Exception {

		if (targetSheet == null || sourceSheet == null || targetWork == null || sourceWork == null) {
			throw new IllegalArgumentException(
					"调用PoiUtil.copySheet()方法时，targetSheet、sourceSheet、targetWork、sourceWork都不能为空，故抛出该异常！");
		}

		// 复制源表中的行
		int maxColumnNum = 0;

		Map styleMap = (copyStyle) ? new HashMap() : null;

		HSSFPatriarch patriarch = targetSheet.createDrawingPatriarch(); // 用于复制注释
		for (int i = sourceSheet.getFirstRowNum(); i <= sourceSheet.getLastRowNum(); i++) {
			HSSFRow sourceRow = sourceSheet.getRow(i);
			HSSFRow targetRow = targetSheet.createRow(i);

			if (sourceRow != null) {
				copyRow(targetRow, sourceRow, targetWork, sourceWork, patriarch, styleMap);
				if (sourceRow.getLastCellNum() > maxColumnNum) {
					maxColumnNum = sourceRow.getLastCellNum();
				}
			}
		}

		// 复制源表中的合并单元格
		mergerRegion(targetSheet, sourceSheet);

		// 设置目标sheet的列宽
		for (int i = 0; i <= maxColumnNum; i++) {
			targetSheet.setColumnWidth(i, sourceSheet.getColumnWidth(i));
		}
	}

	/**
	 * 功能：拷贝row
	 * 
	 * @param targetRow
	 * @param sourceRow
	 * @param styleMap
	 * @param targetWork
	 * @param sourceWork
	 * @param targetPatriarch
	 */
	public static void copyRow(HSSFRow targetRow, HSSFRow sourceRow, HSSFWorkbook targetWork, HSSFWorkbook sourceWork,
			HSSFPatriarch targetPatriarch, Map styleMap) throws Exception {
		if (targetRow == null || sourceRow == null || targetWork == null || sourceWork == null
				|| targetPatriarch == null) {
			throw new IllegalArgumentException(
					"调用PoiUtil.copyRow()方法时，targetRow、sourceRow、targetWork、sourceWork、targetPatriarch都不能为空，故抛出该异常！");
		}

		// 设置行高
		targetRow.setHeight(sourceRow.getHeight());

		for (int i = sourceRow.getFirstCellNum(); i <= sourceRow.getLastCellNum(); i++) {
			HSSFCell sourceCell = sourceRow.getCell(i);
			HSSFCell targetCell = targetRow.getCell(i);

			if (sourceCell != null) {
				if (targetCell == null) {
					targetCell = targetRow.createCell(i);
				}

				// 拷贝单元格，包括内容和样式
				copyCell(targetCell, sourceCell, targetWork, sourceWork, styleMap);

				// 拷贝单元格注释
				copyComment(targetCell, sourceCell, targetPatriarch);
			}
		}
	}

	/**
	 * 功能：拷贝cell，依据styleMap是否为空判断是否拷贝单元格样式
	 * 
	 * @param targetCell
	 *            不能为空
	 * @param sourceCell
	 *            不能为空
	 * @param targetWork
	 *            不能为空
	 * @param sourceWork
	 *            不能为空
	 * @param styleMap
	 *            可以为空
	 */
	public static void copyCell(HSSFCell targetCell, HSSFCell sourceCell, HSSFWorkbook targetWork,
			HSSFWorkbook sourceWork, Map styleMap) {
		if (targetCell == null || sourceCell == null || targetWork == null || sourceWork == null) {
			throw new IllegalArgumentException(
					"调用PoiUtil.copyCell()方法时，targetCell、sourceCell、targetWork、sourceWork都不能为空，故抛出该异常！");
		}

		// 处理单元格样式
		if (styleMap != null) {
			if (targetWork == sourceWork) {
				targetCell.setCellStyle(sourceCell.getCellStyle());
			} else {
				String stHashCode = "" + sourceCell.getCellStyle().hashCode();
				HSSFCellStyle targetCellStyle = (HSSFCellStyle) styleMap.get(stHashCode);
				if (targetCellStyle == null) {
					targetCellStyle = targetWork.createCellStyle();
					targetCellStyle.cloneStyleFrom(sourceCell.getCellStyle());
					styleMap.put(stHashCode, targetCellStyle);
				}

				targetCell.setCellStyle(targetCellStyle);
			}
		}

		// 处理单元格内容
		switch (sourceCell.getCellType()) {
		case HSSFCell.CELL_TYPE_STRING:
			targetCell.setCellValue(sourceCell.getRichStringCellValue());
			break;
		case HSSFCell.CELL_TYPE_NUMERIC:
			targetCell.setCellValue(sourceCell.getNumericCellValue());
			break;
		case HSSFCell.CELL_TYPE_BLANK:
			targetCell.setCellType(HSSFCell.CELL_TYPE_BLANK);
			break;
		case HSSFCell.CELL_TYPE_BOOLEAN:
			targetCell.setCellValue(sourceCell.getBooleanCellValue());
			break;
		case HSSFCell.CELL_TYPE_ERROR:
			targetCell.setCellErrorValue(sourceCell.getErrorCellValue());
			break;
		case HSSFCell.CELL_TYPE_FORMULA:
			targetCell.setCellFormula(sourceCell.getCellFormula());
			break;
		default:
			break;
		}
	}

	/**
	 * 功能：拷贝comment
	 * 
	 * @param targetCell
	 * @param sourceCell
	 * @param targetPatriarch
	 */
	public static void copyComment(HSSFCell targetCell, HSSFCell sourceCell, HSSFPatriarch targetPatriarch)
			throws Exception {
		if (targetCell == null || sourceCell == null || targetPatriarch == null) {
			throw new IllegalArgumentException(
					"调用PoiUtil.copyCommentr()方法时，targetCell、sourceCell、targetPatriarch都不能为空，故抛出该异常！");
		}

		// 处理单元格注释
		HSSFComment comment = sourceCell.getCellComment();
		if (comment != null) {
			HSSFComment newComment = targetPatriarch.createComment(new HSSFClientAnchor());
			newComment.setAuthor(comment.getAuthor());
			newComment.setColumn(comment.getColumn());
			newComment.setFillColor(comment.getFillColor());
			newComment.setHorizontalAlignment(comment.getHorizontalAlignment());
			newComment.setLineStyle(comment.getLineStyle());
			newComment.setLineStyleColor(comment.getLineStyleColor());
			newComment.setLineWidth(comment.getLineWidth());
			newComment.setMarginBottom(comment.getMarginBottom());
			newComment.setMarginLeft(comment.getMarginLeft());
			newComment.setMarginTop(comment.getMarginTop());
			newComment.setMarginRight(comment.getMarginRight());
			newComment.setNoFill(comment.isNoFill());
			newComment.setRow(comment.getRow());
			newComment.setShapeType(comment.getShapeType());
			newComment.setString(comment.getString());
			newComment.setVerticalAlignment(comment.getVerticalAlignment());
			newComment.setVisible(comment.isVisible());
			targetCell.setCellComment(newComment);
		}
	}

	/**
	 * 功能：复制原有sheet的合并单元格到新创建的sheet
	 * 
	 * @param sheetCreat
	 * @param sourceSheet
	 */
	public static void mergerRegion(HSSFSheet targetSheet, HSSFSheet sourceSheet) throws Exception {
		if (targetSheet == null || sourceSheet == null) {
			throw new IllegalArgumentException("调用PoiUtil.mergerRegion()方法时，targetSheet或者sourceSheet不能为空，故抛出该异常！");
		}

		for (int i = 0; i < sourceSheet.getNumMergedRegions(); i++) {
			CellRangeAddress oldRange = sourceSheet.getMergedRegion(i);
			CellRangeAddress newRange = new CellRangeAddress(oldRange.getFirstRow(), oldRange.getLastRow(),
					oldRange.getFirstColumn(), oldRange.getLastColumn());
			targetSheet.addMergedRegion(newRange);
		}
	}

	/**
	 * 功能：重新定义HSSFColor.YELLOW的色值
	 * 
	 * @param workbook
	 * @return
	 */
	public static HSSFColor setMForeColor(HSSFWorkbook workbook) {
		HSSFPalette palette = workbook.getCustomPalette();
		HSSFColor hssfColor = null;
		// byte[] rgb = { (byte) 221, (byte) 241, (byte) 255 };
		// try {
		// hssfColor = palette.findColor(rgb[0], rgb[1], rgb[2]);
		// if (hssfColor == null) {
		// palette.setColorAtIndex(HSSFColor.YELLOW.index, rgb[0], rgb[1],
		// rgb[2]);
		// hssfColor = palette.getColor(HSSFColor.YELLOW.index);
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		return hssfColor;
	}

	/**
	 * 功能：重新定义HSSFColor.PINK的色值
	 * 
	 * @param workbook
	 * @return
	 */
	public static HSSFColor setMBorderColor(HSSFWorkbook workbook) {
		HSSFPalette palette = workbook.getCustomPalette();
		HSSFColor hssfColor = null;
		byte[] rgb = { (byte) 0, (byte) 128, (byte) 192 };
		try {
			hssfColor = palette.findColor(rgb[0], rgb[1], rgb[2]);
			if (hssfColor == null) {
				palette.setColorAtIndex(HSSFColor.PINK.index, rgb[0], rgb[1], rgb[2]);
				hssfColor = palette.getColor(HSSFColor.PINK.index);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hssfColor;
	}
	
//	public InputStream getDownLoadStream() throws Exception {
//		HSSFWorkbook wb = new HSSFWorkbook();
//		// 设置一个靠右排放样式，如果需要其他样式自可以再定义一些
//		HSSFCellStyle style = wb.createCellStyle();
//		// style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
//		style.setAlignment(HSSFCellStyle.ALIGN_RIGHT); // 在单元格中右排放
//		try {
//			for (int i = 0; i < upload.length; i++) {
//				File f = upload[i]; // 取得一个文件
//				FileInputStream is = new FileInputStream(f);
//				HSSFWorkbook wbs = new HSSFWorkbook(is);
//				// 根据读出的Excel，创建Sheet
//				HSSFSheet sheet = wb.createSheet(uploadFileName[i]);
//				// 一直取的是第一个Sheet，一定要注意，如果你要读取所有的Sheet，循环读取即可
//				HSSFSheet childSheet = wbs.getSheetAt(0);
//				// 循环读取Excel的行
//				for (int j = 0; j < childSheet.getLastRowNum(); j++) {
//					// 根据读取的行，创建要合并Sheet的行
//					HSSFRow r = sheet.createRow(j);
//					HSSFRow row = childSheet.getRow(j);
//					// 判断是否为空，因为可能出现空行的情况
//					if (null != row) {
//						// 循环读取列
//						for (int k = 0; k < row.getLastCellNum(); k++) {
//							// 根据读取的列，创建列
//							HSSFCell c = r.createCell(k);
//							HSSFCell cell = row.getCell(k);
//							// 将值和样式一同赋值给单元格
//							String value = "";
//							if (null != cell) {
//								value = switchCell(cell);
//
//							} else {
//								value = " ";
//							}
//							c.setCellValue(value);
//							c.setCellStyle(style);
//						}
//					} else {
//						HSSFCell c = r.createCell(0);
//						c.setCellValue(" ");
//					}
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		// 这种写法不会产生临时文件，因为这里使用字节数组作为介质
//		ByteArrayOutputStream os = new ByteArrayOutputStream();
//		wb.write(os);
//		byte[] content = os.toByteArray();
//		InputStream is = new ByteArrayInputStream(content);
//		return is;
//	}
	
	private String switchCell(HSSFCell cell){
		String value = "";
		switch (cell.getCellType()) {
		case HSSFCell.CELL_TYPE_NUMERIC: // 数值型
			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				// 如果是Date类型则 ，获取该Cell的Date值
				value = HSSFDateUtil.getJavaDate(
						cell.getNumericCellValue())
						.toString();
			} else {// 纯数字，这里要判断是否为小数的情况，因为整数在写入时会被加上小数点
				String t = cell.getNumericCellValue()
						+ "";
				BigDecimal n = new BigDecimal(cell
						.getNumericCellValue());
				// 判断是否有小数点
				if (t.indexOf(".") < 0) {
					value = n.intValue() + "";
				} else {
					// 数字格式化对象
					NumberFormat nf = NumberFormat
							.getInstance();
					// 小数点最大两位
					nf.setMaximumFractionDigits(2);
					// 执行格式化
					value = nf.format(n.doubleValue());
				}
			}
			break;
		case HSSFCell.CELL_TYPE_STRING: // 字符串型
			value = cell.getRichStringCellValue()
					.toString();
			break;
		case HSSFCell.CELL_TYPE_FORMULA:// 公式型
			// 读公式计算值
			value = String.valueOf(cell
					.getNumericCellValue());
			break;
		case HSSFCell.CELL_TYPE_BOOLEAN:// 布尔
			value = " " + cell.getBooleanCellValue();
			break;
		/* 此行表示该单元格值为空 */
		case HSSFCell.CELL_TYPE_BLANK: // 空值
			value = " ";
			break;
		case HSSFCell.CELL_TYPE_ERROR: // 故障
			value = " ";
			break;
		default:
			value = cell.getRichStringCellValue()
					.toString();
		}

		return value;
	}
}
