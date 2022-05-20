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
        int firstKey = -1111;
        for (int keyWord : keyWords) {
            if (KeyEvent.VK_SHIFT == keyWord) {
                firstKey = KeyEvent.VK_SHIFT;
            } else if (firstKey != -1111) {      //开始组合字
                AwtUtil.getRobot().keyPress(firstKey);
                setRandomSleep();
                AwtUtil.getRobot().keyPress(keyWord);
                setRandomSleep();
                AwtUtil.getRobot().keyRelease(firstKey);
                setRandomSleep();
                AwtUtil.getRobot().keyRelease(keyWord);
                setRandomSleep();
                firstKey = -1111;
            } else {             //单个字
                AwtUtil.getRobot().keyPress(keyWord);
                setRandomSleep();
                AwtUtil.getRobot().keyRelease(keyWord);
                setRandomSleep();
            }

        }
    }

    /**
     * 随机休眠防止被检测
     */
    private static void setRandomSleep() {
        int randomSleepNum = AwtUtil.mRandom.nextInt(100);
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
