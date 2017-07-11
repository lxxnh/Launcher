package com.android.launcher3.appwidget;

import android.app.Activity;
import android.app.WallpaperManager;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.launcher3.R;

public class SwitchBoardActivity extends Activity implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener{
    private static final String TAG = "SwitchBoardActivity";
    TextView mWifiTv;
    TextView mGprsTv;
    TextView mFlashlightTv;
    TextView mSoundTv;
    TextView mVibrateTv;
    TextView mAutoScreenRotateTv;
    TextView mAirplanemodeTv;
    TextView mAutoBrightTv;
    TextView mScreenOffTv;
    TextView mGpsTv;
    TextView mBluetoothTv;
    TextView mSavingTv;

    TextView mAppManageTv;
    TextView mSettingsTv;

    SeekBar mBrightSeekBar;

    private static Camera mCamera = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_board);

        setupBackground();
        setupView();
    }

    private void setupBackground() {
        View rootView = findViewById(R.id.root);
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        if (wallpaperDrawable != null) {
            rootView.setBackground(wallpaperDrawable);
        }
    }

    private void setupView() {
        mWifiTv              = (TextView)findViewById(R.id.wifi_switch);
        mGprsTv              = (TextView)findViewById(R.id.gprs_switch);
        mFlashlightTv        = (TextView)findViewById(R.id.flash_light_switch);
        mSoundTv             = (TextView)findViewById(R.id.sound_switch);
        mVibrateTv           = (TextView)findViewById(R.id.vibrate_switch);
        mAutoScreenRotateTv  = (TextView)findViewById(R.id.auto_rotate_switch);
        mAirplanemodeTv      = (TextView)findViewById(R.id.aireplane_mode_switch);
        mAutoBrightTv        = (TextView)findViewById(R.id.light_switch);
        mScreenOffTv         = (TextView)findViewById(R.id.screen_off_time_switch);
        mGpsTv               = (TextView)findViewById(R.id.gps_switch);
        mBluetoothTv         = (TextView)findViewById(R.id.bluetooth_switch);
        mSavingTv            = (TextView)findViewById(R.id.saving_switch);
        mAppManageTv         = (TextView)findViewById(R.id.board_app_manage);
        mSettingsTv          = (TextView)findViewById(R.id.board_system_settings);
        mBrightSeekBar       = (SeekBar)findViewById(R.id.change_light_seekbar);

        mWifiTv.setOnClickListener(this);
        mGprsTv.setOnClickListener(this);
        mFlashlightTv.setOnClickListener(this);
        mSoundTv.setOnClickListener(this);
        mVibrateTv.setOnClickListener(this);
        mAutoScreenRotateTv.setOnClickListener(this);
        mAirplanemodeTv.setOnClickListener(this);
        mAutoBrightTv.setOnClickListener(this);
        mScreenOffTv.setOnClickListener(this);
        mGpsTv.setOnClickListener(this);
        mBluetoothTv.setOnClickListener(this);
        mSavingTv.setOnClickListener(this);
        mAppManageTv.setOnClickListener(this);
        mSettingsTv.setOnClickListener(this);
        mBrightSeekBar.setOnSeekBarChangeListener(this);

        boolean isWifiOn = SwitchBoardUtils.isWifiOn(this);
        setCompoundDrawables(mWifiTv, isWifiOn ?
                R.drawable.board_wifi_on2 : R.drawable.board_wifi_off2);

        boolean dataConnected = SwitchBoardUtils.isDataConnectionAvailable(this);
        setCompoundDrawables(mGprsTv, dataConnected ?
                R.drawable.board_gprs_on2 : R.drawable.board_gprs_off2);

        boolean isGpsOpen = SwitchBoardUtils.isGpsOpen(this);
        setCompoundDrawables(mGpsTv, isGpsOpen ?
                R.drawable.board_gps_on2 : R.drawable.board_gps_off2);

        setCompoundDrawables(mFlashlightTv,
                (null != mCamera) ? R.drawable.board_flash_light_on
                        : R.drawable.board_flash_light_off);

        boolean isSilentMode = SwitchBoardUtils.isSlientMode(this);
        setCompoundDrawables(mSoundTv, isSilentMode ?
                R.drawable.board_sound_off : R.drawable.board_sound_on);

        boolean isVibrateMode = SwitchBoardUtils.isVibrateMode(this);
        setCompoundDrawables(mVibrateTv, isVibrateMode ?
                R.drawable.board_vibrate_on2 : R.drawable.board_vibrate_off2);

        boolean isAutoRotate = SwitchBoardUtils.isAutoRotate(this);
        setCompoundDrawables(mAutoScreenRotateTv, isAutoRotate ?
                R.drawable.board_auto_screen_rotate_on2 : R.drawable.board_auto_screen_rotate_off2);

        boolean isAirplaneMode = SwitchBoardUtils.isAirplaneMode(this);
        setCompoundDrawables(mAirplanemodeTv, isAirplaneMode ?
                R.drawable.board_airplanemode_on2 : R.drawable.board_airplanemode_off2);

        boolean isBluetoothEnabled = SwitchBoardUtils.isBluetoothEnabled(this);
        setCompoundDrawables(mBluetoothTv, isBluetoothEnabled ?
                R.drawable.board_bluetooth_on2 : R.drawable.board_bluetooth_off2);

        setCompoundDrawables(mAutoBrightTv, SwitchBoardUtils.getLightImageSrc(this));

        int screenOffTime = SwitchBoardUtils.getScreenOffTime(this);
        Log.d(TAG, "screenOffTime=" + screenOffTime);
        String screenOffTimeStr = (screenOffTime < 60) ?
                (screenOffTime + "s") : (screenOffTime / 60 + "m");
        mScreenOffTv.setText(screenOffTimeStr);
    }

    private void setCompoundDrawables(TextView v, int drawableResId) {
        Drawable drawable = getResources().getDrawable(drawableResId);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), (int) (drawable.getMinimumHeight()));
        v.setCompoundDrawables(null, drawable, null, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wifi_switch:
                boolean isWifiOn = !SwitchBoardUtils.isWifiOn(this);
                setCompoundDrawables(mWifiTv, isWifiOn ?
                        R.drawable.board_wifi_on2 : R.drawable.board_wifi_off2);
                SwitchBoardUtils.toggleWiFi(this);
                break;

           case R.id.gprs_switch:
               boolean dataConnected = SwitchBoardUtils.isDataConnectionAvailable(this);
               setCompoundDrawables(mGprsTv, dataConnected ?
                       R.drawable.board_gprs_on2 : R.drawable.board_gprs_off2);
               SwitchBoardUtils.turnOnDataConnection(
                       this, !SwitchBoardUtils.isDataConnectionAvailable(this));
               break;

           case R.id.flash_light_switch:
               // TODO 开关闪光灯有问题，打开时不亮，关闭时闪一下
               if (null == mCamera) {
                   mCamera = SwitchBoardUtils.turnOnFlashLight(this);
               } else {
                   SwitchBoardUtils.turnOffFlashLight(mCamera);
                   mCamera = null;
               }
               setCompoundDrawables(mFlashlightTv,
                       (null != mCamera) ? R.drawable.board_flash_light_on
                               : R.drawable.board_flash_light_off);
               break;

           case R.id.sound_switch:
               SwitchBoardUtils.toggleSlientMode(this);
               boolean isSilentMode = SwitchBoardUtils.isSlientMode(this);
               setCompoundDrawables(mSoundTv, isSilentMode ?
                       R.drawable.board_sound_off : R.drawable.board_sound_on);
               break;

           case R.id.vibrate_switch:
               SwitchBoardUtils.toggleVibrate(this);
               boolean isVibrateMode = SwitchBoardUtils.isVibrateMode(this);
               setCompoundDrawables(mVibrateTv, isVibrateMode ?
                       R.drawable.board_vibrate_on2 : R.drawable.board_vibrate_off2);
               break;

           case R.id.auto_rotate_switch:
               SwitchBoardUtils.toggleAutoRotation(this);
               boolean isAutoRotate = SwitchBoardUtils.isAutoRotate(this);
               setCompoundDrawables(mAutoScreenRotateTv, isAutoRotate ?
                       R.drawable.board_auto_screen_rotate_on2 : R.drawable.board_auto_screen_rotate_off2);
               break;

           case R.id.aireplane_mode_switch:
               // TODO Permission Denial: not allowed to send broadcast android.intent.action.AIRPL
               SwitchBoardUtils.toggleAirplanemode(this);
               boolean isAirplaneMode = SwitchBoardUtils.isAirplaneMode(this);
               setCompoundDrawables(mAirplanemodeTv, isAirplaneMode ?
                       R.drawable.board_airplanemode_on2 : R.drawable.board_airplanemode_off2);
               break;

           case R.id.light_switch:
               int imageSrc = SwitchBoardUtils.getLightImageSrc(this);
               if (R.drawable.board_light_auto == imageSrc) {
                   SwitchBoardUtils.setCustomSystemBrightness(this, R.drawable.board_light_full);
               } else if (R.drawable.board_light_full == imageSrc) {
                   SwitchBoardUtils.setCustomSystemBrightness(this, R.drawable.board_light_half);
               } else if (R.drawable.board_light_half == imageSrc) {
                   SwitchBoardUtils.setCustomSystemBrightness(this, R.drawable.board_light_dark);
               } else {
                   SwitchBoardUtils.setCustomSystemBrightness(this, R.drawable.board_light_auto);
               }
               setCompoundDrawables(mAutoBrightTv, SwitchBoardUtils.getLightImageSrc(this));
               break;

           case R.id.screen_off_time_switch:
               int screenOffTime = SwitchBoardUtils.getScreenOffTime(this);
               if (15 == screenOffTime) {
                   SwitchBoardUtils.setScreenOffTime(this, 60);
               } else if (60 == screenOffTime) {
                   SwitchBoardUtils.setScreenOffTime(this, 2 * 60);
               } else if (2 * 60 == screenOffTime) {
                   SwitchBoardUtils.setScreenOffTime(this, 3 * 60);
               } else if (3 * 60 == screenOffTime){
                   SwitchBoardUtils.setScreenOffTime(this, 5 * 60);
               } else if (5 * 60 == screenOffTime) {
                   SwitchBoardUtils.setScreenOffTime(this, 10 * 60);
               } else if (10 * 60 == screenOffTime) {
                   SwitchBoardUtils.setScreenOffTime(this, 30 * 60);
               } else {
                   SwitchBoardUtils.setScreenOffTime(this, 15);
               }
               screenOffTime = SwitchBoardUtils.getScreenOffTime(this);
               String screenOffTimeStr = (screenOffTime < 60) ?
                       (screenOffTime + "s") : (screenOffTime / 60 + "m");
               mScreenOffTv.setText(screenOffTimeStr);

               break;

           case R.id.gps_switch:
               //boolean isGpsOpen = !SwitchBoardUtils.isGpsOpen(this);
               //setCompoundDrawables(mGpsTv, isGpsOpen ?
               //        R.drawable.board_gps_on2 : R.drawable.board_gps_off2);
               SwitchBoardUtils.toggleGPS(this);
               break;

           case R.id.bluetooth_switch:
               SwitchBoardUtils.toggleBluetooth(this);
               boolean isBluetoothEnabled = SwitchBoardUtils.isBluetoothEnabled(this);
               setCompoundDrawables(mBluetoothTv, isBluetoothEnabled ?
                       R.drawable.board_bluetooth_on2 : R.drawable.board_bluetooth_off2);
               break;

           case R.id.saving_switch:
               // TODO 广告弹窗
               break;

            case R.id.board_app_manage:
                SwitchBoardUtils.gotoAppManagePage(this);
                break;

            case R.id.board_system_settings:
                SwitchBoardUtils.gotoSettingsPage(this);
                break;

           default:
               break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Log.d(TAG, "seekbar progress=" + progress);
        SwitchBoardUtils.setSystemBrightness(this, progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
