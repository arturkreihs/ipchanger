package com.ascs;

import org.fusesource.jansi.Ansi;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        var printer = new Printer();
        var console = new Scanner(System.in);

        printer.drawLine();
        printer.print("IPChanger ", Ansi.Color.RED);
        printer.println("by Artur Kreihs");
        printer.drawLine();

        var nm = new NetMgr("98E743179805");
//        var nm = new com.ascs.NetMgr("00D861340E18");

        while (true) {
            printer.print("IPChanger> ", Ansi.Color.BLUE);
            var cmd = console.nextLine().split(" ");
            if (cmd.length > 0) {
                switch (cmd[0]) {
                    case "a":
                    case "add":
                        if (cmd.length > 2) {
                            printer.println(String.format("Adding %s/%s", cmd[1], cmd[2]));
                            nm.addAddress(cmd[1], cmd[2]);
                            break;
                        }
                        if (cmd.length > 1) {
                            if (cmd[1].contains("/")) {
                                var ipmask = cmd[1].split("/");
                                var mask = ipmask[1];
                                if (mask.matches(RegexConst.DIGITS)) {
                                    var ip = ipmask[0];
                                    nm.addAddress(ip, Integer.parseInt(mask));
                                }
                                break;
                            }
                        }
                        break;

                    case "d":
                    case "del":
                        if (cmd.length > 1) {
                            printer.println(String.format("Removing %s", cmd[1]));
                            if (cmd[1].matches(RegexConst.IPADDR)) {
                                nm.delAddress(cmd[1]);
                                break;
                            }
                            if (cmd[1].matches(RegexConst.DIGITS)) {
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
                        printer.printArray(nm.getAddresses());
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

