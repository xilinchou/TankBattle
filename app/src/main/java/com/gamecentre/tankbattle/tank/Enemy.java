package com.gamecentre.tankbattle.tank;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;

import com.gamecentre.tankbattle.model.MTank;
import com.gamecentre.tankbattle.sound.SoundManager;
import com.gamecentre.tankbattle.sound.Sounds;
import com.gamecentre.tankbattle.utils.CONST;
import com.gamecentre.tankbattle.wifidirect.WifiDirectManager;

import java.util.ArrayList;

public class Enemy extends Tank{

    public int sx,sy;
    protected Point target;
    protected int change_dir_time;
    protected int dir_time;
    protected boolean freeze = false, shield, boat;
    protected   int freezeTmr, shieldTmr, boatTmr;
    private static int MaxLives = 20;
    public static int lives = MaxLives;
    protected float bulletSpeed = 1;
    protected int reloadTmr = (int)(0.2*TankView.TO_SEC);
    protected int reload_time = 0;
    public boolean hasBonus = false;
    private boolean breakWall = false;
    protected int lifeFrame = 0;
    protected String killScore;
    protected boolean killed = false;
    public  int id;
    private static int nxtId = 0;
    private ArrayList<Bullet> dbullets;
    int bombID = -1;
    protected boolean hve = false;



    public Enemy(ObjectType type, int x, int y) {
        super(type, x, y, 0);
        nxtId++;
        id = nxtId;
        target = new Point();
        target.x = TankView.WIDTH/2;
        target.y = TankView.HEIGHT/2;

        dir_time = 0;
        change_dir_time = (int)(Math.random()*30 + 10);
        direction = CONST.Direction.DOWN;
        if(type == ObjectType.ST_TANK_B) {
            this.vx = DEFAULT_SPEED*1.1f;
            this.vy = DEFAULT_SPEED*1.1f;
            killScore = "200";
        }

        if(type == ObjectType.ST_TANK_A) {
            this.vx = DEFAULT_SPEED*0.6f;
            this.vy = DEFAULT_SPEED*0.6f;
            killScore = "100";
        }

        if(type == ObjectType.ST_TANK_D) {
            killScore = "400";
        }

        if(type == ObjectType.ST_TANK_C) {
            bulletSpeed = 1.15f;
            reloadTmr = (int)(1*TankView.FPS);
            killScore = "300";
        }
        else {
            reloadTmr = (int)(1.5*TankView.FPS);
        }

        frame = 0;
        dbullets = new ArrayList<>();
//        moving = true;
    }

    public Enemy(ObjectType type, int group, int x, int y) {
        this(type, x, y);
        this.group = group;
    }

    public int getKillScore() {
        return Integer.parseInt(killScore);
    }

    public boolean isHVE() {
        return hve;
    }

    public void setTarget(Point targ) {
        target = targ;
    }

    public void respawn() {
        respawn = true;
        destroyed = false;
        direction = CONST.Direction.DOWN;
        frame = 0;
    }

//    public void setFreeze() {
//        freeze = true;
//        freezeTmr = FreezeTime;
//    }

    public static void freeze() {
        TankView.freeze = true;
        TankView.freezeTmr = TankView.FreezeTime;
    }

    public void changeDirection() {
        if(TankView.freeze) {
            return;
        }
        if(dir_time >= change_dir_time) {
            dir_time = 0;
            change_dir_time = (int)(Math.random()*30 + 10);
            int new_direction;

            float d = (float)Math.random();
            if(d < (type == ObjectType.ST_TANK_A ? 0.8 : prob(0.2f,0.9f)) && target.x > 0 && target.y > 0) {
                int dx = (int)(target.x - x);
                int dy = (int)(target.y - y);

                d = (float)Math.random();

                if(Math.abs(dx) > Math.abs(dy))
                    new_direction = (d < prob(0.2f,0.9f)) ? (dx < 0 ? CONST.Direction.LEFT : CONST.Direction.RIGHT) : (dy < 0 ? CONST.Direction.UP : CONST.Direction.DOWN);
                else
                    new_direction = (d < prob(0.2f,0.9f)) ? (dy < 0 ? CONST.Direction.UP : CONST.Direction.DOWN) : (dx < 0 ? CONST.Direction.LEFT : CONST.Direction.RIGHT);
            }
            else {
                new_direction = (int)(Math.random()*4)%4;
            }

            if(new_direction != direction) {
                direction = new_direction;

                int px_tile = (int)((x/tile_x)*tile_x);
                int py_tile = (int)((y/tile_y)*tile_y);

                if(x-px_tile < tile_x/TileScale) x = px_tile;
                else if(px_tile + tile_x - x < tile_x/TileScale) x = px_tile+tile_x;

                if(y-py_tile < tile_y/TileScale) y = py_tile;
                else if(py_tile + tile_y - y < tile_y/TileScale) y = py_tile+tile_y;
            }

            setStopPoint(change_dir_time, direction);
        }
        else {
            dir_time++;
        }
    }

    protected void setStopPoint(int move_time, int direction) {

        switch (direction) {
            case CONST.Direction.UP:
                sy = (int)(y-vy*move_time);
                if(sy < 0){
                    sy = 0;
                }
                break;
            case CONST.Direction.DOWN:
                sy = (int)(y+vy*move_time);
                if(sy >= TankView.HEIGHT - h){
                    sy = TankView.HEIGHT - h;
                }
                break;
            case CONST.Direction.LEFT:
                sx = (int)(x-vx*move_time);
                if(sx < 0) {
                    sx = 0;
                }
                break;
            case CONST.Direction.RIGHT:
                sx = (int)(x+vx*move_time);
                if(sx > TankView.WIDTH - w) {
                    sx = TankView.WIDTH - w;
                }
                break;
        }
    }

    protected float prob(float min, float max) {
        return (max-min)*TankView.level/TankView.NUM_LEVELS + min;
    }

    public int getDirection() {
        return direction;
    }

    public void move() {
        if(respawn || destroyed) {
            return;
        }
        if(TankView.freeze) {
            return;
        }
        getRect();

        switch (direction) {
            case CONST.Direction.UP:
                if(collision || y <= 0 || y <= sy)return;
                y -= vy;
                if(y < 0){
                    y = 0;
                }
                break;
            case CONST.Direction.DOWN:
                if(collision || y >= TankView.HEIGHT - h || y >= sy)return;
                y += vy;
                if(y >= TankView.HEIGHT - h){
                    y = TankView.HEIGHT - h;
                }
                break;
            case CONST.Direction.LEFT:
                if(collision || x <= 0 || x <= sx)return;
                x -= vx;
                if(x < 0) {
                    x = 0;
                }
                break;
            case CONST.Direction.RIGHT:
                if(collision || x >= TankView.WIDTH - w || x >= sx)return;
                x += vx;
                if(x > TankView.WIDTH - w) {
                    x = TankView.WIDTH - w;
                }
                break;
        }
    }

    public void moveToStopPoint() {
        if(respawn || destroyed) {
            return;
        }
        if(TankView.freeze) {
            return;
        }
        getRect();

        switch (direction) {
            case CONST.Direction.UP:
                if(collision || y <= 0)return;
                y -= vy;
                if(y < 0){
                    y = 0;
                }
                break;
            case CONST.Direction.DOWN:
                if(collision || y >= TankView.HEIGHT - h)return;
                y += vy;
                if(y >= TankView.HEIGHT - h){
                    y = TankView.HEIGHT - h;
                }
                break;
            case CONST.Direction.LEFT:
                if(collision || x <= 0)return;
                x -= vx;
                if(x < 0) {
                    x = 0;
                }
                break;
            case CONST.Direction.RIGHT:
                if(collision || x >= TankView.WIDTH - w)return;
                x += vx;
                if(x > TankView.WIDTH - w) {
                    x = TankView.WIDTH - w;
                }
                break;
        }
    }

    public void fire() {


        if(reload_time > 0 || TankView.freeze || bullets.size() == MaxBullet || isDestroyed() || respawn) {
            return;
        }
        int bx=0,by=0;
        switch (direction) {
            case CONST.Direction.UP:
                bx = x+(int) sprite.w/2;
                by = y;
                by = (int) ((by / tile_y) * tile_y) + tile_y;
                break;
            case CONST.Direction.DOWN:
                bx = x+(int) sprite.w/2;
                by = y+ sprite.h;
                by = (int) ((by / tile_y) * tile_y) - tile_y;
                break;
            case CONST.Direction.LEFT:
                bx = x;
                bx = (int) ((bx / tile_x) * tile_x) + tile_x;
                by = y+(int) sprite.h/2;
                break;
            case CONST.Direction.RIGHT:
                bx = x+ sprite.w;
                bx = (int) ((bx / tile_x) * tile_x) - tile_x;
                by = y+(int) sprite.h/2;
                break;
        }
        bId++;
        bullet.move(direction);
        bullet.setSpeed(bulletSpeed);
        bullet.setPlayer(this.player);
        bullet.id = bId;
        bullets.add((new Bullet(bullet,bx,by)));
//        SoundManager.playSound(Sounds.TANK.FIRE);
        reload_time = (int)((0.4*TankView.FPS) + Math.random()*reloadTmr);
    }

    public boolean hasBoat () {
        return boat;
    }

    public void setBoat(boolean boat) {
        this.boat = boat;
    }


    public void applyShield() {
        group += 2;
        if(group > 4) {
            group = 4;
        }
    }

    public void applyBoat() {
        boat = true;
        boatTmr = BoatTime;
    }

    public void applyTank() {
        hasBonus = true;
    }

    public void applyGun() {
        breakWall = true;
        group = 4;
    }

    public void applyStar() {
        group += 1;
        if(group > 4) {
            group = 4;
        }
    }

    public int collidsWithBonus(Bonus b) {
        if(b.isAvailable() && super.collides_with(b) && TankView.ENEMY_BOOST) {
//            stageScore += 500;
//            SoundManager.playSound(Sounds.TANK.BONUS, 1, 3);
            int bonus = b.getBonus();
//            gotBonus = bonus;
            switch (bonus) {
                case Bonus.GRENADE:
                    TankView.getInstance().currentObj[8] = false;
                    break;
                case Bonus.HELMET:
                    applyShield();
                    break;
                case Bonus.CLOCK:
                    break;
                case Bonus.SHOVEL:
                    break;
                case Bonus.TANK:
                    applyTank();
                    break;
                case Bonus.STAR:
                    applyStar();
                    break;
                case Bonus.GUN:
                    applyGun();
                    break;
                case Bonus.BOAT:
                    applyBoat();
                    break;
            }
            b.clearBonus();
            return bonus;
        }
        return -1;
    }




    public boolean collidesWithObject(GameObjects targ) {
        rect = getRect();
        Rect tRect = targ.getRect();
        setCollissionRect(direction);
        if(Rect.intersects(cRct,tRect)) {
            collision = true;
        }
        else {
            collision = false;
        }
        return  collision;
    }


    public boolean collidsWithBullet(Bullet bullet) {
        if(isDestroyed() || respawn) {
            return false;
        }
        if(super.collides_with(bullet)) {
            if(boat && TankView.ENEMY_BOOST) {
                boat = false;
                bullet.setDestroyed(false);
                return true;
            }
            if(hasBonus) {
                TankView.bonus.setBonus();
            }
            bullet.setDestroyed();
            if(group == 1) {
                killed = true;
//                svrKill = TankView.twoPlayers && WifiDirectManager.getInstance().isServer();
                svrKill = TankView.twoPlayers && bullet.fromPlayer() == 1;
                setDestroyed();
                return true;
            }
            else {
                --group;
                SoundManager.playSound(Sounds.TANK.STEEL);
                return false;
            }
        }
        return false;
    }

    /**
     * Registers the bomb to ensures that the explosion kills enemy only
     * the first time
     * @param id - id of the bomb
     * @return returns true if this is the first encounter with the explosion, false otherwise
     */
    public boolean collideBomb(int id) {
        if(id == bombID){
            return false;
        }
        bombID = id;
        svrKill = TankView.twoPlayers && WifiDirectManager.getInstance().isServer();
        setDestroyed();
        return true;
    }

    public boolean canBreakWall() {
        return TankView.ENEMY_BOOST && breakWall;
    }

    public boolean canClearBush() {
        return  false;
    }

    public void setDestroyed() {
        super.setDestroyed();
        svrKill = TankView.twoPlayers && WifiDirectManager.getInstance().isServer();
    }


    public void update(boolean remote) {
        if(!remote) {
            if (reload_time > 0) {
                --reload_time;
            }
        }

        for(int i = 0; i < bullets.size(); i++) {
            if(bullets.get(i).recycle) {
                bullets.set(i,null);
            }
        }

        // Remove all recycled bullets
        while(bullets.remove(null));

        move();
        if(!remote) {
            fire();
        }

        for(Bullet bullet:bullets) {
            bullet.move();
        }
    }


    public void setModel(MTank model, float scale, boolean server) {
        if(!server){
            if(this.direction != model.dirction) {
                this.direction = model.dirction;
                this.x = (int) (model.x * scale);
                this.y = (int) (model.y * scale);
            }

            this.sx = (int) (model.sx * scale);
            this.sy = (int) (model.sy * scale);

            this.setBoat(model.boat);
            lives = model.lives;
            if(model.group < this.group){
                this.group = model.group;
            }
            this.typeVal = model.typeVal;
            switch (typeVal) {
                case 0:
                    type = ObjectType.ST_TANK_A;
                    break;
                case 1:
                    type = ObjectType.ST_TANK_B;
                    break;
                case 2:
                    type = ObjectType.ST_TANK_C;
                    break;
                case 3:
                    type = ObjectType.ST_TANK_D;
                    break;
            }
            this.respawn = model.respawn;
            this.id = model.id;
            this.hasBonus = model.hasBonus;

            if (model.tDestroyed && !destroyed && model.svrKill) {
                setDestroyed();
                svrKill = model.svrKill;
            }

            for (int i = 0; i < bullets.size(); i++) {
//                if (!(bullets.get(i).isDestroyed()) || bullets.get(i).recycle) {
//                    bullets.set(i, null);   // remove bullets not destroyed and bullets ready for recycling
//                }
                if (bullets.get(i).recycle) {
                    bullets.set(i, null);   // remove bullets ready for recycling
                }
            }

            while (bullets.remove(null)) ;

            for (int i = 0; i < model.bullets.size(); i++) {
                boolean found = false;
                for (int j = 0; j < bullets.size(); j++) {
                    if (model.bullets.get(i)[4] == bullets.get(j).id) {
                        found = true;
                        if (model.bullets.get(i)[3] == 1 && !bullets.get(j).isDestroyed()) {     // bullet should be destroyed now
                            bullets.get(j).x = (int) (model.bullets.get(i)[0] * scale);
                            bullets.get(j).y = (int) (model.bullets.get(i)[1] * scale);
                            bullets.get(j).setDestroyed();
                            bullets.get(j).svrKill = model.bullets.get(i)[5] == 1;  // which player caused the bullet destruction
                        }
                        break;
                    }
                }
                if (!found && model.bullets.get(i)[7] == 1) {                               // bullet has not previously been destroyed
//                    if(Math.abs(model.bullets.get(i)[0] * scale - x) > 10*vx || Math.abs(model.bullets.get(i)[1] * scale - x) > 10*vy) {
//                        break;
//                    }
                    if (model.bullets.get(i)[3] == 1) {     // bullet should be destroyed now
                        bullet.setDestroyed();
                        bullet.svrKill = model.bullets.get(i)[5] == 1;  // which player caused the bullet destruction
                    } else {                                // bullet should not be destroyed now
                        bullet.destroyed = false;
                    }


                    bullet.id = model.bullets.get(i)[4];    // bullet id
                    bullet.move(model.bullets.get(i)[2]);   // bullet direction
                    bullet.setPlayer(0);

                    bullets.add((new Bullet(bullet, (int) (model.bullets.get(i)[0] * scale), (int) (model.bullets.get(i)[1] * scale))));
                    bullet.destroyed = false;
                }
            }

            ((TankActivity)TankView.context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TankView.setEnemyCountView();

                }
            });

        }
        else {
            if(model.group < group){
                group = model.group;
            }

            this.setBoat(model.boat);

            for (int i = 0; i < model.bullets.size(); i++) {
                for (int j = 0; j < bullets.size(); j++) {
                    if (model.bullets.get(i)[4] == bullets.get(j).id) {
                        if (model.bullets.get(i)[3] == 1 && !bullets.get(j).isDestroyed()) {     // bullet should be destroyed now
                            bullets.get(j).x = (int) (model.bullets.get(i)[0] * scale);
                            bullets.get(j).y = (int) (model.bullets.get(i)[1] * scale);
                            bullets.get(j).setDestroyed(model.bullets.get(i)[6] == 1);
                            bullets.get(j).svrKill = model.bullets.get(i)[5] == 1;  // which player caused the bullet destruction
                        }
                        break;
                    }
                }
            }

            if (model.tDestroyed && !destroyed && !model.svrKill) {
                setDestroyed();
                svrKill = model.svrKill;
            }
        }
    }


    public void draw(Canvas canvas) {
        if(respawn) {
            if(frame >= spsprite.frame_count) {
                respawn = false;
                frame = 0;
                return;
            }
            canvas.drawBitmap(spbitmap[frame],x,y,null);
            if(frame_delay <= 0) {
                frame++;
                frame_delay = spsprite.frame_time;
            }
            else{
                --frame_delay;
            }
        }
        else if(!destroyed) {
            if(hasBonus) {
                lifeFrame = (lifeFrame + 1)%3;
            }
            else{
                lifeFrame = 0;
            }
            frame %= sprite.frame_count;
//            Log.d("DRAWE", sprite.frame_count+" "+typeVal+" "+frame);
            canvas.drawBitmap(TankView.tankBitmap.get(sprite.frame_count * typeVal + frame).get(4*(lifeFrame>0?0:group)+direction),x,y,null);
            if(frame_delay <= 0) {
                frame = (frame + 1) % sprite.frame_count;
                frame_delay = sprite.frame_time;
            }
            else{
                --frame_delay;
            }

//            if(boat && boatTmr > 0) {
            if(boat) {
                mBoat.setPosition(x,y);
                mBoat.draw(canvas);
            }

            if(shield && shieldTmr > 0) {
                mShield.setPosition(x,y);
                mShield.draw(canvas);
            }
        }
        else if(!recycle) {
            if (frame < dsprite.frame_count) {
                canvas.drawBitmap(dbitmap[frame], x - (int) (w / 2), y - (int) (h / 2), null);
                if(killed){
                    drawText(canvas,killScore);
                }
                if(frame_delay <= 0) {
                    frame = frame + 1;
                    frame_delay = dsprite.frame_time;
                }
                else{
                    --frame_delay;
                }
            } else if (frame == dsprite.frame_count) {
                killed = false;
                super.recycle();
//                respawn();
            }
        }

        for(Bullet bullet:bullets) {
            if(bullet != null) {
                bullet.draw(canvas);
            }
        }
    }
}
