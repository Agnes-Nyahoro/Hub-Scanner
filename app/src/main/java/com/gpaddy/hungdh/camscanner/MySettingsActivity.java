package com.gpaddy.hungdh.camscanner;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import androidx.annotation.Nullable;
import com.todobom.queenscanner.R;

/**
 * Created by HUNGDH on 4/17/2017.
 */

public class MySettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.my_settings);
    }
}
