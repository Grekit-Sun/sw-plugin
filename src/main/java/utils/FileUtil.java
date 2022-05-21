package utils;

import java.io.*;
import java.util.List;

public class FileUtil {


    /**
     * 拼接打印
     *
     * @param list
     * @param path
     */
    public static void appendContentToFile(List<String> list, String path) {
        FileWriter fw = null;
        try {
            //如果文件存在，则追加内容；如果文件不存在，则创建文件
            File f = new File(path);
            fw = new FileWriter(f, true);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            PrintWriter pw = new PrintWriter(fw);
            //一次写一行
            for (String s : list) {
                pw.println(s);
            }
            pw.flush();
            try {
                fw.flush();
                pw.close();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 文字写入文本
     *
     * @param list
     * @param path
     */
    public static void stringWriteIntoFile(List<String> list, String path) {
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
