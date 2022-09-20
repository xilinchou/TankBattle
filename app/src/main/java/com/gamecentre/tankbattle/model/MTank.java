package com.gamecentre.tankbattle.model;

import com.gamecentre.tankbattle.tank.Bullet;
import com.gamecentre.tankbattle.tank.Enemy;
import com.gamecentre.tankbattle.tank.ObjectType;
import com.gamecentre.tankbattle.tank.Player;

import java.io.Serializable;
import java.util.ArrayList;

public class MTank implements Serializable {
    public int x,y,sx,sy;
    public ObjectType type;
    public int dirction;
    public int armour;
    public int lives;
    public boolean boat,shield;
    public boolean tDestroyed, respawn;
    public ArrayList<int[]>bullets;
    public int typeVal, group;
    public int id;
    public boolean hasBonus = false;
    public boolean svrKill = false;
    public int gotBonus = 0;
    public int player = 0;
    public boolean freeze = false;
    public boolean bBomb = false;

    public  MTank(ObjectType type, int x, int y, int dirction) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.dirction = dirction;

    }

    public MTank(Player p) {
        x = p.x;
        y = p.y;
        type = p.type;
        dirction = p.getDirection();
        boat = p.hasBoat();
        shield = p.hasShield();
        armour = p.armour;
        lives = p.lives;
        tDestroyed = p.isDestroyed();
        respawn = p.respawn;
        svrKill = p.svrKill;
        gotBonus = p.gotBonus;
        player = p.player;
        freeze = p.isFrozen();
        bBomb = p.getBBomb();



        bullets = new ArrayList<>();
        ArrayList<Bullet> pBullets = p.getBullets();
        for(Bullet b:pBullets) {
            if(b == null || b.recycle) {
                continue;
            }
//            if(b.isDestroyed() && !b.svrKill && WifiDirectManager.getInstance().isServer()) {
//                continue;
//            }
//
//            if(b.isDestroyed() && b.svrKill && !WifiDirectManager.getInstance().isServer()) {
//                continue;
//            }

            if(b.isDestroyed()) {
                if(b.sent) {
                    continue;
                }
                b.sent = true;
            }


            int[] bt = {
                    b.x,
                    b.y,
                    b.getDirection(),
                    b.isDestroyed()?1:0,
                    b.id,
                    b.svrKill?1:0,
                    b.explode?1:0,
                    b.launched?1:0
            };
            bullets.add(bt);
        }
    }


    public MTank(Enemy e) {
        x = e.x;
        y = e.y;
        sx = e.sx;
        sy = e.sy;
        type = e.type;
        svrKill = e.svrKill;
        dirction = e.getDirection();
        tDestroyed = e.isDestroyed();
        boat = e.hasBoat();
//        shield = p.hasShield();
//        armour = p.armour;
        lives = Enemy.lives;
        typeVal = e.typeVal;
        group = e.group;
        respawn = e.respawn;
        id = e.id;
        hasBonus = e.hasBonus;




        bullets = new ArrayList<>();
        ArrayList<Bullet> pBullets = e.getBullets();
        for(Bullet b:pBullets) {
            if(b == null) {
                continue;
            }
//            if(b.isDestroyed() && !b.svrKill && WifiDirectManager.getInstance().isServer()) {
//                continue;
//            }
//
//            if(b.isDestroyed() && b.svrKill && !WifiDirectManager.getInstance().isServer()) {
//                continue;
//            }

            int[] bt = {b.x,
                        b.y,
                        b.getDirection(),
                        b.isDestroyed()?1:0,
                        b.id,
                        b.svrKill?1:0,
                        b.explode?1:0,
                        b.launched?1:0};
            bullets.add(bt);
        }
    }
}
