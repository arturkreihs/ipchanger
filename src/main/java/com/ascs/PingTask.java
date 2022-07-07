package com.ascs;

import java.util.function.Consumer;

public class PingTask implements Runnable {

    private final String _ipaddr;
    private final Consumer<Boolean> _cb;

    public PingTask(String ipaddr, Consumer<Boolean> cb) {
        _ipaddr = ipaddr;
        _cb = cb;
    }

    @Override
    public void run() {
        try {
            Proc.execStatus(String.format("arp -d %s", _ipaddr));
            _cb.accept(Proc.execStatus(String.format("ping %s -n 2 -w 2", _ipaddr)) == 0);
        } catch (Exception ignored) {}
    }
}
