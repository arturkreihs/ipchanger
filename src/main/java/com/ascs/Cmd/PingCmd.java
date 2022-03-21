package com.ascs.Cmd;

import com.ascs.NetMgr;
import com.ascs.Printer;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class PingCmd implements ICmd {

    private final Printer _printer;
    private final NetMgr _nm;

    public PingCmd(Printer p, NetMgr nm) {
        _printer = p;
        _nm = nm;
    }

    @Override
    public void register(Map<Character, ICmd> reg) {
        reg.put('c', this);
    }

    @Override
    public void exec(Optional<String> arg) throws Exception {
        if (arg.isPresent()) {
//            var match = Arrays.stream(_nm.getAddresses()).filter((i) -> i.endsWith(arg.get())).toArray();
//            if (match.length == 1) {
//                var ifAddr = (String)match[0];
//                _printer.println(_nm.getNet());
//            } else if (match.length > 1) {
//                for (var addr : match) {
//                    _printer.println("Which one?", Printer.INFOCOLOR);
//                    _printer.println(_nm.getNet((String)addr), Printer.INFOCOLOR);
//                }
//            } else {
//                _printer.println("No match", Printer.ERRCOLOR);
//            }
        }
    }

    @Override
    public String getHelp() {
        return "null";
    }
}
