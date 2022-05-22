import module.sell.SellMain;
import module.sm.SmMain;
import utils.AwtUtil;
import utils.SalesWordUtil;
import utils.ScreenUtil;
import utils.ThreadPoolUtil;
import constant.ConstantSaleWord;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import static org.opencv.core.Core.NATIVE_LIBRARY_NAME;

/**
 * Description：
 *
 * @data:2022/5/14 下午1:49
 * @author: ZhengXiang Sun
 */
public class SwPluginMainApp {

    /**
     * 世界喊话开关
     */
    private static boolean mIsSaleOnTheWorld = true;

    /**
     * 做师门开光
     */
    private static boolean mIsDoSm = false;

    /**
     * 获取屏幕坐标点
     */
    private static boolean mIsShowCurrentPoint = false;

    public static Random mRandom = new Random();

    public static void main(String[] args) {
        //init robot
        AwtUtil.init();
        //加载OpenCv库
        System.loadLibrary(NATIVE_LIBRARY_NAME);
        //shout on the world
        shoutOnTheWorld();
        //show current point
        showCurrentPoint();
        //做师门
        doSm();
    }

    /**
     * 做师门
     */
    private static void doSm() {
        ThreadPoolUtil.getInstance().execute(() -> {
            if(mIsDoSm) SmMain.start();
        });
    }

    /**
     * show current point
     */
    private static void showCurrentPoint() {
        ThreadPoolUtil.getInstance().execute(() -> {
            ScreenUtil.getScreenShot(1172,471,107,15,null);
            if (mIsShowCurrentPoint) {
                while (true) {
                    try {
                        Thread.sleep(800);
                        //获取鼠标坐标点
                        ScreenUtil.getPointInfo();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    /**
     * shout on the world
     */
    private static void shoutOnTheWorld() {
        ThreadPoolUtil.getInstance().execute(() -> {
            if(mIsSaleOnTheWorld){   //世界卖钱
                SellMain.start();
            }
        });
    }
}
