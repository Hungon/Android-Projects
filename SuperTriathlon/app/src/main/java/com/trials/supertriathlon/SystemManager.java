package com.trials.supertriathlon;

import android.content.Context;
import android.util.Log;

/**
 * Created by Kohei Moroi on 11/26/2016.
 */

public class SystemManager implements HasRecords, HasProperties {
    // kind of file
    private static String FILES[][] = {
            { OFF_ROAD_RECORD_BEST_EASY,
                    OFF_ROAD_RECORD_BEST_NORMAL,
                    OFF_ROAD_RECORD_BEST_HARD }, // off-road
            { ROAD_RECORD_BEST_EASY,
                    ROAD_RECORD_BEST_NORMAL,
                    ROAD_RECORD_BEST_HARD },     // road
            { SEA_RECORD_BEST_EASY,
                    SEA_RECORD_BEST_NORMAL,
                    SEA_RECORD_BEST_HARD },      // sea
    };
    // new record is up to renewal or not.
    public enum RECORD_RENEWAL {
        NEW_RECORD,
        NOT_RENEWAL,
    }
    private final static String FILE_USER_INFO = "userInfo";
    private final static int USER_INFO_NAME = 0;
    private final static int USER_INFO_ID = 1;
    private final static int USER_INFO_PREVIEW_RANK = 2;
    private final static int USER_INFO_KIND = 3;

    private Context mContext;
    private static String sUserName = "";
    private static int sUserId = 0;
    private static boolean sUserExists = false;

    public SystemManager(Context context) {
        this.mContext = context;
    }
    public boolean retrieveUserInfo() throws NullPointerException{
        Record record = new Record(this.mContext);
        String info[] = record.loadFile(FILE_USER_INFO,USER_INFO_KIND);
        try {
            if (info[USER_INFO_ID] != null) {
                sUserName = info[USER_INFO_NAME];
                sUserId = Integer.parseInt(info[USER_INFO_ID]);
                sUserExists = !(sUserId == 0);
                return sUserExists;
            } else {
                sUserExists = false;
            }
        } catch(NullPointerException pe){
            Log.e(SystemManager.class.getSimpleName(),pe.getMessage());
        }
        return false;
    }

    public int[] getTheRecords(int stageNum, int level) {
        Record record = new Record(this.mContext);
        return record.LoadRecordFileFromLocal(FILES[stageNum][level],RECORD_KIND);
    }
    public RECORD_RENEWAL[] updateBestRecord(int stageNum,int level,int[] item) {
        RECORD_RENEWAL update[] = new RECORD_RENEWAL[item.length];
        for (int i = 0; i < update.length; i++) update[i] = RECORD_RENEWAL.NOT_RENEWAL;
        boolean updateF = false;
        Record record = new Record(this.mContext);
        // get best record
        // if there isn't the file, return values are 0.
        int best[] = record.LoadRecordFileFromLocal(FILES[stageNum][level],item.length);
        // to compare best record with current record.
        for (int i = 0; i < item.length; i++) {
            // to compare between old record and new record.
            if (best[i] < item[i]) {
                best[i] = item[i];
                update[i] = RECORD_RENEWAL.NEW_RECORD;
                updateF = true;
            }
        }
        // to update best record
        if (updateF) record.SaveRecordToLocalFile(FILES[stageNum][level],best,item.length);
        return update;
    }
    public void resetTheBestRecord(int stageNum, int level) {
        int reset[] = new int[RECORD_KIND];
        Record record = new Record(this.mContext);
        for (int i = 0; i < reset.length; i++) reset[i] = 0;
        // reset all record
        for (int i = 0; i < RECORD_KIND; i++) {
            record.SaveRecordToLocalFile(FILES[stageNum][level], reset, RECORD_KIND);
        }
    }
    public void resetAllTheBestRecords(int stageNum) {
        int levels[] = {LEVEL_EASY,LEVEL_NORMAL,LEVEL_HARD};
        for (int l:levels) {
            this.resetTheBestRecord(stageNum,l);
        }
    }
    public void saveUserInfo(String name, int id) {
        Record record = new Record(this.mContext);
        record.saveUserInfo(FILE_USER_INFO, name, id);
        sUserName = name;
        sUserId = id;
    }


    // Setter functions
    public static void setUserName(String name) { sUserName = name; }
    public static void setUserId(int id) { sUserId = id; }
    public static void setUserExists(boolean newUser) { sUserExists = newUser; }
    // Getter functions
    public static String getUserName() { return sUserName; }
    public static int getUserId() { return sUserId; }
    public static boolean getUserExists() { return sUserExists; }

    /* The class to save record */
    private class Record implements HasRecords {
        // static variables
        // filed
        private File            mFile;

        /*
            Constructor
        */
        private Record(Context activity) {
            // file class
            this.mFile = new File(activity);
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
            this.mFile.InitReadFileFromLocal(fileName+".txt");
            // convert string to char
            String str = this.mFile.ReadString();
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
            this.mFile.FileClose();
            return record;
        }
        public String[] loadFile(String fileName, int itemMax) {

            // for return
            String res[] = new String[itemMax];
            // for copy with the record
            char[] detect;
            // read the file
            this.mFile.InitReadFileFromLocal(fileName+".txt");
            // convert string to char
            String str = this.mFile.ReadString();
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
                    res[itemCount] = ch;
                    itemCount++;        // item to next
                    ch = "";            // reset ch
                    if (itemCount == itemMax) break;
                    continue;
                }
                // convert char to string and catch each other
                ch = ch.concat(String.valueOf(d));
            }
            // close file
            this.mFile.FileClose();
            return res;
        }
        /*
            Save record to the file in local area
        */
        public boolean SaveRecordToLocalFile(String fileName, int[] record, int item) {
            if (record.length < item) return false;
            // initialize file class
            this.mFile.InitWriteFileInLocal(fileName+".txt");
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
            this.mFile.WriteStringToLocalFile(save,0);
            // close file
            this.mFile.FileClose();
            return true;
        }
        public boolean saveUserInfo(String fileName,String userName,int userId) {
            // initialize file class
            this.mFile.InitWriteFileInLocal(fileName+".txt");
            // for write
            String save = "";
            // save record
            save = save.concat(userName+"/");
            save = save.concat(Integer.toString(userId)+"/");
            // to write record
            this.mFile.WriteStringToLocalFile(save,0);
            // close file
            this.mFile.FileClose();
            return true;
        }
    }
}
