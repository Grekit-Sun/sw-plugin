package utils;

import bean.SwPointBean;
import constant.ConstantScreen;

import java.awt.*;

public class MaterialUtil {

    private static final String DIR_RES = "/Users/sun/dev/00_IDEA/workspace/sw-plugin/src/main/resources/";



    /**
     * 开始遍历物资
     */
    public static boolean startFindMaterials() {
        while (startFind(false)) {
            ThreadPoolUtil.sleep(500);
        }
            System.out.println("都买好了...");
        return true;
    }

    private static boolean startFind(boolean isNextPage) {
        if(!isNextPage) {
            System.out.println("点交易中心...");
            AwtUtil.getRobot().mouseMove(ConstantScreen.TRADING_CENTER_X, ConstantScreen.TRADING_CENTER_Y);
            AwtUtil.performLeftMouseClick(1);
            //看下需不需要买
            ThreadPoolUtil.sleep(1000);
            ScreenUtil.getScreenShot(ConstantScreen.ROOT_X, ConstantScreen.ROOT_Y, 1024, 768, null);
            SwPointBean rs = ImageProcessingUtil.matchTemplate(DIR_RES + "source/need.jpg", DIR_RES + "buffer/screenshot.jpg");
            Color pc = AwtUtil.getRobot().getPixelColor(ConstantScreen.ROOT_X + rs.x + 10, ConstantScreen.ROOT_Y + rs.y + 4);
            System.out.println("" + (ConstantScreen.ROOT_X + rs.x + 10) + "," + ( ConstantScreen.ROOT_Y + rs.y + 4) + pc);
            if(pc.getRed() == 214 && pc.getGreen() == 146 && pc.getBlue() == 32){
                System.out.println("需要购买...");
            }else {
                System.out.println("不需要购买...");
                System.out.println("点击xx...");
                AwtUtil.getRobot().mouseMove(ConstantScreen.CLOSE_BUY_MATERIALS_X, ConstantScreen.CLOSE_BUY_MATERIALS_Y);
                AwtUtil.performLeftMouseClick(1);
                return false;
            }
        }
        ScreenUtil.getScreenShot(ConstantScreen.COLLECT_MATERIALS_X, ConstantScreen.COLLECT_MATERIALS_Y,
                ConstantScreen.COLLECT_MATERIALS_WIDTH, ConstantScreen.COLLECT_MATERIALS_HEIGHT, null);
        SwPointBean swPointBean = ImageProcessingUtil.matchTemplate(DIR_RES + "source/need_materials.jpg", DIR_RES + "buffer/screenshot.jpg");
        //确认下是否为需求
        if (confirmNeed(swPointBean)) {
            AwtUtil.getRobot().mouseMove(ConstantScreen.COLLECT_MATERIALS_X + swPointBean.x + 20, ConstantScreen.COLLECT_MATERIALS_Y + swPointBean.y + 20);
            //选中物资
            System.out.println("选中物资...");
            AwtUtil.performLeftMouseClick(1);
            //移动到购买
            AwtUtil.getRobot().mouseMove(ConstantScreen.BUY_MATERIALS_X, ConstantScreen.BUY_MATERIALS_Y);
            //点击购买
            System.out.println("点击购买...");
            AwtUtil.performLeftMouseClick(1);
            ThreadPoolUtil.sleep(1000);
            //判断是不是单价过高提示
            Color pixelColor = AwtUtil.getRobot().getPixelColor(ConstantScreen.SURE_BUY_MATERIALS_X, ConstantScreen.SURE_BUY_MATERIALS_Y);
            if (pixelColor.getRed() == 101 && pixelColor.getGreen() == 230 && pixelColor.getBlue() == 190) {
                System.out.println("弹出单价过高提示...");
                AwtUtil.getRobot().mouseMove(ConstantScreen.SURE_BUY_MATERIALS_X, ConstantScreen.SURE_BUY_MATERIALS_Y);
                AwtUtil.performLeftMouseClick(1);
            }
            System.out.println("购买物资成功...");
            System.out.println("点击xx...");
            AwtUtil.getRobot().mouseMove(ConstantScreen.CLOSE_BUY_MATERIALS_X, ConstantScreen.CLOSE_BUY_MATERIALS_Y);
            AwtUtil.performLeftMouseClick(1);
            return true;
        } else {
            //没找到物资，点下一页
            AwtUtil.getRobot().mouseMove(ConstantScreen.COLLECT_MATERIALS_NEXT_PAGE_X, ConstantScreen.COLLECT_MATERIALS_NEXT_PAGE_Y);
            System.out.println("没找到物资，点下一页...");
            AwtUtil.performLeftMouseClick(1);
            return startFind(true);
        }
    }

    /**
         * 确认是否为需求
         *
         * @return
         */
        private static boolean confirmNeed(SwPointBean swPointBean) {
            System.out.println("开始遍历需求的物资...");
            for (int i = ConstantScreen.COLLECT_MATERIALS_X + swPointBean.x; i < ConstantScreen.COLLECT_MATERIALS_X + swPointBean.x + 43; i++) {
                for (int j = ConstantScreen.COLLECT_MATERIALS_Y + swPointBean.x; j < ConstantScreen.COLLECT_MATERIALS_Y + swPointBean.y + 43; j++) {
                    Color pixelColor = AwtUtil.getRobot().getPixelColor(i, j);
                    if (pixelColor.getRed() == 221 && pixelColor.getGreen() == 155 && pixelColor.getBlue() == 0) {
                        Color anOtherPixel = AwtUtil.getRobot().getPixelColor(i + 7, j - 10);
                        if (anOtherPixel.getRed() == 210 && anOtherPixel.getGreen() == 200 && anOtherPixel.getBlue() == 104) {  //确定是要买的物资
                            System.out.println("找到物资，位置为：（" + i + "，" + j + ")");
                            return true;
                        }
                    }
                }
            }
            System.out.println("结束遍历需求的物资...");
            return false;
        }

    /**
     * 需要自己交物资
     */
    public static void needSendBySelf() {
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
            ThreadPoolUtil.sleep(200);
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
}
