package com.trials.harmony;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.support.annotation.RequiresPermission;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by USER on 1/31/2016.
 */
public class Talk implements HasMessageFrame {

    // static variables
    // the process of talk
    public final static int READY              = -1;
    public final static int READING            = 0;
    public final static int FINISH_TO_READ     = 1;
    public final static int DELETE             = 2;
    public final static int DISPLAYED          = 3;
    public final static int PAUSE              = 4;
    // each font size
    private final static int     FONT_SMALL_SIZE            = 10;
    private final static int     FONT_MEDIUM_SIZE           = 18;
    private final static int     FONT_LARGE_SIZE            = 36;
    private final static int     WAIT_DEFAULT_FRAME         = 3;
    // to words drawing process
    private int             mFontSize;
    private int             mFontDefaultSize;
    private int             mFontColour;
    private int             mFontDefaultColour;
    private char[]          mRawText;
    private int             mTextIndex;
    private int             mStartWord;
    private int             mDisplayWordCount;
    private int             mDisplayWordLimitCount;
    private int             mDisplayInterval;
    private boolean         mIsDisplaying;
    private boolean         mRestartF;
    private int             mProcess;
    private Point           mTextPos;
    private int             mWaitingFrame = WAIT_DEFAULT_FRAME;
    // the count to make the interval
    private int             mIntervalCount;
    // the interval to make the blank time
    private int             mFixedInterval;
    private Context         mContext;
    private Image           mImage;
    // for balloon's
    private CharacterEx     mBalloon[];
    // showing text
    private String          mShowingText[];

    // Constructor
    public Talk(Context context, Image image) {
        this.mContext = context;
        this.mImage = image;
        // to allot the memory for balloon
        this.mBalloon = new CharacterEx[2];
        for (int i = 0; i < this.mBalloon.length; i++) {
            this.mBalloon[i] = new CharacterEx(context, image);
        }
    }

    /*
        Create Text
    */
    public void CreateTalkFromLocalFile(
            String filePath, Point offsetPos,
            int frame, int fixedInterval, int balloonType) throws Exception
    {
        String str;                 // to read words.
        String mes = "";            // to catch each words.
        InputStream inputStream;
        BufferedReader bufferedReader;
        try {
            // load the file.
            inputStream = this.mContext.getAssets().open(filePath+".txt");
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
            this.mProcess = DELETE;
        }
        this.ResetTalk();
        // to convert String type to char type.
        this.mRawText = mes.toCharArray();
        // reset index.
        this.mFixedInterval = fixedInterval;
        // set offset to draw text.
        this.mTextPos.x = offsetPos.x;
        this.mTextPos.y = offsetPos.y;
        // default frame
        if (frame < 2) this.mWaitingFrame = WAIT_DEFAULT_FRAME;
        this.mWaitingFrame = frame;
        // to diverge the initialization from the type
        int alpha[] = {250,100};
        Point screen = MainView.GetScreenSize();
        for (int i = 0; i < this.mBalloon.length; i++) {
            this.mBalloon[i].InitCharacterEx(
                    BALLOON_FILE_NAME[balloonType],
                    (screen.x-BALLOON_SIZE[balloonType].x)>>1,offsetPos.y-60,
                    BALLOON_SIZE[balloonType].x,BALLOON_SIZE[balloonType].y,
                    0,BALLOON_SIZE[balloonType].y*i,
                    alpha[i],0.0f,balloonType);
            this.mBalloon[i].mExistFlag = false;
        }
    }
    /*
        Create Text
    */
    public void CreateTalkByRawText(
            String sentence, Point offsetPos,
            int frame, int fixedInterval,
            int balloonType)
    {
        // Reset messages.
        this.ResetTalk();
        // to convert String type to char type.
        this.mRawText = sentence.toCharArray();
        this.mFixedInterval = fixedInterval;
        // set offset to draw text.
        this.mTextPos.x = offsetPos.x;
        this.mTextPos.y = offsetPos.y;
        // default frame
        if (frame < 2) this.mWaitingFrame = WAIT_DEFAULT_FRAME;
        this.mWaitingFrame = frame;
        // to set the balloon
        int alpha[] = {250,100};
        Point screen = MainView.GetScreenSize();
        for (int i = 0; i < this.mBalloon.length; i++) {
            this.mBalloon[i].InitCharacterEx(
                    BALLOON_FILE_NAME[balloonType],
                    (screen.x-BALLOON_SIZE[balloonType].x)>>1,offsetPos.y-60,
                    BALLOON_SIZE[balloonType].x,BALLOON_SIZE[balloonType].y,
                    0,BALLOON_SIZE[balloonType].y*i,
                    alpha[i],1.0f,balloonType);
            this.mBalloon[i].mExistFlag = false;
        }
    }

    /*
        Update Talk
    */
    public int UpdateTalk() {
        // when count time reached to fixed interval time
        // to show the texts.
        if (this.mProcess == READY) {
            // count
            this.mIntervalCount++;
            if (this.mFixedInterval < this.mIntervalCount) {
                // to show the texts and balloon.
                this.mIsDisplaying = true;
                for (CharacterEx f:this.mBalloon) f.mExistFlag = true;
                this.mProcess = READING;
            }
        }
        if (!this.mIsDisplaying) {
            // to update the balloon
            for (CharacterEx f:this.mBalloon) {
                if (f.mExistFlag) {
                    f.VariableScaleWhenReachedTheFixedValueNoExistence(-0.1f,0.0f);
                }
            }
            return READY;
        } else {
            // to update the balloon
            for (CharacterEx f:this.mBalloon) {
                if (f.mExistFlag) {
                    f.VariableScale(0.1f,1.0f);
                }
            }
        }
        // to diverge the process from the current variable
        // when reading enabled, to talking process.
        if (this.mProcess == READING) {
            this.ReadTalk();
        }
        else if (this.mProcess == FINISH_TO_READ){
            this.WaitTalk();
        }
        this.mDisplayInterval++;       // to make the interval to show the text
        // to increase count after selected frame has passed.
        if (this.mDisplayWordCount < this.mDisplayWordLimitCount) {
            if ((this.mDisplayInterval % this.mWaitingFrame == 1)) {
                this.mDisplayWordCount++;
            }
        } else {
            return DISPLAYED;
        }
        return this.mProcess;
    }

    /*
        Draw text
    */
    public void DrawTalk(int leadingX, int leadingY) {
        if (!this.mIsDisplaying) return;
        // to show the balloon behind texts
        for (CharacterEx f:this.mBalloon) f.DrawCharacterEx();
        int wordCount = 0;
        Point dstPos = new Point();
        // set coordination to draw word.
        dstPos.x = this.mTextPos.x;
        dstPos.y = this.mTextPos.y;
        // font size
        float adjustW;

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
                        this.mFontColour = this.mFontDefaultColour;
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
                        this.mFontColour = Color.YELLOW;
                        break;
                    // not to use drawing word.
                    case 'e':
                    case 'd':
                    case 'p':           // it means 'pause'.
                    case 'w':
                    default:
                }
                continue;
            }
            // adjust leading
            adjustW = this.adjustCharacter(this.mRawText[i],this.mFontSize);
            // Draw words.
            this.mImage.DrawChar(this.mRawText, dstPos.x, dstPos.y, this.mFontSize, this.mFontColour, i);
            dstPos.x += this.mFontSize-(leadingX+(int)Math.abs(adjustW));
            // when the desperation-X is over the frame image's width,
            // go to new line
            if (this.mBalloon[0].mPos.x+(this.mBalloon[0].mSize.x*this.mBalloon[0].mScale) < dstPos.x) {
                dstPos.x = this.mTextPos.x;
                dstPos.y += this.mFontSize-leadingY;
            }
            // increase word count.
            wordCount++;
            // when reach to end, to finish reading.
            if (this.mDisplayWordCount <= wordCount) break;
        }
    }
    /*
        Release
    */
    public void ReleaseTalk() {
        if (this.mBalloon != null) {
            for (int i = 0; i < this.mBalloon.length; i++) {
                this.mBalloon[i].ReleaseCharacterEx();
                this.mBalloon[i] = null;
            }
        }
    }
    /*
        Reset setting
     */
    private void ResetTalk() {
        this.mStartWord = 0;
        this.mDisplayWordLimitCount = this.mDisplayWordCount = this.mTextIndex = 0;
        this.mIntervalCount = this.mDisplayInterval = 0;
        this.mFixedInterval = 0;
        this.mWaitingFrame = 0;
        this.mProcess = READY;
        this.mIsDisplaying = false;
        this.mRestartF = false;
        this.mTextPos = new Point();
        if (this.mRawText != null) this.mRawText = null;
        if (this.mShowingText != null ) this.mShowingText = null;
    }

    /*
        Read words.
    */
    private void ReadTalk() {
        int index = 0;
        boolean exit = false;
        char copy[][] = new char[10][256];
        int line = 0;
        // to reset showing text
        if (this.mShowingText != null ) this.mShowingText = null;
        // to read word from the file.
        do {
            if (this.mRawText.length <= this.mTextIndex) {
                this.mProcess = DELETE;
                exit = true;
            } else {       // if find a special word.
                if (this.mRawText[this.mTextIndex] == '\\') {
                    this.mTextIndex++;
                    index++;
                    exit = this.SpecialWordProcess();
                    // if detecting the special word that means waiting,
                    // to next line
                    if (this.mRawText[this.mTextIndex] == 'w') {
                        line++;
                        index = 0;
                    }
                // if not to find a special word.
                } else {
                    // to increase the count to limit showing word
                    this.mDisplayWordLimitCount++;
                    copy[line][index] = this.mRawText[this.mTextIndex];
                }
            }
            index++;
            this.mTextIndex++;
        } while (!exit);
        // to allot memory
        this.mShowingText = new String[line+1];
        // convert text of char into string
        for (int i = 0; i < this.mShowingText.length; i++) {
            this.mShowingText[i] = String.valueOf(copy[i], 0, index);
        }
    }
    /*
        The process based on a special word.
    */
    private boolean SpecialWordProcess() {
        boolean processed = false;
        // the process of a special word.
        switch(this.mRawText[this.mTextIndex]) {
            case 'd' :          // delete
                processed = true;
                this.mProcess = DELETE;
                break;
            case 'e' :          // end to draw text and file to next.
                this.mProcess = FINISH_TO_READ;
                processed = true;
                break;
            case 'p' :          // end to draw text and file to next.
                this.mProcess = PAUSE;
                processed = true;
                break;
        }
        return processed;
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
        this.mDisplayWordLimitCount = this.mDisplayWordCount = 0;
        this.mIntervalCount = this.mDisplayInterval = 0;
        this.mProcess = READING;
        this.mRestartF = false;
    }
    /*
        adjust font size
    */
    private float adjustCharacter(char word, int fontSize) {
        switch(word) {
            // lower case
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
            // upper case
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
    /**************************************************************
        Each setter functions
    *************************************************************/
    /*
        Set Restart reading flag.
    */
    public void RestartReading() {
        // when the current process is pause, to set the process to restart reading
        if (this.mProcess == PAUSE) {
            this.mProcess = FINISH_TO_READ;
        }
        this.mRestartF = true;
    }
    /*
        Switch to display the window message
    */
    public void SwitchShowingWindowMessage(boolean showing) { this.mIsDisplaying = showing; }
    // Set font size as default
    public void SetFontSizeAsDefault(int size) { 
        this.mFontDefaultSize = this.mFontSize = size; 
    }
    // Set font color as default
    public void SetFontColourAsDefault(int colour) {
        this.mFontColour = this.mFontDefaultColour = colour;
    }
    /*******************************************************************
        Each getter functions
    *****************************************************************/
    /*
        Get the text that showing
    */
    public String[] GetShowingText() {
        String res[] = new String[1];
        return (this.mShowingText == null) ? res : this.mShowingText;
    }
    boolean getIsDisplaying() { return this.mIsDisplaying; }
}