package com.ascs.Cmd;

import com.ascs.NetMgr;
import com.ascs.Printer;

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
    public void exec(Optional<String> arg) {
        _printer.println("del");
    }
}
