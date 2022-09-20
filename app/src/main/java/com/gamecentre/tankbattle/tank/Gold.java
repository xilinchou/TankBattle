package com.gamecentre.tankbattle.tank;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.gamecentre.tankbattle.R;


public class Gold extends GameObjects{

    Bitmap bitmap;
    Paint paint;
    private boolean available = false;
    private int alpha = 0;
    private final int alphaStep = 10;
    private boolean upCount = true;
    private boolean taken = false;
    private  int scoreTmr = 5;


    public Gold() {
        super(0,0);
        bitmap = BitmapFactory.decodeResource(TankView.context.getResources(), R.drawable.gold_bar);
        bitmap = Bitmap.createScaledBitmap(bitmap,TankView.tile_dim,TankView.tile_dim,false);
        paint = new Paint();
        super.w = TankView.tile_dim;
        super.h = TankView.tile_dim;
        available = false;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setAvailable(boolean available) {
        this.available = available;
        this.taken = false;
        scoreTmr = 5;
    }

    public boolean isAvailable() {
        return available && !taken;
    }

    public void setTaken() {
        taken = true;
    }


    public void draw(Canvas canvas) {
        if(!available) {
            return;
        }
        int step = alphaStep;
        if(!taken) {
            if (upCount) {
                alpha += step;
                if (alpha >= 250) {
                    upCount = false;
                }
            } else {
                alpha -= step;
                if (alpha <= 10) {
                    upCount = true;
                }
            }
            paint.setAlpha(alpha);
            canvas.drawBitmap(bitmap, x, y, paint);
        }
        else {
            if(scoreTmr > 0) {
                drawText(canvas,"800");
                --scoreTmr;
            }
            else {
                available = false;
            }
        }
    }
}
