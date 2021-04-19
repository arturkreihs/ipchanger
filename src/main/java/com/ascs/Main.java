package com.ascs;

import org.fusesource.jansi.Ansi;

import java.util.Scanner;

public class Main {

    private static Ansi.Color INFOCOLOR = Ansi.Color.YELLOW;

    public static void main(String[] args) throws Exception {
        var printer = new Printer();
        var console = new Scanner(System.in);

        printer.drawLine();
        printer.print("IPChanger ", Ansi.Color.RED);
        printer.println("by Artur Kreihs");
        printer.drawLine();

        NetMgr nm;
        if (args.length > 0) {
            nm = new NetMgr(args[0]);
        } else {
            printer.println("No MAC address", Ansi.Color.RED);
            return;
        }

        while (true) {
            printer.print("IPChanger> ", Ansi.Color.BLUE);
            String cmdline = console.nextLine();
            if (cmdline.length() == 0) {
                continue;
            }
            if (!cmdline.contains(" ")) {
                var sb = new StringBuilder(cmdline);
                sb.insert(1, " ");
                cmdline = sb.toString();
            }
            var cmd = cmdline.split(" ", -1);
            if (cmd.length > 0) {
                switch (cmd[0]) {
                    case "a":
                    case "add":
                        if (cmd.length > 2) {
                            printer.println(String.format("Adding %s/%s", cmd[1], cmd[2]), INFOCOLOR);
                            nm.addAddress(cmd[1], cmd[2]);
                            break;
                        }
                        if (cmd.length > 1) {
                            if (cmd[1].contains("/")) {
                                var ipmask = cmd[1].split("/");
                                var mask = ipmask[1];
                                if (mask.matches(RegexConst.DIGITS)) {
                                    printer.println(String.format("Adding %s/%s", ipmask[0], ipmask[1]), INFOCOLOR);
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
                            if (cmd[1].matches(RegexConst.IPADDR)) {
                                printer.println(String.format("Removing %s", cmd[1]), INFOCOLOR);
                                nm.delAddress(cmd[1]);
                                break;
                            }
                            if (cmd[1].matches(RegexConst.DIGITS)) {
                                if (nm.delAddress(Integer.parseInt(cmd[1]) - 1)) {
                                    printer.println("Address not found", INFOCOLOR);
                                } else {
                                    printer.println(String.format("Removing %s", cmd[1]), INFOCOLOR);
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

                    default:
                        break;
                }
            }
        }
    }
}

