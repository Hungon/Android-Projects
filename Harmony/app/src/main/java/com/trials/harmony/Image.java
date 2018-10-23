package com.trials.harmony;

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
    private SurfaceHolder   m_Surface;      // surface holder
    private Canvas          m_Canvas;       // Canvas
    private int             m_OriginX;      // image's origin X
    private int             m_OriginY;      // image's origin Y

    // constructor
    public Image(SurfaceHolder holder) {
        this.m_Surface = holder;
    }

    /*
      lock the surface to draw
    */
    public void lock() {
        this.m_Canvas = this.m_Surface.lockCanvas();
        if(this.m_Canvas == null) return;
        this.m_Canvas.translate(this.m_OriginX, this.m_OriginY);
    }
    /*
      unlock the surface to finish drawing.
     */
    public void unlock() {
        if (this.m_Canvas == null) return;
        this.m_Surface.unlockCanvasAndPost(this.m_Canvas);
    }

    // dedicate origin coordination to draw.
    public void setOrigin(int x, int y) {
        this.m_OriginX = x;
        this.m_OriginY = y;
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
        if (this.m_Canvas == null) return;
        Paint paint = new Paint();
        paint.setColor(color);               // set color
        paint.setStyle(Paint.Style.FILL);
        this.m_Canvas.drawRect(new Rect(x, y, x + w, y + h), paint);
    }
    /*
        Draw a whole image
    */
    public void DrawImageFast(int x, int y, Bitmap bitmap) {
        if (this.m_Canvas == null) return;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Rect src = new Rect(0, 0, w, h);
        Rect dst = new Rect(x, y, x + w, y + h);
        this.m_Canvas.drawBitmap(bitmap, src, dst, null);
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
        if (this.m_Canvas == null) return;
        Rect src = new Rect(srcX, srcY, srcX + w, srcY + h);
        Rect dst = new Rect(dstX, dstY, dstX + w, dstY + h);
        this.m_Canvas.drawBitmap(bitmap, src, dst, null);
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
        if (this.m_Canvas == null) return;
        double scaleW = w*scale;
        double scaleH = h*scale;
        Rect src = new Rect(srcX, srcY, srcX + w, srcY + h);
        Rect dst = new Rect(
                dstX + ((w - (int)scaleW)>>1),
                dstY + ((h - (int)scaleH)>>1),
                (dstX + ((w - (int)scaleW)>>1))+(int)scaleW,
                (dstY + ((h - (int)scaleH)>>1))+(int)scaleH);
        this.m_Canvas.drawBitmap(bitmap, src, dst, null);
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
        if (this.m_Canvas == null) return;
        Paint paint = new Paint();
        Rect src = new Rect(srcX, srcY, srcX + w, srcY + h);
        Rect dst = new Rect(dstX, dstY, dstX + w, dstY + h);
        // set alpha
        paint.setAlpha(alpha);
        this.m_Canvas.drawBitmap(bitmap, src, dst, paint);
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
        if (this.m_Canvas == null) return;
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
        this.m_Canvas.drawBitmap(bitmap, src, dst, paint);
    }

    // draw text
    public void drawText(String string, int x, int y, float size, int color) {
        if (this.m_Canvas == null) return;
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setTextSize(size);
        this.m_Canvas.drawText(string, x, y, paint);
    }
    public void drawText(String string, int x, int y, float size, int color, int alpha) {
        if (this.m_Canvas == null) return;
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setTextSize(size);
        paint.setAlpha(alpha);
        this.m_Canvas.drawText(string, x, y, paint);
    }
    /*
        Draw char
     */
    public void DrawChar(char mes[], int x, int y, float size, int color, int index) {
        if (this.m_Canvas == null) return;
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setTextSize(size);
        String str = String.valueOf(mes[index]);
        this.m_Canvas.drawText(str, x, y, paint);
    }
}