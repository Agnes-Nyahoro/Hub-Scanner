package com.joshuabutton.queenscanner.sign.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import androidx.appcompat.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;

import com.gpaddy.hungdh.base.BaseActivity;
import com.joshuabutton.queenscanner.PresenterScanner;
import com.joshuabutton.queenscanner.handle.HandleActivity;
import com.todobom.queenscanner.R;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignActivity extends BaseActivity {
    @BindView(R.id.signView)
    DrawSignView signView;
    private int checkSize = 0;
    private int checkColor = 0;
    public static final int REQUEST_SIGN=212;
    private String[] color = new String[]{"Black", "Red", "Blue", "Green", "Purple", "Yellow"};
    private int[] resColor;
    private String[] paintSize = new String[]{"6", "10", "20", "30", "40", "45", "50", "60", "70", "80"};

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_sign;
    }

    @Override
    protected void initData() {

        resColor = new int[]{Color.BLACK, Color.RED, Color.BLUE, Color.GREEN, getResources().getColor(R.color.purple), Color.YELLOW};
    }

    @Override
    protected void initView() {
        setTitle("Sign");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sign, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signClear:
                signView.clear();
                break;
            case R.id.signSize:
                setPainSize(this);
                break;
            case R.id.signColor:
                setPenColor(this);
                break;
            case R.id.signDone:
                if (signView.getCachebBitmap() != null) {

                    try {
                        signView.save(PresenterScanner.FILE_NAME);
                        setResult(RESULT_OK);
                        finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setPenColor(Context context) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        mBuilder.setTitle("Choose color");
        mBuilder.setSingleChoiceItems(color, checkColor, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position) {
                signView.setPenColor(resColor[position]);
                checkColor = position;
                dialogInterface.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void setPainSize(Context context) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        mBuilder.setTitle("Choose size");
        mBuilder.setSingleChoiceItems(paintSize, checkSize, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position) {
                signView.setPaintWidth(Integer.parseInt(paintSize[position]));
                checkSize = position;
                dialogInterface.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    public static void startSign(Context context){
        Intent intent=new Intent(context,SignActivity.class);
        if (context instanceof HandleActivity){
            ((HandleActivity)context).startActivityForResult(intent,REQUEST_SIGN);
        }
    }
}
