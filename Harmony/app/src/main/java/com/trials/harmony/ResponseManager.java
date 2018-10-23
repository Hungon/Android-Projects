package com.trials.harmony;


import org.jetbrains.annotations.Contract;

/**
 * Created by USER on 4/22/2016.
 */
public class ResponseManager implements HasScene {

    // static variables

    // filed
    private static int mSwitchElement;
    private int mCurrentScene;
    private Response mResponse;
    public String mResponseWords[];
    public boolean mCallingRecognizer;

    /*
        Constructor
    */
    public ResponseManager(int mode, int idMax) {
        this.mCurrentScene = SceneManager.GetCurrentScene();
        // To diverge the allocation of response class
        switch (this.mCurrentScene) {
            case SCENE_OPENING :
                this.mResponse = new ResponseInOp();
                break;
            case SCENE_PROLOGUE:
                this.mResponse = new ResponseInPrologue(mode);
                break;
            case SCENE_PLAY:
                this.mResponse = new ResponseInPlay(idMax);
                break;
            case SCENE_MAIN_MENU:
                this.mResponse = new ResponseInMainMenu();
                break;
            case SCENE_RESULT:
                this.mResponse = new ResponseInResult();
                break;
            case SCENE_TUTORIAL:
                this.mResponse = new ResponseInTutorial();
                break;
            case SCENE_CREDIT_VIEW:
                this.mResponse = new ResponseInCredit();
                break;
            default :
                this.mResponse = null;
        }
    }

    /*
        Initialize
    */
    public void InitResponseManager() {
        mSwitchElement = 0x00;
        // when current scene is Main menu or Tutorial,
        // Reset that element
        if (this.mCurrentScene == SceneManager.SCENE_TUTORIAL) {
            MusicSelector.ResetMusicElementRecognized();
        }
        // Initialize response
        this.mResponse.InitResponse();
        this.mCallingRecognizer = false;
    }

    /*
        Update entirely update in each scene.
    */
    public int UpdateResponseManager() {
        // to update response
        int response = this.mResponse.UpdateResponse();
        this.mResponseWords = this.mResponse.mResponseWords;
        mSwitchElement = this.mResponse.mSwitchElement;
        this.mCallingRecognizer = this.mResponse.ToCheckIdToCallRecognizer();
        return response;
    }

    /*
        Release
    */
    public void ReleaseResponseManager() {
        if (this.mResponseWords != null) this.mResponseWords = null;
        this.mResponse.ReleaseResponse();
        this.mResponse = null;
    }
    /***************************************************************
     Each getter functions
     *************************************************************/
    /*
        Get response words
    */
    public String[] GetResponseWords() { return this.mResponseWords; }
    /*
        Get element to switch process
    */
    @Contract(pure = true)
    public static int GetSwitchElement() { return mSwitchElement; }
    /*
        Get status that calling the recognizer
    */
    public boolean IsCallingRecognizer() { return this.mCallingRecognizer; }
}