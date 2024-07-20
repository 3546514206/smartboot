package org.smartboot.flow.manager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * @author qinluo
 * @date 2021-10-14 01:52:12
 * @since 1.0.0
 */
public final class HostUtils {

    /**
     * Default hostname and ip.
     */
    private static String hostname = "localhost";
    private static String hostIp = "127.0.0.1";

    private HostUtils() {

    }

    static {
        try {
            hostname = InetAddress.getLocalHost().getHostName();
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && !inetAddress.getHostAddress().contains(":")) {
                        hostIp = inetAddress.getHostAddress();
                        break;
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    public static String getHostName() {
        return hostname;
    }

    public static String getHostIp() {
        return hostIp;
    }
}
