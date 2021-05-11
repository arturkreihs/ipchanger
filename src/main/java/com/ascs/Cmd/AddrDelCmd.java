package com.ascs.Cmd;

import com.ascs.NetMgr;
import com.ascs.Printer;
import com.ascs.RegexConst;

import java.util.Map;
import java.util.Optional;

public class AddrDelCmd implements ICmd {

    private final Printer _printer;
    private final NetMgr _nm;

    public AddrDelCmd(Printer p, NetMgr nm) {
        _printer = p;
        _nm = nm;
    }

    @Override
    public void register(Map<Character, ICmd> reg) {
        reg.put('d', this);
    }

    @Override
    public void exec(Optional<String> arg) throws Exception {
        if (arg.isPresent()) {
            var a = arg.get();
            if (a.matches(RegexConst.IPADDR)) {
                printInfo(a);
                printResult(_nm.delAddress(a));
                return;
            }
            if (a.matches(RegexConst.DIGITS)) {
                printInfo(a);
                printResult(_nm.delAddress(Integer.parseInt(a) - 1));
                return;
            }
            _printer.println("Wrong argument format", Printer.ERRCOLOR);
        } else {
            _printer.println("Argument required", Printer.ERRCOLOR);
        }
    }

    @Override
    public String getHelp() {
        return "Removes IP address (ex: \"d10.0.0.1\", \"d3\")";
    }

    private void printInfo(String addr) {
        _printer.println(String.format("Removing %s", addr), Printer.INFOCOLOR);
    }

    private void printResult(boolean result) {
        if (result) {
            _printer.println("Address not found", Printer.ERRCOLOR);
        } else {
            _printer.println("Address removed", Printer.SUCCESSCOLOR);
        }
    }
}
