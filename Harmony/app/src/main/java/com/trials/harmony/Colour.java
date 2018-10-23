package com.trials.harmony;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;

/**
 * Created by USER on 4/20/2016.
 */
public class Colour {
    // static variables
    // filed
    private Sound               mSound;
    private boolean             mAvailableToPlay;
    private RecognitionButton   mButton;
    public double               mOblique;
    public Utility              mUtility;

    /*
        Constructor
    */
    public Colour(Context context, Image image) {
        // to allot the memory
        this.mButton = new RecognitionButton(context,image);
        this.mSound = new Sound(context);
        this.mUtility = new Utility();
    }
    /*
        Initialize
    */
    public void InitColour(String seFile, String imageFile, Point pos,
        Point size, Point src, int alpha, float scale, int type)
    {
        // to set the image
        this.mButton.InitRecognitionButton(
                imageFile,"",
                pos, size,
                src, alpha,scale,
                type);
        // to make the sound
        if (!seFile.equals("")) this.mSound.CreateSound(seFile);
        this.mAvailableToPlay = false;
        this.mOblique = 0.0;
    }
    /*
        Update
    */
    public void ToPlayColour() {
        // to play the sound and to notice the process toward user.
        if (this.mAvailableToPlay) {
            this.mSound.PlaySE();
        }
    }
    /*
        Draw the colour
    */
    public void DrawColour() { this.mButton.DrawRecognitionButton(); }
    /*
        To check the type which is pressed.
        and to get that type
    */
    public int IsPressedTheColour() { return this.mButton.IsPressedTheButton(); }
    /*
        as touched the button and then to process
    */
    public boolean IsTouchedColour(CharacterEx ch) { return this.mButton.IsTouchedButton(ch); }
    /*
        To look for the colour.
        following the found type, to return the direction
        that between finger and the colour's position.
    */
    public int ToSeekTheColour(Point area) { return this.mButton.ToSeekTheButton(area); }
    /*
        Release
    */
    public void ReleaseColour() {
        // sound
        if (this.mSound != null) {
            this.mSound.StopSE();
            this.mSound = null;
        }
        // recognition button
        this.mButton.ReleaseRecognitionButton();
        this.mButton = null;
    }
    /*
        Stop sound
    */
    public void StopSound() { this.mSound.StopSE(); }
    /*********************************************************
     Each setter functions
     *******************************************************/
    /*
        Set the starting flag that available to play the SE
    */
    public void AvailableToPlay(boolean play) { this.mAvailableToPlay = play; }
    /*
        Set position
    */
    public void SetPosition(int x, int y) { this.mButton.SetPosition(x,y);}
    /*
        Set alpha
    */
    public void SetAlpha(int alpha) { this.mButton.SetAlpha(alpha); }
    /*
        Set exist
    */
    public void SetExist(boolean exist) { this.mButton.SetExist(exist); }
    /*
        Set type
    */
    public void SetType(int type) { this.mButton.SetType(type); }
    /*
        Set origin position
    */
    public void SetOriginPosition(int x, int y) { this.mButton.SetOriginPosition(x,y); }
    /*
        Set sound file
    */
    public void SetSoundFile(String file) { this.mSound.CreateSound(file); }
    /***************************************************
     Each getter functions
     ***************************************************/
    /*
        Is pressed the button
    */
    public boolean AsPressesTheButton() { return this.mButton.AsPressesTheButton(); }
    /*
        Get position
    */
    public Point GetPosition() { return this.mButton.GetPosition(); }
    /*
        Get count that was pressed button
    */
    public int GetCountWasPressedButton() { return this.mButton.GetCountWasPressedButton(); }
    /*
        Get the type
    */
    public int GetType() { return this.mButton.GetType(); }
    /*
        Get the direction
    */
    public int GetDirection() { return this.mButton.GetDirection(); }
    /*
        Get exist
    */
    public boolean GetExist() { return this.mButton.GetExist(); }
    /*
        Get whole size
    */
    public PointF GetWholeSize() { return this.mButton.GetWholeSize(); }
    /*
        Get the flag which is able to play the sound
    */
    public boolean GetAvailableToPlay() { return this.mAvailableToPlay; }
    /*
        Get oblique long
    */
    public double GetOblique() { return this.mOblique; }
    /*
        Get count played sound
    */
    public int GetCountPlayedSound() { return this.mSound.GetCountPlayedSound(); }
    /*
        Get alpha
    */
    public int GetAlpha() { return this.mButton.GetAlpha(); }
}