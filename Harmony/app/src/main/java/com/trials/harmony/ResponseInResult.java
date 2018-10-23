package com.trials.harmony;

/**
 * Created by Kohei Moroi on 7/24/2016.
 */
public class ResponseInResult extends Response {
    // Constructor
    public ResponseInResult() {}
    /*
        Update
    */
    @Override
    protected int UpdateResponse() {
        boolean guiding = GuidanceManager.GetIsGuiding();
        int id = RecognizerManager.GetRecognitionId()[0];
        if ((mSwitchElement&UP_TO_FINISH) != UP_TO_FINISH) {
            if (this.mResponseWords != null) this.mResponseWords = null;
            // when talk class reached to last word which is the special of a word 'd',
            // to get the result in play scene.
            if (!guiding && (mSwitchElement&0x01)!=0x01) {
                mSwitchElement |= 0x01;
                // to set the words to guide
                this.mResponseWords = new String[GuidanceManager.GUIDANCE_SENTENCE_FOR_SCORE.length];
                System.arraycopy(GuidanceManager.GUIDANCE_SENTENCE_FOR_SCORE, 0, this.mResponseWords, 0, GuidanceManager.GUIDANCE_SENTENCE_FOR_SCORE.length);
                // to set each score to guidance words
                int eachScore[] = ScoreManager.GetEachScore();
                for (int i = 0; i < this.mResponseWords.length; i++) {
                    this.mResponseWords[i] += Integer.toString(eachScore[i]);
                }
                return INITIALIZE_GUIDANCE;
            } else if (!guiding && (mSwitchElement&0x01)==0x01 &&
                    (mSwitchElement&0x02)!=0x02) {
                mSwitchElement |= 0x02;
                mSwitchElement |= TO_CALL_RECOGNIZER;
                String guidance[] = {
                        "Do you want to check the records again?"
                };
                // to set guidance message to the recognizer
                this.mRecognitionMessageId = GUIDANCE_MESSAGE_ANSWER;
                // to set the words to guide
                this.mResponseWords = new String[guidance.length];
                System.arraycopy(guidance, 0, this.mResponseWords, 0, guidance.length);
                return INITIALIZE_GUIDANCE;
                // to asking about to repeat guidance
            } else if (!guiding && (mSwitchElement&0x02)==0x02 && id == RECOGNITION_ID_EMPTY) {
                if (this.mUtility.ToMakeTheInterval(FIXED_TIME_TO_REPEAT)) {
                    mSwitchElement &= ~0x02;
                }
                return EMPTY;
                // when answer is yes, to say the best record again.
            } else if (!guiding && 1 <= RecognitionMode.GetCurrentCountCalledRecognizer() &&
                    (mSwitchElement&0x02)==0x02 && id == RECOGNITION_ID_YES) {
                mSwitchElement &= ~0x01;
                mSwitchElement &= ~0x02;
                String guidance[] = {
                        "Sure, to listen each record",
                };
                // to set guidance message to the recognizer
                this.mRecognitionMessageId = GUIDANCE_MESSAGE_ANSWER;
                // to set guidance words before to call recognizer.
                this.mResponseWords = new String[guidance.length];
                System.arraycopy(guidance, 0, this.mResponseWords, 0, guidance.length);
                return INITIALIZE_GUIDANCE;
                // when answer is no, to transition to opening scene.
            } else if (!guiding && 1 <= RecognitionMode.GetCurrentCountCalledRecognizer() &&
                    (mSwitchElement&0x02)==0x02 && id == RECOGNITION_ID_NO) {
                mSwitchElement |= UP_TO_FINISH;
                String guidance[] = {
                        "OK, to transition to main menu scene",
                };
                // to set guidance words before to call recognizer.
                this.mResponseWords = new String[guidance.length];
                System.arraycopy(guidance, 0, this.mResponseWords, 0, guidance.length);
                return INITIALIZE_GUIDANCE;
            }
        } else if ((mSwitchElement&UP_TO_FINISH)==UP_TO_FINISH) {
            // when already finished to guide, to transition to main menu after counted interval.
            if (!guiding) {
                if (this.mUtility.ToMakeTheInterval(200)) {
                    Wipe.SetAvailableToCreate(true);
                    SceneManager.SetNextScene(SceneManager.SCENE_MAIN_MENU);
                    return TRANSITION_TO_NEXT_SCENE;
                }
            }
        }
        return EMPTY;
    }
}
