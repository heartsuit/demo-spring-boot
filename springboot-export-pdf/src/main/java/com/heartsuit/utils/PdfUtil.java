package com.heartsuit.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Desc:导出pdf工具类 Created by jinx on 2017/9/30.
 */
public class PdfUtil {

	/**
	 * 生成诸如 ( 打印时间：20140531001 编号：20140531001)之类的信息 && 左对齐
	 *
	 * @param paragraph
	 * @param font
	 *            字体设置
	 * @param key
	 *            举个栗子---打印时间：20140531001 则 key = "打印时间："
	 * @param value
	 *            value= "20140531001"
	 * @param num
	 *            key之前多少个空格
	 * @throws DocumentException
	 */
	public static void addChunk(Paragraph paragraph, Font font, String key, String value, int num) throws DocumentException {
		StringBuffer sb = new StringBuffer();
		if (num > 0) {
			for (int i = 0; i < num; i++) {
				sb.append(" ");
			}
		}
		Chunk c1 = new Chunk(sb.toString() + key, font);
		Chunk c2 = new Chunk(value, font);
		paragraph.add(c1);
		paragraph.add(c2);
		paragraph.setAlignment(Element.ALIGN_LEFT);
	}

	/**
	 * 给文字添加下划线方法
	 * @param paragraph 具体的某个段落
	 * @param message 具体要添加下划线的文字
	 * @param isUnderline 是否需要添加下划线 ，true的时候添加下划线，false不添加
	 */
	public static void addUnderlineChunk(Paragraph paragraph, String message, boolean isUnderline) {
		Chunk underline = new Chunk(message);
		// 是否需要添加下划线
		if (isUnderline) {
			// 添加下划线
			underline.setUnderline(0.1f, -1f);
		}
		paragraph.add(underline);
	}

	/**
	 * 添加表格信息方法
	 *
	 * @param table
	 *            创建的表格
	 * @param paragraphValue
	 *            填充表格的值信息
	 * @param font
	 *            字体的大小
	 * @param colSpan
	 *            是否跨列
	 * @param rowSpan
	 *            是否跨行
	 * @param colSize
	 *            具体跨几列
	 * @param rowSize
	 *            具体跨几行
	 */
	public static void addTableCell(PdfPTable table, String paragraphValue,
			Font font, boolean colSpan, boolean rowSpan, int colSize,
			int rowSize) {
		PdfPCell cell = new PdfPCell();
		cell.setPhrase(new Paragraph(paragraphValue, font));
		// 居中设置
		cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		// 设置表格的高度
		cell.setMinimumHeight(26);
		if (rowSpan) {// 是否跨行
			cell.setRowspan(rowSize);
		}
		if (colSpan) {// 是否跨列
			cell.setColspan(colSize);
		}
		// 具体的某个cell加入到表格
		table.addCell(cell);
	}

	/**
	 * 格式化时间方法 生成诸如：2017年9月28日，2019-09-28
	 *
	 * @param forMatContext
	 *            具体的格式化格式 如：yyyyMMdd、 HH:mm:ss
	 * @return 格式化后的字符串
	 */
	public static String getDateFormatString(String forMatContext) {
		final ThreadLocal<DateFormat> df = ThreadLocal
				.withInitial(() -> new SimpleDateFormat(forMatContext));
		DateFormat dateFormat = df.get();
		return dateFormat.format(new Date());
	}

}
