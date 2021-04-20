package com.ascs;

public class Proc {
    public static String[] Exec(String cmd) throws Exception {
        var p = Runtime.getRuntime().exec(cmd);
        var stream = p.getInputStream();
        p.waitFor();

        var data = new String(stream.readAllBytes()).replace("\r\r\n", "^");
        data = data.substring(1).substring(0, data.length() - 2);
        return data.split("\\^");
    }
}
