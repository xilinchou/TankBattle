package com.gamecentre.tankbattle.tank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.gamecentre.tankbattle.R;
import com.gamecentre.tankbattle.billing.TransactionManager;
import com.gamecentre.tankbattle.connection.ClientConnectionThread;
import com.gamecentre.tankbattle.connection.ServerConnectionThread;
import com.gamecentre.tankbattle.model.Game;
import com.gamecentre.tankbattle.model.TankGameModel;
import com.gamecentre.tankbattle.sound.SoundManager;
import com.gamecentre.tankbattle.sound.Sounds;
import com.gamecentre.tankbattle.utils.CONST;
import com.gamecentre.tankbattle.utils.MessageRegister;
import com.gamecentre.tankbattle.utils.RemoteMessageListener;
import com.gamecentre.tankbattle.utils.WifiDialogListener;
import com.gamecentre.tankbattle.wifidirect.WifiDialog;
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

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class TankMenuActivity extends AppCompatActivity implements WifiDialogListener, ServiceListener, RemoteMessageListener {

//    private ActivityMainBinding binding;
    private BillingClient billingClient;
    private ProductDetails productDetails;
    private Purchase purchase;

    static final String TAG = "InAppPurchaseTag";



    TankTextView grenadeTxt, helmetTxt, clockTxt, shovelTxt, tankTxt,starTxt, gunTxt, boatTxt, goldTxt, retryTxt, retryTmr, adCoinTxt;
    ImageView shopImg, retryImg, inviteBtn;
    ObjectAnimator animateInviteBtn;
    boolean firstTime = true;

    TankTextView inviteTxt;
    private AdView mAdView;
    private InterstitialAd interstitialAd;
    private RewardedInterstitialAd mRewardedInterstitialAd;
    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712";

    private RewardedAd mRewardedAd;
    private static final String RAD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917";
    private static final String RIAD_UNIT_ID = "ca-app-pub-3940256099942544/5354046379";
    boolean isLoading;
    boolean isLoadingIntAds;
    public static boolean GOT_REWARD = false;
    public static boolean GOT_IREWARD = false;

    private boolean opened = false;

    SharedPreferences settings;

    static Intent intent = null;

    public static final String TWO_PLAYERS = "two players";
    public static final String
            PREF_MUTED = "muted",
            PREF_VIBRATE = "vibrate",
            PREF_LEVEL = "level",

            STAGE_NAMES = "STAGE_NAMES";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tank_menu);
        TransactionManager.getInstnce().billingSetup(this);
        MessageRegister.getInstance().setMsgListener(this);
        settings = getSharedPreferences("TankSettings", 0);

        goldTxt = findViewById(R.id.goldCountTxt);
        shopImg = findViewById(R.id.shop);
        retryImg = findViewById(R.id.gameImg);
        retryTxt = findViewById(R.id.retryTxt);
        retryTmr = findViewById(R.id.menuRetryTmrTxt);
        adCoinTxt = findViewById(R.id.adCoinTxt);

        updateStore();

        if(intent == null) {
            intent = getIntent();
        }
        Bundle b = intent.getExtras();
        String tankType = b.getString(TankTypeActivity.TANK_TYPE, "");

        ((TankTextView)findViewById(R.id.tanktype)).setText(tankType);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

//        loadAd();
        loadRewardedAd();
        loadRewardedInterstitialAd();



        shopImg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    clickAnimate(view);
                    SoundManager.playSound(Sounds.TANK.CLICK);
                    new Handler().postDelayed(() -> {
                        openStore();
                    },300);
                }
                return false;
            }
        });


        MessageRegister.getInstance().setwifiDialogListener(this);
        setListeners();
        inviteTxt = (TankTextView) findViewById(R.id.ivName);
        inviteTxt.setSelected(true);

        int newDay = checkNewDay();
        Log.d("DATE CHECK", String.valueOf(newDay));
        if(newDay > 0 && firstTime){
            openReward(newDay);
        }

        MessageRegister.getInstance().setServiceListener(this);
        opened = true;

        inviteBtn = findViewById(R.id.inviteBtn);

        animateInviteBtn = ObjectAnimator.ofPropertyValuesHolder(
                inviteBtn,
                PropertyValuesHolder.ofFloat("scaleX", 1.5f),
                PropertyValuesHolder.ofFloat("scaleY", 1.5f)
        );
        animateInviteBtn.setDuration(500);
        animateInviteBtn.setRepeatMode(ValueAnimator.REVERSE);
        animateInviteBtn.setRepeatCount(1);

        SoundManager.playSound(Sounds.TANK.GAME_BACKGROUND,true);
    }

    public void clickAnimate(View v) {
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


    protected void onResume() {
        super.onResume();
        opened = true;
        SoundManager.resumeGameSounds();
    }

    protected void onStop() {
        super.onStop();
    }

    protected void onPause() {
        super.onPause();
        SoundManager.pauseGameSounds();
    }


    @Override
    protected void onDestroy() {
        opened = false;
        super.onDestroy();
    }

    public void onServiceMessageReceived(int games, long time_left, boolean h6) {
        if(opened){
            SimpleDateFormat sdf = new SimpleDateFormat("mm:ss", Locale.ENGLISH);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            retryTxt.setText(String.valueOf(games));
            if(games >= CONST.Tank.MAX_GAME_COUNT && !h6) {
                Log.d("SERVICE MESSAGE MENU", String.valueOf(games) + " " + time_left + " false");
                retryTmr.setText("");
                retryImg.setBackground(ResourcesCompat.getDrawable(this.getResources(),R.drawable.retry_img,null));
            }
            else if(h6) {
                Log.d("SERVICE MESSAGE MENU", String.valueOf(games) + " " + time_left + " true");
                sdf = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                retryTmr.setText(sdf.format(time_left));
//                retryTxt.setText("");
                retryImg.setBackground(ResourcesCompat.getDrawable(this.getResources(),R.drawable.game6h,null));
            }
            else {
                Log.d("SERVICE MESSAGE MENU", String.valueOf(games) + " " + time_left + " false");
                retryTmr.setText(sdf.format(time_left));
                retryImg.setBackground(ResourcesCompat.getDrawable(this.getResources(),R.drawable.retry_img,null));
            }
        }
    }

    @Override
    public void onMessageReceived(Game message) {
        if(message instanceof TankGameModel) {
            TankGameModel msg = (TankGameModel)message;

            if(WifiDirectManager.getInstance().isServer() && ServerConnectionThread.serverStarted)
            {
                if (msg.playerInfo) {
                    TankStageDialog.p2Ready = msg.playerReady;
                    Log.d("Msg From P2", "Ready: " +" "+ TankStageDialog.p2Ready);
                }
            }

        }
    }

    public int checkNewDay() {
//        Date date = null;
//        String str = "Jul 30 2003 23:11:52.454 UTC";
//        SimpleDateFormat df = new SimpleDateFormat("MMM dd yyyy HH:mm:ss.SSS zzz", Locale.ENGLISH);
//        try{
//            date = df.parse(str);
//        }
//        catch (ParseException e) {
//            Log.d("DATE CHECK", "PARSE EXCEPTION");
//            return 0;
//        }
//        long epoch = date.getTime();


        long newDay;
        int numDays = 0;
        long lastDay = settings.getLong(TankActivity.LAST_DAY,0);
        long currentDay = (long) (System.currentTimeMillis() / 86400000);
//        long currentDay = (long) (epoch / 86400000);

        newDay = currentDay - lastDay;

        numDays = settings.getInt(TankActivity.CONSECUTIVE_DAYS,0);

        if(newDay == 1) {
            numDays++;
            if(numDays > 7) {
                numDays = 1;
            }
        }
        else if(newDay > 1) {
            numDays = 1;
        }
        else{
            firstTime = false;
        }

        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(TankActivity.LAST_DAY,currentDay);
        editor.putInt(TankActivity.CONSECUTIVE_DAYS,numDays);
        editor.apply();

        return numDays;
    }

    public void openReward(int day) {
        TankDailyRewardDialog wd = new TankDailyRewardDialog(TankMenuActivity.this, day);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(wd.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        wd.show();
        wd.getWindow().setAttributes(lp);
    }


    public void openStore() {
        TankPurchaseDialog wd = new TankPurchaseDialog(TankMenuActivity.this);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(wd.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        wd.show();
        wd.getWindow().setAttributes(lp);
    }


    public void updateStore() {
        goldTxt.setText(String.valueOf(settings.getInt(TankActivity.GOLD,3)));
        retryTxt.setText(String.valueOf(settings.getInt(TankActivity.RETRY_COUNT,5)));
        adCoinTxt.setText(String.valueOf(settings.getInt(TankActivity.AD_COIN,0)));
    }

    protected void setListeners () {
        this.findViewById(R.id.tnkP1menu)
                .setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent m) {
                        if(m.getAction() == MotionEvent.ACTION_DOWN) {
                            clickAnimate(v);
                            SoundManager.playSound(Sounds.TANK.CLICK);
                            new Handler().postDelayed(() -> {
                                openStages(v, false);
                            },300);
                        }
                        return true;
                    }
                });

        this.findViewById(R.id.tnkP2menu)
                .setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent m) {
                        if(m.getAction() == MotionEvent.ACTION_DOWN) {
                            clickAnimate(v);
                            SoundManager.playSound(Sounds.TANK.CLICK);
                            new Handler().postDelayed(() -> {
                                if ((WifiDirectManager.getInstance().isServer() && ServerConnectionThread.serverStarted) ||
                                        (!WifiDirectManager.getInstance().isServer() && ClientConnectionThread.serverStarted)) {
                                    openStages(v, true);
                                } else {
                                    //TODO Change toast to dialog
                                    SoundManager.playSound(Sounds.TANK.CLICK2);
                                    Toast toast = Toast.makeText(TankMenuActivity.this.getApplicationContext(),
                                            "Invite a player first",
                                            Toast.LENGTH_SHORT);

                                    ViewGroup group = (ViewGroup) toast.getView();
                                    TextView messageTextView = (TextView) group.getChildAt(0);
                                    messageTextView.setTextSize(20);

                                    toast.show();
                                    animateInviteBtn.start();
                                }
                            },300);
                        }
                        return true;
                    }
                });

        this.findViewById(R.id.tnkConstmenu)
                .setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent m) {
                        if(m.getAction() == MotionEvent.ACTION_DOWN) {
                            clickAnimate(v);
                            SoundManager.playSound(Sounds.TANK.CLICK);
                            new Handler().postDelayed(()->{
                                Log.d("Construction", "Opening fragment");
                                FragmentManager fragmentManager = getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.fragmentFrame,new ConstructionFragment(TankMenuActivity.this));
                                fragmentTransaction.addToBackStack("cFragment");
                                fragmentTransaction.commit();
                            },300);
                        }
                        return true;
                    }
                });

        this.findViewById(R.id.tnkExitmenu)
                .setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent m) {
                        if(m.getAction() == MotionEvent.ACTION_DOWN) {
                            clickAnimate(v);
                            SoundManager.playSound(Sounds.TANK.CLICK);
                            new Handler().postDelayed(()->{
                                SoundManager.stopGameSounds();
                                Intent i = new Intent(TankMenuActivity.this, TankTypeActivity.class);
                                TankMenuActivity.this.startActivity(i);
                                TankMenuActivity.this.finish();
                            },300);
                        }
                        return true;
                    }
                });

//        this.findViewById(R.id.settingsBtn)
//                .setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        openSettings(view);
//                    }
//                });

        this.findViewById(R.id.inviteBtn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SoundManager.playSound(Sounds.TANK.CLICK);

//                        showInterstitial();
//                        if(CheckAdd.getInstance().click()) {
                            showRewardedInterstitialAd(false);
//                        }
                    }
                });
    }

    public void startGame(boolean twoPlayers) {
        SoundManager.stopSound(Sounds.TANK.GAME_BACKGROUND);
        Intent i = new Intent(this, TankActivity.class);
        i.putExtra(TankMenuActivity.TWO_PLAYERS, twoPlayers);
        startActivity(i);
        finish();
    }

//    public void openSettings(View view) {
//        TankSettingsDialog cdd = new TankSettingsDialog(this);
//
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(cdd.getWindow().getAttributes());
//        cdd.show();
//        cdd.getWindow().setAttributes(lp);
//    }


    public void openStages(View view, boolean twoPlayers) {
//        TankStageDialog cdd = new TankStageDialog(this, twoPlayers);
//
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(cdd.getWindow().getAttributes());
//        cdd.show();
//        cdd.getWindow().setAttributes(lp);


        Log.d("Stage Fragment", "Opening fragment");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentFrame,new TankStageFragment(this, twoPlayers));
        fragmentTransaction.addToBackStack("cFragment");
        fragmentTransaction.commit();
    }

    public void openPlayerSearchView() {
        WifiDirectManager.getInstance().initialize(TankMenuActivity.this);
        WifiDirectManager.getInstance().registerBReceiver();

        WifiDialog wd = new WifiDialog(TankMenuActivity.this);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(wd.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        wd.show();
        wd.getWindow().setAttributes(lp);
    }

    @Override
    public void onWifiDilogClosed() {
//        this.getView().update(true);
        if(WifiDirectManager.getInstance().isServer() && ServerConnectionThread.serverStarted) {
            findViewById(R.id.inviteBtn).setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.p1,null));
            inviteTxt = (TankTextView) findViewById(R.id.ivName);
            inviteTxt.setText(WifiDirectManager.getInstance().getDeviceName());
            inviteTxt.setSelected(true);

        }
        else if(!WifiDirectManager.getInstance().isServer() && ClientConnectionThread.serverStarted) {
            findViewById(R.id.inviteBtn).setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.p2,null));
            inviteTxt = (TankTextView) findViewById(R.id.ivName);
            inviteTxt.setText(WifiDirectManager.getInstance().getDeviceName());
            inviteTxt.setSelected(true);
        }
        else {
            inviteTxt.setText(R.string.invite_def_txt);
        }
    }

    public void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                this,
                AD_UNIT_ID,
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        TankMenuActivity.this.interstitialAd = interstitialAd;
                        Log.i("Interstitial Ad", "onAdLoaded");
//                        Toast.makeText(TankMenuActivity.this, "onAdLoaded()", Toast.LENGTH_SHORT).show();
                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        TankMenuActivity.this.interstitialAd = null;
                                        Log.d("TAG", "The ad was dismissed.");
                                        openPlayerSearchView();
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        TankMenuActivity.this.interstitialAd = null;
                                        Log.d("TAG", "The ad failed to show.");
                                        openPlayerSearchView();
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        // Called when fullscreen content is shown.
                                        Log.d("TAG", "The ad was shown.");
                                    }
                                });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("Interstitial Ad", loadAdError.getMessage());
                        interstitialAd = null;

                        String error =
                                String.format(
                                        "domain: %s, code: %d, message: %s",
                                        loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());
//                        Toast.makeText(TankMenuActivity.this, "onAdFailedToLoad() with error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (interstitialAd != null) {
            interstitialAd.show(this);
        } else {
//            Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show();
            openPlayerSearchView();
        }
    }


    void loadRewardedInterstitialAd() {
        if (mRewardedInterstitialAd == null) {
            isLoadingIntAds = true;

            AdRequest adRequest = new AdRequest.Builder().build();
            // Use the test ad unit ID to load an ad.
            RewardedInterstitialAd.load(
                    TankMenuActivity.this,
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
                        openPlayerSearchView();
                    }
                });
        dialog.show(getSupportFragmentManager(), "AdDialogFragment");
    }

    private void showRewardedInterstitialAd(boolean cancel) {
        if (mRewardedInterstitialAd == null) {
            Log.d("Rewarded InterstitialAD", "The rewarded interstitial ad is not ready.");
            openPlayerSearchView();
            return;
        }

//        RewardItem rewardItem = mRewardedInterstitialAd.getRewardItem();
//        int rewardAmount = rewardItem.getAmount();
//        String rewardType = rewardItem.getType();

        Log.d("Rewarded InterstitialAD", "The rewarded interstitial ad is ready.");
//        introduceVideoAd(2, "ADS Coins");
        if(cancel) {
            introduceVideoAd(2, "Ad coins");
        }
        else {
            showRewardedVideoIAD();
        }
    }

    private void showRewardedVideoIAD() {

        if (mRewardedInterstitialAd == null) {
            Log.d("Rewarded InterstitialAD", "The rewarded interstitial ad wasn't ready yet.");
            return;
        }

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
                    }

                    /** Called when ad showed the full screen content. */
                    @Override
                    public void onAdShowedFullScreenContent() {
                        SoundManager.pauseSound(Sounds.TANK.GAME_BACKGROUND);
                        Log.d("Rewarded InterstitialAD", "onAdShowedFullScreenContent");
                        GOT_IREWARD = false;

//                        Toast.makeText(TankMenuActivity.this, "onAdShowedFullScreenContent", Toast.LENGTH_SHORT)
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
                        openPlayerSearchView();

//                        Toast.makeText(
//                                        TankActivity.this, "onAdFailedToShowFullScreenContent", Toast.LENGTH_SHORT)
//                                .show();
                    }

                    /** Called when full screen content is dismissed. */
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        SoundManager.resumeSound(Sounds.TANK.GAME_BACKGROUND);
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        mRewardedInterstitialAd = null;
                        Log.d("Rewarded InterstitialAD", "onAdDismissedFullScreenContent");
//                        Toast.makeText(TankMenuActivity.this, "onAdDismissedFullScreenContent", Toast.LENGTH_SHORT)
//                                .show();
                        // Preload the next rewarded interstitial ad.
                        loadRewardedInterstitialAd();
                        if(GOT_IREWARD) {
                            int adcoin = settings.getInt(TankActivity.AD_COIN,0);
                            adcoin += 2;
                            adCoinTxt.setText(String.valueOf(adcoin));
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putInt(TankActivity.AD_COIN,adcoin);
                            editor.apply();
                            SoundManager.playSound(Sounds.TANK.EARN_GOLD);
                            String msg = String.format(Locale.ENGLISH, "Got %s Ad coins", TankMenuActivity.this.getResources().getString(R.string.adCoin_bonus));
                            Toast.makeText(TankMenuActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                        openPlayerSearchView();
                    }
                });

        Activity activityContext = TankMenuActivity.this;
        mRewardedInterstitialAd.show(
                activityContext,
                new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        // Handle the reward.
                        Log.d("Rewarded InterstitialAD", "The user earned the reward.");
                        GOT_IREWARD = true;
//                        addCoins(rewardItem.getAmount());
                    }
                });
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
                            TankMenuActivity.this.isLoading = false;
//                            Toast.makeText(TankMenuActivity.this, "onAdFailedToLoad", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            TankMenuActivity.this.mRewardedAd = rewardedAd;
                            Log.d("Rewarded Ads", "onAdLoaded");
                            TankMenuActivity.this.isLoading = false;
//                            Toast.makeText(TankMenuActivity.this, "onAdLoaded", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    public boolean showRewardedVideo(Dialog purchaseDialog) {

        if (mRewardedAd == null) {
            Log.d("TAG", "The rewarded ad wasn't ready yet.");
            return false;
        }

        mRewardedAd.setFullScreenContentCallback(
                new FullScreenContentCallback() {
                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                        Log.d("Rewarded Ads", "onAdShowedFullScreenContent");
//                        Toast.makeText(TankMenuActivity.this, "onAdShowedFullScreenContent", Toast.LENGTH_SHORT).show();
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
//                                TankMenuActivity.this, "onAdFailedToShowFullScreenContent", Toast.LENGTH_SHORT)
//                                .show();
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        mRewardedAd = null;
                        Log.d("Rewarded Ads", "onAdDismissedFullScreenContent");
//                        Toast.makeText(TankMenuActivity.this, "onAdDismissedFullScreenContent", Toast.LENGTH_SHORT)
//                                .show();
                        // Preload the next rewarded ad.
                        if(GOT_REWARD) {
                            SharedPreferences.Editor editor = settings.edit();
                            if(purchaseDialog instanceof TankPurchaseDialog) {
                                int goldCount = settings.getInt(TankActivity.GOLD, 0);
                                int amount = Integer.parseInt((TankMenuActivity.this.getResources().getString(R.string.vidGold).replace("x", "")));
                                ((TankPurchaseDialog) purchaseDialog).goldTxt.setText(String.format("%s", goldCount + amount));
                                editor.putInt(TankActivity.GOLD, goldCount + amount);
                                editor.apply();
                                SoundManager.playSound(Sounds.TANK.EARN_GOLD);
                            }
                            else if(purchaseDialog instanceof TankPurchaseGameDialog) {
                                int retryCount = settings.getInt(TankActivity.RETRY_COUNT, 0);
                                int amnt = Integer.parseInt(((TankPurchaseGameDialog) purchaseDialog).getContext().getResources().getString(R.string.adGameAmnt).replace("+", ""));
//                                ((TankPurchaseGameDialog) purchaseDialog).gameCountTxt.setText(String.format("x%s", retryCount + amnt));
                                editor.putInt(TankActivity.RETRY_COUNT, retryCount + 1);
                                editor.apply();
                                SoundManager.playSound(Sounds.TANK.EARN_GOLD);
                            }

                            if(purchaseDialog instanceof TankDailyRewardDialog) {
//                                int goldCount = settings.getInt(TankActivity.GOLD, 0);
//                                ((TankPurchaseDialog) purchaseDialog).goldCountTxt.setText(String.format("x%s", goldCount + 1));
//                                editor.putInt(TankActivity.GOLD, goldCount + 1);
//                                editor.apply();
//                                SoundManager.playSound(Sounds.TANK.EARN_GOLD);
                                ((TankDailyRewardDialog) purchaseDialog).setDoubleReward();
                            }
                        }
                        TankMenuActivity.this.loadRewardedAd();
                    }
                });
        Activity activityContext = TankMenuActivity.this;
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

        return true;
    }
}