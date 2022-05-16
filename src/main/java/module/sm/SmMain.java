package module.sm;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * Description：师门主类
 *
 * @data:2022/5/14 下午8:34
 * @author: ZhengXiang Sun
 */
public class SmMain {
    private static Robot mRobot;
    private static final String DIR_RES = "/Users/sun/dev/00_IDEA/workspace/sw-plugin/src/main/resources/";

    private static final int ROOT_X = 573;
    private static final int ROOT_Y = 96;

    /**
     * 继续任务
     */
    private static final int SHOT_X_CONTINUE_TASK = ROOT_X + 384;
    private static final int SHOT_Y_CONTINUE_TASK = ROOT_Y + 467;
    private static final int CONTINUE_TASK_WIDTH = 112;
    private static final int CONTINUE_TASK_HEIGHT = 15;

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

    /**
     * 挑战
     */
    private static final int SHOT_X_CHALLENGE = ROOT_X + 805;
    private static final int SHOT_Y_CHALLENGE = ROOT_Y + 186;
    private static final int CHALLENGE_WIDTH = 161;
    private static final int CHALLENGE_OK_WIDTH = 161;
    private static final int CHALLENGE_HEIGHT = 15;

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
    static {
        mSmImageList =  new ArrayList<BufferedImage>();
        mSmImageList.add(mCatchPetImg);
        mSmImageList.add(mCollectMaterialsImg);
        mSmImageList.add(mContinueTaskImg);
        mSmImageList.add(mChallengeImg);
    }

    public SmMain() {
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

    /**
     * https://www.csdn.net/tags/Mtzacg4sOTIwMzktYmxvZwO0O0OO0O0O.html
     * @return
     */
    public static String imageToText(){
        Tesseract tesseract = new Tesseract();
        String resText = "";
        //设置字库
        tesseract.setDatapath("/usr/local/Cellar/tesseract/5.1.0/share/tessdata");
        //如果需要识别英文之外的语种，需要指定识别语种，并且需要将对应的语言包放进项目中
        // chi_sim ：简体中文， eng    根据需求选择语言库
        tesseract.setLanguage("chi_sim");
        try {
            Thread.sleep(1000);
            long startTime = System.currentTimeMillis();
             resText = tesseract.doOCR(new File(DIR_RES + "cutImage/screenshot.jpg"));
            // 输出识别结果
            System.out.println("识别结果: \n" + resText + "\n 耗时：" + (System.currentTimeMillis() - startTime) + "ms");
        } catch (TesseractException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return resText;
    }

    public static void compareWithLocalSm(){
        BufferedImage image = getSpecifiedScreenImage(SHOT_X_CHALLENGE, SHOT_Y_CHALLENGE, CHALLENGE_WIDTH, CHALLENGE_HEIGHT);
        int oldPercent = 0;
        imageToText();
//        System.out.println("start>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//
//        System.out.println("catch pet percentage:%" + compareImage(mCatchPetImg, image));
//
//        System.out.println("catch collect percentage:%" + compareImage(mCollectMaterialsImg, image));
//
//        System.out.println("catch continue percentage:%" + compareImage(mContinueTaskImg, image));
//
//        System.out.println("catch challenge percentage:%" + compareImage(mChallengeImg, image));
//
//        System.out.println("end>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

//        for(BufferedImage smImage : mSmImageList){
//            int currentPercent = compareImage(smImage, image);
//            Math.max(oldPercent,currentPercent);
//            oldPercent = currentPercent;
//        }
    }


    /**
     * 获取指定位置的图片
     * @param x
     * @param y
     * @param width
     * @param height
     * @return
     */
    public static BufferedImage getSpecifiedScreenImage(int x,int y, int width,int height) {
        // robot init
        try {
            if (mRobot == null) mRobot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        //表示截取以（150，500）为坐上顶点的，200px*200px大小的图
        BufferedImage bufferedImage = mRobot.createScreenCapture(new Rectangle(x, y, width, height));
        try {
            ImageIO.write(denoise(bufferedImage), "jpg", new File(DIR_RES + "cutImage/screenshot.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bufferedImage;
    }

    /**
     * 优化图像清晰度
     * @param image
     * @return
     */
//    private static BufferedImage optImage(BufferedImage image) {
//        int[][] grayImage = getGrayByImage(image);
//        BufferedImage optImage = new BufferedImage(grayImage.length,grayImage[0].length,BufferedImage.TYPE_INT_RGB);
//        for(int width= 0 ; width < grayImage.length;width++){
//            for(int height = 0; height < grayImage[0].length; height++){
//                // 二值化
//                if(grayImage[width][height] <= 110){
//                    grayImage[width][height] = 255;
//                }else {
//                    grayImage[width][height] = 0;
//                }
//                Color optColor = new Color(grayImage[width][height],grayImage[width][height],grayImage[width][height]);
//                optImage.setRGB(width,height,optColor.getRGB());
//            }
//        }
//        return optImage;
//    }

    //Function to denoise an image.
    public static BufferedImage denoise(BufferedImage image) {
        //Get original width and height of the image.
        int width = image.getWidth(null);
        int height = image.getHeight(null);

        int pixel;
        int a,p;

        //Initialize a BufferedImage with the same width and height.
        BufferedImage denoisedImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int color_array[] = new int[3];

        //For each element in the original image, denoise it by finding the median of it and its neighbors.
        //Set the median values as the RGB value of denoised_img.

        for(int col = 0; col < width; col++) {
            for(int row = 0; row < height; row++) {
                pixel = ((BufferedImage) image).getRGB(col,row);
                color_array=findNeighbors(image,row,col);
                a = (pixel >>24) & 0xFF;
                p = (a<<24) | (color_array[0]<<16) | (color_array[1]<<8) | color_array[2];
                denoisedImg.setRGB(col, row, p);
            }
        }
        return denoisedImg;
    }

    //Method to find the neighbors of a 2D Matrix (Image) element.
    public static int[] findNeighbors(BufferedImage img, int row, int col)
    {
        //Get original width and height of the image.
        int width = img.getWidth(null);
        int height = img.getHeight(null);

        //Arrays to store the red,green and blue values of each neighbor element.
        int red[] = new int[9];
        int green[] = new int[9];
        int blue[] = new int[9];

        int pixel;
        int i = 0;
        int x,y;

        //For each neighbor element (including the current element), get its red, blue and green value and append to respective arrays.
        for(x = row - 1;x <= row+1;x++) {
            if(x < height && x >= 0) {
                for(y = col - 1;y <= col+1;y++) {
                    if (y < width && y >= 0) {
                        pixel = ((BufferedImage) img).getRGB(y,x);
                        red[i] = (pixel >> 16) & 0xFF;
                        green[i] = (pixel >> 8) & 0xFF;
                        blue[i] = pixel & 0xFF;
                        i++;
                    }
                }
            }
        }


        //For pixels in the edges, number of neighbors won't be 9.
        if(i != 9) {
            red = Arrays.copyOfRange(red, 0, i);
            green = Arrays.copyOfRange(green, 0, i);
            blue = Arrays.copyOfRange(blue, 0, i);
        }

        //Find median of each array.
        int redclr = findMedian(red,red.length);
        int greenclr = findMedian(green,green.length);
        int blueclr = findMedian(blue,blue.length);

        //Store median values in an array and return it.
        int color[] = {redclr,greenclr,blueclr};
        return color;
    }

    //Method to sort an array and find the median.
    public static int findMedian(int arr[], int n)
    {
        // Sort the array
        Arrays.sort(arr);

        // For even number of elements
        if (n % 2 != 0)
            return (int)arr[n / 2];

        // For odd number of elements
        return (int)(arr[(n - 1) / 2] + arr[n / 2]) / 2;
    }


    /**
     * 对比图片相似度
     *
     * @param sourceImage
     * @param cutImage
     */
    public static int compareImage(BufferedImage sourceImage, BufferedImage cutImage) {
        int similarity = 0;
        int different = 0;
        //source
        int[][] sourceGray = getGrayByImage(sourceImage);
        //cut
        int[][] cutGray = getGrayByImage(cutImage);
        // 为了确保程序安全这里还是取两个图片像素中较少的一个作为基准防止越界
        int size1 = Math.min(sourceGray.length, cutGray.length);
        int size2 = Math.min(sourceGray[0].length, cutGray[0].length);
        // 遍历每一个像素点并获取该像素点的灰度
        // 比较两个灰度看是否在一定容差范围内，如果在则认为相似，否则认为不相似
        // 这里比较投机，有需要可以研究分布概率在判断相似度
        // 这里的容差并非是一个标准数据，是本人实验得出来比较合理的容差范围，仅仅代表个人观点
        for (int i = 0; i < size1; i++) {
            for (int j = 0; j < size2; j++) {
                if (Math.abs(sourceGray[i][j] - cutGray[i][j]) <= 20) {
                    similarity++;
                } else {
                    different++;
                }
            }
        }
         int similarPercent = similarity * 100/(similarity + different) ;
//        System.out.println("similarity:" + similarity + "  different:" + different + "  Percentage of similarity：" + similarPercent);
        return similarPercent;
    }

    /**
     * 获取图片的灰度值
     *
     * @param image
     * @return
     */
    private static int[][] getGrayByImage(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] imageGray = new int[width][height];
        int minx = image.getMinX();
        int miny = image.getMinY();
        //提取像素灰度值
        for (int i = minx; i < width; i++) {
            for (int j = miny; j < height; j++) {
                imageGray[i][j] = getGrayByPixel(image.getRGB(i, j));
//                System.out.println("gray [i] = " + i + "  [j] = " + j + "  gray = " + imageGray[i][j]);
            }
        }
        return imageGray;
    }

    /**
     * 灰度值计算
     *
     * @param pixel
     * @return
     */
    public static int getGrayByPixel(int pixel) {
        // 下面三行代码将一个数字转换为RGB数字
        int r = (pixel & 0xff0000) >> 16 ;
        int g = (pixel & 0xff00) >> 8;
        int b = (pixel & 0xff);
        return (int) (0.3 * r + 0.59 * g + 0.11 * b);
    }

    /**
     * 图像加亮
     * @param color
     * @return
     */
    private static int imageLight(int color){
        if(color >255){
            color = 255;
        }
        return color;
    }

    /**
     * 图像加亮
     * @param image
     * @return
     */
    private static BufferedImage optImage(BufferedImage image){
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] imageGray = new int[width][height];
        int minx = image.getMinX();
        int miny = image.getMinY();
        //提取像素灰度值
        for (int i = minx; i < width; i++) {
            for (int j = miny; j < height; j++) {
                // 图像加亮（调整亮度识别率非常高）
                int r = (int) (((image.getRGB(i, j) >> 16) & 0xFF) * 1.1 + 30);
                int g = (int) (((image.getRGB(i, j) >> 8) & 0xFF) * 1.1 + 30);
                int b = (int) (((image.getRGB(i, j) >> 0) & 0xFF) * 1.1 + 30);
                //图像加亮；
                r = imageLight(r);
                g = imageLight(g);
                b = imageLight(b);
                //END
                imageGray[i][j] = (int) Math
                        .pow((Math.pow(r, 2.2) * 0.2973 + Math.pow(g, 2.2)
                                * 0.6274 + Math.pow(b, 2.2) * 0.0753), 1 / 2.2);
            }
        }
        // 二值化
        int threshold = ostu(imageGray, width, height);
        BufferedImage binaryBufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                if (imageGray[x][y] > threshold)
                {
                    imageGray[x][y] |= 0x00FFFF;
                } else
                {
                    imageGray[x][y] &= 0xFF0000;
                }
                binaryBufferedImage.setRGB(x, y, imageGray[x][y]);
            }
        }

        //去除干扰线条
        for(int y = 1; y < height-1; y++){
            for(int x = 1; x < width-1; x++){
                boolean flag = false ;
                if(isBlack(binaryBufferedImage.getRGB(x, y))){
                    //左右均为空时，去掉此点
                    if(isWhite(binaryBufferedImage.getRGB(x-1, y)) && isWhite(binaryBufferedImage.getRGB(x+1, y))){
                        flag = true;
                    }
                    //上下均为空时，去掉此点
                    if(isWhite(binaryBufferedImage.getRGB(x, y+1)) && isWhite(binaryBufferedImage.getRGB(x, y-1))){
                        flag = true;
                    }
                    //斜上下为空时，去掉此点
                    if(isWhite(binaryBufferedImage.getRGB(x-1, y+1)) && isWhite(binaryBufferedImage.getRGB(x+1, y-1))){
                        flag = true;
                    }
                    if(isWhite(binaryBufferedImage.getRGB(x+1, y+1)) && isWhite(binaryBufferedImage.getRGB(x-1, y-1))){
                        flag = true;
                    }
                    if(flag){
                        binaryBufferedImage.setRGB(x,y,-1);
                    }
                }
            }
        }
        // 矩阵打印
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                if (isBlack(binaryBufferedImage.getRGB(x, y)))
                {
                    System.out.print("*");
                } else
                {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
        return binaryBufferedImage;
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
//            File output = new File(DIR_RES + "source/cat_pet1111.jpg");
//            ImageIO.write(res, "jpg", output);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bufferedImage;
    }

    /**
     * 获取屏幕坐标
     */
    public static void getPointInfo() {
        int x = 0;
        int y = 0;
        while (true) {
            PointerInfo pinfo = MouseInfo.getPointerInfo();
            int mx = pinfo.getLocation().x;
            int my = pinfo.getLocation().y;
            if (x != mx || y != my) {
                x = mx;
                y = my;
                System.out.println("x:" + mx + ",y:" + my);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * 求出图像处理阈值
     * @param gray
     * @param w
     * @param h
     * @return
     */
    public static int ostu(int[][] gray, int w, int h)
    {
        int[] histData = new int[w * h];
        // Calculate histogram
        for (int x = 0; x < w; x++)
        {
            for (int y = 0; y < h; y++)
            {
                int red = 0xFF & gray[x][y];
                histData[red]++;
            }
        }

        // Total number of pixels
        int total = w * h;

        float sum = 0;
        for (int t = 0; t < 256; t++)
            sum += t * histData[t];

        float sumB = 0;
        int wB = 0;
        int wF = 0;

        float varMax = 0;
        int threshold = 0;

        for (int t = 0; t < 256; t++)
        {
            wB += histData[t]; // Weight Background
            if (wB == 0)
                continue;

            wF = total - wB; // Weight Foreground
            if (wF == 0)
                break;

            sumB += (float) (t * histData[t]);

            float mB = sumB / wB; // Mean Background
            float mF = (sum - sumB) / wF; // Mean Foreground

            // Calculate Between Class Variance
            float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);

            // Check if new maximum found
            if (varBetween > varMax)
            {
                varMax = varBetween;
                threshold = t;
            }
        }

        return threshold;
    }

    public static boolean isBlack(int colorInt)
    {
        Color color = new Color(colorInt);
        if (color.getRed() + color.getGreen() + color.getBlue() <= 300)
        {
            return true;
        }
        return false;
    }

    public static boolean isWhite(int colorInt)
    {
        Color color = new Color(colorInt);
        if (color.getRed() + color.getGreen() + color.getBlue() > 300)
        {
            return true;
        }
        return false;
    }
}
