package com.trials.harmony;

import android.content.Context;

import org.jetbrains.annotations.Contract;

/**
 * Created by Kohei Moroi on 7/24/2016.
 */
public class SystemManager implements HasSystem, FileManager, HasMusicInfo {

    private static Context  mContext;
    // each system variable
    private static int[] mSystem;
    private static Utility sUtility;

    // Constructor
    public SystemManager(Context context) {
        mContext = context;
        mSystem = new int[SYSTEM_KIND];
        sUtility = new Utility();
    }
    /*
        To load system variables to global
    */
    public void LoadSystemVariables() {
        // get user's experience
        // return value is a bit number.
        Record record = new Record(mContext);
        // to load each system values
        mSystem = record.LoadRecordFileFromLocal(FILE_EXPERIENCE, mSystem.length);
        // when the current mode is Association, to set the something mode relative to association.
        checkInvalidMode();
    }
    /*****************************************************
     Each setter functions
     *****************************************************/
    /*
        Set user's experience
    */
    public static void SetUserExperience(int bitNum) {
        mSystem[SYSTEM_EXPERIENCE] |= bitNum;
        // update experience
        Record record = new Record(mContext);
        record.SaveRecordToLocalFile(FILE_EXPERIENCE,mSystem,mSystem.length);
    }
    /*
        Set play mode
    */
    public static void SetPlayMode(int mode) {
        if (MODE_LIST[0] <= mode && mode <= MODE_LIST[MODE_LIST.length-1]) {
            // when overwrite the mode,
            // to save the value to local file
            mSystem[SYSTEM_MODE] = mode;
            Record record = new Record(mContext);
            record.SaveRecordToLocalFile(FILE_EXPERIENCE,mSystem,mSystem.length);
        }
    }
    /*
        Set game level
    */
    public static void SetGameLevel(int level) {
        if (LEVEL_EASY <= level && level <= LEVEL_HARD) {
            mSystem[SYSTEM_LEVEL] = level;
            // when overwrite the level,
            // to save the value to local file
            Record record = new Record(mContext);
            record.SaveRecordToLocalFile(FILE_EXPERIENCE,mSystem,mSystem.length);
        }
    }
    static void saveMusicInfo(int trackNum) {
        if (0 <= trackNum && trackNum < MUSIC_INFO.length) {
            mSystem[SYSTEM_MUSIC_NUMBER] = trackNum;
            Record record = new Record(mContext);
            record.SaveRecordToLocalFile(FILE_EXPERIENCE,mSystem,mSystem.length);
        }
    }

    static void checkInvalidMode() {
        // when the current mode is Association, to set the something mode relative to association.
        if (SystemManager.GetPlayMode() == MODE_ASSOCIATION) {
            int modes[] = {
                    MODE_ASSOCIATION_IN_EMOTIONS,
                    MODE_ASSOCIATION_IN_FRUITS,
                    MODE_ASSOCIATION_IN_ALL
            };
            int ran = sUtility.GetRandom(modes.length);
            // set the mode
            SystemManager.SetPlayMode(modes[ran]);
        }
    }

    /*
       Get user's experience
    */
    @Contract(pure = true)
    public static int GetUserExperience() { return mSystem[SYSTEM_EXPERIENCE]; }
    /*
        Get play mode
    */
    @Contract(pure = true)
    public static int GetPlayMode() { return mSystem[SYSTEM_MODE]; }
    /*
        Get game level
    */
    @Contract(pure = true)
    public static int GetGameLevel() { return mSystem[SYSTEM_LEVEL]; }
    @Contract(pure = true)
    static int getMusicId() { return mSystem[SYSTEM_MUSIC_NUMBER]; }
}