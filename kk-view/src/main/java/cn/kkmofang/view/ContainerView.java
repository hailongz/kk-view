package cn.kkmofang.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import cn.kkmofang.view.value.Pixel;
import cn.kkmofang.view.value.Position;

/**
 * Created by zhanghailong on 2018/4/19.
 */

public class ContainerView extends ElementView {


    public final ContentView contentView;
    public final FrameLayout scrollView;

    public ContainerView(Context context, boolean horizontal ) {
        super(context);
        if(horizontal) {
            scrollView = new HScrollView(context, this);
        } else {
            scrollView = new ScrollView(context, this);
        }
        addView(scrollView);
        contentView = new ContentView(context);
        scrollView.addView(contentView);
    }

    private int _pullScrollY = -1;
    private boolean _pulling = false;
    private boolean _cancelPullScrolling = false;

    public void setCancelPullScrolling(boolean v) {
        _cancelPullScrolling = v;
    }

    protected View positionPullView() {
        if(_positions != null && _positions.containsKey(Position.Pull.intValue()))  {
            return _positions.get(Position.Pull.intValue());
        }

        return null;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if(positionPullView() == null){
            return super.dispatchTouchEvent(ev);
        }

        if(_cancelPullScrolling && ev.getAction() == MotionEvent.ACTION_MOVE) {
            return super.dispatchTouchEvent(ev);
        }

        if(scrollView instanceof android.widget.ScrollView) {

            switch (ev.getAction() ) {
                case MotionEvent.ACTION_DOWN:
                {
                    _cancelPullScrolling = false;

                    onStartTracking();
                }
                    break;
                case MotionEvent.ACTION_MOVE:
                {
                    if(scrollView.getScrollY() ==0 ){

                        int dy = (int) ev.getY();

                        if(_pullScrollY == -1) {
                            _pullScrollY = dy;
                        }

                        int y = (int) ((_pullScrollY - dy ) * 0.5f);

                        if(!_pulling && y < 0 ){
                            _pulling = true;
                        }

                        if(_pulling) {

                            contentView.setTranslationY(-y);
                            onScrollChanged(0, y, 0, 0);
                            View v = positionPullView();
                            if (v != null) {
                                v.setTranslationY(-y);
                            }

                            return false;

                        }

                    } else if(_pulling) {
                        contentView.setTranslationY(0);
                        View v= positionPullView();
                        if(v != null) {
                            v.setTranslationY(0);
                        }
                        onScrollChanged(0,0,0,0);
                    }
                }
                break;
                default:
                {
                    _cancelPullScrolling = false;

                    onStopTracking();

                    if(_pulling) {
                        _pullScrollY = -1;
                        _pulling = false;
                        contentView.setTranslationY(0);
                        View v = positionPullView();
                        if (v != null) {
                            v.setTranslationY(0);
                        }
                        onScrollChanged(0,0,0,0);
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                    }

                }
                break;
            }

        }

        return super.dispatchTouchEvent(ev);
    }

    public void setContentSize(int width,int height) {
        contentView.setContentSize(width,height);
    }

    public void scrollToVisible(int l,int t,int r,int b,boolean animated) {
        int tx = scrollView.getScrollX();
        int ty = scrollView.getScrollY();
        int v_l = Math.max(l, tx);
        int v_t = Math.max(t, ty);
        int v_r = Math.min(r, tx + getWidth());
        int v_b = Math.min(b, ty + getHeight());

        if(v_r - v_l < r -  l) {
            if(l < tx) {
                tx = l;
            } else {
                tx = tx + getWidth() - (r - l);
            }
        }

        if(v_b - v_t < b -  t) {
            if(t < ty) {
                ty = t;
            } else {
                ty = ty + getHeight() - (b - t);
            }
        }

        if(animated) {
            {
                @SuppressLint("ObjectAnimatorBinding") ObjectAnimator animator = ObjectAnimator.ofInt(scrollView, "scrollX", scrollView.getScrollX(), tx);
                animator.setDuration(300);
                animator.start();
            }
            {
                @SuppressLint("ObjectAnimatorBinding") ObjectAnimator animator = ObjectAnimator.ofInt(scrollView, "scrollY", scrollView.getScrollY(), ty);
                animator.setDuration(300);
                animator.start();
            }
        } else {
            scrollView.scrollTo(tx, ty);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int count = getChildCount();

        for(int i=0;i<count;i++) {

            View v = getChildAt(i);

            if(v != null && v.getVisibility() == View.VISIBLE) {

                WeakReference<ViewElement> e = (WeakReference<ViewElement>) v.getTag(cn.kkmofang.unity.R.id.kk_view_element);

                if(e != null) {
                    ViewElement element = e.get();
                    if(element != null) {
                        v.layout(
                                (int) (element.left()),
                                (int) (element.top()),
                                (int) Math.ceil(element.right()),
                                (int) Math.ceil(element.bottom()));
                    }
                } else {

                    if(_positions != null) {

                        boolean isPosotionView = false;

                        for(int position : _positions.keySet()) {
                            PositionView positionView = _positions.get(position);
                            if(positionView == v) {

                                isPosotionView = true;

                                ViewElement ee = positionView.element();

                                if(ee != null) {

                                    float mleft = ee.margin.left.floatValue(0, 0);
                                    float mright = ee.margin.right.floatValue(0, 0);
                                    float mtop = ee.margin.top.floatValue(0, 0);
                                    float mbottom = ee.margin.bottom.floatValue(0, 0);
                                    int width = (int) Math.ceil( ee.width() + mleft + mright);
                                    int height = (int) Math.ceil( ee.height() + mtop + mbottom);

                                    if(position == Position.Top.intValue()) {
                                        v.layout(0,0,width,height);
                                    } else if(position == Position.Bottom.intValue()) {
                                        v.layout(0,b - t - height,width,b - t );
                                        break;
                                    } else if(position == Position.Pull.intValue()) {
                                        v.layout(0,-height,width,0);
                                        break;
                                    }
                                }

                                break;
                            }
                        }


                        if(isPosotionView) {
                            continue;
                        }
                    }

                    v.layout(0,0,r-l,b-t);
                }
            }

        }
    }

    protected void onScrollChanged(int l, int t, int oldl, int oldt) {


        ViewParent p = getParent();

        while(p != null) {
            if(p instanceof ContainerView) {
                ContainerView v = (ContainerView) p;
                v.setCancelPullScrolling(true);
            }
            p = p.getParent();
        }

        onScroll(l, t);
    }

    public static class ContentView extends ElementView {

        private int _contentWidth;
        private int _contentHeight;

        public ContentView(Context context) {
            super(context);
            setClipChildren(false);
        }

        public void setContentSize(int width,int height) {
            _contentWidth = width;
            _contentHeight = height;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(_contentWidth,_contentHeight);
            measureChildren(widthMeasureSpec,heightMeasureSpec);
        }

    }


    private Map<Integer,PositionView> _positions;

    public boolean isPosition(ViewElement element) {
        return _positions != null && _positions.containsKey(element.position.intValue()) && _positions.get(element.position.intValue()).element() != element;
    }

    public void setPosition(ViewElement element,Position position) {

        if(element == null) {

            if(_positions != null && _positions.containsKey(position.intValue())) {
                PositionView v = _positions.get(position.intValue());
                v.setVisibility(View.GONE);

                ViewElement e = v.element();

                if(e != null) {
                    e.obtainView(contentView);
                    if(position == Position.Pull) {
                        View vv = e.view();
                        if(vv != null) {
                            vv.setTranslationY(0);
                        }
                    }
                }

                v.setElement(null);
            }

        } else {

            if(_positions == null) {
                _positions = new TreeMap<>();
            }

            PositionView v;

            if(_positions.containsKey(position.intValue())) {
                v = _positions.get(position.intValue());
            } else {
                v = new PositionView(getContext());
                _positions.put(position.intValue(),v);
                addView(v);
            }

            v.setVisibility(View.VISIBLE);

            ViewElement e = v.element();

            if(e != null) {
                e.obtainView(contentView);
            }

            v.setElement(element);

            element.obtainView(v);

        }
    }

    private void onScroll(int x, int y){
        if (_scrollListeners != null){
            for (OnScrollListener listener : _scrollListeners) {
                listener.onScroll(x , y);
            }
        }
    }

    private void onStartTracking(){
        if (_scrollListeners != null){
            for (OnScrollListener listener : _scrollListeners) {
                listener.onStartTracking();
            }
        }
    }

    private void onStopTracking(){
        if (_scrollListeners != null){
            for (OnScrollListener listener : _scrollListeners) {
                listener.onStopTracking();
            }
        }
    }

    private Set<OnScrollListener> _scrollListeners = new HashSet<>();
    public void setScrollListener(OnScrollListener listener){
        if (_scrollListeners != null){
            _scrollListeners.add(listener);
        }
    }

    public void removeScrollListener(OnScrollListener listener){
        if (_scrollListeners != null){
            _scrollListeners.remove(listener);
        }
    }

    public void removeScrollListeners(){
        if (_scrollListeners != null){
            _scrollListeners.clear();
        }
    }
//    private OnScrollListener _OnScrollListener;
//
//    public void setOnScrollListener(OnScrollListener v) {
//        _OnScrollListener = v;
//    }
//
//    public OnScrollListener getOnScrollListener() {
//        return _OnScrollListener;
//    }

    public static interface OnScrollListener {
        void onScroll(int x,int y);
        void onStartTracking();
        void onStopTracking();
    }

    public static class ScrollView extends NestedScrollView {

        private WeakReference<ContainerView> _containerView;

        public ScrollView(Context context,ContainerView containerView) {
            super(context);
            _containerView = new WeakReference<>(containerView);
            setVerticalFadingEdgeEnabled(false);
            setOverScrollMode(OVER_SCROLL_NEVER);
        }

        @Override
        protected void onScrollChanged(int l, int t, int oldl, int oldt) {
            super.onScrollChanged(l,t,oldl,oldt);
            ContainerView v = _containerView.get();
            if(v != null ){
                v.onScrollChanged(l,t,oldl,oldt);
            }
        }

    }

    public static class HScrollView extends android.widget.HorizontalScrollView {

        private WeakReference<ContainerView> _containerView;

        public HScrollView(Context context,ContainerView containerView) {
            super(context);
            _containerView = new WeakReference<>(containerView);
            setHorizontalScrollBarEnabled(false);
            setHorizontalFadingEdgeEnabled(false);
            setOverScrollMode(OVER_SCROLL_NEVER);
        }

        @Override
        protected void onScrollChanged(int l, int t, int oldl, int oldt) {
            super.onScrollChanged(l,t,oldl,oldt);
            ContainerView v = _containerView.get();
            if(v != null ){
                v.onScrollChanged(l,t,oldl,oldt);
            }
        }

        public ContainerView getParentView(){
            ViewParent parent = getParent();
            while (parent != null){
                if (parent instanceof ContainerView){
                    return (ContainerView) parent;
                }
                parent = parent.getParent();
            }
            return null;
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            ContainerView parent = getParentView();
            switch (ev.getAction()){
                case MotionEvent.ACTION_DOWN:
                    if (parent != null){
                        parent.setCancelPullScrolling(true);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:

                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    if (parent != null){
                        parent.setCancelPullScrolling(false);
                    }
                    break;
            }
            return super.dispatchTouchEvent(ev);
        }
    }

    public static class PositionView  extends ElementView {

        public PositionView(Context context) {
            super(context);
        }

        private ViewElement _element;

        public ViewElement element() {
            return _element;
        }

        public void setElement(ViewElement element) {
            _element = element;
        }


        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {

            if(_element != null) {
                View v = _element.view();
                if(v != null) {
                    int marginLeft = (int) _element.margin.left.floatValue(0,0);
                    int marginRight = (int) Math.ceil( _element.margin.right.floatValue(0,0));
                    int marginTop = (int) _element.margin.top.floatValue(0,0);
                    int marginBottom = (int) Math.ceil( _element.margin.bottom.floatValue(0,0));
                    v.layout(marginLeft,marginTop,r - l - marginRight,b -t - marginBottom);
                }
            }

        }
    }
}
