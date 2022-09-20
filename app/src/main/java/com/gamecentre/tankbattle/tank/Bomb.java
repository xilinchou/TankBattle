package com.gamecentre.tankbattle.tank;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import com.gamecentre.tankbattle.utils.CONST;

public class Bomb extends GameObjects{
    Sprite sprite;
    Bitmap[] bitmap;
    int frame_delay = 0;
    int frame = 0;
    boolean explode = false;
    int fuseTime = 4*TankView.FPS;
    int fuseTmr;
    Rect[] explodeRect = {new Rect(), new Rect(), new Rect(), new Rect(), new Rect()};
    boolean from_player;
    public int id = 0;
    private boolean dropped = false;
    boolean up = true;
    private long activateTime;
    private boolean moving = false;
    float acc,v;
    int dir;

    private final int
            MIDDLE = 0,
            UP = 1,
            DOWN = 2,
            RIGHT = 3,
            LEFT = 4;


    public Bomb(int x, int y, boolean from_player) {
        super(x, y);
//        sprite = SpriteObjects.getInstance().getData(ObjectType.ST_WATER);
//        Bitmap bm = Bitmap.createBitmap(TankView.graphics, sprite.x, sprite.y ,sprite.w,sprite.h*2);
//        bitmap = new Bitmap[2];
//        for(int i = 0; i < 2; i++){
//            bitmap[i] = Bitmap.createBitmap(bm,0,i*sprite.h, sprite.w, sprite.h);
//        }
        super.w = TankView.bombSprite.w;
        super.h = TankView.bombSprite.h;
        this.from_player = from_player;



        fuseTmr = fuseTime;
        acc = -TankView.tile_dim/20;
        v = TankView.tile_dim/2;


//        super.x = x*sprite.w;
//        super.y = y*sprite.h;
    }

    public void setPosition(int xp, int yp) {
        this.x = xp+w/2;
        this.y = yp+h/2;



    }

    public boolean isMoving() {
        return moving;
    }

    public void drop(int dir) {
        ++id;
        dropped = true;
        moving = true;
        this.dir = dir;
        v = TankView.tile_dim/2f;
        fuseTmr = fuseTime;
        frame = 0;
        frame_delay = TankView.bombSprite.frame_time;
    }

    public void activate() {
        activateTime = System.currentTimeMillis();
    }

    public void setDestroyed() {
        dropped = false;
    }

    public void setExplosion() {
        frame = 0;
        explode = true;
        dropped = false;
        frame_delay = TankView.fireSprite.frame_time;
        up = true;

        int dim = TankView.fireSprite.w;

        explodeRect[MIDDLE].left = x;
        explodeRect[MIDDLE].top = y;
        explodeRect[MIDDLE].right = x+dim;
        explodeRect[MIDDLE].bottom = y+dim;

        explodeRect[UP].left = x;
        explodeRect[UP].top = Math.max(y - dim, 0);
        explodeRect[UP].right = x+dim;
        explodeRect[UP].bottom = y;

        explodeRect[RIGHT].left = x+dim;
        explodeRect[RIGHT].top = y;
        explodeRect[RIGHT].right = Math.min(x + 2*dim, TankView.WIDTH);
        explodeRect[RIGHT].bottom = y+dim;

        explodeRect[DOWN].left = x;
        explodeRect[DOWN].top = y+dim;
        explodeRect[DOWN].right = x+dim;
        explodeRect[DOWN].bottom = Math.min(y + 2*dim, TankView.HEIGHT);

        explodeRect[LEFT].left = Math.max(x - dim, 0);
        explodeRect[LEFT].top = y;
        explodeRect[LEFT].right = x;
        explodeRect[LEFT].bottom = y+dim;
//        setPosition(x, y);
    }

    public boolean destroyObj(GameObjects obj) {
        for(Rect b:explodeRect) {
            if(Rect.intersects(b,obj.getRect())){
                Log.d("BOMB COLLISION", "Got Collision");
                return true;
            }
        }
        Log.d("BOMB COLLISION", "Got no Collision");
        return false;
    }

    public boolean collidesWithObject(GameObjects obj) {
        if(super.collides_with(obj) || super.collides_with_wall()) {
            moving = false;
            x = (int)(Math.floor(x/26.0 + 0.5))*26;
            y = (int)(Math.floor(y/26.0 + 0.5))*26;
            moving = false;
            return true;
        }
        return false;
    }

    public boolean isExploding() {
        return explode;
    }

    public boolean isDropped() {
        return dropped;
    }

    public void update() {
        if(moving) {
            switch (dir) {
                case CONST.Direction.UP:
                    y -= v;
                    if(y < 0) {
                        y = 0;
                        moving = false;
                    }
                    break;
                case CONST.Direction.DOWN:
                    y += v;
                    if(y > TankView.HEIGHT - h) {
                        y = TankView.HEIGHT - h;
                        moving = false;
                    }
                    break;
                case CONST.Direction.LEFT:
                    x -= v;
                    if(x < 0) {
                    x = 0;
                    moving = false;
                }
                    break;
                case CONST.Direction.RIGHT:
                    x += v;
                    if(x > TankView.WIDTH - w) {
                        x = TankView.WIDTH - w;
                        moving = false;
                    }
                    break;
            }
            Log.d("BOMB", String.valueOf(x)+" "+y+" "+acc);
            v += acc;
            if(v <= 0){
                moving = false;
            }
        }
    }

    public void draw(Canvas canvas) {
        if(dropped) {
            if (frame_delay <= 0) {
                frame = (frame + 1) % TankView.bombSprite.frame_count;
                frame_delay = TankView.bombSprite.frame_time;
            } else {
                --frame_delay;
            }
            canvas.drawBitmap(TankView.bombBitmap.get(frame), x, y, null);
        }
        else if(explode) {
            Log.d("BOMB", String.valueOf(frame)+" "+ dropped +" "+ explode +" "+ up);
            if(frame < 0) {
                dropped = false;
                explode = false;
                return;
            }

            for(int dir = 0; dir < 5; dir++) {
                canvas.drawBitmap(TankView.fireBitmap[dir][frame], explodeRect[dir].left, explodeRect[dir].top, null);
            }

            if(frame_delay <= 0) {
                if(up) {
                    frame++;
                    if(frame >= TankView.fireSprite.frame_count) {
                        up = false;
                        frame = TankView.fireSprite.frame_count - 1;
                    }
                }
                else{
                    frame--;
                }
                frame_delay = TankView.fireSprite.frame_time;
            }
            else {
                --frame_delay;
            }



//            canvas.drawBitmap(TankView.fireBitmap[MIDDLE][frame], explodeRect[MIDDLE].left, explodeRect[MIDDLE].top, null);
//            canvas.drawBitmap(TankView.fireBitmap[UP][frame], explodeRect[UP].left, explodeRect[UP].top, null);
//            canvas.drawBitmap(TankView.fireBitmap[DOWN][frame], explodeRect[DOWN].left, explodeRect[DOWN].top, null);
//            canvas.drawBitmap(TankView.fireBitmap[RIGHT][frame], explodeRect[RIGHT].left, explodeRect[RIGHT].top, null);
//            canvas.drawBitmap(TankView.fireBitmap[LEFT][frame], explodeRect[LEFT].left, explodeRect[LEFT].top, null);
//            canvas.drawBitmap(TankView.fireBitmap[5][frame], x, y, null);
//            canvas.drawBitmap(TankView.fireBitmap[6][frame], x, y, null);

//            explode = false;
//            setDestroyed();
        }

    }
}
