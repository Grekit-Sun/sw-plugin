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

    public static final String DIR_RES = "/Users/sun/dev/00_IDEA/workspace/sw-plugin/src/main/resources/";

    private static List<String> mXyList = new CopyOnWriteArrayList<>();

    /**
     * 获取屏幕坐标
     */
    public static SwPointBean getPointInfo() {
        SwPointBean pointBean = new SwPointBean();
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
            System.out.println("x:" + mx + ",y:" + my + "  mouse color:" + AwtUtil.getRobot().getPixelColor(x, y));
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
        //表示截取以（150，500）为坐上顶点的，200px*200px大小的图
        BufferedImage bufferedImage = AwtUtil.getRobot().createScreenCapture(new Rectangle(x, y, width, height));
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
