package com.gamecentre.tankbattle.tank;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.gamecentre.tankbattle.R;
import com.gamecentre.tankbattle.sound.SoundManager;
import com.gamecentre.tankbattle.sound.Sounds;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

///**
// * A simple {@link Fragment} subclass.
// * Use the {@link ConstructionFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class ConstructionFragment extends Fragment implements View.OnTouchListener {


    View rootView;

    SharedPreferences settings;
    AppCompatActivity activity;
    LinearLayout stage;
    ImageView stone,brick,water,bush,ice,delObj,clearStage;
    ImageView playStage,loadStage,saveStage;
    Drawable selectedObject = null;
    int newObjId = 1;
    int oldObjId = 1;

    int width, height, dim;

    ArrayList<int []> redoStack;
    int redoPointer;
    int pointerLimit;
    final int REDO_SIZE = 100;

    private char[][] stageObjects;
    private ArrayList<String>stageNames;

    public ConstructionFragment() {
        // Required empty public constructor
    }

    public ConstructionFragment(AppCompatActivity activity) {
        this.activity = activity;
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
        rootView =  inflater.inflate(R.layout.fragment_construction, container, false);
        rootView.findViewById(R.id.construction_View).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        settings = activity.getSharedPreferences("TankSettings", 0);
        stageNames = loadStageNames();

        stage = (LinearLayout) rootView.findViewById(R.id.stage);
        stage.setOrientation(LinearLayout.VERTICAL);
        stage.setOnTouchListener(this);

        stone = rootView.findViewById(R.id.stone);
        brick = rootView.findViewById(R.id.brick);
        bush = rootView.findViewById(R.id.bush);
        water = rootView.findViewById(R.id.water);
        ice = rootView.findViewById(R.id.ice);
        delObj = rootView.findViewById(R.id.delete);
        clearStage = rootView.findViewById(R.id.clear);

        stone.setOnTouchListener(objSelectListener);
        brick.setOnTouchListener(objSelectListener);
        bush.setOnTouchListener(objSelectListener);
        water.setOnTouchListener(objSelectListener);
        ice.setOnTouchListener(objSelectListener);
        delObj.setOnTouchListener(objSelectListener);
        clearStage.setOnTouchListener(objSelectListener);

        selectedObject = stone.getBackground();

        LinearLayout.LayoutParams vLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        vLayout.gravity = Gravity.CENTER;
        vLayout.weight = 1;
        vLayout.height = 0;


        LinearLayout.LayoutParams hLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        hLayout.gravity = Gravity.CENTER;
        hLayout.weight = 1;
        hLayout.width = 0;

        for(int row = 0; row < 26; row++) {

            LinearLayout rowObj = new LinearLayout(rootView.getContext());
            rowObj.setOrientation(LinearLayout.HORIZONTAL);
            rowObj.setLayoutParams(vLayout);
            for(int col = 0; col < 26; col++) {
                ImageView obj = new ImageView(rootView.getContext());
                obj.setLayoutParams(hLayout);
                if(row == 0 || row == 1) {
                    if(col == 0 || col == 1 || col == 12 || col == 13 || col == 24 || col == 25) {
                        obj.setBackgroundColor(Color.DKGRAY);
                    }
                }
                else if(row >= 23) {
                    if(col >= 11 && col <= 14) {
                        obj.setBackgroundColor(Color.DKGRAY);
                    }
                    if(row >= 24) {
                        if(col == 8 || col == 9 || col == 16 || col == 17) {
                            obj.setBackgroundColor(Color.DKGRAY);
                        }
                    }
                }
                rowObj.addView(obj);
            }
            stage.addView(rowObj);
        }

        rootView.findViewById(R.id.saveStage).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    SoundManager.playSound(Sounds.TANK.CLICK);
                    openSaveDialog();
                }
                return true;
            }
        });

        rootView.findViewById(R.id.loadStage).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    SoundManager.playSound(Sounds.TANK.CLICK);
                    openLoadDialog();
                }
                return true;
            }
        });

//        rootView.findViewById(R.id.playStage).setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//
//                }
//                return true;
//            }
//        });


        rootView.findViewById(R.id.backStage).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    SoundManager.playSound(Sounds.TANK.CLICK);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    Log.d("Fragment","Closing fragment");
                    if (fragmentManager.getBackStackEntryCount() > 0) {
                        fragmentManager.popBackStack();
                    }
                }
                return true;
            }
        });


        int [] init = new int[]{-1,-1,0,0};
        redoStack = new ArrayList<>(Collections.nCopies(REDO_SIZE,init));
        redoPointer = 0;
        pointerLimit = 0;

        stageObjects = new char[26][26];

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        stage.post(()->{
            int w = stage.getWidth();
            int h = stage.getHeight();
            Log.d("Resume Fragment", String.valueOf(width)+" "+height);

            int d = Math.min(w,h);
            d = (d/26)*26;

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)stage.getLayoutParams();

            params.width = d;
            params.height = d;
            stage.setLayoutParams(params);
            width = d;
            height = d;
            dim = (int)(width/26);
        });
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Log.d("Touched stage", motionEvent.getX() + " " + motionEvent.getY() + " " + dim);
        int row = (int)(motionEvent.getY()/dim);
        int col = (int)(motionEvent.getX()/dim);
        if(row == 0 || row == 1) {
            if(col == 0 || col == 1 || col == 12 || col == 13 || col == 24 || col == 25) {
                return true;
            }
        }
        else if(row >= 23) {
            if(col >= 11 && col <= 14) {
                return true;
            }
            if(row >= 24) {
                if(col == 8 || col == 9 || col == 16 || col == 17) {
                    return true;
                }
            }
        }
        if(row < 26 && row >= 0 && col < 26 && col >= 0) {
            ImageView pos = (ImageView) ((LinearLayout)((LinearLayout)view).getChildAt(row)).getChildAt(col);
            pos.setBackground(selectedObject);
            updateStageObj(row,col,newObjId);
//            int[] stack = redoStack.get(redoPointer);
//            stack[0] = row;
//            stack[1] = col;
//            stack[2] = stack[3];
//            stack[3] = newObjId;
//            redoStack.set(redoPointer,stack);
//            redoPointer++;
//            redoPointer %= REDO_SIZE;
//            int limits = 0;
//            if(redoPointer > pointerLimit)
//            pointerLimit = (redoPointer-REDO_SIZE) % REDO_SIZE;
        }

        return true;
    }

    View.OnTouchListener objSelectListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                if(view.getId() == R.id.stone) {
                    newObjId = 1;
                }
                else if(view.getId() == R.id.brick) {
                    newObjId = 2;
                }
                else if(view.getId() == R.id.bush) {
                    newObjId = 3;
                }
                else if(view.getId() == R.id.water) {
                    newObjId = 4;
                }
                else if(view.getId() == R.id.ice) {
                    newObjId = 5;
                }
                else if(view.getId() == R.id.delete) {
                    newObjId = 0;
                }
                else if(view.getId() == R.id.clear) {
                    for(int row = 0; row < 26; row++) {
                        for(int col = 0; col < 26; col++) {
                            if(row == 0 || row == 1) {
                                if(col == 0 || col == 1 || col == 12 || col == 13 || col == 24 || col == 25) {
                                    continue;
                                }
                            }
                            else if(row >= 23) {
                                if(col >= 11 && col <= 14) {
                                    continue;
                                }
                                if(row >= 24) {
                                    if(col == 8 || col == 9 || col == 16 || col == 17) {
                                        continue;
                                    }
                                }
                            }

                            ImageView pos = (ImageView) ((LinearLayout)((LinearLayout)stage).getChildAt(row)).getChildAt(col);
                            pos.setBackground(null);
                            updateStageObj(row,col,0);

                        }
                    }
                }

                ((RelativeLayout)stone.getParent()).setBackgroundColor(Color.TRANSPARENT);
                ((RelativeLayout)brick.getParent()).setBackgroundColor(Color.TRANSPARENT);
                ((RelativeLayout)bush.getParent()).setBackgroundColor(Color.TRANSPARENT);
                ((RelativeLayout)water.getParent()).setBackgroundColor(Color.TRANSPARENT);
                ((RelativeLayout)ice.getParent()).setBackgroundColor(Color.TRANSPARENT);
                ((RelativeLayout)delObj.getParent()).setBackgroundColor(Color.TRANSPARENT);
                ((RelativeLayout)clearStage.getParent()).setBackgroundColor(Color.TRANSPARENT);

                RelativeLayout cursor = (RelativeLayout) view.getParent();
                cursor.setBackgroundColor(Color.YELLOW);
                selectedObject = view.getBackground();
            }

            if(motionEvent.getAction() == MotionEvent.ACTION_UP && view.getId() == R.id.clear) {
                RelativeLayout cursor = (RelativeLayout) view.getParent();
                cursor.setBackgroundColor(Color.GRAY);
                ((RelativeLayout)stone.getParent()).setBackgroundColor(Color.YELLOW);
                selectedObject = stone.getBackground();
                newObjId = 1;
            }
            return true;
        }
    };

    private Drawable getObject(int id) {
        Drawable d = null;
        switch(id) {
            case 1:
                d = stone.getBackground();
                break;
            case 2:
                d = brick.getBackground();
                break;
            case 3:
                d = bush.getBackground();
                break;
            case 4:
                d = water.getBackground();
                break;
            case 5:
                d = ice.getBackground();
                break;
        }
        return d;
    }

    private void updateStageObj(int row, int col, int id) {
        switch(id) {
            case 0:
                stageObjects[row][col] = 0;
                break;
            case 1:
                stageObjects[row][col] = '@';
                break;
            case 2:
                stageObjects[row][col] = '#';
                break;
            case 3:
                stageObjects[row][col] = '%';
                break;
            case 4:
                stageObjects[row][col] = '~';
                break;
            case 5:
                stageObjects[row][col] = '-';
                break;
        }
    }

    public void updateStage(char[][] stageIDs) {
        for(int row = 0; row < 26; row++) {
            for(int col = 0; col < 26; col++) {
                if(row == 0 || row == 1) {
                    if(col == 0 || col == 1 || col == 12 || col == 13 || col == 24 || col == 25) {
                        continue;
                    }
                }
                else if(row >= 23) {
                    if(col >= 11 && col <= 14) {
                        continue;
                    }
                    if(row >= 24) {
                        if(col == 8 || col == 9 || col == 16 || col == 17) {
                            continue;
                        }
                    }
                }

                char id = stageIDs[row][col];
                ImageView pos = (ImageView) ((LinearLayout)((LinearLayout)stage).getChildAt(row)).getChildAt(col);
                if(id==0) {
                    pos.setBackground(null);
                }
                else if(id=='@') {
                    pos.setBackground(stone.getBackground());
                }
                else if(id=='#') {
                    pos.setBackground(brick.getBackground());
                }
                else if(id=='%') {
                    pos.setBackground(bush.getBackground());
                }
                else if(id=='~') {
                    pos.setBackground(water.getBackground());
                }
                else if(id=='-') {
                    pos.setBackground(ice.getBackground());
                }

                stageObjects[row][col] = id;

            }
        }
    }

    private void saveStage(String name, char[][] stage) {
        SharedPreferences.Editor editor = settings.edit();
        Gson gson = new Gson();
        String json = gson.toJson(stage);
        editor.putString(name,json);
        editor.apply();
    }

    private void saveStageNames(ArrayList<String> stageNames) {
        SharedPreferences.Editor editor = settings.edit();
        Gson gson = new Gson();
        String json = gson.toJson(stageNames);
        editor.putString(TankMenuActivity.STAGE_NAMES,json);
        editor.apply();
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

    private char[][] loadStage(String name) {
        String stage = settings.getString(name,null);
        if(stage == null) {
            return new char[26][26];
        }
        Type type = new TypeToken<char[][]>() {}.getType();
        Gson gson = new Gson();
        return gson.fromJson(stage,type);
    }

    public void openSaveDialog() {
        TankSaveDialog cdd = new TankSaveDialog(activity, stageNames, stageObjects);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(cdd.getWindow().getAttributes());
        cdd.show();
        cdd.getWindow().setAttributes(lp);
    }

    public void openLoadDialog() {
        TankLoadDialog cdd = new TankLoadDialog(activity, this);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(cdd.getWindow().getAttributes());
        cdd.show();
        cdd.getWindow().setAttributes(lp);
    }
}