package com.gamecentre.tankbattle.tank;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.core.content.res.ResourcesCompat;

import com.gamecentre.tankbattle.model.Game;
import com.gamecentre.tankbattle.model.TankGameModel;
import com.gamecentre.tankbattle.sound.SoundManager;
import com.gamecentre.tankbattle.sound.Sounds;
import com.gamecentre.tankbattle.utils.ButtonListener;
import com.gamecentre.tankbattle.utils.CONST;
import com.gamecentre.tankbattle.utils.MessageRegister;
import com.gamecentre.tankbattle.R;
import com.gamecentre.tankbattle.utils.RemoteMessageListener;
import com.gamecentre.tankbattle.wifidirect.WifiDirectManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;


public class TankView extends View implements RemoteMessageListener, ButtonListener {

    /** Debug tag */
    @SuppressWarnings("unused")
    private static final String TAG = "TankView";
    private long refSendStartTime = System.currentTimeMillis();
    private long refReceiveStartTime = System.currentTimeMillis();
    protected static final int FPS = 25;
    public static final float TO_SEC = 1000f/FPS;
    public static boolean CONSTRUCTION = false;
    public static final int
            STAGE_COMPLETE = 1,
            GAME_OVER = 2,
            PAUSE = 3,
            END_GAME = 4,
            RESUME = 5,
            RESTART = 6,
            GIFT_LIFE = 7;

    public static int GOLD_LEVEL = 0;

    public static int EVENT = 0;

    public static int CHECKING_RETRY = 0;

    private TankView.State mCurrentState = TankView.State.Running;
    private TankView.State mLastState = TankView.State.Stopped;
    private boolean started = false;
    public static Context context;

    public static TankView instance;

    public static int WIDTH;
    public static int HEIGHT;
    public static float SCALE;
    public static float RESIZE = 1;
    public static Bitmap graphics;
    public static final int NUM_LEVELS = 35;
    public static final int NUM_OBJECTIVES = 11;
    private final float LPROB = 0.6f/NUM_LEVELS;
    public static int level = 0;
    private boolean enemyBoat = false;

//    private int sW, sH;

    public static ArrayList<ArrayList<ImageView>> enemyCount;




    public static enum State { Running, Stopped}

    /** Flag that marks this view as initialized */
    private boolean mInitialized = false;
    private boolean should_end = false;
    private boolean round_started = false;

    protected boolean updatingRemote = false;
    protected boolean updatingGame = false;
    protected boolean drawing = false;
    protected boolean notifyStageComplete = false;
    protected boolean notifyGameOver = false;
    protected boolean notifyPause = false;
    public boolean notifyEndGame = false;
    public boolean notifyRetryStage = false;
    public boolean notifyGiftLife = false;
    public boolean notifyReceivedLife = false;

    /** Preferences loaded at startup */
    private int mTankSpeedModifier;

    /** Lives modifier */
    private int mLivesModifier;

    /** Starts a new round when set to true */
    public static boolean mNewRound = true;

    /** Keeps the game thread alive */
    private boolean mContinue = true;
    private boolean mEndGame = true;

    /** Mutes sounds when true */
    private boolean mSound = false;
    private static boolean mVibrate = false;
    private static Vibrator mVibrator;

    private Player P2, P1;
    private Eagle eagle;
    public static ArrayList<Bitmap> bombBitmap;
    public static Bitmap mineBitmap;
    public static Sprite bombSprite;
    public static Bitmap[][] fireBitmap;
    public static Sprite fireSprite;
    Bomb bomb;
    public static ArrayList<ArrayList<Bitmap>> tankBitmap;
    public static Sprite tankSprite;
    public Drawable eCountImg;
    public Bitmap eCountBm;
    private ArrayList<Enemy> Enemies;
    private TankGameModel sendModel;
    private ArrayList<ArrayList<GameObjects>> levelObjects;
    ArrayList<int[]> levelObjectsUpdate;
    private ArrayList<Bush> levelBushes;
    private ArrayList<Integer> levelBushesUpdate;
    public static Bonus bonus;
    public static Gold gold;
    private int new_enemy_time = 0;
    private final int genEnemyTime = 1*FPS;
    private final int MAX_ENEMIES = 6;
    private final int MAX_HVE = 8;
    private boolean new_hve = false;
    private Point hve_pos;
    private int NUM_HVE, HVE_LIVES;
    public static int SCENE_SOUND = -1;
    private final int[][] eaglePos = {{11,25},{11,24},{11,23},{12,23},{13,23},{14,23},{14,24},{14,25}};

    private  Bitmap curtain;
    public static int tile_dim;
    private int curtainFrame = 0;
    private int curtainFrameTmr = 0;
    private final int curtainFrameTime = (int)(0.01*FPS);
    private boolean closingCurtain = false;
    private boolean openingCurtain = false;
    private boolean movingCurting = true;
    private boolean curtainPause = false;
    private int curtainPauseTmr;
    private final int curtainPauseTime = (int)(0.5*FPS);
    Paint curtainPaint = new Paint();
    Rect curtainTRect;
    Rect curtainBRect;

    public static boolean gameover = false;
    public static boolean stageComplete = false;

    private int scoreFrame;
    private int enemyFrame;
    private int scoreFrameTmr;
    private int scoreFrameTime = (int)(0.1*FPS);
    public boolean showingScore = false;

    private int showScoreDelay = (int)(5*FPS);
    private int showScoreTmr = 0;

    private int newStageTmr = (int)(2*FPS);


    public static ConcurrentLinkedQueue<Game> gameModel;


    public static boolean freeze = false;
    public static int freezeTmr = 0;
    public static int FreezeTime = (int)(8*TankView.FPS);

    public static boolean protectEagle = false;
    public static int protectEagleTmr = 0;
    public static int ProtectEagleTime = (int)(20*TankView.FPS);

    public static boolean ENEMY_BOOST = true;


    private boolean drawStarted = false;
    private boolean startSound = false;
    private boolean playerReady = false;

    public static Typeface typeface;
    public static Paint txtPaint;

    public ArrayList<boolean[]> objectives;
    public boolean[] currentObj;
    public int numStars = 0;

    public static long GameStartTime;

    /** Touch boxes for various functions. These are assigned in initialize() */
    private Rect mPauseTouchBox;

    /** Timestamp of the last frame created */
    private long mLastFrame = 0;

//    protected ArrayList<TankView.Bullet> bullets = new ArrayList<>();

    /** Random number generator */
    private static final Random RNG = new Random();

    /** Pool for our sound effects */
    protected SoundPool mPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);

    protected int mWinSFX, mMissSFX, mPaddleSFX, mWallSFX, mShootSFX, mHitSFX, mBrickSFX;

    /** Paint object */
    private final Paint mPaint = new Paint();

    /** Padding for touch zones and paddles */
    private static final int PADDING = 3;

    /** Redraws the screen according to FPS */
    private TankView.RefreshHandler mRedrawHandler = null;

//    private TankView.RemoteUpdateHandler mRemoteUpdateHandler = new TankView.RemoteUpdateHandler();

    /** Flags indicating who is a player */
    public static boolean twoPlayers = false;

    /**
     * An overloaded class that repaints this view in a separate thread.
     * Calling PongView.update() should initiate the thread.
     * @author OEP
     *
     */
    class RefreshHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            boolean done = false;
            while(twoPlayers && updatingRemote){
                sleep(1);
            };
            updatingGame = true;
            TankView.this.update();
            updatingGame = false;
            TankView.this.invalidate(); // Mark the view as 'dirty'


//            new Handler(Looper.getMainLooper()).postDelayed(new Runnable(){
//                @Override
//                public void run() {
//                    TankView.this.invalidate(); // Mark the view as 'dirty'
//                }
//            },5);
        }

        public void sleep(long delay) {
            this.removeMessages(0);
            this.sendMessageDelayed(obtainMessage(0), delay);
        }
    }


    class RemoteUpdateHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            while(true) {
//                if (gameModel != null && !gameModel.isEmpty()) {
//                    updatingRemote = true;
//                    TankView.this.getRemoteUpdate();
//                    updatingRemote = false;
//                    TankView.this.invalidate();
//                }
                if (gameModel != null && !gameModel.isEmpty() && !drawing && !updatingGame) {
                    updatingRemote = true;
                    TankView.this.getRemoteUpdate();
                    updatingRemote = false;
                    TankView.this.invalidate();
//                    ((TankActivity)context).runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            TankView.this.invalidate();
//                        }
//                    });
                }
            }
        }
    }

    Thread tRemoteHandler = new Thread() {
        @Override
        public void run(){
            while (!should_end){
                //we can't update the UI from here so we'll signal our handler and it will do it for us.
//                mRemoteUpdateHandler.sendMessage(null);
                if (gameModel != null && !gameModel.isEmpty() && !drawing && !updatingGame) {

                    updatingRemote = true;
                    TankView.this.getRemoteUpdate();
                    updatingRemote = false;

                    ((TankActivity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TankView.this.invalidate();
                        }
                    });
                }
            }
        }
    };

    /**
     * Creates a new PongView within some context
     * @param context
     * @param attrs
     */
    public TankView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TankView.this.context = context;
        instance = this;
        constructView();
        mNewRound = true;
    }

    public TankView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        instance = this;
        constructView();
        mNewRound = true;
    }

    public static TankView getInstance() {
        return instance;
    }

    public Context getTankViewContext() {
        return context;
    }

    /**
     * Set the paddles to their initial states and as well the ball.
     */
    private void constructView() {

        setFocusable(true);
        MessageRegister.getInstance().setMsgListener(this);
        MessageRegister.getInstance().setButtonListener(this);

        Context ctx = this.getContext();
        SharedPreferences settings = ctx.getSharedPreferences("TankSettings", 0);
        loadPreferences(settings);

    }

    private  void loadGameObjects() {
        SpriteObjects.getInstance().insert(ObjectType.ST_TANK, 0, 0, 32, 32, 2, 10, true);

        SpriteObjects.getInstance().insert(ObjectType.ST_TANK_A, 128, 0, 32, 32, 2, 10, true);
        SpriteObjects.getInstance().insert(ObjectType.ST_TANK_B, 128, 64, 32, 32, 2, 10, true);
        SpriteObjects.getInstance().insert(ObjectType.ST_TANK_C, 128, 128, 32, 32, 2, 10, true);
        SpriteObjects.getInstance().insert(ObjectType.ST_TANK_D, 128, 192, 32, 32, 2, 10, true);

        SpriteObjects.getInstance().insert(ObjectType.ST_PLAYER_1, 640, 0, 32, 32, 2, 10, true); //50
        SpriteObjects.getInstance().insert(ObjectType.ST_PLAYER_2, 768, 0, 32, 32, 2, 10, true);

        SpriteObjects.getInstance().insert(ObjectType.ST_BRICK_WALL, 928, 0, 16, 16, 1, 200, false);
        SpriteObjects.getInstance().insert(ObjectType.ST_STONE_WALL, 928, 144, 16, 16, 1, 200, false);
        SpriteObjects.getInstance().insert(ObjectType.ST_WATER, 928, 160, 16, 16, 2, 10, true);
        SpriteObjects.getInstance().insert(ObjectType.ST_BUSH, 928, 192, 16, 16, 1, 200, false);
        SpriteObjects.getInstance().insert(ObjectType.ST_ICE, 928, 208, 16, 16, 1, 200, false);

        SpriteObjects.getInstance().insert(ObjectType.ST_BONUS_GRENADE, 896, 0, 32, 32, 1, 200, false);
        SpriteObjects.getInstance().insert(ObjectType.ST_BONUS_HELMET, 896, 32, 32, 32, 1, 200, false);
        SpriteObjects.getInstance().insert(ObjectType.ST_BONUS_CLOCK, 896, 64, 32, 32, 1, 200, false);
        SpriteObjects.getInstance().insert(ObjectType.ST_BONUS_SHOVEL, 896, 96, 32, 32, 1, 200, false);
        SpriteObjects.getInstance().insert(ObjectType.ST_BONUS_TANK, 896, 128, 32, 32, 1, 200, false);
        SpriteObjects.getInstance().insert(ObjectType.ST_BONUS_STAR, 896, 160, 32, 32, 1, 200, false);
        SpriteObjects.getInstance().insert(ObjectType.ST_BONUS_GUN, 896, 192, 32, 32, 1, 200, false);
        SpriteObjects.getInstance().insert(ObjectType.ST_BONUS_BOAT, 896, 224, 32, 32, 1, 200, false);

        SpriteObjects.getInstance().insert(ObjectType.ST_SHIELD, 976, 0, 32, 32, 2, 2, true);
        SpriteObjects.getInstance().insert(ObjectType.ST_CREATE, 1008, 0, 32, 32, 10, 1, false);
        SpriteObjects.getInstance().insert(ObjectType.ST_DESTROY_TANK, 1040, 0, 64, 64, 7, 1, false);//70
        SpriteObjects.getInstance().insert(ObjectType.ST_DESTROY_BULLET, 1108, 0, 32, 32, 5, 1, false); //40
        SpriteObjects.getInstance().insert(ObjectType.ST_BOAT_P1, 944, 96, 32, 32, 1, 200, false);
        SpriteObjects.getInstance().insert(ObjectType.ST_BOAT_P2, 976, 96, 32, 32, 1, 200, false);

        SpriteObjects.getInstance().insert(ObjectType.ST_EAGLE, 944, 0, 32, 32, 1, 200, false);
        SpriteObjects.getInstance().insert(ObjectType.ST_DESTROY_EAGLE, 1040, 0, 64, 64, 7, 1, false); //100
        SpriteObjects.getInstance().insert(ObjectType.ST_FLAG, 944, 64, 16, 16, 1, 200, false);

        SpriteObjects.getInstance().insert(ObjectType.ST_BULLET, 944, 128, 8, 8, 1, 200, false);

        SpriteObjects.getInstance().insert(ObjectType.ST_LEFT_ENEMY, 944, 144, 16, 16, 1, 200, false);
        SpriteObjects.getInstance().insert(ObjectType.ST_STAGE_STATUS, 976, 64, 32, 32, 1, 200, false);

        SpriteObjects.getInstance().insert(ObjectType.ST_TANKS_LOGO, 0, 260, 406, 72, 1, 200, false);

        SpriteObjects.getInstance().insert(ObjectType.ST_CURTAIN, 928, 224, 16, 16, 1, 200, false);

        SpriteObjects.getInstance().insert(ObjectType.ST_BOMB, 0, 0, 16, 16, 3, 2, true);

        SpriteObjects.getInstance().insert(ObjectType.ST_FIRE, 0, 0, 16, 16, 4, 1, true);

        SpriteObjects.getInstance().insert(ObjectType.ST_MINE, 0, 0, 32, 32, 4, 1, true);



    }

    protected void loadPreferences(SharedPreferences prefs) {

        mSound = prefs.getBoolean(TankMenuActivity.PREF_MUTED, mSound);
        SoundManager.enableSound(mSound);

        mVibrate = prefs.getBoolean(TankMenuActivity.PREF_VIBRATE, mVibrate);
        mVibrator = (Vibrator) (context.getSystemService(Context.VIBRATOR_SERVICE));

    }

    private void loadLevel(int level) {
        levelObjects = new ArrayList<>();
        levelBushes = new ArrayList<>();
        gold = new Gold();

        if(twoPlayers) {
            levelBushesUpdate = new ArrayList<>();
        }
        if(twoPlayers) {
            levelObjectsUpdate = new ArrayList<>();
        }
        BufferedReader reader;
        int row_count = 0;
        try {
            InputStream inputStream = context.getAssets().open(String.valueOf(level));
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = reader.readLine();

            while(line != null){
                ArrayList<GameObjects> row = new ArrayList<>();
                Log.d("LEVEL", line);
                for (int i = 0; i < line.length(); i++){
                    GameObjects obj;
                    char c = line.charAt(i);
                    switch (c){
                        case '#' :
                            obj = new Brick(i,row_count);
                            break;
                        case '@' :
                            obj = new StoneWall(i,row_count);
                            break;
                        case '%' :
                            levelBushes.add(new Bush(i,row_count));
                            obj =  null;
                            break;
                        case '~' :
                            obj = new Water(i,row_count);
                            break;
                        case '-' :
                            obj = new Ice(i,row_count);
                            break;
                        default: obj = null;
                    }
                    row.add(obj);
                }
                levelObjects.add(row);
                ++row_count;
                line = reader.readLine();
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }

//        Log.d("LOADING LEVEL", String.valueOf(levelObjects.size()) + " " + levelObjects.get(0).size());

        eagle = new Eagle();

    }

    private void loadConstructionLevel(int level) {
        levelObjects = new ArrayList<>();
        levelBushes = new ArrayList<>();
        gold = new Gold();

//        level = level-1;

        ArrayList<String>stageNames = loadStageNames();
        if(level > stageNames.size()) {
            level = 1;
        }
        String stageName = stageNames.get(level-1);
        char[][] stage = loadStage(stageName);

        if(twoPlayers) {
            levelBushesUpdate = new ArrayList<>();
        }
        if(twoPlayers) {
            levelObjectsUpdate = new ArrayList<>();
        }
//        BufferedReader reader;
//        int row_count = 0;
        try {
//            InputStream inputStream = context.getAssets().open(String.valueOf(level));
//            reader = new BufferedReader(new InputStreamReader(inputStream));
//            String line = reader.readLine();

            for (int row = 0; row < 26; row++){
                ArrayList<GameObjects> rowObj = new ArrayList<>();
//                Log.d("LEVEL", line);
                for (int col = 0; col < 26; col++){
                    GameObjects obj;
//                    char c = line.charAt(i);
                    switch (stage[row][col]){
                        case '#' :
                            obj = new Brick(col,row);
                            break;
                        case '@' :
                            obj = new StoneWall(col,row);
                            break;
                        case '%' :
                            levelBushes.add(new Bush(col,row));
                            obj =  null;
                            break;
                        case '~' :
                            obj = new Water(col,row);
                            break;
                        case '-' :
                            obj = new Ice(col,row);
                            break;
                        default: obj = null;
                    }
                    rowObj.add(obj);
                }
                levelObjects.add(rowObj);
//                ++row_count;
//                line = reader.readLine();
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }

//        Log.d("LOADING LEVEL", String.valueOf(levelObjects.size()) + " " + levelObjects.get(0).size());
        for (int[] eaglePo : eaglePos) {
            levelObjects.get(eaglePo[1]).set(eaglePo[0], new Brick(eaglePo[0], eaglePo[1]));
        }
        eagle = new Eagle();

    }

    private void initializeGame() {
        SCALE = getResources().getDisplayMetrics().density;

        ViewGroup.LayoutParams params = TankView.this.getLayoutParams();
//        int dimH = (int)(((TankActivity)context).gameView.getHeight()*0.95);
        int dimW = (int)(((TankActivity)context).gameView.getWidth()*0.9);
        int dimH = (int)(TankView.this.getHeight()*0.95);
//        int dimW = (int)(TankView.this.getWidth()*0.95);
        Log.d("LAYOOUT", String.valueOf(dimH) + " " + dimW);
        int dim = (int)Math.min(dimH,dimW*0.5);
        dim = (int)(dim/52f)*52;
        params.width = dim;
        params.height = dim;
//        TankView.this.setLayoutParams(params);
        TankView.this.layout(0,0,dim,dim);

        // Bonus frame

        ViewGroup.LayoutParams bonusLayout = ((TankActivity)context).bonusFrame.getLayoutParams();
        int bDim = (int)(((dimW - dim)*0.9/2));
        bDim = (int)(Math.min(bDim, dimH*0.45));
        int bmDim = (int)(bDim*.05);
        bonusLayout.width = bDim;
        bonusLayout.height = bDim;
        ((TankActivity)context).bonusFrame.layout(bmDim,bmDim,bDim,bDim);

        // Navigation buttons


        ViewGroup.LayoutParams navLayout = ((TankActivity)context).navView.getLayoutParams();
        int navDim = (int)(Math.min((dimW - dim)*0.9/2,dimH-bDim)*0.95);
//        navLayout.width = bDim;
//        navLayout.height = bDim;
        navLayout.width = navDim;
        navLayout.height = navDim;
        ((TankActivity)context).navView.layout(bmDim,dimH-navDim,navDim,navDim);

        RelativeLayout.LayoutParams btnLayout = (RelativeLayout.LayoutParams)((TankActivity)context).lftBtn.getLayoutParams();
        btnLayout.width = (int)(navDim*0.4);
        btnLayout.height = (int)(navDim*0.4);
        ((TankActivity)context).lftBtn.setLayoutParams(btnLayout);

        btnLayout = (RelativeLayout.LayoutParams)((TankActivity)context).upBtn.getLayoutParams();
        btnLayout.width = (int)(navDim*0.4);
        btnLayout.height = (int)(navDim*0.4);
        ((TankActivity)context).upBtn.setLayoutParams(btnLayout);

        btnLayout = (RelativeLayout.LayoutParams)((TankActivity)context).rtBtn.getLayoutParams();
        btnLayout.width = (int)(navDim*0.4);
        btnLayout.height = (int)(navDim*0.4);
        ((TankActivity)context).rtBtn.setLayoutParams(btnLayout);

        btnLayout = (RelativeLayout.LayoutParams)((TankActivity)context).dwnBtn.getLayoutParams();
        btnLayout.width = (int)(navDim*0.4);
        btnLayout.height = (int)(navDim*0.4);
        ((TankActivity)context).dwnBtn.setLayoutParams(btnLayout);

//        btnLayout = (RelativeLayout.LayoutParams)((TankActivity)context).stick.getLayoutParams();
//        btnLayout.width = (int)(bDim*0.35);
//        btnLayout.height = (int)(bDim*0.35);
//        ((TankActivity)context).stick.setLayoutParams(btnLayout);

        // Shoot button

        ViewGroup.LayoutParams shtLayout = ((TankActivity)context).shootAlign.getLayoutParams();
        int stDim = (int)(bDim*0.7);
        shtLayout.width = stDim;
        shtLayout.height = stDim;

        ((TankActivity)context).shootAlign.layout(dimW-stDim-bmDim,dimH-stDim-bmDim, stDim, stDim);

        // Bomb button

        ViewGroup.LayoutParams bmbLayout = ((TankActivity)context).bombAlign.getLayoutParams();
        int bmbDim = stDim/2;
        bmbLayout.width = bmbDim;
        bmbLayout.height = bmbDim;
        ((TankActivity)context).bombAlign.layout(dimW-bmbDim-bmDim,dimH-bmbDim-stDim-bmDim, bmbDim, bmbDim);

        // Pause button

        ViewGroup.LayoutParams psLayout = ((TankActivity)context).pauseControl.getLayoutParams();
        int psDim = (int)(bDim*0.6);
        psLayout.width = psDim;
        ((TankActivity)context).pauseControl.layout(dimW-psDim-bmDim,bmDim, psDim, psLayout.height);





        WIDTH = TankView.this.getWidth();
        HEIGHT = TankView.this.getHeight();
        tile_dim = (int)(HEIGHT/26);
//        tile_dim = (int)(dim/26);
        Log.d("VIEW DIM", WIDTH + " " + HEIGHT);
        Log.d("VIEW SCALE", String.valueOf(SCALE));

        graphics = BitmapFactory.decodeResource(context.getResources(), R.drawable.tanktexture);
        Bitmap bombBm = BitmapFactory.decodeResource(context.getResources(), R.drawable.bomb);
        Bitmap fireBm = BitmapFactory.decodeResource(context.getResources(), R.drawable.fire);
        mineBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.bonus_mine);

        Bitmap test = Bitmap.createBitmap(graphics,0,0,32,32);
        int h = test.getHeight();
        RESIZE  = dim/(h*13f);

//        TankView.typeface = Typeface.createFromAsset(TankView.context.getAssets(),"prstartk.ttf");
        TankView.typeface = Typeface.createFromAsset(TankView.context.getAssets(),"arialbd.ttf");
        TankView.txtPaint = new Paint();
        txtPaint.setTypeface(TankView.typeface);
        txtPaint.setColor(Color.WHITE);
//        txtPaint.setStyle(Paint.Style.STROKE);
        txtPaint.setTextSize(10*SCALE);


        loadGameObjects();

        graphics = Bitmap.createScaledBitmap(graphics,(int)(RESIZE*graphics.getWidth()/SCALE),(int)(RESIZE*graphics.getHeight()/SCALE),false);

        bombBm = Bitmap.createScaledBitmap(bombBm,(int)(RESIZE*bombBm.getWidth()/SCALE),(int)(RESIZE*bombBm.getHeight()/SCALE),false);
        bombSprite = SpriteObjects.getInstance().getData(ObjectType.ST_BOMB);
        bombBitmap = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            bombBitmap.add(Bitmap.createBitmap(bombBm,i*bombSprite.w, 0, bombSprite.w, bombSprite.h));
        }

        mineBitmap = Bitmap.createScaledBitmap(mineBitmap,(int)(RESIZE*mineBitmap.getWidth()/SCALE),(int)(RESIZE*mineBitmap.getHeight()/SCALE),false);

        fireBm = Bitmap.createScaledBitmap(fireBm,(int)(RESIZE*fireBm.getWidth()/SCALE),(int)(RESIZE*fireBm.getHeight()/SCALE),false);
        fireSprite = SpriteObjects.getInstance().getData(ObjectType.ST_FIRE);
        fireBitmap = new Bitmap[7][4];
        for(int i = 0; i < 7; i++) {
            for(int j = 0; j < 4; j++) {
                fireBitmap[i][j] = Bitmap.createBitmap(fireBm, j * fireSprite.w, i * fireSprite.h, fireSprite.w, fireSprite.h);
            }
        }

        tankSprite = SpriteObjects.getInstance().getData(ObjectType.ST_TANK);
        Bitmap bm = Bitmap.createBitmap(TankView.graphics, 0, 0 , tankSprite.w*28, tankSprite.h* tankSprite.frame_count*4);
        tankBitmap = new ArrayList<>();
        for(int i = 0; i < 8; i++){
            ArrayList<Bitmap> b = new ArrayList<>();
            for(int j = 0; j < 28; j++) {
                b.add(Bitmap.createBitmap(bm, j*tankSprite.w, i*tankSprite.h, tankSprite.w, tankSprite.h));
            }
            tankBitmap.add(b);
        }

        Sprite sprite = SpriteObjects.getInstance().getData(ObjectType.ST_LEFT_ENEMY);
        eCountBm = Bitmap.createBitmap(TankView.graphics, sprite.x, sprite.y, sprite.w, sprite.h);
        eCountImg = new BitmapDrawable(context.getResources(), eCountBm);
//        getEnemyCountView();

        ((TankActivity)context).enemyCountImg.setBackground(eCountImg);

        sprite = SpriteObjects.getInstance().getData(ObjectType.ST_FLAG);
        bm = Bitmap.createBitmap(TankView.graphics, sprite.x, sprite.y, sprite.w, sprite.h);
        Drawable d = new BitmapDrawable(context.getResources(), bm);
        ((TankActivity)context).P1StatusImg.setBackground(d);

        if(twoPlayers) {
            bm = Bitmap.createBitmap(TankView.graphics, sprite.x, sprite.y + sprite.h, sprite.w, sprite.h);
            d = new BitmapDrawable(context.getResources(), bm);
            ((TankActivity)context).P2StatusImg.setBackground(d);
        }

        sprite = SpriteObjects.getInstance().getData(ObjectType.ST_STAGE_STATUS);
        bm = Bitmap.createBitmap(TankView.graphics, sprite.x, sprite.y, sprite.w, sprite.h);
        d = new BitmapDrawable(context.getResources(), bm);
        ((TankActivity)context).StageFlag.setBackground(d);

        sprite = SpriteObjects.getInstance().getData(ObjectType.ST_CURTAIN);
        bm = Bitmap.createBitmap(TankView.graphics, sprite.x, sprite.y, sprite.w, sprite.h);
        curtain = Bitmap.createScaledBitmap(bm,WIDTH,bm.getHeight(),false);
        curtainPaint.setColor(Color.GRAY);
        curtainPaint.setStyle(Paint.Style.FILL);
        curtainTRect = new Rect();
        curtainBRect = new Rect();

        ((TankActivity)context).scoreView.setVisibility(View.INVISIBLE);

        resetScoreView();

        if(twoPlayers) {
            gameModel = new ConcurrentLinkedQueue<>();
            if(WifiDirectManager.getInstance().isServer()) {
                P1 = new Player(ObjectType.ST_PLAYER_1, getWidth() / 2 , getHeight() / 2 , 1);
                P2 = new Player(ObjectType.ST_PLAYER_2, getWidth() / 2, getHeight() / 2, 2);
            }
            else {
                P2 = new Player(ObjectType.ST_PLAYER_1, getWidth() / 2 , getHeight() / 2 , 1);
                P1 = new Player(ObjectType.ST_PLAYER_2, getWidth() / 2, getHeight() / 2, 2);
            }

            tRemoteHandler.start();
//            tRemoteHandler = new RemoteUpdateHandler();
//            tRemoteHandler.sendMessage(null);

        }
        else {
            P1 = new Player(ObjectType.ST_PLAYER_1, getWidth() / 2 - 100, getHeight() / 2 - 100, 1);
            Log.d("PLAYERS", "ONE PLAYER");
        }
        Enemies = new ArrayList<>();
        bonus = new Bonus();

        TankView.GOLD_LEVEL = ((TankActivity)context).settings.getInt(TankActivity.GOLD_LEVEL,0);

        gameover = false;
        stageComplete = false;
        showingScore = false;
        sendModel = new TankGameModel();

        if(twoPlayers) {
            if(WifiDirectManager.getInstance().isServer()) {
                ((TankActivity)context).giftBtn.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.p1_gift,null));
            }
            else {
                ((TankActivity)context).giftBtn.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.p2_gift,null));
            }
            ((TankActivity)context).showGift();
            ((TankActivity)context).enableGift();
        }
        else {
            ((TankActivity)context).hideGift();
            ((TankActivity)context).disableGift();
        }

    }

    public void retryStage() {
        if(twoPlayers) {
            gameModel = new ConcurrentLinkedQueue<>();
            if(WifiDirectManager.getInstance().isServer()) {
                P1 = new Player(ObjectType.ST_PLAYER_1, getWidth() / 2 , getHeight() / 2 , 1);
                P2 = new Player(ObjectType.ST_PLAYER_2, getWidth() / 2, getHeight() / 2, 2);
            }
            else {
                P2 = new Player(ObjectType.ST_PLAYER_1, getWidth() / 2 , getHeight() / 2 , 1);
                P1 = new Player(ObjectType.ST_PLAYER_2, getWidth() / 2, getHeight() / 2, 2);
            }
        }
        else {
            P1 = new Player(ObjectType.ST_PLAYER_1, getWidth() / 2 - 100, getHeight() / 2 - 100, 1);
        }

        bonus = new Bonus();
        mNewRound = true;
//        if(((TankActivity)context).mInterstitialAd == null) {
//            ((TankActivity) context).loadInterstitialAd();
//        }
//        level;

        try{SoundManager.stopSound(SCENE_SOUND);}
        catch (Exception e){}
        try{SoundManager.stopSound(Sounds.TANK.HVE_SOUND);}
        catch (Exception e){}
    }

    /**
     * Reset the paddles/touchboxes/framespersecond/ballcounter for the next round.
     */
    private void nextRound() {
//        level = 33;
        drawStarted = false;
        startSound = false;
        notifyStageComplete = false;
        notifyGameOver = false;
        notifyPause = false;
        notifyEndGame = false;
        notifyRetryStage = false;
        freeze = false;
        P1.unFreeze();
        if(twoPlayers) {
            P2.setFreeze();
        }
        bonus.reset();
        P1.bomb.id = 0;
        P1.bulletIntercept = 0;
        P1.killTime.clear();
        numStars = 1;

        ((TankActivity)context).scoreView.setVisibility(View.INVISIBLE);
        ((TankActivity)context).gameStars.setVisibility(View.INVISIBLE);

        Enemy.lives = 20;
        setEnemyCountView();
        Enemies.clear();


//        level;
        clearLevel();
        if(CONSTRUCTION) {
            if(!twoPlayers || WifiDirectManager.getInstance().isServer()) {
                loadConstructionLevel(level);
            }
        }
        else {
            loadLevel(level);
        }
        ((TankActivity)context).StageTxt.setText(String.valueOf(level));
        ((TankActivity)context).bmbText.setText(String.valueOf(P1.getMines()));

        closingCurtain = true;
        openingCurtain = false;
        curtainFrame = 0;
        curtainFrameTmr = curtainFrameTime;

        ((TankActivity)context).gameOverTxt.setVisibility(View.INVISIBLE);
        ((TankActivity)context).gameOverTxt.setY(HEIGHT);
        gameover = false;
        stageComplete = false;
        ((TankActivity)context).enableControls();

        resetScoreView();

        if(P1.lives > 0) {
            P1.respawn();
            P1.resetKills();
            P1.clearBullets();
            P1.bId = 0;
            P1.stageScore = 0;
        }
        showingScore = false;
        round_started = true;
//        if(((TankActivity)context).mInterstitialAd == null) {
//            ((TankActivity) context).loadInterstitialAd();
//        }
        Log.d("NXT ROUND GOLD LEVEL", String.valueOf(TankView.GOLD_LEVEL) + level);
        if(TankView.GOLD_LEVEL < level) {
            int x,y;
            Log.d("NXT ROUND", "New gold available");

            if(!levelBushes.isEmpty() && Math.random() < 0.2) {
                int p = (int)(Math.floor(Math.random()*levelBushes.size()));
                gold.setPosition(levelBushes.get(p).x, levelBushes.get(p).y);
            }
            else {
                do {
                    x = (int) (Math.random() * 25);
                    y = (int) (Math.random() * 25);
                } while (levelObjects.get(x).get(y) == null || levelObjects.get(x).get(y) instanceof Ice || levelObjects.get(x).get(y) instanceof Water);
//            gold.setPosition(TankView.WIDTH / 2, TankView.HEIGHT / 2);
                gold.setPosition(levelObjects.get(x).get(y).x, levelObjects.get(x).get(y).y);
            }
            gold.setAvailable(true);
        }
        else {
            gold.setAvailable(false);
            Log.d("NXT ROUND", "New gold not available");
        }

        bomb = new Bomb(TankView.WIDTH / 2, TankView.HEIGHT / 2, true);

//        bomb.setPosition(TankView.WIDTH / 2, TankView.HEIGHT / 2);

        objectives = ((TankActivity)context).loadObjectives();
        currentObj = new boolean[objectives.get(0).length];
        for(int i = 0; i < currentObj.length; i++) {
            switch (i) {
                case 0:
                case 3:
                case 5:
                case 8:
                case 9:
                    currentObj[i] = true;
                    break;
                default:
                    currentObj[i] = false;
            }
        }
        ((TankActivity) context).updateBonusStack();
        GameStartTime = System.currentTimeMillis();
        ((TankActivity)context).start_timer();
        P1.enableMove();
        P1.enableFire();

//        level = 30;

        double num_hve = Math.pow(10,level*Math.log10(MAX_HVE)/NUM_LEVELS);
        NUM_HVE = (int)Math.floor(num_hve+0.5);
        HVE_LIVES = NUM_HVE;
        HVE.IS_AVAILABLE = false;
        Log.d("HVE: ", String.valueOf(NUM_HVE));
        SCENE_SOUND = (int)(Sounds.TANK.FIGHT_SCENE1 + Math.random()*(Sounds.TANK.FIGHT_SCENE7-Sounds.TANK.FIGHT_SCENE1) + 0.5);
        SoundManager.playSound(SCENE_SOUND,true);
    }

    /**
     * The main loop. Call this to update the game state.
     */
    public void update() {
        if(mRedrawHandler == null){
            mRedrawHandler = new TankView.RefreshHandler();
            mRedrawHandler.sleep(1000 / FPS);
            return;
        }
        if(getHeight() == 0 || getWidth() == 0) {
            mRedrawHandler.sleep(1000 / FPS);
            return;
        }

        if(!mInitialized) {
            initializeGame();
            mInitialized = true;
        }

        long now = System.currentTimeMillis();
        if(gameRunning() && mCurrentState != TankView.State.Stopped) {
            if(now - mLastFrame >= 1000 / FPS) {
                if(mNewRound) {
                    nextRound();
                    mNewRound = false;
                }
                doGameLogic();
            }
        }

        // We will take this much time off of the next update() call to normalize for
        // CPU time used updating the game state.

        if(mContinue) {
            long diff = System.currentTimeMillis() - now;
//            Log.d("DIFF TIME", String.valueOf(diff));
            mRedrawHandler.sleep(Math.max(0, (1000 / FPS) - diff) );
        }
    }

    public void update(boolean start) {
        this.started = start;
        mContinue = true;
        update();
    }

    private void displayGameOver() {
        ((TankActivity)context).gameOverTxt.setVisibility(View.VISIBLE);
        ((TankActivity)context).gameOverTxt.animate().setDuration(2000);
        ((TankActivity)context).gameOverTxt.animate().y(HEIGHT/2);
    }

    private void resetScoreView() {
        ((TankActivity)context).p1Score.setText(String.valueOf(0));

        ((TankActivity)context).p1AScore.setText(String.valueOf(0));
        ((TankActivity)context).p1BScore.setText(String.valueOf(0));
        ((TankActivity)context).p1CScore.setText(String.valueOf(0));
        ((TankActivity)context).p1DScore.setText(String.valueOf(0));

        ((TankActivity)context).p1ACount.setText(String.valueOf(0));
        ((TankActivity)context).p1BCount.setText(String.valueOf(0));
        ((TankActivity)context).p1CCount.setText(String.valueOf(0));
        ((TankActivity)context).p1DCount.setText(String.valueOf(0));
        ((TankActivity)context).p1Count.setText(String.valueOf(0));

        ((TankActivity)context).p2Score.setText(String.valueOf(0));

        ((TankActivity)context).p2AScore.setText(String.valueOf(0));
        ((TankActivity)context).p2BScore.setText(String.valueOf(0));
        ((TankActivity)context).p2CScore.setText(String.valueOf(0));
        ((TankActivity)context).p2DScore.setText(String.valueOf(0));

        ((TankActivity)context).p2ACount.setText(String.valueOf(0));
        ((TankActivity)context).p2BCount.setText(String.valueOf(0));
        ((TankActivity)context).p2CCount.setText(String.valueOf(0));
        ((TankActivity)context).p2DCount.setText(String.valueOf(0));
        ((TankActivity)context).p2Count.setText(String.valueOf(0));

        scoreFrame = 0;


    }

    private void showScores() {
        if(enemyFrame >= 5 && stageComplete) {
//            if(--newStageTmr <= 0){
//                mNewRound = true;
//                newStageTmr = (int) (2 * TO_SEC);
//            }
            return;
        }
        if(!showingScore) {
            //First time
            SoundManager.stopSounds();
            //TODO why is retry count default 3 here?
            int retries = ((TankActivity)context).settings.getInt(TankActivity.RETRY_COUNT,3);

            ((TankActivity) context).retryCount.setText(String.valueOf(retries));
            ((TankActivity) context).stageScore.setText(String.valueOf(level));


            if(!twoPlayers || WifiDirectManager.getInstance().isServer()) {
                ((TankActivity) context).p1Score.setText(String.valueOf(P1.totalScore += P1.stageScore));

                if(twoPlayers) {
                    ((TankActivity) context).p2Score.setText(String.valueOf(P2.totalScore += P2.stageScore));
                }
            }
            else{
                ((TankActivity) context).p2Score.setText(String.valueOf(P1.totalScore += P1.stageScore));
                ((TankActivity) context).p1Score.setText(String.valueOf(P2.totalScore += P2.stageScore));
            }

            ((TankActivity) context).scoreView.setVisibility(View.VISIBLE);

            showingScore = true;
            scoreFrameTmr = scoreFrameTime;
            enemyFrame = 0;
            Log.d("SCORES", "Showing scores");

        }

        int p1Kills = 0,p2Kills = 0;
        int p1TKills = 0,p2TKills = 0;


        if(!twoPlayers || WifiDirectManager.getInstance().isServer()) {
            if(enemyFrame < P1.kills.length) {
                p1Kills = P1.kills[enemyFrame];
            }
            p1TKills = P1.totalKills;

            if(twoPlayers) {
                if(enemyFrame < P1.kills.length) {
                    p2Kills = P2.kills[enemyFrame];
                }
                p2TKills = P2.totalKills;
            }
        }
        else{
            if(enemyFrame < P1.kills.length) {
                p1Kills = P2.kills[enemyFrame];
                p2Kills = P1.kills[enemyFrame];
            }

            p1TKills = P2.totalKills;
            p2TKills = P1.totalKills;
        }

        if(scoreFrameTmr <= 0) {
            int p1killCount, p2killCount=0;

            switch (enemyFrame) {
                case 0:
                        p1killCount = Integer.parseInt((String) ((TankActivity) context).p1ACount.getText());

                        if(twoPlayers) {
                            p2killCount = Integer.parseInt((String) ((TankActivity) context).p2ACount.getText());
                        }

                    if (p1killCount < p1Kills || (twoPlayers && p2killCount < p2Kills)) {
                        if(p1killCount < p1Kills) {
                            p1killCount++;
                            ((TankActivity) context).p1AScore.setText(String.valueOf(p1killCount * 100));
                            ((TankActivity) context).p1ACount.setText(String.valueOf(p1killCount));
                        }

                        if(twoPlayers && p2killCount < p2Kills) {
                            p2killCount++;
                            ((TankActivity) context).p2AScore.setText(String.valueOf(p2killCount * 100));
                            ((TankActivity) context).p2ACount.setText(String.valueOf(p2killCount));
                        }
                        SoundManager.playSound(Sounds.TANK.SCORE);
                    }
                    else {
                        enemyFrame++;
                    }
                    break;
                case 1:
                        p1killCount = Integer.parseInt((String) ((TankActivity) context).p1BCount.getText());

                        if(twoPlayers) {
                            p2killCount = Integer.parseInt((String) ((TankActivity) context).p2BCount.getText());
                        }

                    if (p1killCount < p1Kills || (twoPlayers && p2killCount < p2Kills)) {
                        if(p1killCount < p1Kills) {
                            p1killCount++;
                            ((TankActivity) context).p1BScore.setText(String.valueOf(p1killCount * 200));
                            ((TankActivity) context).p1BCount.setText(String.valueOf(p1killCount));
                        }

                        if(twoPlayers && p2killCount < p2Kills) {
                            p2killCount++;
                            ((TankActivity) context).p2BScore.setText(String.valueOf(p2killCount * 200));
                            ((TankActivity) context).p2BCount.setText(String.valueOf(p2killCount));
                        }
                        SoundManager.playSound(Sounds.TANK.SCORE);
                    }
                    else {
                        enemyFrame++;
                    }
                    break;
                case 2:
                        p1killCount = Integer.parseInt((String) ((TankActivity) context).p1CCount.getText());

                        if(twoPlayers) {
                            p2killCount = Integer.parseInt((String) ((TankActivity) context).p2CCount.getText());
                        }

                    if (p1killCount < p1Kills || (twoPlayers && p2killCount < p2Kills)) {
                        if(p1killCount < p1Kills) {
                            p1killCount++;
                            ((TankActivity) context).p1CScore.setText(String.valueOf(p1killCount * 300));
                            ((TankActivity) context).p1CCount.setText(String.valueOf(p1killCount));
                        }

                        if(twoPlayers && p2killCount < p2Kills) {
                            p2killCount++;
                            ((TankActivity) context).p2CScore.setText(String.valueOf(p2killCount * 300));
                            ((TankActivity) context).p2CCount.setText(String.valueOf(p2killCount));
                        }
                        SoundManager.playSound(Sounds.TANK.SCORE);
                    }
                    else {
                        enemyFrame++;
                    }
                    break;
                case 3:
                        p1killCount = Integer.parseInt((String) ((TankActivity) context).p1DCount.getText());

                        if(twoPlayers) {
                            p2killCount = Integer.parseInt((String) ((TankActivity) context).p2DCount.getText());
                        }

                    if (p1killCount < p1Kills || (twoPlayers && p2killCount < p2Kills)) {
                        if(p1killCount < p1Kills) {
                            p1killCount++;
                            ((TankActivity) context).p1DScore.setText(String.valueOf(p1killCount * 400));
                            ((TankActivity) context).p1DCount.setText(String.valueOf(p1killCount));
                        }

                        if(twoPlayers && p2killCount < p2Kills) {
                            p2killCount++;
                            ((TankActivity) context).p2DScore.setText(String.valueOf(p2killCount * 400));
                            ((TankActivity) context).p2DCount.setText(String.valueOf(p2killCount));
                        }
                        SoundManager.playSound(Sounds.TANK.SCORE);
                    }
                    else {
                        enemyFrame++;
                    }
                    break;

                case 4:

                    ((TankActivity) context).p1Count.setText(String.valueOf(p1TKills));
                    if(twoPlayers) {
                        ((TankActivity) context).p2Count.setText(String.valueOf(p2TKills));
                    }
                    switch (numStars) {
                        case 0:
                            ((TankActivity) context).gameStars.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.star0,null));
                            break;
                        case 1:
                            ((TankActivity) context).gameStars.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.star1,null));
                            break;
                        case 2:
                            ((TankActivity) context).gameStars.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.star2,null));
                            break;
                        case 3:
                            ((TankActivity) context).gameStars.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.star3,null));
                            break;
                    }
                    ((TankActivity)context).gameStars.setVisibility(View.VISIBLE);

                    SoundManager.playSound(Sounds.TANK.PAUSE);
                    enemyFrame++;
                    break;
            }
            scoreFrameTmr = scoreFrameTime;
        }
        else {
            --scoreFrameTmr;
        }
    }

    private void saveNewStage(int level){
        SharedPreferences settings = context.getSharedPreferences("TankSettings", 0);
        int oldLevel = settings.getInt(TankMenuActivity.PREF_LEVEL,1);
        if(oldLevel < level) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(TankMenuActivity.PREF_LEVEL, level);
            editor.apply();
        }
    }

    private void clearLevel() {
        levelObjects = null;
        levelBushes = null;
    }


    private void moveCurtain() {
        if(curtainPause && --curtainPauseTmr > 0) {
            if(curtainPauseTmr == 1) {
                ((TankActivity)context).curtainTxt.setVisibility(View.INVISIBLE);
            }
            return;
        }
        if(movingCurting && (closingCurtain || openingCurtain)){
            if(curtainFrame < 13){
                if (curtainFrameTmr <= 0) {
                    curtainFrame++;
                    curtainFrameTmr = curtainFrameTime;
                }
                else {
                    curtainFrameTmr--;
                }
            }
        }
    }






    public static void setEnemyCountView() {

        ((TankActivity)context).enemyCountTxt.setText(String.valueOf(Enemy.lives));
    }


    public boolean isStarted(){
        return started;
    }



    private void checkCollisionTwoTanks(Player t1, Player t2) {
        t1.collidesWithObject(t2);
    }

    private void checkCollisionPlayer(Player p) {
        for(Enemy e:Enemies){
            if(!e.isDestroyed() && p.collidesWithObject(e)) {
                return;
            }
        }
        if(P1.bomb.isDropped() && P1.collidesWithObject(P1.bomb)) {
            return;
        }
        if(twoPlayers) {
            if(P1.collidesWithObject(P2)) {
                return;
            }
        }
        if(!p.collidesWithObject(eagle)) {
            int col = p.x/tile_dim;
            int row = p.y/tile_dim;
            int minRow = Math.max(0,row-2);
            int maxRow = Math.min(levelObjects.size(),row+3);
            int minCol = Math.max(0,col-2);
            int maxCol = Math.min(levelObjects.get(0).size(),col+3);
            boolean stop = false;
            for(int i = minRow; i < maxRow; i++){
                for(int j = minCol; j < maxCol; j++){
                    if(levelObjects.get(i).get(j) != null && (levelObjects.get(i).get(j) instanceof Brick || levelObjects.get(i).get(j) instanceof StoneWall || (levelObjects.get(i).get(j) instanceof Water && !p.hasBoat()))) {

                        if(p.collidesWithObject(levelObjects.get(i).get(j))) {
                            stop = true;
                            p.stopSlip();
                            break;
                        }
                    }
                    else if(levelObjects.get(i).get(j) instanceof Ice && p.collidesWithObject(levelObjects.get(i).get(j))) {
                        p.iceSlippage();
                    }
                }
                if(stop){
                    break;
                }
            }
        }
    }

    private void checkCollisionEnemyWithPlayer(Tank p, ArrayList<Enemy> enemies) {
        for(Tank e:enemies){
            if(e.collidesWithObject(p)) {
                continue;
            }
            else{
                e.collidesWithObject(eagle);
            }
        }

    }

    private void checkCollisionEnemy() {
        for(int e1 = 0; e1 < Enemies.size(); e1++) {
            if(Enemies.get(e1).collidesWithObject(P1)) {
                continue;
            }
            else if(twoPlayers && Enemies.get(e1).collidesWithObject(P2)) {
                continue;
            }
            else if(Enemies.get(e1).collidesWithObject(eagle)) {
                continue;
            }
            else if(P1.bomb.isDropped() && Enemies.get(e1).collidesWithObject(P1.bomb)) {
                continue;
            }
            else {
                for (int e2 = 0; e2 < Enemies.size(); e2++) {
//                    if (e1 == e2) {
//                        continue;
//                    }
                    if (e1!=e2 && Enemies.get(e1).collidesWithObject(Enemies.get(e2))) {
                        break;
                    }
                    else {
                        int col = Enemies.get(e1).x/tile_dim;
                        int row = Enemies.get(e1).y/tile_dim;
                        int minRow = Math.max(0,row-2);
                        int maxRow = Math.min(levelObjects.size(),row+3);
                        int minCol = Math.max(0,col-2);
                        int maxCol = Math.min(levelObjects.get(0).size(),col+3);
                        boolean stop = false;
                        for(int i = minRow; i < maxRow; i++){
                            for(int j = minCol; j < maxCol; j++){
                                if(levelObjects.get(i).get(j) != null && (levelObjects.get(i).get(j) instanceof Brick || levelObjects.get(i).get(j) instanceof StoneWall || (levelObjects.get(i).get(j) instanceof Water && !enemyBoat))) {
                                    if(Enemies.get(e1).collidesWithObject(levelObjects.get(i).get(j))) {
                                        stop = true;
                                        break;
                                    }
                                }
                            }
                            if(stop){
                                break;
                            }
                        }
                    }

                }
            }
//
        }
    }

    private void checkCollisionPlayerBullet(Player p) {
        for(Bullet pbullet:p.getBullets()){
            if(!pbullet.isDestroyed()) {
                for (Enemy e : Enemies) {
                    if(e.collidsWithBullet(pbullet)) {
                        ++p.totalKills;
                        if(e.type == ObjectType.ST_TANK_A) {
                            ++p.kills[0];
                            p.stageScore += 100;
                            p.killTime.add(((TankActivity)context).playtime);
                        }
                        else if(e.type == ObjectType.ST_TANK_B) {
                            ++p.kills[1];
                            p.stageScore += 200;
                            p.killTime.add(((TankActivity)context).playtime);
                        }
                        else if(e.type == ObjectType.ST_TANK_C) {
                            ++p.kills[2];
                            p.stageScore += 300;
                            p.killTime.add(((TankActivity)context).playtime);
                        }
                        else if(e.type == ObjectType.ST_TANK_D) {
                            ++p.kills[3];
                            p.stageScore += 400;
                            p.killTime.add(((TankActivity)context).playtime);
                        }
                        break;
                    }
                    for(Bullet ebullet:e.getBullets()) {
                        if(!pbullet.isDestroyed() && !ebullet.isDestroyed()) {
                            if (pbullet.collides_with(ebullet)) {
                                pbullet.setDestroyed(false);
                                ebullet.setDestroyed(false);
                                Log.d("COLLISION", "Bullet collision from " + (WifiDirectManager.getInstance().isServer()?"Server":"Client"));
                                ++P1.bulletIntercept;
                                break;
                            }
                        }
                    }
                }
                if(pbullet.isDestroyed()) {
                    continue;
                }
                int col = pbullet.x/tile_dim;
                int row = pbullet.y/tile_dim;
                int minRow = Math.max(0,row-2);
                int maxRow = Math.min(levelObjects.size(),row+2);
                int minCol = Math.max(0,col-2);
                int maxCol = Math.min(levelObjects.get(0).size(),col+2);
                boolean stop = false;
                int count = 0;
                for(int i = minRow; i < maxRow; i++){
                    for(int j = minCol; j < maxCol; j++){
                        if(levelObjects.get(i).get(j) != null && !levelObjects.get(i).get(j).isDestroyed() && pbullet.collides_with(levelObjects.get(i).get(j))) {
                            if(levelObjects.get(i).get(j) instanceof Brick) {
                                boolean coll = ((Brick) levelObjects.get(i).get(j)).collidsWithBullet(pbullet.getDirection());
                                if(coll && twoPlayers) {
                                    switch (pbullet.getDirection()) {
                                        case CONST.Direction.UP:
                                            levelObjectsUpdate.add(new int[]{i,j,6});
                                            break;
                                        case CONST.Direction.DOWN:
                                            levelObjectsUpdate.add(new int[]{i,j,7});
                                            break;
                                        case CONST.Direction.LEFT:
                                            levelObjectsUpdate.add(new int[]{i,j,8});
                                            break;
                                        case CONST.Direction.RIGHT:
                                            levelObjectsUpdate.add(new int[]{i,j,9});
                                            break;
                                    }

                                }
                                if(p.canBreakWall() || !coll) {
                                    levelObjects.get(i).set(j, null);
                                    for (int[] eaglePo : eaglePos) {
                                        if (eaglePo[0] == i && eaglePo[1] == j) {
                                            currentObj[8] = false;
                                            break;
                                        }
                                    }
                                    if(twoPlayers) {
                                        levelObjectsUpdate.add(new int[]{i, j, 0});
                                    }
                                }
                                pbullet.setDestroyed();
                                count++;
                                if(count >= 2){
                                    stop = true;
                                    break;
                                }
                                SoundManager.playSound(Sounds.TANK.BRICK,1,1);
                            }
                            else if(levelObjects.get(i).get(j) instanceof StoneWall) {
                                pbullet.setDestroyed();
                                if(p.canBreakWall()) {
                                    levelObjects.get(i).set(j,null);
                                    if(twoPlayers) {
                                        levelObjectsUpdate.add(new int[]{i, j, 0});
                                    }
                                    if(protectEagle) {
                                        for (int[] eaglePo : eaglePos) {
                                            if (eaglePo[0] == i && eaglePo[1] == j) {
                                                currentObj[8] = false;
                                                break;
                                            }
                                        }
                                    }
                                }
                                count++;
                                if(count >= 2){
                                    stop = true;
                                    break;
                                }
                                if(p.canBreakWall()) {
                                    SoundManager.playSound(Sounds.TANK.BRICK, 1, 1);
                                }
                                else{
                                    SoundManager.playSound(Sounds.TANK.STEEL, 1, 1);
                                }
                            }
                        }
                    }
                    if(stop){
                        break;
                    }
                }
                count = 0;
                for(int i = 0; i < levelBushes.size(); i++) {
                    if(p.canClearBush() && levelBushes.get(i) != null && pbullet.collides_with(levelBushes.get(i))) {
                        levelBushes.set(i,null);
                        if(twoPlayers) {
                            levelBushesUpdate.add(i);
                        }
                        count++;
                        if(count >= 2){
                            break;
                        }
                        SoundManager.playSound(Sounds.TANK.BRICK,1,1);
                    }
                }
                if(!eagle.isDestroyed()) {
                    eagle.collidesWithBullet(pbullet);
                }
            }
        }
    }

    private void checkCollisionPlayerBomb(Player p) {
        if(p.bomb.isMoving()) {
            int col = p.bomb.x/tile_dim;
            int row = p.bomb.y/tile_dim;
            int minRow = Math.max(0,row-2);
            int maxRow = Math.min(levelObjects.size(),row+3);
            int minCol = Math.max(0,col-2);
            int maxCol = Math.min(levelObjects.get(0).size(),col+3);

            for(int i = minRow; i < maxRow; i ++) {
                for(int j = minCol; j < maxCol; j ++) {
                    if((levelObjects.get(i).get(j) instanceof Brick || levelObjects.get(i).get(j) instanceof StoneWall) && p.bomb.collidesWithObject(levelObjects.get(i).get(j))){
                        return;
                    }
                }
            }
            for (Enemy e : Enemies) {
                p.bomb.collidesWithObject(e);
                return;
            }

            if(twoPlayers && p.bomb.collidesWithObject(P2)) {
                return;
            }
        }
//        if(p.bomb.isDropped()) {
//
//        }
        else if(p.bomb.isExploding()) {
            int col = p.bomb.x/tile_dim;
            int row = p.bomb.y/tile_dim;
            int minRow = Math.max(0,row-2);
            int maxRow = Math.min(levelObjects.size(),row+3);
            int minCol = Math.max(0,col-2);
            int maxCol = Math.min(levelObjects.get(0).size(),col+3);

            for(int i = minRow; i < maxRow; i ++) {
                for(int j = minCol; j < maxCol; j ++) {
                    if (levelObjects.get(i).get(j) instanceof  Brick && p.bomb.destroyObj(levelObjects.get(i).get(j))) {
                        levelObjects.get(i).set(j, null);
                        if(twoPlayers) {
                            levelObjectsUpdate.add(new int[]{i, j, 0});
                        }
                    }
                }
            }

            if(p.bomb.destroyObj((p))) {
                p.collideBomb(p.bomb.id);
            }

            for (Enemy e : Enemies) {
                if(p.bomb.destroyObj(e)) {
                    if(e.collideBomb(p.bomb.id)) {
                        ++p.totalKills;
                        if (e.type == ObjectType.ST_TANK_A) {
                            ++p.kills[0];
                            p.stageScore += e.getKillScore();
                            p.killTime.add(((TankActivity) context).playtime);
                        } else if (e.type == ObjectType.ST_TANK_B) {
                            ++p.kills[1];
                            p.stageScore += e.getKillScore();
                            p.killTime.add(((TankActivity) context).playtime);
                        } else if (e.type == ObjectType.ST_TANK_C) {
                            ++p.kills[2];
                            p.stageScore += e.getKillScore();
                            p.killTime.add(((TankActivity) context).playtime);
                        } else if (e.type == ObjectType.ST_TANK_D) {
                            if(e.isHVE() && !e.isDestroyed()) {
                                continue;
                            }
                            ++p.kills[3];
                            p.stageScore += e.getKillScore();
                            p.killTime.add(((TankActivity) context).playtime);
                        }
                    }
                }
            }
        }


    }

    private void checkCollisionEnemyBulletWithPlayer(Player p) {
            for(Enemy e:Enemies) {
                for (Bullet bullet : e.getBullets()) {
                    if (bullet != null && !bullet.isDestroyed() && p.collidsWithBullet(bullet)) {
                        return;
                    }
                }
            }
    }

    private void checkCollisionEnemyBullet(Player p) {
        for(Enemy e:Enemies) {
            for(Bullet bullet: e.getBullets()) {
                if(!bullet.isDestroyed()) {
                    p.collidsWithBullet(bullet);

                    if(bullet.isDestroyed()) {
                        continue;
                    }

                    int col = bullet.x/tile_dim;
                    int row = bullet.y/tile_dim;
                    int minRow = Math.max(0,row-2);
                    int maxRow = Math.min(levelObjects.size(),row+2);
                    int minCol = Math.max(0,col-2);
                    int maxCol = Math.min(levelObjects.get(0).size(),col+2);

                    boolean stop = false;
                    int count = 0;
                    for(int i = minRow; i < maxRow; i++){
                        for(int j = minCol; j < maxCol; j++){
                            if(levelObjects.get(i).get(j) != null && !levelObjects.get(i).get(j).isDestroyed() && bullet.collides_with(levelObjects.get(i).get(j))) {
                                if(levelObjects.get(i).get(j) instanceof Brick) {
                                    boolean coll = ((Brick) levelObjects.get(i).get(j)).collidsWithBullet(bullet.getDirection());
                                    if(coll && twoPlayers) {
                                        switch (bullet.getDirection()) {
                                            case CONST.Direction.UP:
                                                levelObjectsUpdate.add(new int[]{i,j,6});
                                                break;
                                            case CONST.Direction.DOWN:
                                                levelObjectsUpdate.add(new int[]{i,j,7});
                                                break;
                                            case CONST.Direction.LEFT:
                                                levelObjectsUpdate.add(new int[]{i,j,8});
                                                break;
                                            case CONST.Direction.RIGHT:
                                                levelObjectsUpdate.add(new int[]{i,j,9});
                                                break;
                                        }

                                    }
                                    if(e.canBreakWall() || !coll) {
                                        levelObjects.get(i).set(j, null);
                                        if(twoPlayers) {
                                            levelObjectsUpdate.add(new int[]{i, j, 0});
                                        }
                                        for (int[] eaglePo : eaglePos) {
                                            if (eaglePo[0] == i && eaglePo[1] == j) {
                                                currentObj[8] = false;
                                                break;
                                            }
                                        }
                                    }
                                    bullet.setDestroyed();
                                    count++;
                                    if(count >= 2){
                                        stop = true;
                                        break;
                                    }
//                                    SoundManager.playSound(Sounds.TANK.BRICK,1,1);
                                }
                                else if(levelObjects.get(i).get(j) instanceof StoneWall) {
                                    bullet.setDestroyed();
                                    if(e.canBreakWall()) {
                                        levelObjects.get(i).set(j,null);
                                        if(twoPlayers){
                                            levelObjectsUpdate.add(new int[]{i, j, 0});
                                        }
                                        if(protectEagle) {
                                            for (int[] eaglePo : eaglePos) {
                                                if (eaglePo[0] == i && eaglePo[1] == j) {
                                                    currentObj[8] = false;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    count++;
                                    if(count >= 2){
                                        stop = true;
                                        break;
                                    }
                                }
                            }
                        }
                        if(stop){
                            break;
                        }
                    }
                    count = 0;
                    for(int i = 0; i < levelBushes.size(); i++) {
                        if(e.canClearBush() && levelBushes.get(i) != null && bullet.collides_with(levelBushes.get(i))) {
                            levelBushes.set(i,null);
                            count++;
                            if(count >= 2){
                                break;
                            }
//                            SoundManager.playSound(Sounds.TANK.BRICK,1,1);
                        }
                    }
                    if(!eagle.isDestroyed()) {
                        eagle.collidesWithBullet(bullet);
                    }
                }
            }
        }
    }

    private void checkCollisionPlayerWithBonus(Player p, Bonus b) {
        int resp = p.collidsWithBonus(b);
        switch(resp) {
            case 0:
                for(int i = 0; i < Enemies.size(); i++) {
                    if(Enemies.get(i).isHVE()) {
                        if(((HVE)Enemies.get(i)).getShiled() > 0) {
                            int hveShield = ((HVE)Enemies.get(i)).reduceShiled(1);
                            if(hveShield > 0 ) {
                                continue;
                            }
                        }
                    }
                    Log.d("BOMB","BOMBED " + Enemies.get(i).isHVE());
                    Enemies.get(i).setDestroyed();
                    Enemies.get(i).svrKill = TankView.twoPlayers && WifiDirectManager.getInstance().isServer();
                }
                break;
            case 2:
//                p.setFreeze();
                Enemy.freeze();
                break;
            case 3:
                protectEagle = true;
                eagle.protection = 1;
                protectEagleTmr = ProtectEagleTime;
                for (int[] eaglePo : eaglePos) {
                    levelObjects.get(eaglePo[1]).set(eaglePo[0], new StoneWall(eaglePo[0], eaglePo[1]));
                }
                break;
        }
    }


    private void checkCollisionEnemyWithBonus(Bonus b) {
        if(!TankView.ENEMY_BOOST) {
            return;
        }
        for(Enemy e: Enemies) {
            int resp = e.collidsWithBonus(b);
            switch(resp) {
                case 0:
                    P1.setDestroyed();
                    P1.setBBomb(true);
                    P1.svrKill = TankView.twoPlayers && WifiDirectManager.getInstance().isServer();
//                    if(twoPlayers) {
//                        P2.setDestroyed();
//                        P2.svrKill = TankView.twoPlayers && WifiDirectManager.getInstance().isServer();
//                    }
                    break;
                case 2:
                    P1.freeze();
                    break;
                case 3:
                    protectEagle = true;
                    eagle.protection = 3;
                    protectEagleTmr = ProtectEagleTime;
                    for (int[] eaglePo : eaglePos) {
                        levelObjects.get(eaglePo[1]).set(eaglePo[0], null);
                    }
                    break;
            }
        }
    }

    private void checkCollisionPlayerWithGold() {
        if(gold.isAvailable() && P1.collides_with(gold)) {
            currentObj[6] = true;
            P1.stageScore += 800;
//            ((TankActivity)context).updateGold(1);
            gold.setTaken();
            TankView.GOLD_LEVEL = level;
            ((TankActivity)context).saveInt(TankActivity.GOLD_LEVEL,level);
            SoundManager.playSound(Sounds.TANK.FIND_GOLD);
        }
    }

    public void updateP1Lives(int life) {
        P1.lives += life;
        P1.updateLifeView();
    }

    public void giftLife() {
        if(twoPlayers &&  P1.lives > 1) {
            notifyGiftLife = true;
        }
    }

    private void doGameLogic(){
//        if(twoPlayers && !playerReady && !WifiDirectManager.getInstance().isServer()) {
//            waitPlayer();
//            return;
//        }
//        if(notifyPause) {
//            notifyPause = false;
//            pause();
//        }
        if(notifyGiftLife) {
            notifyGiftLife = false;
            sendPlayerInfo(GIFT_LIFE);
            P1.loseLife();
        }
        if(notifyReceivedLife) {
            notifyReceivedLife = false;
            P1.applyTank();
        }
        if(notifyRetryStage) {
            int games  = ((TankActivity)context).settings.getInt(TankActivity.RETRY_COUNT,0);
            games--;
            SharedPreferences.Editor editor;
            editor = ((TankActivity)context).settings.edit();
            editor.putInt(TankActivity.RETRY_COUNT,games);
            editor.commit();
            retryStage();
            return;
        }
        if(notifyEndGame) {
            notifyEndGame = false;
//            pauseNoAds();
            TankView.EVENT = TankView.END_GAME;
//            if(((TankActivity)context).mInterstitialAd == null) {
////                ((TankActivity) context).loadInterstitialAd();
//                ((TankActivity) context).loadRewardedInterstitialAd();
//            }
////            ((TankActivity) context).showInterstitialAd();
//            ((TankActivity) context).showRewardedInterstitialAd();
            ((TankActivity)context).endGame();
        }

        if(((Enemy.lives <= 0 && Enemies.size() <= 0) || notifyStageComplete) && !stageComplete) {
//            pauseNoAds();
            TankView.EVENT = TankView.STAGE_COMPLETE;
            sendPlayerInfo(STAGE_COMPLETE);
//            if(((TankActivity)context).mInterstitialAd == null) {
////                ((TankActivity) context).loadInterstitialAd();
//                ((TankActivity) context).loadRewardedInterstitialAd();
//            }
////            ((TankActivity) context).showInterstitialAd();
//            ((TankActivity) context).showRewardedInterstitialAd();
            doStageComplete();
        }
        else if((((!twoPlayers && P1.lives <= 0) || (twoPlayers && P1.lives <= 0 && P2.lives <= 0)) || (eagle != null && eagle.isDestroyed()) || notifyGameOver) && !gameover) {

            if(eagle != null && eagle.isDestroyed()) {
//                pauseNoAds();
                TankView.EVENT = TankView.GAME_OVER;
                sendPlayerInfo(GAME_OVER);
                doGameOver();
            }

            else {
                if ( CHECKING_RETRY == 0) {
                    pauseNoAds();
                    doCheckRetry();
                } else if (CHECKING_RETRY == 2 || CHECKING_RETRY == 3) {
                    // Got new life
                    CHECKING_RETRY = 0;
                } else if (CHECKING_RETRY == 4) {
                    // Did not get new life
                    CHECKING_RETRY = 0;
                    SoundManager.stopSounds();
                    pauseNoAds();
                    TankView.EVENT = TankView.GAME_OVER;
                    sendPlayerInfo(GAME_OVER);
                    doGameOver();
                }
            }

        }

        if((gameover || stageComplete) && showScoreTmr == 5) {
            if(!showingScore) {
                pauseNoAds();
                if(CheckAdd.getInstance().transition()) {
                    if (((TankActivity) context).mInterstitialAd == null) {
                        ((TankActivity) context).loadRewardedInterstitialAd();
                    }
                    ((TankActivity) context).showRewardedInterstitialAd(false);
                }
                else {
                    resumeNoAds();
                }
            }
        }

        if((gameover || stageComplete) && showScoreTmr <= 0) {
            showScores();
        }
        else if ((gameover || stageComplete) && !showingScore) {
            --showScoreTmr;
        }
        if(showingScore) {
            return;
        }
//        checkCollisionTwoTanks(P1,P2);
        if(drawStarted && !startSound) {
            SoundManager.playSound(Sounds.TANK.GAMESTART,1,3);
            Log.d("SOUND", "Played start sound");
            startSound = true;
        }

//        sendToWifi();
        moveCurtain();
        if(movingCurting && curtainFrame >= 13) {
            closingCurtain = false;
            openingCurtain = true;
            curtainFrame = 0;
            curtainPauseTmr = curtainPauseTime;
            curtainPause = true;
            ((TankActivity)context).curtainTxt.setText(new StringBuilder().append("STAGE ").append(level).toString());
            ((TankActivity)context).curtainTxt.setVisibility(View.VISIBLE);
        }
        if(movingCurting && curtainFrame >= 12 && openingCurtain) {
            movingCurting = false;
        }

        if(freeze && freezeTmr > 0) {
            --freezeTmr;
        }
        else {
            freeze = false;
        }


        if(protectEagle && protectEagleTmr > 0) {
            --protectEagleTmr;
        }
        else if(protectEagle){
            for (int[] eaglePo : eaglePos) {
                levelObjects.get(eaglePo[1]).set(eaglePo[0], new Brick(eaglePo[0], eaglePo[1]));
            }
            protectEagle = false;
            eagle.protection = 2;
        }

        if(new_hve && HVE.isViewing()){
            sendToWifi();
            return;
        }

        if(twoPlayers) {
            levelObjectsUpdate.clear();
            levelBushesUpdate.clear();
        }

        if(!twoPlayers || WifiDirectManager.getInstance().isServer()) {
            for (int i = 0; i < Enemies.size(); i++) {
                if (Enemies.get(i).recycle) {
                    Enemies.set(i, null);
                }
            }
            // Remove all recycled enemy
            while (Enemies.remove(null)) ;

            generateEnemy();

//            checkCollisionPlayer(P1);
            checkCollisionPlayerBullet(P1);
            checkCollisionPlayerWithBonus(P1, bonus);
            checkCollisionEnemyWithBonus(bonus);
            checkCollisionEnemyBullet(P1);
            checkCollisionPlayerBomb(P1);
            if(twoPlayers){
                checkCollisionEnemyBulletWithPlayer(P2);
            }

        }

        if(twoPlayers && !WifiDirectManager.getInstance().isServer()) {
            checkCollisionPlayerBullet(P1);
            checkCollisionPlayerWithBonus(P1, bonus);
            checkCollisionEnemyBulletWithPlayer(P1);
        }
            checkCollisionPlayer(P1);
            checkCollisionPlayerWithGold();
            P1.update();
            if(twoPlayers) {
                P2.updateBullets();
            }

        if(!twoPlayers || WifiDirectManager.getInstance().isServer()) {
            // Get target

//            Rect pRect = P1.getRect();
            Point targ;
            for (Enemy e : Enemies) {
                targ = getTarget(e);
                e.setTarget(targ);
            }

            for (Enemy e : Enemies) {
                e.changeDirection();
            }

            checkCollisionEnemy();

            for (Enemy e : Enemies) {
                e.update(false);
            }
        }
        else {
            checkCollisionEnemy();
            for (Enemy e : Enemies) {
                e.update(true);
            }
        }

        sendToWifi();
    }


    private Point getTarget(Enemy e) {
        int min_dist = TankView.HEIGHT + TankView.WIDTH;
        Point targ = new Point();
        if (e.type == ObjectType.ST_TANK_A || e.type == ObjectType.ST_TANK_D) {
            int dx = (int) (e.x - P1.x);
            int dy = (int) (e.y - P1.y);
            int dist = dx + dy;
            if (dist < min_dist) {
                min_dist = dist;
                targ.x = (int) (P1.x + P1.w / 2);
                targ.y = (int) (P1.y + P1.h / 2);
            }
        }

        if(!e.isHVE()) {
            int dx = e.getRect().left - eagle.getRect().left;
            int dy = e.getRect().top - eagle.getRect().top;
            int dist = dx + dy;
            if (dist < min_dist) {
                min_dist = dist;
                targ.x = (int) (eagle.x + eagle.w / 2);
                targ.y = (int) (eagle.y + eagle.h / 2);
            }
        }

        if(bonus.isAvailable()) {
            float prob = groupProb(0.2f,0.9f);
            if(Math.random() < prob) {
                Point btarg = new Point();
                btarg.x = bonus.x;
                btarg.y = bonus.y;
                int bdist = bonus.x + bonus.y;
                if(bdist < min_dist) {
                    min_dist = bdist;
                    targ.x = bonus.x;
                    targ.y = bonus.y;
                }
            }

        }
        return targ;
    }

    private int getCompletedObjectives(int level) {
        int completed = 0;
        boolean[] obj = objectives.get(level-1);
        for(boolean i:obj) {
            if(i) {
                ++completed;
            }
        }
        return completed;
    }

    public void doCheckRetry() {
        if(CHECKING_RETRY == 0) {
            CHECKING_RETRY = 1;
            TankEndGameDialog wd = new TankEndGameDialog((TankActivity) context, this);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(wd.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            wd.show();
            wd.getWindow().setAttributes(lp);
        }
    }

    public void doGameOver() {
        SoundManager.stopSounds();
//        try{SoundManager.stopSound(SCENE_SOUND);}
//        catch (Exception e){}
//        try{SoundManager.stopSound(Sounds.TANK.HVE_SOUND);}
//        catch (Exception e){}

        for(int i = 2; i < P1.killTime.size(); i ++) {
            if(P1.killTime.get(i) - P1.killTime.get(i-2) <= 10000) {
                currentObj[1] = true;
            }
            if(i > 4) {
                if(P1.killTime.get(i) - P1.killTime.get(i-4) <= 10000) {
                    currentObj[2] = true;
                }
            }
        }


        boolean lo = false;
        for(int i = 0; i < levelObjects.size(); i++) {
            for(int j = 0; j < levelObjects.get(i).size(); j ++) {
                if(levelObjects.get(i).get(j) != null) {
                    currentObj[5] = false;
                    lo = true;
                    break;
                }

            }
        }
        if(!lo) {
            for (int i = 0; i < levelBushes.size(); i++) {
                if (levelBushes.get(i) != null) {
                    currentObj[5] = false;
                    break;
                }
            }
        }

        if(P1.stageScore > 6000) {
            currentObj[10] = true;
        }


        ((TankActivity)context).saveObjectives(objectives);
//        ((TankActivity) context).nxtBtn.setText(R.string.retryTxt);
        P1.disableFire();
        P1.disableMove();
        gameover = true;
        enemyFrame = 0;
        ((TankActivity)context).disableControls();
        SoundManager.playSound(Sounds.TANK.GAMEOVER);
        displayGameOver();
        showScoreTmr = showScoreDelay;
//        sendPlayerInfo(GAME_OVER);
        ((TankActivity)context).stop_timer();


        for(int i = 0; i < currentObj.length; i++) {
            if(i == 0 || i == 3 || i == 4 || i == 8 || i == 9 || objectives.get(level-1)[i]) {
                //Objective already completed. Objectives 0,3,4,8 and 9 can only be completed if game is completed
                continue;
            }
            if(currentObj[i]) {
                //Objective true completed
                objectives.get(level-1)[i] = true;
            }
        }

        ((TankActivity)context).saveObjectives(objectives);

        int completedObjectives = getCompletedObjectives(level);

        ArrayList<Integer> levelStars = ((TankActivity)context).loadStars();

        if(P1.stageScore < 1000) {
            numStars = 0;
        }
        else if(P1.stageScore < 4000) {
            numStars = 1;
        }
        else if (P1.stageScore > 6000 && completedObjectives >= 7) {
            numStars = 3;
        }
        else  {
            numStars = 2;
        }

        if(numStars > levelStars.get(level-1)) {
            levelStars.set(level-1,numStars);
            ((TankActivity)context).saveStars(levelStars);
        }

        ((TankActivity)context).nxtBtn.setAlpha(0.2f);
    }

    public void doStageComplete() {
        SoundManager.stopSounds();
//        try{SoundManager.stopSound(SCENE_SOUND);}
//        catch (Exception e){}
//        try{SoundManager.stopSound(Sounds.TANK.HVE_SOUND);}
//        catch (Exception e){}
        for(int i = 2; i < P1.killTime.size(); i ++) {
            if(currentObj[1] && currentObj[2]) {
                break;
            }
            if(!currentObj[1] && P1.killTime.get(i) - P1.killTime.get(i-2) <= 10000) {
                currentObj[1] = true;
            }
            if(i > 4) {
                if(!currentObj[2] && P1.killTime.get(i) - P1.killTime.get(i-4) <= 10000) {
                    currentObj[2] = true;
                }
            }
        }

        if(((TankActivity)context).playtime <= 120000) {
            currentObj[4] = true;
        }

        boolean lo = false;
        for(int i = 0; i < levelObjects.size(); i++) {
            for(int j = 0; j < levelObjects.get(i).size(); j ++) {
                if(levelObjects.get(i).get(j) != null) {
                    currentObj[5] = false;
                    lo = true;
                    break;
                }

            }
        }
        if(!lo) {
            for (int i = 0; i < levelBushes.size(); i++) {
                if (levelBushes.get(i) != null) {
                    currentObj[5] = false;
                    break;
                }
            }
        }

        if(P1.bulletIntercept >= 30) {
            currentObj[7] = true;
        }

        if(P1.stageScore > 6000) {
            currentObj[10] = true;
        }


        ((TankActivity)context).saveObjectives(objectives);
//        ((TankActivity) context).nxtBtn.setText(R.string.nextTxt);
        P1.stopShooting();
        P1.stopMoving();
        stageComplete = true;
        enemyFrame = 0;
        showScoreTmr = showScoreDelay;
//        sendPlayerInfo(STAGE_COMPLETE);
        ((TankActivity)context).stop_timer();

        for(int i = 0; i < currentObj.length; i++) {
            if(objectives.get(level-1)[i]) {
                //Objective already completed
                continue;
            }
            if(currentObj[i]) {
                //Objective just completed
                objectives.get(level-1)[i] = true;
                //TODO give reward for completing objective
            }
        }
        ((TankActivity)context).saveObjectives(objectives);

        int completedObjectives = getCompletedObjectives(level);

        ArrayList<Integer> levelStars = ((TankActivity)context).loadStars();

        if(P1.stageScore < 1000) {
            numStars = 0;
        }
        else if(P1.stageScore < 4000) {
            numStars = 1;
        }
        else if (P1.stageScore > 6000 && completedObjectives >= 7) {
            numStars = 3;
        }
        else  {
            numStars = 2;
        }

        if(numStars > levelStars.get(level-1)) {
            levelStars.set(level-1,numStars);
            ((TankActivity)context).saveStars(levelStars);
        }

//        level++;
        if(!twoPlayers) {
            saveNewStage(level + 1);
        }

        ((TankActivity)context).nxtBtn.setAlpha(1);
    }

    private void getRemoteUpdate() {
        if(!round_started) {
            return;
        }
//        if(twoPlayers && !gameModel.isEmpty()){
//        if(gameModel.isEmpty()){
//            return;
//        }
        Game m = null;
        try {
            m = gameModel.poll();
        }
        catch (Exception e){
            Log.d("Model", "Model exception");
            return;
        }

        if(!(m instanceof  TankGameModel)) {
            return;
        }
        TankGameModel model = (TankGameModel)m;
        playerReady = true;

        if(!eagle.destroyed){
            eagle.destroyed = model.eagleDestroyed;
        }

        if(model.gift_life) {
            notifyReceivedLife = true;
            return;
        }

        else if(model.gameOver){
            notifyGameOver = true;
            P2.totalKills = model.totalKills;
            P2.totalScore = model.totalScore;
            P2.stageScore = model.stageScore;
            for(int i = 0; i < P2.kills.length; i++) {
                P2.kills[i] = model.kills[i];
            }
            return;
        }
        else if(model.stageComplete) {
            notifyStageComplete = true;
            P2.totalKills = model.totalKills;
            P2.totalScore = model.totalScore;
            P2.stageScore = model.stageScore;
            for(int i = 0; i < P2.kills.length; i++) {
                P2.kills[i] = model.kills[i];
            }
            return;
        }
        else if(model.pause) {
            _pause();
            return;
        }
        else if(model.resume) {
            _resumeNoAds();
            return;
        }
        else if(model.restart) {
            _resumeNoAds();
            notifyRetryStage = true;
            return;
        }

        else if(model.end_game) {
            notifyEndGame = true;
            return;
        }

        long time = System.currentTimeMillis();
        long timeDiff = time - refReceiveStartTime;
        refReceiveStartTime = time;
        Log.d("Transfer time:", String.valueOf(timeDiff-model.time)+" "+timeDiff+" +"+model.time);

        Log.d("P2 ==> ", model.mPlayer.x + " " + model.mPlayer.y + " " + model.mPlayer.dirction);
        float scale = (float) TankView.this.getHeight() / model.height;


        P2.setModel(model.mPlayer, scale);


        if (!WifiDirectManager.getInstance().isServer()) {
            for (int i = 0; i < Enemies.size(); i++) {
                if (Enemies.get(i).recycle) {
                    Enemies.set(i, null);
                }
            }
            while (Enemies.remove(null)) ;
            boolean found;
            for (int i = 0; i < model.mEnemies.size(); i++) {
                found = false;
                for (int j = 0; j < Enemies.size(); j++) {
                    if(model.mEnemies.get(i).id == Enemies.get(j).id){  // enemy exists
                        if(!Enemies.get(j).isDestroyed()) {
                            Enemies.get(j).setModel(model.mEnemies.get(i), scale,false);
                        }
                        found = true;
                        break;
                    }
                }
                if(!found) {                                            // enemy does not exist. create one
                    ObjectType eType = ObjectType.ST_TANK_A;
                    switch (model.mEnemies.get(i).typeVal) {
                        case 0:
                            eType = ObjectType.ST_TANK_A;
                            break;
                        case 1:
                            eType = ObjectType.ST_TANK_B;
                            break;
                        case 2:
                            eType = ObjectType.ST_TANK_C;
                            break;
                        case 3:
                            eType = ObjectType.ST_TANK_D;
                            break;
                    }

                    Enemy e = new Enemy(eType,model.mEnemies.get(i).group, 0, 0);
                    e.setModel(model.mEnemies.get(i), scale, false);
                    Enemies.add(e);
                }
            }

            if(model.mPlayer.freeze) {
                P1.setFreeze();
            }
            else{
                P1.unFreeze();
            }

            if(model.mPlayer.bBomb) {
                P1.setDestroyed();
            }


        }

        if (WifiDirectManager.getInstance().isServer()) {

            for (int i = 0; i < model.mEnemies.size(); i++) {
                for (int j = 0; j < Enemies.size(); j++) {
                    if(model.mEnemies.get(i).id == Enemies.get(j).id){
                        if(!Enemies.get(j).isDestroyed()) {
                            Enemies.get(j).setModel(model.mEnemies.get(i), scale, true);
                        }
                        break;
                    }
                }
            }

            if(model.mPlayer.gotBonus == Bonus.CLOCK) {
                TankView.freeze = true;
                TankView.freezeTmr = TankView.FreezeTime;
            }


//            else if(model.mPlayer.gotBonus == Bonus.GRENADE) {
//
//            }


        }

        for(int[] l:model.lObjects) {

            if(l[2] == 0) {
                levelObjects.get(l[0]).set(l[1],null);
            }
            else if(levelObjects.get(l[0]).get(l[1]) != null) {
                if (l[2] == 6) {
                    ((Brick) levelObjects.get(l[0]).get(l[1])).collidsWithBullet(CONST.Direction.UP);
                } else if (l[2] == 7) {
                    ((Brick) levelObjects.get(l[0]).get(l[1])).collidsWithBullet(CONST.Direction.DOWN);
                } else if (l[2] == 8) {
                    ((Brick) levelObjects.get(l[0]).get(l[1])).collidsWithBullet(CONST.Direction.LEFT);
                } else if (l[2] == 9) {
                    ((Brick) levelObjects.get(l[0]).get(l[1])).collidsWithBullet(CONST.Direction.RIGHT);
                }
            }
        }

        for(int i:model.lBushes) {
            levelBushes.set(i,null);
        }


        if(model.eagleProtection == 1) {
            for (int[] eaglePo : eaglePos) {
                levelObjects.get(eaglePo[1]).set(eaglePo[0], new StoneWall(eaglePo[0], eaglePo[1]));
            }
        }
        else if(model.eagleProtection == 2) {
            for (int[] eaglePo : eaglePos) {
                levelObjects.get(eaglePo[1]).set(eaglePo[0], new Brick(eaglePo[0], eaglePo[1]));
            }
        }
        else if(model.eagleProtection == 3) {
            for (int[] eaglePo : eaglePos) {
                levelObjects.get(eaglePo[1]).set(eaglePo[0], null);
            }
        }

        bonus.setBonus((int)(model.bonus[0]*scale),(int)(model.bonus[1]*scale),model.bonus[2],model.bnsAv,model.bnsClr,model.bonus[3]);

    }

    public void sendToWifi() {
        if(twoPlayers) {
            TankGameModel sendModel = new TankGameModel();
            long time = System.currentTimeMillis();
            sendModel.time = time- refSendStartTime;
            refSendStartTime = time;
//            if(WifiDirectManager.getInstance().isServer()) {
            sendModel.loadEnemies(Enemies);
            sendModel.loadLevelObjects(levelObjectsUpdate);
            sendModel.loadLevelBushes(levelBushesUpdate);
//            }
            sendModel.loadPlayer(P1);
            sendModel.eagleDestroyed = eagle.isDestroyed();
            sendModel.eagleProtection = eagle.protection;
            sendModel.loadBonus(bonus.x, bonus.y, bonus.getBonus(), Bonus.available, Bonus.cleared, Bonus.id);
            WifiDirectManager.getInstance().sendMessage(sendModel);
//            Gson gson = new Gson();
//            WifiDirectManager.getInstance().sendMessage(gson.fromJson(gson.toJson(sendModel), TankGameModel.class));
            eagle.protection = 0;
            Bonus.cleared = false;
            P1.gotBonus = 0;
            P1.setBBomb(false);

            for(Bullet b: P1.bullets) {
                if(b.launched) {
                    b.launched = false;
                }
            }
            for(Enemy e: Enemies) {
                for(Bullet b: e.bullets) {
                    if(b.launched) {
                        b.launched = false;
                    }
                }
            }

        }
    }

    public void sendPlayerInfo(int info) {
        if(!twoPlayers) {
            return;
        }
        TankGameModel model = new TankGameModel();
        switch (info) {
            case STAGE_COMPLETE:
                model.stageComplete = true;
                model.loadPlayerKills(P1.kills);
                model.totalKills = P1.totalKills;
                model.totalScore = P1.totalScore;
                model.stageScore = P1.stageScore;
                break;
            case GAME_OVER:
                model.gameOver = true;
                model.loadPlayerKills(P1.kills);
                model.totalKills = P1.totalKills;
                model.totalScore = P1.totalScore;
                model.stageScore = P1.stageScore;
                break;
            case PAUSE:
                model.pause = true;
                break;
            case RESUME:
                model.resume = true;
                break;
            case RESTART:
                model.restart = true;
                break;
            case END_GAME:
                model.end_game = true;
                break;
            case GIFT_LIFE:
                model.gift_life = true;
                break;

        }
        WifiDirectManager.getInstance().sendMessage(model);
    }

    public void generateEnemy() {
        ++new_enemy_time;
        if(new_enemy_time > genEnemyTime) {
            int group = 1;
            float g1 = groupProb(0.8f,0.02f);
            float g2 = groupProb(0.15f,0.03f);
            float g3 = groupProb(0.03f,0.15f);
            float g4 = groupProb(0.02f,0.80f);




            float g = (float)(Math.random());

            if(g < g1) {
                group = 1;
            }
            else if(g < g1+g2) {
                group = 2;
            }
            else if(g < g1+g2+g3) {
                group = 3;
            }
//            else if(g <= g1+g2+g3+g4) {
            else{
                group = 4;
            }


            Log.d("EProb", String.valueOf(g) + " | " + g1 + " " + g2 + " " + g3 + " " + g4 +" | " + group);


            float e = (float)(Math.random());
            int type = (e < 0.2 + LPROB*level) ? 3 : (int)(Math.random()*3)%3;
            ObjectType tank_type = ObjectType.ST_TANK_A;
            switch (type) {
                case 0:
                    tank_type = ObjectType.ST_TANK_A;
                    break;
                case 1:
                    tank_type = ObjectType.ST_TANK_B;
                    break;
                case 2:
                    tank_type = ObjectType.ST_TANK_C;
                    break;
                case 3:
                    tank_type = ObjectType.ST_TANK_D;
                    break;
            }

            if(Enemies.size() < MAX_ENEMIES && Enemy.lives > 0) {
                Enemy enemy;
                if(Enemy.lives <= 2*NUM_HVE) {
                    if(Enemy.lives <= HVE_LIVES) {
                        float v = 0.5f*(float)Math.pow(10,level*Math.log10(2.2)/NUM_LEVELS);
                        float vb = 0.8f*(float)Math.pow(10,level*Math.log10(1.75)/NUM_LEVELS);
                        enemy = new HVE(0, 0,v,vb);
                        --HVE_LIVES;
                        new_hve = true;
                    }
                    else {
                        float pHVE = (float)(Math.random());
                        if(pHVE < 0.5) {
                            float v = 0.5f*(float)Math.pow(10,level*Math.log10(2.2)/NUM_LEVELS);
                            float vb = 0.8f*(float)Math.pow(10,level*Math.log10(1.75)/NUM_LEVELS);
                            enemy = new HVE(0, 0, v, vb);
                            --HVE_LIVES;
                            new_hve = true;
                        }
                        else {
                            enemy = new Enemy(tank_type, group, 0, 0);
                            new_hve = false;
                        }
                    }
                }
                else {
                    enemy = new Enemy(tank_type, group, 0, 0);
                    new_hve = false;
                }

                int p = (int) (Math.random() * 3) % 3;
//                int px = p * 6*enemy.w;
//                px = (px > TankView.WIDTH - enemy.w) ? (int) (TankView.WIDTH - enemy.w) : px;
                enemy.x = p * 6*enemy.w;
                enemy.y = 0;
                if(new_hve) {
                    ((HVE)enemy).getView(P1);
                }
                if(Math.random() < 0.2) {
                    enemy.hasBonus = true;
                }
                Enemies.add(enemy);
                --Enemy.lives;
                int row = (int)(Math.ceil((Enemy.lives+1.0)/2));
                int col = (Enemy.lives+1)%2 == 0 ? 1:0;
//                TankView.enemyCount.get(row-1).get(col).setBackground(null);
                setEnemyCountView();
            }
            new_enemy_time = 0;
        }
    }

    private float groupProb(float min, float max) {
        return (max-min)*level/NUM_LEVELS + min;
    }

    public void applyBonus(String tag) {

        if(tag.equals(TankActivity.GRENADE)) {
            for(Enemy e:Enemies) {
                if(e.isHVE()) {
                    if(((HVE)e).getShiled() > 0) {
                        int hveShield = ((HVE)e).reduceShiled(1);
                        if(hveShield > 0 ) {
                            continue;
                        }
                    }
                }
                e.setDestroyed();
                e.svrKill = TankView.twoPlayers && WifiDirectManager.getInstance().isServer();
            }
        }
        else if(tag.equals(TankActivity.CLOCK)) {
            TankView.freeze = true;
            TankView.freezeTmr = TankView.FreezeTime;
        }
        else if(tag.equals(TankActivity.SHOVEL)) {
            protectEagle = true;
            eagle.protection = 1;
            protectEagleTmr = ProtectEagleTime;
            for (int[] eaglePo : eaglePos) {
                levelObjects.get(eaglePo[1]).set(eaglePo[0], new StoneWall(eaglePo[0], eaglePo[1]));
            }
        }
        else if(tag.equals(TankActivity.TANK)) {
            P1.applyTank();
        }
        else if(tag.equals(TankActivity.GUN)) {
            P1.applyGun();
        }
        else if(tag.equals(TankActivity.BOAT)) {
            P1.applyBoat();
        }
        else if(tag.equals(TankActivity.STAR)) {
            P1.applyStar();
        }
        else if(tag.equals(TankActivity.SHIELD)) {
            P1.applyShield();
        }
        else if(tag.equals(TankActivity.MINE)) {
            P1.applyMine();
        }
    }


    @Override
    public void onDraw(Canvas canvas) {
//        if(mCurrentState == TankView.State.Stopped) {
//            return;
//        }

        if(showingScore) {
            return;
        }

        super.onDraw(canvas);

        if(!mInitialized) {
            return;
        }

        long now = System.currentTimeMillis();

        drawing = true;
        gold.draw(canvas);

        if(!twoPlayers || (twoPlayers && !updatingRemote)) {
            for (ArrayList<GameObjects> rowsObjs : levelObjects) {
                for (GameObjects obj : rowsObjs) {
                    if (obj != null) {
                        obj.draw(canvas);
                    }
                }
            }
        }

        if(P1 != null) {
            P1.draw(canvas);
        }

        if(P2 != null && !updatingRemote) {
            P2.draw(canvas);
        }

        eagle.draw(canvas);

        if(!twoPlayers || (twoPlayers && !updatingRemote)){
            for (Tank e : Enemies) {
                e.draw(canvas);
            }
        }

        for(Bush bush:levelBushes) {
            if(bush != null) {
                bush.draw(canvas);
            }
        }

//        bomb.draw(canvas);
        bonus.draw(canvas);

        if(movingCurting) {
            if(openingCurtain) {
                curtainTRect.left = 0;
                curtainTRect.top = 0;
                curtainTRect.right = WIDTH;
                curtainTRect.bottom = tile_dim*(13-curtainFrame);

                curtainBRect.left = 0;
                curtainBRect.top = tile_dim*(12+curtainFrame);
                curtainBRect.right = WIDTH;
                curtainBRect.bottom = HEIGHT;
            }
            else if(closingCurtain) {
                curtainTRect.left = 0;
                curtainTRect.top = 0;
                curtainTRect.right = WIDTH;
                curtainTRect.bottom = tile_dim*(curtainFrame);

                curtainBRect.left = 0;
                curtainBRect.top = tile_dim*(26-curtainFrame);
                curtainBRect.right = WIDTH;
                curtainBRect.bottom = HEIGHT;
            }
            canvas.drawRect(curtainTRect,curtainPaint);
            canvas.drawRect(curtainBRect,curtainPaint);
        }
        drawStarted = true;
        drawing = false;

        long diff = System.currentTimeMillis() - now;
//        Log.d("DRAW TIME", String.valueOf(diff));

    }


    @Override
    public void onMessageReceived(Game message) {
        if(!mInitialized || P2 == null) {
            return;
        }
        gameModel.add(message);
    }

    private void buttonPressed(int dir) {
        switch (dir) {
            case CONST.Direction.UP: {
                ((TankActivity)context).upBtn.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.up31_btn,null));

                ((TankActivity)context).dwnBtn.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.down30_btn,null));
                ((TankActivity)context).lftBtn.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.left30_btn,null));
                ((TankActivity)context).rtBtn.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.right30_btn,null));
                break;
            }
            case CONST.Direction.DOWN: {
                ((TankActivity)context).dwnBtn.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.down31_btn,null));

                ((TankActivity)context).upBtn.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.up30_btn,null));
                ((TankActivity)context).lftBtn.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.left30_btn,null));
                ((TankActivity)context).rtBtn.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.right30_btn,null));
                break;
            }
            case CONST.Direction.LEFT: {
                ((TankActivity)context).lftBtn.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.left31_btn,null));

                ((TankActivity)context).upBtn.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.up30_btn,null));
                ((TankActivity)context).dwnBtn.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.down30_btn,null));
                ((TankActivity)context).rtBtn.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.right30_btn,null));
                break;
            }
            case CONST.Direction.RIGHT: {
                ((TankActivity)context).rtBtn.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.right31_btn,null));

                ((TankActivity)context).upBtn.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.up30_btn,null));
                ((TankActivity)context).lftBtn.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.left30_btn,null));
                ((TankActivity)context).dwnBtn.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.down30_btn,null));
                break;
            }
            default: {
                ((TankActivity)context).rtBtn.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.right30_btn,null));
                ((TankActivity)context).upBtn.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.up30_btn,null));
                ((TankActivity)context).lftBtn.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.left30_btn,null));
                ((TankActivity)context).dwnBtn.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.down30_btn,null));
                break;
            }
        }
    }

    @Override
    public void onButtonPressed(View v, MotionEvent m) {
        Log.d("Button Pressed", "Strick move");

//        if (v.getId() == R.id.navStick) {
//            if (m.getX() < 0) {
//                P1.move(CONST.Direction.LEFT);
//            } else if (m.getX() > ((TankActivity) context).stick.getWidth()) {
//                P1.move(CONST.Direction.RIGHT);
//            } else if (m.getY() > ((TankActivity) context).stick.getHeight()) {
//                P1.move(CONST.Direction.DOWN);
//            } else if(m.getY() < ((TankActivity) context).stick.getHeight()) {
//                P1.move(CONST.Direction.UP);
//            }
//        }

        if(v.getId() == R.id.upBtn) {
            if (m.getX() < 0) {
                P1.move(CONST.Direction.LEFT);
                buttonPressed(CONST.Direction.LEFT);
            } else if (m.getX() > ((TankActivity) context).upBtn.getWidth()) {
                P1.move(CONST.Direction.RIGHT);
                buttonPressed(CONST.Direction.RIGHT);
            } else if (m.getY() > ((TankActivity) context).upBtn.getHeight()) {
                P1.move(CONST.Direction.DOWN);
                buttonPressed(CONST.Direction.DOWN);
            } else {
                P1.move(CONST.Direction.UP);
                buttonPressed(CONST.Direction.UP);
            }

            if(m.getAction() == MotionEvent.ACTION_UP) {
                buttonPressed(-1);
            }
        }

        else  if (v.getId() == R.id.downBtn) {
            if (m.getX() < 0) {
                P1.move(CONST.Direction.LEFT);
                buttonPressed(CONST.Direction.LEFT);
            } else if (m.getX() > ((TankActivity) context).dwnBtn.getWidth()) {
                P1.move(CONST.Direction.RIGHT);
                buttonPressed(CONST.Direction.RIGHT);
            } else if (m.getY() < 0) {
                P1.move(CONST.Direction.UP);
                buttonPressed(CONST.Direction.UP);
            } else {
                P1.move(CONST.Direction.DOWN);
                buttonPressed(CONST.Direction.DOWN);
            }

            if(m.getAction() == MotionEvent.ACTION_UP) {
                buttonPressed(-1);
            }
        }

        else  if (v.getId() == R.id.leftBtn) {
            if (m.getY() < 0) {
                P1.move(CONST.Direction.UP);
                buttonPressed(CONST.Direction.UP);
            } else if (m.getY() > ((TankActivity) context).lftBtn.getHeight()) {
                P1.move(CONST.Direction.DOWN);
                buttonPressed(CONST.Direction.DOWN);
            } else if (m.getX() > ((TankActivity) context).lftBtn.getWidth()) {
                P1.move(CONST.Direction.RIGHT);
                buttonPressed(CONST.Direction.RIGHT);
            } else {
                P1.move(CONST.Direction.LEFT);
                buttonPressed(CONST.Direction.LEFT);
            }

            if(m.getAction() == MotionEvent.ACTION_UP) {
                buttonPressed(-1);
            }
        }

        else  if (v.getId() == R.id.rightBtn) {
            if (m.getY() < 0) {
                P1.move(CONST.Direction.UP);
                buttonPressed(CONST.Direction.UP);
            } else if (m.getY() > ((TankActivity) context).rtBtn.getHeight()) {
                P1.move(CONST.Direction.DOWN);
                buttonPressed(CONST.Direction.DOWN);
            } else if (m.getX() < 0) {
                P1.move(CONST.Direction.LEFT);
                buttonPressed(CONST.Direction.LEFT);
            } else {
                P1.move(CONST.Direction.RIGHT);
                buttonPressed(CONST.Direction.RIGHT);
            }

            if(m.getAction() == MotionEvent.ACTION_UP) {
                buttonPressed(-1);
            }
        }
        else  if (v.getId() == R.id.shootBtn) {
            P1.startShooting();
        }

        if(m.getAction() == MotionEvent.ACTION_UP) {
            if(v.getId()== R.id.shootBtn) {
                ((TankActivity)context).shtBtn.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.shoot30_btn,null));
                P1.stopShooting();
            }

            else if(v.getId()== R.id.bombBtn) {
                ((TankActivity)context).bmbBtn.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.mine30_btn,null));
                P1.dropBomb();
            }

            else {
                P1.stopMoving();
            }
        }
        if(m.getAction() == MotionEvent.ACTION_DOWN) {
            if(v.getId() == R.id.shootBtn) {
                ((TankActivity)context).shtBtn.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.shoot31_btn,null));
                TankView.vibrate();
//                P1.fire();
            }
            else if(v.getId() == R.id.bombBtn) {
                ((TankActivity)context).bmbBtn.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.mine31_btn,null));
                P1.activateBomb();
            }
        }
    }



    /**
     * Reset the lives, paddles and the like for a new game.
     */
    public void newGame() {
    }

    /**
     * This is kind of useless as well.
     */
    private void resumeLastState() {
        if(mLastState == TankView.State.Stopped && mCurrentState == TankView.State.Stopped) {
            mCurrentState = TankView.State.Running;
        }
        else if(mCurrentState != TankView.State.Stopped) {
            // Do nothing
        }
        else if(mLastState != TankView.State.Stopped) {
            mCurrentState = mLastState;
            mLastState = TankView.State.Stopped;
        }
    }

    public boolean gameRunning() {
        // TODO
//        return mInitialized && P2 != null && P1 != null
//                && P2.isAlive() && P1.isAlive();
        return true;
    }


    public void enablePause(boolean enable) {
        ((TankActivity)context).pauseBtn.setEnabled(enable);
    }

    public void resumeNoAds() {
        if(!SoundManager.isActive(SCENE_SOUND)) {
            SoundManager.playSound(SCENE_SOUND,true);
        }
        SoundManager.resumeSound(SCENE_SOUND);
        sendPlayerInfo(RESUME);
//        enablePause(true);
//        ((TankActivity)context).enableControls();
        ((TankActivity)TankView.context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                enablePause(true);
                ((TankActivity)context).enableControls();
            }
        });
        mLastState = TankView.State.Stopped;
        mCurrentState = State.Running;
//        SoundManager.resumeSounds();
    }

    public void _resumeNoAds() {
        if(!SoundManager.isActive(SCENE_SOUND)) {
            SoundManager.playSound(SCENE_SOUND,true);
        }
        SoundManager.resumeSound(SCENE_SOUND);
        ((TankActivity)TankView.context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                enablePause(true);
                ((TankActivity)context).enableControls();
            }
        });

        mLastState = TankView.State.Stopped;
        mCurrentState = State.Running;
    }

    public void pauseNoAds() {
        SoundManager.pauseSound(SCENE_SOUND);
        mCurrentState = TankView.State.Stopped;
        mLastState = State.Running;
//        SoundManager.pauseSounds();
    }

    public void interrupt() {
        sendPlayerInfo(PAUSE);
//        TankView.EVENT = TankView.PAUSE;
        enablePause(false);
        ((TankActivity)context).disableControls();
        pauseNoAds();
//        mCurrentState = TankView.State.Stopped;
//        mLastState = State.Running;
//        ((TankActivity)context).pauseBtn.setText(R.string.continueTxt);
//        SoundManager.pauseSounds();
    }


    public void pause() {

        SoundManager.playSound(Sounds.TANK.PAUSE);

        pauseNoAds();
        enablePause(false);

        sendPlayerInfo(PAUSE);
        ((TankActivity)context).disableControls();
        ((TankActivity) context).openPauseDialog(this);
        TankView.EVENT = TankView.PAUSE;
//        ((TankActivity) context).loadInterstitialAd();
//        ((TankActivity) context).showInterstitialAd();
        if(CheckAdd.getInstance().click()) {
            ((TankActivity) context).loadRewardedInterstitialAd();
            ((TankActivity) context).showRewardedInterstitialAd(true);
        }
    }

    public void _pause() {
        SoundManager.playSound(Sounds.TANK.PAUSE);
        pauseNoAds();

        ((TankActivity)TankView.context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                enablePause(false);
                ((TankActivity)context).disableControls();
                TankView.EVENT = TankView.PAUSE;
//                ((TankActivity) context).loadInterstitialAd();
//                ((TankActivity) context).showInterstitialAd();
                if(CheckAdd.getInstance().click()) {
                    ((TankActivity) context).loadRewardedInterstitialAd();
                    ((TankActivity) context).showRewardedInterstitialAd(true);
                }
            }
        });


    }




    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }

    public void setPlayerControl(boolean twoPlayers) {
        this.twoPlayers = twoPlayers;
    }

    public void setVibrate(boolean vibrate) {
        mVibrate = vibrate;
    }

    public void setSound(boolean sound) {
        SoundManager.setSound(sound);
    }

    public static void vibrate() {
        if(!mVibrate) {
            return;
        }
        // Vibrate for 20 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mVibrator.vibrate(VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            mVibrator.vibrate(10);
        }
    }

    public static void vibrate(int time) {
        if(!mVibrate) {
            return;
        }
        // Vibrate for time milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mVibrator.vibrate(VibrationEffect.createOneShot(time, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            mVibrator.vibrate(time);
        }
    }

    public void resume() {

        if(mEndGame) {
            mInitialized = false;
        }
        mEndGame = false;
        mContinue = true;
        update();
    }

    public void stop() {
        mContinue = false;
    }

    /**
     * Release all resource locks.
     */
    public void release() {
        should_end = true;
        mContinue = false;
        mEndGame = true;

//        SoundManager.cleanup();
        showingScore = false;
        mInitialized = false;
        drawStarted = false;
        drawing = false;
        mRedrawHandler = null;

//        P2 = null;
//        P1 = null;
//        eagle = null;
//        tankBitmap = null;
//        tankSprite = null;
//        eCountImg = null;
//        eCountBm = null;
//        Enemies = null;
//        levelObjects = null;
//        levelBushes = null;
//        bonus = null;
//        gameModel = null;
//        level = 0;

    }

    private char[][] loadStage(String name) {
        String stage = ((TankActivity)context).settings.getString(name,null);
        if(stage == null) {
            return new char[26][26];
        }
        Type type = new TypeToken<char[][]>() {}.getType();
        Gson gson = new Gson();
        return gson.fromJson(stage,type);
    }

    private ArrayList<String> loadStageNames() {
        String stageNames = ((TankActivity)context).settings.getString(TankMenuActivity.STAGE_NAMES,null);
        if(stageNames == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        Gson gson = new Gson();
        return gson.fromJson(stageNames,type);
    }
}
