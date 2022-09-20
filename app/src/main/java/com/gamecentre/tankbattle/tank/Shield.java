package com.gamecentre.tankbattle.tank;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Shield extends GameObjects {
    Sprite sprite;
    Bitmap[] bitmap;
    int frame_delay;
    int frame;

    public Shield(int x, int y) {
        super(x,y);
        sprite = SpriteObjects.getInstance().getData(ObjectType.ST_SHIELD);
        Bitmap bm = Bitmap.createBitmap(TankView.graphics, sprite.x, sprite.y ,sprite.w,sprite.h*2);
        bitmap = new Bitmap[2];
        for(int i = 0; i < 2; i++){
            bitmap[i] = Bitmap.createBitmap(bm,0,i*sprite.h, sprite.w, sprite.h);
        }
        super.w = sprite.w;
        super.h = sprite.h;
        frame_delay = sprite.frame_time;
        frame = 0;
    }

    public void draw(Canvas canvas) {
        if(frame_delay <= 0) {
            frame = (frame + 1) % sprite.frame_count;
            frame_delay = sprite.frame_time;
        }
        else{
            --frame_delay;
        }
        canvas.drawBitmap(bitmap[frame],x,y,null);
    }
}
