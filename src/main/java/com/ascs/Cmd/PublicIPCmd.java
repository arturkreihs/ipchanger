package com.ascs.Cmd;

import com.ascs.Printer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import static com.ascs.Printer.ERRCOLOR;
import static com.ascs.Printer.INFOCOLOR;

public class PublicIPCmd implements ICmd {

    static private final int _timeout = 5000;

    private final Printer _printer;

    public PublicIPCmd(Printer printer) {
        _printer = printer;
    }

    @Override
    public void register(Map<Character, ICmd> reg) { reg.put('p', this); }

    @Override
    public void exec(Optional<String> arg) throws Exception {
        URL url = new URL("http://ipinfo.io/ip");

        try {
            URLConnection conn = url.openConnection();
            conn.setReadTimeout(_timeout);
            conn.setConnectTimeout(_timeout);
            conn.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            _printer.println(reader.readLine(), INFOCOLOR);
        } catch (IOException ex) {
            _printer.println("Error while getting the public address", ERRCOLOR);
        }
    }

    @Override
    public String getHelp() { return "Prints public IP address"; }
}
