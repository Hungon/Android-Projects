package com.trials.supertriathlon;

import android.content.Context;
import android.graphics.Point;

/**
 * Created by USER on 3/19/2016.
 */
public class SeaItem {
    // static variables
    // item image setting
    public final static Point          ITEM_SIZE = new Point(48,48);
    // type of item
    public final static int            ITEM_EMPTY      = -1;
    public final static int            ITEM_ABSOLUTE   = 0;
    public final static int            ITEM_SPEED_UP   = 1;
    public final static int            ITEM_SLOW       = 2;
    public final static int            ITEM_SUPER      = 3;
    public final static int            ITEM_KIND       = 4;
    // the effect that got the item
    public final static float           ITEM_EFFECT_SPEED_UP    = 2.0f;
    public final static float           ITEM_EFFECT_SPEED_DOWN  = -1.5f;
    // filed
    private Context                 mContext;
    private Image                   mImage;
    private BaseCharacter           mItem[];
    
    /*
        Constructor
    */
    public SeaItem(Context context, Image image) {
        this.mContext = context;
        this.mImage = image;
        this.mItem = new BaseCharacter[7];
        for (int i = 0; i < this.mItem.length; i++) {
            this.mItem[i] = new BaseCharacter(image);
        }
    }
    /*
        Initialize
    */
    public void InitItem() {
        // image setting
        for (BaseCharacter i: this.mItem) {
            i.LoadCharaImage(this.mContext,"seaitems");
            i.mSize.x = ITEM_SIZE.x;
            i.mSize.y = ITEM_SIZE.y;
            i.mType = ITEM_EMPTY;
        }
    }
    
    /*
        Update
    */
    public void UpdateItem() {

        for (BaseCharacter i: this.mItem) {
            if (!i.mExistFlag) continue;
            // to check the overlap between item and camera's area.
            if (!StageCamera.CollisionCamera(i)) {
                i.mExistFlag = false;
                i.mType = ITEM_EMPTY;
                i.mMoveX = 0;
                i.mMoveY = 0;
            }
            // update the move
            // to increase wave count
            i.mTime++;
            // update move
            i.mMoveX = i.mSpeed*-1;
            i.mMoveY = (float)Math.sin(i.mTime*3.14/180.0f)*i.mSpeed;
            // limit count
            i.mTime %= 100000;
            // add move to position
            i.mPos.x += i.mMoveX;
            i.mPos.y += i.mMoveY;
        }
    }
    /*
        Draw
    */
    public void DrawItem() {
        // camera position
        Point camera = StageCamera.GetCameraPosition();
        if (camera == null) camera = new Point();
        for (BaseCharacter i: this.mItem) {
            if (i.mExistFlag) {
                this.mImage.DrawAlphaAndScale(
                        i.mPos.x-camera.x,i.mPos.y-camera.y,
                        i.mSize.x,i.mSize.y,
                        i.mOriginPos.x,i.mOriginPos.y,
                        i.mAlpha,i.mScale,
                        i.mBmp
                );
            }
        }
    }
    /*
        Release
    */
    public void ReleaseItem() {
        this.mContext = null;
        this.mImage = null;
        for (int i = 0; i < this.mItem.length; i++) {
            this.mItem[i].ReleaseCharaBmp();
            this.mItem[i] = null;
        }
    }
    /*
        Create
    */
    public void CreateItem(int x, int y, int likelihood[]) {
        // to decide the item to create.
        int type = -1;
        // type of item
        int items[] = {
                ITEM_ABSOLUTE,ITEM_SPEED_UP,ITEM_SLOW,ITEM_SUPER
        };
        for (int i = 0; i < likelihood.length; i++) {
            if (MyRandom.GetLikelihood(likelihood[i])) {
                type = items[i];
                break;
            }
        }
        // when type wasn't substituted, to not to create the item.
        if (type == -1) return;
        // to create the item by type
        for (BaseCharacter i: this.mItem) {
            if (i.mExistFlag) continue;
            i.mPos.x = x+300;
            i.mPos.y = y;
            i.mOriginPos.y = type*ITEM_SIZE.y;
            i.mSpeed = 1.0f;
            i.mType = type;
            i.mExistFlag = true;
            break;
        }
    }
    /*
        Collision
    */
    public int CollisionItem(BaseCharacter ch) {
        int type;
        for (BaseCharacter i: this.mItem) {
            if (!i.mExistFlag) continue;
            if (Collision.CollisionCharacter(ch,i)) {
                type = i.mType;
                i.mExistFlag = false;
                i.mType = ITEM_EMPTY;
                i.mMoveX = 0;
                i.mMoveY = 0;
                return type;
            }
        }
        return ITEM_EMPTY;
    }
}