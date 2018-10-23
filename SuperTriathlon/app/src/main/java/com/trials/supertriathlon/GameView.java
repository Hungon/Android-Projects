package com.trials.supertriathlon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.media.AudioManager;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.purplebrain.adbuddiz.sdk.AdBuddiz;
import com.trials.userpreference.SplashActivity;

/**
 * Created by USER on 1/27/2016.
 */
public class GameView extends SurfaceView
        implements SurfaceHolder.Callback, Runnable, HasScenes {

    // field
    private static  Activity mActivity;
    private Thread          mThread;           // thread
    private SceneManager    mSceneManager;     // to manage scene
    private static int      mKeyEvent;         // key event
    private static Point    mScreenSize = new Point();
    private static SurfaceHolder  mSurface;
    private static boolean        mChangeOrientation;
    private static boolean        mIsShowedAd;
    private static boolean        mOnlyOnceShowingAd;

    // static variables
    public enum VIEW_ORIENTATION {
        ORIENTATION_PORTRAIT,
        ORIENTATION_LANDSCAPE
    }

    /*
        Constructor
    */
    public GameView(Activity activity, VIEW_ORIENTATION view) {

        // view constructor
        super(activity);
        mActivity = activity;

        Image           image = null;     // image object
        int nextScene;

        this.mThread = null;
        this.mSceneManager = null;

        // Initialize key event
        mKeyEvent = -999;                  // key code
        // select focus
        setFocusable(true);
        setFocusableInTouchMode(true);

        // create surface holder
        mSurface = getHolder();
        mSurface.setFormat(PixelFormat.RGBA_8888);
        mSurface.addCallback(this);

        // to get bundle
        Intent intent = mActivity.getIntent();
        nextScene = intent.getIntExtra("Scene",SCENE_OPENING);
        Play.SetGameLevel(intent.getIntExtra("Level",Play.LEVEL_EASY));
        mChangeOrientation = intent.getBooleanExtra("ChangeOrientation",false);

        // set screen size
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);

        // diverge orientation from view
        if (view == VIEW_ORIENTATION.ORIENTATION_PORTRAIT) {        // portrait
            // screen size
            mScreenSize.x = 480;
            mScreenSize.y = 800;
            int deviceHeight = mScreenSize.x * p.y / p.x;
            mSurface.setFixedSize(mScreenSize.x, deviceHeight);
            // create screen
            image = new Image(mSurface);
            image.setOrigin(0, (deviceHeight-mScreenSize.y)>>1);
        } else if (view == VIEW_ORIENTATION.ORIENTATION_LANDSCAPE) { // landscape
            // screen size
            mScreenSize.x = 800;
            mScreenSize.y = 480;
            int deviceWidth = mScreenSize.y*p.x/p.y;
            mSurface.setFixedSize(deviceWidth, mScreenSize.y);
            // create screen
            image = new Image(mSurface);
            image.setOrigin((deviceWidth-mScreenSize.x)>>1, 0);
        }
        // create scene manager object.
        this.mSceneManager = new SceneManager(
                activity,
                image,
                nextScene);
    }

    /*
      Create surface
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.mThread = new Thread(this);
        this.mThread.start();
    }
    /*
      Destroy surface
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        this.mThread = null;
    }
    /*
        Change surface
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) { }

    /*
      loop process in whole game.
    */
    @Override
    public void run() {
        while (this.mThread != null) {
            this.mSceneManager.UpdateScene();
        }
    }

    /*
        Change activity
    */
    public static void NextActivity(VIEW_ORIENTATION view, int nextScene, int level) {
        if (view.equals(VIEW_ORIENTATION.ORIENTATION_PORTRAIT)) {            // portrait
            Intent intent = new Intent(mActivity, SuperTriathlon.class);
            // save values
            intent.putExtra("Scene",nextScene);
            intent.putExtra("Level", level);
            intent.putExtra("ChangeOrientation",true);
            mActivity.startActivity(intent);
        } else if (view.equals(VIEW_ORIENTATION.ORIENTATION_LANDSCAPE)) {    // landscape
            Intent intent = new Intent(mActivity, SuperTriathlonLandscape.class);
            // save values
            intent.putExtra("Scene", nextScene);
            intent.putExtra("Level", level);
            intent.putExtra("ChangeOrientation", true);
            mActivity.startActivity(intent);
        }
    }
    public static void goToRankingView() {
        Intent intent = new Intent(mActivity,SplashActivity.class);
        mActivity.startActivity(intent);
    }

    /************************************************************************************************
     Each getter functions
     ************************************************************************************************/
    /*
        Get activity
     */
    public static Activity GetActivity() { return mActivity; }
    /*
        Get the flag that change the orientation
    */
    public static boolean GetChangeOrientation() { return mChangeOrientation; }
    /*
        Screen size
    */
    public static Point     GetScreenSize() { return mScreenSize; }
    /************************************************************************************************
     Each getter functions
     ************************************************************************************************/
    /*
        Set the flag that change the orientation
    */
    public static void SetChangeOrientation(boolean orientation) { mChangeOrientation = orientation; }
    /***********************************************************************************************
     Touch event
     ***********************************************************************************************/

    // static variables
    // filed
    private static Point   mTouchedPos[] = {
            new Point(),new Point()
    };
    private static int     mTouchAction = MotionEvent.ACTION_CANCEL;
    private static int     mTouchIndex;

    /*
        This function is called as touched.
    */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // to get touch-action
        int action = event.getAction();
        mTouchIndex = event.getActionIndex();

        // diverge process from touch action.
        switch(action&MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN :
                mTouchedPos[0].x = (int)(event.getX() * mScreenSize.x / getWidth());
                mTouchedPos[0].y = (int)(event.getY() * mScreenSize.y / getHeight());
                mTouchAction = MotionEvent.ACTION_DOWN;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mTouchedPos[0].x = (int)(event.getX() * mScreenSize.x / getWidth());
                mTouchedPos[0].y = (int)(event.getY() * mScreenSize.y / getHeight());
                mTouchedPos[1].x = (int)(event.getX(mTouchIndex) * mScreenSize.x / getWidth());
                mTouchedPos[1].y = (int)(event.getY(mTouchIndex) * mScreenSize.y / getHeight());
                mTouchAction = MotionEvent.ACTION_POINTER_DOWN;
                break;
            case MotionEvent.ACTION_MOVE:
                mTouchedPos[0].x = (int)(event.getX() * mScreenSize.x / getWidth());
                mTouchedPos[0].y = (int)(event.getY() * mScreenSize.y / getHeight());
                mTouchAction = MotionEvent.ACTION_MOVE;
                break;
            case MotionEvent.ACTION_UP:
                for (Point pos:mTouchedPos) {
                    pos.x = 0;
                    pos.y = 0;
                }
                mTouchAction = MotionEvent.ACTION_UP;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mTouchedPos[0].x = (int) (event.getX() * mScreenSize.x / getWidth());
                mTouchedPos[0].y = (int) (event.getY() * mScreenSize.y / getHeight());
                mTouchedPos[1].x = (int)(event.getX(mTouchIndex) * mScreenSize.x / getWidth());
                mTouchedPos[1].y = (int)(event.getY(mTouchIndex) * mScreenSize.y / getHeight());
                mTouchAction = MotionEvent.ACTION_POINTER_UP;
                break;
            case MotionEvent.ACTION_CANCEL:
                for (Point pos:mTouchedPos) {
                    pos.x = 0;
                    pos.y = 0;
                }
                mTouchAction = MotionEvent.ACTION_CANCEL;
                break;
            default:
                break;
        }
        return true;
    }

    /*
        Each Getter functions
     */

    /*
        Get touched position
     */
    public static Point GetTouchedPosition() { return mTouchedPos[0]; }
    /*
        Get touched position by the index.
    */
    public static Point GetTouchedPosition(int index) {
        if (mTouchedPos.length <= index) index = 0;
        return mTouchedPos[index];
    }
    /*
        Get touch index
    */
    public static int GetTouchIndex() { return mTouchIndex; }

    /*
        Get touch action
     */
    public static int GetTouchAction() { return mTouchAction; }

    /***********************************************************************************************
     Key event
     **********************************************************************************************/
    // is called as key down.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        mKeyEvent = keyCode;
        // to adjust the sound volume
        this.AdjustVolume();
        // redraw screen.
        invalidate();
        return true;
    }
    // is called as key up.
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        mKeyEvent = -999;
        // redraw screen.
        invalidate();
        return true;
    }
    /*
        Get Key event
     */
    public static int GetKeyEvent() { return mKeyEvent; }
    /*
        Adjust sound volume
    */
    private void AdjustVolume() {
        if (mKeyEvent == 25 || mKeyEvent == 24) {
            AudioManager am = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
            // get current volume
            int volume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
            // get max volume
            int volumeMax = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            if (mKeyEvent == 25 && 0 < volume) {
                volume--;
            } else if (mKeyEvent == 24 && volume < volumeMax) {
                volume++;
            }
            // set volume
            int flags = AudioManager.FLAG_SHOW_UI | AudioManager.FLAG_PLAY_SOUND;
            am.setStreamVolume(AudioManager.STREAM_MUSIC, volume, flags);
        }
    }
    
    /***********************************************************
     AdBuddiz to show the advertisements
     ***********************************************************/
    /*
        To show the ad
    */
    public static void ShowAd() {
        if (!IsReadyToShowAd() || mOnlyOnceShowingAd) return;
        AdBuddiz.showAd(mActivity);
        // to substitute the showing flag
        SetOnlyOnceShowingAd(true);
    }
    /*
         To return the flag that is ready to show ad
    */
    public static boolean IsReadyToShowAd() {
        return AdBuddiz.isReadyToShowAd(mActivity);
    }
    /*
        Set the flag that is showed ad
    */
    public static void IsShowedAd(boolean show) { mIsShowedAd = show; }
    /*
        Set the flag that once only showing ad.
    */
    public static void SetOnlyOnceShowingAd(boolean once) { mOnlyOnceShowingAd = once; }
    /*
        Get the flag that is showed ad
    */
    public static boolean IsShowedAd() { return mIsShowedAd; }
}
