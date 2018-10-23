package com.trials.supertriathlon;

/**
 * Created by Kohei Moroi on 11/25/2016.
 */

public interface HasRecords {
    // kind of record
    int    RECORD_TOTAL        = 0;
    int    RECORD_TIME         = 1;
    int    RECORD_CHAIN_MAX    = 2;
    int    RECORD_KIND         = 3;

    // kind of file
    // off-road
    String OFF_ROAD_RECORD_BEST_EASY    = "offroadbest_easy.txt";
    String OFF_ROAD_RECORD_BEST_NORMAL  = "offroadbest_normal.txt";
    String OFF_ROAD_RECORD_BEST_HARD    = "offroadbest_hard.txt";
    // road
    String ROAD_RECORD_BEST_EASY        = "roadbest_easy.txt";
    String ROAD_RECORD_BEST_NORMAL      = "roadbest_normal.txt";
    String ROAD_RECORD_BEST_HARD        = "roadbest_hard.txt";
    // sea
    String SEA_RECORD_BEST_EASY        = "seabest_easy.txt";
    String SEA_RECORD_BEST_NORMAL      = "seabest_normal.txt";
    String SEA_RECORD_BEST_HARD        = "seabest_hard.txt";

}
