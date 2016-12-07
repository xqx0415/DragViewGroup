package xiedroid.com.dragviewgroup;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by XieQXiong on 2016/9/2.
 */
public class PositionLayout extends ViewGroup {
    private static final String TAG = "RoomView";
    private ChildViewRect mChildViewRect;
    //保存所有的View，绑定位置与该位置对应的View
    private Map<ChildViewRect, View> childViewMap = new HashMap<>();
    //用于拖动View等操作的帮助类
    private ViewDragHelper mViewDragHelper;
    private Context mContext;
    //屏幕的宽高
    private int mScreenWidth;
    private int mScreenHeight;
    //外矩形的各个方位的数值
    private float mLeft;
    private float mTop;
    private float mRight;
    private float mBottom;
    //内外矩形相离的距离
    private float mAlignInner;
    //外矩形，内矩形画笔
    private Paint mOutPaint;
    private Paint mInPaint;
    //外矩形，内矩形
    private RectF mOutRectF;
    private RectF mInRectF;
    //是否是圆角矩形，圆角的值，默认是50
    private boolean mIsRoundRect;
    private float mRadius;
    //内外矩形的背景色
    private int mBgColor;
    private int mInnerBgColor;

    //作为用户位置占矩形好宽度的 ？/？  的标识，每次自增 2
    private int up = 1;
    private int down = 1;
    private TextView mName;

    public PositionLayout(Context context) {
        super(context);
        this.mContext = context;
        initPaint();
    }

    public PositionLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        mViewDragHelper = ViewDragHelper.create(this, 1.0f, mCallback);
        initScreenSize();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MeetingRoomView);
        //外矩形的位置  左上，右下
        mLeft = typedArray.getFloat(R.styleable.MeetingRoomView_left, mScreenWidth * 2 / 10);
        mTop = typedArray.getFloat(R.styleable.MeetingRoomView_top, mScreenHeight / 4);
        mRight = typedArray.getFloat(R.styleable.MeetingRoomView_right, mScreenWidth * 8 / 10);
        mBottom = typedArray.getFloat(R.styleable.MeetingRoomView_bottom, mScreenHeight * 3 / 5);
        //内外矩形的间距，默认为100
        mAlignInner = typedArray.getFloat(R.styleable.MeetingRoomView_align_inner_rect, 100);
        //是否需要显示圆角矩形
        mIsRoundRect = typedArray.getBoolean(R.styleable.MeetingRoomView_round_rect, false);
        //圆角矩形的半径，默认值为50，该值在mIsRoundRect为true时，设置才有效
        mRadius = typedArray.getFloat(R.styleable.MeetingRoomView_round_rect_radius, 50);
        //两个矩形的背景色
        mBgColor = typedArray.getColor(R.styleable.MeetingRoomView_background_color,
                mContext.getResources().getColor(R.color.colorPrimary));
        mInnerBgColor = typedArray.getColor(R.styleable.MeetingRoomView_inner_background_color,
                mContext.getResources().getColor(R.color.colorWhite));
        typedArray.recycle();
        initPaint();
    }

    public PositionLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initPaint();
    }

    private void initScreenSize() {//获取屏幕宽高
        mScreenWidth = ScreenSize.getScreenWidth(mContext);
        mScreenHeight = ScreenSize.getScreenHeight(mContext);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (!changed)
            return;
        int childCount = getChildCount();
        float widthAll = mRight - mLeft;//外矩形的宽
        float heightAll = mBottom - mTop;//外矩形的高
        for (int i = 0; i < childCount; i++) {//遍历子View，进行位置的确认
            int measuredWidth = getChildAt(i).getMeasuredWidth();
            int measuredHeight = getChildAt(i).getMeasuredHeight();
            //子View的位置，cl(左),ct(上),cr(右),cb(下)
            int cl, ct, cr, cb;
            if (i == 0) {
                cl = (int) (mLeft - measuredWidth - 20);
                ct = (int) (mBottom - heightAll / 2 - measuredHeight / 2);
                cr = (int) (mLeft - 20);
                cb = (int) (mBottom - heightAll / 2 + measuredHeight / 2);
                getChildAt(i).layout(cl, ct, cr, cb);
                //将位置与对应的View绑定
                mChildViewRect = new ChildViewRect(cl, ct, cr, cb);
                childViewMap.put(mChildViewRect, getChildAt(i));
            } else if (i == 9) {
                cl = (int) mRight + 20;
                ct = (int) (mBottom - heightAll / 2 - measuredHeight / 2);
                cr = (int) mRight + 20 + measuredWidth;
                cb = (int) (mBottom - heightAll / 2 + measuredHeight / 2);
                getChildAt(i).layout(cl, ct, cr, cb);
                //将位置与对应的View绑定
                mChildViewRect = new ChildViewRect(cl, ct, cr, cb);
                childViewMap.put(mChildViewRect, getChildAt(i));
            } else {
                if (i % 2 == 0) {

                    cl = (int) (widthAll * down / 8 - measuredWidth / 2);
                    ct = (int) (mBottom + 20);
                    cr = (int) (widthAll * down / 8 + measuredWidth / 2);
                    cb = (int) (mBottom + measuredHeight + 20);

                    down += 2;
                } else {

                    cl = (int) (widthAll * up / 8 - measuredWidth / 2);
                    ct = (int) (mTop - measuredHeight - 20);
                    cr = (int) (widthAll * up / 8 + measuredWidth / 2);
                    cb = (int) (mTop - 20);
                    up += 2;

                }
                getChildAt(i).layout(cl + (int) mLeft, ct, cr + (int) mLeft, cb);
                //将位置与对应的View绑定
                //这里的左跟右的位置一定要加上 mLeft
                //在做的过程中就是因为没加，花了很多时间排查位置对换的错乱问题
                mChildViewRect = new ChildViewRect(cl + (int) mLeft, ct, cr + (int) mLeft, cb);
                childViewMap.put(mChildViewRect, getChildAt(i));
            }
        }
    }

    /**
     * 为ViewGroup添加子View
     * @param users                 需要添加到ViewGroup的所有View的模型集合，
     *                              UserModel只有viewName，viewRole两个字段，
     *                              一个保存名字，一个作为每个View显示哪张图片的标识。
     * @param currentName           设置哪个名字的子View需要显示明显一点。
     * @param onUserClickListener   每个子View的点击回调接口。
     */
    public void addUsers(List<UserModel> users, String currentName, final OnUserClickListener onUserClickListener) {
        this.removeAllViews();
        for (int i = 0; i < users.size(); i++) {
            mName = (TextView) View.inflate(this.getContext(), R.layout.text_view, null);
            users.get(i).setPicForUser(mContext, mName);
            String userName = users.get(i).getViewName();
            mName.setText(userName);
            final int finalI = i;
            mName.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onUserClickListener != null)
                        onUserClickListener.onUserClick(v, finalI);
                }
            });
            if (userName.equals(currentName)) {
                users.get(i).setCurrentDelegate(mContext, mName);
            }
            this.addView(mName);
        }
        //通知ViewGroup绘制
        postInvalidate();
    }


    private void initPaint() {
        //外矩形的画笔设置
        mOutPaint = new Paint();
        mOutPaint.setColor(mBgColor);
        mOutPaint.setAntiAlias(true);
//        mOutPaint.setStrokeWidth(20);//画笔宽度
        mOutPaint.setStyle(Paint.Style.FILL);//画笔空心  FILL 为实心
        //内矩形的画笔设置
        mInPaint = new Paint();
        mInPaint.setColor(mInnerBgColor);
        mInPaint.setAntiAlias(true);
//        mInPaint.setStrokeWidth(20);//画笔宽度
        mInPaint.setStyle(Paint.Style.FILL);//画笔空心  FILL 为实心
        //外矩形
        mOutRectF = new RectF(mLeft, mTop, mRight, mBottom);
        //内矩形
        mInRectF = new RectF(mLeft + mAlignInner, mTop + mAlignInner, mRight - mAlignInner, mBottom - mAlignInner);
        //ViewGroup  默认不调用onDraw函数，设置下面的方法让它调用
        this.setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mIsRoundRect) {//需要圆角矩形
            canvas.drawRoundRect(mOutRectF, mRadius, mRadius, mOutPaint);
            canvas.drawRoundRect(mInRectF, mRadius, mRadius, mInPaint);
        } else {
            canvas.drawRect(mOutRectF, mOutPaint);
            canvas.drawRect(mInRectF, mInPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    private ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {
        //手指拖动的那个View 的坐标位置
        private ChildViewRect mMoveViewRect;

        /**
         * 手指按住的那个View
         * @param child         View
         * @param pointerId
         * @return
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            mMoveViewRect = new ChildViewRect(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
            Set<ChildViewRect> childViewRects = childViewMap.keySet();
            Iterator<ChildViewRect> iterator = childViewRects.iterator();
            while (iterator.hasNext()) {
                ChildViewRect childViewRect = iterator.next();
                if (childViewRect.getLeft() == mMoveViewRect.getLeft()
                        && childViewRect.getBottom() == mMoveViewRect.getBottom()
                        && childViewRect.getRight() == mMoveViewRect.getRight()
                        && childViewRect.getTop() == mMoveViewRect.getTop()) {
                    //拖动时先把View从childViewMap中移除掉，没有替换的话会在还原位置的时候重新添加到childViewMap
                    childViewMap.remove(childViewRect);
                    break;
                }
            }
            return true;
        }
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            //不让控件拖出屏幕，横向
            final int leftBound = getPaddingLeft();
            final int rightBound = getWidth() - mName.getWidth() - leftBound;
            final int newLeft = Math.min(Math.max(left, leftBound), rightBound);
            return newLeft;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            //不让控件拖出屏幕，纵向
            final int topBound = getPaddingTop();
            final int bottomBound = getHeight() - mName.getHeight() - topBound;
            final int newTop = Math.min(Math.max(top, topBound), bottomBound);
            return newTop;
        }

        //手指释放的时候回调
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int top = releasedChild.getTop();
            int left = releasedChild.getLeft();
            int right = releasedChild.getRight();
            int bottom = releasedChild.getBottom();
            Set<ChildViewRect> childViewRects = childViewMap.keySet();
            Iterator<ChildViewRect> iterator = childViewRects.iterator();
            //是否有View被覆盖
            boolean isReplace = false;
            View beReplaceView = null;
            ChildViewRect beReplaceChildViewRect = null;

            while (iterator.hasNext()) {
                ChildViewRect childViewRect = iterator.next();
                boolean temp1 = bottom >= childViewRect.getTop();
                boolean temp2 = top <= childViewRect.getBottom();
                boolean temp3 = right >= childViewRect.getLeft();
                boolean temp4 = left <= childViewRect.getRight();
                //如果拖动的那个View覆盖在其中一个子View上
                if (temp1 && temp2 && temp3 && temp4) {
                    beReplaceView = childViewMap.get(childViewRect);
                    beReplaceChildViewRect = childViewRect;
                    //将这个被替换的位置移除出childViewMap
                    childViewMap.remove(childViewRect);
                    //然后将被拖动的View的位置与被覆盖View绑定，让childViewMap的对应关系跟View的个数一致
                    childViewMap.put(mMoveViewRect, beReplaceView);
                    isReplace = true;
                    break;
                }
            }
            if (isReplace) {//交换两个View的位置
                beReplaceView.layout(mMoveViewRect.getLeft(), mMoveViewRect.getTop(), mMoveViewRect.getRight(), mMoveViewRect.getBottom());
                releasedChild.layout(beReplaceChildViewRect.getLeft(), beReplaceChildViewRect.getTop(),
                        beReplaceChildViewRect.getRight(), beReplaceChildViewRect.getBottom());
                //将被覆盖的View的位置与拖动的View绑定
                childViewMap.put(beReplaceChildViewRect, releasedChild);
            } else {
                //如果拖动的View释放后，没有覆盖任何一个View，则返回到之前的位置，并且从新绑定位置
                releasedChild.layout(mMoveViewRect.getLeft(), mMoveViewRect.getTop(), mMoveViewRect.getRight(), mMoveViewRect.getBottom());
                childViewMap.put(mMoveViewRect, releasedChild);
            }
        }
        //在边界拖动时回调
        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return getMeasuredWidth() - child.getMeasuredWidth();
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return getMeasuredHeight() - child.getMeasuredHeight();
        }

        //拖动View的同时，只要坐标一直在变化，就会一直调用这个方法
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
        }
    };
}
