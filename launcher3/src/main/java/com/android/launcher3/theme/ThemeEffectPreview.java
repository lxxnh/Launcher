package com.android.launcher3.theme;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.app.ActionBar;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.content.ContentValues;

import com.android.launcher3.Launcher;
import com.android.launcher3.R;

public class ThemeEffectPreview extends Activity implements OnClickListener {
    private ViewPager mViewPager;
    private List<View> mViews;
    private ImageView[] mImageViews;
    private LinearLayout mImageLayout;
    private Button mApplyBtn;
    private SharedPreferences mSharedPreferences;
    private MyViewPageAdapter mPageAdapter;
    private String mTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.theme_effect_preview);
        initLayout();
        initViews();
    }

    private void initLayout() {
        mViewPager = (ViewPager) findViewById(R.id.theme_effect_view_pager);
        mImageLayout = (LinearLayout) findViewById(R.id.theme_effect_image_linear);
        mApplyBtn = (Button) findViewById(R.id.theme_effect_btn);
        mApplyBtn.setOnClickListener(this);
        mImageViews = new ImageView[2];
        mImageViews[0] = (ImageView) mImageLayout.getChildAt(0);
        mImageViews[1] = (ImageView) mImageLayout.getChildAt(1);

    }

    private void initViews() {
        mViews = new ArrayList<View>();
        mSharedPreferences = getSharedPreferences(Utils.SHARED_PREFRENCE, Context.MODE_PRIVATE);
        mTheme = getIntent().getStringExtra(Utils.NAME_KEY);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(mTheme);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        try {
            Bitmap previewBitmap_01 = BitmapFactory.decodeStream(getAssets().open(getString(R.string.theme) + "/" + mTheme + "/" + getString(R.string.preview)
                    + "/" + "preview_01.jpg"));
            Bitmap previewBitmap_02 = BitmapFactory.decodeStream(getAssets().open(getString(R.string.theme) + "/" + mTheme + "/" + getString(R.string.preview)
                    + "/" + "preview_02.jpg"));
            ImageView imageView_01 = new ImageView(this);
            imageView_01.setImageBitmap(previewBitmap_01);
            ImageView imageView_02 = new ImageView(this);
            imageView_02.setImageBitmap(previewBitmap_02);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER;
            mViews.add(imageView_01);
            mViews.add(imageView_02);
            mPageAdapter = new MyViewPageAdapter(mViews);
            mViewPager.setAdapter(mPageAdapter);
            mViewPager.setCurrentItem(0);
            mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private class MyViewPageAdapter extends PagerAdapter {
        public List<View> mListViews;

        public MyViewPageAdapter(List<View> listViews) {
            this.mListViews = listViews;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mListViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == (arg1);
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(mListViews.get(arg1));
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(mListViews.get(arg1), 0);
            return mListViews.get(arg1);
        }
    }

    private class MyOnPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageSelected(int arg0) {
            // TODO Auto-generated method stub
        }

    }

    protected class WallPaperSetTask extends AsyncTask<Void, Void, Boolean> {
        private String theme;
        private Context mContext;

        public WallPaperSetTask(String theme, Context context) {
            this.theme = theme;
            mContext = context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(mContext);
            try {
                String path = mContext.getString(R.string.theme) + "/" + theme + "/"
                        + mContext.getString(R.string.wallpaper) + "/" + mContext.getString(R.string.wallpaper) + ".jpg";
                wallpaperManager.setStream(mContext.getAssets().open(path));
                ContentValues value_lock_screen = new ContentValues();
                value_lock_screen.put("_id", 1);
                value_lock_screen.put("flag", 0);
                ContentValues value_main_menu = new ContentValues();
                value_main_menu.put("_id", 2);
                value_main_menu.put("flag", 0);
                Uri url = Uri.parse("content://" + "com.android.launcher3.settings" + "/" + "wallpaper");
                Uri newUrl_lock_screen = ThemeEffectPreview.this.getContentResolver().insert(url, value_lock_screen);
                Uri newUrl_main_menu = ThemeEffectPreview.this.getContentResolver().insert(url, value_main_menu);
                if (newUrl_lock_screen == null) {
                    mContext.getContentResolver().update(url, value_lock_screen, "_id =  ?", new String[]{"1"});
                }
                if (newUrl_main_menu == null) {
                    mContext.getContentResolver().update(url, value_main_menu, "_id =  ?", new String[]{"2"});
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPreExecute() {
            mApplyBtn.setClickable(false);
            mSharedPreferences.edit()
                    .putString(Utils.NAME_KEY, getIntent().getStringExtra(Utils.NAME_KEY)).commit();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            String path = mContext.getString(R.string.theme) + "/" + theme + "/"
                    + mContext.getString(R.string.wallpaper) + "/" + mContext.getString(R.string.wallpaper) + ".jpg";
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(mContext.getAssets().open(path));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Drawable d = new BitmapDrawable(bitmap);
            Intent intent = new Intent(ThemeEffectPreview.this, Launcher.class);
            startActivity(intent);
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }

    }

    @Override
    public void onClick(View v) {
        WallPaperSetTask task = new WallPaperSetTask(mTheme, this);
        task.execute();
    }
}
