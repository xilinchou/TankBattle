package com.gamecentre.tankbattle.sound;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class SoundManager
{
    static private SoundManager _instance;
    private static SoundPool mSoundPool;
    private static HashMap<Integer, Integer> mSoundPoolMap;
    private static ArrayList<Integer> streamIDs;
    private static ArrayList<Boolean> activeSounds;
    private static AudioManager mAudioManager;
    private static Context mContext;
    private static boolean playSound;

    private SoundManager()
    {

    }

    static synchronized public SoundManager getInstance()
    {
        if (_instance == null) {
            _instance = new SoundManager();
        }
        return _instance;
    }

    public static void initSounds(Context theContext)
    {
        mContext = theContext;
        mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        mSoundPoolMap = new HashMap<Integer, Integer>();
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    public static void loadSounds(int[] resID)
    {
        streamIDs = new ArrayList<>();
        activeSounds = new ArrayList<>();
        streamIDs.add(0);
        activeSounds.add(false);
        for(int i=1; i<= resID.length;i++){
            mSoundPoolMap.put(i, mSoundPool.load(mContext, resID[i-1], 1));
            streamIDs.add(0);
            activeSounds.add(false);
        }
        //TODO
//        mSoundPoolMap.put(1, mSoundPool.load(mContext, R.raw.hit, 1));
//        mSoundPoolMap.put(2, mSoundPool.load(mContext, R.raw.terminator, 1));
//        mSoundPoolMap.put(3, mSoundPool.load(mContext, R.raw.clap, 1));
    }

//    public static void playSound(int index, float speed)
//    {
//        if (playSound)
//        {
//            float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//            streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//            mSoundPool.play(mSoundPoolMap.get(index), streamVolume, streamVolume, 1, 0, speed);
//        }
//    }

    public static void playSound(int index)
    {
        if (playSound)
        {
            float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int stremID = mSoundPool.play(mSoundPoolMap.get(index), streamVolume, streamVolume, 1, 0, 1);
            streamIDs.set(index,stremID);
//            activeSounds.set(index,true);
        }
    }

    public static void playSound(int index, float volume, int priority)
    {
        if (playSound)
        {
            float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int streamID = mSoundPool.play(mSoundPoolMap.get(index), streamVolume*volume, streamVolume*volume, priority, 0, 1);
            streamIDs.set(index,streamID);
//            activeSounds.set(index,true);
        }
    }

    public static void playSound(int index, boolean loop)
    {
        if(loop && activeSounds.get(index)) {
            return;
        }

        if (playSound)
        {
            float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int streamID = mSoundPool.play(mSoundPoolMap.get(index), streamVolume, streamVolume, loop?5:1, loop?-1:0, 1);
            streamIDs.set(index,streamID);
            activeSounds.set(index,true);
        }
    }

    public static void playSound(int index, float volume, int priority, boolean loop)
    {
        if(loop && activeSounds.get(index)) {
            return;
        }

        if (playSound)
        {
            float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int streamID = mSoundPool.play(mSoundPoolMap.get(index), streamVolume*volume, streamVolume*volume, priority, loop?-1:0, 1);
            streamIDs.set(index,streamID);
            activeSounds.set(index,true);
        }
    }

    public static void pauseSounds() {

        for (int soundIndex : mSoundPoolMap.values())
        {
            if(soundIndex == Sounds.TANK.PAUSE) {
                continue;
            }
            mSoundPool.pause(soundIndex);
        }
    }

    public static void resumeGameSounds() {
        Log.d("RESUME", "Resuming game sounds");
        if(!playSound) {
            return;
        }
        for(int i = 0; i < activeSounds.size(); i++)
        {
            if(activeSounds.get(i)) {
                mSoundPool.resume(streamIDs.get(i));
            }
        }
    }

    public static void resumeSound(int index) {
        if(!playSound) {
            return;
        }
        Log.d("RESUME", "Resuming game sound");
        mSoundPool.resume(streamIDs.get(index));

    }

    public static void pauseSound(int index) {
        Log.d("PAUSE", "Pausing game sound");
        if(activeSounds.get(index)) {
            mSoundPool.pause(streamIDs.get(index));
        }
    }

    public static void pauseGameSounds() {
        Log.d("PAUSE", "Pausing game sounds");
        for(int i = 0; i < streamIDs.size(); i++) {
            if(activeSounds.get(i)) {
                mSoundPool.pause(streamIDs.get(i));
            }
        }
    }

    public static void togglePlaySound()
    {
        playSound = !playSound;
        if (!playSound)
        {
            for (int soundIndex : mSoundPoolMap.values())
            {
                stopSound(soundIndex);
            }
        }
    }

    public static void setSound(boolean sound) {
        playSound = sound;
    }

    public static void stopSound(int index)
    {
        Log.d("STOP", "Stopping game sound");
        mSoundPool.stop(streamIDs.get(index));
        activeSounds.set(index,false);
    }

    public static void stopGameSounds()
    {
        Log.d("STOP", "Stopping game sounds");
        for(int i = 0; i < streamIDs.size(); i++) {
            if(activeSounds.get(i)) {
                mSoundPool.stop(streamIDs.get(i));
                activeSounds.set(i, false);
            }
        }
    }

    public static boolean isActive(int index) {
        return activeSounds.get(index);
    }

    public static void cleanup()
    {
        mSoundPool.release();
//        mSoundPool = null;
        mSoundPoolMap.clear();
        mAudioManager.unloadSoundEffects();
        _instance = null;
    }

    public static void enableSound(boolean enable) {
        playSound = enable;
        Log.d("PLAY SOUND", String.valueOf(playSound));
    }
}
