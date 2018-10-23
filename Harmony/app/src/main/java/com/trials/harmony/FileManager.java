package com.trials.harmony;

/**
 * Created by Kohei Moroi on 7/21/2016.
 */
public interface FileManager {
    // the file to save system variables
    String FILE_EXPERIENCE = "experience.txt";
    // the files to load guidance sentence
    String FILE_STARTING_GUIDANCE[] = {
            "opening/opening","prologue/prologue",
            "","mainmenu/mainmenu","result/result",
            "tutorial/tutorial", ""
    };
    // Each the best record
    // each suffix of the file's name was added music id.
    String FILE_EACH_BEST_RECORD[] = {
            "colourbest","sentencebest","","emotionsbest","fruitsbest","allbest"
    };
    String FILE_NAME_EACH_SUFFIX[] = {"easy","normal","hard"};
    // the index to read the file
    // the filed index that done experience
    int FILE_INDEX_RECOGNIZED_IN_OP = 1;
    int FILE_INDEX_DID_NOT_RECOGNIZED_IN_OP = 2;
    int FILE_INDEX_SELECTED_SELECT_MODE  = 1;
    int FILE_INDEX_SELECTED_SELECT_MUSIC = 10;
    int FILE_INDEX_SELECTED_SELECT_LEVEL = 20;
    int FILE_INDEX_SELECTED_ASSOCIATION_GAME = 30;
    int FILE_INDEX_SOUND_MODE_TO_EXPLAIN = 0;
    int FILE_INDEX_SENTENCE_MODE_TO_EXPLAIN = 10;
    int FILE_INDEX_ASSOCIATION_MODE_TO_EXPLAIN = 20;
    int FILE_INDEX_DONE = 100;
    int FILE_INDEX_RESUME = 999;        // when the activity resumes, to read the current file with the index.
    int FILE_INDEX_UP_TO_END = -1;
}