package com.trials.harmony;

import android.content.Context;
import android.graphics.Point;

/**
 * Created by USER on 4/18/2016.
 */
public class LoadingInRecognition {
    // static variables
    // loading image
    // size
    private final static Point      LOADING_IMAGE_SIZE = new Point(256,64);
    // animation setting
    private final static int        ANIMATION_LOADING_COUNT_MAX     = 3;
    private final static int        ANIMATION_LOADING_FRAME         = 20;
    // filed
    private  Image                  m_Image;
    private Context                 m_Context;
    private CharacterEx            m_LoadingImage;
    
    /*
        Constructor
    */
    public LoadingInRecognition(Context context, Image image) {
        this.m_Context = context;
        this.m_Image = image;
        // allot the memory
        this.m_LoadingImage = new CharacterEx(context,image);
    }
    /*
        Initialize the recognizer
    */
    public void InitLoadingInRecognition() {
        Point screen = MainView.GetScreenSize();
        // image setting
        int x = (screen.x - LOADING_IMAGE_SIZE.x) >> 1;
        int y = 100;
        this.m_LoadingImage.InitCharacterEx(
                "recognizerimages",
                x, y,
                LOADING_IMAGE_SIZE.x,
                LOADING_IMAGE_SIZE.y,
                255, 1.0f, 0);
        // animation setting
        this.m_LoadingImage.InitAnimation(
                ANIMATION_LOADING_COUNT_MAX,
                ANIMATION_LOADING_FRAME, 0);
    }
    /*
        Update
    */
    public void UpdateLoadingInRecognition() {
        // to update the speech recognizer image to update
        this.m_LoadingImage.UpdateCharacterEx(true); // to update animation
    }
    /*
        Draw
    */
    public void DrawLoadingInRecognition() {
        this.m_LoadingImage.DrawCharacterEx();       // to draw the loading image
    }
    /*
        Release
    */
    public void ReleaseLoadingInRecognition() {
        this.m_LoadingImage.ReleaseCharacterEx();
        this.m_LoadingImage = null;
    }
}