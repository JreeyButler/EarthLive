package com.dipper.earthlive.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import com.dipper.earthlive.R;

import java.util.Objects;

/**
 * @author Dipper
 * @date 2018/12/27
 */
public class OpenSourceActivity extends Activity {
    private TextView mInformation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getActionBar()).setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_open_source);
        initView();
    }

    private void initView() {
        mInformation = findViewById(R.id.information);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
