package utils;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Description：销售话语
 *
 * @data:2022/5/14 下午2:13
 * @author: ZhengXiang Sun
 */
public class SalesWordUtil {

    private static Robot mRobot;
    private static Random mRandom = new Random();

    public SalesWordUtil() {
    }

    /**
     * 鼠标点击神武，使其获取程序焦点
     */
    private void focusOnSw() {

    }

    /**
     * 喊话
     */
    public static void shoutOnTheWorld(List<Integer> keyWords) {
        // robot init
        try {
            if (mRobot == null) mRobot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        int firstKey = -1111;
        for (int keyWord : keyWords) {
            if (KeyEvent.VK_SHIFT == keyWord) {
                firstKey = KeyEvent.VK_SHIFT;
            } else if (firstKey != -1111) {      //开始组合字
                mRobot.keyPress(firstKey);
                setRandomSleep();
                mRobot.keyPress(keyWord);
                setRandomSleep();
                mRobot.keyRelease(firstKey);
                setRandomSleep();
                mRobot.keyRelease(keyWord);
                setRandomSleep();
                firstKey = -1111;
            } else {             //单个字
                mRobot.keyPress(keyWord);
                setRandomSleep();
                mRobot.keyRelease(keyWord);
                setRandomSleep();
            }

        }
    }

    /**
     * 随机休眠防止被检测
     */
    private static void setRandomSleep() {
        int randomSleepNum = mRandom.nextInt(100);
        int sleepTime = randomSleepNum + 200;
        System.out.println("random sleep num:" + randomSleepNum + "  sleep time:" + sleepTime + "ms..."
                + "\n Current thread name:" + Thread.currentThread().getName());
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
