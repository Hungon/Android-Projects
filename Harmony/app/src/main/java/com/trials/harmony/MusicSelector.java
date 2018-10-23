package com.trials.harmony;

import android.content.Context;
import android.graphics.PointF;

import org.jetbrains.annotations.Contract;

/**
 * Created by Kohei Moroi on 6/30/2016.
 */
public class MusicSelector implements HasScene, HasMusicInfo {
    // static variables
    // default volume
    public final static PointF   DEFAULT_SOUND_VOLUME = new PointF(0.3f,0.3f);
    public final static int      TUNE_ELEMENT_EMPTY = -1;
    public final static int      READY      = -1;
    public final static int      PLAYING    = 0;
    public final static int      STOP       = 1;
    public final static int      UP_TO_END  = 2;
    // the fixed interval between each BGM.
    private final static int     FIXED_INTERVAL_BETWEEN_EACH_BGM = 200;
    // filed
    private Sound   mSound;
    private Utility mUtility[];
    private int     mDuration;
    private int     mFixedInterval;
    private int     mProcessSelection;
    private static int     mCurrentMusicElement;
    private static int     mMaxMusicElement = MUSIC_INFO.length-1;
    private static int     mMusicElementRecognized = TUNE_ELEMENT_EMPTY;
    private int mPreviewElement = -1;
    private static boolean mAvailableToPlay;
    /*
        Constructor
    */
    public MusicSelector(Context context) {
        mMusicElementRecognized = TUNE_ELEMENT_EMPTY;
        this.mSound = new Sound(context);
        this.mUtility = new Utility[1];
        for (int i = 0; i < this.mUtility.length; i++) this.mUtility[i] = new Utility();
    }
    /*
        Initialize
    */
    public void InitSelector() {
        mAvailableToPlay = false;
        if (SceneManager.GetCurrentScene() == SCENE_TUTORIAL) {
            mCurrentMusicElement = 0;
            mMaxMusicElement = 4;
        } else {
            mMaxMusicElement = MUSIC_INFO.length-1;
        }
        // the duration to play BGM
        this.mDuration = 500;
        // to make interval between each BGM.
        this.mFixedInterval = FIXED_INTERVAL_BETWEEN_EACH_BGM>>1;
        // the process to select the BGM in play scene.
        this.mProcessSelection = READY;
    }
    /*
        Update to play each BGM
        when playing music, return value is current process in the function.
    */
    public int UpdateSelector(boolean loop) {
        // to diverge the process from the variable
        switch(this.mProcessSelection) {
            case READY:
                // to make interval that only starting point.
                // when executing guidance, not to be next.
                if (this.mUtility[0].ToMakeTheInterval(this.mFixedInterval)) {
                    this.mProcessSelection = PLAYING;
                }
                break;
            case PLAYING:
                this.mSound.PlayBGM("bgm"+mCurrentMusicElement,true);
                // playing BGM while fixed duration.
                if (this.mUtility[0].ToMakeTheInterval(this.mDuration)) {
                    this.mProcessSelection = STOP;
                    this.mSound.StopBGM();
                    // the fixed interval between each music about 2 seconds.
                    this.mFixedInterval = FIXED_INTERVAL_BETWEEN_EACH_BGM;
                }
                break;
            case STOP:
                // to increase element to play the BGM whenever counted fixed interval.
                if (this.mUtility[0].ToMakeTheInterval(this.mFixedInterval)) {
                    mCurrentMusicElement++;
                    // to limit element.
                    if (loop) {
                        if (mMaxMusicElement < mCurrentMusicElement) {
                            mCurrentMusicElement = 0;
                        }
                    } else {
                        if (mMaxMusicElement < mCurrentMusicElement) {
                            // music element reached to last
                            this.mProcessSelection = UP_TO_END;
                            break;
                        }
                    }
                    // process to next
                    // ready to play music, it may lead number of the music with voice.
                    this.mProcessSelection = READY;
                    // to make a little blank time for guidance.
                    this.mFixedInterval = 1;
                }
                break;
            case UP_TO_END: // current element ends up to last.
                break;
            default:
        }
        int scene = SceneManager.GetCurrentScene();
        // to diverge
        if (scene == SCENE_TUTORIAL) {
            Sound.SetBGMVolume(DEFAULT_SOUND_VOLUME.x, DEFAULT_SOUND_VOLUME.y);
        }
        return this.mProcessSelection;
    }
    /*
        Update Music by the current element
    */
    public void UpdateMusic(boolean loop, int interval) {
        if (!mAvailableToPlay) return;
        boolean playing = Sound.IsPlayingBGM();
        // to set current music element
        if (this.mPreviewElement != mCurrentMusicElement) {
            if (playing) this.mSound.StopBGM();
        }
        if (!playing){
            if (this.mUtility[0].ToMakeTheInterval(interval)) {
                this.ToPlayTheMusic(loop);
                // Reset playback position
                this.mSound.SetPlaybackPosition(0);
                // to set preview element
                this.mPreviewElement = mCurrentMusicElement;
            }
        }
    }
    /*
        To play the music
    */
    public void ToPlayTheMusic(boolean loop) {
        int scene = SceneManager.GetCurrentScene();
        // to diverge
        if (scene == SCENE_PLAY) {
            Sound.SetBGMVolume(DEFAULT_SOUND_VOLUME.x, DEFAULT_SOUND_VOLUME.y);
            loop = false;
        } else {
            Sound.SetBGMVolume(0.1f,0.1f);
        }
        if (!Sound.IsPlayingBGM()) this.PlayMusic(loop);
    }
    /*
        To play the music.
    */
    private void PlayMusic(boolean loop) {
        this.mSound.PlayBGM("bgm"+mCurrentMusicElement,loop);
    }
    /*
        Stop the music temporary
    */
    public void StopTheMusicTemporary() { Sound.StopBGMTemporary(); }
    /*
        Restart to play the music
    */
    public void RestartTheMusic() { Sound.RestartBGM(); }
    /*
        Release
    */
    public void ReleaseSelector() {
        this.mSound.StopBGM();
        this.mSound = null;
        for (int i = 0; i < this.mUtility.length; i++) this.mUtility[i] = null;
    }
    /*
        Reset music id recognized
    */
    public static void ResetMusicElementRecognized() { mMusicElementRecognized = TUNE_ELEMENT_EMPTY; }
    /*****************************************
     Each getter functions
     *****************************************/
    /*
        Get current element to play the music.
    */
    @Contract(pure = true)
    public static int GetCurrentElement() { return mCurrentMusicElement; }
    @Contract(pure = true)
    public static int GetMusicElementRecognized() { return mMusicElementRecognized; }
    /*
        Get max Id to play the music in the current case
    */
    @Contract(pure = true)
    public static int GetMaxId() { return mMaxMusicElement; }
    public boolean GetAvailableToPlay() { return mAvailableToPlay; }
    /*****************************************
     Each setter functions
     *****************************************/
    /*
        Set music element recognized
    */
    public static void SetCurrentElementToPlayMusic(int element) {
        if (0 <= element && element <= mMaxMusicElement) {
            mCurrentMusicElement = element;
        }
    }
    /*
        Set music element recognized
    */
    public static void SetMusicElementRecognized(int element) {
        mMusicElementRecognized = element;
        SystemManager.saveMusicInfo(element);
    }
    /*
        Set available to play music
    */
    public static void SetAvailableToPlay(boolean play) { mAvailableToPlay = play; }
}