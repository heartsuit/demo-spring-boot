package com.heartsuit.utils;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.IOException;

/**
 * @Author Heartsuit
 * @Date 2022-01-02
 */
public class ImageWaterMark extends PdfPageEventHelper {

    private String waterMarkFullFilePath;
    private Image waterMarkImage;

    public ImageWaterMark(String waterMarkFullFilePath) {
        this.waterMarkFullFilePath = waterMarkFullFilePath;
    }

    public void onEndPage(PdfWriter writer, Document document) {
        try {
            float pageWidth = document.right() + document.left();//获取pdf内容正文页面宽度
            float pageHeight = document.top() + document.bottom();//获取pdf内容正文页面高度
            PdfContentByte waterMarkPdfContent = writer.getDirectContentUnder();
            //仅设置一个图片实例对象，整个PDF文档只应用一个图片对象，极大减少因为增加图片水印导致PDF文档大小增加
            if (waterMarkImage == null) {
                waterMarkImage = Image.getInstance(waterMarkFullFilePath);
            }
            //添加水印图片，三行两列
            waterMarkPdfContent.addImage(getSingletonWaterMarkImage(waterMarkImage, pageWidth * 0.2f, pageHeight * 0.1f));
            waterMarkPdfContent.addImage(getSingletonWaterMarkImage(waterMarkImage, pageWidth * 0.2f, pageHeight * 0.4f));
            waterMarkPdfContent.addImage(getSingletonWaterMarkImage(waterMarkImage, pageWidth * 0.2f, pageHeight * 0.7f));
            waterMarkPdfContent.addImage(getSingletonWaterMarkImage(waterMarkImage, pageWidth * 0.6f, pageHeight * 0.2f));
            waterMarkPdfContent.addImage(getSingletonWaterMarkImage(waterMarkImage, pageWidth * 0.6f, pageHeight * 0.5f));
            waterMarkPdfContent.addImage(getSingletonWaterMarkImage(waterMarkImage, pageWidth * 0.6f, pageHeight * 0.8f));
            PdfGState gs = new PdfGState();
            gs.setFillOpacity(0.5f);//设置透明度
            waterMarkPdfContent.setGState(gs);
        } catch (DocumentException | IOException de) {
            de.printStackTrace();
        }
    }

    /**
     * 对一个图片对象设置展示位置等信息，该对象重复利用，减少PDF文件大小
     *
     * @param waterMarkImage
     * @param xPosition
     * @param yPosition
     * @return
     */
    private Image getSingletonWaterMarkImage(Image waterMarkImage, float xPosition, float yPosition) {
        waterMarkImage.setAbsolutePosition(xPosition, yPosition);//坐标
        waterMarkImage.setRotation(-20);//旋转 弧度
        waterMarkImage.setRotationDegrees(-45);//旋转 角度
        waterMarkImage.scalePercent(100);//依照比例缩放
        return waterMarkImage;
    }
}