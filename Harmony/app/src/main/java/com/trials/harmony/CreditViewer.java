package com.trials.harmony;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;

/**
 * Created by Kohei Moroi on 8/30/2016.
 */
public class CreditViewer implements HasMusicInfo {
    private final static int PROCESS_SHOW_TITLE_THAT_IS_CONTRIBUTOR = 0;
    private final static int PROCESS_INTRODUCE_EACH_CONTRIBUTOR     = 1;
    public final static int PROCESS_SHOE_MY_NAME                   = 2;
    public final static int PROCESS_THE_APPRECIATION               = 3;
    public final static int  PROCESS_AVAILABLE_TO_TRANSITION        = 4;
    public final static int PROCESS_GO_TO_WEB_VIEW                  = 999;
    private final static int CONTRIBUTOR_MAX = 6;
    // kind of type to create the web page
    private final static int TEXT_TYPE_OTHER    = 0;
    private final static int TEXT_TYPE_URL      = 1;
    private final static String PROCESS_END_POINT_WORDS[] = {
            "CONTRIBUTORS",
            "http://musmus.main.jp/english.html",
            "Kohei Moroi",
            "Thank you"
    };
    private MyText mMyText[];
    private Context mContext;
    private Image mImage;
    private Utility mUtility;
    private int mContributorCount;
    private String mWebUrl;
    private static int mProcess;
    public CreditViewer(Context context, Image image) {
        this.mContext = context;
        this.mImage = image;
        this.mUtility = new Utility();
    }
    public void InitCreditViewer() {
        Point pos = new Point(100,360);
        int colour = Color.WHITE;
        int size = 36;
        this.mMyText = new MyText[1];
        for (int i = 0; i < this.mMyText.length; i++) {
            this.mMyText[i] = new MyText(this.mContext,this.mImage);
            this.mMyText[i].InitMyText(PROCESS_END_POINT_WORDS[0],pos.x,pos.y,size,colour,0,TEXT_TYPE_OTHER);
            this.mMyText[i].SetVariableAlpha(0);
        }
        mProcess = PROCESS_SHOW_TITLE_THAT_IS_CONTRIBUTOR;
        this.mContributorCount = 0;
        this.mWebUrl = "";
    }
    public void UpdateCreditViewer() {
        if (mProcess == PROCESS_GO_TO_WEB_VIEW) {
            boolean guiding = GuidanceManager.GetIsGuiding();
            if (!guiding) {
                if (this.mUtility.ToMakeTheInterval(100)) {
                    Harmony.CreateWebPage(this.mWebUrl);
                }
            }
        } else if (mProcess < PROCESS_AVAILABLE_TO_TRANSITION) {
            for (int i = 0; i < this.mMyText.length; i++) {
                if (this.mMyText[i].GetVariableAlpha() == 0) {
                    if (this.mMyText[i].mUtility.ToMakeTheInterval((i + 1) * 10))
                        this.mMyText[i].SetVariableAlpha(2);
                }
                if (!this.mMyText[i].UpdateMyTextAlpha()) {
                    if (this.mMyText[i].mAlpha == 255) {
                        if (this.mMyText[i].mUtility.ToMakeTheInterval(20 * (i+1))) {
                            this.mMyText[i].SetVariableAlpha(-2);
                        }
                    }
                }
                // to call the web page of the contributor as touched the url.
                if (mProcess == PROCESS_INTRODUCE_EACH_CONTRIBUTOR) {
                    if (this.mMyText[i].mExistFlag && this.mMyText[i].mType == TEXT_TYPE_URL) {
                        if (Collision.CheckTouch(this.mMyText[i].mPos.x,this.mMyText[i].mPos.y,
                        this.mMyText[i].mSize.x,this.mMyText[i].mSize.y,this.mMyText[i].mScale)) {
                            mProcess = PROCESS_GO_TO_WEB_VIEW;
                            this.mWebUrl = this.mMyText[i].GetCurrentText();
                        }
                    }
                }
            }
            if (!this.mMyText[this.mMyText.length-1].GetExistence()) {
                if (this.mUtility.ToMakeTheInterval(10)) {
                    if (this.mMyText[this.mMyText.length-1].GetCurrentText().equals(PROCESS_END_POINT_WORDS[mProcess])) {
                        mProcess++;
                        for (MyText t:this.mMyText) t.ReleaseMyText();
                        this.mMyText = null;
                    }
                    // each contributor
                    if (mProcess == PROCESS_INTRODUCE_EACH_CONTRIBUTOR) {
                        int contributorMax = (CONTRIBUTOR_MAX < EACH_CONTRIBUTOR.length - this.mContributorCount) ? CONTRIBUTOR_MAX : EACH_CONTRIBUTOR.length - this.mContributorCount;
                        this.mMyText = new MyText[contributorMax<<1];
                        String text = "";
                        Point pos = new Point(10, 50);
                        int size[] = {36,20};
                        int spaceY = 50;
                        int type = TEXT_TYPE_OTHER;
                        int w = 0;
                        int colour = Color.WHITE;
                        for (int i = 0; i < contributorMax; i++) {
                            for (int j = 0; j < 2; j++) {
                                this.mMyText[(i+i)+j] = new MyText(this.mContext, this.mImage);
                                if (j == 0) {
                                    text = EACH_CONTRIBUTOR[this.mContributorCount];
                                    type = TEXT_TYPE_OTHER;
                                    w = 0;
                                    colour = Color.WHITE;
                                } else if (j == 1) {
                                    text = EACH_URL[this.mContributorCount];
                                    type = TEXT_TYPE_URL;
                                    // set the width to create the web URL
                                    w = 400;
                                    colour = Color.CYAN;
                                }
                                this.mMyText[(i+i)+j].InitMyText(text, pos.x,pos.y, size[j], colour, 0, type);
                                this.mMyText[(i+i)+j].SetVariableAlpha(0);
                                this.mMyText[(i+i)+j].SetTextWidth(w);
                                pos.y += spaceY;
                            }
                            this.mContributorCount++;
                        }
                        // my name
                    } else if (mProcess == PROCESS_SHOE_MY_NAME) {
                        this.mMyText = new MyText[3];
                        String text = "";
                        Point pos = new Point(10, 300);
                        int spaceY = 50;
                        int size = 36;
                        for (int i = 0; i < this.mMyText.length; i++) {
                            this.mMyText[i] = new MyText(this.mContext, this.mImage);
                            if (i == 0) {
                                text = "Presented";
                            } else if (i == 1) {
                                text = "by";
                                pos.x = 200;
                            } else if (i == 2) {
                                text = PROCESS_END_POINT_WORDS[2];
                                pos.x = 250;
                            }
                            this.mMyText[i].InitMyText(text, pos.x,pos.y, size, Color.WHITE, 0,TEXT_TYPE_OTHER);
                            this.mMyText[i].SetVariableAlpha(0);
                            pos.y += spaceY;
                        }
                        // last text
                    } else if (mProcess == PROCESS_THE_APPRECIATION) {
                        Point pos = new Point(150,350);
                        int colour = Color.WHITE;
                        int size = 40;
                        this.mMyText = new MyText[1];
                        for (int i = 0; i < this.mMyText.length; i++) {
                            this.mMyText[i] = new MyText(this.mContext,this.mImage);
                            this.mMyText[i].InitMyText(PROCESS_END_POINT_WORDS[3],pos.x,pos.y,size,colour,0,TEXT_TYPE_OTHER);
                            this.mMyText[i].SetVariableAlpha(0);
                        }
                    }
                }
            }
        }
    }
    public void DrawCreditViewer() {
        if (this.mMyText != null) {
            for (MyText t : this.mMyText) {
                t.DrawMyTextByAlpha();
            }
        }
    }
    public void ReleaseCreditViewer() {
        if (this.mMyText != null) {
            for (int i = 0; i < this.mMyText.length; i++) {
                this.mMyText[i].ReleaseMyText();
                this.mMyText[i] = null;
            }
        }
    }
    public static int GetTheCurrentProcess() { return mProcess; }
}