package com.ascs.Cmd;

import java.util.Map;
import java.util.Optional;

public class ExitCmd implements ICmd {

    @Override
    public void register(Map<Character, ICmd> reg) {
        reg.put('q', this);
    }

    @Override
    public void exec(Optional<String> arg) {
        System.exit(0);
    }

    @Override
    public String getHelp() {
        return "Closes application";
    }
}
