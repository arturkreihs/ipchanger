package com.ascs;

import java.util.regex.Pattern;

public class RegexConst {
    static public final Pattern DIGITS = Pattern.compile("^[0-9]+$");
    static public final Pattern IPADDR = Pattern.compile("^((25[0-5]|(2[0-4]|1[0-9]|[1-9]|)[0-9])(\\.(?!$)|$)){4}$");
    static public final Pattern MACADDR = Pattern.compile("(([0-9a-f]{2}-){5}[0-9a-f]{2})");
}
