package com.workout.sallyapp.view.activities;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;

import com.workout.sallyapp.R;
import com.workout.sallyapp.databinding.ActivityWebViewBinding;

public class WebViewActivity extends AppCompatActivity {

    public static final String TITLE_KEY = "activity.webview.KEY_TITLE";

    private String toolbarTitle = "";
    private ActivityWebViewBinding bindingActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Data bindingActivity
        bindingActivity = DataBindingUtil.setContentView(this, R.layout.activity_web_view);
        setSupportActionBar(bindingActivity.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            toolbarTitle = bundle.getString(TITLE_KEY);
        }

        getSupportActionBar().setTitle(toolbarTitle);

        bindingActivity.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (getIntent().getData() != null) {
            Uri data = getIntent().getData();
            WebView webview = (WebView) findViewById(R.id.webview);
            webview.loadUrl(data.toString());
        }
    }
}
