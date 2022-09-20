package com.gamecentre.tankbattle.tank;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import com.gamecentre.tankbattle.sound.SoundManager;
import com.gamecentre.tankbattle.sound.Sounds;
import com.gamecentre.tankbattle.utils.CONST;

import java.util.ArrayList;

public class Tank extends GameObjects {
    protected float vx,vy;
    protected boolean moving = false;
    protected boolean shooting = false;
//    protected ArrayList<ArrayList<Bitmap>> tankBitmap;
    protected final Bitmap[] dbitmap;
    protected final Bitmap[] spbitmap;
    protected int direction;
    protected final Sprite sprite;
    protected final Sprite dsprite;
    protected final Sprite spsprite;
    protected float DEFAULT_SPEED = TankView.tile_dim*6f/TankView.FPS;
    protected int armour = 0;
    protected int frame;
    protected int frame_delay;
    protected int lives = 3;
    protected int reloadTmr = 0;
    protected ArrayList<Bullet> bullets = new ArrayList<>();
    protected int MaxBullet = 1;
//    protected boolean[] collision = new boolean[4];
    protected Bullet bullet;
    public boolean respawn = true;
    protected final int FreezeTime = (int)(8*TankView.FPS);
    protected final int ShieldTime = (int)(10*TankView.FPS);
    protected final int BoatTime = (int)(3*TankView.TO_SEC);
    protected final int IceTime = (int)(0.5*TankView.FPS);
    protected Shield mShield;
    protected Boat mBoat;
    protected boolean freeze, shield, boat, slip, bBomb;
    protected int freezeTmr, shieldTmr, boatTmr, iceTmr;
    public ObjectType type;
    public int typeVal;
    public int group = 1;
    protected int starCount = 0;
    public int tile_x, tile_y;
    protected boolean collision;
    protected Rect cRct;
    protected final int TileScale = 2;
    public int bId = 0;
    public int player;
    protected Bomb bomb;


    public Tank(ObjectType type, int x, int y, int player) {
        super(x,y);
        this.player = player;
        direction = CONST.Direction.UP;
        this.type = type;

        if(type == ObjectType.ST_TANK_A) {
            typeVal = 0;
        }
        else if(type == ObjectType.ST_TANK_B) {
            typeVal = 1;
        }
        else if(type == ObjectType.ST_TANK_C) {
            typeVal = 2;
        }
        else if(type == ObjectType.ST_TANK_D) {
            typeVal = 3;
        }

        sprite = SpriteObjects.getInstance().getData(type);


        dsprite = SpriteObjects.getInstance().getData(ObjectType.ST_DESTROY_TANK);
        Bitmap dbm = Bitmap.createBitmap(TankView.graphics, dsprite.x, dsprite.y ,dsprite.w,dsprite.h*dsprite.frame_count);
        dbitmap = new Bitmap[dsprite.frame_count];
        for(int i = 0; i < dsprite.frame_count; i++){
            dbitmap[i] = Bitmap.createBitmap(dbm,0,i*dsprite.h, dsprite.w, dsprite.h);
        }



        spsprite = SpriteObjects.getInstance().getData(ObjectType.ST_CREATE);
        Bitmap spbm = Bitmap.createBitmap(TankView.graphics, spsprite.x, spsprite.y ,spsprite.w,spsprite.h*spsprite.frame_count);
        spbitmap = new Bitmap[spsprite.frame_count];
        for(int i = 0; i < spsprite.frame_count; i++){
            spbitmap[i] = Bitmap.createBitmap(spbm,0,i*spsprite.h, spsprite.w, spsprite.h);
        }

        frame_delay = spsprite.frame_time;

        mShield = new Shield(x,y);
        mBoat = new Boat(x,y,this.type==ObjectType.ST_PLAYER_1?1:2);

        super.w = sprite.w;
        super.h = sprite.h;
        tile_x = (int)(w/2);
        tile_y = (int)(h/2);

//        DEFAULT_SPEED = TankView.tile_dim/3f;

        this.vx = DEFAULT_SPEED;
        this.vy = DEFAULT_SPEED;
        bullet = new Bullet(ObjectType.ST_BULLET,0,0,player);


        frame = 0;
        freeze = false;
        slip = false;
        cRct = new Rect();
//        cRct.set(getRect());

//        for(int i=0; i<collision.length; i++) {
//            collision[i] = false;
//        }

        bomb = new Bomb(x,y, player>0);
    }

    public void move() {
        if(moving && !collision){
            getRect();
            if(freeze && freezeTmr > 0) {
                --freezeTmr;
                return;
            }
            switch (direction) {
                case CONST.Direction.UP:
                    if(rect.top <= 1)return;
                    y -= vy;
                    break;
                case CONST.Direction.DOWN:
                    if(rect.bottom >= TankView.HEIGHT)return;
                    y += vy;
                    break;
                case CONST.Direction.LEFT:
                    if(rect.left < 1)return;
                    x -= vx;
                    break;
                case CONST.Direction.RIGHT:
                    if(rect.right > TankView.WIDTH)return;
                    x += vx;
                    break;
            }
        }
    }

    public void move(int dir) {
        Log.d("MOVE", "Original");
        direction = dir;
        moving = true;
    }

    public void stopMoving() {
        moving = false;
    }

    public void stopShooting() {
        shooting = false;
    }

    public void dropBomb() {
        if(bomb.isDropped() || bomb.isExploding()) {
            return;
        }
        bomb.setPosition(x,y);
        bomb.drop(direction);
        SoundManager.playSound(Sounds.TANK.BOMB2);
    }

    public void activateBomb() {
        bomb.activate();
    }

    public void startShooting() {
        shooting = true;
    }

    public int getDirection() {
        return direction;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public void fire() {

        if(!bullets.isEmpty()) {
            return;
        }
        int bx=0,by=0;
        switch (direction) {
            case CONST.Direction.UP:
                bx = x+(int) sprite.w/2;
                by = y;
                break;
            case CONST.Direction.DOWN:
                bx = x+(int) sprite.w/2;
                by = y+ sprite.h;
                break;
            case CONST.Direction.LEFT:
                bx = x;
                by = y+(int) sprite.h/2;
                break;
            case CONST.Direction.RIGHT:
                bx = x+ sprite.w;
                by = y+(int) sprite.h/2;
                break;
        }
        bullet.move(direction);
        bullets.add((new Bullet(bullet,bx,by)));
    }

//    public void setReloadTime(int t) {
//        ReloadTime = t;
//    }

    public void clearBullets() {
        bullets.clear();
    }

    public void loseLife() {
        --lives;
    }

    public boolean isAlive() {
        return lives > 0;
    }

    public void upgrade_armour() {
        armour++;
    }

    public void setDestroyed() {
        super.setDestroyed();
        SoundManager.playSound(Sounds.TANK.EXPLOSION, 1.5f,1);
        stopMoving();
        frame = 0;
        frame_delay = dsprite.frame_time;
        loseLife();
    }

    public void setFreeze() {
        freeze = true;
        freezeTmr = FreezeTime;
    }

    public boolean collidesWithObject(GameObjects targ) {
        rect = getRect();
        Rect tRect = targ.getRect();
        setCollissionRect(direction);
//        if(Rect.intersects(collisionRect,tRect)) {
//            collision = true;
//        }
//        else {
//            collision = false;
//        }
//        return  collision;
        switch (direction) {
            case CONST.Direction.UP:
                if(tRect.contains(cRct.left,cRct.top) || tRect.contains(cRct.right,cRct.top)) {
                    collision = true;
                    return true;
                }
                else{
                    collision = false;
                }
                break;
            case CONST.Direction.DOWN:
                if(tRect.contains(cRct.left,cRct.bottom) || tRect.contains(cRct.right,cRct.bottom)) {
                    collision = true;
                    return true;
                }
                else{
                    collision = false;
                }
                break;
            case CONST.Direction.LEFT:
                if(tRect.contains(cRct.left,cRct.top) || tRect.contains(cRct.left,cRct.bottom)) {
                    collision = true;
                    return true;
                }
                else{
                    collision = false;
                }
                break;
            case CONST.Direction.RIGHT:
                if(tRect.contains(cRct.right,cRct.top) || tRect.contains(cRct.right,cRct.bottom)) {
                    collision = true;
                    return true;
                }
                else{
                    collision = false;
                }
                break;
        }
        return false;
    }

    public int collidsWithBonus(Bonus b) {
        if(super.collides_with(b)) {
            int bonus = b.getBonus();
            switch (bonus) {
                case Bonus.GRENADE:
                    break;
                case Bonus.HELMET:
                    shield = true;
                    shieldTmr = ShieldTime;
                    break;
                case Bonus.CLOCK:
                    break;
                case Bonus.SHOVEL:
                    break;
                case Bonus.TANK:
                    ++lives;
                    break;
                case Bonus.STAR:
                    ++starCount;
                    MaxBullet = (int)(starCount/2) + 1;
                    if(MaxBullet > 4){
                        MaxBullet = 4;
                    }
                    armour++;
                    if(armour > 3){
                        armour = 3;
                    }
                    vx *= 1.25;
                    if(vx > DEFAULT_SPEED*1.5){
                        vx = DEFAULT_SPEED*1.5f;
                    }
                    vy *= 1.25;
                    if(vy > DEFAULT_SPEED*1.5){
                        vy = DEFAULT_SPEED*1.5f;
                    }
                    if(armour >= 3){
                        armour = 3;
                    }
                    break;
                case Bonus.GUN:
                    starCount += 3;
                    MaxBullet = (int)(starCount/2) + 1;
                    if(MaxBullet > 4){
                        MaxBullet = 4;
                    }
                    armour += 2;
                    if(armour >= 3){
                        armour = 3;
                    }
                    break;
                case Bonus.BOAT:
                    boat = true;
                    boatTmr = BoatTime;
                    break;
            }
            b.clearBonus();
            return bonus;
        }
        return -1;
    }


    protected int[] setCollissionRect(int dir) {
        getRect();
        int rl,rt,rr,rb;
        switch (dir) {
            case CONST.Direction.UP:
                rl = rect.left+2;
                rt = rect.top-6;
                rr = rect.right-2;
                rb = rect.top-2;
                break;
            case CONST.Direction.DOWN:
                rl = rect.left+2;
                rt = rect.bottom+2;
                rr = rect.right-2;
                rb = rect.bottom+6;
                break;
            case CONST.Direction.LEFT:
                rl = rect.left-6;
                rt = rect.top+2;
                rr = rect.left-2;
                rb = rect.bottom-2;
                break;
            case CONST.Direction.RIGHT:
                rl = rect.right+2;
                rt = rect.top+2;
                rr = rect.right+6;
                rb = rect.bottom-2;
                break;
            default:
                rl = 0;rt=0;rr=0;rb=0;
        }

        cRct.set(rl,rt,rr,rb);
        return new int[]{rl,rt,rr,rb};

    }

    public boolean collidsWithBullet(Bullet bullet) {
        if(isDestroyed()) {
            return false;
        }
        if(super.collides_with(bullet)) {
            if(shield) {
                bullet.setDestroyed();
                return true;
            }
            if(boat) {
                boat = false;
                bullet.setDestroyed();
                return true;
            }
            if(armour >= 3) {
                armour = 2;
                bullet.setDestroyed();
                return true;
            }
            setDestroyed();
            bullet.setDestroyed();
            return true;
        }
        return false;
    }

    public ArrayList<Bullet> getBullets() {
        return bullets;
    }

    public void setBullets(ArrayList<Bullet> b) {
        bullets = b;
    }

    public void respawn() {
        respawn = true;
        destroyed = false;
        direction = CONST.Direction.UP;
        frame = 0;
    }

    public void update() {
//        if(boat && boatTmr > 0){
//            --boatTmr;
//        }

        if(shield && shieldTmr > 0){
            --shieldTmr;
        }

//        if(respawn) {
//            destroyed = false;
//            direction = CONST.Direction.UP;
//            frame = 0;
//            respawn = false;
//        }

        for(int i = 0; i < bullets.size(); i++) {
            if(bullets.get(i).recycle) {
                bullets.set(i,null);
            }
        }

        // Remove all recycled bullets
        while(bullets.remove(null));

        move();
        for(Bullet bullet:bullets) {
            bullet.move();
        }
    }

    public void draw(Canvas canvas) {
//        Bitmap bm;
//        int bx, by, bw, bh;
//        if(!destroyed) {
//            bx = direction * sprite.w;
//            by = sprite.frame_count * armour * sprite.h;
//            by = by + frame*sprite.h;
//            bw = sprite.w;
//            bh = sprite.h;
//            if(frame_delay <= 0) {
//                frame = (frame + 1) % sprite.frame_count;
//                frame_delay = sprite.frame_time;
//            }
//            else{
//                --frame_delay;
//            }
//            bm = Bitmap.createBitmap(bitmap,bx,by,bw,bh);
//            canvas.drawBitmap(bm,x,y,null);
//
////            if(boat && boatTmr > 0) {
//            if(boat) {
//                mBoat.setPosition(x,y);
//                mBoat.draw(canvas);
//            }
//
//            if(shield && shieldTmr > 0) {
//                mShield.setPosition(x,y);
//                mShield.draw(canvas);
//            }
//        }
//        else {
//            if (frame < dsprite.frame_count) {
//                bx = 0;
//                by = frame * dsprite.h;
//                bw = dsprite.w;
//                bh = dsprite.h;
//                if(frame_delay <= 0) {
//                    frame = frame + 1;
//                    frame_delay = dsprite.frame_time;
//                }
//                else{
//                    --frame_delay;
//                }
//                bm = Bitmap.createBitmap(dbitmap, bx, by, bw, bh);
//                canvas.drawBitmap(bm, x - (int) (w / 2), y - (int) (h / 2), null);
//            } else if (frame == dsprite.frame_count) {
//                respawn();
//            }
//        }
//
//        for(Bullet bullet:bullets) {
//            if(bullet != null) {
//                bullet.draw(canvas);
//            }
//        }
    }

}
