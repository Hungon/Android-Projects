package com.trials.harmony;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;

import org.jetbrains.annotations.Contract;

/**
 * Created by USER on 3/30/2016.
 */
public class LeadingWithVoice {
    // static variables
    // filed
    private Talk                mTalk;     // to make the texts
    // showing text
    private String[] mShowingText;
    private Point mTextPos;
    private int mTextSize;
    private int mTextColour;
    private int mTextFrame;
    private int mCurrentFrameType;

    /*
        Constructor
    */
    public LeadingWithVoice(Context context, Image image) {
        // to allot memory
        this.mTalk = new Talk(context,image);
        this.mTextPos = new Point();
        this.mTextFrame = 0;
    }
    /*
        Initialize the process that text to speech.
        to set the texts based on current scene number.
        each value is variable
    */
    public void InitLeadingWithVoiceFromLocalFile(
            String talkFile, Point pos,
            int frame, int size,
            int colour, int balloonType) {
        this.mTextPos = pos;
        this.mTextSize = size;
        this.mTextFrame = frame;
        this.mTextColour = colour;
        // to create the talk file
        try {
            // to set the blank time
            this.mTalk.CreateTalkFromLocalFile(talkFile, pos, frame,60,this.mCurrentFrameType);
        } catch (Exception e) {
            MainView.Toast(e.getMessage());
        }
        this.mCurrentFrameType = balloonType;
        this.mTalk.SetFontSizeAsDefault(size);
        this.mTalk.SetFontColourAsDefault(colour);
    }
    public void InitLeadingWithVoiceFromLocalFile(String talkFile) {
        // to create the talk file
        try {
            // to set the blank time
            this.mTalk.CreateTalkFromLocalFile(talkFile, this.mTextPos, this.mTextFrame,60,this.mCurrentFrameType);
        } catch (Exception e) {
            MainView.Toast(e.getMessage());
        }
        this.mTalk.SetFontSizeAsDefault(this.mTextSize);
        this.mTalk.SetFontColourAsDefault(this.mTextColour);
    }
    public void InitLeadingWithVoiceByRawText(
            String sentence, Point pos,
            int frame, int size,
            int colour, int balloonType) {
        this.mTextPos = pos;
        this.mTextSize = size;
        this.mTextFrame = frame;
        this.mTextColour = colour;
        // to create the talk file
        // to set the blank time
        this.mCurrentFrameType = balloonType;
        this.mTalk.CreateTalkByRawText(sentence, pos, frame,60,this.mCurrentFrameType);
        this.mTalk.SetFontSizeAsDefault(size);
        this.mTalk.SetFontColourAsDefault(colour);
    }
    public void InitLeadingWithVoiceByRawText(String sentence) {
        // to create the talk file
        // to set the blank time
        this.mTalk.CreateTalkByRawText(sentence, this.mTextPos, this.mTextFrame,60,this.mCurrentFrameType);
        this.mTalk.SetFontSizeAsDefault(this.mTextSize);
        this.mTalk.SetFontColourAsDefault(this.mTextColour);
    }
    /*
        Update the process that text to speech
        return value is process.
    */
    public int UpdateLeadingWithVoice() {
        // to update talking
        int process = this.mTalk.UpdateTalk();
        // to get showing text to initialize guidance words
        // when the current process is either each below status.
        if (process == Talk.FINISH_TO_READ || process == Talk.DELETE || process == Talk.PAUSE) {
            // to set showing text to guide
            if (this.mShowingText != null) this.mShowingText = null;
            this.mShowingText = new String[this.mTalk.GetShowingText().length];
            this.mShowingText = this.mTalk.GetShowingText();
        }
        return process;
    }
    /*
        Draw the texts that leading with voice.
    */
    public void DrawLeadingWithVoice() {
        this.mTalk.DrawTalk(2,0);
    }
    /*
        Release the process
    */
    public void ReleaseLeadingWithVoice() {
        this.mTalk.ReleaseTalk();
        this.mTalk = null;
        // Showing text
        this.mShowingText = null;
    }
    /*
        go to new line in the text
    */
    public void GoToNewLine() {
        this.mTalk.RestartReading();
    }
    /*********************************************************
     Each setter function
     *******************************************************/
    // set existence of balloon
    public void SwitchShowingWindowMessage(boolean exist) { this.mTalk.SwitchShowingWindowMessage(exist); }
    /*********************************************************
     Each getter function
     *******************************************************/
    /*
        Get Showing text
    */
    public String[] GetShowingText() {
        String res[] = new String[1];
        return (this.mShowingText == null) ? res : this.mShowingText;
    }
    public boolean getIsDisplaying() { return this.mTalk.getIsDisplaying(); }
}