package com.android.launcher3.theme;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.launcher3.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by lxx on 2/6/17.
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<HashMap<String, Object>> mThemes;

    public ImageAdapter(Context context, List<HashMap<String, Object>> themes) {
        mContext = context;
        mThemes = themes;
    }

    @Override
    public int getCount() {
        return mThemes.size();
    }

    @Override
    public Object getItem(int position) {
        return mThemes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView previewImage = null;
        TextView themeName = null;
        ImageView check = null;
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.theme_grid_item, null);
        }
        previewImage = (ImageView) convertView.findViewById(R.id.grid_item_image);
        themeName = (TextView) convertView.findViewById(R.id.grid_item_theme_name);
        check = (ImageView) convertView.findViewById(R.id.grid_item_check);
        HashMap<String, Object> map = mThemes.get(position);
//        if (((String) map.get(Utils.NAME_KEY)).equals("default")) {
//            themeName.setText(mContext.getString(R.string.default_theme));
//        } else {
            themeName.setText((CharSequence) map.get(Utils.NAME_KEY));
//        }
        previewImage.setImageBitmap((Bitmap) map.get(Utils.BITMAP_KEY));
        String name = mContext.getSharedPreferences(
                Utils.SHARED_PREFRENCE,
                Context.MODE_PRIVATE).getString(Utils.NAME_KEY, "default");
        if (name.equals((String) map.get(Utils.NAME_KEY))) {
            check.setVisibility(View.VISIBLE);
        } else {
            check.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

}
