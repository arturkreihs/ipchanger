package com.ascs;

import org.fusesource.jansi.Ansi;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        var printer = new Printer();
        var console = new Scanner(System.in);

        printer.drawLine();
        printer.println("IPChanger", Ansi.Color.RED);
        printer.drawLine();

        var nm = new NetMgr("98E743179805");
//        var nm = new com.ascs.NetMgr("00D861340E18");

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
                            if (cmd[1].matches("^((25[0-5]|(2[0-4]|1[0-9]|[1-9]|)[0-9])(\\.(?!$)|$)){4}$")) {
                                nm.delAddress(cmd[1]);
                                break;
                            }
                            if (cmd[1].matches("^[0-9]+$")) {
                                if (nm.delAddress(Integer.parseInt(cmd[1]) - 1)) {
                                    printer.println("Address not found");
                                }
                                break;
                            }
                        }
                        break;

                    case "l":
                    case "list":
                        nm.refresh();
                        int idx = 0;
                        for (var addr : nm.getAddresses()) {
                            printer.println(String.format("%d - %s", ++idx, addr), Ansi.Color.YELLOW);
                        }
                        break;

                    case "q":
                    case "quit":
                    case "exit":
                        printer.close();
                        return;
                }
            }
        }

    }
}

