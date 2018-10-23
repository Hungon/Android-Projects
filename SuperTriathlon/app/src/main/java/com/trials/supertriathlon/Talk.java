package com.trials.supertriathlon;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by USER on 1/31/2016.
 */
public class Talk {

    // static variables
    private final static int     FONT_SMALL_SIZE    = 10;
    private final static int     FONT_MEDIUM_SIZE   = 24;
    private final static int     FONT_LARGE_SIZE    = 40;
    private final static int     WAIT_FRAME         = 3;

    // to words drawing process
    private int             mFontSize;
    private int             mFontDefaultSize;
    private int             mFontColor;
    private int             mFontDefaultColor;
    private char[]          mRawText;
    private int             mTextIndex;
    private int             mStartWord;
    private int             mDisplayWord;
    private int             mDisplayCount;
    private boolean         mReadingF;
    private boolean         mDisplayF;
    private boolean         mDeleteF;
    private boolean         mRestartF;
    private boolean         mWaitingF;
    private Point           mTextPos;

    private Context         mContext;
    private Image           mImage;

    /*
        Constructor
     */
    public Talk(Context activity, Image image, int fontSize, int color) {
        this.mContext = activity;
        this.mImage = image;
        this.mFontDefaultSize = this.mFontSize = fontSize;
        this.mFontDefaultColor = this.mFontColor = color;
        // the flag that restart reading text.
        this.mRestartF = false;
        // the flag that is waiting reading to next text.
        this.mWaitingF = false;
    }

    /*
        Create Text
    */
    public void CreateTalk(String filePath, Point offsetPos) throws Exception
    {
        // during drawing, back to called process.
        if (this.mDisplayF) return;
        // Reset messages.
        this.ResetTalk();

        String str;                              // to read words.
        String mes = new String();               // to catch each words.
        InputStream inputStream;
        BufferedReader bufferedReader;
        try {
            // load the file.
            inputStream = this.mContext.getAssets().open(filePath);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            // ro read word.
            while(true) {
                // to read a line from the file.
                str = bufferedReader.readLine();
                // when read to reach end, to finish.
                if (str == null) break;
                // to catch words.
                mes = mes.concat(str);
            }
            // close file
            bufferedReader.close();
            inputStream.close();
        } catch (Exception e) {
            System.out.println("Failed to load the file.");
            e.printStackTrace();
            // to finish talking process.
            this.mDeleteF = true;
        }

        // to convert String type to char type.
        this.mRawText = mes.toCharArray();
        // reset index.
        this.mTextIndex = 0;
        // to enable to read and draw.
        this.mReadingF = true;
        this.mDisplayF = true;
        // set offset to draw text.
        this.mTextPos.x = offsetPos.x;
        this.mTextPos.y = offsetPos.y;
    }
    /*
        Create Text
    */
    public void CreateTalkEx(String mes, Point offsetPos) {
        // during drawing, back to called process.
        if (this.mDisplayF) return;
        // Reset messages.
        this.ResetTalk();
        // to convert String type to char type.
        this.mRawText = mes.toCharArray();
        // reset index.
        this.mTextIndex = 0;
        // to enable to read and draw.
        this.mReadingF = true;
        this.mDisplayF = true;
        // set offset to draw text.
        this.mTextPos.x = offsetPos.x;
        this.mTextPos.y = offsetPos.y;
    }

    /*
        Update Talk
     */
    public boolean UpdateTalk() {

        // when don't display text
        if (!this.mDisplayF) return false;
        // when execute to delete text, to delete.
        if (this.mDeleteF) {
            this.DeleteTalk();
            return false;
        }
        // when reading enabled, to talking process.
        if (this.mReadingF) {
            this.ReadTalk();
        } else if (this.mWaitingF){
            this.WaitTalk();
        }
        return true;
    }

    /*
        Draw text
     */
    public void DrawTalk(int leadingX, int leadingY) {

        if (!this.mDisplayF) return;

        int wordCount = 0;
        Point dstPos = new Point();

        // set coordination to draw word.
        dstPos.x = this.mTextPos.x;
        dstPos.y = this.mTextPos.y;

        // to start to read word from buffer.
        for (int i = this.mStartWord; i < this.mRawText.length; i++) {

            // if there is a special word, to execute process based on the special word.
            if (this.mRawText[i] == '\\') {
                i++;        // to increase count and to find a special word.
                // diverge process from a special word.
                switch (this.mRawText[i]) {
                    case 'n':          // to next line
                        dstPos.x = this.mTextPos.x;
                        dstPos.y += this.mFontSize-leadingY;
                        // font size back default
                        this.mFontSize = this.mFontDefaultSize;
                        // font color back to default
                        this.mFontColor = this.mFontDefaultColor;
                        break;
                    case 'l':          // font-size is large.
                        this.mFontSize = FONT_LARGE_SIZE;
                        break;
                    case 'm':          // font-size is medium.
                        this.mFontSize = FONT_MEDIUM_SIZE;
                        break;
                    case 's':          // font-size is small.
                        this.mFontSize = FONT_SMALL_SIZE;
                        break;
                    case 'y':           // color to yellow
                        this.mFontColor = Color.YELLOW;
                        break;
                    // not to use drawing word.
                    case	'e':
                    case	'd':
                    default:
                        System.out.println("The special word isn't defined." + this.mRawText[i]);
                }
                continue;
            }

            // Draw words.
            this.mImage.DrawChar(this.mRawText, dstPos.x, dstPos.y, this.mFontSize, this.mFontColor, i);

            dstPos.x += this.mFontSize-leadingX;
            // increase word count.
            wordCount++;
            // when reach to end, to finish reading.
            if (wordCount >= this.mDisplayWord) break;
        }
    }

    /*
        Reset setting
     */
    private void ResetTalk() {
        this.mRawText = null;
        this.mStartWord = 0;
        this.mDisplayWord = 0;
        this.mDisplayCount = 0;
        this.mDisplayF = false;
        this.mReadingF = false;
        this.mDeleteF = false;
        this.mRestartF = false;
        this.mWaitingF = false;
        this.mTextPos = new Point();
    }

    /*
        Delete text
     */
    private void DeleteTalk() {
        // if there is \e that special word, file to next.
        if (this.mTextIndex < this.mRawText.length) {
            // get next file path.
            String str = new String(this.mRawText, this.mTextIndex,
                    this.mRawText.length - this.mTextIndex);
            // create talking process by the file.
            this.mDisplayF = false;
            try {
                this.CreateTalk(str, this.mTextPos);
            } catch (Exception e) {
            }
        } else {
            this.mDeleteF = false;
            this.mDisplayF = false;
        }
    }

    /*
        Read words.
     */
    private void ReadTalk() {
        // count frame
        this.mDisplayCount++;

        // after selected frame has passed.
        if ((this.mDisplayCount % WAIT_FRAME == 1)) {
            boolean exit = false;
            // to read word from the file.
            do {
                if (this.mRawText.length <= this.mTextIndex) {
                    this.EndTalk();
                    exit = true;
                } else {       // if found a special word.
                    if (this.mRawText[this.mTextIndex] == '\\') {
                        this.mTextIndex++;
                        exit = this.SpecialWordProcess();
                    }   // if not found a special word.
                    else {
                        this.mDisplayWord++;
                    }
                    exit = true;
                }
                this.mTextIndex++;
            } while (!exit);
        }
    }

    /*
        Skip text.
     */
    private void SkipTalk() {
        // if finished to skip, to restart to read.
        if (!this.mReadingF) {
            this.RestartTalk();
            return;
        }

        boolean exit = false;

        // to read word from the file.
        do {
            if (this.mRawText.length <= this.mTextIndex) {
                this.EndTalk();
                exit = true;
            } else {       // if found a special word.
                if (this.mRawText[this.mTextIndex] == '\\') {
                    this.mTextIndex++;
                    exit = this.SpecialWordProcess();
                }   // if not found a special word.
                else {
                    this.mDisplayWord++;
                }
            }
            this.mTextIndex++;
        } while (!exit);
    }

    /*
        The process based on a special word.
     */
    private boolean SpecialWordProcess() {
        boolean processed = false;

        // the process of a special word.
        switch(this.mRawText[this.mTextIndex]) {
            case 'd' :          // delete
                this.mDeleteF = processed = true;
                break;
            case 'e' :          // end to draw text and file to next.
                this.mReadingF = false;
                // the flag that is waiting reading to next text.
                this.mWaitingF = true;
                processed = true;
                break;
        }
        return processed;
    }
    /*
        End to draw text.
     */
    private void EndTalk() {
        this.mDeleteF = true;
        this.mReadingF = false;
    }
    /*
        Wait to draw text.
     */
    private void WaitTalk() {
        // to restart as touched screen.
        if (this.mRestartF) this.RestartTalk();
    }
    /*
        Restart talking
    */
    private void RestartTalk() {
        this.mStartWord = this.mTextIndex;
        this.mDisplayWord = 0;
        this.mDisplayCount = 0;
        this.mReadingF = true;
        this.mRestartF = false;
        this.mWaitingF = false;
    }

    /*
        Set Restart reading flag.
     */
    public void SetRestartReading(boolean restartF) { this.mRestartF = restartF; }

    /*
        Set reading point that beginning position
     */
    public void SetBackToBeginning() {
        // starting word
        this.mStartWord = 0;
        // the words count that is displaying
        this.mDisplayWord = 0;
        this.mDisplayCount = 0;
        // reset index.
        this.mTextIndex = 0;
        // to enable to read and draw.
        this.mReadingF = true;
        this.mDisplayF = true;
        this.mWaitingF = false;
        // font size back default
        this.mFontSize = this.mFontDefaultSize;
        // font color back to default
        this.mFontColor = this.mFontDefaultColor;
    }

    /*
        Get the flag that is waiting reading to next text.
     */
    public boolean GetWaitingFlag() { return this.mWaitingF; }

    /*
        Change font size
    */
    private float AdjustLeading(char word, int fontSize) {
        switch(word) {
            // small size
            case 'a':
                fontSize /= 3;
                break;
            case 'b':
                fontSize /= 3;
                break;
            case 'c':
                fontSize /= 3;
                break;
            case 'd':
                fontSize /= 3;
                break;
            case 'e':
                fontSize /= 3;
                break;
            case 'f':
                fontSize /= 2;
                break;
            case 'g':
                fontSize /= 3;
                break;
            case 'h':
                fontSize /= 3;
                break;
            case 'i':
                fontSize /= 2;
                break;
            case 'j':
                fontSize /= 2;
                break;
            case 'k':
                fontSize /= 3;
                break;
            case 'l':
                fontSize /= 2;
                break;
            case 'm':
                return 0.0f;
            case 'n':
                fontSize /= 3;
                break;
            case 'o':
                fontSize /= 3;
                break;
            case 'p':
                fontSize /= 3;
                break;
            case 'q':
                fontSize /= 3;
                break;
            case 'r':
                fontSize /= 2;
                break;
            case 's':
                fontSize /= 3;
                break;
            case 't':
                fontSize /= 2;
                break;
            case 'u':
                fontSize /= 3;
                break;
            case 'v':
                fontSize /= 3;
                break;
            case 'w':
                return 0.0f;
            case 'x':
                fontSize /= 3;
                break;
            case 'y':
                fontSize /= 3;
                break;
            case 'z':
                fontSize /= 3;
                break;
            // big size
            case 'A':
                fontSize /= 4;
                break;
            case 'B':
                fontSize /= 4;
                break;
            case 'C':
                fontSize /= 4;
                break;
            case 'D':
                fontSize /= 4;
                break;
            case 'E':
                fontSize /= 4;
                break;
            case 'F':
                fontSize /= 4;
                break;
            case 'G':
                fontSize /= 4;
                break;
            case 'H':
                fontSize /= 5;
                break;
            case 'I':
                fontSize /= 2;
                break;
            case 'J':
                fontSize /= 4;
                break;
            case 'K':
                fontSize /= 4;
                break;
            case 'L':
                fontSize /= 3;
                break;
            case 'M':
                return 0.0f;
            case 'N':
                fontSize /= 4;
                break;
            case 'O':
                fontSize /= 4;
                break;
            case 'P':
                fontSize /= 4;
                break;
            case 'Q':
                fontSize /= 4;
                break;
            case 'R':
                fontSize /= 4;
                break;
            case 'S':
                fontSize /= 4;
                break;
            case 'T':
                fontSize /= 4;
                break;
            case 'U':
                fontSize /= 4;
                break;
            case 'V':
                fontSize /= 4;
                break;
            case 'W':
                return 0.0f;
            case 'X':
                fontSize /= 4;
                break;
            case 'Y':
                fontSize /= 4;
                break;
            case 'Z':
                fontSize /= 4;
                break;
            default:        // space-X
                return fontSize/2;
        }
        return fontSize;
    }
}