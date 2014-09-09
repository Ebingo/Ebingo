package com.promote.ebingo.publish.login;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.promote.ebingo.R;

import org.w3c.dom.Text;

/**
 * Created by acer on 2014/9/5.
 */
public class TagView extends TextView {

    private Paint paint=new Paint();
    private TagViewInfo info;
    private int selectColor;
    private int noneSelectColor;
    public TagView(Context context) {
        this(context, null);
    }
    public TagView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        info=new TagViewInfo();
        selectColor=getResources().getColor(R.color.yellow);
        noneSelectColor=getResources().getColor(R.color.light_gray);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!info.cancelAble&&info.checked){
            paint.setColor(selectColor);
        }else{
            paint.setColor(noneSelectColor);
        }

        canvas.drawRect(0,0,getWidth(),getHeight(),paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_UP:
                if (!info.cancelAble){
                    info.checked=!info.checked;
                }
                invalidate();
                break;
        }
        return true;
    }

    class TagViewInfo{
        boolean checked=false;
        boolean cancelAble=false;
        CharSequence text="";
    }
}
