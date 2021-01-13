import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Collections;

public class NetMgr {

    private final NetworkInterface _ni;

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
        _ni = tni;
    }

    public String getName() {
        return _ni.getName();
    }
}
