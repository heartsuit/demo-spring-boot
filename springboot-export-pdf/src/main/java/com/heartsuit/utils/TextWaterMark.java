package com.heartsuit.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.IOException;

/**
 * 添加文字水印
 *
 * @Author Heartsuit
 * @Date 2021-12-18
 */
public class TextWaterMark extends PdfPageEventHelper {
    private String waterMarkText;

    public TextWaterMark(String waterMarkText) {
        this.waterMarkText = waterMarkText;
    }

    public void onEndPage(PdfWriter writer, Document document) {
        try {
            float pageWidth = document.right() + document.left();//获取pdf内容正文页面宽度
            float pageHeight = document.top() + document.bottom();//获取pdf内容正文页面高度
            //设置水印字体格式
            BaseFont base = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font waterMarkFont = new Font(base, 20, Font.BOLD, BaseColor.LIGHT_GRAY);
            PdfContentByte waterMarkPdfContent = writer.getDirectContentUnder();
            Phrase phrase = new Phrase(waterMarkText, waterMarkFont);
            //两行三列
            ColumnText.showTextAligned(waterMarkPdfContent, Element.ALIGN_CENTER, phrase,
                    pageWidth * 0.25f, pageHeight * 0.2f, 45);
            ColumnText.showTextAligned(waterMarkPdfContent, Element.ALIGN_CENTER, phrase,
                    pageWidth * 0.25f, pageHeight * 0.5f, 45);
            ColumnText.showTextAligned(waterMarkPdfContent, Element.ALIGN_CENTER, phrase,
                    pageWidth * 0.25f, pageHeight * 0.8f, 45);
            ColumnText.showTextAligned(waterMarkPdfContent, Element.ALIGN_CENTER, phrase,
                    pageWidth * 0.65f, pageHeight * 0.2f, 45);
            ColumnText.showTextAligned(waterMarkPdfContent, Element.ALIGN_CENTER, phrase,
                    pageWidth * 0.65f, pageHeight * 0.5f, 45);
            ColumnText.showTextAligned(waterMarkPdfContent, Element.ALIGN_CENTER, phrase,
                    pageWidth * 0.65f, pageHeight * 0.8f, 45);
        } catch (DocumentException | IOException de) {
            de.printStackTrace();
        }
    }
}