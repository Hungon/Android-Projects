package com.trials.harmony;

/**
 * Created by Kohei Moroi on 7/24/2016.
 */
public interface HasScene {
    int     SCENE_NOTHING     = -1;
    int     SCENE_OPENING     = 0;
    int     SCENE_PROLOGUE    = 1;
    int     SCENE_PLAY        = 2;
    int     SCENE_MAIN_MENU   = 3;
    int     SCENE_RESULT      = 4;
    int     SCENE_TUTORIAL    = 5;
    int     SCENE_CREDIT_VIEW = 6;
    String WORDS_OF_SCENES[] = {
            "Opening","Prologue","Play","Main menu","Result","Tutorial","Credit view"
    };
}
