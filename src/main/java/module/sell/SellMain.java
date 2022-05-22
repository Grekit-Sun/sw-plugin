package module.sell;

import constant.ConstantSaleWord;
import constant.ConstantScreen;
import utils.AwtUtil;
import utils.SalesWordUtil;
import utils.ThreadPoolUtil;

import java.awt.*;

public class SellMain extends SalesWordUtil {

    private static final int INTERVAL_SPEAK = 50;

    public static void start() {
        init();
    }

    /**
     * 初始化
     */
    private static void init() {
        //开线程卖
        startSell();
        //开个线程监听好友信息
        startFreMonitor(true);
    }


    private static void startFreMonitor(boolean isStart) {
        ThreadPoolUtil.getInstance().execute(() -> {
            while (isStart) {
                Color pixelColor = AwtUtil.getRobot().getPixelColor(ConstantScreen.FRE_X, ConstantScreen.FRE_Y);
                if (pixelColor.getRed() != 234 && pixelColor.getGreen() != 189 && pixelColor.getBlue() != 125) {      //有好友通知
                    System.out.println("好友来信息了...");
                    ThreadPoolUtil.sleep(200);
                    synchronized (SellMain.class) {
                        //点击好友
                        AwtUtil.getRobot().mouseMove(ConstantScreen.FRE_X, ConstantScreen.FRE_Y);
                        AwtUtil.performLeftMouseClick(1);
                        //鼠标移动到对话框
                        AwtUtil.getRobot().mouseMove(ConstantScreen.FRE_DIAG_X, ConstantScreen.FRE_DIAG_Y);
                        AwtUtil.performLeftMouseClick(1);
                        //打售卖信息
                        //3.5 594  10 1800  (v13952025349)
                        tellPreAndV(ConstantSaleWord.getTellTellInfo());
                        //关闭好友
                        AwtUtil.getRobot().mouseMove(ConstantScreen.FRE_CLOSE_X, ConstantScreen.FRE_CLOSE_Y);
                        AwtUtil.performLeftMouseClick(1);
                    }
                    ThreadPoolUtil.sleep(5 * 1000);
//                    Color c = AwtUtil.getRobot().getPixelColor(ConstantScreen.FRE_CLOSE_X, ConstantScreen.FRE_CLOSE_Y);
//                    if(c.getRed() == 234 && c.getGreen() == 189 && c.getBlue() == 125){
//                    }
                }
            }
        });
    }

    /**
     * 世界喊
     */
    private static void startSell() {
        ThreadPoolUtil.getInstance().execute(() -> {
            ThreadPoolUtil.sleep(AwtUtil.mRandom.nextInt(10 * 1000) + INTERVAL_SPEAK * 1000);
            synchronized (SellMain.class) {
                shoutOnTheWorld(ConstantSaleWord.getSaleWord());
            }
        });
    }
}
