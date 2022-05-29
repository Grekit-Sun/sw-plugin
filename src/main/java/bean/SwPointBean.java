package bean;

public class SwPointBean {

    public int x;

    public int y;

    public int width;

    public int height;

    @Override
    public String toString() {
        return "(" + x + "," + y + ")" + ", （width = " + width + ", height = " + height + "）";
    }
}
