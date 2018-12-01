package com.bootdo.common.utils;

import com.bootdo.common.config.BootdoConfig;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

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
            /*File file = ResourceUtils.getFile("classpath:static/fonts");
            String absolutePath = file.getAbsolutePath();
            System.out.println("absolutePath = " + absolutePath);*/

            BaseFont base = BaseFont.createFont("D:/fonts/SIMSUN.TTC,1", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
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
                    content.setColorFill(Color.lightGray);
                    content.setFontAndSize(base, 50);
                    content.setTextMatrix(70, 200);
                    PdfGState gs = new PdfGState();
                    gs.setFillOpacity(0.3f);// 设置透明度为0.8
                    content.setGState(gs);

                    Rectangle pageRect = reader.getPageSizeWithRotation(i);
                    float rectHeight = pageRect.getHeight();
                    float pageRectWidth = pageRect.getWidth();

                    for (int height = 30; height < rectHeight; height = height + 50 * 2) {
                        for (int width = 30; width < pageRectWidth; width = width + waterMark.length() * 50) {
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

    public static void setImgWatermark(String sourceImgPath, String tarImgPath, String waterMarkContent, String fileExt) {
        Font font = new Font("宋体", Font.BOLD, 100);//水印字体，大小
        Color markContentColor = Color.lightGray;//水印颜色
        Integer degree = 45;//设置水印文字的旋转角度
        float alpha = 0.4f;//设置水印透明度
        OutputStream outImgStream = null;
        try {
            File srcImgFile = new File(sourceImgPath);//得到文件
            java.awt.Image srcImg = ImageIO.read(srcImgFile);//文件转化为图片
            int srcImgWidth = srcImg.getWidth(null);//获取图片的宽
            int srcImgHeight = srcImg.getHeight(null);//获取图片的高
            // 加水印
            BufferedImage bufImg = new BufferedImage(srcImgWidth, srcImgHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = bufImg.createGraphics();//得到画笔
            g.drawImage(srcImg, 0, 0, srcImgWidth, srcImgHeight, null);
            g.setColor(markContentColor); //设置水印颜色
            g.setFont(font);              //设置字体
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));//设置水印文字透明度
            if (null != degree) {
                g.rotate(Math.toRadians(degree));//设置水印旋转
            }
            JLabel label = new JLabel(waterMarkContent);
            FontMetrics metrics = label.getFontMetrics(font);
            int width = metrics.stringWidth(label.getText());//文字水印的宽
            int rowsNumber = srcImgHeight / width;// 图片的高  除以  文字水印的宽    ——> 打印的行数(以文字水印的宽为间隔)
            int columnsNumber = srcImgWidth / width;//图片的宽 除以 文字水印的宽   ——> 每行打印的列数(以文字水印的宽为间隔)
            //防止图片太小而文字水印太长，所以至少打印一次
            if (rowsNumber < 1) {
                rowsNumber = 1;
            }
            if (columnsNumber < 1) {
                columnsNumber = 1;
            }
            for (int j = 0; j < rowsNumber; j++) {
                for (int i = 0; i < columnsNumber; i++) {
                    g.drawString(waterMarkContent, i * width + j * width, -i * width + j * width);//画出水印,并设置水印位置
                }
            }
            g.dispose();// 释放资源
            // 输出图片
            outImgStream = new FileOutputStream(tarImgPath);
            ImageIO.write(bufImg, fileExt, outImgStream);
        } catch (Exception e) {
            e.printStackTrace();
            e.getMessage();
        } finally {
            try {
                if (outImgStream != null) {
                    outImgStream.flush();
                    outImgStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                e.getMessage();
            }
        }
    }

}
