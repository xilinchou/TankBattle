package com.gamecentre.tankbattle.tank;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class BrickTextView  extends androidx.appcompat.widget.AppCompatTextView{

    public BrickTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public BrickTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BrickTextView(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(this.getContext().getAssets(),"tank_font.ttf");
        setTypeface(tf);
    }
}
