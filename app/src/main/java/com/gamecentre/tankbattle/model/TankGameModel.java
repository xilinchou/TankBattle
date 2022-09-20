package com.gamecentre.tankbattle.model;

import com.gamecentre.tankbattle.tank.Enemy;
import com.gamecentre.tankbattle.tank.Player;
import com.gamecentre.tankbattle.tank.TankView;
import com.gamecentre.tankbattle.wifidirect.WifiDirectManager;

import java.io.Serializable;
import java.util.ArrayList;

public class TankGameModel extends Game implements Serializable {
    public long time;
    public boolean playerInfo = false;
    public boolean playerReady = false;
    public ArrayList<MTank> mEnemies = new ArrayList<>();
    public ArrayList<int[]> lObjects = new ArrayList<>();
    public ArrayList<Integer> lBushes = new ArrayList<>();
    public MTank mPlayer;
    public int height = TankView.HEIGHT;
    public boolean gameOver = false;
    public boolean stageComplete = false;
    public boolean pause = false;
    public boolean resume = false;
    public boolean restart = false;
    public boolean end_game = false;
    public boolean gift_life = false;
    public boolean eagleDestroyed;
    public boolean mlevelInfo = false;
    public int mlevel = 0;
    public int eagleProtection = 0;
    public int[] bonus;
    public boolean bnsAv;
    public boolean bnsClr;

    public int[] kills;
    public int totalKills;
    public int totalScore;
    public int stageScore;

    public void loadEnemies(ArrayList<Enemy> enemies) {
        for(Enemy enemy:enemies) {
//            mEnemies.add(new MTank(enemy.type,enemy.x,enemy.y,enemy.getDirection()));
            if(!enemy.recycle){
                if(enemy.isDestroyed() && !enemy.svrKill && WifiDirectManager.getInstance().isServer()) {
                    continue;
                }

                if(enemy.isDestroyed() && enemy.svrKill && !WifiDirectManager.getInstance().isServer()) {
                    continue;
                }
                mEnemies.add(new MTank(enemy));
            }
        }
        mEnemies.trimToSize();
    }

    public void loadPlayer(Player p) {
//        if(p.isDestroyed() && !p.svrKill && WifiDirectManager.getInstance().isServer()) {
//            return;
//        }
//        if(p.isDestroyed() && p.svrKill && !WifiDirectManager.getInstance().isServer()) {
//            return;
//        }
        mPlayer = new MTank(p);
    }

    public void loadLevelObjects(ArrayList<int[]> lo) {
        for(int[] l: lo) {
            lObjects.add(l);
        }
        lObjects.trimToSize();
    }

    public void loadLevelBushes(ArrayList<Integer> lb) {
        for(int l: lb) {
            lBushes.add(l);
        }
        lBushes.trimToSize();
    }

    public void loadBonus(int x, int y, int b, boolean av, boolean cl, int id) {
        bonus = new int[] {x, y, b, id};
        bnsAv = av;
        bnsClr = cl;
    }

    public void loadPlayerKills(int  kills[]) {
        this.kills = new int[kills.length];
        for (int i=0; i < kills.length; i++) {
            this.kills[i] = kills[i];
        }
    }
}
