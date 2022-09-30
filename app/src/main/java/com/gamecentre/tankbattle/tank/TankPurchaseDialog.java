package com.gamecentre.tankbattle.tank;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.gamecentre.tankbattle.R;
import com.gamecentre.tankbattle.billing.TransactionManager;
import com.gamecentre.tankbattle.sound.SoundManager;
import com.gamecentre.tankbattle.sound.Sounds;
import com.gamecentre.tankbattle.utils.CONST;
import com.gamecentre.tankbattle.utils.MessageRegister;
import com.gamecentre.tankbattle.utils.TransanctionListener;

public class TankPurchaseDialog extends Dialog implements View.OnClickListener, TransanctionListener {
    TankView mTankView;

    public AppCompatActivity activity;
    public Dialog dialog;
    public TankTextView watchBtn;
    public ImageView cancelBtn;
    SharedPreferences settings;
    int goldCount;
//    TankTextView goldCountTxt;

    TankTextView grenadeTxt, helmetTxt, clockTxt, shovelTxt, tankTxt,starTxt, gunTxt, boatTxt, mineTxt, goldTxt, retryTxt, adCoinTxt;

    CardView starboatCard, clockshovelCard, gunhelmetCard, tankgrenadeCard, mineCard, gameCard, game6Card, adgoldCard, goldCard;
    LinearLayout watchBtnView;

    private static String TOTAL_GOLD = "TOTAL GOLD";

    public TankPurchaseDialog(AppCompatActivity a, TankView mTankView) {
        super(a);
        this.activity = a;
        this.mTankView = mTankView;
    }

    public TankPurchaseDialog(AppCompatActivity a) {
        super(a);
        this.activity = a;
        this.mTankView = null;
        this.dialog = null;
    }

    public TankPurchaseDialog(AppCompatActivity a, Dialog d) {
        super(a);
        this.activity = a;
        this.mTankView = null;
        this.dialog = d;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        setContentView(R.layout.activity_tank_purchase);
        setCancelable(false);

        MessageRegister.getInstance().setTransListener(this);

        watchBtn = findViewById(R.id.watchBtn);
        cancelBtn = findViewById(R.id.buyCancelBtn);
        watchBtn.setOnClickListener(this);
        findViewById(R.id.watchgold).setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        starboatCard = findViewById(R.id.starboatCard);
        findViewById(R.id.starboat1).setOnClickListener(this);
        findViewById(R.id.starboat2).setOnClickListener(this);
        findViewById(R.id.starboat3).setOnClickListener(this);
        findViewById(R.id.starboat4).setOnClickListener(this);

        clockshovelCard = findViewById(R.id.clockshovelCard);
        findViewById(R.id.clockshovel1).setOnClickListener(this);
        findViewById(R.id.clockshovel2).setOnClickListener(this);
        findViewById(R.id.clockshovel3).setOnClickListener(this);
        findViewById(R.id.clockshovel4).setOnClickListener(this);

        gunhelmetCard = findViewById(R.id.gunhelmetCard);
        findViewById(R.id.gunhelmet1).setOnClickListener(this);
        findViewById(R.id.gunhelmet2).setOnClickListener(this);
        findViewById(R.id.gunhelmet3).setOnClickListener(this);
        findViewById(R.id.gunhelmet4).setOnClickListener(this);

        tankgrenadeCard = findViewById(R.id.tankgrenadeCard);
        findViewById(R.id.tankgrenade1).setOnClickListener(this);
        findViewById(R.id.tankgrenade2).setOnClickListener(this);
        findViewById(R.id.tankgrenade3).setOnClickListener(this);
        findViewById(R.id.tankgrenade4).setOnClickListener(this);

        mineCard = findViewById(R.id.mineCard);
        findViewById(R.id.buy_mine).setOnClickListener(this);
        findViewById(R.id.mine1).setOnClickListener(this);
        findViewById(R.id.mine2).setOnClickListener(this);
        findViewById(R.id.mine3).setOnClickListener(this);
        findViewById(R.id.mine4).setOnClickListener(this);

        gameCard = findViewById(R.id.gameCard);
        findViewById(R.id.buy_game).setOnClickListener(this);
        findViewById(R.id.game2).setOnClickListener(this);
        findViewById(R.id.game3).setOnClickListener(this);
        findViewById(R.id.game4).setOnClickListener(this);

        game6Card = findViewById(R.id.game6Card);
        findViewById(R.id.buy_game6).setOnClickListener(this);
        findViewById(R.id.game62).setOnClickListener(this);
        findViewById(R.id.game63).setOnClickListener(this);
        findViewById(R.id.game64).setOnClickListener(this);

        adgoldCard = findViewById(R.id.adgoldCard);
        findViewById(R.id.buy_goldAd).setOnClickListener(this);
        findViewById(R.id.goldAd2).setOnClickListener(this);
        findViewById(R.id.goldAd3).setOnClickListener(this);
        findViewById(R.id.goldAd4).setOnClickListener(this);

        goldCard = findViewById(R.id.goldCard);
        findViewById(R.id.buy_gold).setOnClickListener(this);
        findViewById(R.id.gold2).setOnClickListener(this);
        findViewById(R.id.gold3).setOnClickListener(this);

        watchBtnView = findViewById(R.id.watchBtnView);

//        goldCountTxt = (TankTextView)findViewById(R.id.stashTxt);

        settings = activity.getSharedPreferences("TankSettings", 0);

//        goldCount = settings.getInt(TankActivity.GOLD, 0);
//        goldCountTxt.setText(String.format("x%s", goldCount));


        grenadeTxt = findViewById(R.id.grenadeCountTxt);
        helmetTxt = findViewById(R.id.shieldCountTxt);
        clockTxt = findViewById(R.id.clockCountTxt);
        shovelTxt = findViewById(R.id.shovelCountTxt);
        tankTxt = findViewById(R.id.tankCountTxt);
        starTxt = findViewById(R.id.starCountTxt);
        gunTxt = findViewById(R.id.gunCountTxt);
        boatTxt = findViewById(R.id.boatCountTxt);
        mineTxt = findViewById(R.id.mineTxt);
        goldTxt = findViewById(R.id.goldCountTxt);
        retryTxt = findViewById(R.id.retryTxt);
        adCoinTxt = findViewById(R.id.adCoinTxt);

        updateBonus();
    }

    public void updateBonus() {
        grenadeTxt.setText(String.valueOf(settings.getInt(TankActivity.GRENADE,3)));
        helmetTxt.setText(String.valueOf(settings.getInt(TankActivity.SHIELD,3)));
        clockTxt.setText(String.valueOf(settings.getInt(TankActivity.CLOCK,3)));
        shovelTxt.setText(String.valueOf(settings.getInt(TankActivity.SHOVEL,3)));
        tankTxt.setText(String.valueOf(settings.getInt(TankActivity.TANK,3)));
        starTxt.setText(String.valueOf(settings.getInt(TankActivity.STAR,3)));
        gunTxt.setText(String.valueOf(settings.getInt(TankActivity.GUN,3)));
        boatTxt.setText(String.valueOf(settings.getInt(TankActivity.BOAT,3)));
        mineTxt.setText(String.valueOf(settings.getInt(TankActivity.MINE,3)));
        goldTxt.setText(String.valueOf(settings.getInt(TankActivity.GOLD,3)));
        retryTxt.setText(String.valueOf(settings.getInt(TankActivity.RETRY_COUNT,5)));
        adCoinTxt.setText(String.valueOf(settings.getInt(TankActivity.AD_COIN,0)));
    }

    public void clickAnimate1(View v) {
        ObjectAnimator viewAnimator = ObjectAnimator.ofPropertyValuesHolder(
                v,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f)
        );
        viewAnimator.setDuration(100);
        viewAnimator.setRepeatMode(ValueAnimator.REVERSE);
        viewAnimator.setRepeatCount(1);
        viewAnimator.start();
    }


    public void clickAnimate2(View v) {
        ObjectAnimator viewAnimator = ObjectAnimator.ofPropertyValuesHolder(
                v,
                PropertyValuesHolder.ofFloat("alpha", 0.5f)
        );
        viewAnimator.setDuration(100);
        viewAnimator.setRepeatMode(ValueAnimator.REVERSE);
        viewAnimator.setRepeatCount(1);
        viewAnimator.start();
    }


    @Override
    public void onClick(View v) {
        int cost;
        int goldCount = settings.getInt(TankActivity.GOLD,0);
        int id = v.getId();
        if (id == R.id.starboat1 || id == R.id.starboat2 || id == R.id.starboat3 || id == R.id.starboat4) {
            clickAnimate2(starboatCard);
            cost = Integer.parseInt((activity.getResources().getString(R.string.starboat_gold).replace("x", "")));
            if (cost <= goldCount) {
                int star = settings.getInt(TankActivity.STAR, 0);
                int boat = settings.getInt(TankActivity.BOAT, 0);

                int amount = Integer.parseInt((activity.getResources().getString(R.string.starboat_count).replace("x", "")));
                star += amount;
                boat += amount;
                goldCount -= cost;
                goldTxt.setText(String.format("%s", goldCount));
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt(TankActivity.STAR, star);
                editor.putInt(TankActivity.BOAT, boat);
                editor.putInt(TankActivity.GOLD, goldCount);
                editor.apply();
                SoundManager.playSound(Sounds.TANK.BUY_ITEM);
            } else {
                // TODO Not enough gold
                SoundManager.playSound(Sounds.TANK.CLICK2);
                Log.d("PURCHASE STARBOAT", "Not enough gold");
            }
        }
        else if (id == R.id.clockshovel1 || id == R.id.clockshovel2 || id == R.id.clockshovel3 || id == R.id.clockshovel4) {
            clickAnimate2(clockshovelCard);
            cost = Integer.parseInt((activity.getResources().getString(R.string.clockshovel_gold).replace("x", "")));
            if (cost <= goldCount) {
                int clock = settings.getInt(TankActivity.CLOCK, 0);
                int shovel = settings.getInt(TankActivity.SHOVEL, 0);

                int amount = Integer.parseInt((activity.getResources().getString(R.string.clockshovel_count).replace("x", "")));
                clock += amount;
                shovel += amount;
                goldCount -= cost;
                goldTxt.setText(String.format("%s", goldCount));
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt(TankActivity.CLOCK, clock);
                editor.putInt(TankActivity.SHOVEL, shovel);
                editor.putInt(TankActivity.GOLD, goldCount);
                editor.apply();
                SoundManager.playSound(Sounds.TANK.BUY_ITEM);
            } else {
                // TODO Not enough gold
                SoundManager.playSound(Sounds.TANK.CLICK2);
                Log.d("PURCHASE CLOCKSHOVEL", "Not enough gold");
            }
        }
        else if (id == R.id.gunhelmet1 || id == R.id.gunhelmet2 || id == R.id.gunhelmet3 || id == R.id.gunhelmet4) {
            clickAnimate2(gunhelmetCard);
            cost = Integer.parseInt((activity.getResources().getString(R.string.gunhelmet_gold).replace("x", "")));
            if (cost <= goldCount) {
                int gun = settings.getInt(TankActivity.GUN, 0);
                int helmet = settings.getInt(TankActivity.SHIELD, 0);

                int amount = Integer.parseInt((activity.getResources().getString(R.string.gunhelmet_count).replace("x", "")));
                gun += amount;
                helmet += amount;
                goldCount -= cost;
                goldTxt.setText(String.format("%s", goldCount));
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt(TankActivity.GUN, gun);
                editor.putInt(TankActivity.SHIELD, helmet);
                editor.putInt(TankActivity.GOLD, goldCount);
                editor.apply();
                SoundManager.playSound(Sounds.TANK.BUY_ITEM);
            } else {
                // TODO Not enough gold
                SoundManager.playSound(Sounds.TANK.CLICK2);
                Log.d("PURCHASE GUNHELMET", "Not enough gold");
            }
        }
        else if (id == R.id.tankgrenade1 || id == R.id.tankgrenade2 || id == R.id.tankgrenade3 || id == R.id.tankgrenade4) {
            clickAnimate2(tankgrenadeCard);
            cost = Integer.parseInt((activity.getResources().getString(R.string.tankgrenade_gold).replace("x", "")));
            if (cost <= goldCount) {
                int tank = settings.getInt(TankActivity.TANK, 0);
                int grenade = settings.getInt(TankActivity.GRENADE, 0);

                int amount = Integer.parseInt((activity.getResources().getString(R.string.tankgrenade_count).replace("x", "")));
                tank += amount;
                grenade += amount;
                goldCount -= cost;
                goldTxt.setText(String.format("%s", goldCount));
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt(TankActivity.TANK, tank);
                editor.putInt(TankActivity.GRENADE, grenade);
                editor.putInt(TankActivity.GOLD, goldCount);
                editor.apply();
                SoundManager.playSound(Sounds.TANK.BUY_ITEM);
            } else {
                // TODO Not enough gold
                SoundManager.playSound(Sounds.TANK.CLICK2);
                Log.d("PURCHASE TANKGRENADE", "Not enough gold");
            }
        }
        else if (id == R.id.buy_mine || id == R.id.mine1 || id == R.id.mine2 || id == R.id.mine3 || id == R.id.mine4) {
            clickAnimate2(mineCard);
            cost = Integer.parseInt((activity.getResources().getString(R.string.mine_gold).replace("x", "")));
            if (cost <= goldCount) {
                int mine = settings.getInt(TankActivity.MINE, 0);

                int amount = Integer.parseInt((activity.getResources().getString(R.string.mine_count).replace("x", "")));
                mine += amount;
                goldCount -= cost;
                goldTxt.setText(String.format("%s", goldCount));
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt(TankActivity.MINE, mine);
                editor.putInt(TankActivity.GOLD, goldCount);
                editor.apply();
                SoundManager.playSound(Sounds.TANK.BUY_ITEM);
            } else {
                // TODO Not enough gold
                SoundManager.playSound(Sounds.TANK.CLICK2);
                Log.d("PURCHASE MINE", "Not enough gold");
            }
        }
        else if (id == R.id.buy_game || id == R.id.game2 || id == R.id.game3 || id == R.id.game4){
            clickAnimate2(gameCard);
            int game_count = settings.getInt(TankActivity.RETRY_COUNT, 0);
            if(game_count >= CONST.Tank.MAX_GAME_COUNT) {
                Toast.makeText(activity, "Games full", Toast.LENGTH_SHORT).show();
                SoundManager.playSound(Sounds.TANK.CLICK2);
            }
            else {
                cost = Integer.parseInt((activity.getResources().getString(R.string.game_gold).replace("x", "")));
                if (cost <= goldCount) {
//                int game_count = settings.getInt(TankActivity.RETRY_COUNT, 0);
                    int amount = Integer.parseInt((activity.getResources().getString(R.string.game_count).replace("x", "")));
                    game_count += amount;

                    goldCount -= cost;
                    goldTxt.setText(String.format("%s", goldCount));
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt(TankActivity.RETRY_COUNT, game_count);
                    editor.putInt(TankActivity.GOLD, goldCount);
                    editor.apply();
                    SoundManager.playSound(Sounds.TANK.BUY_ITEM);
                } else {
                    // TODO Not enough gold
                    SoundManager.playSound(Sounds.TANK.CLICK2);
                    Log.d("PURCHASE TANKGRENADE", "Not enough gold");
                }
            }
        }
        else if (id == R.id.buy_game6 || id == R.id.game62 || id == R.id.game63 || id == R.id.game64){
            clickAnimate2(game6Card);
            cost = Integer.parseInt(activity.getResources().getString(R.string.game6h_gold).replace("x", ""));
            if (cost <= goldCount) {
                goldCount -= cost;
                goldTxt.setText(String.format("%s", goldCount));
                long time_6h = System.currentTimeMillis() + CONST.Tank.LIFE_DURATION_6HRS;
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt(TankActivity.GOLD, goldCount);
                editor.putLong(TankActivity.LIFE_TIME_6H, time_6h);
                editor.putInt(TankActivity.RETRY_COUNT, CONST.Tank.MAX_GAME_COUNT);
                editor.apply();
                SoundManager.playSound(Sounds.TANK.BUY_ITEM);
            }else {
                // TODO Not enough gold
                SoundManager.playSound(Sounds.TANK.CLICK2);
                Log.d("PURCHASE GAME6H", "Not enough gold");
            }


        }
        else if (id == R.id.buy_goldAd || id == R.id.goldAd2 || id == R.id.goldAd3 || id == R.id.goldAd4){
            clickAnimate2(adgoldCard);
            cost = Integer.parseInt(activity.getResources().getString(R.string.adCoin).replace("x", ""));
            int coinCount = settings.getInt(TankActivity.AD_COIN,0);
            if (cost <= coinCount) {
                int amount = Integer.parseInt((activity.getResources().getString(R.string.adGold_count).replace("x", "")));
                coinCount -= cost;
                goldCount += amount;
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt(TankActivity.GOLD, goldCount);
                editor.putInt(TankActivity.AD_COIN, coinCount);
                editor.apply();
                SoundManager.playSound(Sounds.TANK.BUY_ITEM);

                if (dialog instanceof TankEndGameDialog) {
                        ((TankEndGameDialog) this.dialog).setGoldCount();
                } else if (activity instanceof TankMenuActivity) {
                    ((TankMenuActivity) activity).adCoinTxt.setText(String.format("%s", coinCount));
                    ((TankMenuActivity) activity).goldTxt.setText(String.format("%s", goldCount));
                }
            }else {
                // TODO Not enough adcoin
                SoundManager.playSound(Sounds.TANK.CLICK2);
                Log.d("PURCHASE GOLD", "Not enough adcoin");
            }
        }
        else if (id == R.id.buy_gold || id == R.id.gold2 || id == R.id.gold3){
            clickAnimate2(goldCard);
//            ((TankMenuActivity) activity).makePurchase();
            new Handler().postDelayed(()->{
                TransactionManager.getInstnce().makePurchase(activity);
            },300);
        }
//                else if (id == R.id.stashTxt) {
//                    // TODO
//                }
        else if (id == R.id.watchBtn || id == R.id.watchgold) {
            clickAnimate2(watchBtnView);
            new Handler().postDelayed(()->{
                if (activity instanceof TankActivity) {
                    if (dialog instanceof TankEndGameDialog) {
                        ((TankActivity) activity).showRewardedVideo(this, (TankEndGameDialog) this.dialog);
                    } else {
                        ((TankActivity) activity).showRewardedVideo(this);
                    }
                } else if (activity instanceof TankMenuActivity) {
                    ((TankMenuActivity) activity).showRewardedVideo(this);
                }
            },300);
        }
        else if (id == R.id.buyCancelBtn) {
            clickAnimate1(v);
            SoundManager.playSound(Sounds.TANK.CLICK);
            new Handler().postDelayed(()->{
                if (activity instanceof TankActivity) {
                    ((TankActivity) activity).updateBonusStack();
                    if(mTankView != null) {
                        ((TankActivity) activity).getTankView().resumeNoAds();
                    }
                } else if (activity instanceof TankMenuActivity) {
                    ((TankMenuActivity) activity).updateStore();
                }
                dismiss();
            },300);
        }
        updateBonus();
    }

    @Override
    public void onPurchaseSuccessful() {
        Log.d("PURCHASE", "Purchase successful listener");
        Toast.makeText(this.activity, "Purchase successful listener", Toast.LENGTH_SHORT).show();
        int goldCount = settings.getInt(TankActivity.GOLD,0);
        goldCount += 200;
        goldTxt.setText(String.format("%s", goldCount));
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(TankActivity.GOLD, goldCount);
        editor.apply();
        SoundManager.playSound(Sounds.TANK.BUY_ITEM);
//        ((TankMenuActivity) activity).consumePurchase();
        TransactionManager.getInstnce().consumePurchase();

    }


//    @Override
//    public void onClick(View view) {
//
//    }
}
