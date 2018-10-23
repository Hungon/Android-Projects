package com.trials.harmony;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;

/**
 * Created by Kohei Moroi on 11/2/2016.
 */

public class AssociationColour extends CharaBasic {
    private String mAssociationWord;
    AssociationColour(Context context, Image image){
        super(context,image);
    }
    void initColour(String imageFile, Point pos, Point size, int alpha, float scale, int type) {
        super.init(
                imageFile,
                pos, size,
                alpha,scale,type);
    }
    boolean updateColour() {
        boolean res = true;
        if (!this.mChara.mExistFlag) {
            return false;
        } else {
            // when reached to the end position, to return true
            if (!super.movingByBezier()) {
                res = this.mChara.VariableAlpha(this.mVariableAlpha, this.mFixedIntervalForAlpha);
            }
        }
        return res;
    }
    void drawColour() {
        // draw colour's image
        super.Draw();
    }
    void releaseColour() {
        super.Release();
        if (this.mAssociationWord != null) this.mAssociationWord = null;
    }
    boolean feedOut() { return this.mChara.VariableAlphaWhenUpToZeroNoExistence(this.mVariableAlpha,this.mFixedIntervalForAlpha); }
    /*
        Create the object which will move by bezier
    */
    public boolean createObject(Point bezierPos, Point pos, int type, int originY, String word) {
        if (this.mChara.mExistFlag) return false;
        this.mAssociationWord = word;
        this.mChara.mPos.x = pos.x;
        this.mChara.mPos.y = pos.y;
        super.setObject(type,this.mChara.mSize.y*originY);
        // set bezier
        super.setBezierCondition(bezierPos,new PointF(0,0));
        return true;
    }
    void setAlphaParameter(int alpha, int add, int interval) {
        this.mChara.mAlpha = alpha;
        super.setVariableAlpha(add,interval);
    }
    String getAssociationWord() { return this.mAssociationWord; }
    boolean getExist() {return this.mChara.mExistFlag; }
    public PointF getWholeSize() {
        return new PointF(
                this.mChara.mSize.x*this.mChara.mScale,
                this.mChara.mSize.y*this.mChara.mScale);
    }
}
