package com.ascs.Cmd;

import com.ascs.Printer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import java.util.Optional;

import static com.ascs.Printer.INFOCOLOR;

public class PublicIPCmd implements ICmd {

    private final Printer _printer;

    public PublicIPCmd(Printer printer) {
        _printer = printer;
    }

    @Override
    public void register(Map<Character, ICmd> reg) { reg.put('p', this); }

    @Override
    public void exec(Optional<String> arg) throws Exception {
        URL url = new URL("http://ipinfo.io/ip");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
            _printer.println(reader.readLine(), INFOCOLOR);
        }
    }

    @Override
    public String getHelp() { return "Prints public IP address"; }
}
