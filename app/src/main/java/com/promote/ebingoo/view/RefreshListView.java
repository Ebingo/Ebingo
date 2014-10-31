package com.promote.ebingoo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.promote.ebingoo.R;

/**
 * Created by acer on 2014/10/31.
 */
public class RefreshListView extends ListView {
    private View refreshHeader;
    private View headView;
    private View footView;
    private View emptyView;
    private RefreshListener refreshListener;
    private MyScrollListener myScrollListener;
    private ImageView head_image;
    private float lastMovePoint = 0f;
    private final int PULL_UP = 1;
    private final int PULL_DOWN = 2;
    private final int PULL_NOTING = 5;
    private int pull_state = PULL_NOTING;
    /**
     * 标志：处于松开刷新状态，不一定正在刷新
     */
    private boolean wouldRefreshHeader = false;
    /**
     * 标志：正在刷新
     */
    private boolean isRefreshing = false;
    /**
     * 变为向下的箭头,改变箭头方向
     */
    private RotateAnimation mFlipAnimation;
    /**
     * 变为逆向的箭头,旋转
     */
    private RotateAnimation mReverseFlipAnimation;

    /**
     * 是否可以加载更多
     */
    private boolean canLoadMore = true;

    /**
     * 是否可以刷新
     */
    private boolean canRefresh = false;

    private TextView head_text;

    public RefreshListView(Context context) {
        super(context);
        init();
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mFlipAnimation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mFlipAnimation.setInterpolator(new LinearInterpolator());
        mFlipAnimation.setDuration(250);
        mFlipAnimation.setFillAfter(true);
        mReverseFlipAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
        mReverseFlipAnimation.setDuration(250);
        mReverseFlipAnimation.setFillAfter(true);
    }

    @Override
    protected void onFinishInflate() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        headView = inflater.inflate(R.layout.header_view, null);
        footView = inflater.inflate(R.layout.foot_view, null);
        refreshHeader = inflater.inflate(R.layout.header_refresh, null);
        head_image = (ImageView) headView.findViewById(R.id.head_image);
        head_text = (TextView) headView.findViewById(R.id.head_text);
        addFooterView(footView);
        addHeaderView(headView);
//        setHeaderDividersEnabled(false);
        setFooterVisibility(INVISIBLE);
        myScrollListener = new MyScrollListener();
        setCanRefresh(canRefresh);
        setCanLoadMore(canLoadMore);
        super.setOnScrollListener(myScrollListener);
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        myScrollListener.setWrapListener(l);
    }

    public void setRefreshListener(RefreshListener l) {
        myScrollListener.setRefreshListener(l);
    }

    /**
     * 内部用的监听，兼容setOnScrollListener方法的OnScrollListener
     */
    private class MyScrollListener implements OnScrollListener {

        private OnScrollListener mOnScrollListener;
        private RefreshListener refreshListener;
        private boolean isBottom = false;

        /**
         * 设置兼容的OnScrollListener
         */
        final public void setWrapListener(OnScrollListener l) {
            this.mOnScrollListener = l;
        }

        public RefreshListener getRefreshListener() {
            return refreshListener;
        }

        public void setRefreshListener(RefreshListener refreshListener) {
            this.refreshListener = refreshListener;
        }

        @Override
        final public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == SCROLL_STATE_IDLE) {
                if (isBottom && canLoadMore && pull_state == PULL_UP) {
                    setFooterVisibility(VISIBLE);
                    if (refreshListener != null) {
                        refreshListener.onLoadMore();
                    }
                }
            }

            if (wouldRefreshHeader && !isRefreshing && canRefresh && pull_state == PULL_DOWN) {
                setHeadRefreshing();
                if (refreshListener != null) {
                    refreshListener.onRefresh();
                }
            }

            if (mOnScrollListener != null)
                mOnScrollListener.onScrollStateChanged(view, scrollState);
        }

        @Override
        final public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                   int totalItemCount) {
            isBottom = (firstVisibleItem + visibleItemCount == totalItemCount);

            if (mOnScrollListener != null)
                mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }

    }

    public interface RefreshListener {
        /**
         * 当控件处于下拉刷新状态时，调用此方法
         */
        public abstract void onRefresh();

        /**
         * 当控件处于上啦加载时，调用此方法
         */
        public abstract void onLoadMore();
    }

    /**
     * 调用这个方法，结束加载更多UI效果
     */
    public void loadMoreFinished(boolean hasMore) {
        setCanLoadMore(hasMore);
        if (hasMore) {
            setFooterVisibility(INVISIBLE);
        } else {
            setFooterVisibility(GONE);
        }
        pull_state = PULL_NOTING;
    }

    /**
     * 调用这个方法，结束下拉刷新UI效果
     */
    public void refreshFinished() {
        headView.setVisibility(VISIBLE);
        removeHeaderView(refreshHeader);
        isRefreshing = false;
        pull_state = PULL_NOTING;
//        removeHeaderAnim();
    }

    /**
     * 显示或者隐藏footView
     */
    public void setFooterVisibility(int visibility) {
        if (visibility == GONE) {
            removeFooterView(footView);
        } else {
            if (footView.getVisibility() == GONE)
                addFooterView(footView);
        }
        footView.setVisibility(visibility);
    }

    /**
     * 显示或者隐藏footView
     */
    public void setHeaderVisibility(int visibility) {
        if (visibility == GONE) {
            removeHeaderView(headView);
        } else {
            if (headView.getVisibility() == GONE)
                addHeaderView(headView);
        }
        headView.setVisibility(visibility);

    }

    private void setHeadRefreshing() {
        if (getHeaderViewsCount() > 1)
            return;
        headView.setVisibility(INVISIBLE);
        addHeaderView(refreshHeader);
        isRefreshing = true;
    }

    private void removeHeaderAnim() {
        float distance = getTop() - refreshHeader.getBottom();
        TranslateAnimation animation = new TranslateAnimation(getLeft(), getLeft(), getTop(), distance);
        animation.setDuration(1000);
        animation.setFillAfter(true);
        animation.setAnimationListener(headAnimationListener);
        startAnimation(animation);
    }

    private Animation.AnimationListener headAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            removeHeaderView(refreshHeader);
            clearAnimation();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastMovePoint = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:

                if (getFirstVisiblePosition() == 0) {
                    int headLocation[] = new int[2];
                    int listLocation[] = new int[2];
                    getLocationOnScreen(listLocation);
                    headView.getLocationOnScreen(headLocation);

                    int deltaY = headLocation[1] - listLocation[1];
                    rotateImage(deltaY);
                }else{

                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (ev.getY() - lastMovePoint > 0){
                    pull_state = PULL_DOWN;
                }else{
                    pull_state=PULL_UP;
                }
                break;
        }

        return super.onTouchEvent(ev);
    }

    private void rotateImage(int deltaY) {
        if (deltaY > headView.getHeight()) {
            if (!wouldRefreshHeader) {
                head_text.setText("松开立即刷新");
                head_image.clearAnimation();
                head_image.startAnimation(mFlipAnimation);
            }
            wouldRefreshHeader = true;
        } else {
            if (wouldRefreshHeader) {
                head_text.setText("下拉刷新");
                head_image.clearAnimation();
                head_image.startAnimation(mReverseFlipAnimation);
            }
            wouldRefreshHeader = false;
        }
    }

    public boolean canRefresh() {
        return canRefresh;
    }

    public void setCanRefresh(boolean canRefresh) {
        this.canRefresh = canRefresh;
        if (canRefresh) {
            setHeaderVisibility(INVISIBLE);
        } else {
            setHeaderVisibility(GONE);
        }

    }

    public boolean canLoadMore() {
        return canLoadMore;
    }

    public void setCanLoadMore(boolean canLoadMore) {
        this.canLoadMore = canLoadMore;
        if (!canLoadMore) {
            setFooterVisibility(GONE);
        } else {
            setFooterVisibility(INVISIBLE);
        }
    }

    /**
     * 设置上一次更新的时间
     */
    public void setLastUpdated(String date) {
        TextView tv_date = (TextView) headView.findViewById(R.id.head_date);
        tv_date.setVisibility(VISIBLE);
        tv_date.setText(date);
    }

    public boolean isRefreshing() {
        return isRefreshing;
    }
}
