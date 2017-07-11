package com.android.launcher3.appwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.util.Log;
import android.widget.RemoteViews;

import com.android.launcher3.R;

/**
 * Created by wenpingwang on 17-6-9.
 */
public class SwitchBoardProvider extends AppWidgetProvider {
    private static final String TAG = "SwitchBoardProvider";
    public static final String ACTION_WIFI_SWITCH = "com.launcher.action.wifi.switch";
    public static final String ACTION_LIGHT_SWITCH = "com.launcher.action.light.switch";
    public static final String ACTION_GPRS_SWITCH = "com.launcher.action.gprs.switch";
    public static final String ACTION_SOUND_SWITCH = "com.launcher.action.sound.switch";
    public static final String ACTION_FLASH_LIGHT_SWITCH = "com.launcher.action.flashlight.switch";
    public static final String ACTION_MENU = "com.launcher.action.menu";

    private static Camera mCamera = null;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        Log.d(TAG, "onUpdate333");
        updateAppWidget(context);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private void setupImageSrc(Context context, RemoteViews mRemoteViews) {
        if (mRemoteViews != null) {
            // wifi state
            boolean isWifiOn = !SwitchBoardUtils.isWifiOn(context);
            Log.d(TAG, "iswifion=" + isWifiOn);
            mRemoteViews.setImageViewResource(R.id.wifi_switch,
                    isWifiOn ? R.drawable.board_wifi_on : R.drawable.board_wifi_off);
            // light state
            mRemoteViews.setImageViewResource(R.id.light_switch,
                    SwitchBoardUtils.getLightImageSrc(context));
            // mobile connection
            boolean dataConnected = SwitchBoardUtils.isDataConnectionAvailable(context);
            Log.d(TAG, "dataConnected=" + dataConnected);
            mRemoteViews.setImageViewResource(R.id.gprs_switch, dataConnected ?
                    R.drawable.board_gprs_on : R.drawable.board_gprs_off);
            // silent mode
            boolean isSilentMode = SwitchBoardUtils.isSlientMode(context);
            Log.d(TAG, "isSilentMode=" + isSilentMode);
            mRemoteViews.setImageViewResource(R.id.sound_switch, isSilentMode ?
                    R.drawable.board_sound_off : R.drawable.board_sound_on);
            // flash light
            Log.d(TAG, "setupImageSrc mCamera=" + mCamera);
            mRemoteViews.setImageViewResource(R.id.flash_light_switch,
                    (null != mCamera) ? R.drawable.board_flash_light_on
                            : R.drawable.board_flash_light_off);
        } else {
            Log.e(TAG, "!!!! mRemoteViews is null !!!!");
        }
    }

    private RemoteViews setupClickPendingIntent(Context context) {
        RemoteViews remoteViews=new RemoteViews(context.getPackageName(), R.layout.switchboard_layout);
        Intent intent=new Intent(ACTION_WIFI_SWITCH);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.wifi_switch, pendingIntent);

        intent = new Intent(ACTION_LIGHT_SWITCH);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.light_switch, pendingIntent);

        intent = new Intent(ACTION_GPRS_SWITCH);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.gprs_switch, pendingIntent);

        intent = new Intent(ACTION_SOUND_SWITCH);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.sound_switch, pendingIntent);

        intent = new Intent(ACTION_FLASH_LIGHT_SWITCH);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.flash_light_switch, pendingIntent);

        intent = new Intent(ACTION_MENU);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.menu_btn, pendingIntent);

        return remoteViews;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d(TAG, "onReceive" + ", action=" + intent.getAction());

        if (intent.getAction().equals(ACTION_WIFI_SWITCH)) {
            //Toast.makeText(context, "正在操作 wifi", 1).show();
            updateAppWidget(context);
            SwitchBoardUtils.toggleWiFi(context);
        } else if (intent.getAction().equals(ACTION_LIGHT_SWITCH)) {
            //Toast.makeText(context, "点击了light", Toast.LENGTH_LONG).show();
            int imageSrc = SwitchBoardUtils.getLightImageSrc(context);
            if (R.drawable.board_light_auto == imageSrc) {
                SwitchBoardUtils.setCustomSystemBrightness(context, R.drawable.board_light_full);
            } else if (R.drawable.board_light_full == imageSrc) {
                SwitchBoardUtils.setCustomSystemBrightness(context, R.drawable.board_light_half);
            } else if (R.drawable.board_light_half == imageSrc) {
                SwitchBoardUtils.setCustomSystemBrightness(context, R.drawable.board_light_dark);
            } else {
                SwitchBoardUtils.setCustomSystemBrightness(context, R.drawable.board_light_auto);
            }
            updateAppWidget(context);
        } else if (intent.getAction().equals(ACTION_GPRS_SWITCH)) {
            //Toast.makeText(context, "点击了gprs", 1).show();
            // TODO java.lang.NoSuchMethodException: setMobileDataEnabled
            SwitchBoardUtils.turnOnDataConnection(
                    context, !SwitchBoardUtils.isDataConnectionAvailable(context));
            updateAppWidget(context);
        } else if (intent.getAction().equals(ACTION_SOUND_SWITCH)) {
            //Toast.makeText(context, "点击了sound", Toast.LENGTH_LONG).show();
            SwitchBoardUtils.toggleSlientMode(context);
            updateAppWidget(context);
        } else if (intent.getAction().equals(ACTION_FLASH_LIGHT_SWITCH)) {
            //Toast.makeText(context, "点击了flash light", 1).show();
            // TODO 开关闪光灯有问题，打开时不亮，关闭时闪一下
            Log.d(TAG, "onReceive mCamera=" + mCamera);
            if (null == mCamera) {
                mCamera = SwitchBoardUtils.turnOnFlashLight(context);
            } else {
                SwitchBoardUtils.turnOffFlashLight(mCamera);
                mCamera = null;
            }
            updateAppWidget(context);
        } else if (intent.getAction().equals(ACTION_MENU)) {
            //Toast.makeText(context, "点击了menu", Toast.LENGTH_LONG).show();
            context.startActivity(new Intent(context, SwitchBoardActivity.class));
        }
    }

    private void updateAppWidget(Context context) {
        final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        final ComponentName cn = new ComponentName(context, SwitchBoardProvider.class);
        RemoteViews remoteViews = setupClickPendingIntent(context);
        setupImageSrc(context, remoteViews);
        mgr.updateAppWidget(cn, remoteViews);
    }
}
