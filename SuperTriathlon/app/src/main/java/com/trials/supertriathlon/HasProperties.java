package com.trials.supertriathlon;

/**
 * Created by Kohei Moroi on 11/25/2016.
 */

public interface HasProperties {
    // each stage
    int     STAGE_NOTHING  = -1;
    int     STAGE_OFF_ROAD = 0;
    int     STAGE_ROAD     = 1;
    int     STAGE_SEA      = 2;
    int STAGE_NUMBER_LIST[] = {STAGE_OFF_ROAD,STAGE_ROAD,STAGE_SEA};
    // each level
    int     LEVEL_NOTHING   = -1;
    int     LEVEL_EASY      = 0;
    int     LEVEL_NORMAL    = 1;
    int     LEVEL_HARD      = 2;
    int LEVEL_NUMBER_LIST[] = {LEVEL_EASY,LEVEL_NORMAL,LEVEL_HARD};
}
