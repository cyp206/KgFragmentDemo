package com.snow.yp.kgdemo.xfomade.rainbowactionbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.snow.yp.kgdemo.R;

/**
 * Created by y on 2017/5/23.
 */

public class BottomActionBar extends RelativeLayout implements View.OnClickListener {
    private ImageView ivFilte;
    private ImageView ivText;
    private ImageView ivSticker;
    private ImageView ivGraffiti;
    private ImageView ivEdit;
    private ActionBarBg actionBarBg;
    private boolean init;
    private OnBottomActionBarItemClick onBottomActionBarItemClick;
    private RelativeLayout rlFilter;
    private RelativeLayout rlText;
    private RelativeLayout rlSticker;
    private RelativeLayout rlGraffiti;
    private RelativeLayout rlEdit;




    public BottomActionBar(Context context) {
        super(context);
        init();
    }

    public BottomActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }


    public BottomActionBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.bar_bottom_action, this);
        findView();


    }

    private void findView() {
        ivFilte = (ImageView) findViewById(R.id.iv_filter);
        ivText = (ImageView) findViewById(R.id.iv_text);
        ivSticker = (ImageView) findViewById(R.id.iv_sticker);
        ivGraffiti = (ImageView) findViewById(R.id.iv_graffiti);
        ivEdit = (ImageView) findViewById(R.id.iv_edit);
        actionBarBg = (ActionBarBg) findViewById(R.id.action_bar_bg);

        rlFilter = (RelativeLayout) findViewById(R.id.rl_filter);
        rlText = (RelativeLayout) findViewById(R.id.rl_text);
        rlSticker = (RelativeLayout) findViewById(R.id.rl_sticker);
        rlGraffiti = (RelativeLayout) findViewById(R.id.rl_graffiti);
        rlEdit = (RelativeLayout) findViewById(R.id.rl_edit);

        rlFilter.setOnClickListener(this);
        rlText.setOnClickListener(this);
        rlSticker.setOnClickListener(this);
        rlGraffiti.setOnClickListener(this);
        rlEdit.setOnClickListener(this);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (ivFilte.getMeasuredWidth() != 0 && !init) {
            int[] location = new int[2];
            ivFilte.getLocationOnScreen(location);
            if (location[0] == 0) return;
            actionBarBg.initPosition(ActionBarBg.MODE_FUNCTION_FILTER, location[0]);
            init = true;
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        int[] location = new int[2];

        switch (id) {
            case R.id.rl_filter:
                resetIcon();
                ivFilte.setImageResource(R.mipmap.icon_fun_filter);
                ivFilte.getLocationOnScreen(location);
                actionBarBg.changPosition(ActionBarBg.MODE_FUNCTION_FILTER, location[0]);
                if (onBottomActionBarItemClick != null) {
                    onBottomActionBarItemClick.selected(ActionBarBg.MODE_FUNCTION_FILTER);
                }

                break;
            case R.id.rl_text:
                resetIcon();
                ivText.setImageResource(R.mipmap.icon_fun_text);
                ivText.getLocationOnScreen(location);
                actionBarBg.changPosition(ActionBarBg.MODE_FUNCTION_TEXT, location[0]);
                if (onBottomActionBarItemClick != null) {
                    onBottomActionBarItemClick.selected(ActionBarBg.MODE_FUNCTION_TEXT);
                }
                break;
            case R.id.rl_sticker:
                resetIcon();
                ivSticker.setImageResource(R.mipmap.icon_fun_sticker);
                ivSticker.getLocationOnScreen(location);
                actionBarBg.changPosition(ActionBarBg.MODE_FUNCTION_STICKER, location[0]);
                if (onBottomActionBarItemClick != null) {
                    onBottomActionBarItemClick.selected(ActionBarBg.MODE_FUNCTION_STICKER);
                }
                break;
            case R.id.rl_graffiti:
                resetIcon();
                ivGraffiti.setImageResource(R.mipmap.icon_fun_graffiti);
                ivGraffiti.getLocationOnScreen(location);
                actionBarBg.changPosition(ActionBarBg.MODE_FUNCTION_GRAFFITI, location[0]);
                if (onBottomActionBarItemClick != null) {
                    onBottomActionBarItemClick.selected(ActionBarBg.MODE_FUNCTION_GRAFFITI);
                }
                break;
            case R.id.rl_edit:
                resetIcon();
                ivEdit.setImageResource(R.mipmap.icon_fun_edit);
                ivEdit.getLocationOnScreen(location);
                actionBarBg.changPosition(ActionBarBg.MODE_FUNCTION_EDIT, location[0]);
                if (onBottomActionBarItemClick != null) {
                    onBottomActionBarItemClick.selected(ActionBarBg.MODE_FUNCTION_EDIT);
                }
                break;

        }

    }

    private void resetIcon(){
        ivFilte.setImageResource(R.mipmap.icon_fun_filter_un);
        ivText.setImageResource(R.mipmap.icon_fun_text_un);
        ivSticker.setImageResource(R.mipmap.icon_fun_sticker_un);
        ivGraffiti.setImageResource(R.mipmap.icon_fun_graffiti_un);
        ivEdit.setImageResource(R.mipmap.icon_fun_edit_un);
    }


    public void setOnBottomActionBarItemClick(OnBottomActionBarItemClick onBottomActionBarItemClick) {
        if (onBottomActionBarItemClick == null) return;
        this.onBottomActionBarItemClick = onBottomActionBarItemClick;

    }

}
