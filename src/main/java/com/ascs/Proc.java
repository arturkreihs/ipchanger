package com.ascs;

public class Proc {
    public static String[] exec(String cmd) throws Exception {
        var p = Runtime.getRuntime().exec(cmd);
        var stream = p.getInputStream();
        p.waitFor();

        var all = new String(stream.readAllBytes());

        String splitter;
        if (all.contains("\r\r\n")) splitter = "\r\r\n";
        else splitter = "\r\n";

        var data = all.replace(splitter, "^");
        if (data.length() > 2) {
            data = data.substring(1).substring(0, data.length() - 2);
            return data.split("\\^");
        }

        return new String[0];
    }

    public static int execStatus(String cmd) throws Exception {
        return Runtime.getRuntime().exec(cmd).waitFor();
    }
}
