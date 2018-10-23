package com.trials.harmony;

import android.graphics.Point;

/**
 * Created by Kohei Moroi on 7/21/2016.
 */
public interface HasButtons {
    String TASK_BUTTON_FILE_NAME = "taskbuttons";
    Point TASK_BUTTON_SIZE = new Point(64,64);
    // kind of button
    int    BUTTON_EMPTY           = -1;
    int    BUTTON_RECOGNITION     = 0;
    int    BUTTON_START           = 1;
    int    BUTTON_COLOUR_RED      = 2;
    int    BUTTON_COLOUR_BLUE     = 3;
    int    BUTTON_COLOUR_YELLOW   = 4;
    int    BUTTON_COLOUR_GREEN    = 5;
    int    BUTTON_COLOUR_WHITE    = 6;
    int    BUTTON_COLOUR_BLACK    = 7;
    int    BUTTON_SOUND           = 8;
    int    BUTTON_SENTENCE        = 9;
    int    BUTTON_TUTORIAL        = 10;
    int    BUTTON_SELECT_MODE     = 11;
    int    BUTTON_SELECT_TUNE     = 12;
    int    BUTTON_TUNE_NUMBER     = 13;
    int    BUTTON_PLAY_THE_GAME   = 14;
    int    BUTTON_SELECT_LEVEL    = 15;
    int    BUTTON_EASY            = 16;
    int    BUTTON_NORMAL          = 17;
    int    BUTTON_HARD            = 18;
    int    BUTTON_SENTENCE_RED    = 19;
    int    BUTTON_SENTENCE_BLUE   = 20;
    int    BUTTON_SENTENCE_YELLOW = 21;
    int    BUTTON_SENTENCE_GREEN  = 22;
    int    BUTTON_SENTENCE_WHITE  = 23;
    int    BUTTON_SENTENCE_BLACK  = 24;
    int    BUTTON_CREDIT_VIEW     = 25;
    int    BUTTON_CALL_TASK_TABLE = 26;
    int    BUTTON_PREVIEW         = 27;
    int    BUTTON_NEXT            = 28;
    int    BUTTON_RETURN_TO_OP    = 29;
    int    BUTTON_RETURN_TO_MA    = 30;
    int    BUTTON_COLOUR_PURE_VIOLET = 31;
    int    BUTTON_COLOUR_PASTEL_MAGENTA = 32;
    int    BUTTON_SENTENCE_PURE_VIOLET  = 33;
    int    BUTTON_SENTENCE_PASTEL_MAGENTA  = 34;
    int    BUTTON_ASSOCIATION = 35;
    int    BUTTON_ASSOCIATION_IN_EMOTION = 36;
    int    BUTTON_ASSOCIATION_IN_FRUITS = 37;
    int    BUTTON_ASSOCIATION_IN_ALL = 38;
    int    BUTTON_ASSOCIATION_RED = 39;
    int    BUTTON_ASSOCIATION_BLUE = 40;
    int    BUTTON_ASSOCIATION_YELLOW = 41;
    int    BUTTON_ASSOCIATION_GREEN = 42;
    int    BUTTON_ASSOCIATION_WHITE = 43;
    int    BUTTON_ASSOCIATION_BLACK = 44;
    int    BUTTON_ASSOCIATION_VIOLET = 45;
    int    BUTTON_ASSOCIATION_PINK = 46;
    int    BUTTON_ASSOCIATION_LIBRARY = 47;
    int    BUTTON_COLOUR_ORANGE = 48;
    int    BUTTON_SENTENCE_ORANGE = 49;
    int    BUTTON_ASSOCIATION_ORANGE = 50;
    int    BUTTON_BACK_KEY_TO_MAIN_MENU = 51;
    int    BUTTON_BACK_KEY_TO_MAIN_PROLOGUE = 52;

    // kind of guidance words in to used in GuidanceManager class.
    String BUTTONS_GUIDANCE_WORDS[] = {
            "it's recognition button",
            "it's the button to translate to main menu",
            "red",
            "blue",
            "yellow",
            "green",
            "white",
            "black",
            "it's sound mode",
            "it's sentence mode",
            "it's the button to start tutorial",
            "it's the button to select mode",
            "it's the button to select music",
            "The music number is ",           // to add number of music later.
            "it's the button to play the game",
            "it's the button to select game-level",
            "it's easy button",
            "it's normal button",
            "it's hard button",
            "red",
            "blue",
            "yellow",
            "green",
            "white",
            "black",
            "it's the button to see credit",
            "you can obtain the process as double tapped",
            "it's preview button",
            "it's next button",
            "it's the button to return to opening",
            "it's the button to return to main menu",
            "violet",
            "pink",
            "violet",
            "pink",
            "it's association game",
            "Emotions mode",
            "Fruits mode",
            "All button",
            "red",
            "blue",
            "yellow",
            "green",
            "white",
            "black",
            "violet",
            "pink",
            "the library in association mode",
            "orange",
            "orange",
            "orange",
            "you returned the main menu scene",
            "you returned the first page in the task table"
    };

    // buttons list of image
    int[] BUTTONS_LIST = {
            BUTTON_RECOGNITION,
            BUTTON_START,
            BUTTON_SOUND,
            BUTTON_SENTENCE,
            BUTTON_TUTORIAL,
            BUTTON_SELECT_MODE,
            BUTTON_SELECT_TUNE,
            BUTTON_TUNE_NUMBER,
            BUTTON_PLAY_THE_GAME,
            BUTTON_SELECT_LEVEL,
            BUTTON_EASY,
            BUTTON_NORMAL,
            BUTTON_HARD,
            BUTTON_CREDIT_VIEW,
            BUTTON_CALL_TASK_TABLE,
            BUTTON_PREVIEW,
            BUTTON_NEXT,
            BUTTON_RETURN_TO_OP,
            BUTTON_RETURN_TO_MA,
            BUTTON_ASSOCIATION,
            BUTTON_ASSOCIATION_IN_EMOTION,
            BUTTON_ASSOCIATION_IN_FRUITS,
            BUTTON_ASSOCIATION_IN_ALL,
            BUTTON_ASSOCIATION_RED,
            BUTTON_ASSOCIATION_BLUE,
            BUTTON_ASSOCIATION_YELLOW,
            BUTTON_ASSOCIATION_GREEN,
            BUTTON_ASSOCIATION_WHITE,
            BUTTON_ASSOCIATION_BLACK,
            BUTTON_ASSOCIATION_VIOLET,
            BUTTON_ASSOCIATION_PINK,
            BUTTON_ASSOCIATION_LIBRARY,
            BUTTON_ASSOCIATION_ORANGE,

    };

    // colours
    int[]  BUTTON_COLOURS = {
            BUTTON_COLOUR_RED,
            BUTTON_COLOUR_BLUE,
            BUTTON_COLOUR_YELLOW,
            BUTTON_COLOUR_GREEN,
            BUTTON_COLOUR_WHITE,
            BUTTON_COLOUR_BLACK,
            BUTTON_COLOUR_PURE_VIOLET,
            BUTTON_COLOUR_PASTEL_MAGENTA,
            BUTTON_COLOUR_ORANGE
    };
    // kind of colour
    int COLOUR_KIND = BUTTON_COLOURS.length;

    // sentence buttons
    int[] BUTTON_SENTENCES = {
            BUTTON_SENTENCE_RED,
            BUTTON_SENTENCE_BLUE,
            BUTTON_SENTENCE_YELLOW,
            BUTTON_SENTENCE_GREEN,
            BUTTON_SENTENCE_WHITE,
            BUTTON_SENTENCE_BLACK,
            BUTTON_SENTENCE_PURE_VIOLET,
            BUTTON_SENTENCE_PASTEL_MAGENTA,
            BUTTON_SENTENCE_ORANGE
    };
    // kind of sentence
    int SENTENCE_KIND = BUTTON_SENTENCES.length;

    int BUTTON_ASSOCIATIONS_IN_EMOTIONS[] = {
            BUTTON_ASSOCIATION_RED,
            BUTTON_ASSOCIATION_BLUE,
            BUTTON_ASSOCIATION_YELLOW,
            BUTTON_ASSOCIATION_GREEN,
            BUTTON_ASSOCIATION_WHITE,
            BUTTON_ASSOCIATION_BLACK,
            BUTTON_ASSOCIATION_VIOLET,
            BUTTON_ASSOCIATION_PINK,
            BUTTON_ASSOCIATION_ORANGE
    };
    int BUTTON_ASSOCIATIONS_IN_FRUITS[] = {
            BUTTON_ASSOCIATION_RED,
            BUTTON_ASSOCIATION_BLUE,
            BUTTON_ASSOCIATION_YELLOW,
            BUTTON_ASSOCIATION_GREEN,
            BUTTON_ASSOCIATION_WHITE,
            BUTTON_ASSOCIATION_VIOLET,
            BUTTON_ASSOCIATION_ORANGE
    };
    int BUTTON_ASSOCIATIONS_IN_ALL[] = {
            BUTTON_ASSOCIATION_RED,
            BUTTON_ASSOCIATION_BLUE,
            BUTTON_ASSOCIATION_YELLOW,
            BUTTON_ASSOCIATION_GREEN,
            BUTTON_ASSOCIATION_WHITE,
            BUTTON_ASSOCIATION_BLACK,
            BUTTON_ASSOCIATION_VIOLET,
            BUTTON_ASSOCIATION_PINK,
            BUTTON_ASSOCIATION_ORANGE
    };
}
