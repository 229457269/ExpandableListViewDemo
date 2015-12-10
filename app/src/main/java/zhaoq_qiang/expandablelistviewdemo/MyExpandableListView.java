package zhaoq_qiang.expandablelistviewdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;

/**
 * ProjectName : zhaoq_qiang.expandablelistviewdemo
 * Created by : zhaoQiang
 * Email : zhaoq_hero163.com
 * On 2015/12/10 // 19:02
 */

/**
 * 自定义listView
 */
public class MyExpandableListView extends ExpandableListView implements

        OnScrollListener,OnGroupClickListener {


    public MyExpandableListView(Context context) {
        super(context);

        registerListener();
    }

    public MyExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        registerListener();
    }

    public MyExpandableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        registerListener();
    }


    /**
     * 注册监听事件,监听自己事件的  变动
     */
    private void registerListener(){
        setOnScrollListener(this);
        setOnGroupClickListener(this);
    }

    /**
     * 列表需要实现此接口：
     */
    public interface  HeaderAdapter{
        public static final int HEADER_GONE = 0; //不可见状态
        public static final int HEADER_VISABLE = 1;//可见状态
        public static final int HEADER_PUSHED_UP = 2;

        /**
         * 获取头部状态：
         */
        int getHeaderState(int groupPosition,int childPosition);

        /**
         * 配置header,让Header知道显示的内容：
         * @param header
         * @param groupPosition
         * @param childPosition
         * @param alpha
         */
        void configureHeader(View header,int groupPosition,int childPosition,
                             int alpha);


        /**
         * 设置组  按下的状态：
         * @param groupPosition
         * @param status
         */
        void setGroupClickStatus(int groupPosition,int status);

        /**
         * 获取   组按下的状态
         * @param groupPosition
         * @return
         */
        int getGroupClickStatus(int groupPosition);
    }


    private static final int MAX_ALPHA = 255;

    private HeaderAdapter mAdapter;

    /**
     * 用于在  列表头显示的View，mHeaderViewVisiable为true才可见。
     */
    private View mHeaderView;


    /**
     * 列表头是否 可见：
     */
    private boolean mHeaderViewVisable;

    //列表头 宽度
    private int mHeaderViewWidth;

    //列表头高度：
    private int mHeaderViewHeight;

    /**
     * 设置  表头视图
     * @param view
     */
    public  void setHeaderView(View view){
        mHeaderView = view;
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        view.setLayoutParams(lp);

        if (mHeaderView != null) {
            setFadingEdgeLength(0);
        }

        requestLayout();

    }


    /**
     * 点击HeaderView触发的事件：
     */
    private void headerViewClick() {

        //获取   位置
        long packedPosition = getExpandableListPosition(
                this.getFirstVisiblePosition());

        int groupPosition = ExpandableListView.getPackedPositionGroup(
                packedPosition);


        if (mAdapter.getGroupClickStatus(groupPosition)==1){
            this.collapseGroup(groupPosition);
            mAdapter.setGroupClickStatus(groupPosition, 0);
        }else{
            this.expandGroup(groupPosition);
            mAdapter.setGroupClickStatus(groupPosition, 1);
        }

        this.setSelectedGroup(groupPosition);

    }

    private float mDownX;
    private float mDownY;

    /**
     * 如果 HeaderView 是可见的 , 此函数用于判断是否点击了 HeaderView, 并对做相应的处理 ,
     * 因为 HeaderView 是画上去的 , 所以设置事件监听是无效的 , 只有自行控制 .
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mHeaderViewVisable) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mDownX = ev.getX();
                    mDownY = ev.getY();
                    if (mDownX <= mHeaderViewWidth && mDownY <= mHeaderViewHeight) {
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    float x = ev.getX();
                    float y = ev.getY();
                    float offsetX = Math.abs(x - mDownX);
                    float offsetY = Math.abs(y - mDownY);
                    // 如果 HeaderView 是可见的 , 点击在 HeaderView 内 , 那么触发 headerClick()
                    if (x <= mHeaderViewWidth && y <= mHeaderViewHeight
                            && offsetX <= mHeaderViewWidth && offsetY <= mHeaderViewHeight) {
                        if (mHeaderView != null) {
                            headerViewClick();
                        }

                        return true;
                    }
                    break;
                default:
                    break;
            }
        }

        return super.onTouchEvent(ev);

    }

    @Override
    public void setAdapter(ExpandableListAdapter adapter) {
        super.setAdapter(adapter);
        mAdapter = (HeaderAdapter) adapter;
    }

    /**
     *
     * 点击了 Group 触发的事件 , 要根据根据当前点击 Group 的状态来
     */
    @Override
    public boolean onGroupClick(ExpandableListView parent,View v,int groupPosition,long id) {
        if (mAdapter.getGroupClickStatus(groupPosition) == 0) {
            mAdapter.setGroupClickStatus(groupPosition, 1);
            parent.expandGroup(groupPosition);
            //Header自动置顶
            //parent.setSelectedGroup(groupPosition);

        } else if (mAdapter.getGroupClickStatus(groupPosition) == 1) {
            mAdapter.setGroupClickStatus(groupPosition, 0);
            parent.collapseGroup(groupPosition);
        }

        // 返回 true 才可以弹回第一行 , 不知道为什么
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mHeaderView != null) {
            measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
            mHeaderViewWidth = mHeaderView.getMeasuredWidth();
            mHeaderViewHeight = mHeaderView.getMeasuredHeight();
        }
    }

    private int mOldState = -1;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        final long flatPostion = getExpandableListPosition(getFirstVisiblePosition());
        final int groupPos = ExpandableListView.getPackedPositionGroup(flatPostion);
        final int childPos = ExpandableListView.getPackedPositionChild(flatPostion);
        int state = mAdapter.getHeaderState(groupPos, childPos);
        if (mHeaderView != null && mAdapter != null && state != mOldState) {
            mOldState = state;
            mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
        }

        configureHeaderView(groupPos, childPos);
    }

    public void configureHeaderView(int groupPosition, int childPosition) {
        if (mHeaderView == null || mAdapter == null
                || ((ExpandableListAdapter) mAdapter).getGroupCount() == 0) {
            return;
        }

        int state = mAdapter.getHeaderState(groupPosition, childPosition);

        switch (state) {
            case HeaderAdapter.HEADER_GONE: {
                mHeaderViewVisable = false;
                break;
            }

            case HeaderAdapter.HEADER_VISABLE: {
                mAdapter.configureHeader(mHeaderView, groupPosition,childPosition,
                        MAX_ALPHA);

                if (mHeaderView.getTop() != 0){
                    mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
                }

                mHeaderViewVisable = true;

                break;
            }

            case HeaderAdapter.HEADER_PUSHED_UP: {
                View firstView = getChildAt(0);
                int bottom = firstView.getBottom();

                // intitemHeight = firstView.getHeight();
                int headerHeight = mHeaderView.getHeight();

                int y;

                int alpha;

                if (bottom < headerHeight) {
                    y = (bottom - headerHeight);
                    alpha = MAX_ALPHA * (headerHeight + y) / headerHeight;
                } else {
                    y = 0;
                    alpha = MAX_ALPHA;
                }

                mAdapter.configureHeader(mHeaderView, groupPosition,childPosition, alpha);

                if (mHeaderView.getTop() != y) {
                    mHeaderView.layout(0, y, mHeaderViewWidth, mHeaderViewHeight + y);
                }

                mHeaderViewVisable = true;
                break;
            }
        }
    }

    @Override
    /**
     * 列表界面更新时调用该方法(如滚动时)
     */
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mHeaderViewVisable) {
            //分组栏是直接绘制到界面中，而不是加入到ViewGroup中
            drawChild(canvas, mHeaderView, getDrawingTime());
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
        final long flatPos = getExpandableListPosition(firstVisibleItem);
        int groupPosition = ExpandableListView.getPackedPositionGroup(flatPos);
        int childPosition = ExpandableListView.getPackedPositionChild(flatPos);

        configureHeaderView(groupPosition, childPosition);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }


}










