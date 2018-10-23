package com.trials.harmony;

import java.util.Random;

/**
 * Created by USER on 5/10/2016.
 */
public class Utility {
    // static variables
    // filed
    private Random mRandom;
    // to count
    private int    mIntervalCount;
    // the time which is counted process that make the interval
    private int    mMadeProcessCount;
    private int    mFixedInterval;
    /*
        Constructor
    */
    public Utility() {
        this.mRandom = new Random();
        this.mFixedInterval = this.mMadeProcessCount = this.mIntervalCount = 0;
    }
    public Utility(int fixedInterval) {
        this.mRandom = new Random();
        this.mMadeProcessCount = this.mIntervalCount = 0;
        this.mFixedInterval = fixedInterval;
    }
    /*
        To make the interval time to execute the process
        for repeatable
    */
    public boolean ToMakeTheInterval(int fixedTime) {
        this.mIntervalCount++;
        if (fixedTime < this.mIntervalCount) {
            this.mIntervalCount = 0;
            this.mMadeProcessCount++;
            return true;
        }
        return false;
    }
    public boolean ToMakeTheInterval() {
        this.mIntervalCount++;
        if (this.mFixedInterval < this.mIntervalCount) {
            this.mIntervalCount = 0;
            this.mMadeProcessCount++;
            return true;
        }
        return false;
    }
    /*
        To make the interval time to execute the process
        in the selected time.
    */
    public boolean ToMakeTheInterval(int fixedTime, int limitProcess) {
        if (limitProcess <= this.mMadeProcessCount) return false;
        this.mIntervalCount++;
        if (fixedTime < this.mIntervalCount) {
            this.mIntervalCount = 0;
            this.mMadeProcessCount++;
            return true;
        }
        return false;
    }
    /*
        Reset interval to make blank
    */
    public void ResetInterval() { this.mIntervalCount = 0; }
    /*
        Get random that positive-number
    */
    public int GetRandom(int num) {
        return (this.mRandom.nextInt() >>> 1) % num;
    }
    /*
        Get likelihood
    */
    public boolean GetLikelihood(int num) {
        int compare = this.GetRandom(100);
        return (compare < num);
    }
    /*
        Release
    */
    public void ReleaseUtility() {
        this.mRandom = null;
    }
    /*****************************************************
        Each getter functions
    ***************************************************/
    /*
        Get the time that made the process
    */
    public int GetMadeProcess() { return this.mMadeProcessCount; }
    /*****************************************************
        Each setter functions
     ***************************************************/
    /*
        Set fixed interval time
    */
    public void SetFixedInterval(int fixedTime) { this.mFixedInterval = fixedTime; }
}