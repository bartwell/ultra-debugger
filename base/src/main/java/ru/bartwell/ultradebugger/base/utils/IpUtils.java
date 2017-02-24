package ru.bartwell.ultradebugger.base.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by BArtWell on 17.02.2017.
 */

public class IpUtils {
    private static final String IPV4_BASIC_PATTERN_STRING = "(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])";
    private static final Pattern IPV4_PATTERN = Pattern.compile("^" + IPV4_BASIC_PATTERN_STRING + "$");

    @Nullable
    public static String getIpV4() {
        return getIpAddress(true);
    }

    @Nullable
    public static String getIpV6() {
        return getIpAddress(false);
    }

    @Nullable
    private static String getIpAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : interfaces) {
                List<InetAddress> addresses = Collections.list(networkInterface.getInetAddresses());
                for (InetAddress address : addresses) {
                    if (!address.isLoopbackAddress()) {
                        String resultAddress = address.getHostAddress().toUpperCase();
                        boolean isIPv4 = isIPv4Address(resultAddress);
                        if (useIPv4) {
                            if (isIPv4) {
                                return resultAddress;
                            }
                        } else {
                            if (!isIPv4) {
                                int delimiter = resultAddress.indexOf('%');
                                return delimiter < 0 ? resultAddress : resultAddress.substring(0, delimiter);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean isIPv4Address(@NonNull String input) {
        return IPV4_PATTERN.matcher(input).matches();
    }
}
