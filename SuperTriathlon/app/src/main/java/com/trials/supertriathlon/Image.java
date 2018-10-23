package com.trials.supertriathlon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;

/**
 * Created by USER on 1/26/2016.
 */
public class Image {

    // filed
    private SurfaceHolder   mSurface;      // surface holder
    private Canvas          mCanvas;       // Canvas
    private int             mOriginX;      // image's origin X
    private int             mOriginY;      // image's origin Y

    // constructor
    public Image(SurfaceHolder holder) {
        this.mSurface = holder;
    }

    /*
      lock the surface to draw
    */
    public void lock() {
        this.mCanvas = this.mSurface.lockCanvas();
        if(this.mCanvas == null) return;
        this.mCanvas.translate(this.mOriginX, this.mOriginY);
    }
    /*
      unlock the surface to finish drawing.
     */
    public void unlock() {
        if (this.mCanvas == null) return;
        this.mSurface.unlockCanvasAndPost(this.mCanvas);
    }

    // dedicate origin coordination to draw.
    public void setOrigin(int x, int y) {
        this.mOriginX = x;
        this.mOriginY = y;
    }

    // load image
    public Bitmap LoadImage(Context context, String name) {
        int Id = context.getResources().getIdentifier(
                name, "drawable", context.getPackageName());
        return BitmapFactory.decodeResource(context.getResources(), Id);
    }

    /********************************************************************************
     Draw functions
     ********************************************************************************/
    // draw filling rectangular
    public void fillRect(int x, int y, int w, int h, int color) {
        if (this.mCanvas == null) return;
        Paint paint = new Paint();
        paint.setColor(color);               // set color
        paint.setStyle(Paint.Style.FILL);
        this.mCanvas.drawRect(new Rect(x, y, x + w, y + h), paint);
    }
    public void fillRect(int x, int y, int w, int h, int color, int alpha) {
        if (this.mCanvas == null) return;
        Paint paint = new Paint();
        paint.setColor(color);               // set color
        paint.setAlpha(alpha);
        paint.setStyle(Paint.Style.FILL);
        this.mCanvas.drawRect(new Rect(x, y, x + w, y + h), paint);
    }
    /*
        Draw a whole image
    */
    public void DrawImageFast(int x, int y, Bitmap bitmap) {
        if (this.mCanvas == null) return;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Rect src = new Rect(0, 0, w, h);
        Rect dst = new Rect(x, y, x + w, y + h);
        this.mCanvas.drawBitmap(bitmap, src, dst, null);
    }
    /*
        Draw a portion of image.
     */
    public void DrawImage(
            int dstX, int dstY,
            int w, int h,
            int srcX, int srcY,
            Bitmap bitmap)
    {
        if (this.mCanvas == null) return;
        Rect src = new Rect(srcX, srcY, srcX + w, srcY + h);
        Rect dst = new Rect(dstX, dstY, dstX + w, dstY + h);
        this.mCanvas.drawBitmap(bitmap, src, dst, null);
    }

    /*
        The scale process to draw.
     */
    public void DrawScale(
            int dstX, int dstY,
            int w, int h,
            int srcX, int srcY,
            float scale, Bitmap bitmap)
    {
        if (this.mCanvas == null) return;
        double scaleW = w*scale;
        double scaleH = h*scale;
        Rect src = new Rect(srcX, srcY, srcX + w, srcY + h);
        Rect dst = new Rect(
                dstX + ((w - (int)scaleW)>>1),
                dstY + ((h - (int)scaleH)>>1),
                (dstX + ((w - (int)scaleW)>>1))+(int)scaleW,
                (dstY + ((h - (int)scaleH)>>1))+(int)scaleH);
        this.mCanvas.drawBitmap(bitmap, src, dst, null);
    }

    /*
        The alpha process to draw
     */
    public void DrawAlpha(
            int dstX, int dstY,
            int w, int h,
            int srcX, int srcY,
            int alpha, Bitmap bitmap)
    {
        if (this.mCanvas == null) return;
        Paint paint = new Paint();
        Rect src = new Rect(srcX, srcY, srcX + w, srcY + h);
        Rect dst = new Rect(dstX, dstY, dstX + w, dstY + h);
        // set alpha
        paint.setAlpha(alpha);
        this.mCanvas.drawBitmap(bitmap, src, dst, paint);
    }

    /*
        Alpha and scale
     */
    public void DrawAlphaAndScale(
            int dstX, int dstY,
            int w, int h,
            int srcX, int srcY,
            int alpha, float scale,
            Bitmap bitmap)
    {
        if (this.mCanvas == null) return;
        double scaleW = w*scale;
        double scaleH = h*scale;
        Rect src = new Rect(srcX, srcY, srcX + w, srcY + h);
        Rect dst = new Rect(
                dstX + ((w - (int)scaleW)>>1),
                dstY + ((h - (int)scaleH)>>1),
                (dstX + ((w - (int)scaleW)>>1))+(int)scaleW,
                (dstY + ((h - (int)scaleH)>>1))+(int)scaleH);
        // set alpha
        Paint paint = new Paint();
        paint.setAlpha(alpha);
        this.mCanvas.drawBitmap(bitmap, src, dst, paint);
    }

    // draw text
    public void drawText(String string, int x, int y, float size, int color) {
        if (this.mCanvas == null) return;
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setTextSize(size);
        this.mCanvas.drawText(string, x, y, paint);
    }
    public void drawText(String string, int x, int y, float size, int color, int alpha) {
        if (this.mCanvas == null) return;
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setTextSize(size);
        paint.setAlpha(alpha);
        this.mCanvas.drawText(string, x, y, paint);
    }
    /*
        Draw char
     */
    public void DrawChar(char mes[], int x, int y, float size, int color, int index) {
        if (this.mCanvas == null) return;
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setTextSize(size);
        String str = String.valueOf(mes[index]);
        this.mCanvas.drawText(str, x, y, paint);
    }
}