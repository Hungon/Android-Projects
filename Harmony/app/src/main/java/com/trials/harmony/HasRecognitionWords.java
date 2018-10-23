package com.trials.harmony;

/**
 * Created by Kohei Moroi on 7/21/2016.
 */
public interface HasRecognitionWords {
    // the texts to guide
    String GUIDANCE_WORDS[][] = {
            {"Where to next?",
                    "The recognizer can recognise opening, tutorial, " +
                            "main menu, prologue, play and credit view",
                    "Please input your voice once again"},
            {"tell me what colours are",
                    "Please input your voice once again"},
            {"which mode do you like?",
                    "These are sound mode, sentence mode and association mode",
                    "Please input your voice once again"},
            {"which music do you want to listen in the play scene?",
                    "tell me number of music",
                    "Please input your voice once again"},
            {"where to next?",
                    "You should try to say Tutorial",
                    "Please input your voice once again"},
            {"tell me yes or no",
                    "Please input your voice once again"},
            {"which level do you play?",
                    "These are easy, normal and hard",
                    "Please input your voice once again"},
            {"tell me sentence",
                    "Please input your voice once again"},
            {"what do you want to do?",
                    "The recognizer can recognise select mode," +
                            " select music, select level, reference and play the game",
                    "Please input your voice once again"},
            {"which music do you want to listen in the play scene?",
                    "tell me number of music",
                    "Please input your voice once again"},
            {"Which style do you want to play?",
                    "these are emotions style, fruits style and all style",
                    "please input your voice once again"},
            {"what are in emotions?",
            "please input your voice once again"},
            {"what are in fruits?",
                    "please input your voice once again"},
            {"tell me association words",
                    "please input your voice once again"}
    };
    // the id that to guide to input
    int GUIDANCE_MESSAGE_EMPTY = -1;
    int GUIDANCE_MESSAGE_TRANSITION = 0;
    int GUIDANCE_MESSAGE_COLOUR = 1;
    int GUIDANCE_MESSAGE_SELECT_MODE = 2;
    int GUIDANCE_MESSAGE_SELECT_TUNE = 3;
    int GUIDANCE_MESSAGE_TRANSITION_IN_PRACTICE = 4;
    int GUIDANCE_MESSAGE_ANSWER = 5;
    int GUIDANCE_MESSAGE_SELECT_LEVEL = 6;
    int GUIDANCE_MESSAGE_SENTENCE = 7;
    int GUIDANCE_MESSAGE_SELECT_MAIN_MENU = 8;
    int GUIDANCE_MESSAGE_SELECT_TUNE_AFTER_LISTEN_TO_TUNE = 9;
    int GUIDANCE_MESSAGE_SELECT_ASSOCIATION = 10;
    int GUIDANCE_MESSAGE_ASSOCIATION_IN_EMOTIONS = 11;
    int GUIDANCE_MESSAGE_ASSOCIATION_IN_FRUITS = 12;
    int GUIDANCE_MESSAGE_ASSOCIATION_IN_ALL = 13;


    // the words to recognize to relative the id which is managed in recognizer manager.
    String WORDS_POOL[][] = {
            {
            "start", "prologue", "opening", "play", "main", "tutorial", "credit"
            },                     // to transition the scene
            {"red", "blue", "yellow", "green", "white", "black", "violet", "pink","orange"},                   // for colours
            {"sound", "sentence", "association"},                                             // each mode
            {""}, // recognition words in order to max of tune's element in MusicSelector class.
            {"start", "prologue", "opening", "play", "main", "tutorial"},                     // to transition the scene in practice
            {"yes", "no"},
            {"easy", "normal", "hard"},
            {"red", "blue", "yellow", "green", "white", "black", "violet", "pink","orange"},                   // for colours
            {"mode", "music", "level", "play", "reference"},                                     // selections in main menu
            {""}, // recognition words in order to max of tune's element in MusicSelector class.
            {"emotions", "fruits", "all"},           // association game
            {""},{""},{""}      // each association word
    };

    // association words
    String ASSOCIATION_WORDS_IN_EMOTIONS[][] = {
            {"courage", "strength", "warmth", "energy", "survival", "fight",
                    "stimulation", "masculinity", "excitement", "defiance", "aggression",
                    "impact", "strain"},     // red
            {"intelligence", "communication", "trust", "efficiency", "serenity",
                    "duty", "logic", "cool", "reflection", "calm", "cold", "aloofness", "lack",
                    "unfriendly"},          // blue
            {"optimism", "confidence", "self-esteem", "extraversion", "strength", "friendliness",
                    "creativity", "irrationality", "fear", "fragility", "depression",
                    "anxiety", "suicide"}, // yellow
            {"harmony", "balance", "refreshment", "universal", "rest", "restoration", "reassurance",
                    "environment", "awareness", "equilibrium", "peace", "boredom", "stagnation", "blandness",
                    "enervation"},         // green
            {"hygiene", "sterility", "clarity", "purity", "cleanness", "simplicity", "sophistication",
                    "efficiency", "cold", "barriers", "unfriendliness",
                    "elitism"},             // white
            {"sophistication", "glamour", "security", "safety", "efficiency", "substance", "oppression",
                    "cold", "menace", "heaviness"}, // black
            {"spiritual", "awareness", "containment", "vision", "luxury", "authenticity", "truth",
                    "quality", "introversion", "decadence", "suppression",
                    "inferiority"},             // violet
            {"tranquillity", "nurture", "warmth", "femininity", "sexuality", "inhibition",
                    "claustrophobia", "emasculation", "weakness"}, // pink
            {"comfort","food","warmth","security","sensuality","passion","abundance",
            "fun","deprivation","frustration","frivolity","immaturity"} // orange
    };
    String ASSOCIATION_WORDS_IN_FRUITS[][] = {
            {"beets","orange","cherry","cranberry","guava","papaya","grapefruit",
            "grapefruit","pomegranate","radicchio","radish","raspberry","apple","bell pepper",
            "chill pepper","grape","onion","pear","pepper","potato","rhubarb","strawberry",
            "tomato","watermelon"},     // red
            {"currant","salsify","blackberry","blueberry","dried plum","plum",
            "prunes"},                  // blue
            {"apricot","butternut squash","cantaloupe","cape gooseberry","golden kiwifruit","grapefruit",
            "lemon","sweet corn","apple","beet","fig","pepper","potato",
            "summer squash","tomato","watermelon",
            "winter squash","banana"},    // yellow
            {"artichoke","arugula","asparagus","avocado","broccoliflower","broccoli","broccoli rabe","brussel sprout",
            "celery","squash","chinese cabbage","cucumber","endive","apple","bean","cabbage",
            "grape","onion","pea","spinach","sugar snap pea","watercress",
                    "zucchini"},        // green
            {"banana","brown pear","cauliflower","dates","garlic","ginger","jerusalem artichoke","jicama",
            "kohlrabi","onion","parsnip","potato","shallot","turnip","white corn","white nectarine",
                    "white peach"},     // white
            {""},                       // black
            {"eggplant","grape","potato","asparagus","cabbage","carrot",
            "fig","grape","pepper"}, // violet
            {""},                        // pink
            {"apricot","butternut squash","cantaloupe","cape gooseberry",
            "carrot","golden kiwifruit","orange"} // orange
    };
}
