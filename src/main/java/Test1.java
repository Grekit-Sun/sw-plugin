import models.DarkChannelPriorDehaze;
import module.sm.SmMain;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import utils.ImShow;
import utils.SalesWordUtil;
import utils.ThreadPoolUtil;
import utils.constant.ConstantSaleWord;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;

/**
 * Description：
 *
 * @data:2022/5/14 下午1:49
 * @author: ZhengXiang Sun
 */
public class Test1 {

    private static boolean mIsSaleOnTheWorld = false;
    private static boolean mIsDoSm = true;

    private static Random mRandom = new Random();
    private static final int INTERVAL_SPEAK = 50;

    // Paper url: https://www.robots.ox.ac.uk/~vgg/rg/papers/hazeremoval.pdf
    private static final double krnlRatio = 0.01; // set kernel ratio
    private static final double minAtmosLight = 240.0; // set minimum atmospheric light
    private static final double eps = 0.000001;

    public static void main(String[] args) {
        ThreadPoolUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                while (mIsSaleOnTheWorld) {     //世界卖钱
                    try {
                        Thread.sleep(mRandom.nextInt(10 * 1000) + INTERVAL_SPEAK * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    SalesWordUtil.shoutOnTheWorld(ConstantSaleWord.getSaleWord());
                }
            }
        });

        ThreadPoolUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
//                SmMain.compareImage();
                SmMain.getPointInfo();
            }
        });


        ThreadPoolUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {         //做师门
                while (mIsDoSm) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    SmMain.compareWithLocalSm();
                    String imgPath = "src/main/resources/haze_images/train.bmp";
                    Mat image = Imgcodecs.imread(imgPath, Imgcodecs.IMREAD_COLOR);
                    new ImShow("Original").showImage(image);
                    Mat result = DarkChannelPriorDehaze.enhance(image, krnlRatio, minAtmosLight, eps);
                    new ImShow("Dehazing").showImage(result);
//                    SmMain.moveMouse(460 + mRandom.nextInt(100), 480 + mRandom.nextInt(100));
                }
            }
        });
    }
}
