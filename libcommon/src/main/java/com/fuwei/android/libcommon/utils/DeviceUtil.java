package com.fuwei.android.libcommon.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import androidx.core.app.ActivityCompat;

import com.fuwei.android.libcommon.BasePrefHelper;
import com.fuwei.android.libcommon.encrypt.MD5Util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Created by fuwei on 11/2/21.
 */
public class DeviceUtil {
    public static final String BT_PREFIX_NAME = "BinGlass_";

    public static String getBleDeviceNameBySn(String sn) {
        if (!TextUtils.isEmpty(sn) && sn.length() > 4) {
            return BT_PREFIX_NAME + sn.substring(sn.length() - 4);
        }
        return sn;
    }

    /**
     * <p><b>IMEI.</b></p> Returns the unique device ID, for example, the IMEI for GSM and the MEID
     * or ESN for CDMA phones. Return null if device ID is not available.
     * <p>
     * Requires Permission: READ_PHONE_STATE
     *
     * @param context
     * @return
     */
    @SuppressLint("all")
    public synchronized static String getDeviceId(Context context) {
        if (context == null) {
            return "";
        }
        String imei = BasePrefHelper.getDeviceId(context);

        if (!TextUtils.isEmpty(imei)) {
            return imei;
        }

        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm == null || TextUtils.isEmpty(tm.getDeviceId())) {
                // 双卡双待需要通过phone1和phone2获取imei，默认取phone1的imei。
                tm = (TelephonyManager) context.getSystemService("phone1");
            }

            if (tm != null) {
                imei = tm.getDeviceId();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(imei)) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    imei = Build.getSerial();
                } else {
                    imei = Build.SERIAL;
                }
            } catch (Exception e) {
                e.printStackTrace();
                imei = "";
            }
        }

        if (TextUtils.isEmpty(imei) || "unknown".equalsIgnoreCase(imei)) {

            //使用自定义方式 手机号 + 手机型号 生产MD5
            String userPhoneNumber = BasePrefHelper.getUserPhone(context);
            if (TextUtils.isEmpty(userPhoneNumber)) {
                userPhoneNumber = BasePrefHelper.getUserPhone(context);
            }

            if (!TextUtils.isEmpty(userPhoneNumber)) {
                String brand = Build.BRAND;
                if (!TextUtils.isEmpty(brand)) {
                    brand = brand.replace(" ", "");
                }
                String model = Build.MODEL;
                if (!TextUtils.isEmpty(model)) {
                    model = model.replace(" ", "");
                }
                imei = MD5Util.getStringMD5(userPhoneNumber + brand + model);
            }else{
                String brand = Build.BRAND;
                if (!TextUtils.isEmpty(brand)) {
                    brand = brand.replace(" ", "");
                }
                String model = Build.MODEL;
                if (!TextUtils.isEmpty(model)) {
                    model = model.replace(" ", "");
                }
                imei = MD5Util.getStringMD5(UUID.randomUUID() + brand + model);
            }
        }

        if ("unknown".equalsIgnoreCase(imei)) {
            imei = "";
        }

        BasePrefHelper.setDeviceId(context, imei);

        return imei;
    }

    /**
     * 获取设备SN码
     * 安卓Q可能获取不到sn码
     *
     * @return
     */
    @SuppressLint("all")
    @Deprecated
    public synchronized static String getSn() {
        String sn = "";
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                sn = Build.getSerial();
            } else {
                sn = Build.SERIAL;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sn;
    }

    /**
     * Returns the serial number of the SIM, if applicable. Return null if it is
     * unavailable.
     * <p>
     * Requires Permission: READ_PHONE_STATE
     *
     * @param context
     * @return
     */
    public synchronized static String getSimSerialNumber(Context context) {
        if (context == null) {
            return "";
        }
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getSimSerialNumber();
    }

    /**
     * A 64-bit number (as a hex string) that is randomly generated on the
     * device's first boot and should remain constant for the lifetime of the
     * device. (The value may change if a factory reset is performed on the
     * device.)
     *
     * @param context
     * @return
     */
    public synchronized static String getAndroidID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    /**
     * 操作系统版本
     *
     * @return
     */
    public static String getOSversion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 设备商
     *
     * @return
     */
    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * 设备型号
     *
     * @return
     */
    public static String getModel() {
        return Build.MODEL;
    }

    /**
     * 序列号
     *
     * @return
     */
    public static String getSerialNumber() {
        String serial = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception ignored) {
        }
        return serial;
    }

    public static boolean isSNStartWithP16() {
        String sn = getSerialNumber();
        boolean flag = false;
        if (TextUtils.isEmpty(sn)) {
            sn = DeviceUtil.getSerialNumber();
        }
        if (sn.startsWith("P16")) {
            flag = true;
        }
        return flag;
    }

    /**
     * 设置亮度改变pwm值
     * 亮度2以上：38
     * 亮度1：36或者37
     *
     * @param level
     */
    public static void changeLightValueByLevel(int level) {
        String sn = getSerialNumber();
        if (level == 1) {
            if (sn.startsWith("P16")) {
                write2DDP_PWM("38");
            } else {
                write2DDP_PWM("37");
            }
        } else if (level == 2) {
            write2DDP_PWM("39");
        } else if (level == 3) {
            write2DDP_PWM("44");
        }

    }

    private static void write2DDP_PWM(String ascCode) {
        BufferedWriter writer = null;
        FileOutputStream fs = null;
        try {
            File file = new File("sys/class/ddp_pwm_debug/ddp_pwm");
            if (!file.exists()) {
            }
            fs = new FileOutputStream(file);
            writer = new BufferedWriter(new OutputStreamWriter(fs));
            writer.write(ascCode);
            writer.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * SD CARD ID
     *
     * @return
     */
    public static synchronized String getSDcardID() {
        try {
            String sdCid = null;
            String[] memBlkArray = new String[]{"/sys/block/mmcblk0", "/sys/block/mmcblk1", "/sys/block/mmcblk2"};
            for (String memBlk : memBlkArray) {
                File file = new File(memBlk);
                if (file.exists() && file.isDirectory()) {
                    Process cmd = Runtime.getRuntime().exec("cat " + memBlk + "/device/cid");
                    BufferedReader br = new BufferedReader(new InputStreamReader(cmd.getInputStream()));
                    sdCid = br.readLine();
                    if (!TextUtils.isEmpty(sdCid)) {
                        return sdCid;
                    }
                }
            }
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取mac地址
     *
     * @param context
     * @return
     */
    public static String getMac(Context context) {
        if (context == null) {
            return "";
        }
        String mac = null;
        try {
            final WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifi != null) {
                WifiInfo info = wifi.getConnectionInfo();
                if ((null != info) && (info.getMacAddress() != null)) {
                    mac = info.getMacAddress();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mac;
    }

    /**
     * 获取mac地址
     * 可以突破android6.0的限制
     *
     * @return
     */
    public static String getWifiMacAddress() {
        try {
            String interfaceName = "wlan0";
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (!intf.getName().equalsIgnoreCase(interfaceName)) {
                    continue;
                }

                byte[] mac = intf.getHardwareAddress();
                if (mac == null) {
                    return "";
                }

                StringBuilder buf = new StringBuilder();
                for (byte aMac : mac) {
                    buf.append(String.format("%02X:", aMac));
                }
                if (buf.length() > 0) {
                    buf.deleteCharAt(buf.length() - 1);
                }
                return buf.toString();
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "";
    }

    /**
     * 获取IMSI
     *
     * @param context
     * @return
     */
    public static String getIMSI(Context context) {

        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        return tm.getSubscriberId();

    }

    /**
     * get sim serial number
     */
    public static String getSimSerialNum(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        return tm.getSimSerialNumber();
    }

    /**
     * 获取屏幕的分辨率
     *
     * @param context
     * @return int array with 2 items. The first item is width, and the second is height.
     */
    public static int[] getScreenResolution(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();

        int[] resolution = new int[2];
        resolution[0] = dm.widthPixels;
        resolution[1] = dm.heightPixels;

        return resolution;
    }

    /**
     * 获取WIFI的Mac地址
     *
     * @param context
     * @return Wifi的BSSID即mac地址
     */
    public static String getWifiBSSID(Context context) {
        if (context == null) {
            return null;
        }

        String mac = null;
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wm.getConnectionInfo();
        if (info != null) {
            mac = info.getBSSID();// 获得本机的MAC地址
        }

        return mac;
    }

    public static String getPackageVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static int getPackageVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * 获取应用包名
     *
     * @param context
     * @return
     */
    public static String getPackageName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packInfo.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取外部应用的context
     *
     * @param context
     * @param packageName
     * @return
     */
    public static Context getOuterContext(Context context, String packageName) {
        Context otherAppsContext = null;
        try {
            otherAppsContext = context.createPackageContext(packageName, Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return otherAppsContext;
    }

    /**
     * 获取系统休眠时间。
     *
     * @return
     */
    public static int getScreenOffTimeOut(Context context) {
        int sleepTime;
        try {
            sleepTime = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            sleepTime = 15 * 1000;
        }
        return sleepTime;
    }


    public static boolean isScreenOn(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return powerManager.isScreenOn();
    }

    /**
     * app是否在前台
     *
     * @param context
     * @param pkg
     * @return
     */
    public static boolean isAppOnForeground(Context context, String pkg) {
        String topPackageName = null;
        ActivityManager manager = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        if (Build.VERSION.SDK_INT >= 21) {//Build.VERSION_CODES.LOLLIPOP[21以后,只能获取自身是不是在top,若不是,则无法获取处于top的app信息]
            List<ActivityManager.RunningAppProcessInfo> pis = manager.getRunningAppProcesses();
            if (pis != null && pis.size() > 0) {
                ActivityManager.RunningAppProcessInfo topAppProcess = pis.get(0);
                if (topAppProcess != null && topAppProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    topPackageName = topAppProcess.processName;
                }
            }
        } else {
            //getRunningTasks() is deprecated since API Level 21 (Android 5.0)
            List list = manager.getRunningTasks(1);
            if (list != null && list.size() > 0) {
                ActivityManager.RunningTaskInfo localRunningTaskInfo = (ActivityManager.RunningTaskInfo) list.get(0);
                topPackageName = localRunningTaskInfo.topActivity.getPackageName();
            }
        }
        return TextUtils.equals(pkg, topPackageName);
    }

    /**
     * Gets the number of cores available in this device, across all processors.
     * Requires: Ability to peruse the filesystem at "/sys/devices/system/cpu"
     *
     * @return The number of cores, or 1 if failed to get result
     */
    public static int getCPUNumCores() {
        try {
            //Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            //Filter to only list the devices we care about
            File[] files = dir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    //Check if filename is "cpu", followed by a single digit number
                    if (Pattern.matches("cpu[0-9]", pathname.getName())) {
                        return true;
                    }
                    return false;
                }
            });
            //Return the number of cores (virtual CPU devices)
            return files.length;
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * 获取系统参数
     *
     * @param configName
     * @return
     */

    public static String getSystemConf(String configName) {
//        try {
//            Process process = Runtime.getRuntime().exec("getprop " + configName);
//            InputStreamReader ir = new InputStreamReader(process.getInputStream());
//            BufferedReader input = new BufferedReader(ir);
//            String value = input.readLine();
//            input.close();
//            ir.close();
//            process.destroy();
//            return value;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return "";
    }

    /**
     * 设置系统状态
     *
     * @param configName
     * @param value
     * @return
     */
    public static void setSystemConf(String configName, String value) {
//        try {
//            Process process = Runtime.getRuntime().exec("setprop " + configName + " " + value);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 获取硬件版本
     *
     * @return
     */
    public static String getHardwareVersion() {
//        return getSystemConf("ro.hardware");
        return "";
    }

    /**
     * 获取rom软件相关版本
     *
     * @return
     */
    public static int getRomSoftVersion() {
        int res = 0;
//        String rom = getSystemConf("ro.function.code");
//        if (!TextUtils.isEmpty(rom)) {
//            res = Integer.parseInt(rom);
//        }

        return res;
    }

    /**
     * 获取rom版本
     */
    public static String getRomVersion() {
//        return getSystemConf("ro.mediatek.version.release");
        return "";
    }


    public static String getShowhqRomVersion() {
        String showHq = "leja";
        String hqRomVer = getRomVersion();
        if (TextUtils.isEmpty(hqRomVer) == false) {
            String[] s = hqRomVer.split("_");
            if (s != null && s.length >= 3) {
                showHq = s[2];
            }
        }
        return showHq;
    }

    /**
     * 获取installed apk版本
     */
    public static PackageInfo getInstalledAppInfo(Context context, String pname) {
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        if (packages != null) {
            for (PackageInfo pinfo : packages) {
                if (pinfo != null && pinfo.packageName.equals(pname)) {
                    return pinfo;
                }
            }
        }

        return null;

    }

    public static Point getScreenSize(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;       // 屏幕宽（像素，如：480px）
        int screenHeight = dm.heightPixels;      // 屏幕高（像素，如：800p）
        return new Point(screenWidth, screenHeight);
    }
}
