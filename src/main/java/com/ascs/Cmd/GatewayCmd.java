package com.ascs.Cmd;

import com.ascs.NetMgr;
import com.ascs.Printer;
import com.ascs.RegexConst;

import java.util.Map;
import java.util.Optional;

public class GatewayCmd implements ICmd {

    private final Printer _printer;
    private final NetMgr _nm;

    public GatewayCmd(Printer p, NetMgr nm) {
        _printer = p;
        _nm = nm;
    }

    @Override
    public void register(Map<Character, ICmd> reg) {
        reg.put('g', this);
    }

    @Override
    public void exec(Optional<String> arg) throws Exception {
        if (arg.isPresent()) {
            var a = arg.get();
            if (a.matches(RegexConst.IPADDR)) {
                _printer.println(String.format("Setting Gateway to %s", a), Printer.INFOCOLOR);
                if (_nm.setGateway(a)) {
                    _printer.println("Gateway was set", Printer.SUCCESSCOLOR);
                } else {
                    _printer.println("Error while setting the gateway", Printer.ERRCOLOR);
                }
                return;
            }
            _printer.println("Wrong argument format", Printer.ERRCOLOR);
        } else {
            _printer.println(String.format("Gateway is at %s", _nm.getGateway()), Printer.INFOCOLOR);
        }
    }

    @Override
    public String getHelp() {
        return "Without argument - prints gateway, with argument - sets address";
    }
}
