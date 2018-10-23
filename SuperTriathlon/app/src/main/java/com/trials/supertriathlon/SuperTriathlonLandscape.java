package com.trials.supertriathlon;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

// import AdBuddiz SDK
import com.purplebrain.adbuddiz.sdk.AdBuddiz;
import com.purplebrain.adbuddiz.sdk.AdBuddizDelegate;
import com.purplebrain.adbuddiz.sdk.AdBuddizError;

/**
 * Created by USER on 3/12/2016.
 */
public class SuperTriathlonLandscape extends Activity {
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(new GameView(this, GameView.VIEW_ORIENTATION.ORIENTATION_LANDSCAPE));
        // to initialize the advertisement
        AdBuddiz.setPublisherKey("e29999e4-2192-4d4b-a050-02103581b0c4");
        AdBuddiz.cacheAds(this); // this = current Activity
        // OPTIONAL, to get more info about the SDK behavior for AdBuddiz methods.
        // All callbacks in the delegate will be called in UI thread.
        AdBuddiz.setDelegate(new AdBuddizDelegate() {
            @Override
            public void didCacheAd() {
            }
            @Override
            public void didShowAd() {
                GameView.IsShowedAd(true);
            }
            @Override
            public void didFailToShowAd(AdBuddizError error) {
            }
            @Override
            public void didClick() {
            }
            @Override
            public void didHideAd() {
                GameView.IsShowedAd(false);
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        AdBuddiz.onDestroy(); // to minimize memory footprint
    }
    @Override
    public void onStop() {
        super.onStop();
        Sound.StopBGMTemporary();
    }
    @Override
    public void onStart() {
        super.onStart();
        Sound.RestartBGM();
    }
    /*
        the function is called as change the configuration.
    */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}