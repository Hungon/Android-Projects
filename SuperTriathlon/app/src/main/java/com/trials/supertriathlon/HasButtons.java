package com.trials.supertriathlon;

import android.graphics.Point;

/**
 * Created by Kohei Moroi on 11/25/2016.
 */

public interface HasButtons {
    String BUTTONS_FILE = "buttons";
    Point BUTTONS_SIZE[] = {
            new Point(100,48),
            new Point(160,48),
            new Point(120,48),
            new Point(179,48)
    };
    int BUTTON_TYPE_START = 0;
    int BUTTON_TYPE_OPTION = 1;
    int BUTTON_TYPE_CREDIT = 2;
    int BUTTON_TYPE_RANKING = 3;
}
