package com.android.launcher3.theme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.android.launcher3.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lxx on 2/6/17.
 */

public class Utils {
    public static final String NAME_KEY = "theme_key";
    public static final String BITMAP_KEY = "bitmap_key";
    public static final String SHARED_PREFRENCE = "com.android.mlauncher.prefs";
    public static final String SHOW_FLOAT_BUTTON = "show_float_button";

    public static List<HashMap<String, Object>> getThemes(Context context) {
        List<HashMap<String, Object>> allThemes = new ArrayList<HashMap<String, Object>>();
        try {
            String[] themes = context.getAssets().list(context.getString(R.string.theme));
            for (String theme : themes) {
                String path = context.getString(R.string.theme) + "/" + theme + "/"
                        + context.getString(R.string.preview) + "/" + "thumbnail.jpg";
                Bitmap bitmap = BitmapFactory
                        .decodeStream(context.getAssets().open(path));
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put(Utils.NAME_KEY, theme);
                map.put(Utils.BITMAP_KEY, bitmap);
                if (theme.equals("default")) {
                    allThemes.add(0, map);
                } else {
                    allThemes.add(map);
                }
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return allThemes;
    }

    private String trimExtension(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int i = filename.lastIndexOf('.');
            if ((i > -1) && (i < (filename.length()))) {
                return filename.substring(0, i);
            }
        }
        return null;
    }

    private boolean isFileEffect(String name) {
        File file = new File(name);
        if (file.exists() && file.isDirectory() && (file.list().length > 0))
            return true;
        else
            return false;

    }
}
