package utils;

import org.apache.commons.beanutils.PropertyUtilsBean;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;

public class AwtUtil {
    /**
     * 机器变量
     */
    private static Robot mRobot;

    public static Random mRandom = new Random();

    /**
     * 初始化
     */
    public static void init() {
        try {
            if (mRobot == null) mRobot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public static Robot getRobot() {
        try {
            if (mRobot == null) mRobot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        return mRobot;
    }

    public static void setRandomDelay(){
        try {
            if (mRobot == null) mRobot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        int randomSleepNum = mRandom.nextInt(100);
        int sleepTime = randomSleepNum + 200;
        mRobot.delay(sleepTime);
    }

    public static void performLeftMouseClick (int times){
        for (int i = 0 ; i < times ; i ++){
            mRobot.delay(mRandom.nextInt(500) + 1000);
            mRobot.mousePress(KeyEvent.BUTTON1_DOWN_MASK);
            mRobot.delay(mRandom.nextInt(100));
            mRobot.mouseRelease(KeyEvent.BUTTON1_DOWN_MASK);
            mRobot.delay(500 + mRandom.nextInt(100));
        }
    }
}
