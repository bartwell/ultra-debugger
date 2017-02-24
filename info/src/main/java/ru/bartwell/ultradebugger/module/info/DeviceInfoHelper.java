package ru.bartwell.ultradebugger.module.info;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import ru.bartwell.ultradebugger.base.html.RawContentPart;
import ru.bartwell.ultradebugger.base.html.Table;
import ru.bartwell.ultradebugger.base.utils.IpUtils;

/**
 * Created by BArtWell on 15.02.2017.
 */

class DeviceInfoHelper {

    private static final long BYTES_IN_MEGABYTE = 1048576L;
    private static final String DEVICE_TYPE_TABLET = "Tablet";
    private static final String DEVICE_TYPE_MOBILE = "Mobile";

    @NonNull
    private Context mContext;
    @NonNull
    private final int[] mCpu;
    @NonNull
    private final ActivityManager.MemoryInfo mMemoryInfo;
    private int mTableRowY;

    DeviceInfoHelper(@NonNull Context context) {
        mContext = context;
        mCpu = getCpuUsageInfo();

        mMemoryInfo = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mMemoryInfo);
    }

    @NonNull
    Table buildTable() {
        Table table = new Table();
        addTableRow(table, R.string.manufacturer, Build.MANUFACTURER);
        addTableRow(table, R.string.model, Build.MODEL);
        addTableRow(table, R.string.device_type, getDeviceType());
        addTableRow(table, R.string.sdk_version, String.valueOf(Build.VERSION.SDK_INT));
        addTableRow(table, R.string.device_id, getDeviceId());
        addTableRow(table, R.string.display_diagonal, getDisplayDiagonalInInches(mContext));
        addTableRow(table, R.string.processors_count, String.valueOf(Runtime.getRuntime().availableProcessors()));
        addTableRow(table, R.string.ip_v4, IpUtils.getIpV4());
        addTableRow(table, R.string.ip_v6, IpUtils.getIpV6());
        addTableRow(table, R.string.network_type, getNetworkType());
        addTableRow(table, R.string.mac, getMacAddress());
        addTableRow(table, R.string.total_memory, String.valueOf(getTotalMemory()));
        addTableRow(table, R.string.free_memory, String.valueOf(getFreeMemory()));
        addTableRow(table, R.string.used_memory, String.valueOf(getUsedMemory()));
        addTableRow(table, R.string.cpu_usage_total, String.valueOf(mCpu[0] + mCpu[1] + mCpu[2] + mCpu[3]));
        addTableRow(table, R.string.cpu_usage_by_system, String.valueOf(mCpu[1]));
        addTableRow(table, R.string.cpu_usage_by_user, String.valueOf(mCpu[0]));
        addTableRow(table, R.string.cpu_idle, String.valueOf(mCpu[2]));
        addTableRow(table, R.string.device_language, Locale.getDefault().getDisplayLanguage());
        addTableRow(table, R.string.locale_country, mContext.getResources().getConfiguration().locale.getCountry());
        addTableRow(table, R.string.date_and_time, DateFormat.getDateTimeInstance().format(new Date()));
        addTableRow(table, R.string.timezone, TimeZone.getDefault().getID());
        addTableRow(table, R.string.timestamp, String.valueOf(TimeUnit.MILLISECONDS.toSeconds(Calendar.getInstance().getTimeInMillis())));
        return table;
    }

    @NonNull
    private String getDeviceType() {
        if (isTablet(mContext)) {
            if (isDisplayMoreThan7Inches(mContext)) {
                return DEVICE_TYPE_TABLET;
            } else
                return DEVICE_TYPE_MOBILE;
        } else {
            return DEVICE_TYPE_MOBILE;
        }
    }

    private void addTableRow(@NonNull Table table, @StringRes int label, @Nullable String value) {
        table.add(0, mTableRowY, new RawContentPart(mContext.getString(label)));
        table.add(1, mTableRowY, new RawContentPart(value));
        mTableRowY++;
    }

    @NonNull
    private String getDeviceId() {
        @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (deviceId == null) {
            deviceId = "emulator";
        } else {
            try {
                byte[] data = deviceId.getBytes();
                MessageDigest messageDigest = java.security.MessageDigest.getInstance("MD5");
                messageDigest.update(data);
                data = messageDigest.digest();
                BigInteger bigInteger = new BigInteger(data).abs();
                deviceId = bigInteger.toString(36);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return deviceId;
    }

    private long getTotalMemory() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                return mMemoryInfo.totalMem / BYTES_IN_MEGABYTE;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private long getFreeMemory() {
        return mMemoryInfo.availMem / BYTES_IN_MEGABYTE;
    }

    private long getUsedMemory() {
        long totalMemory = getTotalMemory();
        if (totalMemory > 0) {
            return totalMemory - getFreeMemory();
        }
        return 0;
    }

    @NonNull
    private String getMacAddress() {
        String mac = getMacAddress("wlan0");
        if (TextUtils.isEmpty(mac)) {
            mac = getMacAddress("eth0");
        }
        return mac;
    }

    @NonNull
    private String getMacAddress(@NonNull String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : interfaces) {
                if (!networkInterface.getName().equalsIgnoreCase(interfaceName)) {
                    continue;
                }
                byte[] mac = networkInterface.getHardwareAddress();
                if (mac == null) {
                    return "";
                }
                StringBuilder stringBuilder = new StringBuilder();
                for (byte b : mac) {
                    stringBuilder.append(String.format("%02X:", b));
                }
                if (stringBuilder.length() > 0) {
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                }
                return stringBuilder.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return "";
    }

    // Returns integer array with 4 elements:
    // user, system, idle and other cpu usage in percentage
    @NonNull
    private int[] getCpuUsageInfo() {
        try {
            String topCommandResult = executeTop();
            if (!TextUtils.isEmpty(topCommandResult)) {
                topCommandResult = topCommandResult.replaceAll(",", "");
                topCommandResult = topCommandResult.replaceAll("User", "");
                topCommandResult = topCommandResult.replaceAll("System", "");
                topCommandResult = topCommandResult.replaceAll("IOW", "");
                topCommandResult = topCommandResult.replaceAll("IRQ", "");
                topCommandResult = topCommandResult.replaceAll("%", "");
                for (int i = 0; i < 10; i++) {
                    topCommandResult = topCommandResult.replaceAll("  ", " ");
                }
                topCommandResult = topCommandResult.trim();
                String[] part = topCommandResult.split(" ");
                int[] cpuUsageAsInt = new int[part.length];
                for (int i = 0; i < part.length; i++) {
                    part[i] = part[i].trim();
                    cpuUsageAsInt[i] = Integer.parseInt(part[i]);
                }
                return cpuUsageAsInt;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new int[4];
    }

    @Nullable
    private String executeTop() {
        java.lang.Process p = null;
        BufferedReader in = null;
        String returnString = null;
        try {
            p = Runtime.getRuntime().exec("top -n 1");
            in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while (returnString == null || returnString.contentEquals("")) {
                returnString = in.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (p != null) {
                try {
                    p.destroy();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return returnString;
    }

    @NonNull
    private String getNetworkType() {
        String networkStatus = "";
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (wifi.isAvailable()) {
                networkStatus = "WiFi";
            } else if (mobile.isAvailable()) {
                networkStatus = getDataType(mContext);
            } else {
                networkStatus = "No network";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return networkStatus;
    }

    private boolean isTablet(@NonNull Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    private boolean isDisplayMoreThan7Inches(@NonNull Context context) {
        try {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            float yInches = displayMetrics.heightPixels / displayMetrics.ydpi;
            float xInches = displayMetrics.widthPixels / displayMetrics.xdpi;
            double diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);
            return diagonalInches >= 7;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @NonNull
    private String getDisplayDiagonalInInches(@NonNull Context context) {
        try {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            float yInches = displayMetrics.heightPixels / displayMetrics.ydpi;
            float xInches = displayMetrics.widthPixels / displayMetrics.xdpi;
            double diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);
            return String.valueOf(diagonalInches);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private String getDataType(Context context) {
        String type = "Mobile Data";
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                type = "Mobile Data 3G";
                break;
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                type = "Mobile Data 4G";
                break;
            case TelephonyManager.NETWORK_TYPE_GPRS:
                type = "Mobile Data GPRS";
                break;
            case TelephonyManager.NETWORK_TYPE_EDGE:
                type = "Mobile Data EDGE 2G";
                break;
        }
        return type;
    }
}