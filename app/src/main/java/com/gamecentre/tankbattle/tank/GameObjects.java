package com.gamecentre.tankbattle.tank;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import java.io.Serializable;

public class GameObjects implements Serializable {

    public int x,y;
    public int w,h;
    protected Rect rect;
    protected boolean destroyed = false;
    public boolean recycle = false;
    public boolean svrKill = false;

    public GameObjects(int x, int y, int w, int h){
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        rect = new Rect(x,y,x+w,y+h);
    }

    public GameObjects(int x, int y) {
        this.x = x;
        this.y = y;
        rect = new Rect();
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Rect getRect() {
        rect.left = x;
        rect.top = y;
        rect.right = (x+w);
        rect.bottom = (y+h);
        return rect;
    }

    protected Rect getDestRect() {
        Rect dRect = new Rect();
        dRect.set(getRect());
        dRect.left = x + 2;
        dRect.top = y + 2;
        dRect.right = (x+w) - 2;
        dRect.bottom = (y+h) - 2;
        return dRect;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void setDestroyed() {
        destroyed = true;
    }

    public void recycle() {
        destroyed = true;
        recycle = true;
    }

    protected boolean collides_with(GameObjects targ) {
        return Rect.intersects(getRect(),targ.getRect());
    }

    protected boolean collides_with_wall() {
        getRect();

        if(rect.top < 4 || rect.left < 4 || rect.right > TankView.WIDTH || rect.bottom > TankView.HEIGHT) {
            return true;
        }
        else {
            return false;
        }
    }

    protected void drawText(Canvas canvas, String text) {
//        canvas.drawText(text,(int)(x+w/2),(int)(y+h/2),TankView.txtPaint);
//        canvas.drawText(text,(int)(x),(int)(y),TankView.txtPaint);
        canvas.drawText(text,(int)(x),(int)(y+h/2),TankView.txtPaint);
    }

    protected void draw(Canvas canvas) {
        Log.d("DRAW", "Cannot draw from here");
    }
}
