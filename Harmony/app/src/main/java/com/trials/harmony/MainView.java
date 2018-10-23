package com.trials.harmony;

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
import android.widget.Toast;

import org.jetbrains.annotations.Contract;

/**
 * Created by USER on 3/28/2016.
 */
public class MainView extends SurfaceView
        implements SurfaceHolder.Callback, Runnable, HasScene {

    // static variables
    // field
    private static Activity         mActivity;
    private Thread                  mThread;           // thread
    private SceneManager            mSceneManager;        // to manage scene
    private static int              mKeyEvent;         // key event
    private static Point            mScreenSize = new Point();

    /**************************************************
        Constructor
    **************************************************/
    public MainView(Activity activity) {

        // view constructor
        super(activity);
        mActivity = activity;
        SurfaceHolder surfaceHolder;

        this.mThread = null;
        this.mSceneManager = null;

        // Initialize key event
        mKeyEvent = -999;                  // key code
        // select focus
        setFocusable(true);
        setFocusableInTouchMode(true);
        // to get bundle
        Intent intent = mActivity.getIntent();
        int scene = intent.getIntExtra("Scene", SCENE_OPENING);

        // create surface holder
        surfaceHolder = getHolder();
        surfaceHolder.setFormat(PixelFormat.RGBA_8888);
        surfaceHolder.addCallback(this);


        // set screen size
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);

        // screen size
        mScreenSize.x = 480;
        mScreenSize.y = 800;
        int deviceHeight = mScreenSize.x * p.y / p.x;
        surfaceHolder.setFixedSize(mScreenSize.x, deviceHeight);
        // create screen
        Image image = new Image(surfaceHolder);
        image.setOrigin(0, (deviceHeight - mScreenSize.y) >> 1);

        // create scene manager object.
        this.mSceneManager = new SceneManager(
                activity,
                image,
                scene);
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
            if (this.mSceneManager.UpdateSceneManager()) break;
        }
    }

    /*
        To show the toast
    */
    public static void Toast(String text) {
        Toast.makeText(mActivity,text, Toast.LENGTH_SHORT).show();
    }
    /*********************************************************
        Each getter functions
     *********************************************************/
    /*
        Screen size
    */
    @Contract(pure = true)
    public static Point     GetScreenSize() { return mScreenSize; }
    /************************************************************************************************
        Each setter functions
     ************************************************************************************************/
    /*
        To adjust positionX to center in the screen
    */
    @Contract(pure = true)
    public static int CenterXInScreen(int w) { return (mScreenSize.x-w)>>1; }
    /*
        To adjust positionY to center in the screen
    */
    @Contract(pure = true)
    public static int CenterYInScreen(int h) { return (mScreenSize.y-h)>>1; }
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

    /***************************************************
        Each Getter functions
    ****************************************************/

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
    @Contract(pure = true)
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
}