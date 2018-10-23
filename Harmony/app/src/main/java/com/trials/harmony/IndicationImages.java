package com.trials.harmony;

import android.content.Context;
import android.graphics.Point;

/**
 * Created by Kohei Moroi on 8/29/2016.
 */
public class IndicationImages extends ButtonManager implements HasScene, HasButtons {
    // static variables
    // each titles' size
    private final static Point TITLES_SIZE[] = {
            new Point(288,64),          // opening
            new Point(256,64),          // prologue
            new Point(),                // play
            new Point(360,64),          // main menu
            new Point(192,64),          // result
            new Point(250,64),          // tutorial
            new Point(360,64),          // credit view
    };
    // each title
    private final static int TITLE_HARMONY      = 0;
    private final static int TITLE_TUTORIAL     = 1;
    private final static int TITLE_MAIN_MENU    = 2;
    private final static int TITLE_SELECT_MODE  = 3;
    private final static int TITLE_SELECT_MUSIC = 4;
    private final static int TITLE_PROLOGUE     = 5;
    private final static int TITLE_RESULT       = 6;
    private final static int TITLE_SELECT_LEVEL = 8;
    private final static int TITLE_CREDIT_VIEW  = 9;
    private final static int EACH_TOP_TITLE[] = {
            TITLE_HARMONY,      // opening
            TITLE_PROLOGUE,     // prologue
            -1,                 // play
            TITLE_MAIN_MENU,    // main menu
            TITLE_RESULT,       // result
            TITLE_TUTORIAL,     // tutorial
            TITLE_CREDIT_VIEW,  // credit view
    };
    private final static int EACH_LEVEL_ICON[] = {BUTTON_EASY,BUTTON_NORMAL,BUTTON_HARD};
    private final static int EACH_MODE_ICON[] = {
            BUTTON_SOUND,BUTTON_SENTENCE,BUTTON_ASSOCIATION,
            BUTTON_ASSOCIATION_IN_EMOTION,BUTTON_ASSOCIATION_IN_FRUITS,
            BUTTON_ASSOCIATION_IN_ALL
    };
    // filed
    private CharacterEx mTitleImage;
    private CharacterEx mEachIcon[];
    private int         mCurrentScene;
    private int         mVariableAlpha;
    /*
        Constructor
    */
    public IndicationImages(Context context, Image image, int currentScene) {
        this.mCurrentScene = currentScene;
        // except for play scene
        if (this.mCurrentScene == SCENE_PLAY) return;
        this.mTitleImage = new CharacterEx(context,image);
        if (this.mCurrentScene == SCENE_MAIN_MENU || this.mCurrentScene == SCENE_PROLOGUE ||
            this.mCurrentScene == SCENE_RESULT) {
            this.mEachIcon = new CharacterEx[2];
            for (int i = 0; i < this.mEachIcon.length; i++) {
                this.mEachIcon[i] = new CharacterEx(context,image);
                this.mEachIcon[i].mType = -1;
            }
        }
    }
    /*
        Initialize
    */
    public void InitImage() {
        if (this.mCurrentScene == SCENE_PLAY) return;
        Point screen = MainView.GetScreenSize();
        this.mTitleImage.InitCharacterEx(
                "titleimages",
                (screen.x-TITLES_SIZE[this.mCurrentScene].x)>>1,10,
                TITLES_SIZE[this.mCurrentScene].x,TITLES_SIZE[this.mCurrentScene].y,
                0,TITLES_SIZE[this.mCurrentScene].y*EACH_TOP_TITLE[this.mCurrentScene],
                1,1.0f,EACH_TOP_TITLE[this.mCurrentScene]);
        // to change alpha
        this.mVariableAlpha = 2;
        // set each icon image
        if (this.mCurrentScene == SCENE_MAIN_MENU || this.mCurrentScene == SCENE_PROLOGUE ||
            this.mCurrentScene == SCENE_RESULT) {
            int mode = SystemManager.GetPlayMode();
            int level = SystemManager.GetGameLevel();
            this.SetIconImages(mode,level);
        }
    }
    // Set icon images
    private void SetIconImages(int mode, int level) {
        int origin[] = {EACH_MODE_ICON[mode],EACH_LEVEL_ICON[level]};
        int types[] = {mode,level};
        for (int i = 0; i < this.mEachIcon.length; i++) {
            if (types[i] != this.mEachIcon[i].mType) {
                int originY = super.ConvertTypeIntoElementToTransit(origin[i]);
                this.mEachIcon[i].InitCharacterEx(
                        TASK_BUTTON_FILE_NAME,
                        15 + i * TASK_BUTTON_SIZE.x, this.mTitleImage.mPos.y+this.mTitleImage.mSize.y,
                        TASK_BUTTON_SIZE.x, TASK_BUTTON_SIZE.y,
                        0, TASK_BUTTON_SIZE.y * originY,
                        1, 0, types[i]);
            }
        }
    }
    /*
        Update
        to increase or decrease the alpha value by counted time or otherwise
        the current process in each scene.
    */
    public void UpdateImageAlpha() {
        if (this.mCurrentScene == SCENE_PLAY) return;
        int interval = 2;
        // When current scene is Tutorial,
        // to switch the current image by the current element in ResponseManager.
        if (this.mCurrentScene == SCENE_TUTORIAL) {
            // To switch image by current element to switch response process in ResponseManager
            if (!this.mTitleImage.VariableAlpha(this.mVariableAlpha,interval)) {
                int element = ResponseManager.GetSwitchElement();
                // when is selecting mode, to change the current image.
                if ((element & 0x01) == 0x01 && this.mTitleImage.GetType() == TITLE_TUTORIAL) {
                    // change image
                    this.mVariableAlpha = -4;
                    if (this.mTitleImage.GetAlpha() == 0) {
                        this.mTitleImage.SetSize(TITLES_SIZE[SCENE_MAIN_MENU]);
                        this.mTitleImage.SetOriginPosition(new Point(0,64*TITLE_SELECT_MODE));
                        this.mTitleImage.SetType(TITLE_SELECT_MODE);
                        this.mVariableAlpha = 2;
                        // to adjust the position
                        this.mTitleImage.SetPosition(
                                new Point(
                                        MainView.CenterXInScreen(this.mTitleImage.GetSize().x),
                                        this.mTitleImage.GetPosition().y));
                    }
                }
                // when is selecting tune, to change the current image.
                else if ((element & 0x04) == 0x04 && this.mTitleImage.GetType() == TITLE_SELECT_MODE) {
                    // change image
                    this.mVariableAlpha = -4;
                    if (this.mTitleImage.GetAlpha() == 0) {
                        this.mTitleImage.SetOriginPosition(new Point(0,64*TITLE_SELECT_MUSIC));
                        this.mTitleImage.SetType(TITLE_SELECT_MUSIC);
                        this.mVariableAlpha = 2;
                    }
                }
            }
        } else {
            // to change the current alpha of an image.
            this.mTitleImage.VariableAlpha(this.mVariableAlpha,interval);
            // Each icon image
            if (this.mEachIcon != null) {
                int mode = SystemManager.GetPlayMode();
                int level = SystemManager.GetGameLevel();
                // when it's difference between the current type and either system variable,
                // to initialize the icon image.
                this.SetIconImages(mode, level);
                for (CharacterEx icon:this.mEachIcon) {
                    if (!icon.VariableScale(0.1f, 1.0f)) {
                        icon.VariableAlpha(this.mVariableAlpha, interval);
                    }
                }
            }
        }
    }
    /*
        Draw
    */
    public void DrawImage() {
        if (this.mCurrentScene == SCENE_PLAY) return;
        this.mTitleImage.DrawCharacterEx();
        if (this.mEachIcon != null) {
            for (CharacterEx icon : this.mEachIcon) {
                icon.DrawCharacterEx();
            }
        }
    }
    /*
        Release
    */
    public void ReleaseImage() {
        if (this.mCurrentScene == SCENE_PLAY) return;
        if (this.mTitleImage != null) {
            this.mTitleImage.ReleaseCharacterEx();
            this.mTitleImage = null;
        }
        if (this.mEachIcon != null) {
            for (int i = 0; i < this.mEachIcon.length; i++) {
                this.mEachIcon[i].ReleaseCharacterEx();
                this.mEachIcon[i] = null;
            }
        }
    }
}