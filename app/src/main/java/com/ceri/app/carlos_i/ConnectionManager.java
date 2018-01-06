package com.ceri.app.carlos_i;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by Baptiste on 06/01/2018.
 */

public class ConnectionManager {

    public static String getGatewayIPAddress(Context context) {

        WifiManager manager = (WifiManager) context.getSystemService(WIFI_SERVICE);

        WifiInfo info = manager.getConnectionInfo();
        int address = info.getIpAddress();
        return android.text.format.Formatter.formatIpAddress(address);

        //int address = manager.getDhcpInfo().ipAddress;
        //return intToIp(address);

        //int address = manager.getDhcpInfo().gateway;
        //return android.text.format.Formatter.formatIpAddress(address);

        //return getWifiApIpAddress();

    }

    private static String intToIp(int i) {

        return ((i >> 24 ) & 0xFF ) + "."
                + ((i >> 16 ) & 0xFF) + "."
                + ((i >> 8 ) & 0xFF) + "."
                + ( i & 0xFF) ;

    }

    private static String getWifiApIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
                    .hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                if (intf.getName().contains("wlan")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
                            .hasMoreElements();) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()
                                && (inetAddress.getAddress().length == 4)) {
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException ex) {

        }
        return null;
    }

}
