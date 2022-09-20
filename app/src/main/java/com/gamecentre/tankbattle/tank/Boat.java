package com.gamecentre.tankbattle.tank;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Boat extends GameObjects {
    Sprite sprite;
    Bitmap bitmap;

    public Boat(int x, int y, int player) {
        super(x,y);
        sprite = SpriteObjects.getInstance().getData(ObjectType.ST_BOAT_P1);
        bitmap = Bitmap.createBitmap(TankView.graphics, sprite.x + (player-1)*sprite.w, sprite.y ,sprite.w,sprite.h);
        bitmap = Bitmap.createBitmap(bitmap,0,0,sprite.w,sprite.h);
        super.w = sprite.w;
        super.h = sprite.h;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap,x,y,null);
    }
}
