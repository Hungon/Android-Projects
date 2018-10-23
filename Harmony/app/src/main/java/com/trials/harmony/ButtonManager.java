package com.trials.harmony;

/**
 * Created by Kohei Moroi on 8/7/2016.
 */
abstract class ButtonManager implements HasButtons {
    /*
        Convert type into element for each transition button
        return value is orderly number.
    */
    protected int ConvertTypeIntoElementToTransit(int button) {
        for (int i = 0; i < BUTTONS_LIST.length; i++) {
            if (button == BUTTONS_LIST[i]) return i;
        }
        return -1;
    }

}