package com.android.launcher3.appwidget;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.launcher3.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenpingwang on 17-6-13.
 */
public class SwitchBoardUtils {
    private static final String TAG = "SwitchBoardUtils";

    /**
     * wifi 是否打开
     *
     * @param context
     * @return
     */
    synchronized public static boolean isWifiOn(Context context) {
        if (context != null) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            return wifiManager.isWifiEnabled();
        } else {
            return false;
        }
    }

    /**
     * 打开/关闭 wifi
     *
     * @param context
     */
    synchronized public static void toggleWiFi(Context context) {
        if (context != null) {
            boolean isWifiOn = isWifiOn(context);
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            wifiManager.setWifiEnabled(!isWifiOn);
        }
    }

    /**
     * 打开闪光灯
     *
     * @param context
     * @return
     */
    public static Camera turnOnFlashLight(Context context) {
        Camera camera = Camera.open();
        Camera.Parameters params = camera.getParameters();
        params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(params);
        camera.startPreview();
        return camera;
    }

    /**
     * 关闭闪光灯
     *
     * @param camera
     */
    public static void turnOffFlashLight(Camera camera) {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
        }
    }

    /**
     * 获取当前亮度对应的背景图
     *
     * @param context
     * @return
     */
    public static int getLightImageSrc(Context context) {
        int imageSrc = 0;
        int sysBright = getSystemBrightness(context);
        if (isAutoBrightness(context)) {
            imageSrc = R.drawable.board_light_auto;
        } else if (sysBright >= 250) {
            imageSrc = R.drawable.board_light_full;
        } else if (sysBright >= 128) {
            imageSrc = R.drawable.board_light_half;
        } else {
            imageSrc = R.drawable.board_light_dark;
        }
        return imageSrc;
    }

    public static void setCustomSystemBrightness(Context context, int imageSrc) {
        if (R.drawable.board_light_auto == imageSrc) {
            startAutoBrightness(context);
        } else if (R.drawable.board_light_half == imageSrc) {
            stopAutoBrightness(context);
            setSystemBrightness(context, 128);
        } else if (R.drawable.board_light_dark == imageSrc) {
            stopAutoBrightness(context);
            setSystemBrightness(context, 50);
        } else {
            stopAutoBrightness(context);
            setSystemBrightness(context, 255);
        }
    }

    /**
     * 获得系统亮度
     *
     * @return
     */
    public static int getSystemBrightness(Context context) {
        int systemBrightness = 0;
        try {
            systemBrightness = Settings.System.getInt(
                    context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return systemBrightness;
    }

    /**
     * 判断是否开启了自动亮度调节
     */
    public static boolean isAutoBrightness(Context context) {
        boolean isAutoBrightness = false;
        try {
            isAutoBrightness = Settings.System.getInt(
                    context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE)
                    == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        return isAutoBrightness;
    }

    /**
     * 设置系统亮度, 程序退出之后亮度依旧有效
     *
     * @param context
     * @param brightness
     */
    public static void setSystemBrightness(Context context, int brightness) {
        if (brightness < 1) {
            brightness = 1;
        }

        if (brightness > 255) {
            brightness = 255;
        }
        saveBrightness(context, brightness);
    }

    /**
     * 停止自动亮度调节
     *
     * @param context
     */
    public static void stopAutoBrightness(Context context) {
        Settings.System.putInt(context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }

    /**
     * 开启亮度自动调节
     *
     * @param context
     */
    public static void startAutoBrightness(Context context) {
        Settings.System.putInt(context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }

    /**
     * 保存亮度设置状态
     *
     * @param context
     * @param brightness
     */
    public static void saveBrightness(Context context, int brightness) {
        Uri uri = android.provider.Settings.System
                .getUriFor(Settings.System.SCREEN_BRIGHTNESS);
        android.provider.Settings.System.putInt(context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, brightness);
        context.getContentResolver().notifyChange(uri, null);
    }

    /**
     * 设置数据连接状态
     *
     * @param cxt
     * @param state
     * @return
     */
    public static boolean setDataConnectionState(Context cxt, boolean state) {
        ConnectivityManager connectivityManager = null;
        Class connectivityManagerClz = null;
        try {
            connectivityManager = (ConnectivityManager) cxt
                    .getSystemService("connectivity");
            connectivityManagerClz = connectivityManager.getClass();
            Method method = connectivityManagerClz.getMethod(
                    "setMobileDataEnabled", new Class[]{boolean.class});
            method.invoke(connectivityManager, state);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int bv = Build.VERSION.SDK_INT;

    public static boolean turnOnDataConnection(Context context, boolean ON) {
        Intent intent=new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
        ComponentName cName = new ComponentName("com.android.settings",
                "com.android.settings.Settings$DataUsageSummaryActivity");
        intent.setComponent(cName);
        context.startActivity(intent);
//        try{
//            if(bv == Build.VERSION_CODES.FROYO) {
//                Method dataConnSwitchmethod;
//                Class<?> telephonyManagerClass;
//                Object ITelephonyStub;
//                Class<?> ITelephonyClass;
//
//                TelephonyManager telephonyManager = (TelephonyManager) context
//                        .getSystemService(Context.TELEPHONY_SERVICE);
//
//                telephonyManagerClass = Class.forName(telephonyManager.getClass().getName());
//                Method getITelephonyMethod = telephonyManagerClass.getDeclaredMethod("getITelephony");
//                getITelephonyMethod.setAccessible(true);
//                ITelephonyStub = getITelephonyMethod.invoke(telephonyManager);
//                ITelephonyClass = Class.forName(ITelephonyStub.getClass().getName());
//
//                if (ON) {
//                    dataConnSwitchmethod = ITelephonyClass
//                            .getDeclaredMethod("enableDataConnectivity");
//                } else {
//                    dataConnSwitchmethod = ITelephonyClass
//                            .getDeclaredMethod("disableDataConnectivity");
//                }
//                dataConnSwitchmethod.setAccessible(true);
//                dataConnSwitchmethod.invoke(ITelephonyStub);
//            } else {
//                //log.i("App running on Ginger bread+");
//                final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//                final Class<?> conmanClass = Class.forName(conman.getClass().getName());
//                final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
//                iConnectivityManagerField.setAccessible(true);
//                final Object iConnectivityManager = iConnectivityManagerField.get(conman);
//                final Class<?> iConnectivityManagerClass =  Class.forName(iConnectivityManager.getClass().getName());
//                final Method setMobileDataEnabledMethod =
//                        iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
//                setMobileDataEnabledMethod.setAccessible(true);
//                setMobileDataEnabledMethod.invoke(iConnectivityManager, ON);
//            }
//
//            return true;
//        }catch(ClassNotFoundException e){
//            e.printStackTrace();
//            return false;
//        } catch (NoSuchFieldException e){
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
        return false;
    }

    /**
     * check mobile connect(gprs)
      */
    public static boolean isDataConnectionAvailable(final Context context) {
        boolean hasMobileCon = false;

        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfos = cm.getAllNetworkInfo();
        for (NetworkInfo net : netInfos) {
            String type = net.getTypeName();
            if (type.equalsIgnoreCase("MOBILE")) {
                if (net.isConnected()) {
                    hasMobileCon = true;
                }
            }
        }
        return hasMobileCon;
    }

    /**
     * 是否是静音模式
     * @param context
     * @return
     */
    public static boolean isSlientMode(Context context) {
        AudioManager audioManager = (AudioManager)context.getSystemService(context.AUDIO_SERVICE);
        return audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT;
    }

    /**
     * 切换静音模式
     * @param context
     * @return
     */
    public static void toggleSlientMode(Context context) {
        AudioManager audioManager = (AudioManager)context.getSystemService(context.AUDIO_SERVICE);
        if (isSlientMode(context)) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        } else {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        }
    }

    /**
     * GPS开关
     * 当前若关则打开
     * 当前若开则关闭
     */
    public static void toggleGPS(Context context) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(intent);
    }

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     * @param context
     * @return true 表示开启
     */
    public static final boolean isGpsOpen(Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }

        return false;
    }

    public static boolean isVibrateMode(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int ringerMode = audioManager.getRingerMode();
        return AudioManager.RINGER_MODE_VIBRATE == ringerMode;
    }

    public static void toggleVibrate(Context context) {
        if (isVibrateMode(context)) {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        } else {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        }
    }

    public static boolean isAutoRotate(Context context) {
        return 1 == getRotationStatus(context);
    }

    /**
     * 得到屏幕旋转的状态
     */
    public static int getRotationStatus(Context context) {
        int status = 0;
        try {
            status = android.provider.Settings.System.getInt(context.getContentResolver(),
                    android.provider.Settings.System.ACCELEROMETER_ROTATION);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return status;
    }

    /**
     *
     * @param resolver
     * @param status
     */
    public static void setRotationStatus(ContentResolver resolver, int status) {
        Uri uri = android.provider.Settings.System.getUriFor("accelerometer_rotation");
        android.provider.Settings.System.putInt(resolver, "accelerometer_rotation", status);
        resolver.notifyChange(uri, null);
    }

    public static void toggleAutoRotation(Context context) {
        if (isAutoRotate(context)) {
            setRotationStatus(context.getContentResolver(), 0);
        } else {
            setRotationStatus(context.getContentResolver(), 1);
        }
    }

    public static void toggleAirplanemode(Context context) {
        boolean on = isAirplaneMode(context);
        if (on) {
            Settings.System.putInt(context.getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, 0);
            Intent localIntent1 = new Intent(
                    "android.intent.action.AIRPLANE_MODE").putExtra("state", false);
            context.sendBroadcast(localIntent1);
        } else {
            Settings.System.putInt(context.getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, 1);
            Intent localIntent1 = new Intent(
                    "android.intent.action.AIRPLANE_MODE").putExtra("state", true);
            context.sendBroadcast(localIntent1);
        }
    }

    /**
     * 判断手机是否是飞行模式
     * @param context
     * @return
     */
    public static boolean isAirplaneMode(Context context){
        int isAirplaneMode = Settings.System.getInt(context.getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, 0) ;
        return (isAirplaneMode == 1) ? true : false;
    }

    public static boolean isBluetoothEnabled(Context context) {
        BluetoothAdapter blueadapter= BluetoothAdapter.getDefaultAdapter();
        if (blueadapter != null) {
            return blueadapter.isEnabled();
        }
        return false;
    }

    public static void toggleBluetooth(Context context) {
        BluetoothAdapter bluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            if (isBluetoothEnabled(context)) {
                bluetoothAdapter.disable();
            } else {
                bluetoothAdapter.enable();
            }
        }
    }

    /**
     * 获得休眠时间 秒
     */
    public static int getScreenOffTime(Context context) {
        int screenOffTime = 0;
        try {
            screenOffTime = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (Settings.SettingNotFoundException localException) {
            localException.printStackTrace();
        }
        return screenOffTime / 1000;
    }

    /**
     * 设置休眠时间 秒
     */
    public static void setScreenOffTime(Context context, int seconds) {
        Settings.System.putInt(context.getContentResolver(),
                Settings.System.SCREEN_OFF_TIMEOUT, seconds * 1000);
    }

    public static void gotoAppManagePage(Context context) {
        // Intent intent =  new Intent();
        // intent.setAction("android.intent.action.MAIN");
        // intent.setClassName("com.android.settings", "com.android.settings.ManageApplications");
        // context.startActivity(intent);
        Intent intent =  new Intent(Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS);
        context.startActivity(intent);
    }

    public static void gotoSettingsPage(Context context) {
        Intent intent =  new Intent(Settings.ACTION_SETTINGS);
        context.startActivity(intent);
    }

//    private boolean addWeatherWidget(/*SQLiteDatabase db,*//* ContentValues values*//*,TypedArray a*/) {
//
//        String packageName = "com.android.launcher3";//a.getString(R.styleable.Favorite_packageName);
//        String className = "com.android.launcher3.appwidget.SwitchBoardProvider";//a.getString(R.styleable.Favorite_className);
////        if(packageName == null || className == null){
////            return false;
////        }
//
//        final int[] bindSources = new int[] {1024
//               /* Favorites.ITEM_TYPE_WIDGET_WEATHER,*/
//        };
//
//        final ArrayList<ComponentName> bindTargets = new ArrayList<ComponentName>();
//        bindTargets.add(new ComponentName(packageName,  className));
//
//        boolean allocatedAppWidgets = false;
//
//        // Try binding to an analog clock widget
//        try {
//            int appWidgetId = mAppWidgetHost.allocateAppWidgetId();
//
//            values.put(Favorites.ITEM_TYPE, Favorites.ITEM_TYPE_WIDGET_WEATHER);
//            values.put(Favorites.SPANX,a.getString(R.styleable.Favorite_spanX));
//            values.put(Favorites.SPANY, a.getString(R.styleable.Favorite_spanY));
//            values.put(Favorites.APPWIDGET_ID, appWidgetId);
//            db.insert(TABLE_FAVORITES, null, values);
//
//            allocatedAppWidgets = true;
//            Log.i(LOG_TAG, "addWeatherWidget -- allocatedAppWidgets = "+allocatedAppWidgets);
//        } catch (RuntimeException ex) {
//            Log.e(LOG_TAG, "Problem allocating appWidgetId", ex);
//        }
//        // If any appWidgetIds allocated, then launch over to binder
//        if (allocatedAppWidgets) {
//            launchAppWidgetBinder(bindSources, bindTargets);
//        }
//
//        return allocatedAppWidgets;
//    }

    public static void addSwitchBoardWidget(Activity context) {
        int APPWIDGET_HOST_ID = 1024;
        AppWidgetHost appWidgetHost = new AppWidgetHost(
                context.getApplicationContext(), APPWIDGET_HOST_ID);
        int appWidgetId = appWidgetHost.allocateAppWidgetId();
        AppWidgetManager appWidgetManager =
                AppWidgetManager.getInstance(context.getApplicationContext());
        List<AppWidgetProviderInfo> providers = appWidgetManager.getInstalledProviders();

        if (providers == null) {
            Log.e(TAG, "failed to find installed widgets ");
            return;
        }

        final int providerCount = providers.size();
        AppWidgetProviderInfo appWidgetProviderInfo = null;

        for (int i = 0; i < providerCount ; i++) {
            ComponentName provider = providers.get(i).provider;
            //Log.d(TAG, provider.getPackageName());
            if (provider != null && provider.getPackageName().
                    equals("com.android.launcher3")) {
                appWidgetProviderInfo = providers.get(i);
                break;
            }
        }
        if (appWidgetProviderInfo == null) {
            Log.e(TAG, "failed to find recommendations widget ");
            return;
        }

        int sdkVersion = Integer.valueOf(android.os.Build.VERSION.SDK);

        if (sdkVersion > 15) {
            final String methodName = "bindAppWidgetIdIfAllowed";
            boolean success = bindAppWidgetId(appWidgetManager,
                    appWidgetId, appWidgetProviderInfo.provider, methodName);
            if (!success) {
                addWidgetPermission(context, appWidgetManager);
                boolean bindAllowed = bindAppWidgetId(appWidgetManager,
                        appWidgetId, appWidgetProviderInfo.provider, methodName);
                if (!bindAllowed) {
                    Log.e(TAG, " failed to bind widget id : " + appWidgetId);
                    return;
                }
            }
        } else {
            boolean success = bindAppWidgetId(appWidgetManager,
                    appWidgetId, appWidgetProviderInfo.provider, "bindAppWidgetId");
            if (!success) {
                Log.e(TAG, " failed to bind widget id : " + appWidgetId);
                return;
            }
        }

        Log.d(TAG, " successful to bind widget id : " + appWidgetId);
        AppWidgetHostView hostView = appWidgetHost.createView(context, appWidgetId, appWidgetProviderInfo);
        appWidgetHost.startListening();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, appWidgetProviderInfo.minHeight);
        LinearLayout widgetLayout = null;//(LinearLayout) context.findViewById(R.id.switch_board_widget_container);
        if (widgetLayout != null) {
            widgetLayout.addView(hostView, params);
        } else {
            Log.e(TAG, "widgetLayout is null");
        }
    }

    public static void addWidgetPermission(Context context, AppWidgetManager appWidgetManager) {
        String methodName = "setBindAppWidgetPermission";
        try {
            Class [] argsClass = new Class[]{String.class, boolean.class};
            Method method = appWidgetManager.getClass().getMethod(methodName, argsClass);
            Object [] args = new Object[]{context.getPackageName(), true};
            try {
                method.invoke(appWidgetManager, args);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static boolean bindAppWidgetId(AppWidgetManager appWidgetManager,
                                          int appWidgetId, ComponentName componentName,
                                          String methodName) {
        boolean success = false;
        Class [] argsClass = new Class[]{int.class, ComponentName.class};
        try {
            Method method = appWidgetManager.getClass().getMethod(methodName, argsClass);
            Object [] args = new Object[]{appWidgetId, componentName};
            try {
                method.invoke(appWidgetManager, args);
                success = true;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return success;
    }
}
