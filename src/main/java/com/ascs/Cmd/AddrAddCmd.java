package com.ascs.Cmd;

import com.ascs.NetMgr;
import com.ascs.Printer;

import java.util.Map;
import java.util.Optional;

public class AddrAddCmd implements ICmd {

    private final Printer _printer;
    private final NetMgr _nm;

    public AddrAddCmd(Printer p, NetMgr nm) {
        _printer = p;
        _nm = nm;
    }

    @Override
    public void register(Map<Character, ICmd> reg) {
        reg.put('a', this);
    }

    @Override
    public void exec(Optional<String> arg) throws Exception {
        if (arg.isPresent()) {
            var a = arg.get();
            if (a.contains(" ")) {
                var args = a.split(" ");
                printInfo(args[0], args[1]);
                printResult(_nm.addAddress(args[0], args[1]));
                return;
            }
            if (a.contains("/")) {
                var cidr = a.split("/");
                var addr = cidr[0];
                var mask = cidr[1];
                printInfo(addr, mask);
                printResult(_nm.addAddress(addr, Integer.parseInt(mask)));
            }
        }
    }

    @Override
    public String getHelp() {
        return "Add IP address (ex: \"a10.0.0.1/8\", \"a 10.0.0.1 255.0.0.0\")";
    }

    private void printInfo(String addr, String mask) {
        _printer.println(String.format("Adding %s/%s", addr, mask), Printer.INFOCOLOR);
    }

    private void printResult(boolean result) {
        if (result) {
            _printer.println("Error while adding the address", Printer.ERRCOLOR);
        } else {
            _printer.println("Address added", Printer.SUCCESSCOLOR);
        }
    }
}
