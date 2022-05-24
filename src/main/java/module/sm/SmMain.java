package module.sm;

import bean.SwPointBean;
import com.lowagie.text.pdf.PRAcroForm;
import constant.ConstantScreen;
import helper.SwTaskPointhelper;
import models.*;
import module.change.ChangeMain;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import utils.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.opencv.core.CvType.CV_8UC3;
import static org.opencv.imgproc.Imgproc.COLOR_RGB2BGR;
import static org.opencv.imgproc.Imgproc.cvtColor;


/**
 * Description：师门主类
 *
 * @data:2022/5/14 下午8:34
 * @author: ZhengXiang Sun
 */
public class SmMain {
    private static final String DIR_RES = "/Users/sun/dev/00_IDEA/workspace/sw-plugin/src/main/resources/";

//    private static final int ROOT_X = 763;
//    private static final int ROOT_Y = 112;

    private final static String TASK_CHALLENGE = "challenge";
    private final static String TASK_COLLECT = "collect";
    private final static String TASK_CATCH_PET = "catch_pet";

    private static int mCntAccount;

    private static String CURRENT_TASK;

    private static long lastCatchPetTime = 0;
    private static long lastCollectTime = 0;
    private static long lastChallengeTime = 0;

    private static boolean isTenthTask;
    private static boolean mIsNeedChange;

    private static boolean mIsInTradingCenter;


    /**
     * catch pet
     */
    private static final int KIND_CATCH_PET = 1;

    /**
     * Collect materials
     */
    private static final int KIND_COLLECT_MATERIALS = 2;

    /**
     * challenge
     */
    private static final int KIND_CHALLENGE = 3;

    /**
     * 师门的图片灰度值
     */
    private static BufferedImage mCatchPetImg = readImage(DIR_RES + "source/catch_pet.jpg");
    private static BufferedImage mCollectMaterialsImg = readImage(DIR_RES + "source/collect_materials.jpg");
    private static BufferedImage mContinueTaskImg = readImage(DIR_RES + "source/continue_task.jpg");
    private static BufferedImage mChallengeImg = readImage(DIR_RES + "source/challenge.jpg");
    private static ArrayList<BufferedImage> mSmImageList;

    private static Lock mLock = new ReentrantLock();
    private static Condition mCondition = mLock.newCondition();

    static {
        mSmImageList = new ArrayList<BufferedImage>();
        mSmImageList.add(mCatchPetImg);
        mSmImageList.add(mCollectMaterialsImg);
        mSmImageList.add(mContinueTaskImg);
        mSmImageList.add(mChallengeImg);
    }

    public SmMain() {
    }

    static long mOldTime = -1;


    /**
     * 开始做师门
     */
    public static void start() {
        //搜集游戏点的像素信息
        collect();
        //开启师门是否为挑战的监听线程
        startTaskChallengeMonitor(true);
        //开启师门是否为捕捉宠物的监听线程
        startTaskCatchPetMonitor(true);
        //开启师门是否为收集物资的监听线程
        startTaskCollectMonitor(true);
        //开启师门是否为第10环的监听线程
        startTaskTenMonitor(true);
        //开启监听是否要点香的线程
        startFragranceMonitor(true);
    }

    /**
     * 搜集游戏点的像素信息
     */
    private static void collect() {
        collectData("挑战1", ConstantScreen.TASK_CHALLENGE_1_X, ConstantScreen.TASK_CHALLENGE_1_Y, false);
        collectData("挑战2", ConstantScreen.TASK_CHALLENGE_2_X, ConstantScreen.TASK_CHALLENGE_2_Y, false);
        collectData("挑战3", ConstantScreen.TASK_CHALLENGE_3_X, ConstantScreen.TASK_CHALLENGE_3_Y, false);

        collectData("捕捉宠物1", ConstantScreen.TASK_CATCH_PET_1_X, ConstantScreen.TASK_CATCH_PET_1_Y, false);
        collectData("捕捉宠物2", ConstantScreen.TASK_CATCH_PET_2_X, ConstantScreen.TASK_CATCH_PET_2_Y, false);
        collectData("捕捉宠物3", ConstantScreen.TASK_CATCH_PET_3_X, ConstantScreen.TASK_CATCH_PET_3_Y, false);

        collectData("收集物资1", ConstantScreen.TASK_COLLECT_1_X, ConstantScreen.TASK_COLLECT_1_Y, false);
        collectData("收集物资2", ConstantScreen.TASK_COLLECT_2_X, ConstantScreen.TASK_COLLECT_2_Y, false);
        collectData("收集物资3", ConstantScreen.TASK_COLLECT_3_X, ConstantScreen.TASK_COLLECT_3_Y, false);

        collectData("第10环1", ConstantScreen.TASK_END_1_X, ConstantScreen.TASK_END_1_Y, false);
        collectData("第10环2", ConstantScreen.TASK_END_2_X, ConstantScreen.TASK_END_2_Y, false);
        collectData("第10环3", ConstantScreen.TASK_END_3_X, ConstantScreen.TASK_END_3_Y, false);
    }

    /**
     * 监听是否要点香
     *
     * @param isStart
     */
    private static void startFragranceMonitor(boolean isStart) {
        ThreadPoolUtil.getInstance().execute(() -> {
            while (isStart) {
                //判断是否已经点了
                Color c = AwtUtil.getRobot().getPixelColor(ConstantScreen.FRAGRANCE_HAS_X, ConstantScreen.FRAGRANCE_HAS_Y);
                if (c.getRed() == 82 && c.getGreen() == 81 && c.getBlue() == 97) {
                    System.out.println("已经点香了...");
                    // 线程挂起
                    try {
                        mCondition.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                Color pixelColor = AwtUtil.getRobot().getPixelColor(ConstantScreen.FRAGRANCE_X, ConstantScreen.FRAGRANCE_Y);
                if ((pixelColor.getRed() == 219 && pixelColor.getGreen() == 113 && pixelColor.getBlue() == 45)
                        || (pixelColor.getRed() == 255 && pixelColor.getGreen() == 125 && pixelColor.getBlue() == 71)) {
                    AwtUtil.getRobot().mouseMove(ConstantScreen.FRAGRANCE_X, ConstantScreen.FRAGRANCE_Y);
                    AwtUtil.performLeftMouseClick(1);
                    // 线程挂起
                    try {
                        mCondition.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    /**
     * 监听是否为第10环
     */
    private static void startTaskTenMonitor(boolean isStart) {
        ThreadPoolUtil.getInstance().execute(() -> {
            while (isStart) {
                Color c1 = AwtUtil.getRobot().getPixelColor(ConstantScreen.TASK_END_1_X, ConstantScreen.TASK_END_1_Y);
                int r1 = c1.getRed();
                int g1 = c1.getGreen();
                int b1 = c1.getBlue();
                Color c2 = AwtUtil.getRobot().getPixelColor(ConstantScreen.TASK_END_2_X, ConstantScreen.TASK_END_2_Y);
                int r2 = c2.getRed();
                int g2 = c2.getGreen();
                int b2 = c2.getBlue();
                Color c3 = AwtUtil.getRobot().getPixelColor(ConstantScreen.TASK_END_3_X, ConstantScreen.TASK_END_3_Y);
                int r3 = c3.getRed();
                int g3 = c3.getGreen();
                int b3 = c3.getBlue();
                if (r1 >= 226 && r1 <= 252 && g1 == 0 && b1 == 0
                        && r2 == 255 && g2 == 0 && b2 == 0
                        && r3 >= 228 && r3 <= 252 && g3 == 0 && b3 == 0) {
                    System.out.println("Current task is the tenth...");
                    isTenthTask = true;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private static void startTaskCollectMonitor(boolean isStart) {
        ThreadPoolUtil.getInstance().execute(() -> {
            while (isStart) {
                Color c1 = AwtUtil.getRobot().getPixelColor(ConstantScreen.TASK_COLLECT_1_X, ConstantScreen.TASK_COLLECT_1_Y);
                int r1 = c1.getRed();
                int g1 = c1.getGreen();
                int b1 = c1.getBlue();
                Color c2 = AwtUtil.getRobot().getPixelColor(ConstantScreen.TASK_COLLECT_2_X, ConstantScreen.TASK_COLLECT_2_Y);
                int r2 = c2.getRed();
                int g2 = c2.getGreen();
                int b2 = c2.getBlue();
                Color c3 = AwtUtil.getRobot().getPixelColor(ConstantScreen.TASK_COLLECT_3_X, ConstantScreen.TASK_COLLECT_3_Y);
                int r3 = c3.getRed();
                int g3 = c3.getGreen();
                int b3 = c3.getBlue();
                if (r1 >= 247 && r1 <= 251 && g1 >= 251 && g1 <= 254 && b1 == 0
                        && r2 >= 231 && r2 <= 243 && g2 >= 236 && g2 <= 246 && b2 == 0
                        && r3 >= 243 && r3 <= 249 && g3 >= 247 && g3 <= 252 && b3 == 0) {
                    CURRENT_TASK = TASK_COLLECT;
                    if (System.currentTimeMillis() - lastCollectTime > 2 * 60 * 100) {
                        synchronized (SmMain.class) {
                            System.out.println("toCollect...");
                            toCollect();
                        }
                        System.out.println("Current task is " + CURRENT_TASK + " and is to buy it...");
                        lastCollectTime = System.currentTimeMillis();
                    }
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     * 监听是否为捕捉宠物
     */
    private static void startTaskCatchPetMonitor(boolean isStart) {
        ThreadPoolUtil.getInstance().execute(() -> {
            while (isStart) {
                Color c1 = AwtUtil.getRobot().getPixelColor(ConstantScreen.TASK_CATCH_PET_1_X, ConstantScreen.TASK_CATCH_PET_1_Y);
                int r1 = c1.getRed();
                int g1 = c1.getGreen();
                int b1 = c1.getBlue();
                Color c2 = AwtUtil.getRobot().getPixelColor(ConstantScreen.TASK_CATCH_PET_2_X, ConstantScreen.TASK_CATCH_PET_2_Y);
                int r2 = c2.getRed();
                int g2 = c2.getGreen();
                int b2 = c2.getBlue();
                Color c3 = AwtUtil.getRobot().getPixelColor(ConstantScreen.TASK_CATCH_PET_3_X, ConstantScreen.TASK_CATCH_PET_3_Y);
                int r3 = c3.getRed();
                int g3 = c3.getGreen();
                int b3 = c3.getBlue();
                if (r1 >= 216 && r1 <= 238 && g1 >= 222 && g1 <= 239 && b1 == 0
                        && r2 >= 228 && r2 <= 243 && g2 >= 234 && g2 <= 247 && b2 == 0
                        && r3 >= 240 && r3 <= 247 && g3 >= 244 && g3 <= 250 && b3 == 0) {
                    CURRENT_TASK = TASK_CATCH_PET;
                    if (System.currentTimeMillis() - lastCatchPetTime > 2 * 60 * 100) {
                        synchronized (SmMain.class) {
                            toCatchPet();
                        }
                        System.out.println("Current task is " + CURRENT_TASK + " and is to catch it...");
                        lastCatchPetTime = System.currentTimeMillis();
                    }
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     * 去挑战
     */
    private static void toChallenge() {
        SwPointBean canClickXy = findCanClickXy();
        AwtUtil.getRobot().mouseMove(canClickXy.x, canClickXy.y);
        AwtUtil.performLeftMouseClick(2);
        //直到出现继续任务，才继续往下走
        continueTask();
    }

    /**
     * 去收集物资
     */
    private static void toCollect() {
        AwtUtil.getRobot().delay(1000);
        AwtUtil.getRobot().mouseMove(ConstantScreen.TRADING_CENTER_X, ConstantScreen.TRADING_CENTER_Y);
        AwtUtil.getRobot().delay(1000 + AwtUtil.mRandom.nextInt(500));
        AwtUtil.performLeftMouseClick(1);
        AwtUtil.getRobot().delay(2 * 1000 + AwtUtil.mRandom.nextInt(500));
        //开始遍历需求的物资
        if (hasThisMaterial() || startFindMaterials()) {     //找到物资，点击xx
            AwtUtil.getRobot().mouseMove(ConstantScreen.CLOSE_BUY_MATERIALS_X, ConstantScreen.CLOSE_BUY_MATERIALS_Y);
            AwtUtil.getRobot().delay(800 + AwtUtil.mRandom.nextInt(200));
            AwtUtil.performLeftMouseClick(1);
        }
        SwPointBean canClickXy = findCanClickXy();
        AwtUtil.getRobot().mouseMove(canClickXy.x, canClickXy.y);
        AwtUtil.getRobot().delay(1000 + AwtUtil.mRandom.nextInt(200));
        AwtUtil.performLeftMouseClick(2);
        continueTask();
    }

    /**
     * ´
     * 身上本来就有
     */
    private static boolean hasThisMaterial() {
        return false;
    }

    /**
     * 开始遍历物资
     */
    private static boolean startFindMaterials() {
        ScreenUtil.getScreenShot(ConstantScreen.COLLECT_MATERIALS_X, ConstantScreen.COLLECT_MATERIALS_Y,
                ConstantScreen.COLLECT_MATERIALS_WIDTH, ConstantScreen.COLLECT_MATERIALS_HEIGHT, null);
        SwPointBean swPointBean = ImageProcessingUtil.matchTemplate(DIR_RES + "source/need_materials.jpg", DIR_RES + "buffer/screenshot.jpg");
        //确认下是否为需求
        if (confirmNeed(swPointBean)) {
            AwtUtil.getRobot().mouseMove(ConstantScreen.COLLECT_MATERIALS_X + swPointBean.x + 20, ConstantScreen.COLLECT_MATERIALS_Y + swPointBean.y + 20);
            AwtUtil.getRobot().delay(800 + AwtUtil.mRandom.nextInt(200));
            //选中物资
            AwtUtil.performLeftMouseClick(1);
            //移动到购买
            AwtUtil.getRobot().mouseMove(ConstantScreen.BUY_MATERIALS_X, ConstantScreen.BUY_MATERIALS_Y);
            AwtUtil.getRobot().delay(500 + AwtUtil.mRandom.nextInt(300));
            //点击购买
            AwtUtil.performLeftMouseClick(1);
            ThreadPoolUtil.sleep(3000);
            //判断是不是单价过高提示
            Color pixelColor = AwtUtil.getRobot().getPixelColor(ConstantScreen.SURE_BUY_MATERIALS_X, ConstantScreen.SURE_BUY_MATERIALS_Y);
            if (pixelColor.getRed() == 101 && pixelColor.getGreen() == 230 && pixelColor.getBlue() == 190) {
                System.out.println("弹出单价过高提示...");
                AwtUtil.getRobot().mouseMove(ConstantScreen.SURE_BUY_MATERIALS_X, ConstantScreen.SURE_BUY_MATERIALS_Y);
                AwtUtil.getRobot().delay(200);
                AwtUtil.performLeftMouseClick(1);
            }
            return true;
        } else {
            //没找到物资，点下一页
            AwtUtil.getRobot().mouseMove(ConstantScreen.COLLECT_MATERIALS_NEXT_PAGE_X, ConstantScreen.COLLECT_MATERIALS_NEXT_PAGE_Y);
            AwtUtil.getRobot().delay(800 + AwtUtil.mRandom.nextInt(200));
            AwtUtil.performLeftMouseClick(1);
            return startFindMaterials();
        }
    }

    /**
     * 确认是否为需求
     *
     * @return
     */
    private static boolean confirmNeed(SwPointBean swPointBean) {
        for (int i = ConstantScreen.COLLECT_MATERIALS_X + swPointBean.x; i < ConstantScreen.COLLECT_MATERIALS_X + swPointBean.x + 43; i++) {
            for (int j = ConstantScreen.COLLECT_MATERIALS_Y + swPointBean.x; j < ConstantScreen.COLLECT_MATERIALS_Y + swPointBean.y + 43; j++) {
                Color pixelColor = AwtUtil.getRobot().getPixelColor(i, j);
                if (pixelColor.getRed() == 221 && pixelColor.getGreen() == 155 && pixelColor.getBlue() == 0) {
                    Color anOtherPixel = AwtUtil.getRobot().getPixelColor(i + 7, j - 10);
                    if (anOtherPixel.getRed() == 210 && anOtherPixel.getGreen() == 200 && anOtherPixel.getBlue() == 104) {  //确定是要买的物资
                        System.out.println("找到物资，位置为：（" + i + "，" + j + ")");
//                        //鼠标移动到物资上
//                        AwtUtil.getRobot().mouseMove(i, j);
//                        AwtUtil.getRobot().delay(500 + AwtUtil.mRandom.nextInt(300));
//                        //选中物资
//                        AwtUtil.performLeftMouseClick(2);
//                        //移动到购买
//                        AwtUtil.getRobot().mouseMove(ConstantScreen.BUY_MATERIALS_X, ConstantScreen.BUY_MATERIALS_Y);
//                        AwtUtil.getRobot().delay(500 + AwtUtil.mRandom.nextInt(300));
//                        //点击购买
//                        AwtUtil.performLeftMouseClick(1);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 去捉宠物
     */
    private static void toCatchPet() {
        //找到点击点
        SwPointBean canClickXy = findCanClickXy();
        if (canClickXy == null) {
            System.out.println("没找到可以点击的点...");
        }
        //鼠标移动到对应点
        AwtUtil.getRobot().mouseMove(canClickXy.x, canClickXy.y);
        //点击鼠标
        AwtUtil.performLeftMouseClick(3);
        //开始监听是否有"需"
        startWatchNeed();
    }

    private static void startWatchNeed() {
        mIsInTradingCenter = false;
        synchronized (SmMain.class) {
            while (!mIsInTradingCenter) {
                Color pixelColor = AwtUtil.getRobot().getPixelColor(ConstantScreen.TRADING_CENTER_NEED_PET_CLOSE_X, ConstantScreen.TRADING_CENTER_NEED_PET_CLOSE_Y);
                if ((pixelColor.getRed() == 230 || (pixelColor.getRed() == 237)) && pixelColor.getGreen() == 252 && pixelColor.getBlue() == 254) {
                    //到了交易中心,识别处可点击的区域
                    ScreenUtil.getScreenShot(ConstantScreen.ROOT_X, ConstantScreen.ROOT_Y, 1024, 768, null);
                    SwPointBean swPointBean = ImageProcessingUtil.matchTemplate(DIR_RES + "source/enter_trading.jpg", DIR_RES + "buffer/screenshot.jpg");
                    //点击买宠物
                    AwtUtil.getRobot().mouseMove(ConstantScreen.ROOT_X + swPointBean.x + swPointBean.width / 2, ConstantScreen.ROOT_Y + swPointBean.y + swPointBean.heigt / 2);  //
                    AwtUtil.performLeftMouseClick(1);
                    //点击购买
                    AwtUtil.getRobot().delay(AwtUtil.mRandom.nextInt(500));
                    AwtUtil.getRobot().mouseMove(ConstantScreen.BUY_BY_CREDIT_X, ConstantScreen.BUY_BY_CREDIT_Y);
                    AwtUtil.performLeftMouseClick(1);
                    mIsInTradingCenter = true;
                }
            }
            AwtUtil.getRobot().delay(AwtUtil.mRandom.nextInt(500) + 1000);
            AwtUtil.getRobot().mouseMove(ConstantScreen.CLOSE_CATCH_PET_X, ConstantScreen.CLOSE_CATCH_PET_Y);
            //关闭
            AwtUtil.performLeftMouseClick(1);
            //找到可以点击的按钮
            SwPointBean canClickXy = findCanClickXy();
            AwtUtil.getRobot().mouseMove(canClickXy.x, canClickXy.y);
            AwtUtil.performLeftMouseClick(2);
            continueTask();
        }
    }

    /**
     * 继续任务
     */
    private static void continueTask() {
        boolean isContinue = false;
        while (!isContinue) {
            Color pixelColor = AwtUtil.getRobot().getPixelColor(ConstantScreen.TRADING_CENTER_NEED_PET_CLOSE_X, ConstantScreen.TRADING_CENTER_NEED_PET_CLOSE_Y);
            if ((pixelColor.getRed() == 230 || (pixelColor.getRed() == 237)) && pixelColor.getGreen() == 252 && pixelColor.getBlue() == 254) {
                isContinue = true;
            }
            ThreadPoolUtil.sleep(500);
        }
        if (TASK_COLLECT.equals(CURRENT_TASK)) {
            //监听物资是否需要自己交1
            //识别下确认点击的任务的位置
            ScreenUtil.getScreenShot(ConstantScreen.ROOT_X, ConstantScreen.ROOT_Y, 1024, 768, null);
            SwPointBean bean = ImageProcessingUtil.matchTemplate(DIR_RES + "source/report_task.jpg", DIR_RES + "buffer/screenshot.jpg");
            Color co = AwtUtil.getRobot().getPixelColor(bean.x + 375, bean.y + 479);
            if (co.getRed() == 237 && co.getGreen() == 243 && co.getBlue() == 0) {
                //点击交任务
                AwtUtil.getRobot().mouseMove(ConstantScreen.SEND_MATERIALS_BY_SELF_X, ConstantScreen.SEND_MATERIALS_BY_SELF_Y);
                AwtUtil.performLeftMouseClick(1);
                //识别下确认点击的任务的位置
                ScreenUtil.getScreenShot(ConstantScreen.ROOT_X, ConstantScreen.ROOT_Y, 1024, 768, null);
                SwPointBean swPointBean = ImageProcessingUtil.matchTemplate(DIR_RES + "source/report_collect.jpg", DIR_RES + "buffer/screenshot.jpg");
                //点击继续任务
                AwtUtil.getRobot().mouseMove(ConstantScreen.ROOT_X + swPointBean.x + swPointBean.width / 2, ConstantScreen.ROOT_Y + swPointBean.y + swPointBean.heigt / 2);  //
                AwtUtil.performLeftMouseClick(1);
                needSendBySelf();
            }
            //监听物资是否需要自己交2
            Color c = AwtUtil.getRobot().getPixelColor(ConstantScreen.SEND_MATERIALS_X, ConstantScreen.SEND_MATERIALS_Y);
            if ((c.getRed() == 230 || (c.getRed() == 237)) && c.getGreen() == 252 && c.getBlue() == 254) {
                //需要自己交
                needSendBySelf();
            }
        }
        AwtUtil.getRobot().delay(1000 + AwtUtil.mRandom.nextInt(1000));
        ScreenUtil.getScreenShot(ConstantScreen.ROOT_X, ConstantScreen.ROOT_Y, 1024, 768, null);
        SwPointBean swPointBean = ImageProcessingUtil.matchTemplate(DIR_RES + "source/continue_task.jpg", DIR_RES + "buffer/screenshot.jpg");
        //点击继续任务
        AwtUtil.getRobot().mouseMove(ConstantScreen.ROOT_X + swPointBean.x + swPointBean.width / 2, ConstantScreen.ROOT_Y + swPointBean.y + swPointBean.heigt / 2);  //
        AwtUtil.performLeftMouseClick(1);
        AwtUtil.getRobot().delay(1000 + AwtUtil.mRandom.nextInt(500));
        //关闭桌面遮挡
        Color pixelColor = AwtUtil.getRobot().getPixelColor(ConstantScreen.TRADING_CENTER_NEED_PET_CLOSE_X, ConstantScreen.TRADING_CENTER_NEED_PET_CLOSE_Y);
        if ((pixelColor.getRed() == 230 || (pixelColor.getRed() == 237)) && pixelColor.getGreen() == 252 && pixelColor.getBlue() == 254) {
            AwtUtil.getRobot().mouseMove(ConstantScreen.TRADING_CENTER_NEED_PET_CLOSE_X, ConstantScreen.TRADING_CENTER_NEED_PET_CLOSE_Y);
            AwtUtil.performLeftMouseClick(2);
        }
        if (mIsNeedChange) {
            synchronized (SmMain.class) {
                mCntAccount++;
                if (mCntAccount % 4 == 0) {
                    ChangeMain.changeAccount();
                } else {
                    ChangeMain.ChangePeople();
                }
                mIsNeedChange = false;
                isTenthTask = false;
                //释放摄妖香的锁
                mCondition.signalAll();
            }
        }
        //判断是否为第10环
        if (isTenthTask) {
            synchronized (SmMain.class) {
                mIsNeedChange = true;
            }
        }
    }

    /**
     * 需要自己交物资
     */
    private static void needSendBySelf() {
        //先点击一下背包1
        AwtUtil.getRobot().mouseMove(ConstantScreen.PACKAGE1_ENTER_X, ConstantScreen.PACKAGE1_ENTER_Y);
        AwtUtil.performLeftMouseClick(1);
        //一个个点过去
        clickAllPackage();
        //翻页点
        AwtUtil.getRobot().mouseMove(ConstantScreen.PACKAGE2_ENTER_X, ConstantScreen.PACKAGE2_ENTER_Y);
        AwtUtil.performLeftMouseClick(1);
        //一个个点过去
        clickAllPackage();
        boolean isSendEnd = false;
        while (!isSendEnd) {
            Color pixelColor = AwtUtil.getRobot().getPixelColor(ConstantScreen.SEND_MATERIALS_HAS_CLICK_X, ConstantScreen.SEND_MATERIALS_HAS_CLICK_Y);
            if (pixelColor.getRed() != 113 && pixelColor.getGreen() != 187 && pixelColor.getBlue() != 166) {
                AwtUtil.getRobot().mouseMove(ConstantScreen.SEND_SURE_X, ConstantScreen.SEND_SURE_Y);
                AwtUtil.performLeftMouseClick(1);
                isSendEnd = true;
            }
        }
    }

    private static void clickAllPackage() {
        int cntNoUse = 0;
        for (int i = ConstantScreen.PACKAGE_START_X; i < ConstantScreen.PACKAGE_START_X + 48 * 5; i = i + 48) {
            for (int j = ConstantScreen.PACKAGE_START_Y; j < ConstantScreen.PACKAGE_START_Y + 48 * 8; j = j + 48) {
                Color pixelColor = AwtUtil.getRobot().getPixelColor(i, j);
                if (pixelColor.getRed() == 85 && pixelColor.getGreen() == 143 && pixelColor.getBlue() == 147) {
                    System.out.println("处于背包的不可点击的地方... return");
                    cntNoUse++;
                    i = i + 48;
                    j = ConstantScreen.PACKAGE_START_Y;
                    if (cntNoUse == 5) {
                        return;
                    }
                }
                AwtUtil.getRobot().mouseMove(i, j);
                AwtUtil.performLeftMouseClick(1);
            }

        }
    }

    /**
     * 找到可以点击的点
     *
     * @return
     */
    private static SwPointBean findCanClickXy() {
        SwPointBean swPointBean = new SwPointBean();
        for (int i = ConstantScreen.TASK_FIND_X; i < ConstantScreen.TASK_FIND_X + ConstantScreen.TASK_FIND_WIDTH; i++) {
            for (int j = ConstantScreen.TASK_FIND_Y; j < ConstantScreen.TASK_FIND_Y + ConstantScreen.TASK_FIND_HEIGHT; j++) {
                Color pixelColor = AwtUtil.getRobot().getPixelColor(i, j);
//                if (pixelColor.getRed() == 0 && pixelColor.getGreen() >= 171 && pixelColor.getGreen() <= 237
//                        && pixelColor.getBlue() >= 156 && pixelColor.getBlue() <= 234) {
//                if (pixelColor.getRed() == 0 && pixelColor.getGreen() >= 250 && pixelColor.getBlue() >= 250) {
                if (pixelColor.getRed() == 0 && pixelColor.getGreen() >= 226 && pixelColor.getGreen() <= 245
                        && pixelColor.getBlue() >= 223 && pixelColor.getBlue() <= 243) {
                    swPointBean.x = i;
                    swPointBean.y = j;
                    return swPointBean;
                }
            }
        }
        return null;
    }

    /**
     * 监听是否为挑战
     */
    private static void startTaskChallengeMonitor(boolean isStart) {
        ThreadPoolUtil.getInstance().execute(() -> {
            while (isStart) {
                Color challenge1 = AwtUtil.getRobot().getPixelColor(ConstantScreen.TASK_CHALLENGE_1_X, ConstantScreen.TASK_CHALLENGE_1_Y);
                int r1 = challenge1.getRed();
                int g1 = challenge1.getGreen();
                int b1 = challenge1.getBlue();
                Color challenge2 = AwtUtil.getRobot().getPixelColor(ConstantScreen.TASK_CHALLENGE_2_X, ConstantScreen.TASK_CHALLENGE_2_Y);
                int r2 = challenge2.getRed();
                int g2 = challenge2.getGreen();
                int b2 = challenge2.getBlue();
                Color challenge3 = AwtUtil.getRobot().getPixelColor(ConstantScreen.TASK_CHALLENGE_3_X, ConstantScreen.TASK_CHALLENGE_3_Y);
                int r3 = challenge3.getRed();
                int g3 = challenge3.getGreen();
                int b3 = challenge3.getBlue();
                if (r1 > 233 && r1 < 247 && g1 > 238 && g1 < 249 && b1 == 0
                        && r2 > 217 && r2 < 239 && g2 > 222 && g2 < 241 && b2 == 0
                        && r3 > 216 && r3 < 240 && g3 > 223 && g3 < 241 && b3 == 0) {
                    CURRENT_TASK = TASK_CHALLENGE;
                    if (System.currentTimeMillis() - lastChallengeTime > 2 * 60 * 100) {
                        synchronized (SmMain.class) {
                            toChallenge();
                        }
                        System.out.println("Current task is " + CURRENT_TASK + " and is to challenge it...");
                        lastChallengeTime = System.currentTimeMillis();
                    }
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }


    private static void collectData(String fileName, int x, int y, boolean isCollect) {
        ThreadPoolUtil.getInstance().execute(() -> {
            boolean addHead = true;
            SwTaskPointhelper swTaskPointhelper = new SwTaskPointhelper();
            while (isCollect) {
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                swTaskPointhelper.inputMsgToFile(x, y, fileName, addHead);
                addHead = false;
            }
        });
    }

    private static void startMouseEvent(SwPointBean pointBean) {

        /**
         * 开个线程处理点击事件
         */
        ThreadPoolUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    SwPointBean pointInfo = ScreenUtil.getPointInfo();
                    //截取鼠标图片，发现鼠标截图截不进去
//                    ScreenUtil.getScreenShot(pointInfo.x -50,pointInfo.y-50,100,100,"buffer/mouse.jpg");
                    Color pixelColor = AwtUtil.getRobot().getPixelColor(pointInfo.x, pointInfo.y);
                    if (pixelColor.getBlue() > 225) {
                        if (mOldTime == -1 || System.currentTimeMillis() - mOldTime > 2 * 1000) {
                            mOldTime = System.currentTimeMillis();
                            AwtUtil.getRobot().mousePress(KeyEvent.BUTTON1_MASK);
                            System.out.println("mousse press...");
                        }
                    }
                    try {
                        Thread.sleep(new Random().nextInt(100) + 100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        for (int i = 0; i < pointBean.width * 2; i = i + 2) {
            for (int j = 0; j < pointBean.heigt * 2; j = j + 2) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
//                            moveMouse((int) (ConstantScreen.ROOT_X + pointBean.x + i), (int) (ConstantScreen.ROOT_Y + pointBean.y + j + 15));
            }
        }
    }

    /**
     * 点击任务栏
     */
    private static void pointTaskItem() {
    }

    public static void moveMouse(int x, int y) {
        AwtUtil.getRobot().mouseMove(x, y);
    }

    //Function to denoise an image.
    public static BufferedImage denoise(BufferedImage image) {
        //Get original width and height of the image.
        int width = image.getWidth(null);
        int height = image.getHeight(null);

        int pixel;
        int a, p;

        //Initialize a BufferedImage with the same width and height.
        BufferedImage denoisedImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int color_array[] = new int[3];

        //For each element in the original image, denoise it by finding the median of it and its neighbors.
        //Set the median values as the RGB value of denoised_img.

        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                pixel = ((BufferedImage) image).getRGB(col, row);
                color_array = findNeighbors(image, row, col);
                a = (pixel >> 24) & 0xFF;
                p = (a << 24) | (color_array[0] << 16) | (color_array[1] << 8) | color_array[2];
                denoisedImg.setRGB(col, row, p);
            }
        }
        return denoisedImg;
    }

    //Method to find the neighbors of a 2D Matrix (Image) element.
    public static int[] findNeighbors(BufferedImage img, int row, int col) {
        //Get original width and height of the image.
        int width = img.getWidth(null);
        int height = img.getHeight(null);

        //Arrays to store the red,green and blue values of each neighbor element.
        int red[] = new int[9];
        int green[] = new int[9];
        int blue[] = new int[9];

        int pixel;
        int i = 0;
        int x, y;

        //For each neighbor element (including the current element), get its red, blue and green value and append to respective arrays.
        for (x = row - 1; x <= row + 1; x++) {
            if (x < height && x >= 0) {
                for (y = col - 1; y <= col + 1; y++) {
                    if (y < width && y >= 0) {
                        pixel = ((BufferedImage) img).getRGB(y, x);
                        red[i] = (pixel >> 16) & 0xFF;
                        green[i] = (pixel >> 8) & 0xFF;
                        blue[i] = pixel & 0xFF;
                        i++;
                    }
                }
            }
        }


        //For pixels in the edges, number of neighbors won't be 9.
        if (i != 9) {
            red = Arrays.copyOfRange(red, 0, i);
            green = Arrays.copyOfRange(green, 0, i);
            blue = Arrays.copyOfRange(blue, 0, i);
        }

        //Find median of each array.
        int redclr = findMedian(red, red.length);
        int greenclr = findMedian(green, green.length);
        int blueclr = findMedian(blue, blue.length);

        //Store median values in an array and return it.
        int color[] = {redclr, greenclr, blueclr};
        return color;
    }

    //Method to sort an array and find the median.
    public static int findMedian(int arr[], int n) {
        // Sort the array
        Arrays.sort(arr);

        // For even number of elements
        if (n % 2 != 0)
            return (int) arr[n / 2];

        // For odd number of elements
        return (int) (arr[(n - 1) / 2] + arr[n / 2]) / 2;
    }

    private static BufferedImage readImage(String imagePath) {
        BufferedImage bufferedImage = null;
        try {
            //1.读取本地png图片or读取url图片
            File input = new File(imagePath);
            bufferedImage = ImageIO.read(input);//读取本地图片
            //BufferedImage bimg = ImageIO.read(new URL("http://img.alicdn.com/tfs/TB1kbMoUOLaK1RjSZFxXXamPFXa.png"));//读取url图片
            //2. 填充透明背景为白色
//            BufferedImage res = new BufferedImage(bimg.getWidth(), bimg.getHeight(), BufferedImage.TYPE_INT_RGB);
//            res.createGraphics().drawImage(bimg, 0, 0, Color.WHITE, null); //背景填充色设置为白色，也可以设置为其他颜色比如粉色Color.PINK
            //3. 保存成jpg到本地
//            File output = new File(DIR_RES + "source/cat_pet.jpg");
//            ImageIO.write(res, "jpg", output);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bufferedImage;
    }

    private static BufferedImage matToBufferImage(Mat mat) {
        //编码图像
        MatOfByte matOfByte = new MatOfByte();
        boolean isEncodeOk = Imgcodecs.imencode(".jpg", mat, matOfByte);
//        System.out.println("isEncodeOk:" + isEncodeOk);
        //将编码的Mat存储在字节数组中
        byte[] byteArray = matOfByte.toArray();
        //准备缓冲图像
        InputStream in = new ByteArrayInputStream(byteArray);
        BufferedImage bufImage = null;
        try {
            bufImage = ImageIO.read(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bufImage;
    }

    private static final int blkSize = 10 * 10;
    private static final int patchSize = 8;
    private static final double lambda = 10;
    private static final double gamma = 1.7;
    private static final int r = 10;
    private static final double eps = 1e-6;
    private static final int level = 5;

    private static BufferedImage dehaze(BufferedImage image) {
        BufferedImage rgBformat = converttoRGBformat(image);
        byte[] image_rgb = (byte[]) rgBformat.getData().getDataElements(0, 0, rgBformat.getWidth(), rgBformat.getHeight(), null);
        Mat bimg1_mat;
        bimg1_mat = new Mat(rgBformat.getHeight(), rgBformat.getWidth(), CV_8UC3);
//        String imgPath = "/Users/sun/dev/00_IDEA/workspace/sw-plugin/src/main/resources/source/catch_pet.jpg";
//        Mat image = Imgcodecs.imread(imgPath, Imgcodecs.IMREAD_COLOR);
//        new ImShow("Original").showImage(image);
//        Mat result = DarkChannelPriorDehaze.enhance(bimg1_mat, krnlRatio, minAtmosLight, eps);
        Mat result = RemoveBackScatter.enhance(bimg1_mat, blkSize, patchSize, lambda, gamma, r, eps, level);
        return matToBufferImage(result);
    }

    /**
     * BufferedImage均转为TYPE_3BYTE_BGR，RGB格式
     *
     * @param input 未知格式BufferedImage图片
     * @return TYPE_3BYTE_BGR格式的BufferedImage图片
     */
    public static BufferedImage converttoRGBformat(BufferedImage input) {
        if (null == input) {
            throw new NullPointerException("BufferedImage input can not be null!");
        }
        if (BufferedImage.TYPE_3BYTE_BGR != input.getType()) {
            BufferedImage input_rgb = new BufferedImage(input.getWidth(), input.getHeight(),
                    BufferedImage.TYPE_3BYTE_BGR);
            new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_sRGB), null).filter(input, input_rgb);
            return input_rgb;
        } else {
            return input;
        }
    }
}
