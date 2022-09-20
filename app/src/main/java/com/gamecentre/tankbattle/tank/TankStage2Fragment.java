package com.gamecentre.tankbattle.tank;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.gamecentre.tankbattle.R;
import com.gamecentre.tankbattle.connection.ClientConnectionThread;
import com.gamecentre.tankbattle.connection.ServerConnectionThread;
import com.gamecentre.tankbattle.model.Game;
import com.gamecentre.tankbattle.model.TankGameModel;
import com.gamecentre.tankbattle.sound.SoundManager;
import com.gamecentre.tankbattle.sound.Sounds;
import com.gamecentre.tankbattle.utils.CONST;
import com.gamecentre.tankbattle.utils.MessageRegister;
import com.gamecentre.tankbattle.utils.RemoteMessageListener;
import com.gamecentre.tankbattle.wifidirect.WifiDirectManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;


public class TankStage2Fragment extends Fragment implements View.OnTouchListener, RemoteMessageListener {

    View rootView;

    public AppCompatActivity activity;
    public Dialog d;
    //    public Button yes, no;
    SharedPreferences settings;
    boolean twoPlayers;
    ImageView playBtn, backBtn;
    TankTextView completedTxt;

    GridLayout stageBtns, objGrid;
    LinearLayout.LayoutParams cardParams, cardParamsSel;
    LinearLayout scrollBound;
    ScrollView scrollView;
    int selected = 0;
    ArrayList<boolean[]> objectives;
    private boolean selfDismiss = true;
    public static boolean p2Ready = false, opened = false;


    ArrayList<String> savedNames;
    char[][] stage;
    ListView stageListView;
    ArrayAdapter adapter;
    String stageName;

    public TankStage2Fragment() {

    }

    public TankStage2Fragment(AppCompatActivity a, boolean twoPlayers) {
        this.activity = a;
        this.twoPlayers = twoPlayers;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_tank_stage2, container, false);
        rootView.findViewById(R.id.frag2View).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        settings = activity.getSharedPreferences("TankSettings", 0);

        MessageRegister.getInstance().setMsgListener(this);
        savedNames = loadStageNames();

//        float SCALE = activity.getResources().getDisplayMetrics().density;
//        stageBtns = (GridLayout) rootView.findViewById(R.id.stage_grid);
        objGrid = (GridLayout) rootView.findViewById(R.id.objective_grid);
        scrollView = (ScrollView) rootView.findViewById(R.id.objScroll);

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        scrollBound = (LinearLayout)rootView.findViewById(R.id.scrollBound);
        scrollBound.setEnabled(false);

        rootView.findViewById(R.id.stage_title).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                SoundManager.playSound(Sounds.TANK.CLICK);
                switchStage();
                return true;
            }
        });

        stageListView = rootView.findViewById(R.id.stageList);

        adapter = new ArrayAdapter<String>(stageListView.getContext(),
                R.layout.save_list_view, savedNames);
        stageListView.setAdapter(adapter);

        stageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SoundManager.playSound(Sounds.TANK.CLICK);
                stageName = (String) ((TextView) view).getText();
                int count = adapterView.getCount();
                for(int c = 0; c < count; c++) {
                    ((TextView) adapterView.getChildAt(c)).setBackground(null);
                }
                view.setBackgroundColor(Color.LTGRAY);
                selected = adapterView.getPositionForView(view);
                Log.d("Adapter", "Clicked on view " + selected);
            }
        });

        playBtn = rootView.findViewById(R.id.playGameBtn);
        backBtn = rootView.findViewById(R.id.backGameBtn);

        playBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    int games = settings.getInt(TankActivity.RETRY_COUNT,0);

                    if(games > 0){

                        int level = selected + 1;

                        if (twoPlayers && !WifiDirectManager.getInstance().isServer() && ClientConnectionThread.serverStarted) {
                            //TODO Change to dialog
                            SoundManager.playSound(Sounds.TANK.CLICK2);
                            Toast toast = Toast.makeText(activity.getApplicationContext(),
                                    "Wait for player 1 to select stage!",
                                    Toast.LENGTH_SHORT);

                            ViewGroup group = (ViewGroup) toast.getView();
                            TextView messageTextView = (TextView) group.getChildAt(0);
                            messageTextView.setTextSize(25);

                            toast.show();
                            return true;
                        }
                        else if (twoPlayers && WifiDirectManager.getInstance().isServer() && ServerConnectionThread.serverStarted) {
                            if(p2Ready) {
                                SoundManager.playSound(Sounds.TANK.CLICK);
                                TankGameModel model = new TankGameModel();
                                model.mlevelInfo = true;
                                model.mlevel = level;
                                WifiDirectManager.getInstance().sendMessage(model);
                            }
                            else{
                                //TODO Change to dialog
                                SoundManager.playSound(Sounds.TANK.CLICK2);
                                Toast toast = Toast.makeText(activity.getApplicationContext(),
                                        "Player 2 not ready!",
                                        Toast.LENGTH_SHORT);

                                ViewGroup group = (ViewGroup) toast.getView();
                                TextView messageTextView = (TextView) group.getChildAt(0);
                                messageTextView.setTextSize(25);

                                toast.show();
                                return true;
                            }
                        }

                        TankView.level = level;
                        TankView.CONSTRUCTION = true;
                        //TODO
                        long game6h = settings.getLong(TankActivity.LIFE_TIME_6H,0);
                        if(game6h < System.currentTimeMillis()) {
                            --games;
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putInt(TankActivity.RETRY_COUNT, games);

                            if (games == CONST.Tank.MAX_GAME_COUNT - 1) {
                                editor.putLong(TankActivity.LIFE_TIME, System.currentTimeMillis());
                            }
                            editor.apply();
                        }
                        SoundManager.playSound(Sounds.TANK.CLICK);
                        ((TankMenuActivity) activity).startGame(twoPlayers);
                    }
                    else {
                        SoundManager.playSound(Sounds.TANK.CLICK2);
                        openGamePurchse();
                    }
                }
                return false;
            }
        });



//        displyObjectives(selected);
//        int completed = getCompleted(selected+1);
//        completedTxt.setText(String.format(Locale.ENGLISH,"CHALLENGES %d/%d", completed, TankView.NUM_OBJECTIVES));

        selfDismiss = true;
        opened = true;
        if (twoPlayers && !WifiDirectManager.getInstance().isServer() && ClientConnectionThread.serverStarted) {

            TankGameModel model = new TankGameModel();
            model.playerInfo = true;
            model.playerReady = true;
            WifiDirectManager.getInstance().sendMessage(model);
        }

        backBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    SoundManager.playSound(Sounds.TANK.CLICK);
                    selfDismiss = true;
                    if (twoPlayers && !WifiDirectManager.getInstance().isServer() && ClientConnectionThread.serverStarted) {

                        TankGameModel model = new TankGameModel();
                        model.playerInfo = true;
                        model.playerReady = false;
                        WifiDirectManager.getInstance().sendMessage(model);
                    }
                    dismiss();
                }
                return true;
            }
        });

        return rootView;
    }

    @Override
    public boolean onTouch(View v, MotionEvent m) {
        if(m.getAction() == MotionEvent.ACTION_DOWN) {
            SoundManager.playSound(Sounds.TANK.CLICK);
            int level = (int) (v.getTag());
            int completed = getCompleted(level);
            completedTxt.setText(String.format(Locale.ENGLISH,"CHALLENGES %d/%d", completed, TankView.NUM_OBJECTIVES));
            ((CardView) stageBtns.getChildAt(selected)).setCardBackgroundColor(Color.TRANSPARENT);
            selected = level - 1;
            ((CardView) stageBtns.getChildAt(selected)).setCardBackgroundColor(Color.WHITE);
            displyObjectives(selected);
            scrollView.fullScroll(ScrollView.FOCUS_UP);
        }

        return true;
    }


    @Override
    public void onMessageReceived(Game message) {
        if(message instanceof TankGameModel) {
            TankGameModel msg = (TankGameModel)message;
            if(twoPlayers && !WifiDirectManager.getInstance().isServer() && ClientConnectionThread.serverStarted) {
                if (msg.mlevelInfo) {
                    TankView.level = msg.mlevel;
                    int games = settings.getInt(TankActivity.RETRY_COUNT, 0);
                    --games;
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt(TankActivity.RETRY_COUNT, games);
                    if (games == CONST.Tank.MAX_GAME_COUNT - 1) {
                        editor.putLong(TankActivity.LIFE_TIME, System.currentTimeMillis());
                    }
                    editor.apply();
                    selfDismiss = false;
                    dismiss();
                    ((TankMenuActivity) activity).startGame(twoPlayers);
                }
            }
            else if(twoPlayers && WifiDirectManager.getInstance().isServer() && ServerConnectionThread.serverStarted)
            {
                if (msg.playerInfo) {
                    p2Ready = msg.playerReady;
                }
            }

        }
    }


    public void openGamePurchse() {
        TankPurchaseGameDialog wd = new TankPurchaseGameDialog(activity);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(wd.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        wd.show();
        wd.getWindow().setAttributes(lp);
    }


    private void saveObjectives(ArrayList<boolean[]> objectives) {
        SharedPreferences.Editor editor = settings.edit();
        Gson gson = new Gson();
        String json = gson.toJson(objectives);
        editor.putString(TankActivity.OBJECTIVES,json);
        editor.apply();
    }

    private ArrayList<boolean[]> loadObjectives() {
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

//        ArrayList<boolean[]> objectives = gson.fromJson(json,type);
//        return objectives;
        return gson.fromJson(objectives,type);
    }


    private void saveStars(ArrayList<Integer> stars) {
        SharedPreferences.Editor editor = settings.edit();
        Gson gson = new Gson();
        String json = gson.toJson(stars);
        editor.putString(TankActivity.LEVEL_STARS,json);
        editor.apply();
    }

    private ArrayList<Integer> loadStars() {
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

    private void displyObjectives(int level) {
        for(int obj = 0; obj < TankView.NUM_OBJECTIVES; obj++) {
            if(objectives.get(level)[obj]) {
                ((LinearLayout) ((LinearLayout) ((CardView) objGrid.getChildAt(obj)).getChildAt(0)).getChildAt(0)).getChildAt(1).setVisibility(View.INVISIBLE);
                ((LinearLayout) ((CardView) objGrid.getChildAt(obj)).getChildAt(0)).getChildAt(1).setVisibility(View.VISIBLE);
            }
            else {
                ((LinearLayout) ((LinearLayout) ((CardView) objGrid.getChildAt(obj)).getChildAt(0)).getChildAt(0)).getChildAt(1).setVisibility(View.VISIBLE);
                ((LinearLayout) ((CardView) objGrid.getChildAt(obj)).getChildAt(0)).getChildAt(1).setVisibility(View.INVISIBLE);
            }
        }
    }

    private int getCompleted(int level) {
        int completed = 0;
        boolean[] obj = objectives.get(level-1);
        for(boolean i:obj) {
            if(i) {
                ++completed;
            }
        }
        return completed;
    }

    private void dismiss() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        }
    }

    private void switchStage() {
        Log.d("Fragment Switch", "Switching to fragment1");

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentFrame,new TankStageFragment(activity, twoPlayers));
            fragmentTransaction.addToBackStack("cFragment");
            fragmentTransaction.commit();
        }
    }

    private char[][] loadStage(String name) {
        String stage = settings.getString(name,null);
        if(stage == null) {
            return new char[26][26];
        }
        Type type = new TypeToken<char[][]>() {}.getType();
        Gson gson = new Gson();
        return gson.fromJson(stage,type);
    }

    private ArrayList<String> loadStageNames() {
        String stageNames = settings.getString(TankMenuActivity.STAGE_NAMES,null);
        if(stageNames == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        Gson gson = new Gson();
        return gson.fromJson(stageNames,type);
    }
}