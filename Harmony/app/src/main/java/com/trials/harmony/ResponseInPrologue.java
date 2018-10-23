package com.trials.harmony;

/**
 * Created by Kohei Moroi on 7/24/2016.
 */
public class ResponseInPrologue extends Response implements HasButtons {
    // the current mode
    private int mCurrentMode;
    private int mFixedExperience;
    // Constructor
    public ResponseInPrologue(int mode) {
        this.mCurrentMode = mode;
        int preview = SceneManager.GetPreviewScene();
        // when preview scene is tutorial,
        // to explain
        if (preview == SCENE_TUTORIAL) {
            this.mUserExperience &= ~EXPERIENCE_DONE_PROLOGUE_SOUND_MODE;
            this.mUserExperience &= ~EXPERIENCE_DONE_PROLOGUE_SENTENCE_MODE;
            this.mUserExperience &= ~EXPERIENCE_DONE_PROLOGUE_ASSOCIATION_MODE;
        }
        this.mFixedExperience = Insert.insertFixedExperience(this.mCurrentMode,SCENE_PROLOGUE);
    }
    /*
        Update
    */
    @Override
    protected int UpdateResponse() {
        boolean guiding = GuidanceManager.GetIsGuiding();
        if ((this.mSwitchElement & UP_TO_FINISH) != UP_TO_FINISH) {
            // to diverge the explanation from the current mode
            if ((this.mUserExperience & this.mFixedExperience) != this.mFixedExperience) {
                return this.updateExplanation(this.mCurrentMode);
            // when the user has never experienced the current mode, guide will ask the user to check again
            } else if ((this.mUserExperience & this.mFixedExperience) == this.mFixedExperience) {
                return super.ToAskCheckingAgain(SCENE_PLAY,this.mFixedExperience);
            }
        } else if (!guiding && (this.mSwitchElement & UP_TO_FINISH) == UP_TO_FINISH) {
            if (this.mUtility.ToMakeTheInterval(100)) {
                // Set next scene
                SceneManager.SetNextScene(this.mNextTransitionScene);
                Wipe.SetAvailableToCreate(true);
                return TRANSITION_TO_NEXT_SCENE;
            }
        }
        return EMPTY;
    }
    /*
        To explain the sound mode
    */
    private int updateSoundMode(int id, int call, boolean guiding) {
        // when talk class detected 'p' of a special word,
        // to resonate each colour's sound in prologue scene after the guidance.
        if (!guiding && LeadingManager.GetPauseCountInATextFile() == 1 &&
                (this.mSwitchElement&0x01)!=0x01) {
            // to play each sound after fixed interval that about 2 seconds.
            if (this.mUtility.ToMakeTheInterval(200)) {
                this.mSwitchElement |= 0x01;
                // set available to play every sound of colour.
                ColourManager.setAvailableToUpdate();
            }
            return EMPTY;
            // when the current type which is playing became empty,
            // to restart talking process
        } else if (!guiding && ColourManager.getCurrentButtonType()[0] == BUTTON_EMPTY &&
                (this.mSwitchElement&0x01)==0x01 && (this.mSwitchElement&0x02)!=0x02) {
            this.mSwitchElement |= 0x02;
            LeadingManager.GoToNewLine();
            return EMPTY;
            // when already read first text file, to call recognizer to ask user
            // about listening again each sound.
        } else if (!guiding && LeadingManager.GetPauseCountInATextFile() == 2 &&
                (this.mSwitchElement&0x02)==0x02 && (this.mSwitchElement&0x04)!=0x04 &&
                !ColourManager.getAvailableToPlay()) {
            this.mSwitchElement |= TO_CALL_RECOGNIZER;
            this.mSwitchElement |= 0x04;
            String guidance[] = {"Do you want to listen to each sound again?"};
            // to set guidance words before to call recognizer.
            this.mResponseWords = new String[guidance.length];
            System.arraycopy(guidance, 0, this.mResponseWords, 0, guidance.length);
            // to set guidance message to the recognizer
            this.mRecognitionMessageId = GUIDANCE_MESSAGE_ANSWER;
            return INITIALIZE_GUIDANCE;
            // to repeat after the fixed interval.
        } else if (!guiding && (this.mSwitchElement&0x04)==0x04 && id == RECOGNITION_ID_EMPTY) {
            if (this.mUtility.ToMakeTheInterval(300)) {
                this.mSwitchElement &= ~0x04;
            }
            return EMPTY;
            // when user is want to do that, to play each sound again.
        } else if (!guiding && 1 <= call && (this.mSwitchElement&0x04)==0x04 &&
                (this.mSwitchElement&0x08)!=0x08 && id == RECOGNITION_ID_YES) {
            this.mSwitchElement |= 0x08;
            String guidance[] = {
                    "Sure, to listen to each sound",
            };
            // to set guidance words before to call recognizer.
            this.mResponseWords = new String[guidance.length];
            System.arraycopy(guidance, 0, this.mResponseWords, 0, guidance.length);
            return INITIALIZE_GUIDANCE;
            // to restart playing sound
        } else if (!guiding && (this.mSwitchElement&0x08)==0x08) {
            // reset colours' sound after fixed interval
            if (this.mUtility.ToMakeTheInterval(200)) {
                this.mSwitchElement &= ~0x04;
                this.mSwitchElement &= ~0x08;
                ColourManager.setAvailableToUpdate();
                // reset id recognized
                RecognizerManager.ResetIdRecognized();
            }
            return EMPTY;
            // The process will continue up to the response that answer is no.
            // when answer is no, to next sentence
        } else if (!guiding && 1 <= call && (this.mSwitchElement&0x10)!=0x10 &&
                id == RECOGNITION_ID_NO) {
            LeadingManager.GoToNewLine();
            this.mSwitchElement |= 0x10;
            return EMPTY;
            // when reached to last word in the text file, to transition to play scene after interval
        } else if (!guiding && LeadingManager.GetCountReadFile() == 1 &&
                (this.mSwitchElement&0x10)==0x10) {
            this.mSwitchElement |= UP_TO_FINISH;
            this.mNextTransitionScene = SCENE_PLAY;
            return EMPTY;
        }
        return EMPTY;
    }
    private int updateSentenceMode(int id, int call, boolean guiding) {
        if ((this.mSwitchElement & 0x01) != 0x01) {
            // set available to update
            SentenceManager.setAvailableToUpdate();
            this.mRecognitionMessageId = GUIDANCE_MESSAGE_SENTENCE;
            this.mSwitchElement |= 0x01;
            // when talk class detected 'p' of a special word,
            // to wait for get recognition id.
        } else if (!guiding && LeadingManager.GetPauseCountInATextFile() == 1 &&
                (this.mSwitchElement & 0x01) == 0x01 && (this.mSwitchElement & 0x02) != 0x02) {
            if (this.mUtility.ToMakeTheInterval(100)) {
                this.mResponseWords = new String[1];
                this.mResponseWords[0] = "I'm calling the recognizer";
                this.mSwitchElement |= TO_CALL_RECOGNIZER;
                this.mSwitchElement |= 0x02;
                RecognitionMode.ResetCountCalledRecognizer();           // reset count called the recognizer
                return INITIALIZE_GUIDANCE;
            }
        } else if (!guiding && (this.mSwitchElement & 0x02) == 0x02 &&
                0 < call && (this.mSwitchElement & 0x04) != 0x04) {
            // to load next sentence
            LeadingManager.GoToNewLine();
            this.mSwitchElement |= 0x04;
            if (super.ValidateSelectionByRecognisedId(GUIDANCE_MESSAGE_SENTENCE)) {
                this.mResponseWords = new String[1];
                this.mResponseWords[0] = "That's right";
                return INITIALIZE_GUIDANCE;
            }
            return EMPTY;
        } else if (!guiding && LeadingManager.GetCountReadFile() == 1 &&
                (this.mSwitchElement & 0x04) == 0x04 && (this.mSwitchElement & 0x08) != 0x08) {
            this.mSwitchElement |= TO_CALL_RECOGNIZER;
            this.mSwitchElement |= 0x08;
            String guidance[] = {"Do you want to check again?"};
            // to set guidance words before to call recognizer.
            this.mResponseWords = new String[guidance.length];
            System.arraycopy(guidance, 0, this.mResponseWords, 0, guidance.length);
            // to set guidance message to the recognizer
            this.mRecognitionMessageId = GUIDANCE_MESSAGE_ANSWER;
            return INITIALIZE_GUIDANCE;
        } else if (!guiding && (this.mSwitchElement & 0x08) == 0x08 &&
                id == RECOGNITION_ID_YES && (this.mSwitchElement & 0x10) != 0x10) {
            this.mSwitchElement |= 0x10;
            String guidance[] = {
                    "Sure, I shall explain again",
            };
            // to set guidance words before to call recognizer.
            this.mResponseWords = new String[guidance.length];
            System.arraycopy(guidance, 0, this.mResponseWords, 0, guidance.length);
            return INITIALIZE_GUIDANCE;
        } else if (!guiding && (this.mSwitchElement & 0x10) == 0x10) {
            if (this.mUtility.ToMakeTheInterval(100)) {
                this.mSwitchElement = 0;
                SentenceManager.setAvailableToUpdate();
                LeadingManager.loadTheFileToExplainAgain(); // reset count that finished to read
                RecognizerManager.ResetIdRecognized();      // reset id that recognized
            }
            return EMPTY;
        } else if (!guiding && (this.mSwitchElement & 0x08) == 0x08 &&
                id == RECOGNITION_ID_NO) {
            this.mSwitchElement |= UP_TO_FINISH;
            this.mNextTransitionScene = SCENE_PLAY;
            return EMPTY;
        }
        return EMPTY;
    }
    private int updateAssociation(boolean guiding) {
        if ((this.mSwitchElement & 0x01) != 0x01) {
            // set available to update
            SentenceManager.setAvailableToUpdate();
            if (this.mCurrentMode == MODE_ASSOCIATION_IN_EMOTIONS) {
                this.mRecognitionMessageId = GUIDANCE_MESSAGE_ASSOCIATION_IN_EMOTIONS;
            } else if (this.mCurrentMode == MODE_ASSOCIATION_IN_FRUITS) {
                this.mRecognitionMessageId = GUIDANCE_MESSAGE_ASSOCIATION_IN_FRUITS;
            } else if (this.mCurrentMode == MODE_ASSOCIATION_IN_ALL) {
                this.mRecognitionMessageId = GUIDANCE_MESSAGE_ASSOCIATION_IN_ALL;
            }
            this.mSwitchElement |= 0x01;
        } else if (!guiding && LeadingManager.GetCountReadFile() == 1 &&
                (this.mSwitchElement & 0x01) == 0x01) {
            if (RecognitionButtonManager.GetButtonTypeToObtainProcess() == HasButtons.BUTTON_PLAY_THE_GAME) {
                this.mSwitchElement |= UP_TO_FINISH;
                this.mNextTransitionScene = SCENE_PLAY;
                return EMPTY;
            }
        }
        return EMPTY;
    }
    private int updateExplanation(int mode) {
        int id = RecognizerManager.GetRecognitionId()[0];
        boolean guiding = GuidanceManager.GetIsGuiding();
        int call = RecognitionMode.GetCurrentCountCalledRecognizer();
        if (this.mResponseWords != null) this.mResponseWords = null;
        if (mode == MODE_SOUND) {
            return this.updateSoundMode(id,call,guiding);
        } else if (mode == MODE_SENTENCE) {
            return this.updateSentenceMode(id,call,guiding);
        } else {
            updateAssociation(guiding);
        }
        return EMPTY;
    }
}
