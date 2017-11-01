package com.snow.yp.kgdemo.tablayout;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.snow.yp.kgdemo.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFiFragment extends Fragment {


    public BlankFiFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        ((TextView) view.findViewById(R.id.tv_show)).setText("First Blood");
        return view;
    }

}
