package com.gamecentre.tankbattle.tank;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Ice extends GameObjects{
    Sprite sprite;
    Bitmap bitmap;

    public Ice(int x, int y) {
        super(x, y);

        sprite = SpriteObjects.getInstance().getData(ObjectType.ST_ICE);
        bitmap = Bitmap.createBitmap(TankView.graphics, sprite.x, sprite.y ,sprite.w,sprite.h);
        bitmap = Bitmap.createBitmap(bitmap,0,0,sprite.w,sprite.h);
        super.w = sprite.w;
        super.h = sprite.h;
        super.x = x*sprite.w;
        super.y = y*sprite.h;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap,x,y,null);
    }
}
