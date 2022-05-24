package utils;

import java.awt.event.KeyEvent;

public class KeyWordUtil {

    /**
     * 粘贴
     */
    public static void pasteKeyWords() {
        AwtUtil.getRobot().keyPress(KeyEvent.VK_WINDOWS);
        AwtUtil.setRandomDelay();
        AwtUtil.getRobot().keyPress(KeyEvent.VK_V);
        AwtUtil.setRandomDelay();
        AwtUtil.getRobot().keyRelease(KeyEvent.VK_V);
        AwtUtil.setRandomDelay();
        AwtUtil.getRobot().keyRelease(KeyEvent.VK_WINDOWS);
        AwtUtil.setRandomDelay();
    }
}
