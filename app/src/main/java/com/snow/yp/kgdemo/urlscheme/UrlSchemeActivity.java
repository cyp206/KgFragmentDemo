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

public class UrlSchemeActivity extends AppCompatActivity {

    @BindView(R.id.btn_jump)
    Button btnJump;
    @BindView(R.id.tv_show)
    TextView tvShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_url_scheme);
        ButterKnife.bind(this);
        tvShow.setVisibility(View.GONE);
        btnJump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * scheme//host:port path
                 * mu://zi:9527/index
                 */
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mu://zi:8888/tiam?uid=1001"));
                startActivity(intent);
            }
        });
    }
}
