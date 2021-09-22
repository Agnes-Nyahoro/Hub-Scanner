package com.todobom.queenscanner;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class AccountActivity extends AppCompatActivity implements View.OnClickListener{
    private LinearLayout layoutRate, layoutFeedback, layoutAbout, layoutShare;
    private ImageView ic_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_account);
        initUI( );
        setListeners();
    }
    private void setListeners() {
        layoutAbout.setOnClickListener(this);
        layoutRate.setOnClickListener(this);
        layoutShare.setOnClickListener(this);
        layoutFeedback.setOnClickListener(this);
        ic_back.setOnClickListener(this);

    }

    private void initUI(){
        ic_back=findViewById(R.id.ic_back);
        layoutAbout = findViewById(R.id.layoutAbout);
        layoutRate = findViewById(R.id.layoutRate);
        layoutShare = findViewById(R.id.layoutShare);
        layoutFeedback = findViewById(R.id.layoutFeedback);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ic_back:
                onBackPressed();
                break;
            case R.id.layoutAbout:
                break;
            case R.id.layoutRate:
                final String appPackageName = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                break;
            case R.id.layoutShare:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "https://play.google.com/store/apps/details?id=" + getPackageName();
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Document Manager");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share with"));
                break;

            case R.id.layoutFeedback:
                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setType("plain/text");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{getString(R.string.gpaddy_contact)});
                PackageInfo pInfo = null;
                try {
                    pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name) + "," +
                        Build.MODEL + "," + Build.VERSION.RELEASE + "," + pInfo.versionName);
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.cm_hint_email)));
                break;

        }
    }
}
