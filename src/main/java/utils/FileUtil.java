package utils;

import java.io.*;
import java.util.List;

public class FileUtil {

    /**
     * 文字写入文本
     * @param list
     * @param path
     */
    public static void stringWriteIntoFile(List<String> list,String path) {
        BufferedWriter bw = null;
        FileWriter fr = null;
        try {
            //将写入转化为流的形式
            fr = new FileWriter(path);
            bw = new BufferedWriter(fr);
            //一次写一行
            for (String s : list) {
                bw.write(s);
                bw.newLine();  //换行用
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
                if (fr != null) {
                    fr.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
