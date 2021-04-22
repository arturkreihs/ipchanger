package com.ascs;

import org.fusesource.jansi.Ansi;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    private static Ansi.Color INFOCOLOR = Ansi.Color.YELLOW;
    private static Ansi.Color SUCCESSCOLOR = Ansi.Color.GREEN;
    private static Ansi.Color ERRCOLOR = Ansi.Color.RED;

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
                            printer.println("Address added", SUCCESSCOLOR);
                            break;
                        }
                        if (cmd[1].contains("/")) {
                            var cidr = cmd[1].split("/");
                            var mask = cidr[1];
                            if (mask.matches(RegexConst.DIGITS)) {
                                printer.println(String.format("Adding %s/%s", cidr[0], cidr[1]), INFOCOLOR);
                                var ip = cidr[0];
                                nm.addAddress(ip, Integer.parseInt(mask));
                                printer.println("Address added", SUCCESSCOLOR);
                            }
                            break;
                        }
                        break;

                    case "d":
                    case "del":
                        if (cmd[1].matches(RegexConst.IPADDR)) {
                            printer.println(String.format("Removing %s", cmd[1]), INFOCOLOR);
                            nm.delAddress(cmd[1]);
                            printer.println("Address removed", SUCCESSCOLOR);
                            break;
                        }
                        if (cmd[1].matches(RegexConst.DIGITS)) {
                            printer.println(String.format("Removing %s", cmd[1]), INFOCOLOR);
                            if (nm.delAddress(Integer.parseInt(cmd[1]) - 1)) {
                                printer.println("Address not found", ERRCOLOR);
                            } else {
                                printer.println("Address removed", SUCCESSCOLOR);
                            }
                            break;
                        }
                        break;

                    case "l":
                    case "list":
                        nm.refresh();
                        var arr = new ArrayList<String>();
                        for (var addr : nm.getAddresses()) {
                            var mask = nm.getMask(addr);
                            mask.ifPresent(s -> arr.add(String.format("%-15s / %-15s", addr, s)));
                        }
                        printer.printArray(arr.toArray(new String[0]));
                        break;

                    case "g":
                    case "gate":
                    case "gw":
                    case "gateway":
                        if (cmd[1].matches(RegexConst.IPADDR)) {
                            printer.println(String.format("Setting Gateway to %s", cmd[1]), INFOCOLOR);
                            if (nm.setGateway(cmd[1])) {
                                printer.println("Gateway was set", SUCCESSCOLOR);
                            } else {
                                printer.println("Error while setting the gateway", ERRCOLOR);
                            }
                        } else {
                            printer.println(String.format("Gateway is at %s", nm.getGateway()), INFOCOLOR);
                        }
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

