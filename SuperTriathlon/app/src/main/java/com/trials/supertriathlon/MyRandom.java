package com.trials.supertriathlon;

import java.util.Random;

/**
 * Created by USER on 1/31/2016.
 */
public class MyRandom {
    // get random.
    public static Random GetRandom = new Random();
    /*
        Get random that positive-number
    */
    public static int GetRandom(int num) {
        return (GetRandom.nextInt() >>> 1) % num;
    }
    /*
        Get likelihood
    */
    public static boolean GetLikelihood(int num) {
        int compare = GetRandom(100);
        if (compare < num) return true;
            return false;
    }
}