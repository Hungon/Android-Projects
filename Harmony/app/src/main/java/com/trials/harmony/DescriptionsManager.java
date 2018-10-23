package com.trials.harmony;

import android.content.Context;

/**
 * Created by Kohei Moroi on 8/30/2016.
 */
abstract class DescriptionsManager implements HasDescriptions, HasButtons,
        HasScene, HasMusicInfo, HasSystem, FileManager, HasRecords, HasRecognitionWords {
    private Context mContext;
    public DescriptionsManager(Context context) { this.mContext = context; }
    // Convert button type into the sentence
    protected String[] ConvertTheButtonTypeIntoSentence(int scene, int buttonType) {
        // opening
        int opButtons[] = {BUTTON_TUTORIAL,BUTTON_START,BUTTON_CREDIT_VIEW};
        // main menu
        int mmButtons[] = {
                BUTTON_SELECT_MODE,BUTTON_SELECT_TUNE,
                BUTTON_SELECT_LEVEL,BUTTON_PLAY_THE_GAME,
                BUTTON_ASSOCIATION_LIBRARY,
                BUTTON_SOUND,BUTTON_SENTENCE,
                BUTTON_ASSOCIATION,BUTTON_ASSOCIATION_IN_EMOTION,
                BUTTON_ASSOCIATION_IN_FRUITS,BUTTON_ASSOCIATION_IN_ALL
        };
        int inSelection[];
        String description[] = null;
        int element = -1;
        if (scene == SCENE_OPENING) {
            inSelection = new int[opButtons.length];
            System.arraycopy(opButtons,0,inSelection,0,opButtons.length);
            for (int i = 0; i < inSelection.length; i++) {
                if (inSelection[i] == buttonType) {
                    element = i;
                    break;
                }
            }
            if (element != -1) {
                description = new String[DESCRIPTION_FOR_BUTTON_SELECTED_IN_OPENING[element].length];
                System.arraycopy(DESCRIPTION_FOR_BUTTON_SELECTED_IN_OPENING[element], 0, description, 0, DESCRIPTION_FOR_BUTTON_SELECTED_IN_OPENING[element].length);
            }
        } else if (scene == SCENE_MAIN_MENU) {
            inSelection = new int[mmButtons.length];
            System.arraycopy(mmButtons, 0, inSelection, 0, mmButtons.length);
            for (int i = 0; i < inSelection.length; i++) {
                if (inSelection[i] == buttonType) {
                    element = i;
                    break;
                }
            }
            if (element != -1) {
                description = new String[DESCRIPTION_FOR_BUTTON_SELECTED_IN_MAIN_MENU[element].length];
                System.arraycopy(DESCRIPTION_FOR_BUTTON_SELECTED_IN_MAIN_MENU[element], 0, description, 0, DESCRIPTION_FOR_BUTTON_SELECTED_IN_MAIN_MENU[element].length);
            }
        }
        return description;
    }

    protected String[] referToLibrary(int loadingType, int buttonType) {
        int selection[] = {BUTTON_ASSOCIATION_LIBRARY,BUTTON_ASSOCIATION_IN_EMOTION,BUTTON_ASSOCIATION_IN_FRUITS};
        String dummy[] = null;
        String res[] = null;
        boolean init = false;
        int eachMode[] = {BUTTON_ASSOCIATION_IN_EMOTION,BUTTON_ASSOCIATION_IN_FRUITS};
        for (int s:selection) {
            if (s==loadingType) {
                init = true;
            }
        }
        if (!init) return null;
        int element = -1;
        if (loadingType==BUTTON_ASSOCIATION_LIBRARY) {
            res = new String[2];
            res[0] = "You can refer to association words in ";
            res[1] = BUTTONS_GUIDANCE_WORDS[buttonType]+".";
            return res;
        } else {
            for (int i = 0; i < BUTTON_ASSOCIATIONS_IN_ALL.length; i++) {
                if (BUTTON_ASSOCIATIONS_IN_ALL[i] == buttonType) {
                    element = i;
                    break;
                }
            }
        }
        if (element == -1) return null;
        switch(loadingType) {
            case BUTTON_ASSOCIATION_IN_EMOTION:
                dummy = new String[ASSOCIATION_WORDS_IN_EMOTIONS[element].length];
                System.arraycopy(ASSOCIATION_WORDS_IN_EMOTIONS[element],0,dummy,0,dummy.length);
                break;
            case BUTTON_ASSOCIATION_IN_FRUITS:
                dummy = new String[ASSOCIATION_WORDS_IN_FRUITS[element].length];
                System.arraycopy(ASSOCIATION_WORDS_IN_FRUITS[element],0,dummy,0,dummy.length);
                break;
            default:
        }
        if (dummy!=null) {
            int row = 3;
            int allot = dummy.length/row;
            allot += dummy.length%row+1;
            res = new String[allot];
            for (int i=0;i<res.length;i++) res[i] = "";
            res[0] = "Association words in "+BUTTONS_GUIDANCE_WORDS[buttonType];
            int line = 1;
            for (int i = 0; i < dummy.length; i++) {
                res[line] += Integer.toString(i+1)+"."+dummy[i]+"  ";
                if ((i+1)%row==0) line++;
            }
        }
        return res;
    }

    // convert music id into the sentence
    protected String[] insertMusicInfoByTheId(int musicId) {
        String musicInfo[] = null;
        if (0 <= musicId && musicId < MUSIC_INFO.length) {
            musicInfo = new String[MUSIC_INFO[musicId].length+1];
            System.arraycopy(MUSIC_INFO[musicId],0,musicInfo,1,MUSIC_INFO[musicId].length);
            musicInfo[0] = Integer.toString(musicId+1);
        } else {
            return musicInfo;
        }
        // to get the best record by the selected music id.
        int items[] = Insert.insertRecords(this.mContext,musicId);
        int sum = musicInfo.length+items.length;
        String sentence[] = new String[sum];
        for (int i = 0; i < sum; i++) {
            // when the loop element reached to max value of music's info,
            // to add each record
            // Eventually, the current element reached to aggregate length,
            // to get the last record and to do break.
            int recordElement = i-musicInfo.length;
            if (i == sum-1) {
                sentence[i] = DESCRIPTION_FOR_RECORDS[recordElement] + items[recordElement];
                break;
            } else if (musicInfo.length <= i) {
                sentence[i] = DESCRIPTION_FOR_RECORDS[recordElement] + items[recordElement];
                continue;
            }
            sentence[i] = DESCRIPTION_FOR_MUSIC[i] + musicInfo[i];
        }
        return sentence;
    }
    protected String[] insertInitialDescriptionToPlay() {
        String info[] = Insert.insertMusicInfo(Insert.MUSIC_RECOGNIZED);
        int record[] = Insert.insertRecords(this.mContext,SystemManager.getMusicId());
        String sentence[] = new String[info.length+record.length+1];
        int element;
        for (int i = 0; i < sentence.length; i++) {
            String add = "";
            if (i == 0) {
                add = "OK, you selected the button to play the game.";
            } else if (0 < i && i <= info.length) {
                element = i-1;
                add = DESCRIPTION_FOR_MUSIC[element]+info[element];
            } else if (info.length < i) {
                element = i-(info.length+1);
                add = DESCRIPTION_FOR_RECORDS[element]+record[element];
            }
            sentence[i] = add;
        }
        return sentence;
    }
    protected String[] insertRepeatableDescriptionWhenResume() {
        String sentence[] = new String[DESCRIPTION_TO_NOTICE_EACH_STYLE.length];
        System.arraycopy(DESCRIPTION_TO_NOTICE_EACH_STYLE,0,sentence,0,DESCRIPTION_TO_NOTICE_EACH_STYLE.length);
        int scene = SceneManager.GetCurrentScene();
        String add = "";
        for (int i = 0; i < sentence.length; i++) {
            if (i == 0) {
                add = Insert.insertWordOfScene(scene)+" scene.";
            } else if (i == 1) {
                add = Insert.insertWordOfMode();
            } else if (i == 2) {
                add = Insert.insertWordOfLevel();
            } else if (i == 3) {
                add = Insert.insertMusicInfo(Insert.MUSIC_RECOGNIZED)[1];
            }
            sentence[i] += add;
        }
        return sentence;
    }
    protected String insertDescriptionToExplain() {
        String description[] = new String[DESCRIPTION_TO_NOTICE_APPEARANCE_COLOURS_IN_PLAY.length];
        System.arraycopy(DESCRIPTION_TO_NOTICE_APPEARANCE_COLOURS_IN_PLAY,0,description,0,DESCRIPTION_TO_NOTICE_APPEARANCE_COLOURS_IN_PLAY.length);
        String res = "";
        int coloursType[] = PlayManager.getAppearKind();
        String coloursSentence = "";
        for (int i = 0; i < coloursType.length; i++) {
            String w = Insert.insertSentenceForButtonType(coloursType[i]);
            if (i == coloursType.length-2) {
                coloursSentence += w +"\\w\\n";
                continue;
            } else if (i == coloursType.length-1) {
                coloursSentence += "and " + w + ".";
                break;
            }
            coloursSentence += w + ", ";
        }
        for (int i = 0; i < description.length; i++) {
            res += description[i];
            if (i == 1) {
                res += Insert.insertWordOfMode()+"\\n";
            } else if (i == 2) {
                res += Insert.insertWordOfLevel() + "\\e";
            } else if (i  == 3) {
                res += "\\w\\n";
            } else if (i == 4) {
                res += coloursSentence+"\\w\\n";
            } else if (i == 5) {
                res += "\\p";
            } else if (i == description.length-1) {
                res += "\\d";
            } else {
                res += "\\n";
            }
        }
        return res;
    }
}