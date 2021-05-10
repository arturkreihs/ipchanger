package com.ascs;

import com.ascs.Cmd.*;
import org.fusesource.jansi.Ansi;

import java.util.*;

public class Main {


    public static void main(String[] args) throws Exception {
        var printer = new Printer();
        var console = new Scanner(System.in);

        printer.drawLine();
        printer.print("IPChanger ", Ansi.Color.RED);
        printer.print("by Artur Kreihs");
        printer.println(String.format("%55s", "Press h for help"), Printer.INFOCOLOR);
        printer.drawLine();

        NetMgr nm;
        if (args.length > 0) {
            nm = new NetMgr(args[0]);
        } else {
            printer.println("No MAC address", Ansi.Color.RED);
            return;
        }

//        cmds registration
        Map<Character, ICmd> actions = new HashMap<>();
        new ExitCmd().register(actions);
        new AddrAddCmd(printer, nm).register(actions);
        new AddrDelCmd(printer, nm).register(actions);
        new AddrListCmd(printer, nm).register(actions);

        while (true) {
            printer.print("IPChanger> ", Ansi.Color.BLUE);
            String cmdline = console.nextLine();

            Optional<String> argument = Optional.empty();
            if (cmdline.matches(". .+")) {
                argument = Optional.of(cmdline.substring(2));
            } else if(cmdline.length() > 1) {
                argument = Optional.of(cmdline.substring(1));
            }

            var cmd = actions.getOrDefault(cmdline.charAt(0), null);
            if (cmd != null) {
                cmd.exec(argument);
            } else {
                printer.println("Unknown command");
            }

//
//                    case "d":
//                    case "del":
//                        if (cmd[1].matches(RegexConst.IPADDR)) {
//                            printer.println(String.format("Removing %s", cmd[1]), INFOCOLOR);
//                            if (nm.delAddress(cmd[1])) {
//                                printer.println("Address not found", ERRCOLOR);
//                            } else {
//                                printer.println("Address removed", SUCCESSCOLOR);
//                            }
//                            break;
//                        }
//                        if (cmd[1].matches(RegexConst.DIGITS)) {
//                            printer.println(String.format("Removing %s", cmd[1]), INFOCOLOR);
//                            if (nm.delAddress(Integer.parseInt(cmd[1]) - 1)) {
//                                printer.println("Address not found", ERRCOLOR);
//                            } else {
//                                printer.println("Address removed", SUCCESSCOLOR);
//                            }
//                            break;
//                        }
//                        break;
//
//                    case "l":
//                    case "list":
//                        nm.refresh();
//                        var arr = new ArrayList<String>();
//                        for (var addr : nm.getAddresses()) {
//                            var mask = nm.getMask(addr);
//                            mask.ifPresent(s -> arr.add(String.format("%-15s / %-15s", addr, s)));
//                        }
//                        printer.printArray(arr.toArray(new String[0]));
//                        break;
//
//                    case "g":
//                    case "gate":
//                    case "gw":
//                    case "gateway":
//                        if (cmd[1].matches(RegexConst.IPADDR)) {
//                            printer.println(String.format("Setting Gateway to %s", cmd[1]), INFOCOLOR);
//                            if (nm.setGateway(cmd[1])) {
//                                printer.println("Gateway was set", SUCCESSCOLOR);
//                            } else {
//                                printer.println("Error while setting the gateway", ERRCOLOR);
//                            }
//                        } else {
//                            printer.println(String.format("Gateway is at %s", nm.getGateway()), INFOCOLOR);
//                        }
//                        break;
//                    case "h":
//                    case "help":
//                    case "?":
//                        printer.println("Application usage:");
//                        printer.println(String.format("%12s: lists active ip addresses", "list,l"), INFOCOLOR);
//                        printer.println(String.format("%12s: add ip address (ex: \"a10.0.0.1/24\", \"add 10.0.0.1 255.0.0.0\")", "add,a"), INFOCOLOR);
//                        printer.println(String.format("%12s: removes ip address (ex: \"d10.0.0.1\", \"del 10.0.0.1\", \"d3\")", "del,d"), INFOCOLOR);
//                        printer.println(String.format("%12s: without argument - prints gateway, with argument - sets address", "gate,gw,g"), INFOCOLOR);
//                        printer.println(String.format("%12s: closes application", "quit,q"), INFOCOLOR);
//                        break;
//
//                    case "q":
//                    case "quit":
//                        printer.close();
//                        return;
//
//                    default:
//                        break;
//                }
//            }
        }
    }
}

