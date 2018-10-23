package com.trials.harmony;

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
    private FILE_MODE       m_FileMode;
    private BufferedReader  m_Br;
    private InputStream     m_In;
    private OutputStream    m_Out;
    private Context         m_Context;

    /*
        Constructor
     */
    public File() {
        this.m_Br = null;
        this.m_In = null;
        this.m_Out = null;
    }
    public File(Context activity) {
        this.m_Context = activity;
        try {
            if (this.m_Out != null) this.m_Out.close();
            if (this.m_In != null) this.m_In.close();
            if (this.m_Br != null) this.m_Br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*
        Initialize to read the file from local directory.
     */
    public void InitReadFileFromLocal(String fileName) {
         try {
             // read the file
             this.m_In = this.m_Context.openFileInput(fileName);
             this.m_Br = new BufferedReader(new InputStreamReader(this.m_In));
            // set mode
            this.m_FileMode = FILE_MODE.FILE_READ;
            // initialize the filed to write
            this.m_Out = null;
         } catch (Exception e) {
            e.printStackTrace();
            this.m_Br = null;
            this.m_In = null;
            this.m_Out = null;
             // set error flag
            this.m_FileMode = FILE_MODE.FILE_ERROR;
         }
    }

    /*
        Initialize to write the value to the file in local area.
     */
    public void InitWriteFileInLocal(String fileName) {
        try {
            // open the file
            this.m_Out = this.m_Context.openFileOutput(fileName, Context.MODE_PRIVATE);
            // set mode
            this.m_FileMode = FILE_MODE.FILE_WRITE;
        } catch (Exception e) {
            e.printStackTrace();
            // clear
            this.m_Br = null;
            this.m_In = null;
            this.m_Out = null;
            // set error
            this.m_FileMode = FILE_MODE.FILE_ERROR;
        }
    }
    
    /*
        Close process
     */
    public void FileClose() {
        try {
            // when open the file
            if (this.m_FileMode == FILE_MODE.FILE_READ) {
                // close inputStream
                this.m_In.close();
                this.m_Br.close();
                this.m_Br = null;
                this.m_In = null;
            } else if (this.m_FileMode == FILE_MODE.FILE_WRITE) {
                // close outputStream
                this.m_Out.close();
                this.m_Out = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        if (this.m_FileMode == FILE_MODE.FILE_ERROR) return "";
        String res = new String();
        // convert words to string type
        try {
            String str;
            while(true) {
                str = this.m_Br.readLine();
                if (str == null) break;
                res = res.concat(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        if (this.m_FileMode == FILE_MODE.FILE_ERROR) return false;

        // convert string to byte
        byte by[] = str.getBytes();

        // convert number to words.
        try {
            this.m_Out.write(by, offSet, by.length);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}