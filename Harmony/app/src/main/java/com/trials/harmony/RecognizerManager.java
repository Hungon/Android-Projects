package com.trials.harmony;


import org.jetbrains.annotations.Contract;

/**
 * Created by USER on 5/6/2016.
 */
public class RecognizerManager  implements HasRecognitionId,
        HasRecognitionWords, HasSystem, HasScene, HasButtons {
    // static variables
    // filed
    // the current index
    private Recognizer mRecognizer;
    private int mCurrentCheckerIndex;
    private Utility mUtility;
    // to check words that recognized
    private String[] mWordsPool;
    // process status in Recognizer
    private int mProcessStatus;
    // the wordsIndex which is correct answer
    private static int mAggregateCorrectCount = 0;
    private static int mCurrentCorrectCount = 0;
    private static int mMaxChainCorrectCount = 0;
    private static int[] mToGetRecognitionId;
    private int mChainCount;
    // the max id to recognize the words.
    // default value is 1.
    private static int mMaxIdToRecognize = 1;
    // the guidance Id
    private static int mGuidanceId = GUIDANCE_MESSAGE_EMPTY;
    private static int mColourIdPool[];

    /*
        Constructor
    */
    public RecognizerManager() {
        this.mRecognizer = new Recognizer();
        // When except for result scene, to reset each score
        // because these score is needed in Result scene.
        if (SceneManager.GetCurrentScene() != SCENE_RESULT) {
            // each variable for correct wordsIndex
            mMaxChainCorrectCount = mAggregateCorrectCount = 0;
        }
        // guidance id
        mGuidanceId = GUIDANCE_MESSAGE_EMPTY;
        this.mChainCount = 0;
        this.mUtility = new Utility();
    }

    /*
        Initialize
        the method is called as called recognizer.
    */
    public void InitManager() {
        // to set the text to guide.
        // the argument is fixed interval to start recognizer.
        this.mRecognizer.InitRecognizer(60);
        // to allot the memory to recognize words
        if (mToGetRecognitionId == null) mToGetRecognitionId = new int[mMaxIdToRecognize];
        // reset recognition id
        for (int i = 0; i < mToGetRecognitionId.length; i++) {
            mToGetRecognitionId[i] = RECOGNITION_ID_EMPTY;
        }
        // the current index
        this.mCurrentCheckerIndex = 0;
        // reset current correct wordsIndex
        mCurrentCorrectCount = 0;
        // is recognizing
        this.mProcessStatus = Recognizer.READY;
    }

    /*
        Update
        return value is process which to execute each function in RecognitionMode.
    */
    public int UpdateManager() {
        // not to transition to scene before recognition of the words.
        int process = RecognitionMode.UPDATE;
        // to update recognition process.
        // when finished process, to return false
        this.mProcessStatus = this.mRecognizer.UpdateRecognizer();

        if (this.mProcessStatus == Recognizer.FINISH) {
            // to allot words to recognize
            if (mGuidanceId == GUIDANCE_MESSAGE_SELECT_TUNE || mGuidanceId == GUIDANCE_MESSAGE_SELECT_TUNE_AFTER_LISTEN_TO_TUNE) {
                // to get max element from MusicSelector
                int max = MusicSelector.GetMaxId() + 1;
                // allot memory
                this.mWordsPool = new String[max];
                for (int i = 0; i < max; i++) this.mWordsPool[i] = Integer.toString(i + 1);
            } else {
                // when in play scene and the current mode is association either,
                // to allot each word in WordsChecker function
                this.mWordsPool = new String[WORDS_POOL[mGuidanceId].length];
                // to decide to check words from words' pool.
                System.arraycopy(WORDS_POOL[mGuidanceId], 0, this.mWordsPool, 0, WORDS_POOL[mGuidanceId].length);
            }
            // to make the interval and then
            if (this.mUtility.ToMakeTheInterval(120)) {
                // not to transition to scene before recognition of the words.
                process = RecognitionMode.RELEASE;
                String words = Harmony.GetRecognizedWords();
                // when got the recognized words, to initialize the recognition id
                // to diverge the process from that id.
                if (!words.equals("")) {
                    // when that id to transition scene
                    if (mGuidanceId == GUIDANCE_MESSAGE_TRANSITION) {
                        mToGetRecognitionId[0] = this.checkWords(words.toCharArray(), 0, mGuidanceId);
                        int nextScene = this.CheckTransitionScene(mToGetRecognitionId[0]);
                        if (nextScene != SCENE_NOTHING) {
                            Wipe.SetAvailableToCreate(true);
                            SceneManager.SetNextScene(nextScene);
                            process = RecognitionMode.DESTROY;
                        }
                        // in play scene,
                    } else if (mGuidanceId == GUIDANCE_MESSAGE_COLOUR || mGuidanceId == GUIDANCE_MESSAGE_SENTENCE ||
                            mGuidanceId == GUIDANCE_MESSAGE_ASSOCIATION_IN_EMOTIONS ||
                            mGuidanceId == GUIDANCE_MESSAGE_ASSOCIATION_IN_FRUITS ||
                            mGuidanceId == GUIDANCE_MESSAGE_ASSOCIATION_IN_ALL) {
                        for (int i = 0; i < mToGetRecognitionId.length; i++) {
                            int buttonType = BUTTON_EMPTY;
                            int guidanceId[] = null;
                            if (mToGetRecognitionId[i] != RECOGNITION_ID_EMPTY) continue;
                                // get type of colour that showed up in play scene.
                            if (mGuidanceId == GUIDANCE_MESSAGE_COLOUR) {
                                buttonType = ColourManager.getCurrentButtonType()[i];
                                guidanceId = new int[1];
                                guidanceId[0] = GUIDANCE_MESSAGE_COLOUR;
                            } else if (mGuidanceId == GUIDANCE_MESSAGE_SENTENCE) {
                                buttonType = SentenceManager.getCurrentButtonType()[i];
                                guidanceId = new int[1];
                                guidanceId[0] = GUIDANCE_MESSAGE_SENTENCE;
                            } else if (mGuidanceId == GUIDANCE_MESSAGE_ASSOCIATION_IN_EMOTIONS) {
                                guidanceId = new int[1];
                                guidanceId[0] = GUIDANCE_MESSAGE_ASSOCIATION_IN_EMOTIONS;
                                buttonType = AssociationManager.getCurrentButtonType()[i];
                            } else if (mGuidanceId == GUIDANCE_MESSAGE_ASSOCIATION_IN_FRUITS) {
                                guidanceId = new int[1];
                                guidanceId[0] = GUIDANCE_MESSAGE_ASSOCIATION_IN_FRUITS;
                                buttonType = AssociationManager.getCurrentButtonType()[i];
                            } else if (mGuidanceId == GUIDANCE_MESSAGE_ASSOCIATION_IN_ALL) {
                                guidanceId = new int[2];
                                guidanceId[0] = GUIDANCE_MESSAGE_ASSOCIATION_IN_EMOTIONS;
                                guidanceId[1] = GUIDANCE_MESSAGE_ASSOCIATION_IN_FRUITS;
                                buttonType = AssociationManager.getCurrentButtonType()[i];
                            }
                            if (buttonType == BUTTON_EMPTY) continue;  // when the type is empty, to next.
                            // and then recognition id is empty.
                            int startingElement = PlayManager.convertTypeIntoElement(
                                    SystemManager.GetPlayMode(), buttonType);
                            for (int g:guidanceId) {
                                mToGetRecognitionId[i] = this.checkWords(
                                        words.toCharArray(), startingElement, g);
                            }
                            this.UpdateRecord(mToGetRecognitionId[i]);
                        }
                        this.ToStoreTheIdThatExceptForEmpty();
                    } else if (mGuidanceId == GUIDANCE_MESSAGE_SELECT_MODE) {
                        mToGetRecognitionId[0] = this.checkWords(words.toCharArray(), 0, mGuidanceId);
                        int modes[] = {MODE_SOUND,MODE_SENTENCE,MODE_ASSOCIATION};
                        for (int i = 0; i < RECOGNITION_TO_EXECUTE[GUIDANCE_MESSAGE_SELECT_MODE].length; i++) {
                            if (RECOGNITION_TO_EXECUTE[GUIDANCE_MESSAGE_SELECT_MODE][i] == mToGetRecognitionId[0]) {
                                SystemManager.SetPlayMode(modes[i]);
                                break;
                            }
                        }
                    } else if (mGuidanceId == GUIDANCE_MESSAGE_SELECT_ASSOCIATION) {
                        mToGetRecognitionId[0] = this.checkWords(words.toCharArray(), 0, mGuidanceId);
                        int modes[] = {MODE_ASSOCIATION_IN_EMOTIONS,MODE_ASSOCIATION_IN_FRUITS,MODE_ASSOCIATION_IN_ALL};
                        for (int i = 0; i < RECOGNITION_TO_EXECUTE[GUIDANCE_MESSAGE_SELECT_ASSOCIATION].length; i++) {
                            if (RECOGNITION_TO_EXECUTE[GUIDANCE_MESSAGE_SELECT_ASSOCIATION][i] == mToGetRecognitionId[0]) {
                                SystemManager.SetPlayMode(modes[i]);
                                break;
                            }
                        }
                    } else if (mGuidanceId == GUIDANCE_MESSAGE_SELECT_LEVEL) {
                        mToGetRecognitionId[0] = this.checkWords(words.toCharArray(), 0, mGuidanceId);
                        for (int i = 0; i < LEVEL_LIST.length; i++) {
                            if (mToGetRecognitionId[0] == RECOGNITION_TO_EXECUTE[GUIDANCE_MESSAGE_SELECT_LEVEL][i]) {
                                SystemManager.SetGameLevel(LEVEL_LIST[i]);
                                break;
                            }
                        }
                    } else {
                        mToGetRecognitionId[0] = this.checkWords(words.toCharArray(), 0, mGuidanceId);
                    }
                }
            }
        }
        return process;
    }

    /*
        Release
    */
    public void ReleaseManager() {
        this.mRecognizer.ReleaseRecognizer();
    }

    /*
        Destroy recognizer
    */
    void DestroyRecognize() {
        this.mRecognizer.ReleaseRecognizer();
        this.mRecognizer = null;
        // max id to recognize
        mMaxIdToRecognize = 1;
        if (mToGetRecognitionId != null) mToGetRecognitionId = null;
    }

    /*
        To check the words to execute the process
        return value is id of the word.
    */
    private int checkWords(char[] raw, int startingElement, int guidanceId) {
        // to select the element to the recognized word
        int id = RECOGNITION_ID_EMPTY;
        int wordsIndex;
        int rawIndex;
        // when the current mode is association,
        if (guidanceId == GUIDANCE_MESSAGE_ASSOCIATION_IN_EMOTIONS) {
            if (this.mWordsPool != null) this.mWordsPool = null;
            this.mWordsPool = new String[ASSOCIATION_WORDS_IN_EMOTIONS[startingElement].length];
            System.arraycopy(ASSOCIATION_WORDS_IN_EMOTIONS[startingElement],0,this.mWordsPool,0,ASSOCIATION_WORDS_IN_EMOTIONS[startingElement].length);
        } else if (guidanceId == GUIDANCE_MESSAGE_ASSOCIATION_IN_FRUITS) {
            if (this.mWordsPool != null) this.mWordsPool = null;
            this.mWordsPool = new String[ASSOCIATION_WORDS_IN_FRUITS[startingElement].length];
            System.arraycopy(ASSOCIATION_WORDS_IN_FRUITS[startingElement],0,this.mWordsPool,0,ASSOCIATION_WORDS_IN_FRUITS[startingElement].length);
        }
        for (int j = startingElement; j < this.mWordsPool.length; j++) {
            // when the current mode is all, to loop each mode
            char[] words = this.mWordsPool[j].toCharArray();
            rawIndex = this.mCurrentCheckerIndex;
            boolean check;
            for (int i = rawIndex; i < raw.length; i++) {
                if (words.length == 0) break;
                wordsIndex = 0;
                // when checking first character,
                // to check both upper case and lower case.
                check = (raw[i] == words[wordsIndex]); // lower case
                if (!check) {
                    // if comparing character is number,
                    if (guidanceId == GUIDANCE_MESSAGE_SELECT_TUNE ||
                        guidanceId == GUIDANCE_MESSAGE_SELECT_TUNE_AFTER_LISTEN_TO_TUNE) {
                        words = WordsManager.convertNumberCase(j+1).toCharArray();
                        check = (raw[i] == words[wordsIndex]);
                    }
                    if (!check) {
                        check = (raw[i] == WordsManager.convertCharacterCase(words[wordsIndex], WordsManager.CASE_UPPER));
                    }
                }
                int correct = 0;
                if (check) {
                    rawIndex = i + 1;
                    wordsIndex++;
                    if (wordsIndex < words.length && rawIndex < raw.length) {
                        do {
                            if (wordsIndex < words.length && rawIndex < raw.length) {
                                if (words[wordsIndex] == raw[rawIndex]) {
                                    correct++;
                                }
                            } else {
                                break;
                            }
                            wordsIndex++;
                            rawIndex++;
                        } while (wordsIndex < words.length);
                    }
                }
                // to get the words that recognized words
                if (words.length/2 <= correct) {
                    // to get current rawIndex
                    this.mCurrentCheckerIndex = rawIndex+1;
                    //*************************************
                    // Except for to select tune,
                    //*************************************
                    if (guidanceId == GUIDANCE_MESSAGE_SELECT_TUNE ||
                        guidanceId == GUIDANCE_MESSAGE_SELECT_TUNE_AFTER_LISTEN_TO_TUNE) {
                        // to set current element to play the tune
                        MusicSelector.SetMusicElementRecognized(j);
                        id = RECOGNITION_ID_SELECT_TUNE;
                    } else if (guidanceId == GUIDANCE_MESSAGE_ASSOCIATION_IN_EMOTIONS ||
                            guidanceId == GUIDANCE_MESSAGE_ASSOCIATION_IN_FRUITS) {
                        id = RECOGNITION_TO_EXECUTE[guidanceId][startingElement];
                    } else {
                        if (j < RECOGNITION_TO_EXECUTE[guidanceId].length) {
                            id = RECOGNITION_TO_EXECUTE[guidanceId][j];
                        }
                    }
                    break;
                } else {
                    rawIndex = 0;
                }
            }
            // if current guidance id is colour, to check once only
            if (guidanceId == GUIDANCE_MESSAGE_COLOUR || guidanceId == GUIDANCE_MESSAGE_SENTENCE) break;
        }
        return id;
    }
    /**************************************************
     The id to transition
     which is GUIDANCE_MESSAGE_TRANSITION
     ***************************************************/
    /*
        To check to execute the process based on got the id.
        return value is number of process in the current scene.
    */
    private int CheckTransitionScene(int id) {
        // to diverge the process from got the id.
        if (id == RECOGNITION_ID_TO_PROLOGUE) {
            return CheckToAvailableTransitionNumber(SCENE_PROLOGUE);
        } else if (id == RECOGNITION_ID_TO_OPENING) {
            return CheckToAvailableTransitionNumber(SCENE_OPENING);
        } else if (id == RECOGNITION_ID_TO_PLAY) {
            return CheckToAvailableTransitionNumber(SCENE_PLAY);
        } else if (id == RECOGNITION_ID_TO_SELECT_MODE) {
            return CheckToAvailableTransitionNumber(SCENE_MAIN_MENU);
        } else if (id == RECOGNITION_ID_TO_TUTORIAL) {
            return CheckToAvailableTransitionNumber(SCENE_TUTORIAL);
        } else if (id == RECOGNITION_ID_TO_CREDIT_VIEW) {
            return CheckToAvailableTransitionNumber(SCENE_CREDIT_VIEW);
        }
        return SCENE_NOTHING;
    }
    /*
        To available to transition to next scene.
    */
    @Contract(pure = true)
    private int CheckToAvailableTransitionNumber(int nextScene) {
        if (nextScene == SceneManager.GetCurrentScene()) return SCENE_NOTHING;
        return nextScene;
    }
    // To convert the id into element
    @Contract(pure = true)
    public static int[] ToConvertTheRecognitionIdIntoElement(int guidanceId, int[] pile) {
        int res[] = new int[pile.length];
        for (int i = 0; i < pile.length; i++) {
            if (pile[i] == RECOGNITION_ID_EMPTY) continue;
            for (int j = 0; j < RECOGNITION_TO_EXECUTE[guidanceId].length; j++) {
                if (pile[i] == RECOGNITION_TO_EXECUTE[guidanceId][j]) {
                    res[i] = j;
                    break;
                }
            }
        }
        return res;
    }
    /*
        To detect the correct by recognised id.
    */
    private void UpdateRecord(int id) {
        if (id == RECOGNITION_ID_EMPTY) {
            this.mChainCount = 0;
            return;
        }
        // when got id isn't empty, to wordsIndex the correct answer
        mCurrentCorrectCount++;
        // to renewal the aggregate correct wordsIndex
        mAggregateCorrectCount++;
        // to update the chain value
        this.mChainCount++;
        // to update max chain value
        if (mMaxChainCorrectCount < this.mChainCount) mMaxChainCorrectCount = this.mChainCount;
    }
    /*
        Reset the id that recognized
    */
    public static void ResetIdRecognized() {
        if (mToGetRecognitionId == null) return;
        for (int i = 0; i < mToGetRecognitionId.length; i++) {
            mToGetRecognitionId[i] = RECOGNITION_ID_EMPTY;
        }
    }
    // to store the correct id that except for empty.
    private void ToStoreTheIdThatExceptForEmpty() throws ArrayIndexOutOfBoundsException
    {
        // to store the id
        int currentId[];
        int dummy[] = new int[mToGetRecognitionId.length];
        int oldId[] = {0};
        int count = 0;
        // if find the correct id, to store that id into the global variable
        for (int id:mToGetRecognitionId) {
            if (RECOGNITION_ID_EMPTY < id) {
                dummy[count] = id;
                count++;
            }
        }
        if (count == 0) return;     // there is nothing to do

        // to allot the memory to global variable to store the id.
        int lengthOld = 0;
        if (mColourIdPool != null) {
            lengthOld = mColourIdPool.length;
            oldId = new int[lengthOld];
            // to get old id
            System.arraycopy(mColourIdPool,0,oldId,0,lengthOld);
        }
        // to allot memory for to get current id
        currentId = new int[count];
        System.arraycopy(dummy,0,currentId,0,count);
        if (mColourIdPool != null) mColourIdPool = null;
        if (0 < count+lengthOld) mColourIdPool = new int[count+lengthOld]; // new allocation to store the id
        if (mColourIdPool != null) {
            // to copy the old id
            System.arraycopy(oldId,0,mColourIdPool,0,oldId.length);
            // these are new id
            System.arraycopy(currentId,0,mColourIdPool,oldId.length-1,currentId.length);
        }
    }
    /*****************************************************
     Each setter functions
     ****************************************************/
    /*
        Set Max id to recognize
    */
    public static void SetMaxIdToRecognize(int max) {
        if (0 < max) mMaxIdToRecognize = max;
    }
    /*
        Set guidance id
    */
    public static void SetGuidanceId(int id) {
        if (GUIDANCE_MESSAGE_EMPTY < id && id < GUIDANCE_WORDS.length) {
            mGuidanceId = id;
        }
    }
    public static void ResetStoredId() {
        if (mColourIdPool != null) mColourIdPool = null;
    }
    /*****************************************************
     Each getter function
     ****************************************************/
    /*
        Get process status in Recognizer
    */
    public int GetProcessStatus() { return this.mProcessStatus; }
    /*
        Get id recognized
    */
    @Contract(pure = true)
    public static int[] GetRecognitionId() {
        int res[] = {RecognizerManager.RECOGNITION_ID_EMPTY};
        return (mToGetRecognitionId == null) ? res : mToGetRecognitionId;
    }
    /*
        Get total correct wordsIndex
    */
    @Contract(pure = true)
    public static int   GetAggregateCount() { return mAggregateCorrectCount; }
    /*
        Get current correct wordsIndex
    */
    @Contract(pure = true)
    public static int   GetCurrentCorrectCount() { return mCurrentCorrectCount; }
    /*
        Get chain value
    */
    @Contract(pure = true)
    public static int   GetMaxChainCorrectCount() { return mMaxChainCorrectCount; }
    /*
        Get guidance id
    */
    @Contract(pure = true)
    public static int GetGuidanceId() { return mGuidanceId; }
    // Get the the pile of id that stored
    @Contract(pure = true)
    public static int[] GetStoredId() {
        int res[] = {0};
        return (mColourIdPool != null)?mColourIdPool:res;
    }
}
