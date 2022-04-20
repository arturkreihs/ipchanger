package com.ascs;

public class Proc {
    public static String[] exec(String cmd) throws Exception {
        var p = Runtime.getRuntime().exec(cmd);
        var stream = p.getInputStream();
        p.waitFor();

        var data = new String(stream.readAllBytes()).replace("\r\r\n", "^");
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
