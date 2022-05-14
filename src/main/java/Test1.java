import utils.SalesWordUtil;
import utils.ThreadPoolUtil;
import utils.constant.ConstantSaleWord;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;

/**
 * Description：
 *
 * @data:2022/5/14 下午1:49
 * @author: ZhengXiang Sun
 */
public class Test1 {

    private static Random mRandom = new Random();
    private static final int INTERVAL_SPEAK = 50;
    public static void main(String[] args) {
        ThreadPoolUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(mRandom.nextInt(10 * 1000) + INTERVAL_SPEAK * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    SalesWordUtil.shoutOnTheWorld(ConstantSaleWord.getSaleWord());
                }
            }
        });
    }
}
