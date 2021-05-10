package com.ascs.Cmd;

import com.ascs.NetMgr;
import com.ascs.Printer;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

public class AddrListCmd implements ICmd {

    private final Printer _printer;
    private final NetMgr _nm;

    public AddrListCmd(Printer p, NetMgr nm) {
        _printer = p;
        _nm = nm;
    }

    @Override
    public void register(Map<Character, ICmd> reg) {
        reg.put('l', this);
    }

    @Override
    public void exec(Optional<String> arg) throws Exception {
        _nm.refresh();
        var arr = new ArrayList<String>();
        for (var addr : _nm.getAddresses()) {
            var mask = _nm.getMask(addr);
            mask.ifPresent(s -> arr.add(String.format("%-15s / %-15s", addr, s)));
        }
        _printer.printArray(arr.toArray(new String[0]));
    }
}
