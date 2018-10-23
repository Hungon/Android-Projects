package com.trials.supertriathlon;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

/**
 * Created by USER on 1/30/2016.
 */
public class Sound {

    // filed
    private static MediaPlayer mMediaPlayer;
    private SoundPool   mSoundPool;
    private int         mSoundId;
    private Context     mContext;
    private static boolean     mPlayingBGM;
    private static int         mSeek;

    /*
        Constructor
     */
    public Sound(Context activity) {
        this.mContext = activity;
        mPlayingBGM = false;
        mSeek = 0;
    }

    /*
        Create Sound effect.
     */
    public void CreateSound(String fileName) {
        // get Id from resource folder.
        int Id = this.mContext.getResources().getIdentifier(
                fileName, "raw", this.mContext.getPackageName());
        // create sound pool.
        this.mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
        // create SE based on Id from resource folder.
        this.mSoundId = this.mSoundPool.load(this.mContext, Id, 1);
    }

    /*
        Play BGM
    */
    public void PlayBGM(String fileName) {
        if (!mPlayingBGM) {
            try {
                // get Id from resource folder.
                int Id = this.mContext.getResources().getIdentifier(
                        fileName, "raw", this.mContext.getPackageName());
                mMediaPlayer = MediaPlayer.create(this.mContext, Id);
                mMediaPlayer.setLooping(true);
                // play music
                mMediaPlayer.seekTo(mSeek);
                mMediaPlayer.start();
                mPlayingBGM = true;
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

    /*
        Play SE
    */
    public void PlaySE() {
        this.mSoundPool.play(this.mSoundId, 100, 100, 1, 0, 1);
    }
}