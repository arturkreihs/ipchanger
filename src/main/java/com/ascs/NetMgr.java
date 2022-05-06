package com.ascs;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.IntFunction;
import java.util.regex.Pattern;

public class NetMgr {

    static private final int _timeout = 5000;

    private Optional<NetworkInterface> _ni;
    private final int _idx;
    private final Map<Integer, String> _ipmasks = new HashMap<>();

    public final static Pattern IPSep = Pattern.compile("\\.");

    public NetMgr(String mac) throws Exception {
//        parsing mac address
        int len = mac.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(mac.charAt(i), 16) << 4)
                    + Character.digit(mac.charAt(i + 1), 16));
        }

//        getting network interface
        NetworkInterface tni = null;
        for (var ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
            if (Arrays.equals(ni.getHardwareAddress(), data)) {
                tni = ni;
                break;
            }
        }

        if (tni != null) {
            _ni = Optional.of(tni);
            _idx = tni.getIndex();
        } else {
            _ni = Optional.empty();
            _idx = 0;
        }

        refreshIPMasks();
    }

    public static String getLine(URL url) throws Exception {
        URLConnection conn = url.openConnection();
        conn.setReadTimeout(_timeout);
        conn.setConnectTimeout(_timeout);
        conn.connect();
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        return reader.readLine();
    }

    public void refresh() throws Exception {
        _ni = Optional.ofNullable(NetworkInterface.getByIndex(_idx));

        refreshIPMasks();
    }

    public String[] getAddresses() {
        return _ni.map(networkInterface -> networkInterface.inetAddresses().map(InetAddress::getHostAddress).filter(addr -> RegexConst.IPADDR.matcher(addr).matches()).toArray(String[]::new)).orElseGet(() -> new String[0]);
    }

    public boolean addAddress(String addr, String mask) throws Exception {
        if (_ni.isPresent()) {
            var idx = _ni.get().getIndex();
            var valid = Runtime.getRuntime().exec(String.format("netsh interface ipv4 add address name=%d address=%s mask=%s", idx, addr, mask)).waitFor(_timeout, TimeUnit.MILLISECONDS);
            while(true) {
                refresh();
                if (Arrays.asList(getAddresses()).contains(addr)) {
                    break;
                }
                Thread.sleep(200);
            }
            return !valid;
        }
        return true;
    }

    public boolean addAddress(String addr, int mask) throws Exception {
        if (mask <= 32) {
            var full = mask / 8;
            var rest = mask % 8;
            var maskarr = new int[] { 0, 0, 0, 0};
            var idx = 0;
            while(idx < full) {
                maskarr[idx++] = 255;
            }
            if (rest > 0) {
                maskarr[idx] = (-(255 >> rest)) & 0xff - 1;
            }
            return addAddress(addr, String.format("%d.%d.%d.%d", maskarr[0], maskarr[1], maskarr[2], maskarr[3]));
        }
        return true;
    }

    public boolean delAddress(String addr) throws Exception {
        if (_ni.isPresent()) {
            var idx = _ni.get().getIndex();
            var valid = Runtime.getRuntime().exec(String.format("netsh interface ipv4 delete address name=%d address=%s", idx, addr)).waitFor(_timeout, TimeUnit.MILLISECONDS);
            var timeout = 20;
            while (Arrays.asList(getAddresses()).contains(addr)) {
                refresh();
                Thread.sleep(200);
                if (--timeout == 0) {
                    return false;
                }
            }
            return !valid;
        }
        return true;
    }

    public boolean delAddress(int idx) throws Exception {
        var addresses = getAddresses();
        if (idx < addresses.length) {
            return delAddress(addresses[idx]);
        }
        return true;
    }

    public Optional<String> getMask(String addr) {
        var mask = _ipmasks.getOrDefault(addr.hashCode(), null);
        if (mask != null) {
            return Optional.of(mask);
        }
        return Optional.empty();
    }

    public String getGateway() throws Exception {
        var lines = Proc.exec(String.format("wmic nicconfig where \"InterfaceIndex = %d\" get defaultipgateway /format:csv", _idx));
        if (lines.length > 1) {
            var lineArray = lines[1].split(",");
            if (lineArray.length > 1) {
                var gateways = extractCSVList(lineArray[1]);
                if (gateways.length > 0) {
                    return gateways[0];
                }
            }
        }
        return null;
    }

    public boolean setGateway(String addr) throws Exception {
        Runtime.getRuntime().exec(String.format("netsh interface ip del route 0.0.0.0/0 %d", _idx)).waitFor(_timeout, TimeUnit.MILLISECONDS);
        Runtime.getRuntime().exec(String.format("netsh interface ip add route 0.0.0.0/0 %d %s", _idx, addr)).waitFor(_timeout, TimeUnit.MILLISECONDS);
        return addr.equals(getGateway());
    }

//    public String getNet(String addr) {
//        var mask = getMask(addr);
//        if (mask.isPresent()) {
//            var r = new long[4];
//            var bAddr = ipToBytes(addr);
//            var bMask = ipToBytes(mask.get());
//            if (bAddr != null && bMask != null) {
//                if (bAddr.length == 4 && bMask.length == 4) {
//                    for (var i = 0; i < 4; ++i) {
//                        r[i] = (bAddr[i] & bMask[i]) & 0xFFL;
//                    }
//                    return String.format("%d.%d.%d.%d", r[0], r[1], r[2], r[3]);
//                }
//            }
//        }
//        return null;
//    }

    public static byte[] ipToBytes(String addr) {
        var a = new byte[4];
        var octets = IPSep.split(addr);
        if (octets.length == 4) {
            for (var i = 0; i < 4; ++i) {
                a[i] = (byte)Integer.parseInt(octets[i]);
            }
            return a;
        }
        return null;
    }

    public static String bytesToIP(byte[] addr) {
        return String.format("%d.%d.%d.%d", (addr[0] & 0xff), (addr[1] & 0xff), (addr[2] & 0xff), (addr[3] & 0xff));
    }

    private String[] extractCSVList(String data) {
        if (data.startsWith("{") && data.endsWith("}")) {
            data = data.substring(1).substring(0, data.length() - 2);
            return data.split(";");
        }
        return new String[0];
    }

    private void refreshIPMasks() throws Exception {
        var lines = Proc.exec(String.format("wmic nicconfig where \"InterfaceIndex = %d\" get IPAddress, IPSubnet /format:csv", _idx));
        if (lines.length > 1) {
            var line = lines[1].split(",");
            if (line.length > 2) {
                var idx = 0;
                var masks = extractCSVList(line[2]);
                for (var ipaddr : extractCSVList(line[1])) {
                    int finalIdx = idx++;
                    _ipmasks.compute(ipaddr.hashCode(), (k, v) -> masks[finalIdx]);
                }
            }
        }
    }
}
