package com.gamecentre.tankbattle.tank;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import com.gamecentre.tankbattle.R;
import com.gamecentre.tankbattle.sound.SoundManager;
import com.gamecentre.tankbattle.sound.Sounds;
import com.gamecentre.tankbattle.utils.CONST;

public class Bullet extends GameObjects {

    private float vx,vy;
    private final Bitmap[] bitmap;
    private final Bitmap[] dbitmap;
    private int direction;
    private final Sprite sprite;
    private final Sprite dsprite;
    private int frame;
    private int frame_delay;
    private final float DEFAULT_SPEED = TankView.tile_dim*15f/TankView.FPS;
    private int fromPlayer;
    public boolean explode = true;
    private float speed = 1;
    public int id = 0;
    public boolean sent = false;
    public boolean launched;

    public Bullet(ObjectType type, int x, int y, int fromPlayer) {
        super(x,y);
        this.fromPlayer = fromPlayer;
        direction = CONST.Direction.UP;
        sprite = SpriteObjects.getInstance().getData(type);
        Bitmap bm = Bitmap.createBitmap(TankView.graphics, sprite.x, sprite.y ,sprite.w*4,sprite.h);
        bitmap = new Bitmap[4];
        for(int i = 0; i < 4; i++){
            bitmap[i] = Bitmap.createBitmap(bm,i*sprite.w,0, sprite.w, sprite.h);
        }

        dsprite = SpriteObjects.getInstance().getData(ObjectType.ST_DESTROY_BULLET);
        Bitmap dbm = Bitmap.createBitmap(TankView.graphics, dsprite.x, dsprite.y ,dsprite.w,dsprite.h*dsprite.frame_count);
        dbitmap = new Bitmap[dsprite.frame_count];
        for(int i = 0; i < dsprite.frame_count; i++){
            dbitmap[i] = Bitmap.createBitmap(dbm,0,i*dsprite.h, dsprite.w, dsprite.h);
        }
//        DEFAULT_SPEED = TankView.tile_dim*1.5f;
        this.vx = DEFAULT_SPEED;
        this.vy = DEFAULT_SPEED;
        super.w = sprite.w;
        super.h = sprite.h;
        frame_delay = dsprite.frame_time;
        frame = 0;
        launched = true;
    }

    public Bullet(Bullet bullet,int x, int y) {
        super(x,y);
        id = bullet.id;
        this.fromPlayer = bullet.fromPlayer();
        this.bitmap = bullet.getBitmap();
        this.sprite = SpriteObjects.getInstance().getData(ObjectType.ST_BULLET);

        this.dbitmap = bullet.getDBitmap();
        this.dsprite = SpriteObjects.getInstance().getData(ObjectType.ST_DESTROY_BULLET);

        this.x = x;
        this.y = y;
//        DEFAULT_SPEED = TankView.tile_dim*1.5f;
        this.vx = (DEFAULT_SPEED*bullet.getSpeed());
        this.vy = (DEFAULT_SPEED*bullet.getSpeed());
        this.direction = bullet.getDirection();
        super.w = sprite.w;
        super.h = sprite.h;
        frame_delay = dsprite.frame_time;
        destroyed = bullet.isDestroyed();
        launched = true;
    }

    public Bitmap[] getBitmap() {
        return bitmap;
    }

    public Bitmap[] getDBitmap() {
        return dbitmap;
    }

    public void setXY(int x,int y) {
        this.x = x;
        this.y = y;
    }

    public int getDirection() {
        return direction;
    }

    public void setSpeed(float speed) {
        vx = (DEFAULT_SPEED*speed);
        vy = (DEFAULT_SPEED*speed);

        Log.d("SPEED", String.valueOf(DEFAULT_SPEED)+" "+vx);
        this.speed = speed;
    }

    public float getSpeed() {
        return speed;
    }

    public int fromPlayer() {
        return fromPlayer;
    }

    public void setPlayer(int player) {
        fromPlayer = player;
    }

    protected boolean collides_with(GameObjects targ) {
        rect = getRect();
        Rect r = new Rect();
        r.left = rect.left;
        r.right = rect.right;
        r.top = rect.top;
        r.bottom = rect.bottom;
//        if(!(targ instanceof Enemy)) {
            switch (direction) {
                case CONST.Direction.UP:
                    r.left -= TankView.tile_dim/2;
                    r.right += TankView.tile_dim/2;
//                    r.bottom += TankView.tile_dim/2;
                    break;
                case CONST.Direction.DOWN:
                    r.left -= TankView.tile_dim/2;
                    r.right += TankView.tile_dim/2;
//                    r.top -= TankView.tile_dim/2;
                    break;
                case CONST.Direction.LEFT:
                    r.bottom += TankView.tile_dim/2;
                    r.top -= TankView.tile_dim/2;
//                    r.right += TankView.tile_dim/2;
                    break;
                case CONST.Direction.RIGHT:
                    r.bottom += TankView.tile_dim/2;
                    r.top -= TankView.tile_dim/2;
//                    r.left -= TankView.tile_dim/2;
                    break;
            }

        return Rect.intersects(r,targ.getRect());
    }

    public void move() {
        if(!destroyed && !recycle) {
            if(collides_with_wall()){
                if(fromPlayer > 0) {
                    SoundManager.playSound(Sounds.TANK.STEEL, 1, 1);
                }
                setDestroyed();
                return;
            }
            switch (direction) {
                case CONST.Direction.UP:
                    y -= vy;
                    if(y < 0) {
                        y = 0;
                        if(fromPlayer > 0) {
                            TankView.getInstance().currentObj[9] = false;
                        }
                    }
                    break;
                case CONST.Direction.DOWN:
                    y += vy;
                    if(y > TankView.HEIGHT) {
                        y = TankView.HEIGHT;
                        if(fromPlayer > 0) {
                            TankView.getInstance().currentObj[9] = false;
                        }
                    }
                    break;
                case CONST.Direction.LEFT:
                    x -= vx;
                    if(x < 0) {
                        x = 0;
                        if(fromPlayer > 0) {
                            TankView.getInstance().currentObj[9] = false;
                        }
                    }
                    break;
                case CONST.Direction.RIGHT:
                    x += vx;
                    if(x > TankView.WIDTH) {
                        x = TankView.WIDTH;
                        if(fromPlayer > 0) {
                            TankView.getInstance().currentObj[9] = false;
                        }
                    }
                    break;
            }
        }
    }

    public void move(int dir) {
        direction = dir;
    }


    public void setDestroyed() {
        if(isDestroyed()) {
            return;
        }
        super.setDestroyed();
//        svrKill = TankView.twoPlayers && WifiDirectManager.getInstance().isServer();
        svrKill = TankView.twoPlayers && fromPlayer == 1;
        frame = 0;
        frame_delay = dsprite.frame_time;
        explode = true;
    }

    public void setDestroyed(boolean explode) {
        setDestroyed();
//        super.recycle();
        this.explode = explode;
    }

    public void draw(Canvas canvas) {
        if(!destroyed){
            canvas.drawBitmap(bitmap[direction], x - (float) sprite.w / 2, y - (float) sprite.h / 2, null);
        }
        else if(!recycle){
            if(!this.explode) {
                super.recycle();
                return;
            }
            if (frame < dsprite.frame_count) {
                canvas.drawBitmap(dbitmap[frame], x - (int) (dsprite.w / 2), y - (int) (dsprite.h / 2), null);
                if(frame_delay <= 0) {
                    frame = frame + 1;
                    frame_delay = 0;//dsprite.frame_time;
                }
                else{
                    --frame_delay;
                }
            } else if (frame == dsprite.frame_count) {
                super.recycle();
            }
        }
    }
}
