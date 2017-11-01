package com.snow.yp.kgdemo.xfomade;

import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.snow.yp.kgdemo.R;
import com.snow.yp.kgdemo.puzzViewThumbnail.PuzzThumbnailView;
import com.snow.yp.kgdemo.xfomade.rainbowactionbar.BottomActionBar;
import com.snow.yp.kgdemo.xfomade.rainbowactionbar.OnBottomActionBarItemClick;

public class ActionBarActivity extends AppCompatActivity {


    private PuzzThumbnailView puzzTempleteView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomActionBar bottomActionBar = (BottomActionBar) findViewById(R.id.btn_aciton_bar);

        bottomActionBar.setOnBottomActionBarItemClick(new OnBottomActionBarItemClick() {
            @Override
            public void selected(int type) {
                Log.i("snow_wo_le", type + "");
            }
        });
        Matrix matrix = new Matrix();

        matrix.setScale(1, 0.5f);
        puzzTempleteView = (PuzzThumbnailView) findViewById(R.id.ptView);

    }
}
