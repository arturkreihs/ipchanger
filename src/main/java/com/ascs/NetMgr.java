package com.ascs;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;

public class NetMgr {

    private Optional<NetworkInterface> _ni;
    private final int _idx;
    private final Map<Integer, String> _ipmasks = new HashMap<Integer, String>();

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

    public void refresh() throws Exception {
        var ni = NetworkInterface.getByIndex(_idx);
        if (ni != null) {
            _ni = Optional.of(ni);
        } else {
            _ni = Optional.empty();
        }

        refreshIPMasks();
    }

    public String[] getAddresses() {
        return _ni.map(networkInterface -> networkInterface.inetAddresses().map(InetAddress::getHostAddress).filter(addr -> addr.matches(RegexConst.IPADDR)).toArray(String[]::new)).orElseGet(() -> new String[0]);
    }

    public void addAddress(String addr, String mask) {
        _ni.ifPresent(ni -> {
            try {
                var idx = _ni.get().getIndex();
                Runtime.getRuntime().exec(String.format("netsh interface ipv4 add address name=%d address=%s mask=%s", idx, addr, mask)).waitFor();
                while(true) {
                    refresh();
                    if (Arrays.asList(getAddresses()).contains(addr)) {
                        break;
                    }
                    Thread.sleep(200);
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public void addAddress(String addr, int mask) {
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
            addAddress(addr, String.format("%d.%d.%d.%d", maskarr[0], maskarr[1], maskarr[2], maskarr[3]));
        }
    }

    public void delAddress(String addr) {
        _ni.ifPresent(ni -> {
            try {
                var idx = _ni.get().getIndex();
                Runtime.getRuntime().exec(String.format("netsh interface ipv4 delete address name=%d address=%s", idx, addr)).waitFor();
                while (Arrays.asList(getAddresses()).contains(addr)) {
                    refresh();
                    Thread.sleep(200);
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public boolean delAddress(int idx) {
        var addresses = getAddresses();
        if (idx < addresses.length) {
            delAddress(addresses[idx]);
            return false;
        } else {
            return true;
        }
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
        Runtime.getRuntime().exec(String.format("netsh interface ip del route 0.0.0.0/0 %d", _idx)).waitFor();
        Runtime.getRuntime().exec(String.format("netsh interface ip add route 0.0.0.0/0 %d %s", _idx, addr)).waitFor();
        return addr.equals(getGateway());
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
            var idx = 0;
            var masks = extractCSVList(line[2]);
            for (var ipaddr : extractCSVList(line[1])) {
                int finalIdx = idx++;
                _ipmasks.compute(ipaddr.hashCode(), (k, v) -> masks[finalIdx]);
            }
        }
    }
}
