package com.trials.harmony;

/**
 * Created by Kohei Moroi on 7/24/2016.
 */
public class ResponseInTutorial extends Response implements HasSystem, HasButtons {
    private static int mSelectedMode;

    // Constructor
    public ResponseInTutorial() {
        mSelectedMode = BUTTON_EMPTY;
    }
    /*
        Update
    */
    @Override
    protected int UpdateResponse() {
        if ((mSwitchElement & UP_TO_FINISH) != UP_TO_FINISH) {
            // To diverge the update from user's experience.
            // user has been experienced the tutorial,
            // to ask user about checking again.
            if ((this.mUserExperience & EXPERIENCE_DONE_TUTORIAL) == EXPERIENCE_DONE_TUTORIAL) {
                return super.ToAskCheckingAgain(SCENE_OPENING,EXPERIENCE_DONE_TUTORIAL);
                // When user has been never experienced the tutorial or
                // who want to check again,
                // to execute the below.
            } else if ((this.mUserExperience & EXPERIENCE_DONE_TUTORIAL) != EXPERIENCE_DONE_TUTORIAL) {
                return this.UpdateResponseInNoExperience();
            }
            // last execution hence above processes don't work.
        } else if ((mSwitchElement & UP_TO_FINISH) == UP_TO_FINISH) {
            if (!GuidanceManager.GetIsGuiding()) {
                // to transition to next scene after guidance and made a few blank time.
                if (this.mUtility.ToMakeTheInterval(100)) {
                    // Set next scene
                    SceneManager.SetNextScene(this.mNextTransitionScene);
                    Wipe.SetAvailableToCreate(true);
                    return TRANSITION_TO_NEXT_SCENE;
                }
            }
        }
        return EMPTY;
    }
    /*
        Update response in no experience or checking again the tutorial
    */
    private int UpdateResponseInNoExperience() {
        int call = RecognitionMode.GetCurrentCountCalledRecognizer();
        boolean guiding = GuidanceManager.GetIsGuiding();
        int id = RecognizerManager.GetRecognitionId()[0];
        int buttonType = RecognitionButtonManager.GetButtonTypeToObtainProcess();
        // when the the current guidance id reached ends up to the last element,
        // to call recognizer.
        if (LeadingManager.GetCountReadFile() == 1 && !guiding &&
            (mSwitchElement & 0x01) != 0x01) {
            if (this.mUtility.ToMakeTheInterval(100)) {
                mSwitchElement |= TO_CALL_RECOGNIZER;
                mSwitchElement |= 0x01;
                // to set guidance message to the recognizer
                this.mRecognitionMessageId = GUIDANCE_MESSAGE_SELECT_MODE;
                // to set guidance words before to call recognizer.
                this.mResponseWords = new String[1];
                this.mResponseWords[0] = "I'm calling the recognizer";
                return INITIALIZE_GUIDANCE;
            }
            return EMPTY;
        }
        // when already called the recognizer, to go to below process
        if (1 <= call) {
            // if already called recognizer and get the element, to update guidance
            // Mode: to guide words that about button's position
            if ((mSwitchElement & 0x01) == 0x01 && id == RECOGNITION_ID_EMPTY &&
                    (mSwitchElement & 0x02) != 0x02 && (mSwitchElement & 0x04) != 0x04) {
                // already get process
                mSwitchElement |= 0x02;
                String guidance0[] = {
                        "When you open the task table and then",
                        "if indicating button image is on top",
                        "you can choose the either mode",
                        "sound or sentence as pressed twice quickly"
                };
                this.mResponseWords = new String[guidance0.length];
                System.arraycopy(guidance0, 0, this.mResponseWords, 0, guidance0.length);
                return INITIALIZE_GUIDANCE;
                // if recognizer recognizes user's words by correct words in the pool, id will be 4.
            } else if ((mSwitchElement & 0x01) == 0x01 && (mSwitchElement & 0x04) != 0x04) {
                boolean validate = (super.ValidateSelectionByRecognisedId(GUIDANCE_MESSAGE_SELECT_MODE) ||
                        buttonType == BUTTON_SOUND || buttonType == BUTTON_SENTENCE);
                if (validate) {
                    String guidance1[] = {
                            "Ok, you selected the",
                            "Next, select music in follow tunes",
                    };
                    mSwitchElement |= 0x04;
                    mSwitchElement |= TO_CALL_RECOGNIZER;
                    // to set guidance message to the recognizer
                    this.mRecognitionMessageId = GUIDANCE_MESSAGE_SELECT_TUNE_AFTER_LISTEN_TO_TUNE;
                    // to set guidance words before to call recognizer.
                    this.mResponseWords = new String[guidance1.length];
                    guidance1[0] += Insert.insertWordOfMode();
                    System.arraycopy(guidance1, 0, this.mResponseWords, 0, guidance1.length);

                    // change task table
                    RecognitionButtonManager.reInitializeTaskTable(RecognitionButtonManager.REINITIALIZE_TASK.MUSIC_LIST_IN_TUTORIAL);

                    if (mSelectedMode == BUTTON_EMPTY) mSelectedMode = BUTTON_SELECT_TUNE;
                    return INITIALIZE_GUIDANCE;
                }
            } else if ((mSwitchElement & 0x04) == 0x04) {
                int musicNum = MusicSelector.GetCurrentElement();
                if (buttonType == BUTTON_TUNE_NUMBER && musicNum+1 != this.mPreviewElementToGuide) {
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
                    System.arraycopy(info, 1, guidance, 1, info.length-1);
                    this.mResponseWords = new String[guidance.length];
                    System.arraycopy(guidance, 0, this.mResponseWords, 0, guidance.length);
                    // to set the preview music number
                    this.mPreviewElementToGuide = musicNum;
                    return INITIALIZE_GUIDANCE;
                }
                boolean validate = super.ValidateSelectionByRecognisedId(GUIDANCE_MESSAGE_SELECT_TUNE);
                if (!validate) {
                    // when not to select the element to play music, to recall recognizer to select the element
                    if (this.mUtility.ToMakeTheInterval(1000)) {
                        mSwitchElement |= TO_CALL_RECOGNIZER;
                        // to set guidance message to the recognizer
                        this.mRecognitionMessageId = GUIDANCE_MESSAGE_SELECT_TUNE;
                    }
                    return EMPTY;
                } else {
                    mSwitchElement |= UP_TO_FINISH;
                    String guidance2[] = {
                            "OK, you selected Music number ",
                            "",
                            "",
                            "",
                            "You will be able to listen to the music in the Play scene",
                            "Next scene is to explain how to play in the Play scene"
                    };
                    String info[] = Insert.insertMusicInfo(Insert.MUSIC_RECOGNIZED);
                    guidance2[0] += info[0];
                    System.arraycopy(info, 1, guidance2, 1, info.length-1);
                    this.mResponseWords = new String[guidance2.length];
                    System.arraycopy(guidance2, 0, this.mResponseWords, 0, guidance2.length);
                    this.mNextTransitionScene = SCENE_PROLOGUE;
                    return INITIALIZE_GUIDANCE;
                }
            }
        }
        return EMPTY;
    }
}
