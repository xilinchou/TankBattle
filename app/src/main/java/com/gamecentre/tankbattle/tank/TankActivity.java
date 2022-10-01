package com.gamecentre.tankbattle.tank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.gamecentre.tankbattle.sound.SoundManager;
import com.gamecentre.tankbattle.sound.Sounds;
import com.gamecentre.tankbattle.utils.AutoResizeTextView;
import com.gamecentre.tankbattle.utils.CONST;
import com.gamecentre.tankbattle.utils.MessageRegister;
import com.gamecentre.tankbattle.R;
import com.gamecentre.tankbattle.wifidirect.WifiDirectManager;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class TankActivity extends AppCompatActivity implements View.OnTouchListener, ServiceListener {

    public Button stick, upBtn, dwnBtn, rtBtn, lftBtn, shtBtn, bmbBtn, menuBtn, nxtBtn, retryBtn;
    public Button stickView;
    public LinearLayout enemyCount, bonusFrame, pauseControl;
    public ImageView P1StatusImg, P2StatusImg, StageFlag;
    public TextView P1StatusTxt, P2StatusTxt, StageTxt, enemyCountTxt;
    public TankTextView curtainTxt,gameOverTxt;

    public RelativeLayout scoreView,gameView,navView, shootAlign, bombAlign;
    private LinearLayout challengeItem;
    private ScrollView challengeScroll;
    private TankTextView challengeCount;
    public TankTextView bmbText, retryCount, retryGameTmr, hiScore, stageScore, p1Score, p2Score;
    public TankTextView p1AScore, p1BScore, p1CScore, p1DScore;
    public TankTextView p2AScore, p2BScore, p2CScore, p2DScore;
    public TankTextView p1ACount, p1BCount, p1CCount, p1DCount, p1Count;
    public TankTextView p2ACount, p2BCount, p2CCount, p2DCount, p2Count;
    public ImageView enemyCountImg,gameStars, giftBtn, gameImg;

    private long lastGiftTime = 0;


    public TankTextView pauseBtn;//, gameTimeView;
    public AutoResizeTextView gameTimeView;

    Timer updateTimer;
    public long playtime;

    public static final String
            GRENADE = "GRENADE",
            CLOCK = "CLOCK",
            SHOVEL = "SHOVEL",
            TANK = "TANK",
            GUN = "GUN",
            BOAT = "BOAT",
            STAR = "STAR",
            SHIELD = "SHIELD",
            MINE = "MINE",
            GOLD = "GOLD",

            RETRY_COUNT = "RETRY_COUNT",
            LIFE_TIME = "LIFE_TIME",
            LIFE_TIME_6H = "LIFE_TIME_6H",

            GOLD_LEVEL = "GOLD_LEVEL",
            AD_COIN = "AD_COIN",

            FIRST_TIME = "FIRST_TIME",
            LAST_DAY = "LAST_DAY",
            CONSECUTIVE_DAYS = "CONSECUTIVE_DAYS",

            OBJECTIVES = "OBJECTIVES",
            LEVEL_STARS = "LEVEL_STARS";

    SharedPreferences settings;


    boolean twoPlayers = false;
    boolean first_start;
    public static boolean AdsRunning = false;

    Typeface typeface;

    private  TankView mTankView;
    private AdView mAdView;
    public InterstitialAd mInterstitialAd;
    private RewardedAd mRewardedAd;
    private RewardedInterstitialAd mRewardedInterstitialAd;
    private static final String IAD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712";
    private static final String RAD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917";
    private static final String RIAD_UNIT_ID = "ca-app-pub-3940256099942544/5354046379";
    boolean isLoading;
    boolean isLoadingIntAds;
    public static boolean GOT_REWARD = false;
    public static boolean GOT_IREWARD = false;

    RelativeLayout.LayoutParams layoutParams;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tank);

        settings = getSharedPreferences("TankSettings", 0);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

//        mAdView = findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);

        loadInterstitialAd();
        loadRewardedAd();
        loadRewardedInterstitialAd();

        mTankView = findViewById(R.id.tankView);

        initilizeBonusBank();

        Intent i = getIntent();
        Bundle b = i.getExtras();
        boolean two_Players = b.getBoolean(TankMenuActivity.TWO_PLAYERS, false);
        mTankView.setPlayerControl(two_Players);
        if(two_Players) {
            twoPlayers = true;
        }
        else {
            twoPlayers = false;
        }

        gameView = findViewById(R.id.gameView);
        navView = findViewById(R.id.navView);
        shootAlign = findViewById(R.id.shootAlign);
        bombAlign = findViewById(R.id.bombAlign);
        bmbText = findViewById(R.id.bmbTxt);

        shtBtn = findViewById(R.id.shootBtn);
        bmbBtn = findViewById(R.id.bombBtn);
        upBtn = findViewById(R.id.upBtn);
        dwnBtn = findViewById(R.id.downBtn);
        rtBtn = findViewById(R.id.rightBtn);
        lftBtn = findViewById(R.id.leftBtn);
//        stick = findViewById(R.id.navStick);
//        stickView = findViewById(R.id.navStickView);

        menuBtn = findViewById(R.id.menuBtn);
        nxtBtn = findViewById(R.id.nxtBtn);
        retryBtn = findViewById(R.id.retryBtn);

        pauseBtn = findViewById(R.id.pauseBtn);
        gameTimeView = findViewById(R.id.gameTime);
        enemyCount = findViewById(R.id.enemyCount);


        shtBtn.setOnTouchListener(this);
        bmbBtn.setOnTouchListener(this);
        upBtn.setOnTouchListener(this);
        dwnBtn.setOnTouchListener(this);
        rtBtn.setOnTouchListener(this);
        lftBtn.setOnTouchListener(this);
//        stick.setOnTouchListener(this);
//        stickView.setOnTouchListener(this);

//        ImageView stickView = new ImageView(this);
//        stickView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.navstick));
//        stickView.setTag("StickView");

//        stickView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    ClipData data = ClipData.newPlainText("", "");
//                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(stickView);
//
//                    stickView.startDrag(data, shadowBuilder, stickView, 0);
//                    v.setAlpha(0);
//                }
//                else if (event.getAction() == MotionEvent.ACTION_UP){
//                    v.setAlpha(1);
//                }
//                return false;
//            }
//        });
//
//        stickView.setOnDragListener(new View.OnDragListener() {
//            @Override
//            public boolean onDrag(View v, DragEvent event) {
//                String msg = "Drag and Drop";
//                switch(event.getAction()) {
//                    case DragEvent.ACTION_DRAG_STARTED:
//                        layoutParams = (RelativeLayout.LayoutParams)v.getLayoutParams();
//                        Log.d(msg, "Action is DragEvent.ACTION_DRAG_STARTED");
//                        break;
//
//                    case DragEvent.ACTION_DRAG_ENTERED:
//                        Log.d(msg, "Action is DragEvent.ACTION_DRAG_ENTERED");
//                        int x_cord = (int) event.getX();
//                        int y_cord = (int) event.getY();
//                        break;
//
//                    case DragEvent.ACTION_DRAG_EXITED :
//                        Log.d(msg, "Action is DragEvent.ACTION_DRAG_EXITED");
//                        x_cord = (int) event.getX();
//                        y_cord = (int) event.getY();
//                        layoutParams.leftMargin = x_cord;
//                        layoutParams.topMargin = y_cord;
//                        v.setLayoutParams(layoutParams);
//                        break;
//
//                    case DragEvent.ACTION_DRAG_LOCATION  :
//                        Log.d(msg, "Action is DragEvent.ACTION_DRAG_LOCATION");
//                        x_cord = (int) event.getX();
//                        y_cord = (int) event.getY();
//                        break;
//
//                    case DragEvent.ACTION_DRAG_ENDED   :
//                        Log.d(msg, "Action is DragEvent.ACTION_DRAG_ENDED");
//                        v.setAlpha(1);
//                        // Do nothing
//                        break;
//
//                    case DragEvent.ACTION_DROP:
//                        Log.d(msg, "ACTION_DROP event");
//
//                        // Do nothing
//                        break;
//                    default: break;
//                }
//
//                return false;
//            }
//        });

        menuBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    endGame();
//                    SoundManager.stopGameSounds();
//                    Intent i = new Intent(TankActivity.this, TankMenuActivity.class);
//                    TankActivity.this.startActivity(i);
//                    TankActivity.this.finish();
                }
                return true;
            }
        });

        nxtBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
//                    int retries = settings.getInt(TankActivity.RETRY_COUNT,3);
                    if (TankView.stageComplete) {
                        TankView.mNewRound = true;
                        TankView.level++;
                    }
                }
                return true;
            }
        });

        retryBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    int retries = settings.getInt(TankActivity.RETRY_COUNT,3);
                    if (TankView.gameover) {
                        if(retries == CONST.Tank.MAX_GAME_COUNT) {
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putLong(TankActivity.LIFE_TIME,System.currentTimeMillis());
                            editor.apply();
                        }
                        if(retries > 0){
                            //TODO
                            long game6h = settings.getLong(TankActivity.LIFE_TIME_6H,0);
                            if(game6h < System.currentTimeMillis()) {
                                --retries;
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putInt(RETRY_COUNT, retries);
                                editor.commit();
                            }
                            mTankView.retryStage();
                        }
                        else {
                            openGamePurchse();
                        }
                    }
                    else if (TankView.stageComplete) {
                        TankView.mNewRound = true;
//                        TankView.level++;
                    }
                }
                return true;
            }
        });

        pauseBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    mTankView.pause();
                }
                return true;
            }
        });

        giftBtn = findViewById(R.id.giftLife);
        giftBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if(System.currentTimeMillis() - lastGiftTime >= 500) {
                        mTankView.giftLife();
                        lastGiftTime = System.currentTimeMillis();
                    }
                }
                return true;
            }
        });


        enableControls();


        bonusFrame = findViewById(R.id.bonusFrame);
        pauseControl = findViewById(R.id.pauseControl);

        P1StatusImg = findViewById(R.id.P1StautusImg);
        P2StatusImg = findViewById(R.id.P2StautusImg);
        P1StatusTxt = findViewById(R.id.P1StautusTxt);
        P2StatusTxt = findViewById(R.id.P2StautusTxt);
        curtainTxt = findViewById(R.id.curtainText);
        gameOverTxt = findViewById(R.id.gameOverText);

        StageFlag = findViewById(R.id.stageImg);
        StageTxt = findViewById(R.id.stageTxt);

        typeface = Typeface.createFromAsset(getAssets(),"prstartk.ttf");
        P1StatusTxt.setTypeface(typeface);
        P1StatusTxt.setTextSize(8);
        P1StatusTxt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        P2StatusTxt.setTypeface(typeface);
        P2StatusTxt.setTextSize(8);
        P2StatusTxt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        enemyCountImg = findViewById(R.id.enemyCountImg);
        enemyCountTxt = findViewById(R.id.enemyCountTxt);
        enemyCountTxt.setTypeface(typeface);
        enemyCountTxt.setTextSize(8);
        enemyCountTxt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);


        StageTxt.setTypeface(typeface);
        StageTxt.setTextSize(10);
        StageTxt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);


        scoreView = findViewById(R.id.scoreView);
        retryCount = findViewById(R.id.retryGameTxt);
        retryGameTmr = findViewById(R.id.retryGameTmr);
        hiScore = findViewById(R.id.HiScore);
        stageScore = findViewById(R.id.StageScore);

        p1Score = findViewById(R.id.P1Score);
        p2Score = findViewById(R.id.P2Score);

        p1AScore = findViewById(R.id.enemyAScoreP1);
        p1BScore = findViewById(R.id.enemyBScoreP1);
        p1CScore = findViewById(R.id.enemyCScoreP1);
        p1DScore = findViewById(R.id.enemyDScoreP1);

        p2AScore = findViewById(R.id.enemyAScoreP2);
        p2BScore = findViewById(R.id.enemyBScoreP2);
        p2CScore = findViewById(R.id.enemyCScoreP2);
        p2DScore = findViewById(R.id.enemyDScoreP2);

        p1ACount = findViewById(R.id.enemyACountP1);
        p1BCount = findViewById(R.id.enemyBCountP1);
        p1CCount = findViewById(R.id.enemyCCountP1);
        p1DCount = findViewById(R.id.enemyDCountP1);
        p1Count = findViewById(R.id.totalCountP1);

        p2ACount = findViewById(R.id.enemyACountP2);
        p2BCount = findViewById(R.id.enemyBCountP2);
        p2CCount = findViewById(R.id.enemyCCountP2);
        p2DCount = findViewById(R.id.enemyDCountP2);
        p2Count = findViewById(R.id.totalCountP2);

        gameStars = findViewById(R.id.gameStars);
        gameImg = findViewById(R.id.retryGameImg);

        challengeItem = findViewById(R.id.challengeItems);
        challengeCount = findViewById(R.id.scoreViewChallenge);
        challengeScroll = findViewById(R.id.challengeScroll);
        if(TankView.CONSTRUCTION) {
            challengeScroll.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return true;
                }
            });
        }

        scoreView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        MessageRegister.getInstance().setServiceListener(this);

        first_start = true;
    }

    public TankView getTankView() {
        return this.mTankView;
    }

    public void start_timer() {
        updateTimer = new Timer("update");
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                update_gametime();
            }
        }, 0, 1000);
    }


    public void stop_timer() {
        updateTimer.cancel();
    }


    private void update_gametime(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playtime = System.currentTimeMillis() - TankView.GameStartTime;
                SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
                gameTimeView.setText(sdf.format(playtime));
            }
        });
    }

    public void displyObjectives(int completed, boolean[] objectives) {
        challengeCount.setText(String.format(Locale.ENGLISH,"CHALLENGES %d/11",completed));
        if(TankView.CONSTRUCTION) {
            challengeCount.setAlpha(0.4f);
            challengeItem.setAlpha(0.4f);
//            challengeScroll.setEnabled(false);
        }
        else {
            challengeCount.setAlpha(1f);
            challengeItem.setAlpha(1f);
//            challengeScroll.setEnabled(true);
        }
        for(int obj = 0; obj < TankView.NUM_OBJECTIVES; obj++) {
            if (objectives[obj]) {
                ((TankTextView) challengeItem.getChildAt(obj)).setTextColor(this.getResources().getColor(R.color.green));
            } else {
                ((TankTextView) challengeItem.getChildAt(obj)).setTextColor(this.getResources().getColor(R.color.white));
            }
        }
    }


    public void onServiceMessageReceived(int games, long time_left, boolean h6) {
        if(mTankView.showingScore) {
            SimpleDateFormat sdf = new SimpleDateFormat("mm:ss", Locale.ENGLISH);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            retryCount.setText(String.valueOf(games));
            if(games >= CONST.Tank.MAX_GAME_COUNT && !h6) {
                Log.d("SERVICE MESSAGE MENU", String.valueOf(games) + " " + time_left + " false");
                retryGameTmr.setText("");
                gameImg.setBackground(ResourcesCompat.getDrawable(this.getResources(),R.drawable.retry_img,null));
            }
            else if(h6) {
                Log.d("SERVICE MESSAGE MENU", String.valueOf(games) + " " + time_left + " true");
                sdf = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                retryGameTmr.setText(sdf.format(time_left));
//                retryCount.setText((""));
                gameImg.setBackground(ResourcesCompat.getDrawable(this.getResources(),R.drawable.game6h,null));
            }
            else{
                Log.d("SERVICE MESSAGE MENU", String.valueOf(games) + " " + time_left + " false");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                retryGameTmr.setText(sdf.format(time_left));
                gameImg.setBackground(ResourcesCompat.getDrawable(this.getResources(),R.drawable.retry_img,null));
            }
        }
    }



    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        MessageRegister.getInstance().registerButtonAction(view, motionEvent);
        return false;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initilizeBonusBank() {

        SharedPreferences.Editor editor = settings.edit();

//        TankView.GOLD_LEVEL = settings.getInt(TankActivity.GOLD_LEVEL,0);
//        Log.d("SAVED GOLD_LEVEL", String.valueOf(TankView.GOLD_LEVEL));
//        if(TankView.GOLD_LEVEL == 0) {
//            editor.putInt(TankActivity.GOLD_LEVEL,0);
//        }

        LinearLayout bonusBank = findViewById(R.id.bonusBank);
        int numRow = bonusBank.getChildCount();

        for(int row = 0; row < numRow; row++) {
            int numCol = ((LinearLayout)bonusBank.getChildAt(row)).getChildCount();
            for(int col = 0; col < numCol; col++) {
                RelativeLayout bonus = (RelativeLayout)((LinearLayout)(bonusBank.getChildAt(row))).getChildAt(col);
                bonus.setOnTouchListener(bonusListener);
                int bonusCount = settings.getInt((String) bonus.getTag(),3);
                TankTextView txtView = (TankTextView)(bonus.getChildAt(0));
                txtView.setText(bonusCount > 0 ? String.valueOf(bonusCount) : "+");
                editor.putInt((String) bonus.getTag(),bonusCount);
            }
        }

        editor.apply();
    }

    public void updateBonusStack() {


        Log.d("UPDATING BONUS STACK", String.valueOf(TankView.GOLD_LEVEL));

        LinearLayout bonusBank = findViewById(R.id.bonusBank);
        int numRow = bonusBank.getChildCount();

        for(int row = 0; row < numRow; row++) {
            int numCol = ((LinearLayout)bonusBank.getChildAt(row)).getChildCount();
            for(int col = 0; col < numCol; col++) {
                RelativeLayout bonus = (RelativeLayout)((LinearLayout)(bonusBank.getChildAt(row))).getChildAt(col);
                int bonusCount = settings.getInt((String) bonus.getTag(),0);
                TankTextView txtView = (TankTextView)(bonus.getChildAt(0));
                txtView.setText(bonusCount > 0 ? String.valueOf(bonusCount) : "+");
            }
        }
    }

    View.OnTouchListener bonusListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                int count = settings.getInt((String) view.getTag(),0);
                Log.d("BONUS", "Got bonus" + (String) view.getTag());
                if(count <= 0 || ((String) view.getTag()).equals("GOLD")) {
                    Log.d("BONUS", "Buy more bonus");
                    mTankView.interrupt();
                    openStore();

                }
                else {
                    mTankView.applyBonus((String) view.getTag());
                    --count;
                    TankTextView txtView = ((TankTextView)((RelativeLayout)view).getChildAt(0));
                    txtView.setText(count > 0 ? String.valueOf(count) : "+");
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt((String) view.getTag(),count);
                    editor.apply();
                }
            }
            return true;
        }
    };


    public void openPauseDialog(TankView tankView) {

        TankPauseGameDialog wd = new TankPauseGameDialog(this, tankView);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(wd.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        wd.show();
        wd.getWindow().setAttributes(lp);
    }



    public void openGamePurchse() {

        TankPurchaseGameDialog wd = new TankPurchaseGameDialog(this, mTankView);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(wd.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        wd.show();
        wd.getWindow().setAttributes(lp);
    }


    public void openStore() {

        TankPurchaseDialog wd = new TankPurchaseDialog(TankActivity.this, mTankView);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(wd.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        wd.show();
        wd.getWindow().setAttributes(lp);
    }

    public void openStore(Dialog d) {

        TankPurchaseDialog wd = new TankPurchaseDialog(TankActivity.this, d);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(wd.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        wd.show();
        wd.getWindow().setAttributes(lp);
    }

    public void saveObjectives(ArrayList<boolean[]> objectives) {
        SharedPreferences.Editor editor = settings.edit();
        Gson gson = new Gson();
        String json = gson.toJson(objectives);
        editor.putString(TankActivity.OBJECTIVES,json);
        editor.apply();
    }

    public ArrayList<boolean[]> loadObjectives() {
        String objectives = settings.getString(TankActivity.OBJECTIVES,null);
        if(objectives == null) {
            ArrayList<boolean[]> obj = new ArrayList<>();
            for(int i = 0; i < TankView.NUM_LEVELS; i++) {
                boolean[] p = new boolean[TankView.NUM_OBJECTIVES];
                for(int j = 0; j < p.length; j++) {
                    p[j] = false;
                }
                obj.add(p);
            }
            saveObjectives(obj);
            return obj;
        }
        Type type = new TypeToken<ArrayList<boolean[]>>() {}.getType();
        Gson gson = new Gson();
        return gson.fromJson(objectives,type);
    }


    public void saveStars(ArrayList<Integer> stars) {
        SharedPreferences.Editor editor = settings.edit();
        Gson gson = new Gson();
        String json = gson.toJson(stars);
        editor.putString(TankActivity.LEVEL_STARS,json);
        editor.apply();
    }

    public ArrayList<Integer> loadStars() {
        String stars = settings.getString(TankActivity.LEVEL_STARS,null);
        if(stars == null) {
            ArrayList<Integer> star = new ArrayList<>();
            for(int i = 0; i < TankView.NUM_LEVELS; i++) {
                star.add(0);
            }
            saveStars(star);
            return star;
        }
        Type type = new TypeToken<ArrayList<Integer>>() {}.getType();
        Gson gson = new Gson();
        return gson.fromJson(stars,type);
    }


    public void saveString(String key, String val) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key,val);
        editor.apply();
    }

    public void saveInt(String key, int val) {
        Log.d("Saving " + key, String.valueOf(val));
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key,val);
        editor.apply();
    }

    public void saveBoolean(String key, boolean val) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key,val);
        editor.apply();
    }

    public void saveArray(String key, ArrayList<Integer> val) {
        SharedPreferences.Editor editor = settings.edit();
        Gson gson = new Gson();
        String json = gson.toJson(val);
        editor.putString(key,json);
        editor.apply();
    }

    public ArrayList<boolean[]> loadArray(String key) {
        String json = settings.getString(key,null);
        Type type = new TypeToken<ArrayList<boolean[]>>() {}.getType();
        Gson gson = new Gson();

//        ArrayList<boolean[]> objectives = gson.fromJson(json,type);
//        return objectives;
        return gson.fromJson(json,type);
    }



//    public void updateGold(int addition) {
//        int count = settings.getInt(TankActivity.GOLD,0);
//        count += addition;
//        if(count < 0) {
//            count = 0;
//        }
//
//        TankTextView txtView = findViewById(R.id.goldCount);
//        txtView.setText(count > 0 ? String.valueOf(count) : "+");
//        SharedPreferences.Editor editor = settings.edit();
//        editor.putInt(TankActivity.GOLD,count);
//        editor.apply();
//    }

    public void enableControls() {
        shtBtn.setEnabled(true);
        bmbBtn.setEnabled(true);
        upBtn.setEnabled(true);
        dwnBtn.setEnabled(true);
        rtBtn.setEnabled(true);
        lftBtn.setEnabled(true);
        if(twoPlayers) {
            enableGift();
        }
    }


    public void endGame() {
        mTankView.stop();
        SoundManager.stopGameSounds();
//        try{SoundManager.stopSound(TankView.SCENE_SOUND);}
//        catch (Exception e){}
//        try{SoundManager.stopSound(Sounds.TANK.HVE_SOUND);}
//        catch (Exception e){}
//        mTankView.release();
        Intent i = new Intent(TankActivity.this, TankMenuActivity.class);
        TankActivity.this.startActivity(i);
        TankActivity.this.finish();
    }

    public void disableControls() {
        shtBtn.setEnabled(false);
        bmbBtn.setEnabled(false);
        upBtn.setEnabled(false);
        dwnBtn.setEnabled(false);
        rtBtn.setEnabled(false);
        lftBtn.setEnabled(false);
        if(twoPlayers) {
            disableGift();
        }
    }

    public void enableGift() {
        giftBtn.setEnabled(true);
    }

    public void disableGift() {
        giftBtn.setEnabled(false);
    }

    public void showGift() {
        giftBtn.setVisibility(View.VISIBLE);
    }

    public void hideGift() {
        giftBtn.setVisibility(View.INVISIBLE);
    }

    protected void onStop() {
        super.onStop();
        mTankView.stop();
    }

    public TankView getView() {
        return mTankView;
    }



    protected void onDestroy() {
        super.onDestroy();
        mTankView.release();
        WifiDirectManager.getInstance().cancelDisconnect();
        Log.d("TANKACTIVITY", "Activity destroyed");
    }


    protected void onPause() {
        super.onPause();
        SoundManager.pauseGameSounds();
    }

    protected void onResume() {
        super.onResume();
        SoundManager.resumeGameSounds();
        mTankView.resume();
    }



    public void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                this,
                IAD_UNIT_ID,
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        TankActivity.this.mInterstitialAd = interstitialAd;
                        Log.i("Interstitial Ad", "onAdLoaded");
//                        Toast.makeText(TankActivity.this, "onAdLoaded()", Toast.LENGTH_SHORT).show();
                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        TankActivity.this.mInterstitialAd = null;
                                        Log.d("TAG", "The ad was dismissed.");
                                        TankActivity.this.loadInterstitialAd();
                                        if(TankView.EVENT != TankView.PAUSE) {
                                            mTankView.resumeNoAds();
                                        }
                                        AdsRunning = false;
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        TankActivity.this.mInterstitialAd = null;
                                        Log.d("TAG", "The ad failed to show.");
//                                        if(TankView.EVENT != TankView.PAUSE) {
//                                            mTankView.pauseNoAds();
//                                        }
                                        AdsRunning = false;
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        // Called when fullscreen content is shown.
                                        Log.d("TAG", "The ad was shown.");
                                        AdsRunning = true;
                                    }
                                });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("Interstitial Ad", loadAdError.getMessage());
                        mInterstitialAd = null;

//                        if(TankView.EVENT != TankView.PAUSE) {
//                            mTankView.pauseNoAds();
//                        }

                        String error =
                                String.format(
                                        "domain: %s, code: %d, message: %s",
                                        loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());
//                        Toast.makeText(
//                                TankActivity.this, "onAdFailedToLoad() with error: " + error, Toast.LENGTH_SHORT)
//                                .show();
                    }
                });
    }

    public void showInterstitialAd() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
        } else {
//            Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show();
            if(TankView.EVENT != TankView.PAUSE) {
                mTankView.resumeNoAds();
            }
        }
    }



    public void loadRewardedAd() {
        if (mRewardedAd == null) {
            isLoading = true;
            GOT_REWARD = false;
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(
                    this,
                    RAD_UNIT_ID,
                    adRequest,
                    new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error.
                            Log.d("Rewarded Ads", loadAdError.getMessage());
                            mRewardedAd = null;
                            TankActivity.this.isLoading = false;
//                            Toast.makeText(TankActivity.this, "onAdFailedToLoad", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            TankActivity.this.mRewardedAd = rewardedAd;
                            Log.d("Rewarded Ads", "onAdLoaded");
                            TankActivity.this.isLoading = false;
//                            Toast.makeText(TankActivity.this, "onAdLoaded", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    public void loadRewardedInterstitialAd() {
        if (mRewardedInterstitialAd == null) {
            isLoadingIntAds = true;

            AdRequest adRequest = new AdRequest.Builder().build();
            // Use the test ad unit ID to load an ad.
            RewardedInterstitialAd.load(
                    TankActivity.this,
                    RIAD_UNIT_ID,
                    adRequest,
                    new RewardedInterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(RewardedInterstitialAd ad) {
                            Log.d("Rewarded InterstitialAD", "onAdLoaded");

                            mRewardedInterstitialAd = ad;
                            isLoadingIntAds = false;
//                            Toast.makeText(TankActivity.this, "onAdLoaded", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onAdFailedToLoad(LoadAdError loadAdError) {
                            Log.d("Rewarded InterstitialAD", "onAdFailedToLoad: " + loadAdError.getMessage());

                            // Handle the error.
                            mRewardedInterstitialAd = null;
                            isLoadingIntAds = false;
//                            Toast.makeText(TankActivity.this, "onAdFailedToLoad", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void introduceVideoAd(int rewardAmount, String rewardType) {
        AdDialogFragment dialog = AdDialogFragment.newInstance(rewardAmount, rewardType);
        dialog.setAdDialogInteractionListener(
                new AdDialogFragment.AdDialogInteractionListener() {
                    @Override
                    public void onShowAd() {
                        Log.d("Rewarded InterstitialAD", "The rewarded interstitial ad is starting.");
                        showRewardedVideoIAD();
                    }

                    @Override
                    public void onCancelAd() {
                        Log.d("Rewarded InterstitialAD", "The rewarded interstitial ad was skipped before it starts.");
                    }
                });
        dialog.show(getSupportFragmentManager(), "AdDialogFragment");
    }

    public void showRewardedInterstitialAd(boolean cancel) {
        if (mRewardedInterstitialAd != null) {
//            RewardItem rewardItem = mRewardedInterstitialAd.getRewardItem();
//            int rewardAmount = rewardItem.getAmount();
//            String rewardType = rewardItem.getType();

            Log.d("Rewarded InterstitialAD", "The rewarded interstitial ad is ready.");
            if(cancel) {
                introduceVideoAd(2, "Ad coins");
            }
            else {
                showRewardedVideoIAD();
            }
        }
        else {
            Log.d("Rewarded InterstitialAD", "The rewarded interstitial ad is not ready.");
//            Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show();
            if(TankView.EVENT != TankView.PAUSE) {
                mTankView.resumeNoAds();
            }
        }


    }


    private void showRewardedVideoIAD() {

        mRewardedInterstitialAd.setFullScreenContentCallback(
                new FullScreenContentCallback() {
                    @Override
                    public void onAdClicked() {
                        // Called when a click is recorded for an ad.
                        Log.d("Rewarded InterstitialAD", "Ad was clicked.");
                        GOT_IREWARD = true;
                    }

                    @Override
                    public void onAdImpression() {
                        // Called when an impression is recorded for an ad.
                        Log.d("Rewarded InterstitialAD", "Ad recorded an impression.");
                        GOT_IREWARD = true;
                    }


                    /** Called when ad showed the full screen content. */
                    @Override
                    public void onAdShowedFullScreenContent() {
                        Log.d("Rewarded InterstitialAD", "onAdShowedFullScreenContent");
                        GOT_IREWARD = false;

//                        Toast.makeText(TankActivity.this, "onAdShowedFullScreenContent", Toast.LENGTH_SHORT)
//                                .show();
                    }

                    /** Called when the ad failed to show full screen content. */
                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        Log.d("Rewarded InterstitialAD", "onAdFailedToShowFullScreenContent: " + adError.getMessage());
                        GOT_IREWARD = false;
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        mRewardedInterstitialAd = null;
                        loadRewardedInterstitialAd();

//                        Toast.makeText(
//                                        TankActivity.this, "onAdFailedToShowFullScreenContent", Toast.LENGTH_SHORT)
//                                .show();
                    }

                    /** Called when full screen content is dismissed. */
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        mRewardedInterstitialAd = null;
                        Log.d("Rewarded InterstitialAD", "onAdDismissedFullScreenContent");
//                        Toast.makeText(TankActivity.this, "onAdDismissedFullScreenContent", Toast.LENGTH_SHORT)
//                                .show();
                        // Preload the next rewarded interstitial ad.
                        loadRewardedInterstitialAd();
                        if(GOT_IREWARD) {
                            int adcoin = settings.getInt(TankActivity.AD_COIN,0);
                            adcoin += 2;
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putInt(TankActivity.AD_COIN,adcoin);
                            editor.apply();
                            SoundManager.playSound(Sounds.TANK.EARN_GOLD);
                            String msg = String.format(Locale.ENGLISH, "Got %s Ad coins", TankActivity.this.getResources().getString(R.string.adCoin_bonus));
                            Toast.makeText(TankActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                        if(TankView.EVENT != TankView.PAUSE) {
                            mTankView.resumeNoAds();
                        }
                    }
                });

        Activity activityContext = TankActivity.this;
        mRewardedInterstitialAd.show(
                activityContext,
                new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        // Handle the reward.
                        Log.d("Rewarded InterstitialAD", "The user earned the reward.");
//                        addCoins(rewardItem.getAmount());
                        GOT_IREWARD = true;
                    }
                });
    }


    public void showRewardedVideo(Dialog purchaseDialog) {
        showRewardedVideo(purchaseDialog,null);
    }
    public void showRewardedVideo(Dialog purchaseDialog, Dialog auxDialog) {

        if (mRewardedAd == null) {
            Log.d("TAG", "The rewarded ad wasn't ready yet.");
            return;
        }

        mRewardedAd.setFullScreenContentCallback(
                new FullScreenContentCallback() {
                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                        Log.d("Rewarded Ads", "onAdShowedFullScreenContent");
//                        Toast.makeText(TankActivity.this, "onAdShowedFullScreenContent", Toast.LENGTH_SHORT)
//                                .show();
                        GOT_REWARD = false;
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when ad fails to show.
                        Log.d("Rewarded Ads", "onAdFailedToShowFullScreenContent");
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        mRewardedAd = null;
                        GOT_REWARD = false;
//                        Toast.makeText(
//                                TankActivity.this, "onAdFailedToShowFullScreenContent", Toast.LENGTH_SHORT)
//                                .show();
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        mRewardedAd = null;
                        Log.d("Rewarded Ads", "onAdDismissedFullScreenContent");
//                        Toast.makeText(TankActivity.this, "onAdDismissedFullScreenContent", Toast.LENGTH_SHORT)
//                                .show();
                        // Preload the next rewarded ad.
                        if(GOT_REWARD) {
                            SharedPreferences.Editor editor = settings.edit();
                            if(purchaseDialog instanceof TankPurchaseDialog) {
                                int goldCount = settings.getInt(TankActivity.GOLD, 0);
                                int amount = Integer.parseInt((TankActivity.this.getResources().getString(R.string.vidGold).replace("x", "")));
                                ((TankPurchaseDialog) purchaseDialog).goldTxt.setText(String.format("x%s", goldCount + amount));
                                editor.putInt(TankActivity.GOLD, goldCount + amount);
                                editor.apply();
                                SoundManager.playSound(Sounds.TANK.EARN_GOLD);
                            }
                            else if(purchaseDialog instanceof TankEndGameDialog) {
                                TankView.CHECKING_RETRY = 2;
//                                mTankView.updateP1Lives(1);
                                mTankView.updateP1Lives(Integer.parseInt(TankActivity.this.getResources().getString(R.string.retryWatchAmnt).replace("x","")));
//
                            }

                            else if(purchaseDialog instanceof TankPurchaseGameDialog) {
                                int retryCount = settings.getInt(TankActivity.RETRY_COUNT, 0);
                                int amnt = Integer.parseInt(((TankPurchaseGameDialog) purchaseDialog).getContext().getResources().getString(R.string.adGameAmnt).replace("+", ""));
                                ((TankPurchaseGameDialog) purchaseDialog).gameCountTxt.setText(String.valueOf(retryCount + amnt));
                                editor.putInt(TankActivity.RETRY_COUNT, retryCount + 1);
                                editor.apply();
                                SoundManager.playSound(Sounds.TANK.EARN_GOLD);
                            }

                            if(auxDialog instanceof TankEndGameDialog) {
                                ((TankEndGameDialog)auxDialog).setGoldCount();
                            }
                        }
                        TankActivity.this.loadRewardedAd();
                    }
                });
        Activity activityContext = TankActivity.this;
        mRewardedAd.show(
                activityContext,
                new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        // Handle the reward.
                        Log.d("Rewarded Ads", "The user earned the reward.");
                        int rewardAmount = rewardItem.getAmount();
                        String rewardType = rewardItem.getType();
                        GOT_REWARD = true;

                    }
                });
    }


}