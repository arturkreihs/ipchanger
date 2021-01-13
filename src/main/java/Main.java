public class Main {

    public static void main(String[] args) throws Exception{
        var printer = new Printer();

        printer.drawLine();
        printer.println("IPChanger");
        printer.drawLine();

        var nm = new NetMgr("98E743179805");
        for (var addr : nm.getAddresses()) {
            printer.println(addr);
        }
    }
}

