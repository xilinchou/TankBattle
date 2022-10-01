package com.gamecentre.tankbattle.tank;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import com.gamecentre.tankbattle.sound.SoundManager;
import com.gamecentre.tankbattle.sound.Sounds;
import com.gamecentre.tankbattle.utils.CONST;
import com.gamecentre.tankbattle.wifidirect.WifiDirectManager;

import java.util.ArrayList;

public class HVE extends Enemy{

    private  int frame_time = 7;
    private final int MAX_LIFE = 20;
    private int boostShield = MAX_LIFE;
    private ArrayList<Point> hView1, hView2;
    private boolean spawned = false;
    int view_frame_delay;
    int view_frame_time = 8;
    int view_frame;
    Paint vPaint;
    private static boolean viewing = false;
    private boolean gotTarget = false;
    private boolean bombed = false;
    int dframe = 0;
    int dframe_delay;
    static boolean IS_AVAILABLE = false;


    public HVE(int x, int y, float v, float vb) {
        super(ObjectType.ST_TANK_D, 1, x, y );
        hve = true;
        this.vx = DEFAULT_SPEED*v;//1.1f;
        this.vy = DEFAULT_SPEED*v;//1.1f;
        bulletSpeed = vb;//1.4f;
        MaxBullet = 2;
        reloadTmr = (int)(0.5*TankView.FPS);
        view_frame_delay = view_frame_time;
        view_frame = 0;
        vPaint = new Paint();
        vPaint.setColor(Color.GREEN);
        killScore = "600";

        if(!IS_AVAILABLE){
            SoundManager.stopSound(TankView.SCENE_SOUND);
            TankView.SCENE_SOUND = Sounds.TANK.HVE_SOUND;
            IS_AVAILABLE = true;
        }
        SoundManager.playSound(TankView.SCENE_SOUND,true);

    }

    public boolean hasTarget() {
        return gotTarget;
    }

    public void changeDirection() {
        if(TankView.freeze) {
            return;
        }
        /***
         * get target x,y
         *
         * get all x where there's possible movement in y towards target
         *
         * select x with the closest having the highest possibility for selection
         *
         * increase fire rate if aligned with target
         */
        if(dir_time >= change_dir_time) {
            dir_time = 0;
            change_dir_time = (int)(Math.random()*30 + 10);
            int new_direction;

            float d = (float)Math.random();
            if(d < 0.9 && target.x > 0 && target.y > 0) {
                int dx = (int)(target.x - x);
                int dy = (int)(target.y - y);

                d = (float)Math.random();

                if(Math.abs(dx) > Math.abs(dy)) {
                    new_direction = (d < 0.8) ? (dx < 0 ? CONST.Direction.LEFT : CONST.Direction.RIGHT) : (dy < 0 ? CONST.Direction.UP : CONST.Direction.DOWN);
                }
                else {
                    new_direction = (d < 0.8) ? (dy < 0 ? CONST.Direction.UP : CONST.Direction.DOWN) : (dx < 0 ? CONST.Direction.LEFT : CONST.Direction.RIGHT);
                }
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

//    public void setDestroyed() {
//        super.setDestroyed();
//        dframe = 0;
//        dframe_delay = dsprite.frame_time;
//    }

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
            if(--boostShield <= 0) {
                killed = true;
//                svrKill = TankView.twoPlayers && WifiDirectManager.getInstance().isServer();
                svrKill = TankView.twoPlayers && bullet.fromPlayer() == 1;
                setDestroyed();
                return true;
            }
            else {
//                --boostShield;
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
//        boostShield -= 5;
        reduceShiled(5);
        if(boostShield <= 0) {
            svrKill = TankView.twoPlayers && WifiDirectManager.getInstance().isServer();
            setDestroyed();
            return true;
        }
        return false;
    }

    public int getShiled() {
        return boostShield;
    }

    public int reduceShiled(int amount) {
        boostShield -= amount;
        if(boostShield > 0) {
            bombed = true;
            dframe = 0;
            dframe_delay = dsprite.frame_time;
            SoundManager.playSound(Sounds.TANK.EXPLOSION, 1.5f,4);
        }
        return boostShield;
    }

    public ArrayList<Point> getPlayerView(Player p) {
        ArrayList<Point> hView = new ArrayList<>();
        int px, py;
        int D = (int)Math.sqrt(Math.pow(p.y - y,2) + Math.pow(p.x - x,2));
        double angle = Math.atan2(p.y - y, p.x - x);
        double cosAngle = Math.cos(angle);
        double sinAngle = Math.sin(angle);
        int d = tile_x;
        while (d<D) {
            py = (int)(d*sinAngle + y+h/2);
            px = (int)(d*cosAngle + x+w/2f);
            hView.add(new Point(px,py));
            d += tile_x;
        }
        return hView;
    }

    public void getView(Player p) {
        hView1 = getPlayerView(p);
        gotTarget = true;
        viewing = true;
    }

    public void getView(Player p1, Player p2) {
        hView1 = getPlayerView(p1);
        hView2 = getPlayerView(p2);
        gotTarget = true;
        viewing = true;
    }

    public static boolean isViewing() {
        return viewing;
    }

    public void draw(Canvas canvas) {
        if(respawn) {
            if(frame >= spsprite.frame_count) {
                respawn = false;
                spawned = true;
                viewing = true;
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

            frame %= sprite.frame_count;
            int l = (int)(((float)boostShield/MAX_LIFE)*w);
            canvas.drawLine(x,y-(h/8),x+l, y-h/8,vPaint);
            canvas.drawBitmap(TankView.tankBitmap.get(sprite.frame_count * typeVal + frame).get(4*lifeFrame+direction),x,y,null);
            if(frame_delay <= 0) {
                int mod = (int)Math.ceil(boostShield/4);
                frame = (frame + 1) % sprite.frame_count;
                frame_delay = frame_time;//sprite.frame_time;
                lifeFrame = (lifeFrame + 1) % 5;
//                if(mod <= 1) {
//                    lifeFrame = 1;
//                }
//                else {
//                    lifeFrame = (lifeFrame + 1) % mod;
//                }
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

            if(spawned) {
                int len = 0;
                if(TankView.twoPlayers) {
                    len = Math.max(hView1.size(),hView2.size());
                }
                else {
                    len = hView1.size();
                }
                for(int i = 0; i < len; i++) {
                    if(TankView.twoPlayers) {
                        if(i < hView1.size()) {
                            canvas.drawCircle(hView1.get(i).x, hView1.get(i).y, 2, vPaint);
                        }
                        if(i < hView2.size()) {
                            canvas.drawCircle(hView2.get(i).x, hView2.get(i).y, 2, vPaint);
                        }
                    }
                    else {
                        canvas.drawCircle(hView1.get(i).x, hView1.get(i).y, 2, vPaint);
                    }
                    if(i == view_frame) {
                        break;
                    }
                }
                if(view_frame_delay <= 0){
                    view_frame_delay = view_frame_time;
                    view_frame++;
                }
                else {
                    --view_frame_delay;
                }
                if(view_frame >= len) {
                    spawned = false;
                    viewing = false;
                }
            }

            if(bombed) {
                if (dframe < dsprite.frame_count) {
                    canvas.drawBitmap(dbitmap[dframe], x - (int) (w / 2), y - (int) (h / 2), null);
                    if(dframe_delay <= 0) {
                        dframe = dframe + 1;
                        dframe_delay = dsprite.frame_time;
                    }
                    else{
                        --dframe_delay;
                    }
                } else if (dframe == dsprite.frame_count) {
                    bombed = false;
//                respawn();
                }
            }
        }
        else if(!recycle || bombed) {
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
