import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception{
        var printer = new Printer();
        var console = new Scanner(System.in);

        printer.drawLine();
        printer.println("IPChanger");
        printer.drawLine();

//        var nm = new NetMgr("98E743179805");
        var nm = new NetMgr("00D861340E18");

        while (true) {
            printer.print("ipchanger> ");
            var cmd = console.nextLine().split(" ");
            if (cmd.length > 0) {
                switch (cmd[0]) {
                    case "a":
                    case "add":
                        if (cmd.length > 2) {
                            printer.println(String.format("Adding %s/%s", cmd[1], cmd[2]));
                            nm.addAddress(cmd[1], cmd[2]);
                        }
                        break;

                    case "d":
                    case "del":
                        if (cmd.length > 1) {
                            printer.println(String.format("Removing %s", cmd[1]));
                            nm.delAddress(cmd[1]);
                        }
                        break;

                    case "l":
                    case "list":
                        for (var addr : nm.getAddresses()) {
                            printer.println(addr);
                        }
                        break;

                    case "q":
                    case "quit":
                    case "exit":
                        return;
                }
            }
        }

    }
}

