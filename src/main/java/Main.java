import java.net.Inet4Address;
import java.net.InetAddress;

public class Main {

    public static void main(String[] args) throws Exception{
        var printer = new Printer();

        printer.drawLine();
        printer.println("IPChanger");
        printer.drawLine();

//        var nm = new NetMgr("98E743179805");
        var nm = new NetMgr("00D861340E18");
        for (var addr : nm.getAddresses()) {
            printer.println(addr);
        }

        nm.addAddress(Inet4Address.getByName("192.168.78.3"));
    }
}

