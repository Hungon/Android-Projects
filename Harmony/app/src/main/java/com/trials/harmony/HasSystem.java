package com.trials.harmony;

/**
 * Created by Kohei Moroi on 7/24/2016.
 */
public interface HasSystem {
    // each mode
    int MODE_SOUND      = 0;
    int MODE_SENTENCE   = 1;
    int MODE_ASSOCIATION = 2;
    int MODE_ASSOCIATION_IN_EMOTIONS = 3;
    int MODE_ASSOCIATION_IN_FRUITS  = 4;
    int MODE_ASSOCIATION_IN_ALL = 5;
    int MODE_LIST[] = {
            MODE_SOUND,MODE_SENTENCE,
            MODE_ASSOCIATION,
            MODE_ASSOCIATION_IN_EMOTIONS,
            MODE_ASSOCIATION_IN_FRUITS,
            MODE_ASSOCIATION_IN_ALL
    };
    // level
    int LEVEL_EASY      = 0;
    int LEVEL_NORMAL    = 1;
    int LEVEL_HARD      = 2;
    int LEVEL_LIST[] = {
            LEVEL_EASY,LEVEL_NORMAL,LEVEL_HARD
    };
    // kind of experience
    int EXPERIENCED_FIRST_IMPRESSION_IN_OPENING   = 0x01;
    int EXPERIENCE_DONE_TUTORIAL                  = 0x02;
    int EXPERIENCE_DONE_PROLOGUE_SOUND_MODE       = 0x04;
    int EXPERIENCE_DONE_PROLOGUE_SENTENCE_MODE    = 0x08;
    int EXPERIENCED_FIRST_IMPRESSION_IN_MAIN_MENU = 0x10;
    int EXPERIENCED_FIRST_IMPRESSION_IN_RESULT    = 0x20;
    int EXPERIENCE_DONE_PROLOGUE_ASSOCIATION_MODE = 0x40;


    // each mode
    int SYSTEM_EXPERIENCE   = 0;
    int SYSTEM_MODE         = 1;
    int SYSTEM_LEVEL        = 2;
    int SYSTEM_MUSIC_NUMBER = 3;
    int SYSTEM_KIND         = 4;
}