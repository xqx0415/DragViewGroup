package xiedroid.com.dragviewgroup;

/**
 * 描述：用于保存子View的左上右下值
 * 包名：xiedroid.com.dragviewgroup
 * 类名：ChildViewRect
 *
 * @author XieQingXiong
 * @创建时间 2016/12/3   18:55
 */
public class ChildViewRect {
    private int left;
    private int top;
    private int right;
    private int bottom;

    public ChildViewRect() {
    }
    public ChildViewRect(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }
}
