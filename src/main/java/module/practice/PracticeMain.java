package module.practice;

import bean.SwPointBean;
import constant.ConstantScreen;
import utils.*;

import java.awt.*;

public class PracticeMain {

    private static final String DIR_RES = "/Users/sun/dev/00_IDEA/workspace/sw-plugin/src/main/resources/";

    public static void start(){
        //开启线程，做修炼
        startPracticeThread();
    }

    private static void startPracticeThread() {
        ThreadPoolUtil.getInstance().execute(()->{
            ThreadPoolUtil.sleep(1000);
            System.out.println("点击日程...");
            AwtUtil.getRobot().mouseMove(ConstantScreen.SCHEDULE_X,ConstantScreen.SCHEDULE_Y);
            AwtUtil.performLeftMouseClick(1);
            ThreadPoolUtil.sleep(200);
            System.out.println("截图...");
            ScreenUtil.getScreenShot(ConstantScreen.SCHEDULE_TASK_X,ConstantScreen.SCHEDULE_TASK_Y,685,210,null);
            System.out.println("开始图像匹配修炼...");
            SwPointBean swPointBean = ImageProcessingUtil.matchTemplate(DIR_RES + "source/practice.jpg", DIR_RES + "buffer/screenshot.jpg");
//            Color p1 = AwtUtil.getRobot().getPixelColor( ConstantScreen.SCHEDULE_TASK_X + swPointBean.x + 50, ConstantScreen.SCHEDULE_TASK_Y +swPointBean.y + 41);
//            Color p2 = AwtUtil.getRobot().getPixelColor(ConstantScreen.SCHEDULE_TASK_X + swPointBean.x + 35,  ConstantScreen.SCHEDULE_TASK_Y + swPointBean.y + 91);
            System.out.println("找到修炼...");
            System.out.println("点击修炼...");
            AwtUtil.getRobot().mouseMove(ConstantScreen.SCHEDULE_TASK_X + swPointBean.x + swPointBean.width/2,
                    ConstantScreen.SCHEDULE_TASK_Y + swPointBean.y + swPointBean.height/2);
            AwtUtil.performLeftMouseClick(1);
            //关闭日程
            System.out.println("关闭日程...");
            AwtUtil.getRobot().mouseMove(ConstantScreen.CLOSE_SCHEDULE_TASK_X ,ConstantScreen.CLOSE_SCHEDULE_TASK_Y );
            AwtUtil.performLeftMouseClick(1);
            boolean isArrivePractice = false;
            while (!isArrivePractice) {
                Color pixelColor = AwtUtil.getRobot().getPixelColor(ConstantScreen.TRADING_CENTER_NEED_PET_CLOSE_X, ConstantScreen.TRADING_CENTER_NEED_PET_CLOSE_Y);
                if ((pixelColor.getRed() == 230 || (pixelColor.getRed() == 237)) && pixelColor.getGreen() == 252 && pixelColor.getBlue() == 254) {
                    isArrivePractice = true;
                    System.out.println("走到了玄修位置...");
//                    ScreenUtil.getScreenShot(1148,547,154,14,null);
                }
                ThreadPoolUtil.sleep(200);
            }
            //领取任务
            receiveTask();
            for(int i = 0; i< 4 ; i++){
                startBuy();
                continueTask();
            }
            //第5环
            boolean needWait = true;
            //监听物资是否需要自己交2
            while (needWait) {
                //关闭桌面遮挡
                Color pixelColor = AwtUtil.getRobot().getPixelColor(ConstantScreen.TRADING_CENTER_NEED_PET_CLOSE_X, ConstantScreen.TRADING_CENTER_NEED_PET_CLOSE_Y);
                if ((pixelColor.getRed() == 230 || (pixelColor.getRed() == 237)) && pixelColor.getGreen() == 252 && pixelColor.getBlue() == 254) {
                    ScreenUtil.getScreenShot(ConstantScreen.ROOT_X, ConstantScreen.ROOT_Y, 1024, 768, null);
                    SwPointBean rs = ImageProcessingUtil.matchTemplate(DIR_RES + "source/receive_practice.jpg", DIR_RES + "buffer/screenshot.jpg");
                    //点击保护道士
                    System.out.println("点击保护道士...");
                    AwtUtil.getRobot().mouseMove(ConstantScreen.ROOT_X + rs.x + rs.width / 2, ConstantScreen.ROOT_Y + rs.y + rs.height / 2);  //
                    AwtUtil.performLeftMouseClick(1);
                    needWait = false;
                }
                ThreadPoolUtil.sleep(800);
            }
        });
    }

    private static void continueTask() {
        boolean needWait = true;
        System.out.println("判断是否需要自己一个个交...");
        //监听物资是否需要自己交2
        Color c = AwtUtil.getRobot().getPixelColor(ConstantScreen.SEND_MATERIALS_X, ConstantScreen.SEND_MATERIALS_Y);
        while (needWait) {
            if ((c.getRed() == 230 || (c.getRed() == 237)) && c.getGreen() == 252 && c.getBlue() == 254) {
                //需要自己交
                MaterialUtil.needSendBySelf();
            }
            //关闭桌面遮挡
            Color pixelColor = AwtUtil.getRobot().getPixelColor(ConstantScreen.TRADING_CENTER_NEED_PET_CLOSE_X, ConstantScreen.TRADING_CENTER_NEED_PET_CLOSE_Y);
            if ((pixelColor.getRed() == 230 || (pixelColor.getRed() == 237)) && pixelColor.getGreen() == 252 && pixelColor.getBlue() == 254) {
                AwtUtil.getRobot().mouseMove(ConstantScreen.TRADING_CENTER_NEED_PET_CLOSE_X, ConstantScreen.TRADING_CENTER_NEED_PET_CLOSE_Y);
                AwtUtil.performLeftMouseClick(2);
                needWait = false;
            }
            ThreadPoolUtil.sleep(800);
        }
    }

    private static void startBuy() {
        MaterialUtil.startFindMaterials();
        System.out.println("交任务...");
        SwPointBean xy = findCanClickXy();
        AwtUtil.getRobot().mouseMove(xy.x, xy.y);
        AwtUtil.performLeftMouseClick(1);
    }

    /**
     * 找到可以点击的点
     *
     * @return
     */
    private static SwPointBean findCanClickXy() {
        System.out.println("开始遍历可点击的任务区域...");
        SwPointBean swPointBean = new SwPointBean();
        for (int i = ConstantScreen.PRACTICE_FIND_X; i < ConstantScreen.PRACTICE_FIND_X + ConstantScreen.PRACTICE_FIND_WIDTH; i++) {
            for (int j = ConstantScreen.PRACTICE_FIND_Y; j < ConstantScreen.PRACTICE_FIND_Y + ConstantScreen.PRACTICE_FIND_HEIGHT; j++) {
                Color pixelColor = AwtUtil.getRobot().getPixelColor(i, j);
//                if (pixelColor.getRed() == 0 && pixelColor.getGreen() >= 171 && pixelColor.getGreen() <= 237
//                        && pixelColor.getBlue() >= 156 && pixelColor.getBlue() <= 234) {
//                if (pixelColor.getRed() == 0 && pixelColor.getGreen() >= 250 && pixelColor.getBlue() >= 250) {
                if (pixelColor.getRed() == 0 && pixelColor.getGreen() >= 226 && pixelColor.getGreen() <= 245
                        && pixelColor.getBlue() >= 223 && pixelColor.getBlue() <= 243) {
                    swPointBean.x = i;
                    swPointBean.y = j;
                    System.out.println("可点击的任务区域:"+swPointBean + "...");
                    return swPointBean;
                }
            }
        }
        return null;
    }

    private static void receiveTask() {
        ScreenUtil.getScreenShot(ConstantScreen.ROOT_X, ConstantScreen.ROOT_Y, 1024, 768, null);
        SwPointBean rs = ImageProcessingUtil.matchTemplate(DIR_RES + "source/receive_practice.jpg", DIR_RES + "buffer/screenshot.jpg");
        //点击领取任务
        System.out.println("点击领取任务...");
        AwtUtil.getRobot().mouseMove(ConstantScreen.ROOT_X + rs.x + rs.width / 2, ConstantScreen.ROOT_Y + rs.y + rs.height / 2);  //
        AwtUtil.performLeftMouseClick(1);
        //关闭
        AwtUtil.getRobot().mouseMove(ConstantScreen.TRADING_CENTER_NEED_PET_CLOSE_X, ConstantScreen.TRADING_CENTER_NEED_PET_CLOSE_Y);
        AwtUtil.performLeftMouseClick(1);
    }
}
