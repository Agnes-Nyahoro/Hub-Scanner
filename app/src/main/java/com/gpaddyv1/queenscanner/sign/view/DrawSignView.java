package com.joshuabutton.queenscanner.sign.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import androidx.annotation.ColorInt;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Phí Văn Tuấn on 26/11/2018.
 */

public class DrawSignView extends View {
    private Canvas cacheCanvas;
    private Bitmap cachebBitmap;
    private boolean isTouched = false;
    private int mBackColor = 0;
    private Context mContext;
    private final Paint mGesturePaint = new Paint();
    private int mPaintWidth = 10;
    private final Path mPath = new Path();
    private int mPenColor = ViewCompat.MEASURED_STATE_MASK;
    private float mX;
    private float mY;

    public DrawSignView(Context context) {
        super(context);
        init(context);
    }

    public DrawSignView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public DrawSignView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context);
    }

    public void init(Context context) {
        this.mContext = context;
        this.mGesturePaint.setAntiAlias(true);
        this.mGesturePaint.setStyle(Paint.Style.STROKE);
        this.mGesturePaint.setStrokeWidth((float) this.mPaintWidth);
        this.mGesturePaint.setColor(this.mPenColor);
    }

    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.cachebBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        this.cacheCanvas = new Canvas(this.cachebBitmap);
        this.cacheCanvas.drawColor(this.mBackColor);
        this.isTouched = false;
    }

    public Paint getGesturePaint() {
        return mGesturePaint;
    }

    public Path getPath() {
        return mPath;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDown(motionEvent);
                break;
            case MotionEvent.ACTION_UP:
                this.cacheCanvas.drawPath(this.mPath, this.mGesturePaint);
                this.mPath.reset();
                break;
            case MotionEvent.ACTION_MOVE:
                this.isTouched = true;
                touchMove(motionEvent);
                break;
        }
        invalidate();
        return true;
    }

    public void setCachebBitmap(Bitmap bitmap) {
        this.cachebBitmap = bitmap;
        this.isTouched = true;
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(this.cachebBitmap, 0.0f, 0.0f, this.mGesturePaint);
        canvas.drawPath(this.mPath, this.mGesturePaint);

    }

    private void touchDown(MotionEvent motionEvent) {
        this.mPath.reset();
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        this.mX = x;
        this.mY = y;

        this.mPath.moveTo(x, y);
    }

    private void touchMove(MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        float f = this.mX;
        float f2 = this.mY;
        float abs = Math.abs(x - f);
        float abs2 = Math.abs(y - f2);
        if (abs >= 3.0f || abs2 >= 3.0f) {
            this.mPath.quadTo(f, f2, (x + f) / 2.0f, (y + f2) / 2.0f);
            this.mX = x;
            this.mY = y;
        }
    }

    public void clear() {
        if (this.cacheCanvas != null) {
            this.isTouched = false;
            this.mGesturePaint.setColor(this.mPenColor);
            this.cacheCanvas.drawColor(this.mBackColor, PorterDuff.Mode.CLEAR);
            this.mGesturePaint.setColor(this.mPenColor);
            invalidate();
        }
    }

    public void save(String fileName) throws IOException {
        Bitmap   bitmap = clearBlank(cachebBitmap, 10);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] toByteArray = byteArrayOutputStream.toByteArray();
        if (toByteArray != null) {
            File file = new File(fileName);
            if (file.exists()) {
                file.delete();
            }
            OutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(toByteArray);
            fileOutputStream.close();
        }
        if (byteArrayOutputStream != null) {
            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();
        }
//        if (!(this.cachebBitmap == null || this.cachebBitmap.isRecycled())) {
//            this.cachebBitmap.recycle();
//        }
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    public Bitmap getBitMap() {
        setDrawingCacheEnabled(true);
        buildDrawingCache();
        Bitmap drawingCache = getDrawingCache();
        setDrawingCacheEnabled(false);
        return drawingCache;
    }
    public Bitmap getCachebBitmap(){
        return cachebBitmap;
    }

    public void setPaintWidth(int paintWidth) {
        if (paintWidth <= 0) {
            paintWidth = 10;
        }
        this.mPaintWidth = paintWidth;
        this.mGesturePaint.setStrokeWidth((float) paintWidth);
    }
    private Bitmap clearBlank(Bitmap bp, int blank) {
        int HEIGHT = bp.getHeight();
        int WIDTH = bp.getWidth();
        int top = 0, left = 0, right = 0, bottom = 0;
        int[] pixs = new int[WIDTH];
        boolean isStop;
        for (int y = 0; y < HEIGHT; y++) {
            bp.getPixels(pixs, 0, WIDTH, 0, y, WIDTH, 1);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBackColor) {
                    top = y;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        for (int y = HEIGHT - 1; y >= 0; y--) {
            bp.getPixels(pixs, 0, WIDTH, 0, y, WIDTH, 1);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBackColor) {
                    bottom = y;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        pixs = new int[HEIGHT];
        for (int x = 0; x < WIDTH; x++) {
            bp.getPixels(pixs, 0, 1, x, 0, 1, HEIGHT);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBackColor) {
                    left = x;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        for (int x = WIDTH - 1; x > 0; x--) {
            bp.getPixels(pixs, 0, 1, x, 0, 1, HEIGHT);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBackColor) {
                    right = x;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        if (blank < 0) {
            blank = 0;
        }
        left = left - blank > 0 ? left - blank : 0;
        top = top - blank > 0 ? top - blank : 0;
        right = right + blank > WIDTH - 1 ? WIDTH - 1 : right + blank;
        bottom = bottom + blank > HEIGHT - 1 ? HEIGHT - 1 : bottom + blank;
        return Bitmap.createBitmap(bp, left, top, right - left, bottom - top);
    }
    public void setBackColor(@ColorInt int i) {
        this.mBackColor = i;
    }

    public void setPenColor(int penColor) {
        this.mPenColor = penColor;
        this.mGesturePaint.setColor(penColor);
    }

    public boolean getTouched() {
        return this.isTouched;
    }
}
