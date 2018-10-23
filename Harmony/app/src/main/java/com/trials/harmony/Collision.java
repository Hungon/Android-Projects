package com.trials.harmony;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;

/**
 * Created by USER on 1/29/2016.
 */
public class Collision {
    /*
        To check to collision Touched position and selected object.
    */
    public static boolean CheckTouch(int x, int y, int w, int h, float scale) {

        float scaleW = w * scale;
        float scaleH = h * scale;
        // get touched position.
        int index = MainView.GetTouchIndex();
        Point touchPos = MainView.GetTouchedPosition(index);
        // check overlap between touched-position and object's position.
        if (x <= touchPos.x &&
            y <= touchPos.y &&
            touchPos.x <= x + scaleW &&
            touchPos.y <= y + scaleH) {
            return true;
        }
        return false;
    }

    /*
        Check to collision between two character that is Base character class.
     */
    public static boolean CollisionCharacter(CharacterEx ch1, CharacterEx ch2) {
        // ch1's size
        float ch1W = ch1.mSize.x * ch1.mScale;
        float ch1H = ch1.mSize.y * ch1.mScale;
        // ch2
        float ch2W = ch2.mSize.x * ch2.mScale;
        float ch2H = ch2.mSize.y * ch2.mScale;

        // check overlap between the two character's rectangular.
        if (ch1.mPos.x + Math.abs(ch1.mMove.x) < ch2.mPos.x + ch2W &&
            ch1.mPos.x + ch1W - Math.abs(ch1.mMove.x) > ch2.mPos.x &&
            ch1.mPos.y + Math.abs(ch1.mMove.y) < ch2.mPos.y + ch2H &&
            ch1.mPos.y + ch1H - Math.abs(ch1.mMove.y) > ch2.mPos.y) {
            return true;
        }
        return false;
    }

    /*
        Check to collision between two character that is Base character class.
    */
    public static boolean CollisionCharacter(CharacterEx ch1, CharacterEx ch2, Rect ch1Safety, Rect ch2Safety) {
        // ch1's size
        float ch1W = ch1.mSize.x * ch1.mScale;
        float ch1H = ch1.mSize.y * ch1.mScale;
        // ch2
        float ch2W = ch2.mSize.x * ch2.mScale;
        float ch2H = ch2.mSize.y * ch2.mScale;
        // safety area
        float Safety1[] = {
                ch1Safety.left*ch1.mScale,ch1Safety.top*ch1.mScale,
                ch1Safety.right*ch1.mScale,ch1Safety.bottom*ch1.mScale,
        };
        // safety area
        float Safety2[] = {
                ch2Safety.left*ch2.mScale,ch2Safety.top*ch2.mScale,
                ch2Safety.right*ch2.mScale,ch2Safety.bottom*ch2.mScale,
        };


        // check overlap between the two character's rectangular.
        if (ch1.mPos.x + Safety1[0] < ch2.mPos.x + ch2W - Safety2[2] &&
            ch1.mPos.x + ch1W - Safety1[2] > ch2.mPos.x + Safety2[0] &&
            ch1.mPos.y + Safety1[1] < ch2.mPos.y + ch2H - Safety2[3] &&
            ch1.mPos.y + ch1H - Safety1[3] > ch2.mPos.y + Safety2[1]) {
            return true;
        }
        return false;
    }
    /*
        Broaden collision area to forward
    */
    public static boolean BroadenCollisionAreaToForward(CharacterEx searcher, CharacterEx target, Point detectArea) {
        // center position
        PointF  center = new PointF();
        center.x = searcher.mPos.x+((int)(searcher.mSize.x*searcher.mScale)>>1);
        center.y = searcher.mPos.y+((int)(searcher.mSize.y*searcher.mScale)>>1);
        // target whole size
        PointF targetSize = new PointF();
        targetSize.x = target.mSize.x*target.mScale;
        targetSize.y = target.mSize.y*target.mScale;
        // detect width
        int detectW = detectArea.x>>1;
        // check overlap between searcher and target.
        if (target.mPos.x < center.x+detectW &&
            center.x-detectW < target.mPos.x+targetSize.x &&
            target.mPos.y < center.y-detectArea.y &&
            searcher.mPos.y < target.mPos.y+targetSize.y) {
            return true;
        }
        return false;
    }
    
    /*
        Not overlap both character.
     */
    public static byte NotOverlapCharacter(CharacterEx ch1, CharacterEx ch2, Rect safety1) {
        // return value
        byte ret = -1;
        // ch1 size
        PointF ch1Size = new PointF(ch1.mSize.x*ch1.mScale,ch1.mSize.y*ch1.mScale);
        // ch2 size
        PointF ch2Size = new PointF(ch2.mSize.x*ch2.mScale,ch2.mSize.y*ch2.mScale);

        // check overlap between ch1's left tip and ch2's right tip.
        if (ch1.mPos.x+safety1.left < ch2.mPos.x+ch2Size.x &&
                ch2.mPos.x < ch1.mPos.x+safety1.left &&
                ch1.mPos.y+safety1.top < ch2.mPos.y+ch2Size.y &&
                ch2.mPos.y < ch1.mPos.y+ch1Size.y-safety1.bottom) {
            // modify position
            ch2.mPos.x = ch1.mPos.x-(int)ch2Size.x+safety1.left;
            // value
            ret = 0;
        }
        // ch1's top
/*
        if (ch1.mPos.x+ch1Size.x-safety1.right < ch2.mPos.x &&
                ch2.mPos.x+ch2Size.x < ch1.mPos.x+safety1.left &&
                ch1.mPos.y+safety1.top < ch2.mPos.y+ch2Size.y &&
                ch2.mPos.y < ch1.mPos.y+safety1.top) {
            // modify position
            ch2.mPos.y = ch1.mPos.y-(int)ch2Size.y+safety1.top;
            // value
            ret = 1;
        }
*/
        // right
        if (ch1.mPos.x+ch1Size.x-safety1.right < ch2.mPos.x+ch2Size.x &&
                ch2.mPos.x < ch1.mPos.x+ch1Size.x-safety1.right &&
                ch1.mPos.y+safety1.top < ch2.mPos.y+ch2Size.y &&
                ch2.mPos.y < ch1.mPos.y+ch1Size.y-safety1.bottom) {
            // modify position
            ch2.mPos.x = ch1.mPos.x+(int)ch1Size.x-safety1.right;
            // value
            ret = 2;
        }
        // bottom
/*
        if (ch2.mPos.x < ch1.mPos.x+ch1Size.x-safety1.right &&
                ch1.mPos.x+safety1.left < ch2.mPos.x+ch2Size.x &&
                ch1.mPos.y+ch1Size.y-safety1.bottom < ch2.mPos.y+ch2Size.y &&
                ch2.mPos.y < ch1.mPos.y+ch1Size.y) {
            // modify position
            ch2.mPos.y = ch1.mPos.y+(int)ch1Size.y-safety1.bottom;
            // value
            ret = 3;
        }
*/
        return ret;
    }
}