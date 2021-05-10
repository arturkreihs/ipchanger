package com.ascs.Cmd;

import java.util.Map;
import java.util.Optional;

public interface ICmd {
    void register(Map<Character, ICmd> reg);
    void exec(Optional<String> arg) throws Exception;
    String getHelp();
}
