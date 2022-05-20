package utils;

import bean.SwPointBean;
import constant.ConstantScreen;
import module.sm.SmMain;
import org.opencv.core.Point;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ScreenUtil {

    private static Robot mRobot;
    private static final String DIR_RES = "/Users/sun/dev/00_IDEA/workspace/sw-plugin/src/main/resources/";

    private static List<String> mXyList = new CopyOnWriteArrayList<>();

    static int minR = 255;
    static int maxR = 0;
    static int minG = 255;
    static int maxG =0;
    static int minB = 255;
    static  int maxB = 0;
    /**
     * 获取屏幕坐标
     */
    public static SwPointBean getPointInfo() {
        SwPointBean pointBean = new SwPointBean();
        try {
            if (mRobot == null) mRobot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        int x = 0;
        int y = 0;
        PointerInfo pinfo = MouseInfo.getPointerInfo();
        int mx = pinfo.getLocation().x;
        int my = pinfo.getLocation().y;
        if (x != mx || y != my) {
            x = mx;
            y = my;
            pointBean.x = x;
            pointBean.y = y;
//            System.out.println("x:" + mx + ",y:" + my + "  mouse color:" + mRobot.getPixelColor(x, y));
            Color pixelColor = mRobot.getPixelColor(ConstantScreen.TASK_CHALLENGE_2_X, ConstantScreen.TASK_CHALLENGE_2_Y);
            maxR = Math.max(maxR,pixelColor.getRed());
            minR = Math.min(minR,pixelColor.getRed());

            maxG = Math.max(maxG,pixelColor.getGreen());
            minG = Math.min(minG,pixelColor.getGreen());

            maxB = Math.max(maxB,pixelColor.getBlue());
            minB = Math.min(minB,pixelColor.getBlue());
            String xy = "(maxR:" + maxR + ",minR:" + minR + ")" + "(maxG:" + maxG + ",minG:" + minG + ")" + "(maxB:" + maxB + ",minB:" + minB + ")";
            mXyList.add(xy);
            System.out.println(xy);
            if(mXyList.size() > 10) {
                ThreadPoolUtil.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        FileUtil.stringWriteIntoFile(mXyList, DIR_RES + "file/sm.txt");
                        mXyList.clear();
                    }
                });
            }

        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        return pointBean;
    }

    /**
     * 获取屏幕截图
     *
     * @param x
     * @param y
     * @param width
     * @param height
     * @return
     */
    public static BufferedImage getScreenShot(int x, int y, int width, int height, String path) {
        // robot init
        try {
            if (mRobot == null) mRobot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        //表示截取以（150，500）为坐上顶点的，200px*200px大小的图
        BufferedImage bufferedImage = mRobot.createScreenCapture(new Rectangle(x, y, width, height));
        try {
            if (path != null && !path.isEmpty()) {
                ImageIO.write(bufferedImage, "jpg", new File(DIR_RES + path));
            }
            ImageIO.write(bufferedImage, "jpg", new File(DIR_RES + "buffer/screenshot.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bufferedImage;
    }
}
