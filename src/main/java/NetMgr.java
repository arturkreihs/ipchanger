import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

public class NetMgr {

    private final Optional<NetworkInterface> _ni;

    public NetMgr(String mac) throws Exception {
//        parsing mac address
        int len = mac.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(mac.charAt(i), 16) << 4)
                    + Character.digit(mac.charAt(i+1), 16));
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
        } else {
            _ni = Optional.empty();
        }
    }

    String[] getAddresses() {
        return _ni.map(networkInterface -> networkInterface.inetAddresses().map(InetAddress::getHostAddress).filter(addr -> addr.matches("^((25[0-5]|(2[0-4]|1[0-9]|[1-9]|)[0-9])(\\.(?!$)|$)){4}$")).toArray(String[]::new)).orElseGet(() -> new String[0]);
    }

    void addAddress(InetAddress address) {
        _ni.ifPresent(ni -> {
            var idx = _ni.get().getIndex();
            
        });
    }
}
