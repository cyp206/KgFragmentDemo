package com.snow.yp.kgdemo.urlscheme;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.snow.yp.kgdemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.attr.host;

public class UrlSchemeSecondActivity extends AppCompatActivity {

    @BindView(R.id.btn_jump)
    Button btnJump;
    @BindView(R.id.tv_show)
    TextView tvShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_url_scheme);
        ButterKnife.bind(this);
        btnJump.setVisibility(View.GONE);


        // 获取uri参数
        Intent intent = getIntent();
        String scheme = intent.getScheme();
        Uri uri = intent.getData();
        if (uri != null) {
            String host = uri.getHost();
            String dataString = intent.getDataString();
            String from = uri.getQueryParameter("from");
            String path = uri.getPath();
            String encodedPath = uri.getEncodedPath();
            String queryString = uri.getQuery();

            //...根据uri判断打开哪个页，或者打开哪个功能
            tvShow.setText("host:" + host +
                    "\n" +
                    "dataString:" + dataString +
                    "\n" +
                    "from:" + from +
                    "\n" +
                    "path:" + path +
                    "\n" +
                    "encodedPath:" + encodedPath +
                    "\n" +
                    "queryString:" + queryString +
                    "\n" +
                    "scheme:" + scheme
            );

        }
    }
}
