package com.ascs.Cmd;

import com.ascs.NetMgr;
import com.ascs.Printer;
import com.ascs.RegexConst;

import java.util.Map;
import java.util.Optional;

public class GatewayCmd implements ICmd {

    private final Printer _printer;
    private final NetMgr _nm;

    private String _prevGateway = "";

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
            if (RegexConst.IPADDR.matcher(a).matches()) {
                _printer.println(String.format("Setting Gateway to %s", a), Printer.INFOCOLOR);
                _prevGateway = _nm.getGateway();
                setGateway(a);
                return;
            } else if (a.equals("s")) {
                if (RegexConst.IPADDR.matcher(_prevGateway).matches()) {
                    var newGateway = _prevGateway;
                    _prevGateway = _nm.getGateway();
                    _printer.println(String.format("Switching gateway to %s", newGateway), Printer.INFOCOLOR);
                    setGateway(newGateway);
                } else {
                    _printer.println("Gateway has not yet been set", Printer.ERRCOLOR);
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
        return "Without argument - prints, with argument - sets, 's' - fast switch";
    }

    private void setGateway(String addr) throws Exception {
        if (_nm.setGateway(addr)) {
            _printer.println("Gateway was set", Printer.SUCCESSCOLOR);
        } else {
            _printer.println("Error while setting the gateway", Printer.ERRCOLOR);
        }
    }
}
