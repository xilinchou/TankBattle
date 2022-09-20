package com.gamecentre.tankbattle.tank;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.gamecentre.tankbattle.R;
import com.gamecentre.tankbattle.sound.SoundManager;
import com.gamecentre.tankbattle.sound.Sounds;

import java.util.ArrayList;

public class Bonus extends GameObjects {

    private ArrayList<Bitmap> bitmaps;
    private Bitmap bonusBm;
    private Sprite sprite;
    private int bonus = -1;
    private int BLINKRATE = 10;
    private boolean on = false;
    private int blinkTmr = BLINKRATE;
    private int bonusTmr = 200;
    private int scoreTmr;

    public static boolean cleared = false;
    public static final int GRENADE = 0;
    public static final int HELMET = 1;
    public static final int CLOCK = 2;
    public static final int SHOVEL = 3;
    public static final int TANK = 4;
    public static final int STAR = 5;
    public static final int GUN = 6;
    public static final int BOAT = 7;
    public static final int MINE = 8;
    public static boolean available = false;
    public static int id = 0;

    public Bonus() {
        super(0,0);
        sprite = SpriteObjects.getInstance().getData(ObjectType.ST_BONUS_GRENADE);
        super.w = sprite.w;
        super.h = sprite.h;

        bitmaps = new ArrayList<>();

        for(int i = 0; i < 8; i++) {
            Bitmap bm = Bitmap.createBitmap(TankView.graphics, sprite.x, sprite.y+(i*sprite.h), sprite.w, sprite.h);
            bitmaps.add(bm);
        }
        bitmaps.add(TankView.mineBitmap);
    }

    public void setBonus() {
//        if(bonusTmr > 0){
//            --bonusTmr;
//            return;
//        }
//        float prob = (float)Math.random();
        bonus = pollBonus(10);

//        int bonus = (int) (Math.random() * 8);
//        this.bonus = bonus;
        this.x = (int)(Math.random()*TankView.WIDTH- bitmaps.get(0).getWidth());
        this.y = (int)(Math.random()*TankView.HEIGHT- bitmaps.get(0).getHeight());
        id++;
        SoundManager.playSound(Sounds.TANK.POWERUP);
        if(bonus >= 0 && bonus <= 8) {
            bonusBm = bitmaps.get(bonus);
            on = true;
            available = true;
            cleared = false;
        }
//        bonusTmr = 200;
    }

//    private int pollBonus(int count) {
//        int[] b = new int[] {0,0,0,0,0,0,0,0};
//
//        for(int i = 0; i < count; i++) {
//            float p = (float)Math.random();
//            if(p < 0.1) {
//                b[0]++;
//            }
//            else if(p < 0.4) {
//                b[1]++;
//            }
//            else if(p < 0.5) {
//                b[2]++;
//            }
//            else if(p < 0.7) {
//                b[3]++;
//            }
//            else if(p < 0.75) {
//                b[4]++;
//            }
//            else if(p < 0.85) {
//                b[5]++;
//            }
//            else if(p < 0.9) {
//                b[6]++;
//            }
//            else {
//                b[7]++;
//            }
//        }
//        int max = 0;
//        int bonus = 0;
//        for(int i = 0; i < 8; i ++) {
//            if(b[i] > max){
//                max = b[i];
//                bonus = i;
//            }
//        }
//        return bonus;
//    }


//    private int pollBonus(int count) {
//        int[] b = new int[] {0,0,0,0,0,0,0,0};
//
//        for(int i = 0; i < count; i++) {
//            float p = (float)Math.random();
//            if(p < 0.18) {
//                b[0]++;
//            }
//            else if(p < 0.36) {
//                b[1]++;
//            }
//            else if(p < 0.47) {
//                b[2]++;
//            }
//            else if(p < 0.65) {
//                b[3]++;
//            }
//            else if(p < 0.72) {
//                b[4]++;
//            }
//            else if(p < 0.83) {
//                b[5]++;
//            }
//            else if(p < 0.9) {
//                b[6]++;
//            }
//            else {
//                b[7]++;
//            }
//        }
//        int max = 0;
//        int bonus = 0;
//        for(int i = 0; i < 8; i ++) {
//            if(b[i] > max){
//                max = b[i];
//                bonus = i;
//            }
//        }
//        return bonus;
//    }


    private int pollBonus(int count) {
        float p = (float)Math.random();

        if(p < 0.18) {
            return 0;
        }
        else if(p < 0.36) {
            return 1;
        }
        else if(p < 0.47) {
            return 2;
        }
        else if(p < 0.65) {
            return 3;
        }
        else if(p < 0.72) {
            return 4;
        }
        else if(p < 0.83) {
            return 5;
        }
        else if(p < 0.875) {
            return 6;
        }
        else if(p < 0.975){
            return 7;
        }
        else {
            return 8;
        }
    }



    public void setBonus(int x, int y, int b, boolean av, boolean cl, int id) {
        if(Bonus.id > id){
            return;
        }

        Bonus.id = id;

        if(b == -1) {
            bonus = -1;
            available = false;
            cleared = true;
            return;
        }
        if(cl){
            bonus = -1;
            bonusTmr = 200;
            scoreTmr = 5;
            available = false;
            cleared = true;
            return;
        }
//        if(x == this.x && y == this.y && b == this.bonus ) {
//            available = av;
//            return;
//        }
        bonusBm = bitmaps.get(b);
        this.x = x;
        this.y = y;
        this.bonus = b;
        available = true;

    }

    public int getBonus() {
        return bonus;

    }

    public void clearBonus() {
        bonus = -1;
//        on = false;
        bonusTmr = 200;
        scoreTmr = 5;
        available = false;
        cleared = true;
    }

    public void reset() {
        available = false;
    }
    public boolean isAvailable() {
        return available;
    }
    public void draw(Canvas canvas) {
        if(!available) {
            return;
        }
        if(bonus >= 0 && bonus <= 8) {
            if(blinkTmr > 0) {
                --blinkTmr;
            }
            else {
                on = !on;
                blinkTmr = BLINKRATE;
            }
            if(on) {
                if(x < 0) {
                    x = 0;
                }
                else if(TankView.WIDTH - x < (int)(sprite.w)) {
                    x = TankView.WIDTH - (int)(sprite.w);
                }

                if(y < 0) {
                    y = 0;
                }
                else if(TankView.HEIGHT - y < (int)(sprite.h)) {
                    y = TankView.HEIGHT - (int)(sprite.h);
                }
                canvas.drawBitmap(bonusBm, x, y, null);
            }
        }
        else if(scoreTmr > 0) {
            drawText(canvas,"500");
            --scoreTmr;
        }
    }
}
