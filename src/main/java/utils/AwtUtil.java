package utils;

import java.awt.*;
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
}
