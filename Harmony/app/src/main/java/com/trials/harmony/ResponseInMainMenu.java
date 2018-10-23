package com.trials.harmony;

/**
 * Created by Kohei Moroi on 7/24/2016.
 */
public class ResponseInMainMenu extends Response implements HasButtons, HasSystem, FileManager {


    // the changeable type which is button type to transition selection in main menu scene.
    private int mSelectedMode;

    // Constructor
    public ResponseInMainMenu() {
        mSelectedMode = BUTTON_EMPTY;
        this.mRecognitionMessageId = GUIDANCE_MESSAGE_SELECT_MAIN_MENU;
    }

    /*
        Update
    */
    @Override
    protected int UpdateResponse() {
        boolean guiding = GuidanceManager.GetIsGuiding();
        if ((mSwitchElement & UP_TO_FINISH) != UP_TO_FINISH) {
            int selection = RecognitionButtonManager.GetButtonTypeToObtainProcess();
            if (this.validateMode(selection)) this.mSelectedMode = selection;
            if (this.mResponseWords != null) this.mResponseWords = null;

            // to diverge the selection from the current mode
            if (mSelectedMode == BUTTON_SELECT_MODE) {
                return this.IsSelectedTheMode();
            } else if (mSelectedMode == BUTTON_SELECT_TUNE) {
                return this.IsSelectedMusic();
            } else if (mSelectedMode == BUTTON_SELECT_LEVEL) {
                return this.IsSelectedLevel();
                // When selected the button to play the game.
            } else if (mSelectedMode == BUTTON_PLAY_THE_GAME) {
                return this.IsSelectedPlayGame();
            } else if (mSelectedMode == BUTTON_ASSOCIATION_LIBRARY) {
                return this.IsSelectedLibrary();
            } else {
                boolean validate = super.ValidateSelectionByRecognisedId(GUIDANCE_MESSAGE_SELECT_MAIN_MENU);
                // when user already selected the mode, not to call the recognizer.
                if (!validate) {
                    if (this.validateMode(this.mSelectedMode))  return EMPTY;
                    int callCount = RecognitionMode.GetCurrentCountCalledRecognizer();
                    if (callCount == 0) {
                        if (this.mUtility.ToMakeTheInterval(300)) {
                            mSwitchElement |= TO_CALL_RECOGNIZER;
                            this.mRecognitionMessageId = GUIDANCE_MESSAGE_SELECT_MAIN_MENU;
                            return EMPTY;
                        }
                    }
                } else {
                    int id = RecognizerManager.GetRecognitionId()[0];
                    mSelectedMode = this.ConvertRecognitionIdIntoButtonId(id);
                    return EMPTY;
                }
            }
            // Eventually, when switch variable was substituted the Up to Finish,
            // to transition to next scene after fixed interval.
        } else if (!guiding && (mSwitchElement & UP_TO_FINISH) == UP_TO_FINISH) {
            if (this.mUtility.ToMakeTheInterval(200)) {
                SceneManager.SetNextScene(this.mNextTransitionScene);
                Wipe.SetAvailableToCreate(true);
                return TRANSITION_TO_NEXT_SCENE;
            }
        }
        return EMPTY;
    }

    /*
        When selected the mode selection
    */
    private int IsSelectedTheMode() {
        int buttonType = RecognitionButtonManager.GetButtonTypeToObtainProcess();
        boolean guiding = GuidanceManager.GetIsGuiding();
        int mode = SystemManager.GetPlayMode();
        if (!guiding && (mSwitchElement & 0x01) != 0x01) {
            mSwitchElement |= 0x01;
            this.mRecognitionMessageId = GUIDANCE_MESSAGE_SELECT_MODE;
            // reinitialize task
            RecognitionButtonManager.reInitializeTaskTable(RecognitionButtonManager.REINITIALIZE_TASK.SELECT_MODE_IN_MAIN_MENU);
            // Mode: to guide words that about button's position
            LeadingManager.setFileIndex(FILE_INDEX_SELECTED_SELECT_MODE);
            return EMPTY;
            // whe user selected either sound or sentence, to set guidance
        } else if (!guiding && this.availableToTransitionByButtonType(buttonType)) {
            this.mResponseWords = new String[1];
            this.mResponseWords[0] = "Ok, you selected ";
            this.mResponseWords[0] += Insert.insertWordOfMode();
            mSwitchElement |= UP_TO_FINISH;
            this.mNextTransitionScene = SCENE_MAIN_MENU;
            return INITIALIZE_GUIDANCE;
        } else if ((this.mSwitchElement&0x02)!=0x02 &&
                !guiding && buttonType == BUTTON_ASSOCIATION) {    // when the current button type is association,
            this.mSwitchElement |= 0x02;
            this.mResponseWords = new String[1];
            // set text file
            LeadingManager.setFileIndex(FILE_INDEX_SELECTED_ASSOCIATION_GAME);
            this.mResponseWords = null;
            RecognitionButtonManager.reInitializeTaskTable(RecognitionButtonManager.REINITIALIZE_TASK.SELECT_ASSOCIATION);
            this.mRecognitionMessageId = GUIDANCE_MESSAGE_SELECT_ASSOCIATION;
            return EMPTY;
        } else if (!guiding && 1 < LeadingManager.GetCountReadFile()&&
                (this.mSwitchElement&0x01)==0x01 && (this.mSwitchElement&0x04)!=0x04) {
            if (this.mUtility.ToMakeTheInterval(100)) {
                this.mSwitchElement |= 0x04;
                this.mSwitchElement |= TO_CALL_RECOGNIZER;
            }
            return EMPTY;
        } else if (super.ValidateSelectionByRecognisedId(GUIDANCE_MESSAGE_SELECT_MODE)) {
            if (this.availableToTransitionByMode(mode)) {
                this.mResponseWords = new String[1];
                this.mResponseWords[0] = "Ok, you selected ";
                this.mResponseWords[0] += Insert.insertWordOfMode();
                mSwitchElement |= UP_TO_FINISH;
                // set next scene
                this.mNextTransitionScene = SCENE_MAIN_MENU;
                return INITIALIZE_GUIDANCE;
            } else if ((this.mSwitchElement&0x08)!=0x08){
                this.mSwitchElement &= ~0x04;
                this.mSwitchElement |= 0x08;
                // set text file
                LeadingManager.setFileIndex(FILE_INDEX_SELECTED_ASSOCIATION_GAME);
                RecognitionButtonManager.reInitializeTaskTable(RecognitionButtonManager.REINITIALIZE_TASK.SELECT_ASSOCIATION);
                this.mRecognitionMessageId = GUIDANCE_MESSAGE_SELECT_ASSOCIATION;   // set guidance message
                this.mResponseWords = null;
                return EMPTY;
            }
        } else if (super.ValidateSelectionByRecognisedId(GUIDANCE_MESSAGE_SELECT_ASSOCIATION)) {
            if (this.availableToTransitionByMode(mode)) {
                this.mResponseWords = new String[1];
                this.mResponseWords[0] = "Ok, you selected ";
                this.mResponseWords[0] += Insert.insertWordOfMode();
                mSwitchElement |= UP_TO_FINISH;
                // set next scene
                this.mNextTransitionScene = SCENE_MAIN_MENU;
                return INITIALIZE_GUIDANCE;
            }
        }
        return EMPTY;
    }

    /*
        When selected the select music
    */
    private int IsSelectedMusic() {
        int buttonType = RecognitionButtonManager.GetButtonTypeToObtainProcess();
        this.mRecognitionMessageId = GUIDANCE_MESSAGE_SELECT_TUNE;
        // as touched Select tune
        if ((mSwitchElement & 0x01) != 0x01) {
            mSwitchElement |= 0x01;
            RecognizerManager.SetGuidanceId(GUIDANCE_MESSAGE_SELECT_TUNE);
            // reinitialize task
            RecognitionButtonManager.reInitializeTaskTable(RecognitionButtonManager.REINITIALIZE_TASK.MUSIC_LIST_IN_MAIN_MENU);

            // Mode: to guide words that about button's position
            LeadingManager.setFileIndex(FILE_INDEX_SELECTED_SELECT_MUSIC);
            return EMPTY;
            // as touched music number
        } else if (buttonType == BUTTON_TUNE_NUMBER) {
            int musicNum = MusicSelector.GetCurrentElement();
            if (musicNum + 1 != this.mPreviewElementToGuide) {
                // set available to play music
                MusicSelector.SetAvailableToPlay(true);
                musicNum++;
                String guidance[] = {
                        "You are playing the number " + musicNum,
                        "",
                        "",
                        "",
                };
                String info[] = Insert.insertMusicInfo(Insert.MUSIC_PLAYING);
                System.arraycopy(info, 1, guidance, 1, info.length - 1);
                this.mResponseWords = new String[guidance.length];
                System.arraycopy(guidance, 0, this.mResponseWords, 0, guidance.length);
                // to set the preview music number
                this.mPreviewElementToGuide = musicNum;
                return INITIALIZE_GUIDANCE;
            }
        }
        boolean validate = super.ValidateSelectionByRecognisedId(GUIDANCE_MESSAGE_SELECT_TUNE);
        if (!validate) {
            // when not to select the element to play music, to recall recognizer to select the element
            if (this.mUtility.ToMakeTheInterval(1500)) {
                mSwitchElement |= TO_CALL_RECOGNIZER;
            }
        } else {
            mSwitchElement |= UP_TO_FINISH;
            String guidance2[] = {
                    "OK, you selected Music number ",
                    "",
                    "",
                    "",
                    "You will be able to listen to the music in the Play scene",
            };
            String info[] = Insert.insertMusicInfo(Insert.MUSIC_RECOGNIZED);
            guidance2[0] += info[0];
            System.arraycopy(info, 1, guidance2, 1, info.length - 1);
            this.mResponseWords = new String[guidance2.length];
            System.arraycopy(guidance2, 0, this.mResponseWords, 0, guidance2.length);
            this.mNextTransitionScene = SCENE_MAIN_MENU;
            return INITIALIZE_GUIDANCE;
        }
        return EMPTY;
    }

    /*
        When selected the level selection
    */
    private int IsSelectedLevel() {
        int buttonType = RecognitionButtonManager.GetButtonTypeToObtainProcess();
        boolean guiding = GuidanceManager.GetIsGuiding();
        this.mRecognitionMessageId = GUIDANCE_MESSAGE_SELECT_LEVEL;
        if (!guiding && (mSwitchElement & 0x01) != 0x01) {
            mSwitchElement |= 0x01;

            RecognizerManager.SetGuidanceId(GUIDANCE_MESSAGE_SELECT_LEVEL);
            // reinitialize task
            RecognitionButtonManager.reInitializeTaskTable(RecognitionButtonManager.REINITIALIZE_TASK.SELECT_LEVEL_IN_MAIN_MENU);

            // Mode: to guide words that about button's position
            LeadingManager.setFileIndex(FILE_INDEX_SELECTED_SELECT_LEVEL);
            return EMPTY;
            // when user has'nt called the recognizer, to call the recognizer
        } else if (!guiding && LeadingManager.GetCountReadFile() == 1 &&
                (mSwitchElement & 0x01) == 0x01 && (mSwitchElement & 0x02) != 0x02) {
            mSwitchElement |= TO_CALL_RECOGNIZER;
            mSwitchElement |= 0x02;
            return EMPTY;
            // whe user pressed either each level, to set guidance
            // and then to transition to main menu scene.
        } else if (!guiding && buttonType == BUTTON_EASY ||
                buttonType == BUTTON_NORMAL || buttonType == BUTTON_HARD) {
            mSwitchElement |= UP_TO_FINISH;
            // set next scene
            this.mNextTransitionScene = SCENE_MAIN_MENU;
            this.mResponseWords = new String[1];
            this.mResponseWords[0] = "Ok, you selected ";
            // set sentence by the current level
            this.mResponseWords[0] += Insert.insertWordOfLevel();
            return INITIALIZE_GUIDANCE;
            // when recognizer did not recognise the sentence,
            // to initialize the guidance and waiting for select of user.
            // when selecting the something of the level,
            // to transition to main menu scene after guiding.
        } else if (super.ValidateSelectionByRecognisedId(GUIDANCE_MESSAGE_SELECT_LEVEL)) {
            mSwitchElement = UP_TO_FINISH;
            this.mResponseWords = new String[1];
            this.mResponseWords[0] = "Ok, you selected ";
            // set sentence by the current level
            this.mResponseWords[0] += Insert.insertWordOfLevel();
            this.mNextTransitionScene = SCENE_MAIN_MENU;
            return INITIALIZE_GUIDANCE;
        }
        return EMPTY;
    }

    private int IsSelectedLibrary() {
        this.mRecognitionMessageId = GUIDANCE_MESSAGE_SELECT_MAIN_MENU;
        return EMPTY;
    }

    /*
        To convert recognition id into button id which are specified id in main menu scene.
        first operation: recognition id.
        return value: button id
    */
    private int ConvertRecognitionIdIntoButtonId(int id) {
        int buttonId[] = {
                BUTTON_SELECT_MODE,
                BUTTON_SELECT_TUNE,
                BUTTON_SELECT_LEVEL,
                BUTTON_PLAY_THE_GAME,
                BUTTON_ASSOCIATION_LIBRARY
        };
        int reId[] = {
                RECOGNITION_ID_SELECT_MODE,
                RECOGNITION_ID_SELECT_TUNE,
                RECOGNITION_ID_SELECT_LEVEL,
                RECOGNITION_ID_SELECT_PLAY_GAME,
                RECOGNITION_ID_SELECT_REFERENCE_TO_ASSOCIATION
        };
        for (int i = 0; i < reId.length; i++) {
            if (id == reId[i]) {
                return buttonId[i];
            }
        }
        return BUTTON_EMPTY;
    }
    /*
        Is selected the button to play the game
    */
    private int IsSelectedPlayGame() {
        boolean guiding = GuidanceManager.GetIsGuiding();
        if ((mSwitchElement & 0x01) != 0x01) {
            mSwitchElement |= 0x01;
            // to set initial description to play
            LeadingManager.GoToNextFile(LeadingManager.INITIAL_DESCRIPTION_TO_PLAY);
        } else if ((mSwitchElement&0x01)==0x01 && !guiding){
            mSwitchElement |= UP_TO_FINISH;
            // next scene
            this.mNextTransitionScene = SCENE_PROLOGUE;
        }
        return EMPTY;
    }

    private boolean validateMode(int buttonType) {
        return (buttonType == BUTTON_SELECT_MODE || buttonType == BUTTON_SELECT_LEVEL ||
                buttonType == BUTTON_SELECT_TUNE || buttonType == BUTTON_ASSOCIATION_LIBRARY ||
                buttonType == BUTTON_PLAY_THE_GAME);
    }

    private boolean availableToTransitionByButtonType(int buttonType) {
        return (buttonType == BUTTON_SOUND || buttonType == BUTTON_SENTENCE ||
                buttonType == BUTTON_ASSOCIATION_IN_EMOTION || buttonType == BUTTON_ASSOCIATION_IN_FRUITS ||
                buttonType == BUTTON_ASSOCIATION_IN_ALL);
    }
    private boolean availableToTransitionByMode(int mode) {
        return (mode == MODE_SOUND || mode == MODE_SENTENCE ||
                mode == MODE_ASSOCIATION_IN_EMOTIONS || mode == MODE_ASSOCIATION_IN_FRUITS ||
                mode == MODE_ASSOCIATION_IN_ALL);
    }
}