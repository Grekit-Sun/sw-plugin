package module.change;

import constant.ConstantScreen;
import utils.AwtUtil;

public class ChangeMain {

    public static void ChangePeople(){
        AwtUtil.getRobot().mouseMove(ConstantScreen.ROOT_X,ConstantScreen.ROOT_Y);
        AwtUtil.performLeftMouseClick(1);
        AwtUtil.getRobot().delay(1000);
        //restart
        AwtUtil.getRobot().mouseMove(ConstantScreen.RESTART_X,ConstantScreen.RESTART_Y);
        AwtUtil.performLeftMouseClick(1);
        AwtUtil.getRobot().delay(5000);
        //start1
        AwtUtil.getRobot().mouseMove(ConstantScreen.START1_X,ConstantScreen.START1_Y);
        AwtUtil.performLeftMouseClick(1);
        AwtUtil.getRobot().delay(5000);
        //start2
        AwtUtil.getRobot().mouseMove(ConstantScreen.START2_X,ConstantScreen.START2_Y);
        AwtUtil.performLeftMouseClick(1);
        AwtUtil.getRobot().delay(5000);
        //change people
        AwtUtil.getRobot().mouseMove(ConstantScreen.CHANGE_PEO_X,ConstantScreen.CHANGE_PEO_Y);
        AwtUtil.performLeftMouseClick(1);
        AwtUtil.getRobot().delay(5000);
        //start3
        AwtUtil.getRobot().mouseMove(ConstantScreen.START3_X,ConstantScreen.START3_Y);
        AwtUtil.performLeftMouseClick(1);
        AwtUtil.getRobot().delay(5000);
    }
}
