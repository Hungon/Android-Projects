package com.trials.harmony;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * Created by Kohei Moroi on 8/31/2016.
 */
public class WebViewActivity extends Activity implements HasScene {
    private int mKeyEvent;         // key event
    private Activity mActivity;
    private WebView mWebView;
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // create web page
        this.mWebView = new WebView(this);
        WebSettings settings = this.mWebView.getSettings();
        settings.setSaveFormData(false);
        settings.setSupportZoom(false);
        settings.setJavaScriptEnabled(true);
        // get URL
        this.mActivity = this;
        Intent intent = super.getIntent();
        String url = intent.getStringExtra("URL");
        // web view and informed request process.
        this.mWebView.setWebViewClient(new WebViewClient() {
            // is called before transition to web site.
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
            // is called as occured error.
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String url) {
                toast("Connection Error!");
            }
        });
        setContentView(this.mWebView);
        // load HTML
        this.mWebView.loadUrl(url);
    }
    // show toast
    private void toast(String text) {
        if (text == null) text = "";
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
    /***********************************************************************************************
     Key event
     **********************************************************************************************/
    // is called as key down.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        this.mKeyEvent = keyCode;
        // to return to Harmony activity.
        if (this.mKeyEvent == KeyEvent.KEYCODE_BACK) {
            this.mWebView.clearHistory();
            this.mWebView.clearCache(false);
            this.mWebView.reload();
            Intent intent = new Intent(this.mActivity, Harmony.class);
            intent.putExtra("Scene",SCENE_OPENING);
            this.mActivity.startActivity(intent);
        }
        // to adjust the sound volume
        this.AdjustVolume();
        return true;
    }
    // is called as key up.
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        this.mKeyEvent = -999;
        return true;
    }
    /*
        Adjust sound volume
    */
    private void AdjustVolume() {
        if (this.mKeyEvent == KeyEvent.KEYCODE_VOLUME_DOWN || this.mKeyEvent == KeyEvent.KEYCODE_VOLUME_UP) {
            AudioManager am = (AudioManager) this.mActivity.getSystemService(Context.AUDIO_SERVICE);
            // get current volume
            int volume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
            // get max volume
            int volumeMax = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            if (this.mKeyEvent == KeyEvent.KEYCODE_VOLUME_DOWN && 0 < volume) {
                volume--;
            } else if (this.mKeyEvent == KeyEvent.KEYCODE_VOLUME_UP && volume < volumeMax) {
                volume++;
            }
            // set volume
            int flags = AudioManager.FLAG_SHOW_UI | AudioManager.FLAG_PLAY_SOUND;
            am.setStreamVolume(AudioManager.STREAM_MUSIC, volume, flags);
        }
    }
}