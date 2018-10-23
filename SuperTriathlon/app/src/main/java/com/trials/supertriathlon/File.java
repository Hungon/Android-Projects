package com.trials.supertriathlon;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by USER on 2/4/2016.
 */
public class File {
    // static variables
    public enum FILE_MODE {
        FILE_READ,
        FILE_WRITE,
        FILE_ERROR,
    }

    // filed
    private FILE_MODE       mFileMode;
    private BufferedReader  mBr;
    private InputStream     mIn;
    private OutputStream    mOut;
    private Context         mContext;

    /*
        Constructor
     */
    public File(Context activity) {
        this.mContext = activity;
        try {
            if (this.mOut != null) this.mOut.close();
            if (this.mIn != null) this.mIn.close();
            if (this.mBr != null) this.mBr.close();
        } catch (Exception e) {
        }
    }
    /*
        Initialize to read the file from local directory.
     */
    public void InitReadFileFromLocal(String fileName) {
         try {
             // read the file
             this.mIn = this.mContext.openFileInput(fileName);
             this.mBr = new BufferedReader(new InputStreamReader(this.mIn));
            // set mode
            this.mFileMode = FILE_MODE.FILE_READ;
            // initialize the filed to write
            this.mOut = null;
         } catch (Exception e) {
            this.mBr = null;
            this.mIn = null;
            this.mOut = null;
             // set error flag
            this.mFileMode = FILE_MODE.FILE_ERROR;
         }
    }

    /*
        Initialize to write the value to the file in local area.
     */
    public void InitWriteFileInLocal(String fileName) {
        try {
            // open the file
            this.mOut = this.mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            // set mode
            this.mFileMode = FILE_MODE.FILE_WRITE;
        } catch (Exception e) {
            // clear
            this.mBr = null;
            this.mIn = null;
            this.mOut = null;
            // set error
            this.mFileMode = FILE_MODE.FILE_ERROR;
        }
    }
    
    /*
        Close process
     */
    public void FileClose() {
        try {
            // when open the file
            if (this.mFileMode == FILE_MODE.FILE_READ) {
                // close inputStream
                this.mIn.close();
                this.mBr.close();
                this.mBr = null;
                this.mIn = null;
            } else if (this.mFileMode == FILE_MODE.FILE_WRITE) {
                // close outputStream
                this.mOut.close();
                this.mOut = null;
            }
        } catch (Exception e) {
        }
    }

    /*******************************************************************************
     * Each method to read the file.
     ******************************************************************************/
    /*
        Read string
     */
    public String ReadString() {
        // check error
        if (this.mFileMode == FILE_MODE.FILE_ERROR) return "";
        String res = new String();
        // convert words to string type
        try {
            String str;
            while(true) {
                str = this.mBr.readLine();
                if (str == null) break;
                res = res.concat(str);
            }
        } catch (Exception e) {
            return "0";
        }
        return res;
    }

    /*******************************************************************************
     * Each method to write the file.
     ******************************************************************************/
    /*
        Write string to byte
     */
    public boolean WriteStringToLocalFile(String str, int offSet) {
        // check error
        if (this.mFileMode == FILE_MODE.FILE_ERROR) return false;

        // convert string to byte
        byte by[] = str.getBytes();

        // convert number to words.
        try {
            this.mOut.write(by, offSet, by.length);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}