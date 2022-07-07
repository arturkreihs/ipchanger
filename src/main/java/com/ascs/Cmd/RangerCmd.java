package com.ascs.Cmd;

import com.ascs.PingTask;
import com.ascs.Printer;
import com.ascs.RegexConst;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

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

                    if (end - start > 20) {
                        _printer.println("Range too big", Printer.ERRCOLOR);
                        return;
                    }

                    Queue<Integer> ongoing = new ArrayBlockingQueue<>(end - start + 1);

                    for (var idx = start; idx <= end; idx++) {
                        var ipaddr = constIP + idx;
                        int finalIdx = idx;
                        ongoing.add(finalIdx);
                        new Thread(new PingTask(ipaddr, (state) -> {
                            _printer.println(ipaddr, state ? Printer.SUCCESSCOLOR : Printer.ERRCOLOR);
                            ongoing.remove(finalIdx);
                        })).start();
                    }

                    while (!ongoing.isEmpty()) {
                        Thread.sleep(500);
                    }
                }
            }
        }
    }

    @Override
    public String getHelp() {
        return "Pings range of IP addresses (ex: \"r192.168.0.0-255\")";
    }
}
