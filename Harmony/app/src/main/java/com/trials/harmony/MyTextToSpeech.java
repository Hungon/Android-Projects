package com.trials.harmony;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Created by USER on 4/14/2016.
 */
public class MyTextToSpeech implements TextToSpeech.OnInitListener {

    // filed
    private final static float      SPEECH_DEFAULT_RATE     = 0.9f;
    private final static float      SPEECH_DEFAULT_PITCH    = 1.0f;
    // for text to speech
    private TextToSpeech    m_TextToSpeech;
    private boolean         m_AvailableSpeaking;
    private String          m_TextToSpeechWords;
    private float           m_SpeechRate        = SPEECH_DEFAULT_RATE;
    private float           m_Pitch             = SPEECH_DEFAULT_PITCH;

    /*
        Constructor
    */
    public MyTextToSpeech(Context context) {
        // to create the instance of text to speech
        m_TextToSpeech = new TextToSpeech(context,this);
    }
    /**********************************************************
     Each methods for text to speech
     ********************************************************/
    /*
        Is called as to finish to prepare
        the initialization of process that text to speech.
    */
    @Override
    public void onInit(int status) {
        try {
            if (m_TextToSpeech != null) {
                if (status == TextToSpeech.SUCCESS) {
                    // to decide locale
                    Locale locale = Locale.ENGLISH;
                    if (TextToSpeech.LANG_AVAILABLE <= m_TextToSpeech.isLanguageAvailable(locale)) {
                        m_TextToSpeech.setLanguage(locale);
                    } else {
                        MainView.Toast("Unsupported language.");
                    }
                } else if (status == TextToSpeech.ERROR) {
                    MainView.Toast("TextToSpeech.ERROR.");
                }
            }
        } catch (NullPointerException e) {
            MainView.Toast(e.getMessage());
        }
    }
    /*
        Initialize the text to speak
    */
    public void InitTextToSpeech(String text, float rate, float pitch) {
        m_SpeechRate = rate;
        m_Pitch = pitch;
        m_TextToSpeechWords = text;
        m_AvailableSpeaking = true;
    }
    /*
        Initialize the text to speak
    */
    public void InitTextToSpeech(String text) {
        m_SpeechRate = SPEECH_DEFAULT_RATE;
        m_Pitch = SPEECH_DEFAULT_PITCH;
        m_TextToSpeechWords = text;
        m_AvailableSpeaking = true;
    }

    /*
        Start process that text to speech
    */
    public void StartTextToSpeech() {
        // to check the error that TextToSpeech is null or not.
        if (m_TextToSpeech == null) return;
        // when recognizer exists or executing the process that text to speech,
        // to stop the speech
        if (!m_AvailableSpeaking) return;
        try {
            // to play the speech
            if (m_TextToSpeechWords != null) {
                // not to execute the process tha text to speech.
                m_AvailableSpeaking = false;
                m_TextToSpeech.setPitch(m_Pitch);
                m_TextToSpeech.setSpeechRate(m_SpeechRate);
                m_TextToSpeech.speak(m_TextToSpeechWords, TextToSpeech.QUEUE_FLUSH, null);
            }
        } catch(NullPointerException e) {
            MainView.Toast(e.getMessage());
        }
    }
    /*
        Release
    */
    public void ReleaseTextToSpeech() {
        m_TextToSpeechWords = "";
        try {
            if (m_TextToSpeech != null) {
                m_TextToSpeech.shutdown();
                m_TextToSpeech = null;
            }
        } catch (NullPointerException e) {
            MainView.Toast(e.getMessage());
        }
    }
    /*
        To stop the process that text to speech
    */
    public void StopTextToSpeech() {
        try {
            if (m_TextToSpeech != null) {
                m_TextToSpeech.stop();
            }
        } catch (NullPointerException e) {
            MainView.Toast(e.getMessage());
        }
    }
    /*
        To get the flag that speaking
    */
    public boolean GetSpeaking() {
        try {
            if (m_TextToSpeech != null) {
                return this.m_TextToSpeech.isSpeaking();
            }
        } catch (NullPointerException e) {
            MainView.Toast(e.getMessage());
        }
        return false;
    }
}