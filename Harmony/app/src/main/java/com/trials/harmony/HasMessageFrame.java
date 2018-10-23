package com.trials.harmony;

import android.graphics.Point;

/**
 * Created by Kohei Moroi on 8/29/2016.
 */
public interface HasMessageFrame {
    // the kind of frame
    int FRAME_SMALL_BALLOON     = 0;
    int FRAME_MEDIUM_BALLOON    = 1;
    // each file's name
    String BALLOON_FILE_NAME[] = {
            "smallballoon","mediumballoon"
    };
    // balloon's size
    Point BALLOON_SIZE[] = {
            new Point(450,200),
            new Point(450,250)
    };

}