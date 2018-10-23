package com.trials.harmony;

import android.graphics.Point;

/**
 * Created by Kohei Moroi on 7/25/2016.
 */
public interface HasScoreImage {
    // kind of score number image
    int    SCORE_TYPE_NORMAL       = 0;
    int    SCORE_TYPE_GRADATION    = 1;
    // file of score number image
    String FILE_SCORE_NUMBER[] = {
            "scorenumber1",     // normal
            "scorenumber2",     // gradation
    };
    // score number
    Point SCORE_NUMBER_SIZE = new Point(40, 50);
    // Score colour image
    Point SCORE_COLOUR_SIZE = new Point(32,32);
    // score number images
    int   SCORE_NUMBER_COLOR_WHITE        = 0;
    int   SCORE_NUMBER_COLOR_BLACK        = 1;
    int   SCORE_NUMBER_COLOR_RED          = 2;
    int   SCORE_NUMBER_COLOR_BLUE         = 3;
    int   SCORE_NUMBER_COLOR_GREEN        = 4;
    int   SCORE_NUMBER_COLOR_YELLOW       = 5;
    int   SCORE_NUMBER_COLOR_LIGHT_BLUE   = 6;
    int   SCORE_NUMBER_KIND_OF_COLOR      = 7;
    // score colours
    int SCORE_COLOUR_RED        = 0;
    int SCORE_COLOUR_BLUE       = 1;
    int SCORE_COLOUR_YELLOW     = 2;
    int SCORE_COLOUR_GREEN      = 3;
    int SCORE_COLOUR_WHITE      = 4;
    int SCORE_COLOUR_BLACK      = 5;
    int SCORE_COLOUR_VIOLET     = 6;
    int SCORE_COLOUR_PINK       = 7;
    int SCORE_COLOUR_ORANGE     = 8;
    int SCORE_COLOUR_LIST[] = {
            SCORE_COLOUR_RED,
            SCORE_COLOUR_BLUE,
            SCORE_COLOUR_YELLOW,
            SCORE_COLOUR_GREEN,
            SCORE_COLOUR_WHITE,
            SCORE_COLOUR_BLACK,
            SCORE_COLOUR_VIOLET,
            SCORE_COLOUR_PINK,
            SCORE_COLOUR_ORANGE
    };
}
