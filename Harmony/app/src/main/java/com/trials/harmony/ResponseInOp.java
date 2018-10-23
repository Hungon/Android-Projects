package com.trials.harmony;

/**
 * Created by Kohei Moroi on 7/24/2016.
 */
public class ResponseInOp extends Response implements HasSystem, HasRecognitionWords, FileManager {
    // Constructor
    public ResponseInOp() {}
    /*
        Update
    */
    @Override
    protected int UpdateResponse() {
        // To diverge the update from user's experience.
        if ((this.mUserExperience&EXPERIENCED_FIRST_IMPRESSION_IN_OPENING)==EXPERIENCED_FIRST_IMPRESSION_IN_OPENING) return EMPTY;
        // to execute during not to substitute Up_To_Finish into mSwitchElement
        if ((mSwitchElement & UP_TO_FINISH) != UP_TO_FINISH) {
            int callCount = RecognitionMode.GetCurrentCountCalledRecognizer();
            // to get id recognized
            int idRecognized = RecognizerManager.GetRecognitionId()[0];
            if (this.mResponseWords != null) this.mResponseWords = null;
            // when the current guidance ends up to the last element, to call the recognizer.
            if (0 == callCount) {
                if (LeadingManager.GetCountReadFile() == 1 && !GuidanceManager.GetIsGuiding() &&
                    (mSwitchElement & 0x01) != 0x01) {
                    mSwitchElement |= TO_CALL_RECOGNIZER;
                    mSwitchElement |= 0x01;
                    // to set guidance message to the recognizer
                    this.mRecognitionMessageId = GUIDANCE_MESSAGE_TRANSITION_IN_PRACTICE;
                    // to set guidance words before to call recognizer.
                    this.mResponseWords = new String[1];
                    this.mResponseWords[0] = "I'm calling the recognizer";
                    return INITIALIZE_GUIDANCE;
                }
            } else {
                // if already called recognizer and get the element, to update guidance
                // Mode: to guide words that about button's position
                if (idRecognized != RECOGNITION_ID_EMPTY && (mSwitchElement & 0x01) == 0x01) {
                    mSwitchElement |= UP_TO_FINISH;
                    // to go to new file
                    LeadingManager.setFileIndex(FILE_INDEX_RECOGNIZED_IN_OP);
                    return EMPTY;
                } else if (idRecognized == RECOGNITION_ID_EMPTY && (mSwitchElement & 0x01) == 0x01) {
                    mSwitchElement |= UP_TO_FINISH;
                    // to go to new file
                    LeadingManager.setFileIndex(FILE_INDEX_DID_NOT_RECOGNIZED_IN_OP);
                    return EMPTY;
                }
            }
            // last execution hence above processes don't work.
        } else if ((mSwitchElement & UP_TO_FINISH) == UP_TO_FINISH) {
            // to switch the guidance in recognition mode
            RecognizerManager.SetGuidanceId(GUIDANCE_MESSAGE_TRANSITION);
            return EMPTY;       // is waiting for user touches the button
        }
        return EMPTY;
    }
}