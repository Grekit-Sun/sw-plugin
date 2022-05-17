import module.sm.SmMain;
import utils.SalesWordUtil;
import utils.ScreenUtil;
import utils.ThreadPoolUtil;
import constant.ConstantSaleWord;

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
    private static boolean mIsSaleOnTheWorld = false;

    /**
     * 做师门开光
     */
    private static boolean mIsDoSm = true;

    /**
     * 获取屏幕坐标点
     */
    private static boolean mIsShowCurrentPoint = true;

    private static Random mRandom = new Random();
    private static final int INTERVAL_SPEAK = 50;


    public static void main(String[] args) {
        //加载OpenCv库
        System.loadLibrary(NATIVE_LIBRARY_NAME);
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
                if (mIsShowCurrentPoint) {
                    //获取鼠标坐标点
                    ScreenUtil.getPointInfo();
                }
            }
        });

        ThreadPoolUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {         //做师门
                while (mIsDoSm) {
                    SmMain.start();
                }
            }
        });
    }
}
