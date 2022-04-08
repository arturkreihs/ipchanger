package com.ascs.Cmd;

import com.ascs.NetMgr;
import com.ascs.Printer;

import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class MacGetCmd implements ICmd {

    private final Pattern _macPattern = Pattern.compile("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$");
    private final Printer _printer;

    public MacGetCmd(Printer printer) {
        _printer = printer;
    }

    @Override
    public void register(Map<Character, ICmd> reg) {
        reg.put('m', this);
    }

    @Override
    public void exec(Optional<String> arg) throws Exception {
        if (arg.isPresent()) {
            if (_macPattern.matcher(arg.get()).matches()) {
                var macEncoded = URLEncoder.encode(arg.get(), StandardCharsets.US_ASCII);
                var vendor = NetMgr.getLine(new URL("http://api.macvendors.com/" + macEncoded));
                _printer.println(vendor, Printer.INFOCOLOR);
            }
        }
    }

    @Override
    public String getHelp() {
        return "Prints vendor for given MAC";
    }
}
