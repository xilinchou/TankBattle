package com.gamecentre.tankbattle.tank;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.gamecentre.tankbattle.R;
import com.gamecentre.tankbattle.sound.SoundManager;
import com.gamecentre.tankbattle.sound.Sounds;

public class Eagle extends GameObjects{

    private final Bitmap[] bitmap;
    private final Bitmap[] dbitmap;
    private final Sprite sprite;
    private final Sprite dsprite;
    private int frame;
    private int frame_delay;
    private boolean dead = false;
    public int protection = 0;

    public Eagle() {
        super(0,0);
        sprite = SpriteObjects.getInstance().getData(ObjectType.ST_EAGLE);
        Bitmap bm = Bitmap.createBitmap(TankView.graphics, sprite.x, sprite.y ,sprite.w,sprite.h*2);
        bitmap = new Bitmap[2];
        for(int i = 0; i < 2; i++){
            bitmap[i] = Bitmap.createBitmap(bm,0,i*sprite.h, sprite.w, sprite.h);
        }

        dsprite = SpriteObjects.getInstance().getData(ObjectType.ST_DESTROY_EAGLE);
        Bitmap dbm = Bitmap.createBitmap(TankView.graphics, dsprite.x, dsprite.y ,dsprite.w,dsprite.h*dsprite.frame_count);
        dbitmap = new Bitmap[dsprite.frame_count];
        for(int i = 0; i < dsprite.frame_count; i++){
            dbitmap[i] = Bitmap.createBitmap(dbm,0,i*dsprite.h, dsprite.w, dsprite.h);
        }
        frame_delay = dsprite.frame_time;
        frame = 0;

        super.w = sprite.w;
        super.h = sprite.h;
        super.x = (int)(TankView.WIDTH/2-w/2);
        super.y = (int)(TankView.HEIGHT - sprite.h);
    }

    public void collidesWithBullet(Bullet b) {
        if(super.collides_with(b)) {
            setDestroyed();
        }
    }

    public void setDestroyed() {
        super.setDestroyed();
        SoundManager.playSound(Sounds.TANK.EXPLOSION);
        frame = 0;
        frame_delay = dsprite.frame_time;
    }

    public void draw(Canvas canvas) {
        int bx, by, bw, bh;
        Bitmap bm;
        bw = sprite.w;
        bh = sprite.h;
        if(!destroyed || dead) {
            if(!destroyed) {
                canvas.drawBitmap(bitmap[0], x, y, null);
            }
            else {
                canvas.drawBitmap(bitmap[1], x, y, null);
            }
        }
        else {
            if (frame < dsprite.frame_count) {
                canvas.drawBitmap(dbitmap[frame], x - (int) (dsprite.w / 4), y - (int) (dsprite.h / 4), null);
                if(frame_delay <= 0) {
                    frame = frame + 1;
                    frame_delay = dsprite.frame_time;
                }
                else{
                    --frame_delay;
                }
            }
            if (frame >= dsprite.frame_count) {
                dead = true;
            }
        }
    }
}
