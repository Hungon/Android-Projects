package com.trials.harmony;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;

/**
 * Created by USER on 3/28/2016.
 */
public class Harmony extends Activity implements HasScene {
    // static variables
    private final static String RECOGNIZER_NAME = "Recognizer";
    private final static String RECOGNIZER_STATUS = "Was called the recognizer";
    public final static int        REQUEST_CODE = 1234;
    // the flag that progress of speech recognizer
    public final static int        RECOGNIZER_ERROR            = -1;
    public final static int        RECOGNIZER_READY_TO_LISTEN  = 0;
    public final static int        RECOGNIZER_LISTENING        = 1;
    public final static int        RECOGNIZER_STOP_SPEAK       = 2;
    public final static int        RECOGNIZER_END              = 3;
    // filed
    private static Activity         mActivity;
    // for speech recognizer
    private static int              mRecognizerProgress;
    // recognizer intent
    private static Intent           mIntent;
    // the words to recognize
    private static String           mRecognizedWords = "";
    /*****************************************
     Is called as create the activity.
     ***************************************/
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        // to prepare the process that text to speech
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mActivity = this;
        // to allot the memory to recognizer intent
        if (mIntent == null) mIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        setContentView(new MainView(this));
    }
    /**********************************************************
     the method for recognizer speech
     **********************************************************/
    /*
        Start to recognize the speech
    */
    public static void StartRecognition() {
        try {
            // to reset the recognized words
            mRecognizedWords = "";
            // to execute the process of recognition.
            // put the status to register.
            mIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            // to put the values into register
            mIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, RECOGNIZER_NAME);
            mIntent.putExtra(RECOGNIZER_NAME,true);
            mIntent.putExtra(RECOGNIZER_STATUS,true);
            mIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,600);
            // the process that recognition
            mRecognizerProgress = RECOGNIZER_LISTENING;
            // to start activity to get the results that recognized.
            mActivity.startActivityForResult(mIntent, REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            mRecognizerProgress = RECOGNIZER_ERROR;
            MainView.Toast(e.getMessage());
        }
    }
    /*
        Is called as destroyed the activity.
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // to substitute the value
        mRecognizerProgress = RECOGNIZER_STOP_SPEAK;
        try {
            // to get the result of recognition.
            if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
                ArrayList<String> results = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                float[] confidences = data.getFloatArrayExtra(
                        RecognizerIntent.EXTRA_CONFIDENCE_SCORES);
                if (0 < results.size()) {
                    for (int i = 0; i < results.size(); i++) {
                        // if confidence is more than 0.3, to get the words.
                        if (0.0f <= confidences[i]) {
                            mRecognizedWords += results.get(i) + " ";
                        }
                    }
                }
                super.onActivityResult(requestCode, resultCode, data);
                mIntent.putExtra("Recognizer",false);
            }
        } catch (NullPointerException e) {
            mRecognizerProgress = RECOGNIZER_ERROR;
            MainView.Toast(e.getMessage());
            mIntent.putExtra("Recognizer",false);
        }
    }
    /*
        To reset the words recognized
    */
    public static void ResetRecognizedWords() { mRecognizedWords = ""; }
    /*
        To reset the progress
    */
    public static void ResetProgress() { mRecognizerProgress = RECOGNIZER_READY_TO_LISTEN; }
    /*
        Create web page
    */
    public static void CreateWebPage(String url) {
        Intent intent = new Intent(mActivity, WebViewActivity.class);
        // save values
        intent.putExtra("URL",url);
        mActivity.startActivity(intent);
    }

    /**********************************************
     Each getter functions
     ********************************************/
    /*
        To get the progress of recognizer
    */
    @Contract(pure = true)
    public static int GetRecognizerProgress() { return mRecognizerProgress; }
    /*
        To get the words that recognized
    */
    @Contract(pure = true)
    public static String GetRecognizedWords() { return mRecognizedWords; }
    /********************************************
     Each setter functions
     ******************************************/
    /*
        To set the progress to variable
    */
    public static void SetRecognitionProgress(int progress) { mRecognizerProgress = progress; }
    /**********************************************************
     Is called as stopped the activity.
     **********************************************************/
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onStop() {
        super.onStop();
        Sound.StopBGMTemporary();
    }
    @Override
    public void onStart() {
        super.onStart();
    }
    @Override
    public void onResume() {
        super.onResume();
        Sound.RestartBGM();
        int scene = SceneManager.GetCurrentScene();
        int type = -1;
        boolean wasCalled = mIntent.getBooleanExtra(RECOGNIZER_STATUS,false);
        int initial = 0x03;
        if (wasCalled) {        // when the recognizer was called, nothing to do
            mIntent.putExtra(RECOGNIZER_STATUS,false);
            initial = 0x02;
            return;
        }
        if ((initial&0x01)==0x01) {
            if (scene == SCENE_OPENING) {
                type = LeadingManager.RESUME_DESCRIPTION;
            } else if (scene == SCENE_MAIN_MENU) {
                type = LeadingManager.RESUME_DESCRIPTION;
            } else {
                initial &= ~0x01;
            }
        }
        if ((initial&0x02)==0x02) {
            // not to call task table in below scenes
            if (scene == SCENE_PROLOGUE || scene == SCENE_RESULT || scene == SCENE_CREDIT_VIEW) {
                initial &= ~0x02;
            }
        }
        if (initial == 0) return;
        if ((initial&0x01)==0x01) LeadingManager.GoToNextFile(type);
        if ((initial&0x02)==0x02) RecognitionButtonManager.toCallTaskTable();
    }
    /*
        the function is called as change the configuration.
    */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}