package com.trials.harmony;

import android.content.Context;

/**
 * Created by Kohei Moroi on 10/10/2016.
 */

public class Insert implements HasSystem, HasButtons, HasMusicInfo, HasRecords, FileManager, HasScene {
    final static int MUSIC_RECOGNIZED = 0;
    final static int MUSIC_PLAYING    = 1;

    static String insertSentenceForButtonType(int type) {
        if (type == BUTTON_EMPTY) return "";
        return BUTTONS_GUIDANCE_WORDS[type];
    }
    static String insertWordOfScene(int scene) {
        if (scene < SCENE_OPENING || SCENE_CREDIT_VIEW < scene) return "";
        return WORDS_OF_SCENES[scene];
    }
    static String insertWordOfMode() {
        String guidance = "";
        int mode = SystemManager.GetPlayMode();
        if (mode == MODE_SOUND) {
            guidance = "sound mode";
        } else if (mode == MODE_SENTENCE) {
            guidance = "sentence mode";
        } else if (mode == MODE_ASSOCIATION) {
            guidance = "association game";
        } else if (mode == MODE_ASSOCIATION_IN_EMOTIONS) {
            guidance = "emotions mode";
        } else if (mode == MODE_ASSOCIATION_IN_FRUITS) {
            guidance = "fruits mode";
        } else if (mode == MODE_ASSOCIATION_IN_ALL) {
            guidance = "all mode";
        }
        return guidance;
    }
    static int insertFixedExperience(int mode,int scene) {
        int ex = -1;
        if (scene == SCENE_OPENING) {
            ex = EXPERIENCED_FIRST_IMPRESSION_IN_OPENING;
        } else if (scene == SCENE_MAIN_MENU) {
            ex = EXPERIENCED_FIRST_IMPRESSION_IN_MAIN_MENU;
        } else if (scene == SCENE_TUTORIAL) {
            ex = EXPERIENCE_DONE_TUTORIAL;
        } else if (scene == SCENE_RESULT) {
            ex = EXPERIENCED_FIRST_IMPRESSION_IN_RESULT;
        } else if (scene == SCENE_PROLOGUE) {
            switch (mode) {
                case MODE_SOUND:
                    ex = EXPERIENCE_DONE_PROLOGUE_SOUND_MODE;
                    break;
                case MODE_SENTENCE:
                    ex = EXPERIENCE_DONE_PROLOGUE_SENTENCE_MODE;
                    break;
                case MODE_ASSOCIATION_IN_FRUITS:
                case MODE_ASSOCIATION_IN_EMOTIONS:
                case MODE_ASSOCIATION_IN_ALL:
                    ex = EXPERIENCE_DONE_PROLOGUE_ASSOCIATION_MODE;
                    break;
                default:
            }
        }
        return ex;
    }
    static String insertWordOfLevel() {
        int level[] = {LEVEL_EASY,LEVEL_NORMAL,LEVEL_HARD};
        int get = SystemManager.GetGameLevel();
        String word[] =  {"easy","normal","hard"};
        String guidance = "";
        for (int i = 0; i < level.length; i++) {
            if (get == level[i]) {
                guidance = word[i];
                break;
            }
        }
        return guidance;
    }
    static String[] insertMusicInfo(int kind) {
        int trackNum = 0;
        String res[] = null;
        if (kind == MUSIC_RECOGNIZED) {
            trackNum = SystemManager.getMusicId()+1;
        } else if (kind == MUSIC_PLAYING) {
            trackNum = MusicSelector.GetCurrentElement()+1;
        }
        if (trackNum == 0) return res;
        String number = Integer.toString(trackNum);
        res = new String[4];
        res[0] = number;
        System.arraycopy(MUSIC_INFO[trackNum-1],0,res,1,MUSIC_INFO[trackNum-1].length);
        return res;
    }
    static int[] insertRecords(Context context, int musicId) {
        Record record = new Record(context);
        int mode = SystemManager.GetPlayMode();
        int level = SystemManager.GetGameLevel();
        return record.LoadRecordFileFromLocal(
                FILE_EACH_BEST_RECORD[mode]+
                FILE_NAME_EACH_SUFFIX[level]+
                Integer.toString(musicId),
                SCORE_KIND);
    }
}
