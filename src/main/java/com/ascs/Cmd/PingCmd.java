package com.ascs.Cmd;

import com.ascs.NetMgr;
import com.ascs.Printer;

import java.util.*;
import java.util.function.BiFunction;

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

            //for every ip in list do
            //  ip mul mask
            //  arg byte[4] mul mask
            //  xor both and save result
            var competitors = new HashMap<byte[], Integer>();
            for (var ifaceAddr : _nm.getAddresses()) {
                _nm.getMask(ifaceAddr).ifPresent((ifaceMask) -> {
                    var bIfaceMask = NetMgr.ipToBytes(ifaceMask);
                    var ifaceNet = mixAddrs(NetMgr.ipToBytes(ifaceAddr), bIfaceMask, this::mulFunction);
                    var inputNet = mixAddrs(input, bIfaceMask, this::mulFunction);
                    var score = 0;
                    var mixed = mixAddrs(ifaceNet, inputNet, this::xorFunction);
                    for (var i = 0; i < 4; i++) {

                    }
                });
            }

            // compare results, smallest wins

            // ifaceNet OR winner = IP to ping


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
        return (byte)(a ^ b);
    }
}
