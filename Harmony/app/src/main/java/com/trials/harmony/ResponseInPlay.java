package com.trials.harmony;

/**
 * Created by Kohei Moroi on 7/24/2016.
 */
public class ResponseInPlay extends Response implements HasButtons {
    // the max id to create colour or sentence
    private int mRecognitionIdMax;

    // Constructor
    public ResponseInPlay(int idMax) {
        this.mRecognitionIdMax = idMax;
    }
    /*
        Update
    */
    @Override
    protected int UpdateResponse() {
        // when current scene is play
        // to play the BGM
        int count = RecognitionMode.GetCurrentCountCalledRecognizer();
        int id = RecognizerManager.GetGuidanceId();
        // from recognizer
        if (this.validateId(id)) {
            if (this.mPreviewCountCalledRecognizer < count) {
                // to set guidance
                RecognizerManager.SetGuidanceId(GUIDANCE_MESSAGE_TRANSITION);
                if (this.mResponseWords != null) this.mResponseWords = null;
                this.mPreviewCountCalledRecognizer = count;
                String res[] = new String[1];
                // to get the correct count
                int correct = RecognizerManager.GetCurrentCorrectCount();
                // to loop to id max that available to recognize
                if (correct == 0) {
                    res[0] = "next!";
                } else if (0 < correct && correct < this.mRecognitionIdMax) {
                    String word = "";
                    if (correct == 1) {
                        word = " match!";
                    } else if (0 < correct) {
                        word = " matches!";
                    }
                    res[0] = Integer.toString(correct) + word;
                } else if (this.mRecognitionIdMax == correct) {
                    res[0] = "perfect!";
                }
                this.mResponseWords = new String[res.length];
                System.arraycopy(res, 0, this.mResponseWords, 0, res.length);
                return INITIALIZE_GUIDANCE;
            }
        } else if (RecognitionButtonManager.GetIsOpeningTask()) {
            int buttonType = RecognitionButtonManager.GetButtonTypeToObtainProcess();
            if ((this.mSwitchElement&0x01)!=0x01) {
                this.mSwitchElement |= 0x01;
                if (this.mResponseWords != null) this.mResponseWords = null;
                String guidance[] = {
                        "total points is ",
                        ""
                };
                guidance[0] += ScoreManager.getAggregatePoints();
                int time[] = Time.getTime();
                String timeWords[] = {" minute "," second "," millisecond "};
                for (int i = 0; i < time.length; i++) {
                    if (0 < time[i]) {
                        if (time[i] == 1) {
                            guidance[1] += time[i]+timeWords[i];
                        } else {
                            guidance[1] += time[i]+timeWords[i]+"s";
                        }
                    }
                }
                if (!guidance[1].equals("")) guidance[1] += "remaining";
                this.mResponseWords = new String[guidance.length];
                System.arraycopy(guidance, 0, this.mResponseWords, 0, guidance.length);
                return INITIALIZE_GUIDANCE;
            }
            if (buttonType == BUTTON_RECOGNITION) {
                if (this.mPreviewCountCalledRecognizer < count) {
                    this.mPreviewCountCalledRecognizer++;
                }
            }
        } else {
            this.mSwitchElement = 0;
        }
        return EMPTY;
    }
    private boolean validateId(int guidanceId) {
        return (guidanceId == GUIDANCE_MESSAGE_COLOUR || guidanceId == GUIDANCE_MESSAGE_SENTENCE ||
                guidanceId == GUIDANCE_MESSAGE_ASSOCIATION_IN_EMOTIONS || guidanceId == GUIDANCE_MESSAGE_ASSOCIATION_IN_FRUITS ||
                guidanceId == GUIDANCE_MESSAGE_ASSOCIATION_IN_ALL);
    }
}