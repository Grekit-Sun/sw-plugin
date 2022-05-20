package helper;

import bean.SwPointBean;
import constant.ConstantScreen;
import utils.ScreenUtil;

import java.awt.*;

public class SwTaskPointhelper {

    private static Robot mRobot;
    /**
     * 挑战
     *     public static final int ROOT_X = 764;
     *     public static final int ROOT_Y = 62;
     *     挑战：1620，253
     *     (maxR:246,minR:237)(maxG:248,minG:240)(maxB:0,minB:0)
     */


    /**
     * 鼠标跳到捕捉宠物处
     */
    public static void jumpToCatchPet(){
        moveMouse(ConstantScreen.TASK_CHALLENGE_1_X,ConstantScreen.TASK_CHALLENGE_1_Y);
    }

    public static void moveMouse(int x, int y) {
        // robot init
        try {
            if (mRobot == null) mRobot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        mRobot.mouseMove(x, y);
    }

    public static void inputMsgToFile(){
        SwPointBean pointInfo = ScreenUtil.getPointInfo();
    }
}
