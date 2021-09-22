package com.gpaddy.hungdh.base;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.ButterKnife;

/**
 * Created by Phí Văn Tuấn on 24/11/2018.
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected abstract int getLayoutRes();

    protected abstract void initData();

    protected abstract void initView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutRes());

        ButterKnife.bind(this);

        initView();

        initData();
    }
}
