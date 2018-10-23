package com.trials.supertriathlon;

import android.content.Context;

/**
 * Created by USER on 3/7/2016.
 */
public class RaceScoreManager {
    // static variables
    // filed
    private RaceScore   mRaceScore;
    private int         mCurrentStage;
    private int         mScoreType;

    /*
        Constructor
    */
    public RaceScoreManager(Context context, Image image) {
        this.mRaceScore = new RaceScore(context, image);
    }
    /*
        Initialize
    */
    public void InitRaceScoreManager() {
        // get current stage number
        this.mCurrentStage = Play.GetCurrentStageNumber();
        this.mRaceScore.InitRaceScore();
        this.mScoreType = -1;
    }
    /*
        Update
    */
    public void UpdateRaceScoreManager() {
        int chain;
        boolean update = false;
        // diverge update from current stage number
        switch(this.mCurrentStage) {
            case Play.STAGE_OFF_ROAD:
                // get chain value
                chain = ActionCommand.GetConsecutiveCount();
                this.mScoreType = ActionCommand.GetSuccessCount();
                update = OffroadPlayer.GetShowAction();
                this.mRaceScore.UpdateRaceScore(chain,this.mScoreType,update);
                break;
            case Play.STAGE_ROAD:
                chain = RoadPlayer.GetChain();
                // add chain bonus to total point
                if (this.mRaceScore.GetIndicateChain() < chain) {
                    RaceScore.UpdateTotalPoint(50*chain);
                }
                for (int score: RaceScore.SCORE_EVALUATION_TYPE_BOX) {
                    // to show value image and that value bonus point to total point.
                    if (score*3 == chain && this.mScoreType != score) {
                        update = true;
                        this.mScoreType = score;
                    } 
                }
                // update score
                this.mRaceScore.UpdateRaceScore(chain,this.mScoreType,update);
                break;
            case Play.STAGE_SEA:
                chain = SeaPlayer.GetChain();
                // add chain bonus to total point
                if (this.mRaceScore.GetIndicateChain() < chain) {
                    RaceScore.UpdateTotalPoint(50*chain);
                }
                for (int score: RaceScore.SCORE_EVALUATION_TYPE_BOX) {
                    // to show value image and that value bonus point to total point.
                    if (score*5 == chain && this.mScoreType != score) {
                        update = true;
                        this.mScoreType = score;
                    }
                }
                // update score
                this.mRaceScore.UpdateRaceScore(chain,this.mScoreType,update);
                break;
        }
    }
    /*
        Draw
    */
    public void DrawRaceScoreManager() {
        this.mRaceScore.DrawRaceScore();
    }
    /*
        Release
    */
    public void ReleaseRaceScoreManager() {
        this.mRaceScore.ReleaseRaceScore();
        this.mRaceScore = null;
    }
}