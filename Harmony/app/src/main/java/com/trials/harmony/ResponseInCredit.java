package com.trials.harmony;

/**
 * Created by Kohei Moroi on 8/2/2016.
 */
public class ResponseInCredit extends Response implements HasMusicInfo {
    public ResponseInCredit() {}
    @Override
    protected int UpdateResponse() {
        boolean guiding = GuidanceManager.GetIsGuiding();
        if ((mSwitchElement & UP_TO_FINISH) != UP_TO_FINISH) {
            if (this.mResponseWords != null) this.mResponseWords = null;
            int process = CreditViewer.GetTheCurrentProcess();
            if ((this.mSwitchElement&0x01)!=0x01 && process == CreditViewer.PROCESS_GO_TO_WEB_VIEW) {
                this.mSwitchElement |= 0x01;
                this.mResponseWords = new String[1];
                this.mResponseWords[0] = "Ok, check out the contributor";
                return INITIALIZE_GUIDANCE;
            } else if ((this.mSwitchElement&0x02)!=0x02 && process == CreditViewer.PROCESS_SHOE_MY_NAME) {
                this.mSwitchElement |= 0x02;
                this.mResponseWords = new String[1];
                this.mResponseWords[0] = "Presented by Kohei Moroi";
                return INITIALIZE_GUIDANCE;
            } else if ((this.mSwitchElement&0x04)!=0x04 && process == CreditViewer.PROCESS_THE_APPRECIATION) {
                this.mSwitchElement |= 0x04;
                this.mResponseWords = new String[1];
                this.mResponseWords[0] = "Thank you";
                return INITIALIZE_GUIDANCE;
            } else if (process == CreditViewer.PROCESS_AVAILABLE_TO_TRANSITION) {
                this.mSwitchElement |= UP_TO_FINISH;
                this.mNextTransitionScene = SCENE_OPENING;
                return EMPTY;
            }
        } else if (!guiding && (mSwitchElement & UP_TO_FINISH) == UP_TO_FINISH) {
            if (this.mUtility.ToMakeTheInterval(400)) {
                SceneManager.SetNextScene(this.mNextTransitionScene);
                Wipe.SetAvailableToCreate(true);
                return TRANSITION_TO_NEXT_SCENE;
            }
        }
        return EMPTY;
    }
}