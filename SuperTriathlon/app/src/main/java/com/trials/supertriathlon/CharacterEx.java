package com.trials.supertriathlon;

import android.content.Context;

/**
 * Created by USER on 3/17/2016.
 */
abstract class CharacterEx extends BaseCharacter {
    // static variables
    // filed
    protected Context     mContext;
    protected Image       mImage;
    protected Animation   mAni;

    /*
        Constructor
    */
    public CharacterEx() {
        this.mContext = null;
        this.mImage = null;
        // allot memory
        this.mAni = new Animation();
    }

    /************************************************************************************
        Each abstract functions
    ***********************************************************************************/
    /*
        Initialize
    */
    abstract protected void InitCharacter();
    /*
        Update
    */
    abstract protected void UpdateCharacter();
    /*
        Release
    */
    abstract protected void ReleaseCharacter();
    /*
        Draw
    */
    abstract protected void DrawCharacter();
}