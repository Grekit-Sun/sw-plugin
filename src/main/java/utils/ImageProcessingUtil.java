package utils;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImageProcessingUtil {

    public static void compareImage(String targetImagePath , String baseImagePath){
        compareHist_2(targetImagePath,baseImagePath);
    }

    /**
     * OpenCV-4.0.0 直方图比较
     *
     * @return: void
     * @date: 2020年1月14日20:15:39
     */
    public static void compareHist_2(String targetImagePath , String baseImagePath) {
        Mat src_1 = Imgcodecs.imread(targetImagePath);// 图片 1
        Mat src_2 = Imgcodecs.imread(baseImagePath);// 图片 2

        Mat hvs_1 = new Mat();
        Mat hvs_2 = new Mat();
        //图片转HSV
        Imgproc.cvtColor(src_1, hvs_1,Imgproc.COLOR_BGR2HSV);
        Imgproc.cvtColor(src_2, hvs_2,Imgproc.COLOR_BGR2HSV);

        Mat hist_1 = new Mat();
        Mat hist_2 = new Mat();

        //直方图计算
        Imgproc.calcHist(Stream.of(hvs_1).collect(Collectors.toList()),new MatOfInt(0),new Mat(),hist_1,new MatOfInt(255) ,new MatOfFloat(0,256));
        Imgproc.calcHist(Stream.of(hvs_2).collect(Collectors.toList()),new MatOfInt(0),new Mat(),hist_2,new MatOfInt(255) ,new MatOfFloat(0,256));

        //图片归一化
        Core.normalize(hist_1, hist_1, 1, hist_1.rows() , Core.NORM_MINMAX, -1, new Mat() );
        Core.normalize(hist_2, hist_2, 1, hist_2.rows() , Core.NORM_MINMAX, -1, new Mat() );

        //直方图比较
        double a = Imgproc.compareHist(hist_1,hist_1,Imgproc.CV_COMP_CORREL);
        double b = Imgproc.compareHist(hist_1,hist_2, Imgproc.CV_COMP_CORREL);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("越接近1越相识度越高");
        System.out.println("同一张图片\t比较结果(相识度)："+a);
        System.out.println("不同图片\t比较结果(相识度)："+b);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
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
        Imgproc.cvtColor(src, hsv,Imgproc.COLOR_BGR2HSV);

        Mat hist = new Mat();
        //直方图计算
        Imgproc.calcHist(Stream.of(hsv).collect(Collectors.toList()),new MatOfInt(0),new Mat(),hist,new MatOfInt(255) ,new MatOfFloat(0,256));
        //图片归一化
        Core.normalize(hist, hist, 1, hist.rows() , Core.NORM_MINMAX, -1, new Mat() );
        //直方图比较
        double a = Imgproc.compareHist(hist,hist,Imgproc.CV_COMP_CORREL);
        System.out.println("越接近1越相识度越高\n比较结果："+a);
    }
}
