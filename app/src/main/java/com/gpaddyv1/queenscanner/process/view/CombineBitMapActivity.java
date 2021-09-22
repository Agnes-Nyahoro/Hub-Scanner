package com.joshuabutton.queenscanner.process.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.gpaddy.hungdh.base.BaseActivity;
import com.joshuabutton.queenscanner.PresenterScanner;
import com.joshuabutton.queenscanner.handle.HandleActivity;
import com.joshuabutton.queenscanner.sign.view.DragImageView;
import com.todobom.queenscanner.R;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CombineBitMapActivity extends BaseActivity {
    @BindView(R.id.imgView)
    ImageView imgView;
    @BindView(R.id.view_root)
    RelativeLayout viewRoot;

    //    private Canvas canvas;
    private String imagePath;
    private Bitmap source, sign;
    private DragImageView signView;
    private int view_width = 0;
    private int view_height = 0;
    public static int REQUEST_MERGE = 311;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_combine_bit_map;
    }

    @Override
    protected void initData() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        source = BitmapFactory.decodeFile(imagePath);
        sign = BitmapFactory.decodeFile(PresenterScanner.FILE_NAME);
        signView = new DragImageView(this);
        signView.setDrawable(new BitmapDrawable(sign));
        imgView.setImageBitmap(source);
        viewRoot.addView(signView);
        view_height = imgView.getMeasuredHeight();
        view_width = imgView.getMeasuredWidth();

    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        imagePath = getIntent().getStringExtra("imagePath");

    }

    private Bitmap resizeImage2(Bitmap bitmap, int i, int i2) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float f = (((float) i) + 0.0f) / ((float) width);
        float f2 = (((float) i2) + 0.0f) / ((float) height);
        if (f > f2) {
            f = f2;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(f, f);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }

    private Bitmap mergeBitmap(Bitmap bitmap, Bitmap bitmap2, int i, int i2) {
        if (bitmap == null || bitmap.isRecycled() || bitmap2 == null || bitmap2.isRecycled()) {
            return null;
        }
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        Canvas canvas = new Canvas(createBitmap);
        canvas.drawBitmap(bitmap, new Matrix(), null);
        canvas.drawBitmap(bitmap2, (float) i, (float) i2, null);
        return createBitmap;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done_sign, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.signDone) {
            Bitmap mergeBitmap;
            Bitmap resizeImage2 = resizeImage2(sign, signView.getBitmapMatrix_right() - signView.getBitmapMatrix_left(), ((signView.getBitmapMatrix_right() - signView.getBitmapMatrix_left()) * sign.getHeight()) / sign.getWidth());
            Bitmap bitmap = null;
            view_width = imgView.getMeasuredWidth();
            view_height = imgView.getMeasuredHeight();
            if ((((float) source.getWidth()) * 1.0f) / ((float) view_width) > (((float) source.getHeight()) * 1.0f) / ((float) view_height)) {
                bitmap = resizeImage2(sign, (resizeImage2.getWidth() * source.getWidth()) / view_width, (resizeImage2.getHeight() * source.getWidth()) / view_width);
                mergeBitmap = mergeBitmap(source, bitmap, (signView.getBitmapMatrix_left() * source.getWidth()) / view_width, (int) ((((((float) signView.getBitmapMatrix_top()) - (((((float) view_height) * 1.0f) - (((((float) source.getHeight()) * 1.0f) * ((float) view_width)) / ((float) source.getWidth()))) / 2.0f)) * 1.0f) * ((float) source.getWidth())) / ((float) view_width)));
            } else if ((((float) source.getWidth()) * 1.0f) / ((float) view_width) == (((float) source.getHeight()) * 1.0f) / ((float) view_height)) {
                bitmap = resizeImage2(sign, (resizeImage2.getWidth() * source.getWidth()) / view_width, (resizeImage2.getHeight() * source.getWidth()) / view_width);
                mergeBitmap = mergeBitmap(source, bitmap, (signView.getBitmapMatrix_left() * source.getWidth()) / view_width, (signView.getBitmapMatrix_top() * source.getWidth()) / view_width);
            } else if ((((float) source.getWidth()) * 1.0f) / ((float) view_width) < (((float) source.getHeight()) * 1.0f) / ((float) view_height)) {
                bitmap = resizeImage2(sign, (resizeImage2.getWidth() * source.getHeight()) / view_height, (resizeImage2.getHeight() * source.getHeight()) / view_height);
                mergeBitmap = mergeBitmap(source, bitmap, ((signView.getBitmapMatrix_left() - ((int) (((((float) view_width) * 1.0f) - (((((float) source.getWidth()) * 1.0f) * ((float) view_height)) / ((float) source.getHeight()))) / 2.0f))) * source.getHeight()) / view_height, (signView.getBitmapMatrix_top() * source.getHeight()) / view_height);
            } else {
                mergeBitmap = null;
            }
            OutputStream bufferedOutputStream = null;
            try {
                bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(imagePath));
                mergeBitmap.compress(Bitmap.CompressFormat.JPEG, 85, bufferedOutputStream);
                setResult(RESULT_OK);
                finish();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * @param context
     * @param imagePath duong dan file image them sign
     */
    public static void onStartCombine(Context context, String imagePath) {
        Intent intent = new Intent(context, CombineBitMapActivity.class);
        if (context instanceof HandleActivity) {
            intent.putExtra("imagePath", imagePath);
            ((HandleActivity) context).startActivityForResult(intent, REQUEST_MERGE);
        }
    }
}
