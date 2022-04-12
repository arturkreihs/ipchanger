package com.ascs.Cmd;

import com.ascs.Printer;

import java.util.Map;
import java.util.Optional;

public class ExitCmd implements ICmd {

    private final Printer _printer;

    public ExitCmd(Printer printer) {
        _printer = printer;
    }

    @Override
    public void register(Map<Character, ICmd> reg) {
        reg.put('q', this);
    }

    @Override
    public void exec(Optional<String> arg) {
        _printer.close();
        System.exit(0);
    }

    @Override
    public String getHelp() {
        return "Closes application";
    }
}
