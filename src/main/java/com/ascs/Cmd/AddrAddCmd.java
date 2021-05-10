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
    public void exec(Optional<String> arg) {
        arg.ifPresent((a) -> {
            var args = a.split(" ");
        });
    }
}
