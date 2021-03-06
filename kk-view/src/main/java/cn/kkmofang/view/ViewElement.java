package cn.kkmofang.view;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import cn.kkmofang.image.ImageGravity;
import cn.kkmofang.unity.R;
import cn.kkmofang.image.ImageStyle;
import cn.kkmofang.view.layout.FlexLayout;
import cn.kkmofang.view.layout.HorizontalLayout;
import cn.kkmofang.view.layout.RelativeLayout;
import cn.kkmofang.view.value.Color;
import cn.kkmofang.view.value.Edge;
import cn.kkmofang.view.value.Pixel;
import cn.kkmofang.view.value.Position;
import cn.kkmofang.view.value.Shadow;
import cn.kkmofang.view.value.Transform;
import cn.kkmofang.view.value.V;
import cn.kkmofang.view.value.VerticalAlign;


/**
 * Created by zhanghailong on 2018/1/17.
 */

public class ViewElement extends Element implements Cloneable{

    private float _x;
    private float _y;
    private float _width;
    private float _height;
    private float _contentWidth;
    private float _contentHeight;
    private float _contentOffsetX;
    private float _contentOffsetY;
    private Layout _layout = RelativeLayout;

    public IViewContext viewContext;

    public final Edge padding = new Edge();
    public final Edge margin = new Edge();

    public final Pixel width = new Pixel();
    public final Pixel minWidth = new Pixel();
    public final Pixel maxWidth = new Pixel();

    public final Pixel height = new Pixel();
    public final Pixel minHeight = new Pixel();
    public final Pixel maxHeight = new Pixel();

    public final Pixel left = new Pixel();
    public final Pixel top = new Pixel();
    public final Pixel right = new Pixel();
    public final Pixel bottom = new Pixel();

    public final Pixel borderWidth = new Pixel();
    public final Pixel borderRadius = new Pixel();
    public final Shadow shadow = new Shadow();

    public VerticalAlign verticalAlign = VerticalAlign.Top;
    public Position position = Position.None;

    //view背景
    private GradientDrawable gradientDrawable;

    private View _view;

    public ViewElement() {
        viewContext = ViewContext.current();
    }

    @Override
    public void changedKey(String key) {
        super.changedKey(key);
        String v = get(key);

        if("padding".equals(key)) {
            padding.set(v);
        } else if ("margin".equals(key)) {
            margin.set(v);
        } else if ("width".equals(key)) {
            width.set(v);
        } else if ("min-width".equals(key)) {
            minWidth.set(v);
        } else if ("max-width".equals(key)) {
            maxWidth.set(v);
        } else if ("height".equals(key)) {
            height.set(v);
        } else if ("min-height".equals(key)) {
            minHeight.set(v);
        } else if ("max-height".equals(key)) {
            maxHeight.set(v);
        } else if ("left".equals(key)) {
            left.set(v);
        } else if ("top".equals(key)) {
            top.set(v);
        } else if ("right".equals(key)) {
            right.set(v);
        } else if ("bottom".equals(key)) {
            bottom.set(v);
        } else if ("layout".equals(key)) {
            setLayout(v);
        } else if ("vertical-align".equals(key)) {
            verticalAlign = VerticalAlign.valueOfString(v);
        } else if ("position".equals(key)) {
            position = Position.valueOfString(v);
        } else if("border-width".equals(key)) {
            borderWidth.set(v);
        } else if("border-radius".equals(key)) {
            borderRadius.set(v);
        } else if("box-shadow".equals(key)) {
            shadow.set(v);
        }

        if(_view != null) {
            onSetProperty(_view,key,v);
        }
    }

    public float x() {
        return _x;
    }

    public float y() {
        return _y;
    }

    public float left() {
        return _x;
    }

    public float top() {
        return _y;
    }

    public float right() {
        return _x + _width;
    }

    public float bottom() {
        return _y + _height;
    }

    public float width() {
        return _width;
    }

    public float height() {
        return _height;
    }

    public void setWidth(float width) {
        _width = width;
    }

    public void setHeight(float height) {
        _height = height;
    }

    public void setX(float x) {
        _x = x;
    }

    public void setY(float y) {
        _y = y;
    }

    public float contentWidth() {
        return _contentWidth;
    }

    public float contentHeight() {
        return _contentHeight;
    }

    public void setContentSize(float width, float height) {
        _contentWidth = width;
        _contentHeight = height;
    }


    public float contentOffsetX() {
        return _contentOffsetX;
    }

    public float contentOffsetY() {
        return _contentOffsetY;
    }

    public void setContentOffset(float x, float y) {
        _contentOffsetX = x;
        _contentOffsetY = y;
    }

    public Layout layout() {
        return _layout;
    }

    public void setLayout(Layout v) {
        _layout = v;
    }

    public void setLayout(String v) {
        if ("relative".equals(v)) {
            _layout = RelativeLayout;
        } else if ("flex".equals(v)) {
            _layout = FlexLayout;
        } else if ("horizontal".equals(v)) {
            _layout = HorizontalLayout;
        } else {
            _layout = null;
        }
    }

    public View view() {
        return _view;
    }

    public void setView(View v) {
        _view = v;
    }

    public String reuse() {
        String v = get("reuse");
        if (v == null) {
            return "#" + levelId();
        }
        return v;
    }

    public Class<?> viewClass() {
        String v = get("#view");
        if (v == null) {
            return ElementView.class;
        }
        try {
            return Class.forName(v);
        } catch (Throwable e) {
            Log.d(Tag.Tag, Log.getStackTraceString(e));
        }
        return ElementView.class;
    }

    protected View createView() {

        View vv = null;

        Class<?> viewClass = viewClass();
        if (viewClass != null) {
            try {
                vv = (View) viewClass.getConstructor(Context.class).newInstance(viewContext.getContext());
            } catch (Throwable e) {
                Log.d(Tag.Tag, Log.getStackTraceString(e));
            }
        }

        return vv;
    }

    public void obtainView(View view) {

        if (_view != null && _view.getParent() == view) {

            obtainChildrenView();

            return;
        }

        recycleView();

        Class<?> viewClass = this.viewClass();

        View vv = null;

        String reuse = reuse();

        if (reuse != null && !"".equals(reuse)) {

            Map<String, Queue<View>> dequeue = (Map<String, Queue<View>>) view.getTag(R.id.kk_view_dequeue);

            if (dequeue != null && dequeue.containsKey(reuse)) {
                Queue<View> queue = dequeue.get(reuse);
                while (!queue.isEmpty()) {
                    vv = queue.poll();
                    if(viewClass.isAssignableFrom(vv.getClass())) {
                        break;
                    }
                }
            }

        }

        if (vv == null) {
            vv = createView();
        }

        if (vv == null) {
            vv = new ElementView(viewContext.getContext());
        }

        Element p = parent();

        if (p != null && p instanceof ViewElement) {
            ((ViewElement) p).addSubview(vv, this, view);
        } else if (view instanceof ViewGroup) {
            ((ViewGroup) view).addView(vv);
        }

        setView(vv);
        onObtainView(vv);
        onLayout(vv);

        for (String key : keys()) {
            String v = get(key);
            onSetProperty(vv, key, v);
        }

        obtainChildrenView();
    }

    public void recycleView() {

        if (_view != null) {

            recycleView(this);


            Element e = firstChild();

            while (e != null) {
                if (e instanceof ViewElement) {
                    ((ViewElement) e).recycleView();
                }
                e = e.nextSibling();
            }


        }
    }

    public void obtainChildrenView() {
        obtainChildrenView(_view);
    }

    protected void obtainChildrenView(View view) {

        if (view != null) {

            Element p = firstChild();

            while (p != null) {
                if (p instanceof ViewElement) {
                    ViewElement e = (ViewElement) p;
                    if (isChildrenVisible(e)) {
                        e.obtainView(view);
                    } else {
                        e.recycleView();
                    }
                }
                p = p.nextSibling();
            }

        }
    }

    public void addSubview(View view, ViewElement element, View toView) {
        if (toView instanceof ViewGroup) {
            ViewGroup p = (ViewGroup) toView;
            String v = element.get("floor");
            if ("back".equals(v)) {
                p.addView(view, 0);
            } else {
                p.addView(view);
            }
        }
    }

    @Override
    protected void onWillRemoveChildren(Element element) {
        super.onWillRemoveChildren(element);

        if (element instanceof ViewElement) {
            ((ViewElement) element).recycleView();
        }
    }

    @Override
    protected void onDidAddChildren(Element element) {
        super.onDidAddChildren(element);

        if(element instanceof ViewElement) {

            ViewElement e = (ViewElement) element;

            if(e.viewLayer() != View.LAYER_TYPE_NONE) {
                setViewLayer(e.viewLayer());
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public static void recycleView(ViewElement element) {

        View view = element.view();

        if(view == null) {
            return;
        }

        ViewGroup p = (ViewGroup) view.getParent();

        if (p != null) {

            String reuse = element.reuse();

            if (reuse != null && !"".equals(reuse) && !(view instanceof SurfaceView)) {

                Map<String, Queue<View>> dequeue = (Map<String, Queue<View>>) p.getTag(R.id.kk_view_dequeue);

                if (dequeue == null) {
                    dequeue = new TreeMap<>();
                    p.setTag(R.id.kk_view_dequeue, dequeue);
                }

                Queue<View> queue;

                if (dequeue.containsKey(reuse)) {
                    queue = dequeue.get(reuse);
                } else {
                    queue = new LinkedList<>();
                    dequeue.put(reuse, queue);
                }

                queue.add(view);

            }

            p.removeView(view);
        }

        element.onRecycleView(view);

        element.setView(null);
    }

    @Override
    public void remove() {
        recycleView();
        super.remove();
    }

    @Override
    public void recycle() {
        recycleView(this);
        super.recycle();
    }

    public boolean isChildrenVisible(ViewElement element) {

        if(V.booleanValue(element.get("keepalive"),false)) {
            return true;
        }

        int l = (int) Math.max(element.left(), contentOffsetX());
        int t = (int) Math.max(element.top(), contentOffsetY());
        int r = (int) Math.ceil(Math.min(element.right(), contentOffsetX() + width()));
        int b = (int) Math.ceil(Math.min(element.bottom(), contentOffsetY() + height()));

        return r > l && b > t;
    }

    public boolean isHidden() {
        return V.booleanValue(get("hidden"), false);
    }

    public void layoutChildren() {
        if (_layout != null) {
            _layout.layout(this);
        }
    }

    public void willLayout() {

    }

    public void onLayout() {
        if (_view != null) {
            onLayout(_view);
        }
    }

    public void layout(int width, int height) {
        _width = width;
        _height = height;
        if (_layout != null) {
            _layout.layout(this);
        }
        onLayout();
    }

    private Set<String> _changedKeys = null;

    private void setOnChangedKeys(View view,String key) {

        boolean v = _changedKeys == null;

        if(_changedKeys == null) {
            _changedKeys = new TreeSet<>();
        }

        _changedKeys.add(key);

        if(v) {

            final WeakReference<ViewElement> e = new WeakReference<>(this);

            view.post(new Runnable() {
                @Override
                public void run() {
                    ViewElement element = e.get();
                    if(element != null) {
                        Set<String> keys = element._changedKeys;
                        if(keys != null) {
                            element._changedKeys = null;
                            element.onChangedKeys(keys);
                        }
                    }
                }
            });

        }
    }

    protected void updateAnimKeys(View view,Set<String> keys) {

        if(keys.contains("opacity")) {
            float v = V.floatValue(get("opacity"),1.0f);
            if(v > 1.0f) {
                v = 1.0f;
            } else if(v < 0.0f) {
                v = 0.0f;
            }
            view.setAlpha(v);
        }

        if(keys.contains("transform")) {
            Transform.valueOf(view,get("transform"));
        }
    }

    private List<String> _animatingList = null;
    protected void onChangedKeys(final Set<String> keys) {

        View view = this.view();

        if(view ==null) {
            return;
        }

        if(keys.contains("animation")) {

            final String name = get("animation");

            boolean hasAnimating = false;

            if(_animatingList == null || !_animatingList.contains(name)) {

                if (_animatingList == null){
                    _animatingList  = new ArrayList<>();
                }

                view.clearAnimation();

                if (name != null) {

                    Element p = firstChild();

                    while (p != null) {
                        if (p instanceof AnimationElement) {
                            if (name.equals(p.get("name"))) {
                                final WeakReference<ViewElement> e = new WeakReference<>(this);
                                final WeakReference<AnimationElement> anims = new WeakReference<>((AnimationElement) p);
                                ((AnimationElement) p).startAnimation(view, new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        _animatingList.add(name);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        ViewElement element = e.get();
                                        _animatingList.remove(name);
                                        if(element != null) {
                                            View view = element.view();
                                            if(view != null) {
                                                AnimationElement p = anims.get();
                                                if (p != null){
                                                    Element e = p.firstChild();
                                                    while (e != null){
                                                        if (e instanceof AnimationElement.Transform){
                                                            keys.add("transform");
                                                        } else if (e instanceof AnimationElement.Opacity){
                                                            keys.add("opacity");
                                                        }
                                                        e = e.nextSibling();
                                                    }
                                                }
                                                element.updateAnimKeys(view,keys);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                });
                                hasAnimating = true;
                                break;
                            }
                        }
                        p = p.nextSibling();
                    }
                }

            }

            if(!hasAnimating) {
                updateAnimKeys(view,keys);
            }

        } else {
            updateAnimKeys(view,keys);
        }

    }


    protected void onSetProperty(View view, String key, String value) {

        if("opacity".equals(key)) {
            setOnChangedKeys(view,key);
        } else if("hidden".equals(key)) {
            setVisible(!V.booleanValue(value, true), view);
        } else if("overflow".equals(key)) {
            if ("hidden".equals(value)) {
                if (view instanceof ViewGroup) {
                    ((ViewGroup) view).setClipChildren(true);
                }
            }
        } else if("animation".equals(key)) {
            setOnChangedKeys(view,key);
        } else if("transform".equals(key)) {
            setOnChangedKeys(view,key);
        } else if(key.startsWith("border")) {
            drawBorder();
        } else if ("background-color".equals(key)){
            view.setBackgroundColor(Color.valueOf(value, 0));
        } else if ("background-image".equals(key)){
            if (viewContext != null){
                ImageStyle style = new ImageStyle(viewContext.getContext());
                style.gravity = ImageGravity.RESIZE;
                Drawable background = viewContext.getImage(get("background-image"), style);
                view.setBackground(background);
            }
        }

        if (view instanceof IElementView) {
            ((IElementView) view).setProperty(view, this, key, value);
        }
    }

    private int _viewLayer = View.LAYER_TYPE_NONE;

    public int viewLayer() {
        return _viewLayer;
    }

    public void setViewLayer(int viewLayer) {

        _viewLayer = viewLayer;

        if(_viewLayer != View.LAYER_TYPE_NONE) {

            View v = view();

            if(v != null) {
                v.setLayerType(_viewLayer,null);
            }
        }
    }

    /**
     * 设置view是否隐藏
     * @param visible
     */
    private void setVisible(boolean visible, View view){
        view.setVisibility(visible?View.VISIBLE:View.GONE);
    }


    private boolean drawBorder = false;
    protected void drawBorder(){
        if (drawBorder)return;
        View view = view();
        if (view != null){
            view.setWillNotDraw(false);
            Handler handler = view.getHandler();
            if (handler != null){
                drawBorder = true;
                final WeakReference<View> v = new WeakReference<>(view);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        View vv = v.get();
                        if (vv != null){
                            vv.invalidate();
                        }
                        drawBorder = false;
                    }
                });
            }
        }
    }

    protected void onLayout(View view) {
        if (view instanceof IElementView) {
            ((IElementView) view).layout(view, this);
        }
    }

    protected void onObtainView(View view) {
        view.setTag(R.id.kk_view_element, new WeakReference<ViewElement>(this));
        if (view instanceof IElementView) {
            ((IElementView) view).obtainView(view, this);
        }
    }

    protected void onRecycleView(View view) {

        if (view instanceof IElementView) {
            ((IElementView) view).recycleView(view, this);
        }

        view.setTag(R.id.kk_view_element, null);
    }

    public static interface Layout {
        public void layout(ViewElement element);
    }

    /**
     * 相对布局 "relative"
     */
    public static Layout RelativeLayout = new RelativeLayout();

    /**
     * 流式布局 "flex" 左到右 上到下
     */
    public static Layout FlexLayout = new FlexLayout();

    /**
     * 水平布局 "horizontal" 左到右
     */
    public static Layout HorizontalLayout = new HorizontalLayout();
}
