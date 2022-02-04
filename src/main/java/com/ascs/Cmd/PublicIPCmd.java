package com.ascs.Cmd;

import com.ascs.Printer;
import org.json.JSONObject;

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
        try {
            var publicIP = getLine(new URL("http://ipinfo.io/ip"));
            var json = new JSONObject(getLine(new URL("http://ip-api.com/json/" + publicIP)));
            var country = json.getString("country");
            _printer.print(publicIP, INFOCOLOR);
            _printer.println(" (" + country + ")");
        } catch (IOException ex) {
            _printer.println("Error while getting the public address", ERRCOLOR);
        }
    }

    @Override
    public String getHelp() { return "Prints public IP address"; }

    private String getLine(URL url) throws Exception {
        URLConnection conn = url.openConnection();
        conn.setReadTimeout(_timeout);
        conn.setConnectTimeout(_timeout);
        conn.connect();
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        return reader.readLine();
    }
}
