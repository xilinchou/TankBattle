package com.gamecentre.tankbattle.sound;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.util.HashMap;

public class SoundManager
{
    static private SoundManager _instance;
    private static SoundPool mSoundPool;
    private static HashMap<Integer, Integer> mSoundPoolMap;
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
        for(int i=1; i<= resID.length;i++){
            mSoundPoolMap.put(i, mSoundPool.load(mContext, resID[i-1], 1));
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
            mSoundPool.play(mSoundPoolMap.get(index), streamVolume, streamVolume, 1, 0, 1);
        }
    }

    public static void playSound(int index, float volume, int priority)
    {
        if (playSound)
        {
            float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            mSoundPool.play(mSoundPoolMap.get(index), streamVolume*volume, streamVolume*volume, priority, 0, 1);
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

    public static void resumeSounds() {
        for (int soundIndex : mSoundPoolMap.values())
        {
            mSoundPool.resume(soundIndex);
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
        mSoundPool.stop(mSoundPoolMap.get(index));
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
