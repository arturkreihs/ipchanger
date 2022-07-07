package com.ascs.Cmd;

import com.ascs.PingTask;
import com.ascs.Printer;
import com.ascs.RegexConst;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class RangerCmd implements ICmd {

    private final Printer _printer;

    public RangerCmd(Printer printer) {
        _printer = printer;
    }

    @Override
    public void register(Map<Character, ICmd> reg) {
        reg.put('r', this);
    }

    @Override
    public void exec(Optional<String> arg) throws Exception {
        if (arg.isPresent()) {
            var parts = RegexConst.DASH.split(arg.get());
            if (parts.length == 2) {
                if (RegexConst.IPADDR.matcher(parts[0]).matches() && RegexConst.DIGITS.matcher(parts[1]).matches()) {
                    var constIPend = StringUtils.ordinalIndexOf(parts[0], ".", 3);
                    var constIP = parts[0].substring(0, constIPend + 1);
//                    _printer.println(constIP, Printer.INFOCOLOR);

                    var start = Integer.parseInt(parts[0].substring(constIPend + 1));
                    var end = Integer.parseInt(parts[1]);

                    if (end - start > 60) {
                        _printer.println("Range too big", Printer.ERRCOLOR);
                        return;
                    }

                    // collect ping responses
                    Map<Integer, Boolean> results = new ConcurrentHashMap<>();
                    for (var idx = start; idx <= end; idx++) {
                        int idxCopy = idx;
                        new Thread(new PingTask(constIP + idx, (state) -> results.put(idxCopy, state))).start();
                    }

                    // wait for full collection
                    while (results.size() < end - start + 1) Thread.sleep(200);

                    // present results
                    var idx = 0;
                    for (var key : results.keySet().stream().sorted().collect(Collectors.toList())) {
                        var ipaddr = constIP + key;
                        var state = results.get(key);
                        _printer.print(String.format("%-16s", ipaddr), state ? Printer.SUCCESSCOLOR : Printer.ERRCOLOR);
                        if (++idx % 5 == 0) _printer.println();
                    }

                    _printer.println();
                }
            }
        }
    }

    @Override
    public String getHelp() {
        return "Pings range of IP addresses (ex: \"r192.168.0.0-255\")";
    }
}
