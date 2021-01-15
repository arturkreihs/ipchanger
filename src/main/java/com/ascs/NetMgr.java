package com.ascs;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

public class NetMgr {

    private Optional<NetworkInterface> _ni;
    private final int _idx;

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
    }

    public void refresh() throws Exception {
        var ni = NetworkInterface.getByIndex(_idx);
        if (ni != null) {
            _ni = Optional.of(ni);
        } else {
            _ni = Optional.empty();
        }
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

    public boolean delAddress(int idx) throws Exception {
        var addresses = getAddresses();
        if (idx < addresses.length) {
            delAddress(addresses[idx]);
            return false;
        } else {
            return true;
        }
    }
}
