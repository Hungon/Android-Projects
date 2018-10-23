package com.trials.supertriathlon;

/**
 * Created by USER on 2/24/2016.
 */
public class Creation {
    // filed
    public int             mExistCount;
    public int             mCreatedCount;
    public int             mFixedInterval;
    public int             mIntervalCount;
    public int             mCreatedCountMax;

    /*
        Constructor
     */
    public Creation() {
        this.mExistCount = 0;
        this.mCreatedCount = 0;
        this.mFixedInterval = 0;
        this.mIntervalCount = 0;
        this.mCreatedCountMax = 0;
    }
    /*
        Constructor
    */
    public Creation(int fixedInterval) {
        this.mExistCount = 0;
        this.mCreatedCount = 0;
        this.mFixedInterval = fixedInterval;
        this.mIntervalCount = 0;
        this.mCreatedCountMax = 0;
    }
    /*
        Creation interval
     */
    public boolean CreationInterval() {
        if (this.mFixedInterval < this.mIntervalCount) {
            this.mIntervalCount = 0;           // reset count
            return true;
        }
        return false;
    }
    /*
        Check count
    */
    public boolean CheckCreatedCount(int countMax) {
        this.mCreatedCount++;
        if (countMax <= this.mCreatedCount){
            this.mCreatedCount = 0;    // reset count
            return true;
        }
        return false;
    }

    /*
        Replace element by character's type.
    */
    public int[] ReplaceElementByType(BaseCharacter ch[], int[] priority, int start, int max) {
        int[] replace = new int[max];
        int cnt = 0;
        // loop to kind
        for (int j = start; j < priority.length; j++) {
            // loop to max
            for (int i = 0; i < ch.length; i++) {
                if (ch[i].mExistFlag) {
                    if (ch[i].mType == priority[j]) {
                        replace[cnt] = i;
                        cnt++;
                    }
                }
            }
        }
        return replace;
    }
    /*
        Replace element by character's type.
    */
    public int[] ReplaceElementByType(BaseCharacter ch[], int[] priority, int start) {
        int[] replace = new int[ch.length];
        int cnt = 0;
        // loop to kind
        for (int j = start; j < priority.length; j++) {
            // loop to max
            for (int i = 0; i < ch.length; i++) {
                if (ch[i].mExistFlag) {
                    if (ch[i].mType == priority[j]) {
                        replace[cnt] = i;
                        cnt++;
                    }
                }
            }
        }
        return replace;
    }

    /*
        Replace element by character's position-Y.
    */
    public int[] ReplaceElementByPositionY(BaseCharacter ch[]) {
        int[] replace = new int[ch.length];
        // loop to kind
        for (int j = 0; j < ch.length; j++) {
            // loop to max
            for (int i = j+1; i < ch.length; i++) {
                if (ch[i].mPos.y <= ch[j].mPos.y) {
                    replace[j] = i;
                } else {
                    replace[j] = j;
                }
            }
        }
        return replace;
    }

    /*
        Reset
     */
    public void ResetCreation() {
        this.mExistCount = 0;
        this.mCreatedCount = 0;
        this.mFixedInterval = 0;
        this.mIntervalCount = 0;
        this.mCreatedCountMax = 0;
    }
}