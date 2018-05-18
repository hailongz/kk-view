package cn.kkmofang.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;

import cn.kkmofang.view.value.Pixel;
import cn.kkmofang.view.value.TextAlign;

/**
 * Created by zhanghailong on 2018/4/18.
 */

public class Text {

    public float maxWidth = 0;
    public float lineSpacing = 0;
    public float letterSpacing = 0;
    public TextAlign textAlign = TextAlign.Left;
    public final TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    public final SpannableStringBuilder string = new SpannableStringBuilder();

    private StaticLayout _layout;
    private float _width;

    public void setNeedDisplay() {
        _layout = null;
    }

    public boolean isNeedDisplay() {
        return _layout == null;
    }

    public void setMaxWidth(int v) {
        if(maxWidth != v) {
            maxWidth = v;
            setNeedDisplay();
        }
    }

    public void setLineSpacing(int v) {
        if(lineSpacing != v) {
            lineSpacing = v;
            setNeedDisplay();
        }
    }

    public void setLetterSpacing(int v) {
        if(letterSpacing != v) {
            letterSpacing = v;
            setNeedDisplay();
        }
    }


    public void setTextAlign(TextAlign v) {
        if(textAlign != v) {
            textAlign = v;
            setNeedDisplay();
        }
    }

    public void draw(Canvas canvas,int width,int height) {
        build();
        canvas.translate(0,(height - _layout.getHeight()) * 0.5f);
        _layout.draw(canvas);
    }


    public void build() {

        if(_layout == null) {


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                paint.setLetterSpacing(letterSpacing);
            }

            Layout.Alignment align = Layout.Alignment.ALIGN_NORMAL;

            if(textAlign == TextAlign.Center) {
                align = Layout.Alignment.ALIGN_CENTER;
            } else if(textAlign == TextAlign.Right) {
                align = Layout.Alignment.ALIGN_OPPOSITE;
            }

            if(maxWidth == Pixel.Auto) {
                align =  Layout.Alignment.ALIGN_NORMAL;
            }

           _layout = new StaticLayout(
                    string,
                    0,
                    string.length(),
                    paint,
                   (int) Math.ceil(maxWidth),
                    align,
                    1.0f,
                    0f,
                    false);

            _width = 0;

            for(int i=0;i<_layout.getLineCount();i++) {
                float v = _layout.getLineWidth(i);
                if(v > _width) {
                    _width = v;
                }
            }

        }

    }

    public float width() {
        build();
        return _width;
    }

    public float height() {
        build();
        return _layout.getHeight();
    }

}
