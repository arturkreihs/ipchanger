package com.ascs.Cmd;

import com.ascs.NetMgr;
import com.ascs.Printer;
import com.ascs.Proc;

import java.util.*;
import java.util.function.BiFunction;
import java.util.logging.ConsoleHandler;

public class PingCmd implements ICmd {

    private final Printer _printer;
    private final NetMgr _nm;

    public PingCmd(Printer p, NetMgr nm) {
        _printer = p;
        _nm = nm;
    }

    @Override
    public void register(Map<Character, ICmd> reg) {
        reg.put('c', this);
    }

    @Override
    public void exec(Optional<String> arg) throws Exception {
        if (arg.isPresent()) {
            _nm.refresh();
            var partial = arg.get();

            //arg is full or partial ip address
            //arg split by '.'
            //fill byte[4] from end
            var input = new byte[] {0,0,0,0};
            var reversed = Arrays.asList(NetMgr.IPSep.split(partial));
            Collections.reverse(reversed);
            var idx = 3;
            for (var part : reversed) {
                byte p;
                try {
                    p = (byte) Integer.parseInt(part);
                } catch (NumberFormatException ignored) {
                    p = 0;
                }
                input[idx--] = p;
            }

            // TEST
//            System.out.println((input[0] & 0xFF) + "." + (input[1] & 0xFF) + "." + (input[2] & 0xFF) + "." + (input[3] & 0xFF));

            //for every ip in list do
            //  ip mul mask
            //  arg byte[4] mul mask
            //  xor both and save result
            var competitors = new HashMap<byte[], Long>();
            for (var ifaceAddr : _nm.getAddresses()) {
                _nm.getMask(ifaceAddr).ifPresent((ifaceMask) -> {
                    var bIfaceMask = NetMgr.ipToBytes(ifaceMask);
                    var bIfaceAddr = NetMgr.ipToBytes(ifaceAddr);
                    var ifaceNet = mixAddrs(bIfaceAddr, bIfaceMask, this::mulFunction);
                    var inputNet = mixAddrs(input, bIfaceMask, this::mulFunction);
                    long score = 0;
                    var mixed = mixAddrs(inputNet, ifaceNet, this::xorFunction);
                    for (var i = 0; i < 4; i++) {
                        score += mixed[i] & 0xFF;
                    }
                    competitors.put(ifaceNet, score);
                });
            }

            // TEST
//            for (var c : competitors.entrySet()) {
//                var ip = c.getKey();
//                System.out.println((ip[0] & 0xFF) + "." + (ip[1] & 0xFF) + "." +(ip[2] & 0xFF) + "." + (ip[3] & 0xFF) + " - " + c.getValue());
//            }

            // compare results, smallest wins
            var minVal = Long.MAX_VALUE;
            byte[] ifaceNet = {0,0,0,0};
            for (var competitor : competitors.entrySet()) {
                var val = competitor.getValue();
                if (val < minVal) {
                    minVal = val;
                    ifaceNet = competitor.getKey();
                }
            }

            // ifaceNet OR input = IP to ping
            var addr = mixAddrs(ifaceNet, input, this::orFunction);

            // TEST
//            _printer.println((addr[0] & 0xFF) + "." + (addr[1] & 0xFF) + "." +(addr[2] & 0xFF) + "." + (addr[3] & 0xFF));

//            Proc.exec("ping -c1 " + NetMgr.bytesToIP(addr));
            _printer.println("ping -c1 " + NetMgr.bytesToIP(addr));
        } else {
            _printer.println("Argument is required", Printer.ERRCOLOR);
        }
    }

    @Override
    public String getHelp() {
        return "null";
    }

    private byte[] mixAddrs(byte[] a, byte[] b, BiFunction<Byte, Byte, Byte> function) {
        var result = new byte[] {0,0,0,0};
        for (var i = 0; i < 4; i++) {
            result[i] = function.apply(a[i], b[i]);
        }
        return result;
    }

    private byte mulFunction(byte a, byte b) {
        return (byte)(a & b);
    }

    private byte xorFunction(byte a, byte b) {
        if (a == 0) return (byte)0xff;
        return (byte)(a ^ b);
    }

    private byte orFunction(byte a, byte b) {
        return (byte)(a | b);
    }
}
