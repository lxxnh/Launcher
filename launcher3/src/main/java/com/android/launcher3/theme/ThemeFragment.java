package com.android.launcher3.theme;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.android.launcher3.R;

import java.util.HashMap;
import java.util.List;

public class ThemeFragment extends Fragment {
    private View mView;
    private List<HashMap<String, Object>> mThemes;
    private GridView mGridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("lxx", "create----");
        mView = inflater.inflate(R.layout.theme_picker, null);
        initView();
        initData();
        setListener();
        return mView;
    }

    private void initView() {
        mGridView = (GridView) mView.findViewById(R.id.gridview);
        mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
    }

    private void initData() {
        mThemes = Utils.getThemes(getActivity());
        mGridView.setAdapter(new ImageAdapter(getActivity(), mThemes));
    }

    private void setListener() {
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position == mThemes.size()) {
                } else {
                    Intent intent = new Intent(getActivity(), ThemeEffectPreview.class);
                    intent.putExtra(Utils.NAME_KEY, (String) mThemes.get(position).get(Utils.NAME_KEY));
                    startActivity(intent);
                }
            }
        });
    }
}