package com.trials.harmony;

import org.jetbrains.annotations.Contract;

/**
 * Created by Kohei Moroi on 10/9/2016.
 */

abstract class PlayManager implements HasButtons, HasSystem, HasColourImage, HasRecognitionWords  {
    private Utility mUtility;
    private int mPreviewElement;
    protected PROCESS_TO_PLAY mProcess;
    protected int mCount;
    static int mAppearKind[] = null;
    static boolean  sTerminate;
    static int sTypeToGet[];
    static boolean sAvailableToUpdate;
    enum PROCESS_TO_PLAY {
        READY,INITIALIZE,UPDATE,GUIDING,CALLING
    }

    public PlayManager(int appearKind, int idMax) {
        this.mUtility = new Utility();
        sTypeToGet = new int[idMax];
        for (int i = 0; i < sTypeToGet.length; i++) {
            sTypeToGet[i] = BUTTON_EMPTY;
        }
        if (mAppearKind == null) {
            mAppearKind = new int[appearKind];
            for (int i = 0; i < mAppearKind.length; i++) mAppearKind[i] = -1;
        }
        this.mProcess = PROCESS_TO_PLAY.READY;
        sTerminate = false;
        this.mPreviewElement = 0;
        this.mCount = 0;
        sAvailableToUpdate = false;
    }

    abstract protected void initManager();
    abstract protected int updateManager();
    abstract protected void drawManager();
    abstract protected void releaseManager();

    protected void initAppearance(int level, int mode) throws NullPointerException {
        // when kind of colour is existing, do nothing
        int init = 0;
        for (int a:mAppearKind) {
            if (BUTTON_EMPTY < a) init++;
        }
        if (init == mAppearKind.length) return;
        // get list of colour by the current mode
        int buttonKind[] = getColourTypeList(mode);
        if (buttonKind != null) {
            int max = getColourKindMax(mode,level);
            // to diverge the current level,
            if (level == LEVEL_EASY) {
                System.arraycopy(buttonKind, 0, mAppearKind, 0, mAppearKind.length);
            } else {
                int dummy[] = new int[mAppearKind.length];
                for (int i = 0; i < dummy.length; i++) dummy[i] = -1;
                int correct = 0;
                for (int i = 0; i < mAppearKind.length; i++) {
                    int count = 0;
                    boolean re = true;
                    do {
                        if (count == 0) mAppearKind[i] = buttonKind[this.mUtility.GetRandom(max)];
                        if (!(dummy[count] == mAppearKind[i])) {
                            count++;
                            if (count == dummy.length) break;
                            if (dummy[count] == -1) {
                                correct++;
                                dummy[correct - 1] = mAppearKind[i];
                                re = false;
                            }
                        } else {
                            count = 0;
                        }
                    } while (re);
                }
            }
        }
        this.mCount = 0;
    }
    protected void release() {
        if (mAppearKind != null) mAppearKind = null;
        if (sTypeToGet != null) sTypeToGet = null;
    }
    /*
        To check to kind of sentence to exchange with own type to the element.
        Return value is that element.
    */
    @Contract(pure = true)
    static int convertTypeIntoElement(int mode, int type) {
        int buttonKind[] = getColourTypeList(mode);
        if (buttonKind != null) {
            for (int i = 0; i < buttonKind.length; i++) {
                if (buttonKind[i] == type) {
                    return i;
                }
            }
        }
        return -1;
    }
    private static int[] getColourTypeList(int mode) {
        int buttonKind[] = null;
        if (mode == MODE_SOUND) {
            buttonKind = new int[BUTTON_COLOURS.length];
            System.arraycopy(BUTTON_COLOURS,0,buttonKind,0,BUTTON_COLOURS.length);
        } else if (mode == MODE_SENTENCE) {
            buttonKind = new int[BUTTON_SENTENCES.length];
            System.arraycopy(BUTTON_SENTENCES,0,buttonKind,0,BUTTON_SENTENCES.length);
        } else if (mode == MODE_ASSOCIATION_IN_EMOTIONS) {
            buttonKind = new int[BUTTON_ASSOCIATIONS_IN_EMOTIONS.length];
            System.arraycopy(BUTTON_ASSOCIATIONS_IN_EMOTIONS, 0, buttonKind, 0, BUTTON_ASSOCIATIONS_IN_EMOTIONS.length);
        } else if (mode == MODE_ASSOCIATION_IN_FRUITS) {
            buttonKind = new int[BUTTON_ASSOCIATIONS_IN_FRUITS.length];
            System.arraycopy(BUTTON_ASSOCIATIONS_IN_FRUITS, 0, buttonKind, 0, BUTTON_ASSOCIATIONS_IN_FRUITS.length);
        } else if (mode == MODE_ASSOCIATION_IN_ALL) {
            buttonKind = new int[BUTTON_ASSOCIATIONS_IN_ALL.length];
            System.arraycopy(BUTTON_ASSOCIATIONS_IN_ALL, 0, buttonKind, 0, BUTTON_ASSOCIATIONS_IN_ALL.length);
        }
        return buttonKind;
    }
    protected void toBeOrdinary() {
        this.mCount = 0;
        this.mPreviewElement = 0;
        for (int i = 0; i < sTypeToGet.length; i++) {
            sTypeToGet[i] = BUTTON_EMPTY;
        }
    }
    static int getColourKindMax(int mode,int level) {
        int allot = 0;
        switch(level) {
            case LEVEL_EASY:
                allot = (mode==MODE_ASSOCIATION_IN_FRUITS)?4:6;
                break;
            case LEVEL_NORMAL:
                allot = (mode==MODE_ASSOCIATION_IN_FRUITS)?5:7;
                break;
            case LEVEL_HARD:
                allot = (mode==MODE_ASSOCIATION_IN_FRUITS)?7:9;
                break;
        }
        return allot;
    }
    static int getColourKindMax(int mode) {
        int allot;
        allot = (mode==MODE_ASSOCIATION_IN_FRUITS)?BUTTON_ASSOCIATIONS_IN_FRUITS.length:9;
        return allot;
    }
    protected int getButtonTypeToGuide(int interval) {
        if (this.mUtility.ToMakeTheInterval(interval)) {
            boolean guiding = GuidanceManager.GetIsGuiding();
            if (sTypeToGet.length <= this.mPreviewElement) return BUTTON_EMPTY;
            // when not to guide,
            // to increase the preview element to get the button type to guide.
            if (!guiding && sTypeToGet[this.mPreviewElement] != BUTTON_EMPTY) {
                int type = sTypeToGet[this.mPreviewElement];
                this.mPreviewElement++;
                sTerminate = (this.mPreviewElement == sTypeToGet.length);
                return type;
            }
        }
        return BUTTON_EMPTY;
    }
    static int[] getAppearKind() {
        int res[] = {-1};
        return (mAppearKind == null)?res:mAppearKind;
    }
    /*
        Get the current type of button that reached to the end position
    */
    @Contract(pure = true)
    static int[] getCurrentButtonType() {
        int res[] = {-1};
        res = (sTypeToGet == null) ? res : sTypeToGet;
        return res;
    }
    static void setAvailableToUpdate() { sAvailableToUpdate = true; }
}
