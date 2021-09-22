package com.joshuabutton.queenscanner.sign.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.core.view.ViewCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Phí Văn Tuấn on 28/11/2018.
 */

public class DragImageView extends View {
    private int MUTILDOWM = 2;
    private int MUTILMOVE = 3;
    private int SINGALDOWN = 1;
    private boolean isFirst = true;
    private Context mContext;
    private double mD1;
    private Drawable mDrawable;
    private Rect mDrawableRect = new Rect();

    private float mOldX = 0.0f;

    private float mOldY = 0.0f;

    private Paint mPaint;
    private float mRation_WH = 0.0f;
    private int mStatus = 0;

    enum STATUS {
        SINGAL,
        MUTILDOWN,
        MUTILMOVE
    }

    public DragImageView(Context context) {
        super(context);
        mContext = context;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(ViewCompat.MEASURED_STATE_MASK);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(35.0f);
    }

    @SuppressLint({"DrawAllocation"})
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDrawable != null && mDrawable.getIntrinsicHeight() != 0 && mDrawable.getIntrinsicWidth() != 0) {
            setBounds();
            mDrawable.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int x;
        if (motionEvent.getPointerCount() == 1) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mStatus = SINGALDOWN;
                    mOldX = motionEvent.getX();
                    mOldY = motionEvent.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    checkBounds();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mStatus == SINGALDOWN) {
                        x = (int) (motionEvent.getX() - mOldX);
                        int y = (int) (motionEvent.getY() - mOldY);
                        mOldX = motionEvent.getX();
                        mOldY = motionEvent.getY();
                        mDrawableRect.offset(x, y);
                        invalidate();
                        break;
                    }
                    break;
            }
        }else {

            if (motionEvent.getAction() != 5) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        Log.i("mStatus", "mutildouble_up");
                        mStatus = 0;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mStatus = MUTILMOVE;
                        float x2 = motionEvent.getX(0);
                        float y2 = motionEvent.getY(0);
                        float x3 = motionEvent.getX(1);
                        float y3 = motionEvent.getY(1);

                        double sqrt = Math.sqrt(Math.pow((double) (x2 - x3), 2.0d) + Math.pow((double) (y2 - y3), 2.0d));
                        int i;
                        float f5;
                        if (mD1 < sqrt) {
                            if (mDrawableRect.width() < mContext.getResources().getDisplayMetrics().widthPixels * 2) {
                                y3 = (float) 10;
                                i = (int) (y3 / mRation_WH);
                                f5 = ((float) i) * 1.0f;
                                if (y3 / mRation_WH > f5 && y3 / mRation_WH >= f5 + 0.5f) {
                                    i++;
                                }
                                mDrawableRect.set(mDrawableRect.left - 10, mDrawableRect.top - i, mDrawableRect.right + 10, mDrawableRect.bottom + i);

                                invalidate();
                            }
                        } else if (mDrawableRect.width() > dip2px(mContext, 30)) {
                            y3 = (float) 10;
                            i = (int) (y3 / mRation_WH);
                            f5 = ((float) i) * 1.0f;
                            if (y3 / mRation_WH > f5 && y3 / mRation_WH >= f5 + 0.5f) {
                                i++;
                            }
                            mDrawableRect.set(mDrawableRect.left + 10, mDrawableRect.top + i, mDrawableRect.right - 10, mDrawableRect.bottom - i);
                            invalidate();
                        }
                        mD1 = sqrt;
                        break;
//                        if (f4 >= ((float) centerX)) {
//                            if (f2 >= ((float) centerY)) {
//
//                                break;
//                            }
//
//                            break;
//                        } else if (f2 >= ((float) centerY)) {
//                            break;
//                        } else {
//                            break;
//                        }
                }
            }

        }

        return true;
    }

    public void setBounds() {

        if (isFirst) {
            mRation_WH = ((float) mDrawable.getIntrinsicWidth()) / ((float) mDrawable.getIntrinsicHeight());
            int min = Math.min(getWidth(), dip2px(mContext, mDrawable.getIntrinsicWidth()));
            int i = (int) (((float) min) / mRation_WH);
            int width = (getWidth() - min) / 2;
            int height = (getHeight() - i) / 2;
            mDrawableRect.set(width, height, min + width, i + height);
            isFirst = false;

        }
        mDrawable.setBounds(mDrawableRect);

    }

    public void checkBounds() {
        int left = mDrawableRect.left;
        int top = mDrawableRect.top;
        int right = mDrawableRect.right;
        int bottom = mDrawableRect.bottom;

        if (top < dip2px(getContext(), 0)) {
            mDrawableRect.offset(0, dip2px(getContext(), 0) - top);
            invalidate();
        }
        if (bottom > (getResources().getDisplayMetrics().heightPixels - getStatusBarHeight()) - dip2px(getContext(), 56)) {
            mDrawableRect.offset(0, ((getResources().getDisplayMetrics().heightPixels - getStatusBarHeight()) - bottom) - dip2px(getContext(), 56));
            invalidate();
        }
        if (left < 0) {
            mDrawableRect.offset(-left, 0);
            invalidate();
        }
        if (right > getWidth()) {
            mDrawableRect.offset(getWidth()-right, 0);
            invalidate();
        }
    }

    public int getBitmapMatrix_left() {
        return mDrawableRect.left;
    }

    public int getBitmapMatrix_top() {
        return mDrawableRect.top;
    }

    public int getBitmapMatrix_right() {
        return mDrawableRect.right;
    }

    public Drawable getDrawable() {
        return mDrawable;
    }

    public void setDrawable(Drawable drawable) {
        mDrawable = drawable;
    }

    public int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5F);
    }
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
