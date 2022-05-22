package constant;

import utils.ThreadPoolUtil;

import java.awt.event.KeyEvent;
import java.util.*;

/**
 * Description：
 *
 * @data:2022/5/14 下午4:00
 * @author: ZhengXiang Sun
 */
public class ConstantSaleWord {

    private static final boolean SALE_WORD_DEFAULT_VALUE = false;
    private static final boolean SALE_WORD_COMBINE_VALUE = true;
    private static List<Integer> mSaleWordList;

    /**
     * 给添加过来的好友发信息
     */
    private static List<Integer> mTellInfoList;


    private static void setTellInfo(){
        if (mTellInfoList == null) {
            mTellInfoList  = new ArrayList<Integer>();
        }
        if (mTellInfoList.size() > 0) {
            mTellInfoList.clear();
        }

        mTellInfoList.add(KeyEvent.VK_3);
        mTellInfoList.add(KeyEvent.VK_PERIOD);
        mTellInfoList.add(KeyEvent.VK_5);
        mTellInfoList.add(KeyEvent.VK_EQUALS);
        mTellInfoList.add(KeyEvent.VK_5);
        mTellInfoList.add(KeyEvent.VK_9);
        mTellInfoList.add(KeyEvent.VK_4);
        mTellInfoList.add(KeyEvent.VK_SPACE);
        mTellInfoList.add(KeyEvent.VK_COMMA);
        mTellInfoList.add(KeyEvent.VK_1);
        mTellInfoList.add(KeyEvent.VK_0);
        mTellInfoList.add(KeyEvent.VK_EQUALS);
        mTellInfoList.add(KeyEvent.VK_1);
        mTellInfoList.add(KeyEvent.VK_8);
        mTellInfoList.add(KeyEvent.VK_0);
        mTellInfoList.add(KeyEvent.VK_0);
        mTellInfoList.add(KeyEvent.VK_SPACE);
        mTellInfoList.add(KeyEvent.VK_SPACE);
        mTellInfoList.add(KeyEvent.VK_COMMA);

        mTellInfoList.add(KeyEvent.VK_W);
        mTellInfoList.add(KeyEvent.VK_E);
        mTellInfoList.add(KeyEvent.VK_I);
        mTellInfoList.add(KeyEvent.VK_SPACE);
        mTellInfoList.add(KeyEvent.VK_1);
        mTellInfoList.add(KeyEvent.VK_3);
        mTellInfoList.add(KeyEvent.VK_9);
        mTellInfoList.add(KeyEvent.VK_5);
        mTellInfoList.add(KeyEvent.VK_2);
        mTellInfoList.add(KeyEvent.VK_0);
        mTellInfoList.add(KeyEvent.VK_2);
        mTellInfoList.add(KeyEvent.VK_5);
        mTellInfoList.add(KeyEvent.VK_3);
        mTellInfoList.add(KeyEvent.VK_4);
        mTellInfoList.add(KeyEvent.VK_9);
        mTellInfoList.add(KeyEvent.VK_ENTER);
    }

    private static void setSaleWord() {
        if (mSaleWordList == null) {
            mSaleWordList  = new ArrayList<Integer>();
        }
        if (mSaleWordList.size() > 0) {
            mSaleWordList.clear();
        }

        mSaleWordList.add(KeyEvent.VK_X);
        mSaleWordList.add(KeyEvent.VK_U);
        mSaleWordList.add(KeyEvent.VK_Y);
        mSaleWordList.add(KeyEvent.VK_A);
        mSaleWordList.add(KeyEvent.VK_O);
        mSaleWordList.add(KeyEvent.VK_D);
        mSaleWordList.add(KeyEvent.VK_E);
        mSaleWordList.add(KeyEvent.VK_L);
        mSaleWordList.add(KeyEvent.VK_A);
        mSaleWordList.add(KeyEvent.VK_O);
        mSaleWordList.add(KeyEvent.VK_B);
        mSaleWordList.add(KeyEvent.VK_A);
        mSaleWordList.add(KeyEvent.VK_N);
        mSaleWordList.add(KeyEvent.VK_SPACE);
        mSaleWordList.add(KeyEvent.VK_COMMA);

        mSaleWordList.add(KeyEvent.VK_D);
        mSaleWordList.add(KeyEvent.VK_O);
        mSaleWordList.add(KeyEvent.VK_N);
        mSaleWordList.add(KeyEvent.VK_G);
        mSaleWordList.add(KeyEvent.VK_D);
        mSaleWordList.add(KeyEvent.VK_E);
        mSaleWordList.add(KeyEvent.VK_L);
        mSaleWordList.add(KeyEvent.VK_A);
        mSaleWordList.add(KeyEvent.VK_I);
        mSaleWordList.add(KeyEvent.VK_SPACE);
        mSaleWordList.add(KeyEvent.VK_SHIFT);
        mSaleWordList.add(KeyEvent.VK_3);
        mSaleWordList.add(KeyEvent.VK_4);
        mSaleWordList.add(KeyEvent.VK_4);
        mSaleWordList.add(KeyEvent.VK_ENTER);
    }

    /**
     * 获取销售话术，如"需要的老板，懂的来#44"
     *
     * @return
     */
    public static List<Integer> getSaleWord(){
        if (mSaleWordList != null && mSaleWordList.size() > 0) {
            return mSaleWordList;
        } else {
            setSaleWord();
            ThreadPoolUtil.sleep(200);
            System.out.println("当前没有销售话术，正在设置...");
            return getSaleWord();
        }
    }

    public static List<Integer> getTellTellInfo(){
        if (mTellInfoList != null && mTellInfoList.size() > 0) {
            return mTellInfoList;
        } else {
            setTellInfo();
            ThreadPoolUtil.sleep(200);
            System.out.println("当前没有告诉好友的信息，正在设置...");
            return getTellTellInfo();
        }
    }
}
