package module.sm;

import bean.SwPointBean;
import constant.ConstantScreen;
import models.*;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import utils.ImShow;
import utils.ImageProcessingUtil;
import utils.ScreenUtil;
import utils.ThreadPoolUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static org.opencv.core.CvType.CV_8UC3;
import static org.opencv.imgproc.Imgproc.COLOR_RGB2BGR;
import static org.opencv.imgproc.Imgproc.cvtColor;


/**
 * Description：师门主类
 *
 * @data:2022/5/14 下午8:34
 * @author: ZhengXiang Sun
 */
public class SmMain {
    private static Robot mRobot;
    private static final String DIR_RES = "/Users/sun/dev/00_IDEA/workspace/sw-plugin/src/main/resources/";

//    private static final int ROOT_X = 763;
//    private static final int ROOT_Y = 112;





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

    static long mOldTime = - 1;


    /**
     * 开始做师门
     */
    public static void start(){

        // robot init
        try {
            if (mRobot == null) mRobot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }

        //实时抓取师门区域的图片
        ScreenUtil.getScreenShot(ConstantScreen.ROOT_X, ConstantScreen.ROOT_Y,
                ConstantScreen.SW_WINDOW_WIDTH, ConstantScreen.SW_WINDOW_HEIGHT,null);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        SwPointBean pointBean = ImageProcessingUtil.matchTemplate(DIR_RES + "source/catch_pet.jpg", DIR_RES + "buffer/screenshot.jpg");
        /**
         * 开个线程处理点击事件
         */
        ThreadPoolUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    SwPointBean pointInfo = ScreenUtil.getPointInfo();
                    Color pixelColor = mRobot.getPixelColor(pointInfo.x + 3, pointInfo.y + 3);
                    if (pixelColor.getRed() > 85 && pixelColor.getRed() < 100 && pixelColor.getGreen() > 80
                            && pixelColor.getGreen() < 100 && pixelColor.getRed() > 75 && pixelColor.getRed() < 95) {
                        if(mOldTime == -1 || System.currentTimeMillis() - mOldTime > 5 * 1000) {
                            mOldTime = System.currentTimeMillis();
                            mRobot.mousePress(KeyEvent.BUTTON1_MASK);
                            System.out.println("mousse press...");
                        }
                    }
                    try {
                        Thread.sleep(new Random().nextInt(100) + 100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        for(int i = 0 ;i<pointBean.width * 2; i ++){
            for(int j = 0; j<pointBean.heigt * 2;j++){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                moveMouse((int) (ConstantScreen.ROOT_X + pointBean.x + i), (int) (ConstantScreen.ROOT_Y + pointBean.y + j + 15));
            }
        }
//        double collectProbability= ImageProcessingUtil.compareImage(DIR_RES + "buffer/screenshot.jpg", DIR_RES + "source/collect.jpg");
//        double catchPetProbability= ImageProcessingUtil.compareImage(DIR_RES + "buffer/screenshot.jpg", DIR_RES + "source/catch_pet.jpg");
//        double challengeProbability= ImageProcessingUtil.compareImage(DIR_RES + "buffer/screenshot.jpg", DIR_RES + "source/challenge3.jpg");
//        if(collectProbability > catchPetProbability && collectProbability > challengeProbability){
//            System.out.println("当前师门任务类别是：【收集材料】...");
//        }
//        if(catchPetProbability > collectProbability && catchPetProbability > challengeProbability){
//            System.out.println("当前师门任务类别是：【抓宠物】...");
//        }
//        if(challengeProbability > collectProbability && challengeProbability > catchPetProbability){
//            System.out.println("当前师门任务类别是：【挑战】...");
//        }
//        System.out.println("当前师门任务类别是：【收集材料】:" + collectProbability + "【抓宠物】:" + catchPetProbability  + "【挑战】:" + challengeProbability );

//        ImageProcessingUtil.imageToText(DIR_RES + "buffer/screenshot.jpg");
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

    private static  BufferedImage matToBufferImage(Mat mat){
        //编码图像
        MatOfByte matOfByte = new MatOfByte();
        boolean isEncodeOk = Imgcodecs.imencode(".jpg", mat, matOfByte);
//        System.out.println("isEncodeOk:" + isEncodeOk);
        //将编码的Mat存储在字节数组中
        byte[] byteArray = matOfByte.toArray();
        //准备缓冲图像
        InputStream in = new ByteArrayInputStream(byteArray);
        BufferedImage bufImage = null;
        try {
            bufImage = ImageIO.read(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bufImage;
    }

    private static final int blkSize = 10 * 10;
    private static final int patchSize = 8;
    private static final double lambda = 10;
    private static final double gamma = 1.7;
    private static final int r = 10;
    private static final double eps = 1e-6;
    private static final int level = 5;
    private static BufferedImage dehaze(BufferedImage image){
        BufferedImage rgBformat = converttoRGBformat(image);
        byte[] image_rgb = (byte[]) rgBformat.getData().getDataElements(0, 0, rgBformat.getWidth(), rgBformat.getHeight(), null);
        Mat bimg1_mat;
        bimg1_mat = new Mat(rgBformat.getHeight(), rgBformat.getWidth(), CV_8UC3);
//        String imgPath = "/Users/sun/dev/00_IDEA/workspace/sw-plugin/src/main/resources/source/catch_pet.jpg";
//        Mat image = Imgcodecs.imread(imgPath, Imgcodecs.IMREAD_COLOR);
//        new ImShow("Original").showImage(image);
//        Mat result = DarkChannelPriorDehaze.enhance(bimg1_mat, krnlRatio, minAtmosLight, eps);
        Mat result = RemoveBackScatter.enhance(bimg1_mat, blkSize, patchSize, lambda, gamma, r, eps, level);
        return matToBufferImage(result);
    }

    /**
     * BufferedImage均转为TYPE_3BYTE_BGR，RGB格式
     *
     * @param input 未知格式BufferedImage图片
     * @return TYPE_3BYTE_BGR格式的BufferedImage图片
     */
    public static BufferedImage converttoRGBformat(BufferedImage input)
    {
        if (null == input)
        {
            throw new NullPointerException("BufferedImage input can not be null!");
        }
        if (BufferedImage.TYPE_3BYTE_BGR != input.getType())
        {
            BufferedImage input_rgb = new BufferedImage(input.getWidth(), input.getHeight(),
                    BufferedImage.TYPE_3BYTE_BGR);
            new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_sRGB), null).filter(input, input_rgb);
            return input_rgb;
        } else
        {
            return input;
        }
    }
}
