package com.trials.harmony;

import android.content.Context;

/**
 * Created by USER on 2/5/2016.
 */
public class Record implements HasRecords {

    // static variables
    // new record is up to renewal or not.
    public enum RECORD_RENEWAL {
        NEW_RECORD,
        NOT_RENEWAL,
    }
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
    public int[] LoadRecordFileFromLocal(String fileName, int itemMax) {

        // for return
        int record[] = new int[itemMax];
        // for copy with the record
        char[] detect;
        // read the file
        this.m_File.InitReadFileFromLocal(fileName+".txt");
        // convert string to char
        String str = this.m_File.ReadString();
        detect = str.toCharArray();
        // item count and digit
        int itemCount = 0;
        // char to string
        String ch = "";

        // loop to count and to detect punctuate then substitute value to record array.
        for (char d: detect) {
            // when find out '/', to get record
            if (d == '/') {
                // save record
                record[itemCount] = Integer.parseInt(ch);
                itemCount++;        // item to next
                ch = "";            // reset ch
                if (itemCount == itemMax) break;
                continue;
            }
            // convert char to string and catch each other
            ch = ch.concat(String.valueOf(d));
        }
        // close file
        this.m_File.FileClose();
        return record;
    }
    /*
        Save record to the file in local area
    */
    public boolean SaveRecordToLocalFile(String fileName, int[] record, int item) {
        if (record.length < item) return false;
        // initialize file class
        this.m_File.InitWriteFileInLocal(fileName+".txt");
        // for convert int to string
        String get[] = new String[item];
        // for write
        String save = "";
        // save record
        for (int i = 0; i < item; i++) {
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
        To check to new record and then update the record or not,
    */
    public RECORD_RENEWAL[] UpdateBestRecord(String fileName, int[] record) {
        RECORD_RENEWAL update[] = new RECORD_RENEWAL[record.length];
        for (int i = 0; i < update.length; i++) update[i] = RECORD_RENEWAL.NOT_RENEWAL;
        boolean updateF = false;
        // get best record
        // if there isn't the file, return values are 0.
        int best[] = this.LoadRecordFileFromLocal(fileName,record.length);
        // to compare best record with current record.
        for (int i = 0; i < record.length; i++) {
            // to compare between old record and new record.
            if (best[i] < record[i]) {
                best[i] = record[i];
                update[i] = RECORD_RENEWAL.NEW_RECORD;
                updateF = true;
            }
        }
        // to update best record
        if (updateF) this.SaveRecordToLocalFile(fileName,best,record.length);
        return update;
    }
}