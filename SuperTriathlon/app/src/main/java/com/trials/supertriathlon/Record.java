package com.trials.supertriathlon;

import android.content.Context;

/**
 * Created by USER on 2/5/2016.
 */
public class Record {

    // static variables
    // the flag that renewal record.
    public enum RECORD_RENEWAL {
        NEW_RECORD,
        NOT_RENEWAL,
    }
    // kind of record
    public final static int    RECORD_TOTAL        = 0;
    public final static int    RECORD_TIME         = 1;
    public final static int    RECORD_CHAIN_MAX    = 2;
    public final static int    RECORD_KIND         = 3;

    // kind of file
    // off-road
    public final static String OFF_ROAD_RECORD_BEST_EASY    = "offroadbest_easy.txt";
    public final static String OFF_ROAD_RECORD_BEST_NORMAL  = "offroadbest_normal.txt";
    public final static String OFF_ROAD_RECORD_BEST_HARD    = "offroadbest_hard.txt";
    // road
    public final static String ROAD_RECORD_BEST_EASY        = "roadbest_easy.txt";
    public final static String ROAD_RECORD_BEST_NORMAL      = "roadbest_normal.txt";
    public final static String ROAD_RECORD_BEST_HARD        = "roadbest_hard.txt";
    // sea
    public final static String SEA_RECORD_BEST_EASY        = "seabest_easy.txt";
    public final static String SEA_RECORD_BEST_NORMAL      = "seabest_normal.txt";
    public final static String SEA_RECORD_BEST_HARD        = "seabest_hard.txt";

    // filed
    private File            m_File;

    /*
        Constructor
    */
    public Record(Context activity) {
        // file class
        this.m_File = new File(activity);
    }

    /*
        Load record file from local area
    */
    public int[] LoadRecordFileFromLocal(String fileName, int count) {

        // for return
        int record[] = new int[count];
        // for copy with the record
        char[] detect;
        // read the file
        this.m_File.InitReadFileFromLocal(fileName);
        // convert string to char
        String str = this.m_File.ReadString();
        detect = str.toCharArray();
        // item count and digit
        int itemCount = 0;
        // char to string
        String ch = new String();

        // loop to count and to detect punctuate then substitute value to record array.
        for (int i = 0; i < detect.length; i++) {
            // when find out '/', to get record
            if (detect[i] == '/') {
                // save record
                record[itemCount] = Integer.parseInt(ch);
                itemCount++;        // item to next
                ch = "";            // reset ch
                if (itemCount == count) break;
                continue;
            }
            // convert char to string and catch each other
            ch = ch.concat(String.valueOf(detect[i]));
        }
        // close file
        this.m_File.FileClose();
        return record;
    }

    /*
        Save record to the file in local area
    */
    public boolean SaveRecordToLocalFile(String fileName, int[] record, int count) {
        if (record.length < count) return false;
        // initialize file class
        this.m_File.InitWriteFileInLocal(fileName);
        // for convert int to string
        String get[] = new String[count];
        // for write
        String save = new String();
        // save record
        for (int i = 0; i < count; i++) {
            // get record
            get[i] = Integer.toString(record[i]);
            // add punctuate to item
            get[i] = get[i].concat("/");
            save = save.concat(get[i]);
        }
        // to write record
        this.m_File.WriteStringToLocalFile(save,0);
        // close file
        this.m_File.FileClose();
        return true;
    }

    /*
        Update best record
     */
    public RECORD_RENEWAL[] UpdateBestRecord(String fileName, int[] record) {

        RECORD_RENEWAL update[] = new RECORD_RENEWAL[RECORD_KIND];
        for (int i = 0; i < update.length; i++) update[i] = RECORD_RENEWAL.NOT_RENEWAL;
        boolean updateF = false;

        // get best record
        // if there isn't the file, return values are 0.
        int best[] = this.LoadRecordFileFromLocal(fileName,record.length);
        
        // to compare best record with current record.
        for (int i = 0; i < record.length; i++) {
            // except for time record
            if (RECORD_TIME != i) {
                // compare
                if (best[i] < record[i]) {
                    best[i] = record[i];
                    update[i] = RECORD_RENEWAL.NEW_RECORD;
                    updateF = true;
                }
            } else {    // time record
                if (record[i] < best[i] || best[RECORD_TIME] == 0){
                    best[i] = record[i];
                    update[i] = RECORD_RENEWAL.NEW_RECORD;
                    updateF = true;
                }
            }
        }
        // to update best record
        if (updateF) this.SaveRecordToLocalFile(fileName,best,record.length);
        return update;
    }

    /*
        Reset best record
     */
    public void ResetBestRecord(String fileName) {
        // get best record
        // get best record
        int reset[] = new int[RECORD_KIND];
        reset[0] = 0;                   // total
        reset[1] = 0;                   // time
        reset[2] = 0;                   // chain max
        // reset all record
        for (int i = 0; i < RECORD_KIND; i++) {
            this.SaveRecordToLocalFile(fileName, reset, RECORD_KIND);
        }
    }
}