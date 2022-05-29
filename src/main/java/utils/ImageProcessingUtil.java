package utils;

import bean.SwPointBean;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImageProcessingUtil {

    /**
     * 模板匹配
     * RE：https://blog.csdn.net/datouniao1/article/details/108449562
     */
    public static SwPointBean matchTemplate(String targetImagePath, String baseImagePath) {
        SwPointBean pointBean = new SwPointBean();
        Mat src_1 = Imgcodecs.imread(targetImagePath);// 图片 1
        Mat src_2 = Imgcodecs.imread(baseImagePath);// 图片 2
        Mat result = new Mat();
        int method = Imgproc.TM_CCOEFF;
        Imgproc.matchTemplate(src_2, src_1, result, Imgproc.TM_CCOEFF);
        Core.MinMaxLocResult result_m = Core.minMaxLoc(result);//取最大值和最小值
        Point maxloc = result_m.maxLoc;
        Point minloc = result_m.minLoc;
        double maxVal = result_m.maxVal;
        double minVal = result_m.minVal;
//        System.out.println("maxVal:" + maxVal + "  minVal:" + minVal);
        //获取坐标
        Point p1;
        //如果是平方不同或者归一化平方不同,那么就取最小值
        p1 = (method == Imgproc.TM_SQDIFF || method == Imgproc.TM_SQDIFF_NORMED) ? minloc : maxloc;

        Point p2 = new Point(p1.x + src_1.cols(), p1.y + src_1.rows());

        //绘制
        Imgproc.rectangle(src_2, p1, p2, new Scalar(0, 0, 255));
        System.out.println("矩阵顶点:" + p1.toString() + "  矩阵顶点:" + p2.toString());

        HighGui.imshow("原图", src_2);
        HighGui.imshow("模板", src_1);
        HighGui.waitKey(10);

        pointBean.x = (int)p1.x;
        pointBean.y = (int)p1.y;
        pointBean.width = src_1.cols();
        pointBean.height = src_1.rows();
        return pointBean;
    }

    public static double compareImage(String targetImagePath, String baseImagePath) {
        return compareHist_2(targetImagePath, baseImagePath);
    }

    /**
     * OpenCV-4.0.0 直方图比较
     *
     * @return: void
     * @date: 2020年1月14日20:15:39
     */
    public static double compareHist_2(String targetImagePath, String baseImagePath) {
        Mat src_1 = Imgcodecs.imread(targetImagePath);// 图片 1
        Mat src_2 = Imgcodecs.imread(baseImagePath);// 图片 2

        Mat hvs_1 = new Mat();
        Mat hvs_2 = new Mat();
        //图片转HSV
        Imgproc.cvtColor(src_1, hvs_1, Imgproc.COLOR_BGR2HSV);
        Imgproc.cvtColor(src_2, hvs_2, Imgproc.COLOR_BGR2HSV);

        Mat hist_1 = new Mat();
        Mat hist_2 = new Mat();

        //直方图计算
        Imgproc.calcHist(Stream.of(hvs_1).collect(Collectors.toList()), new MatOfInt(0), new Mat(), hist_1, new MatOfInt(255), new MatOfFloat(0, 256));
        Imgproc.calcHist(Stream.of(hvs_2).collect(Collectors.toList()), new MatOfInt(0), new Mat(), hist_2, new MatOfInt(255), new MatOfFloat(0, 256));

        //图片归一化
        Core.normalize(hist_1, hist_1, 1, hist_1.rows(), Core.NORM_MINMAX, -1, new Mat());
        Core.normalize(hist_2, hist_2, 1, hist_2.rows(), Core.NORM_MINMAX, -1, new Mat());

        //直方图比较
        double a = Imgproc.compareHist(hist_1, hist_1, Imgproc.CV_COMP_CORREL);
        double b = Imgproc.compareHist(hist_1, hist_2, Imgproc.CV_COMP_CORREL);
//        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//        System.out.println("越接近1越相识度越高");
//        System.out.println("同一张图片\t比较结果(相识度)："+a);
//        System.out.println("不同图片\t比较结果(相识度)："+b);
//        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        return b;
    }

    /**
     * OpenCV-4.0.0 直方图比较
     *
     * @return: void
     * @date: 2020年1月14日20:15:39
     */
    public static void compareHist_1() {
        Mat src = Imgcodecs.imread("C:\\Users\\Administrator\\Pictures\\3.jpeg");

        Mat hsv = new Mat();

        //图片转HSV
        Imgproc.cvtColor(src, hsv, Imgproc.COLOR_BGR2HSV);

        Mat hist = new Mat();
        //直方图计算
        Imgproc.calcHist(Stream.of(hsv).collect(Collectors.toList()), new MatOfInt(0), new Mat(), hist, new MatOfInt(255), new MatOfFloat(0, 256));
        //图片归一化
        Core.normalize(hist, hist, 1, hist.rows(), Core.NORM_MINMAX, -1, new Mat());
        //直方图比较
        double a = Imgproc.compareHist(hist, hist, Imgproc.CV_COMP_CORREL);
        System.out.println("越接近1越相识度越高\n比较结果：" + a);
    }

    /**
     * 图片转文字
     * https://www.csdn.net/tags/Mtzacg4sOTIwMzktYmxvZwO0O0OO0O0O.html
     *
     * @return
     */
    public static String imageToText(BufferedImage image) {
        Tesseract tesseract = new Tesseract();
        String resText = "";
        //设置字库
        tesseract.setDatapath("/usr/local/Cellar/tesseract/5.1.0/share/tessdata");
        //如果需要识别英文之外的语种，需要指定识别语种，并且需要将对应的语言包放进项目中
        // chi_sim ：简体中文， eng    根据需求选择语言库
        tesseract.setLanguage("chi_sim");
        try {
            Thread.sleep(1000);
            long startTime = System.currentTimeMillis();
            resText = tesseract.doOCR(image);
            // 输出识别结果
            System.out.println("图片->文字结果: \n" + resText + "\n 耗时：" + (System.currentTimeMillis() - startTime) + "ms");
        } catch (TesseractException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return resText;
    }

    /**
     * 图片转文字
     * https://www.csdn.net/tags/Mtzacg4sOTIwMzktYmxvZwO0O0OO0O0O.html
     *
     * @return
     */
    public static String imageToText(String Path) {
        Tesseract tesseract = new Tesseract();
        String resText = "";
        //设置字库
        tesseract.setDatapath("/usr/local/Cellar/tesseract/5.1.0/share/tessdata");
        //如果需要识别英文之外的语种，需要指定识别语种，并且需要将对应的语言包放进项目中
        // chi_sim ：简体中文， eng    根据需求选择语言库
        tesseract.setLanguage("chi_sim");
        try {
            Thread.sleep(1000);
            long startTime = System.currentTimeMillis();
            resText = tesseract.doOCR(new File(Path));
            // 输出识别结果
            System.out.println("图片->文字结果: \n" + resText + "\n 耗时：" + (System.currentTimeMillis() - startTime) + "ms");
        } catch (TesseractException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return resText;
    }

    /**
     * 优化图像清晰度
     *
     * @param image
     * @return
     */
    private static BufferedImage binaryMethod(BufferedImage image) {
        int[][] grayImage = getGrayByImage(image);
        BufferedImage optImage = new BufferedImage(grayImage.length, grayImage[0].length, BufferedImage.TYPE_INT_RGB);
        for (int width = 0; width < grayImage.length; width++) {
            for (int height = 0; height < grayImage[0].length; height++) {
                // 二值化
                if (grayImage[width][height] <= 110) {
                    grayImage[width][height] = 255;
                } else {
                    grayImage[width][height] = 0;
                }
                Color optColor = new Color(grayImage[width][height], grayImage[width][height], grayImage[width][height]);
                optImage.setRGB(width, height, optColor.getRGB());
            }
        }
        return optImage;
    }

    /**
     * 获取图片的灰度值
     *
     * @param image
     * @return
     */
    private static int[][] getGrayByImage(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] imageGray = new int[width][height];
        int minx = image.getMinX();
        int miny = image.getMinY();
        //提取像素灰度值
        for (int i = minx; i < width; i++) {
            for (int j = miny; j < height; j++) {
                imageGray[i][j] = getGrayByPixel(image.getRGB(i, j));
//                System.out.println("gray [i] = " + i + "  [j] = " + j + "  gray = " + imageGray[i][j]);
            }
        }
        return imageGray;
    }


    /**
     * 对比图片相似度
     *
     * @param sourceImage
     * @param cutImage
     */
    public static int compareImage(BufferedImage sourceImage, BufferedImage cutImage) {
        int similarity = 0;
        int different = 0;
        //source
        int[][] sourceGray = getGrayByImage(sourceImage);
        //cut
        int[][] cutGray = getGrayByImage(cutImage);
        // 为了确保程序安全这里还是取两个图片像素中较少的一个作为基准防止越界
        int size1 = Math.min(sourceGray.length, cutGray.length);
        int size2 = Math.min(sourceGray[0].length, cutGray[0].length);
        // 遍历每一个像素点并获取该像素点的灰度
        // 比较两个灰度看是否在一定容差范围内，如果在则认为相似，否则认为不相似
        // 这里比较投机，有需要可以研究分布概率在判断相似度
        // 这里的容差并非是一个标准数据，是本人实验得出来比较合理的容差范围，仅仅代表个人观点
        for (int i = 0; i < size1; i++) {
            for (int j = 0; j < size2; j++) {
                if (Math.abs(sourceGray[i][j] - cutGray[i][j]) <= 20) {
                    similarity++;
                } else {
                    different++;
                }
            }
        }
        int similarPercent = similarity * 100 / (similarity + different);
//        System.out.println("similarity:" + similarity + "  different:" + different + "  Percentage of similarity：" + similarPercent);
        return similarPercent;
    }

    /**
     * 灰度值计算
     *
     * @param pixel
     * @return
     */
    public static int getGrayByPixel(int pixel) {
        // 下面三行代码将一个数字转换为RGB数字
        int r = (pixel & 0xff0000) >> 16;
        int g = (pixel & 0xff00) >> 8;
        int b = (pixel & 0xff);
        return (int) (0.3 * r + 0.59 * g + 0.11 * b);
    }


    /**
     * 图像加亮
     *
     * @param image
     * @return
     */
    public static BufferedImage optImage(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] imageGray = new int[width][height];
        int minx = image.getMinX();
        int miny = image.getMinY();
        //提取像素灰度值
        for (int i = minx; i < width; i++) {
            for (int j = miny; j < height; j++) {
                // 图像加亮（调整亮度识别率非常高）
                int r = (int) (((image.getRGB(i, j) >> 16) & 0xFF) * 1.1 + 30);
                int g = (int) (((image.getRGB(i, j) >> 8) & 0xFF) * 1.1 + 30);
                int b = (int) (((image.getRGB(i, j) >> 0) & 0xFF) * 1.1 + 30);
                //图像加亮；
                r = imageLight(r);
                g = imageLight(g);
                b = imageLight(b);
                //END
                imageGray[i][j] = (int) Math
                        .pow((Math.pow(r, 2.2) * 0.2973 + Math.pow(g, 2.2)
                                * 0.6274 + Math.pow(b, 2.2) * 0.0753), 1 / 2.2);
            }
        }
        // 二值化
        int threshold = ostu(imageGray, width, height);
        BufferedImage binaryBufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (imageGray[x][y] > threshold) {
                    imageGray[x][y] |= 0x00FFFF;
                } else {
                    imageGray[x][y] &= 0xFF0000;
                }
                binaryBufferedImage.setRGB(x, y, imageGray[x][y]);
            }
        }

        //去除干扰线条
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                boolean flag = false;
                if (isBlack(binaryBufferedImage.getRGB(x, y))) {
                    //左右均为空时，去掉此点
                    if (isWhite(binaryBufferedImage.getRGB(x - 1, y)) && isWhite(binaryBufferedImage.getRGB(x + 1, y))) {
                        flag = true;
                    }
                    //上下均为空时，去掉此点
                    if (isWhite(binaryBufferedImage.getRGB(x, y + 1)) && isWhite(binaryBufferedImage.getRGB(x, y - 1))) {
                        flag = true;
                    }
                    //斜上下为空时，去掉此点
                    if (isWhite(binaryBufferedImage.getRGB(x - 1, y + 1)) && isWhite(binaryBufferedImage.getRGB(x + 1, y - 1))) {
                        flag = true;
                    }
                    if (isWhite(binaryBufferedImage.getRGB(x + 1, y + 1)) && isWhite(binaryBufferedImage.getRGB(x - 1, y - 1))) {
                        flag = true;
                    }
                    if (flag) {
                        binaryBufferedImage.setRGB(x, y, -1);
                    }
                }
            }
        }
        // 矩阵打印
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (isBlack(binaryBufferedImage.getRGB(x, y))) {
//                    System.out.print("*");
                } else {
//                    System.out.print(" ");
                }
            }
//            System.out.println();
        }
        return binaryBufferedImage;
    }

    private static boolean isBlack(int colorInt) {
        Color color = new Color(colorInt);
        if (color.getRed() + color.getGreen() + color.getBlue() <= 300) {
            return true;
        }
        return false;
    }

    private static boolean isWhite(int colorInt) {
        Color color = new Color(colorInt);
        if (color.getRed() + color.getGreen() + color.getBlue() > 300) {
            return true;
        }
        return false;
    }

    /**
     * 求出图像处理阈值
     *
     * @param gray
     * @param w
     * @param h
     * @return
     */
    private static int ostu(int[][] gray, int w, int h) {
        int[] histData = new int[w * h];
        // Calculate histogram
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int red = 0xFF & gray[x][y];
                histData[red]++;
            }
        }

        // Total number of pixels
        int total = w * h;

        float sum = 0;
        for (int t = 0; t < 256; t++)
            sum += t * histData[t];

        float sumB = 0;
        int wB = 0;
        int wF = 0;

        float varMax = 0;
        int threshold = 0;

        for (int t = 0; t < 256; t++) {
            wB += histData[t]; // Weight Background
            if (wB == 0)
                continue;

            wF = total - wB; // Weight Foreground
            if (wF == 0)
                break;

            sumB += (float) (t * histData[t]);

            float mB = sumB / wB; // Mean Background
            float mF = (sum - sumB) / wF; // Mean Foreground

            // Calculate Between Class Variance
            float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);

            // Check if new maximum found
            if (varBetween > varMax) {
                varMax = varBetween;
                threshold = t;
            }
        }

        return threshold;
    }

    /**
     * 图像加亮
     *
     * @param color
     * @return
     */
    private static int imageLight(int color) {
        if (color > 255) {
            color = 255;
        }
        return color;
    }
}
