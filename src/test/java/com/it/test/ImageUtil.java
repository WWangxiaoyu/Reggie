package com.it.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 图片处理工具类
 *
 * @author zhj
 * @since 2021-09-15
 */
@Slf4j
public class ImageUtil {

    /**
     * 生成背景透明的 文字水印，文字位于透明区域正中央，可设置旋转角度
     *
     * @param width  生成图片宽度
     * @param height 生成图片高度
     * @param text   水印文字
     * @param color  颜色对象
     * @param font   awt字体
     * @param degree 水印文字旋转角度
     * @param alpha  水印不透明度0f-1.0f
     */
    public static BufferedImage waterMarkByText(int width, int height, String text, Color color,
                                                Font font, Double degree, float alpha) {
        //获取真实宽度
        int realWidth = (int)Math.ceil(getRealFontWidth(font, text));
        int realHeight = (int)Math.ceil(getRealFontHeight(font));

        // 创建画布
        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // 得到画笔对象
        Graphics2D g2d = buffImg.createGraphics();

        // ----------  增加下面的代码使得背景透明  -----------------
//        buffImg = g2d.getDeviceConfiguration()
//                .createCompatibleImage(width, height, Transparency.TRANSLUCENT);
//        g2d.dispose();
//        g2d = buffImg.createGraphics();
        // ----------  背景透明代码结束  -----------------

        // 设置对线段的锯齿状边缘处理
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        //把源图片写入
//            g2d.drawImage(srcImg.getScaledInstance(srcImg.getWidth(null),
//                    srcImg.getHeight(null), Image.SCALE_SMOOTH), 0, 0,null);

        // 设置水印旋转
        if (null != degree) {
            //注意rotate函数参数theta，为弧度制，故需用Math.toRadians转换一下
            //以矩形区域中央为圆心旋转
            g2d.rotate(Math.toRadians(degree), (double) buffImg.getWidth() / 2,
                    (double) buffImg.getHeight() / 2);
        }

        // 设置颜色
        g2d.setColor(color);

        // 设置 Font
        g2d.setFont(font);

        //设置透明度:1.0f为透明度 ，值从0-1.0，依次变得不透明
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        float fontSize = font.getSize();
        //计算绘图偏移x、y，使得使得水印文字在图片中居中
        //这里需要理解x、y坐标是基于Graphics2D.rotate过后的坐标系
        float x = 0;
        float y = fontSize - 2;
        //取绘制的字串宽度、高度中间点进行偏移，使得文字在图片坐标中居中
        g2d.drawString(text, x, y);
        //释放资源
        g2d.dispose();
        // 根据宽高缩放
//        return modifyImageScale(buffImg, width, height);
        return buffImg;
    }

    public static BufferedImage waterMarkByText(int width, int heigth, String text, Color color, float alpha) {
        //jdk默认字体
        Font font = new Font("Dialog", Font.ROMAN_BASELINE, 33);
        return waterMarkByText(width, heigth, text, color, font, -30d, alpha);
    }

    public static BufferedImage waterMarkByText(int width, int heigth, String text, float alpha) {
        return waterMarkByText(width, heigth, text, Color.GRAY, alpha);
    }


    public static BufferedImage waterMarkByText(int width, int heigth, String text) {
        return waterMarkByText(width, heigth, text, 0.2f);
    }

    public static BufferedImage waterMarkByText(String text) {
        return waterMarkByText(200, 150, text);
    }

    /**
     * 获取真实字符串宽度
     */
    private static float getRealFontWidth(Font font, String text) {
        // 整个字符串的宽度
        if (StringUtils.isNotBlank(text)) {
            FontMetrics fm = sun.font.FontDesignMetrics.getMetrics(font);
            return fm.stringWidth(text);
        } else {
            return 100f;
        }
    }

    /**
     * 获取真实字符串高度
     */
    private static float getRealFontHeight(Font font) {
        // 高度
        FontMetrics fm = sun.font.FontDesignMetrics.getMetrics(font);
        return fm.getHeight();
    }

    /**
     * 图片缩放
     *
     * @param sourceImg 原图
     * @param width     缩放宽度
     * @param height    生成高度
     * @return 缩放后图片
     */
    public static BufferedImage modifyImageScale(BufferedImage sourceImg, int width, int height) {
        //设置生成图片宽*高，色彩
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        //创建画布
        Graphics2D g2 = bi.createGraphics();
        //设置图片透明  注********：只有png格式的图片才能设置背景透明，jpg设置图片颜色变的乱七八糟
        bi = g2.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
        //重新创建画布
        g2 = bi.createGraphics();
        //画图
        g2.drawImage(sourceImg, 0, 0, width, height, null);
        //关闭资源
        g2.dispose();
        return bi;
    }


    /**
     * 生成时间戳图片
     *
     * @param width 宽度
     * @param height 高度
     * @return 时间戳图片
     */
    public static byte[] getDateImg(Float width, Float height, int fontSize) {
        if (width == null || height == null) {
            log.error(">>>>>>>>>> 【ERROR】时间戳宽高异常，请稍后重试 width: {}, height:{}", width, height);
            throw new IllegalArgumentException("时间戳宽高异常，请稍后重试");
        }
        // 设置字体
        Font font = new Font("微软雅黑", Font.PLAIN, fontSize);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(" yyyy年MM月dd日");
        String text = LocalDate.now().format(dateFormatter);
        // 给图片添加文字水印
        int w = (int)Math.ceil(width);
        int h = (int)Math.ceil(height);
        BufferedImage bi1 = waterMarkByText(w, h, text, Color.BLACK, font, 0d, 1f);
        try {
            // 写入文件
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(bi1, "png", out);
            return out.toByteArray();
        } catch (IOException e) {
            log.error(">>>>>>>>>> 【ERROR】时间戳添加失败，请联系管理员: {}", e.getMessage(), e);
            throw new IllegalArgumentException("时间戳添加失败，请联系管理员");
        }
    }

    /**
     * 生成指定日期格式的时间戳图片
     * @param width
     * @param height
     * @param fontSize
     * @param format
     * @return
     */
    public static byte[] getFormatDateImg(Float width, Float height, int fontSize, String format) {
        if (width == null || height == null) {
            log.error(">>>>>>>>>> 【ERROR】时间戳宽高异常，请稍后重试 width: {}, height:{}", width, height);
            throw new IllegalArgumentException("时间戳宽高异常，请稍后重试");
        }
        // 设置字体
        Font font = new Font("微软雅黑", Font.PLAIN, fontSize);
        DateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date();
        String text = dateFormat.format(date);
        // 给图片添加文字水印
        int w = (int)Math.ceil(width);
        int h = (int)Math.ceil(height);
        BufferedImage bi1 = waterMarkByText(w, h, text, Color.GREEN, font, 0d, 1f);
        try {
            // 写入文件
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(bi1, "png", out);
            return out.toByteArray();
        } catch (IOException e) {
            log.error(">>>>>>>>>> 【ERROR】时间戳添加失败，请联系管理员: {}", e.getMessage(), e);
            throw new IllegalArgumentException("时间戳添加失败，请联系管理员");
        }
    }

    public static void main(String[] args) {
        int width = 140;
        int height = 18;
        Font font = new Font("微软雅黑", Font.PLAIN, 18);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
        String text = LocalDate.now().format(dateFormatter);
        // 给图片添加文字水印
        BufferedImage bi1 = waterMarkByText(width, height, text, Color.BLACK, font, 0d, 1f);
        try {
            // 写入文件
            ImageIO.write(bi1, "png", new File("D:/test.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}