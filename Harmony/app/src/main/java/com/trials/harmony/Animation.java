package com.trials.harmony;

import android.graphics.Point;

/**
 * Created by USER on 1/31/2016.
 */
public class Animation {

    // filed
    public Point        m_StartPic;
    public Point        m_Size;
    public int          m_CountMax;
    public int          m_Count;
    public int          m_Frame;
    public int          m_Time;
    public int          m_Type;
    public boolean      m_ReverseFlag;
    public int          m_Direction;
    protected Point     m_OriginPos;

    /*
        Constructor
     */
    public Animation() {
        this.m_StartPic = new Point(0, 0);
        this.m_Size = new Point(0, 0);
        this.m_CountMax = 0;
        this.m_Count = 0;
        this.m_Frame = 0;
        this.m_Time = 0;
        this.m_Type = 0;
        this.m_OriginPos = new Point(0,0);
        this.m_ReverseFlag = false;
        this.m_Direction = 0;
    }

    /*
        Initialize animation value
     */
    public void SetAnimation(
            int srcX, int srcY,
            int w, int h,
            int countMax,
            int frame,
            int type
    ) {
        this.m_StartPic.x = srcX;
        this.m_StartPic.y = srcY;
        this.m_Size.x = w;
        this.m_Size.y = h;
        this.m_CountMax = countMax;
        this.m_Frame = frame;
        this.m_Type = type;
    }

    /*
        Update animation process.
     */
    public boolean UpdateAnimation(Point src, boolean reverseFlag) {

        boolean ret = true;
        this.m_Time++;          // count time

        if (!this.m_ReverseFlag) {
            if (this.m_Time > this.m_Frame) {
                this.m_Count++;         // next animation
                this.m_Time = 0;        // reset time.
            }
            if (this.m_Count >= this.m_CountMax) {
                if (!reverseFlag) {
                    this.m_Count = 0;
                    ret = false;
                } else {
                    this.m_ReverseFlag = true;
                }
            }
        }
        if(this.m_ReverseFlag) {      // subtract count to reverse animation.
            if(this.m_Time > this.m_Frame) {
                this.m_Count--;
                this.m_Time = 0;
            }
            if (this.m_Count <= 0) {
                ret = this.m_ReverseFlag = false;
            }
        }
        // update animation
        src.x = this.m_StartPic.x + (this.m_Count * this.m_Size.x);
        src.y = this.m_StartPic.y + (this.m_Direction * this.m_Size.y);
        return ret;
    }

    /*
        Rest animation
     */
    public void ResetAnimation() {
        this.m_StartPic.x = 0;
        this.m_StartPic.y = 0;
        this.m_Count = 0;
        this.m_Time = 0;
        this.m_OriginPos.x = 0;
        this.m_OriginPos.y = 0;
        this.m_ReverseFlag = false;
        this.m_Direction = 0;
    }
    /*
        All reset the setting
    */
    public void ResetEveryAnimationSetting() {
        this.m_StartPic = new Point(0, 0);
        this.m_Size = new Point(0, 0);
        this.m_CountMax = 0;
        this.m_Count = 0;
        this.m_Frame = 0;
        this.m_Time = 0;
        this.m_Type = 0;
        this.m_OriginPos = new Point(0,0);
        this.m_ReverseFlag = false;
        this.m_Direction = 0;
    }
}