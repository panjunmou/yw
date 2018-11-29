package com.bootdo.common.utils;

import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;

/**
 * 加载水印工具类
 *
 * @author Administrator
 */
public class WaterMarkUtil {

    private static final Logger logger = LoggerFactory.getLogger(WaterMarkUtil.class);

    /**
     * 加水印
     *
     * @param inPdfFilePath
     * @param outPdfFilePath
     * @param waterMark
     * @param isText
     * @throws Exception
     */
    public static void setPdfWatermark(String inPdfFilePath, String outPdfFilePath, String waterMark, Boolean isText)
            throws Exception {
        // 待加水印的文件
        PdfReader reader = new PdfReader(inPdfFilePath);
        // 加完水印的文件
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outPdfFilePath));
        try {
            int total = reader.getNumberOfPages() + 1;

            PdfContentByte content;
            // 设置字体
           /* BaseFont base = BaseFont.createFont(ApplicationUtil.getAppAbsolutePath() + "/fonts/SIMSUN.TTC,1",
                    BaseFont.IDENTITY_H, BaseFont.EMBEDDED);*/
            File file = ResourceUtils.getFile("classpath:static/fonts");
            String absolutePath = file.getAbsolutePath();
            BaseFont base = BaseFont.createFont(absolutePath + "/SIMSUN.TTC,1", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            // 循环对每页插入水印
            if (isText) {
                for (int i = 1; i < total; i++) {
                    // 在内容上方加水印
                    content = stamper.getOverContent(i);
                    // 开始
                    content.beginText();
                    // 设置颜色
                    // 设置字体及字号
                    // 设置起始位置
                    content.setColorFill(Color.LIGHT_GRAY);
                    content.setFontAndSize(base, 20);
                    content.setTextMatrix(70, 200);

                    Rectangle pageRect = reader.getPageSizeWithRotation(i);
                    float rectHeight = pageRect.getHeight();
                    float pageRectWidth = pageRect.getWidth();

                    for (int height = 30; height < rectHeight; height = height + 25 * 2) {
                        for (int width = 30; width < pageRectWidth; width = width + waterMark.length() * 30) {
                            content.showTextAligned(Element.ALIGN_LEFT, waterMark, width, height, 30);
                        }
                    }
                    content.endText();
                }
            } else {
                PdfGState gs = new PdfGState();
                gs.setFillOpacity(0.5f);
                gs.setStrokeOpacity(0.5f);
                Image image = Image.getInstance(waterMark);
                for (int i = 1; i < total; i++) {
                    content = stamper.getUnderContent(i);
                    content.setGState(gs);
                    image.setAbsolutePosition(150, 206);
                    content.addImage(image);
                }
            }
        } catch (Exception e) {
            logger.error("添加水印失败：" + e);
        } finally {
            reader.close();
            stamper.close();
        }
    }
}
