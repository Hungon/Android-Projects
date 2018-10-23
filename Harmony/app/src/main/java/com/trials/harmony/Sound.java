package com.trials.harmony;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import org.jetbrains.annotations.Contract;

/**
 * Created by USER on 1/30/2016.
 */
public class Sound {

    // filed
    private static MediaPlayer mMediaPlayer;
    private SoundPool   mSoundPool;
    private int         mSoundId;
    private static Context     mContext;
    private static boolean     mPlayingBGM;
    private static int         mSeek;
    private int mCountPlayedSound;
    private static int mCurrentPlaybackIndex;
    private static String mFileName;
    private static boolean mLoop;

    /*
        Constructor
     */
    public Sound(Context activity) {
        mContext = activity;
        mPlayingBGM = false;
        mSeek = 0;
    }

    /*
        Create Sound effect.
    */
    public void CreateSound(String fileName) {
        // get Id from resource folder.
        int Id = mContext.getResources().getIdentifier(
                fileName, "raw", mContext.getPackageName());
        // create sound pool.
        this.mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
        // create SE based on Id from resource folder.
        this.mSoundId = this.mSoundPool.load(mContext, Id, 1);
        // the count
        mCurrentPlaybackIndex = this.mCountPlayedSound = 0;
    }

    /*
        Play BGM
    */
    public void PlayBGM(String fileName, boolean loop) {
        if (!mPlayingBGM) {
            try {
                // get Id from resource folder.
                int Id = mContext.getResources().getIdentifier(
                        fileName, "raw", mContext.getPackageName());
                mMediaPlayer = MediaPlayer.create(mContext, Id);
                mMediaPlayer.setLooping(loop);
                mFileName = fileName;
                mLoop = loop;
                // play music
                mMediaPlayer.seekTo(mSeek);
                mMediaPlayer.start();
                mPlayingBGM = true;
                // to count index
                mCurrentPlaybackIndex++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
        To stop BGM
    */
    public static void StopBGMTemporary() {
        try {
            if (mMediaPlayer != null) {
                // get current playback position
                mSeek = mMediaPlayer.getCurrentPosition();
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*
        To restart BGM
    */
    public static void RestartBGM() {
        try {
            if (mMediaPlayer == null) {
                mPlayingBGM = false;       // to reinitialize the BGM
                // get Id from resource folder.
                int Id = mContext.getResources().getIdentifier(
                        mFileName, "raw", mContext.getPackageName());
                mMediaPlayer = MediaPlayer.create(mContext, Id);
                mMediaPlayer.setLooping(mLoop);
                // play music
                mMediaPlayer.seekTo(mSeek);
                mMediaPlayer.start();
                mPlayingBGM = true;
                // to count index
                mCurrentPlaybackIndex++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
        Stop BGM
    */
    public void StopBGM() {
        if (mMediaPlayer == null) return;
        try {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mPlayingBGM = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Play a sound from a sound ID.
     *
     * Play the sound specified by the soundID. This is the value 
     * returned by the load() function. Returns a non-zero streamID
     * if successful, zero if it fails. The streamID can be used to
     * further control playback. Note that calling play() may cause
     * another sound to stop playing if the maximum number of active
     * streams is exceeded. A loop value of -1 means loop forever,
     * a value of 0 means don't loop, other values indicate the
     * number of repeats, e.g. a value of 1 plays the audio twice.
     * The playback rate allows the application to vary the playback
     * rate (pitch) of the sound. A value of 1.0 means play back at
     * the original frequency. A value of 2.0 means play back twice
     * as fast, and a value of 0.5 means playback at half speed.
     *
     * param soundID a soundID returned by the load() function
     * param leftVolume left volume value (range = 0.0 to 1.0)
     * param rightVolume right volume value (range = 0.0 to 1.0)
     * param priority stream priority (0 = lowest priority)
     * param loop loop mode (0 = no loop, -1 = loop forever)
     * param rate playback rate (1.0 = normal playback, range 0.5 to 2.0)
     * return non-zero streamID if successful, zero if failed
     */
    public void PlaySE() {
        this.mSoundPool.play(this.mSoundId, 1.0f, 1.0f, 1, 0, 1.0f);
        mCountPlayedSound++;
    }
    /*
        Stop SE
    */
    public void StopSE() {
        if (this.mSoundPool != null) {
            this.mSoundPool.pause(this.mSoundId);
        }
    }
    /**
     * Set stream volume.
     *
     * Sets the volume on the stream specified by the streamID.
     * This is the value returned by the play() function. The
     * value must be in the range of 0.0 to 1.0. If the stream does
     * not exist, it will have no effect.
     *
     * a streamID returned by the play() function
     * left volume value (range = 0.0 to 1.0)
     * right volume value (range = 0.0 to 1.0)
     */
    public void SetSoundVolume(float r,float l) {
        if (this.mSoundPool != null) {
            this.mSoundPool.setVolume(this.mSoundId, r, l);
        }
    }
    /*
        Reset playback position
    */
    public void SetPlaybackPosition(int seek) {
        if (mMediaPlayer == null) return;
        mMediaPlayer.seekTo(seek);
    }
    /*
        Set volume to play BGM
    * value must be in the range of 0.0 to 1.0. If the stream does
    * not exist, it will have no effect.
    */
    public static void SetBGMVolume(float r,float l) {
        r = (r < 0 || 1.0f < r)?0:r;
        l = (l < 0 || 1.0f < l)?0:l;
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(r, l);
        }
    }
    /****************************************************************
        Each getter functions
    **************************************************************/
    /*
        Get duration of the file
        return value is the duration in milliseconds.
    */
    @Contract(pure = true)
    public static int GetDuration() {
        if (mMediaPlayer != null) return mMediaPlayer.getDuration();
        return 0;
    }
    /*
        Get current position
        return value is the current playback position in milliseconds.
    */
    @Contract(pure = true)
    public static int GetCurrentPlaybackPosition() {
        if (mMediaPlayer != null) return mMediaPlayer.getCurrentPosition();
        return 0;
    }
    /*
        Get the count played sound
    */
    public int GetCountPlayedSound() { return mCountPlayedSound; }
    /*
        Get index that play music
    */
    @Contract(pure = true)
    public static int GetPlaybackIndex() { return mCurrentPlaybackIndex; }
    /*
        when is playing, to return true
    */
    @Contract(pure = true)
    public static boolean IsPlayingBGM() { return mPlayingBGM; }
}