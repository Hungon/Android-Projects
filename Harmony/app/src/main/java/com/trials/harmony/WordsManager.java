package com.trials.harmony;

import java.util.Locale;

/**
 * Created by Kohei Moroi on 10/1/2016.
 */

class WordsManager {
    final static int CASE_UPPER = 0;
    final static int CASE_LOWER = 1;
    private static String convertStringCase(String src, int caseType) {
        String res = "";
        // when the case is upper,
        if (caseType == CASE_UPPER) {
            res = src.toUpperCase(Locale.getDefault());
        } else if (caseType == CASE_LOWER) {
            res = src.toLowerCase(Locale.getDefault());
        }
        return res;
    }
    static char convertCharacterCase(char c, int caseType) {
        String src = String.valueOf(c);
        char res[] = convertStringCase(src,caseType).toCharArray();
        return res[0];
    }
    static String convertNumberCase(int num) {
        String res[] = {
                "zero","one","two","three","four","five","siz","seven","eight","nine","ten",
                "eleven","twelve","thirteen","fourteen","fifteen","sixteen","seventeen","eighteen","nineteen",
                "twenty","twenty one","twenty two","twenty three","twenty four","twenty five","twenty six","twenty seven","twenty eight","twenty nine",
                "thirty","thirty one","thirty two","thirty three","thirty four","thirty five","thirty six","thirty seven","thirty eight","thirty nine",
                "forty"
        };
        if (num < 0 || res.length <= num) return "";
        return res[num];
    }
}
