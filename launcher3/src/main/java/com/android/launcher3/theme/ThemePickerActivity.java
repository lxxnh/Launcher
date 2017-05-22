package com.android.launcher3.theme;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.android.launcher3.R;

import java.util.ArrayList;

public class ThemePickerActivity extends FragmentActivity implements View.OnClickListener {
    private ArrayList<Fragment> mFragmentList;
    private ArrayList<MagicTextView> mViewList;
    private MagicTextView mTheme;
    private MagicTextView mWallpaper;
    private MagicTextView mLockScreen;
    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.theme_pick_activity);
        initViews();
        initData();
        setListener();

    }

    private void initData() {
        mFragmentList = new ArrayList<Fragment>();
        mFragmentList.add(new ThemeFragment());
        mFragmentList.add(new ThemeFragment());
        mFragmentList.add(new ThemeFragment());

        mViewList = new ArrayList<MagicTextView>();
        mViewList.add(mTheme);
        mViewList.add(mWallpaper);
        mViewList.add(mLockScreen);

        mViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));

    }

    private void initViews() {
        mTheme = (MagicTextView) findViewById(R.id.theme);
        mWallpaper = (MagicTextView) findViewById(R.id.wallpaper);
        mLockScreen = (MagicTextView) findViewById(R.id.lock_screen);
        mTheme.setAlpha(1f);
        float scale = 1 + (float) 1.0 / 5;
        mTheme.setScaleX(scale);
        mTheme.setScaleY(scale);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
    }

    private void setListener() {
        mTheme.setOnClickListener(this);
        mWallpaper.setOnClickListener(this);
        mLockScreen.setOnClickListener(this);
        mViewPager.addOnPageChangeListener(new MyPageChangeListener());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.theme:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.wallpaper:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.lock_screen:
                mViewPager.setCurrentItem(2);
                break;
            default:
                //do nothing
                break;
        }

    }


    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (positionOffset > 0) {
                MagicTextView leftView = mViewList.get(position);
                float leftViewScale = (float) 1.0 + (1 - positionOffset) / 5;
                leftView.setScaleX(leftViewScale);
                leftView.setScaleY(leftViewScale);
                leftView.setAlpha(1 - positionOffset);

                MagicTextView rightView = mViewList.get(position + 1);
                float rightViewScale = (float) 1.0 + positionOffset / 5;
                rightView.setScaleX(rightViewScale);
                rightView.setScaleY(rightViewScale);
                rightView.setAlpha(positionOffset);
            }


        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {


        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }
}
