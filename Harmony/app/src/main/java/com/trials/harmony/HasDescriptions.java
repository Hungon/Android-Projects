package com.trials.harmony;

/**
 * Created by Kohei Moroi on 8/30/2016.
 */
public interface HasDescriptions {
    // each description of the button selected
    String DESCRIPTION_FOR_BUTTON_SELECTED_IN_OPENING[][] = {
            {
                    "You will be able to experience tutorial",
                    "in the whole game."
            },
            {
                    "You can start the game after making sure",
                    "each mode or music and so on."
            },
            {
                    "You can see contributors and",
                    "browse another contribution on ccMixter.",
                    "In web view, You will be able to return to",
                    "the opening scene as pressed back key."
            }
    };
    String DESCRIPTION_FOR_BUTTON_SELECTED_IN_MAIN_MENU[][] = {
            {
                    "Select mode is that you can choose style",
                    "to play the game."
            },
            {
                    "Select music is that you can listen to",
                    "each music to play the game."
            },
            {
                    "Select level is that you can choose level",
                    "to play the game."
            },
            {
                    "Play the game is that you can dive into",
                    "the Harmony world."
            },
            {
                    "You can make sure each association words of",
                    "colours in the mode you selected."
            },
            {
                    "In sound mode, you remember sound of colours",
                    "and answer the recognition",
                    "what colours are."
            },
            {
                    "In sentence mode, you remember sentence of",
                    "colours and answer the recognition",
                    "what colors are."
            },
            {
                    "In association mode, you remember association",
                    "words and answer the recognition",
                    "what associations are."
            },
            {
                    "Associations are in emotions.",
            },
            {
                    "Associations are in fruits and vegetables.",
            },
            {
                    "Associations are in all styles.",
            },
    };
    String DESCRIPTION_TO_NOTICE_EACH_STYLE[] = {
            "You are in the ",
            "Mode is ",
            "Level is ",
            "Music's title is "
    };
    String DESCRIPTION_TO_NOTICE_APPEARANCE_COLOURS_IN_PLAY[] = {
            "You are in the Prologue scene.",
            "Mode is ",
            "Level is ",
            "And, In this time, there will be",
            "",         // insert colours
            "Do you check the explanation?",
            "Sure.",
            "I shall explain again."
    };
    // each description of the music
    String DESCRIPTION_FOR_MUSIC[] = {
            "Music number is ",
            "Title is ",
            "Contributor is ",
            "Length is "
    };
    String DESCRIPTION_FOR_RECORDS[] = {
            "The best aggregate counts is ",
            "The best chain max is ",
            "The best total points is "
    };
}