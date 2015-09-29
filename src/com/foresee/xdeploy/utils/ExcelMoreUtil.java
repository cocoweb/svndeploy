package com.foresee.xdeploy.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * /** POI工具类 功能点： 1、实现excel的sheet复制，复制的内容包括单元的内容、样式、注释
 * 2、setMForeColor修改HSSFColor.YELLOW的色值，setMBorderColor修改PINK的色值
 * 
 * @author Administrator
 *
 *         Excel util, create excel sheet, cell and style.
 * 
 * @param <T>
 *            generic class.
 */
public class ExcelMoreUtil {

    public static enum ExcelType {
        xls, xlsx;
    }

    public static HSSFCellStyle getCellStyle(HSSFWorkbook workbook, boolean isHeader) {
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

    public static void generateHeader(HSSFWorkbook workbook, HSSFSheet sheet, String[] headerColumns) {
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
    public static <T> HSSFSheet creatAuditSheet(HSSFWorkbook workbook, String sheetName, List<T> dataset,
            String[] headerColumns, String[] fieldColumns) throws NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {

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

    // public InputStream getDownLoadStream() throws Exception {
    // HSSFWorkbook wb = new HSSFWorkbook();
    // // 设置一个靠右排放样式，如果需要其他样式自可以再定义一些
    // HSSFCellStyle style = wb.createCellStyle();
    // // style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
    // style.setAlignment(HSSFCellStyle.ALIGN_RIGHT); // 在单元格中右排放
    // try {
    // for (int i = 0; i < upload.length; i++) {
    // File f = upload[i]; // 取得一个文件
    // FileInputStream is = new FileInputStream(f);
    // HSSFWorkbook wbs = new HSSFWorkbook(is);
    // // 根据读出的Excel，创建Sheet
    // HSSFSheet sheet = wb.createSheet(uploadFileName[i]);
    // // 一直取的是第一个Sheet，一定要注意，如果你要读取所有的Sheet，循环读取即可
    // HSSFSheet childSheet = wbs.getSheetAt(0);
    // // 循环读取Excel的行
    // for (int j = 0; j < childSheet.getLastRowNum(); j++) {
    // // 根据读取的行，创建要合并Sheet的行
    // HSSFRow r = sheet.createRow(j);
    // HSSFRow row = childSheet.getRow(j);
    // // 判断是否为空，因为可能出现空行的情况
    // if (null != row) {
    // // 循环读取列
    // for (int k = 0; k < row.getLastCellNum(); k++) {
    // // 根据读取的列，创建列
    // HSSFCell c = r.createCell(k);
    // HSSFCell cell = row.getCell(k);
    // // 将值和样式一同赋值给单元格
    // String value = "";
    // if (null != cell) {
    // value = switchCell(cell);
    //
    // } else {
    // value = " ";
    // }
    // c.setCellValue(value);
    // c.setCellStyle(style);
    // }
    // } else {
    // HSSFCell c = r.createCell(0);
    // c.setCellValue(" ");
    // }
    // }
    // }
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // // 这种写法不会产生临时文件，因为这里使用字节数组作为介质
    // ByteArrayOutputStream os = new ByteArrayOutputStream();
    // wb.write(os);
    // byte[] content = os.toByteArray();
    // InputStream is = new ByteArrayInputStream(content);
    // return is;
    // }

    private static String switchCell(HSSFCell cell) {
        String value = "";
        switch (cell.getCellType()) {
        case HSSFCell.CELL_TYPE_NUMERIC: // 数值型
            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                // 如果是Date类型则 ，获取该Cell的Date值
                value = HSSFDateUtil.getJavaDate(cell.getNumericCellValue()).toString();
            } else {// 纯数字，这里要判断是否为小数的情况，因为整数在写入时会被加上小数点
                String t = cell.getNumericCellValue() + "";
                BigDecimal n = new BigDecimal(cell.getNumericCellValue());
                // 判断是否有小数点
                if (t.indexOf(".") < 0) {
                    value = n.intValue() + "";
                } else {
                    // 数字格式化对象
                    NumberFormat nf = NumberFormat.getInstance();
                    // 小数点最大两位
                    nf.setMaximumFractionDigits(2);
                    // 执行格式化
                    value = nf.format(n.doubleValue());
                }
            }
            break;
        case HSSFCell.CELL_TYPE_STRING: // 字符串型
            value = cell.getRichStringCellValue().toString();
            break;
        case HSSFCell.CELL_TYPE_FORMULA:// 公式型
            // 读公式计算值
            value = String.valueOf(cell.getNumericCellValue());
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
            value = cell.getRichStringCellValue().toString();
        }

        return value;
    }

    /**
     * Excel文件到List
     * 
     * @param fileName
     * @param sheetIndex
     *            // 工作表索引
     * @param skipRows
     *            // 跳过的表头
     * @return
     * @throws Exception
     */
    public static List<Object> readToList(String fileName, int sheetIndex, int skipRows) throws Exception {
        List<Object> ls = new ArrayList<Object>();
        Workbook wb = loadWorkbook(fileName);
        if (null != wb) {
            Sheet sh = wb.getSheetAt(sheetIndex);
            int rows = sh.getPhysicalNumberOfRows();
            for (int i = skipRows; i < rows; i++) {
                Row row = sh.getRow(i);
                if (null == row) {
                    break;
                }
                int cells = row.getPhysicalNumberOfCells();
                if (cells == 0) {
                    continue;
                }
                List<String> r = new ArrayList<String>(cells);
                for (int c = 0; c < cells; c++) {
                    if (c == 0 || c == 4) {
                        try {
                            r.add(String.format("%.0f", row.getCell(c).getNumericCellValue()));
                        } catch (Exception e) {
                            throw new Exception("出现该错误请依次检查：<br>1、【序号】或【端口号】请使用数字<br>2、检查《Webservice信息》是否是第二个sheet页");
                        }
                    } else {
                        r.add(row.getCell(c).getStringCellValue());
                    }
                }
                ls.add(r);
            }
        }

        return ls;
    }

    /**
     * 读取Excel文件，支持2000与2007格式
     * 
     * @param fileName
     * @return
     * @throws Exception
     */
    public static Workbook loadWorkbook(String fileName) throws Exception {
        if (null == fileName)
            return null;

        Workbook wb = null;
        if (fileName.toLowerCase().endsWith(".xls")) {
            try {
                InputStream in = new FileInputStream(fileName);
                POIFSFileSystem fs = new POIFSFileSystem(in);
                wb = new HSSFWorkbook(fs);
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (fileName.toLowerCase().endsWith(".xlsx")) {
            try {
                InputStream in = new FileInputStream(fileName);
                wb = new XSSFWorkbook(in);
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new Exception("不是一个有效的Excel文件");
        }
        return wb;
    }

    /**
     * 将workbook对象，写回文件
     * 
     * @param wb
     * @param sfilename
     */
    public static void writeWorkbookToExcel(Workbook wb, String sfilename) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(sfilename);
            writeWorkbookToExcel(wb, fileOutputStream);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }

    public static void writeWorkbookToExcel(Workbook wb, OutputStream out) {
        try {
            wb.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从列表生成Workbook对象
     * 
     * @param rows
     * @param type
     * @return
     */
    public static Workbook listToWorkbook(List<?> rows, ExcelType type) {
        Workbook wb = null;
        if (ExcelType.xls.equals(type)) {
            wb = new HSSFWorkbook();
        } else if (ExcelType.xlsx.equals(type)) {
            wb = new XSSFWorkbook();
        } else {
            return null;
        }

        Sheet sh = wb.createSheet();
        if (null != rows) {
            for (int i = 0; i < rows.size(); i++) {
                Object obj = rows.get(i);
                Row row = sh.createRow(i);

                if (obj instanceof Collection) {
                    Collection<?> r = (Collection<?>) obj;
                    Iterator<?> it = r.iterator();
                    int j = 0;
                    while (it.hasNext()) {
                        Cell cell = row.createCell(j++);
                        cell.setCellValue(String.valueOf(it.next()));
                    }
                } else if (obj instanceof Object[]) {
                    Object[] r = (Object[]) obj;
                    for (int j = 0; j < r.length; j++) {
                        Cell cell = row.createCell(j);
                        cell.setCellValue(String.valueOf(r[j]));
                    }
                } else {
                    Cell cell = row.createCell(0);
                    cell.setCellValue(String.valueOf(obj));
                }
            }
        }

        return wb;
    }

    /**
     * 列表数据添加到Workbook对象 only xls
     * 
     * @param rows
     * @param type
     * @return
     */
    public static Workbook listToWorkbook(List<?> rows, Workbook tobook, int sheetIndex) {
        // Workbook wb = null;
        // if (ExcelType.xls.equals(type)) {
        // wb = new HSSFWorkbook();
        // } else if (ExcelType.xlsx.equals(type)) {
        // wb = new XSSFWorkbook();
        // } else {
        // return null;
        // }
        Sheet sh = tobook.getSheetAt(sheetIndex);

        int iStartRow = sh.getPhysicalNumberOfRows();

        if (null != rows) {
            for (int i = 0; i < rows.size(); i++) {
                Object obj = rows.get(i);
                Row row = sh.createRow(i + iStartRow);

                if (obj instanceof Collection) {
                    Collection<?> r = (Collection<?>) obj;
                    Iterator<?> it = r.iterator();
                    int j = 0;
                    while (it.hasNext()) {
                        Cell cell = row.createCell(j++);
                        cell.setCellValue(String.valueOf(it.next()));
                    }
                } else if (obj instanceof Object[]) {
                    Object[] r = (Object[]) obj;
                    for (int j = 0; j < r.length; j++) {
                        Cell cell = row.createCell(j);
                        cell.setCellValue(String.valueOf(r[j]));
                    }
                } else {
                    Cell cell = row.createCell(0);
                    cell.setCellValue(String.valueOf(obj));
                }
            }
        }

        return tobook;
    }

    /**
     * 从列表直接生成excel文件
     * 
     * @param rows
     * @param sfilename
     * @param type
     */
    public static void listToExcelFile(List<?> rows, String sfilename, ExcelType type) {

        // File xfile = new File(sfilename);
        // if(xfile.exists()){
        // System.err.println("文件已经存在"+sfilename);
        //
        // }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(sfilename);

            writeWorkbookToExcel(listToWorkbook(rows, type), fileOutputStream);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void listToExcelFile(List<?> rows, String sfilename) {
        listToExcelFile(rows, sfilename, ExcelType.xls);
    }

    public static void WriteListToExcelFile(List<?> rows, String sfilename) {
        FileOutputStream fileOutputStream = null;
        try {
            Workbook towb = loadWorkbook(sfilename);
            listToWorkbook(rows, towb, 0);

            fileOutputStream = new FileOutputStream(sfilename);

            writeWorkbookToExcel(towb, fileOutputStream);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * Copy数据的注入接口
     * 
     */
    public interface IHandleCopyRow {
        public void handleRow(HSSFRow row2, HSSFRow row1, HSSFWorkbook toWB, HSSFWorkbook fromWB);
    }

    public static void copyExcelDataToFile(String excelfile1, String excelfile2) throws IOException {
        copyExcelDataToFile(excelfile1, excelfile2, "功能清单", null);
    }

    /**
     * 追加copy excel 内容到另一个excel文件
     * 
     * @param excelfile1
     *            源内容excel
     * @param excelfile2
     *            目标excel文件
     * @param sheetName
     *            追加复制的sheet名
     * @throws IOException
     */
    public static void copyExcelDataToFile(String excelfile1, String excelfile2, String sheetName,
            IHandleCopyRow xHandleCopyRow) throws IOException {
        int skipRows1 = 2;
        // int skipRows2 = 2;
        Workbook fromWB = null, toWB = null;

        try {
            // 读取file1的内容
            fromWB = loadWorkbook(excelfile1);
            Sheet sh1 = fromWB.getSheet(sheetName);
            int rows1 = sh1.getPhysicalNumberOfRows();

            // 添加保存到file2中
            toWB = loadWorkbook(excelfile2);
            Sheet sh2 = toWB.getSheet(sheetName);
            int rows2 = sh2.getPhysicalNumberOfRows();

            for (int i1 = 0; i1 < rows1 - skipRows1; i1++) {

                Row row1 = sh1.getRow(i1 + skipRows1); // 获取一行from
                if (row1 != null) {
                    // 判断这一行是否空数据
                    Cell cell1 = row1.getCell(1);
                    if (cell1 != null && !POIExcelMakerUtil.getCellValue(row1.getCell(1)).toString().equals("")) {

                        Row row2 = sh2.createRow(rows2 + i1); // 创建一行to

                        if (xHandleCopyRow == null) {
                            // 未传入接口，则直接用行Copy
                            copyRow((HSSFRow) row2, (HSSFRow) row1, (HSSFWorkbook) toWB, (HSSFWorkbook) fromWB,
                                    ((HSSFSheet) sh2).createDrawingPatriarch(), null);

                        } else {
                            // 判断如果传入了接口，则调用接口进行处理
                            xHandleCopyRow.handleRow((HSSFRow) row2, (HSSFRow) row1, (HSSFWorkbook) toWB,
                                    (HSSFWorkbook) fromWB);
                        }

                    }
                }

            }

            // System.out.println(excelfile1);

            writeWorkbookToExcel(toWB, excelfile2);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (toWB != null)
                toWB.close();
            if (fromWB != null)
                fromWB.close();

        }

    }

    /**
     * 扫描数据的注入接口
     *
     */
    public interface IHandleScanRow {
        /**
         * 处理一行数据
         * @param row
         * @param fromWB
         */
        public void handleRow(HSSFRow row, HSSFWorkbook fromWB);
        
        /**
         * @return  返回跳过的表头行数
         */
        public int skipRow();
    }

    public static void scanExcelData(String excelfile, String sheetName, IHandleScanRow xHandleScanRow)
            throws IOException {
        int skipRows1 = xHandleScanRow ==null ? 2:xHandleScanRow.skipRow();
        // int skipRows2 = 2;
        Workbook fromWB = null;

        try {
            // 读取file1的内容
            fromWB = loadWorkbook(excelfile);
            Sheet sh1 = fromWB.getSheet(sheetName);
            int rows1 = sh1.getPhysicalNumberOfRows();

            for (int i1 = 0; i1 < rows1 - skipRows1; i1++) {

                Row row1 = sh1.getRow(i1 + skipRows1); // 获取一行from
                if (row1 != null) {
                    // 判断这一行是否空数据
                    Cell cell1 = row1.getCell(1);
                    if (cell1 != null && !POIExcelMakerUtil.getCellValue(row1.getCell(1)).toString().equals("")) {

                        if (xHandleScanRow == null) {
                            System.out.println("");
                            for(int i2 =0;i2<row1.getPhysicalNumberOfCells();i2++){
                                System.out.print(POIExcelMakerUtil.getCellValue(row1.getCell(i2))+" | ");
                            }
                            

                        } else {
                            // 判断如果传入了接口，则调用接口进行处理
                            xHandleScanRow.handleRow((HSSFRow) row1, (HSSFWorkbook) fromWB);
                        }

                    }
                }

            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (fromWB != null)
                fromWB.close();

        }

    }

    public static void main(String[] args) {
        List<Object> rows = new ArrayList<Object>();

        List<Object> row = new ArrayList<Object>();
        row.add("字符串");
        row.add(11);
        row.add(new Date());
        row.add(1.0);
        rows.add(((Object) row));

        rows.add("中文");
        rows.add(new Date());

        // listToWorkbook(rows, ExcelType.xls);
        // listToWorkbook(rows, ExcelType.xlsx);

        WriteListToExcelFile(rows, "p:/bbb.xls");

        try {
            copyExcelDataToFile("p:/workspace/xls/因开发所致环境变更记录表模版-20150828-黄健-基础办税.xls", "p:/因开发所致环境变更记录表模版 - 副本.xls");
            copyExcelDataToFile("p:/workspace/xls/因开发所致环境变更记录表模版-20150828-杜英恒-产品线.xls", "p:/因开发所致环境变更记录表模版 - 副本.xls");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
