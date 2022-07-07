package com.ascs.Cmd;

import com.ascs.Printer;
import com.ascs.Proc;
import com.ascs.RegexConst;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Optional;

public class RangerCmd implements ICmd {

    private final Printer _printer;

    public RangerCmd(Printer printer) {
        _printer = printer;
    }

    @Override
    public void register(Map<Character, ICmd> reg) {
        reg.put('r', this);
    }

    @Override
    public void exec(Optional<String> arg) throws Exception {
        if (arg.isPresent()) {
            var parts = RegexConst.DASH.split(arg.get());
            if (parts.length == 2) {
                if (RegexConst.IPADDR.matcher(parts[0]).matches() && RegexConst.DIGITS.matcher(parts[1]).matches()) {
                    var constIPend = StringUtils.ordinalIndexOf(parts[0], ".", 3);
                    var constIP = parts[0].substring(0, constIPend + 1);
//                    _printer.println(constIP, Printer.INFOCOLOR);

                    var start = Integer.parseInt(parts[0].substring(constIPend + 1));
                    var end = Integer.parseInt(parts[1]);

                    for (var idx = start; idx <= end; idx++) {
                        var ipaddr = constIP + idx;
                        Proc.execStatus(String.format("arp -d %s", ipaddr));
                        var status = Proc.execStatus(String.format("ping %s -n 1 -w 1", ipaddr)) == 0;
                        _printer.println(constIP + idx, status ? Printer.SUCCESSCOLOR : Printer.ERRCOLOR);
                    }
                }
            }
        }
    }

    @Override
    public String getHelp() {
        return "Pings range of IP addresses (ex: \"r192.168.0.0-255\")";
    }
}
