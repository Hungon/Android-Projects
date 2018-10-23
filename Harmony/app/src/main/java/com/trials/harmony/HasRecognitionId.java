package com.trials.harmony;

/**
 * Created by Kohei Moroi on 7/21/2016.
 */
public interface HasRecognitionId {
    // the id to execute the process
    int RECOGNITION_ID_EMPTY               = -1;
    int RECOGNITION_ID_TO_PROLOGUE         = 0;
    int RECOGNITION_ID_TO_OPENING          = 1;
    int RECOGNITION_ID_CORRECT_RED         = 2;
    int RECOGNITION_ID_CORRECT_BLUE        = 3;
    int RECOGNITION_ID_CORRECT_YELLOW      = 4;
    int RECOGNITION_ID_CORRECT_GREEN       = 5;
    int RECOGNITION_ID_CORRECT_WHITE       = 6;
    int RECOGNITION_ID_CORRECT_BLACK       = 7;
    int RECOGNITION_ID_TO_PLAY             = 8;
    int RECOGNITION_ID_TO_SELECT_MODE      = 9;
    int RECOGNITION_ID_SOUND_MODE          = 10;
    int RECOGNITION_ID_SENTENCE_MODE       = 11;
    int RECOGNITION_ID_SELECT_TUNE         = 12;
    int RECOGNITION_ID_YES                 = 13;
    int RECOGNITION_ID_NO                  = 14;
    int RECOGNITION_ID_TO_TUTORIAL         = 15;
    int RECOGNITION_ID_LEVEL_EASY          = 16;
    int RECOGNITION_ID_LEVEL_NORMAL        = 17;
    int RECOGNITION_ID_LEVEL_HARD          = 18;
    int RECOGNITION_ID_SELECT_MODE         = 19;
    int RECOGNITION_ID_SELECT_LEVEL        = 20;
    int RECOGNITION_ID_SELECT_PLAY_GAME    = 21;
    int RECOGNITION_ID_TO_CREDIT_VIEW      = 22;
    int RECOGNITION_ID_CORRECT_VIOLET      = 23;
    int RECOGNITION_ID_CORRECT_PINK        = 24;
    int RECOGNITION_ID_SELECT_ASSOCIATION  = 25;
    int RECOGNITION_ID_ASSOCIATION_IN_EMOTIONS = 26;
    int RECOGNITION_ID_ASSOCIATION_IN_FRUITS  = 27;
    int RECOGNITION_ID_ASSOCIATION_IN_ALL = 28;
    int RECOGNITION_ID_SELECT_REFERENCE_TO_ASSOCIATION        = 29;
    int RECOGNITION_ID_CORRECT_ORANGE = 30;

    // the id to relative the recognized words
    int[][] RECOGNITION_TO_EXECUTE = {
            {
                    RECOGNITION_ID_TO_SELECT_MODE,
                    RECOGNITION_ID_TO_PROLOGUE,
                    RECOGNITION_ID_TO_OPENING,
                    RECOGNITION_ID_TO_PLAY,
                    RECOGNITION_ID_TO_SELECT_MODE,
                    RECOGNITION_ID_TO_TUTORIAL,
                    RECOGNITION_ID_TO_CREDIT_VIEW
            },                                           // each scene
            {
                    RECOGNITION_ID_CORRECT_RED,
                    RECOGNITION_ID_CORRECT_BLUE,
                    RECOGNITION_ID_CORRECT_YELLOW,
                    RECOGNITION_ID_CORRECT_GREEN,
                    RECOGNITION_ID_CORRECT_WHITE,
                    RECOGNITION_ID_CORRECT_BLACK,
                    RECOGNITION_ID_CORRECT_VIOLET,
                    RECOGNITION_ID_CORRECT_PINK,
                    RECOGNITION_ID_CORRECT_ORANGE
            },                                           // each colour
            {
                    RECOGNITION_ID_SOUND_MODE,
                    RECOGNITION_ID_SENTENCE_MODE,
                    RECOGNITION_ID_SELECT_ASSOCIATION
            },                                           // each mode
            {
                    RECOGNITION_ID_SELECT_TUNE,     // recognition words in order to max of tune's element in MusicSelector class.
            },
            {
                    RECOGNITION_ID_TO_SELECT_MODE,
                    RECOGNITION_ID_TO_PROLOGUE,
                    RECOGNITION_ID_TO_OPENING,
                    RECOGNITION_ID_TO_PLAY,
                    RECOGNITION_ID_TO_SELECT_MODE,
                    RECOGNITION_ID_TO_TUTORIAL
            },                                           // practice in Opening scene
            {
                    RECOGNITION_ID_YES, RECOGNITION_ID_NO
            },
            {
                    RECOGNITION_ID_LEVEL_EASY,RECOGNITION_ID_LEVEL_NORMAL,RECOGNITION_ID_LEVEL_HARD
            },
            {
                    RECOGNITION_ID_CORRECT_RED,
                    RECOGNITION_ID_CORRECT_BLUE,
                    RECOGNITION_ID_CORRECT_YELLOW,
                    RECOGNITION_ID_CORRECT_GREEN,
                    RECOGNITION_ID_CORRECT_WHITE,
                    RECOGNITION_ID_CORRECT_BLACK,
                    RECOGNITION_ID_CORRECT_VIOLET,
                    RECOGNITION_ID_CORRECT_PINK,
                    RECOGNITION_ID_CORRECT_ORANGE
            },                                           // each colour for sentence
            {
                    RECOGNITION_ID_SELECT_MODE,
                    RECOGNITION_ID_SELECT_TUNE,
                    RECOGNITION_ID_SELECT_LEVEL,
                    RECOGNITION_ID_SELECT_PLAY_GAME,
                    RECOGNITION_ID_SELECT_REFERENCE_TO_ASSOCIATION
            },                                           // selections in main menu scene
            {
                    RECOGNITION_ID_SELECT_TUNE,     // recognition words in order to max of tune's element in MusicSelector class.
            },
            {
                    RECOGNITION_ID_ASSOCIATION_IN_EMOTIONS,
                    RECOGNITION_ID_ASSOCIATION_IN_FRUITS,    // association game
                    RECOGNITION_ID_ASSOCIATION_IN_ALL
            },
            // each association id
            {       RECOGNITION_ID_CORRECT_RED,
                    RECOGNITION_ID_CORRECT_BLUE,
                    RECOGNITION_ID_CORRECT_YELLOW,
                    RECOGNITION_ID_CORRECT_GREEN,
                    RECOGNITION_ID_CORRECT_WHITE,
                    RECOGNITION_ID_CORRECT_BLACK,
                    RECOGNITION_ID_CORRECT_VIOLET,
                    RECOGNITION_ID_CORRECT_PINK,
                    RECOGNITION_ID_CORRECT_ORANGE
            },
            {       RECOGNITION_ID_CORRECT_RED,
                    RECOGNITION_ID_CORRECT_BLUE,
                    RECOGNITION_ID_CORRECT_YELLOW,
                    RECOGNITION_ID_CORRECT_GREEN,
                    RECOGNITION_ID_CORRECT_WHITE,
                    RECOGNITION_ID_CORRECT_VIOLET,
                    RECOGNITION_ID_CORRECT_ORANGE
            },
            {
                    RECOGNITION_ID_CORRECT_RED,
                    RECOGNITION_ID_CORRECT_BLUE,
                    RECOGNITION_ID_CORRECT_YELLOW,
                    RECOGNITION_ID_CORRECT_GREEN,
                    RECOGNITION_ID_CORRECT_WHITE,
                    RECOGNITION_ID_CORRECT_BLACK,
                    RECOGNITION_ID_CORRECT_VIOLET,
                    RECOGNITION_ID_CORRECT_PINK,
                    RECOGNITION_ID_CORRECT_ORANGE
            }
    };
}