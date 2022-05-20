package constant;

public class ConstantScreen {

    /**
     * 游戏关闭按钮处的坐标
     * 关闭按钮的中心偏右下的点
     */
    public static final int ROOT_X = 766;
    public static final int ROOT_Y = 87;


    /**
     * 挑战
     */
    public static final int TASK_CHALLENGE_1_X = ROOT_X + 856;
    public static final int TASK_CHALLENGE_1_Y = ROOT_Y + 191;

    public static final int TASK_CHALLENGE_2_X = TASK_CHALLENGE_1_X + 8;
    public static final int TASK_CHALLENGE_2_Y = TASK_CHALLENGE_1_Y + 4;

//    private static final int CHALLENGE_WIDTH = 161;
//    private static final int CHALLENGE_OK_WIDTH = 161;
//    private static final int CHALLENGE_HEIGHT = 15;

    public static final int CHALLENGE_WIDTH = 120;
    public static final int CHALLENGE_OK_WIDTH = 161;
    public static final int CHALLENGE_HEIGHT = 15;

    /**
     * 游戏任务坐标
     */
    public static final int ROOT_TASK_X = ROOT_X + 791;
    public static final int ROOT_TASK_Y = ROOT_Y + 755;

    public static final int SW_WINDOW_X = 112;
    public static final int SW_WINDOW_Y = 15;
    public static final int SW_WINDOW_WIDTH = 1024;
    public static final int SW_WINDOW_HEIGHT = 768;

    /**
     * 继续任务
     */
    public static final int SHOT_X_CONTINUE_TASK = ROOT_X + 384;
    public static final int SHOT_Y_CONTINUE_TASK = ROOT_Y + 467;
    public static final int CONTINUE_TASK_WIDTH = 112;
    public static final int CONTINUE_TASK_HEIGHT = 15;

    /**
     * 抓宠物
     */
    private static final int SHOT_X_CATCH_PET = ROOT_X + 801;
    private static final int SHOT_Y_CATCH_PET = ROOT_Y + 184;
    private static final int CATCH_PET_WIDTH = 120;
    private static final int CATCH_PET_OK_WIDTH = 161;
    private static final int CATCH_PET_HEIGHT = 15;

    /**
     * 收集物资
     */
    private static final int SHOT_X_COLLECT_MATERIALS = ROOT_X + 801;
    private static final int SHOT_Y_COLLECT_MATERIALS = ROOT_Y + 184;
    private static final int COLLECT_MATERIALS_WIDTH = 120;
    private static final int COLLECT_MATERIALS_OK_WIDTH = 161;
    private static final int COLLECT_MATERIALS_HEIGHT = 15;
}
