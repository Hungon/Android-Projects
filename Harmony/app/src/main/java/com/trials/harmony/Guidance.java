package com.trials.harmony;

import android.content.Context;

/**
 * Created by USER on 4/14/2016.
 */
public class Guidance {
    // static variables
    // filed
    private boolean                m_AvailableToPlay;
    private  MyTextToSpeech        m_Speech;

    /*
        Constructor
    */
    public Guidance(Context context) {
        // allot the memory
        this.m_Speech = new MyTextToSpeech(context);
        this.m_AvailableToPlay = false;
    }
    /*
        Initialize the response
    */
    public void InitGuidance(String words) {
        if (!this.m_AvailableToPlay){
            this.m_AvailableToPlay = true;
            this.m_Speech.InitTextToSpeech(words);
        }
    }
    /*
        Update the process that to response to recognized words.
    */
    public void UpdateGuidance() {
        if (this.m_AvailableToPlay) {
            // to response the word
            this.m_Speech.StartTextToSpeech();
            this.m_AvailableToPlay = false;
        }
    }
    /*
        Release
    */
    public void ReleaseGuidance() {
        if (this.m_Speech != null) {
            this.m_Speech.ReleaseTextToSpeech();
            this.m_Speech = null;
        }
    }
    /*
        To stop process
    */
    public void StopGuidance() {
        if (this.m_Speech != null) {
            this.m_Speech.StopTextToSpeech();
        }
    }
    /**********************************************
     Each setter functions
     ********************************************/
    /**********************************************
     Each getter functions
     ********************************************/
    /*
        To get the flag that is speaking the text.
    */
    public boolean GetSpeaking() { return this.m_Speech.GetSpeaking(); }
}