package com.ascs.Cmd;

import com.ascs.Printer;
import org.fusesource.jansi.Ansi;

import java.util.Map;
import java.util.Optional;

public class HelpCmd implements ICmd {

    private final Map<Character, ICmd> _cmds;
    private final Printer _printer;

    public HelpCmd(Printer p, Map<Character, ICmd> cmds) {
        _printer = p;
        _cmds = cmds;
    }

    @Override
    public void register(Map<Character, ICmd> reg) {
        reg.put('h', this);
    }

    @Override
    public void exec(Optional<String> arg) throws Exception {
        if (_cmds != null) {
            _printer.println("Application usage:", Printer.INFOCOLOR);
            for (var cmdEntry : _cmds.entrySet()) {
                _printer.print(cmdEntry.getKey().toString(), Ansi.Color.GREEN);
                _printer.print(": ", Printer.INFOCOLOR);
                _printer.println(cmdEntry.getValue().getHelp(), Printer.INFOCOLOR);
            }
        }
    }

    @Override
    public String getHelp() {
        return "Shows this text";
    }
}
